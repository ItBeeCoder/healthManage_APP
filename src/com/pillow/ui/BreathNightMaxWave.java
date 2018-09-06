/**
 *日期：2018年1月28日下午5:24:19
pillow
TODO
author：shaozq
 */
package com.pillow.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.pillow.R;
import com.pillow.bean.Constants;
import com.pillow.bean.RequestToServer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * @author shaozq
 *2018年1月28日下午5:24:19
 */
public class BreathNightMaxWave extends Activity {
	private Button startBtn = null, stopBtn = null;
	private Timer timer = new Timer();
	private TimerTask task;
	private Handler handler;
	private String title = "";
	private XYSeries series;
	private XYMultipleSeriesDataset mDataset;
	private GraphicalView mViewChart;//
	private XYMultipleSeriesRenderer mXYRenderer;
	private Context context;
	private LinearLayout mLayout;
	private int X = 60,Y = 50;
	int[] num = new int[24];
	static int count = 0,breathnumcount = 0;// 每隔多少包更新一次心电图
	private MyThread myThread;
	private String OldmanAccount="",passuseraccount="";
	private int[] breathnum = new int[24];//本文中用到的是这个数组
	RequestToServer reqtoserver=new RequestToServer();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.breath_realtime_wave);
		findViewById();
		
		startBtn.setOnClickListener(new StartBtn());
		stopBtn.setOnClickListener(new StopBtn());
		
		initCardiograph();
		
		OldmanAccount = (String)getIntent().getStringExtra("oldmanAccount");
		passuseraccount = (String)getIntent().getStringExtra("useraccount");
		
		getConfirmData();
		
		InputStream inputStream = getResources().openRawResource(R.raw.br_30); 
//		num=this.getNum(inputStream);
		
		this.myThread = new MyThread();
		this.myThread.start();
		
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == 1) {
					updateChart(); //
					super.handleMessage(msg);
				}else if (msg.what == 0x02) {
					Log.v("PostGetJson", "" + result);
					if(null != result){
						try {//这里返回的应该是一个12个整数的数组或者一个字符串，含有整晚的呼吸值，最大、最小和平均三类
							JSONObject json= new JSONObject(result);  		
							String re = (String)json.get("result");
							List<Integer> OldmanPrenightBreathData = (List<Integer>)json.get("OldmanPrenightBreathData");
							int ret=Integer.parseInt(re);
							if(ret==1){
								Log.i("OldmanPrenightBreathData.size() === ", OldmanPrenightBreathData.size()+"");
								breathnumcount = 0;
								if(OldmanPrenightBreathData.size()!=0){
									for(int i=0 ;i< OldmanPrenightBreathData.size();i++){
										int breathdata = OldmanPrenightBreathData.get(i);
										breathnum[breathnumcount++] = breathdata;
									}
								num = breathnum;
								Log.i("OldmanPrenightBreathData.size() === ", OldmanPrenightBreathData.size()+"");
								Log.i("num.length === ", num.length+"");
								Log.i("breathnumcount ====", breathnumcount +"");
								}
								
							}else if(ret==0){
							
							//	Toast.makeText(getApplicationContext(), "该子女与老人不存在关系", Toast.LENGTH_LONG).show();
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}  
					}
				}
			 else if (msg.what == 0x01) {
				Toast.makeText(getApplicationContext(), "获取失败",Toast.LENGTH_LONG).show();
			}
			}
		};
	}

	@Override
	public void onDestroy() {
		if (timer != null) {// 
			timer.cancel();
			super.onDestroy();
		}
	}

	private void findViewById(){
		startBtn = (Button) findViewById(R.id.startBtn);
		stopBtn = (Button) findViewById(R.id.stopBtn);
	}
	
	public void initCardiograph() {
		context = getApplicationContext();//
		mLayout = (LinearLayout) findViewById(R.id.chart);// 
		series = new XYSeries(title);// 

		mDataset = new XYMultipleSeriesDataset(); // 
		mDataset.addSeries(series);// 

		//int color = Color.RED;// 
		int color = getResources().getColor(R.color.cardio_color3);
		PointStyle style = PointStyle.CIRCLE;//
		mXYRenderer = buildRenderer(color, style, true);
		
		setChartSettings(mXYRenderer, title, "时间/时", "呼吸值", 0, X, 0, 22,//12
				Color.WHITE, Color.WHITE);// 

		mViewChart = ChartFactory.getLineChartView(context, mDataset,
				mXYRenderer);//
		mViewChart.setBackgroundColor(getResources().getColor(
				R.color.cardio_bg_color));
		mLayout.addView(mViewChart, new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT));// 
	}
	
	protected XYMultipleSeriesRenderer buildRenderer(int color,
			PointStyle style, boolean fill) {// 
		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
		XYSeriesRenderer r = new XYSeriesRenderer();
		r.setColor(color);
		r.setPointStyle(style);
		r.setFillPoints(fill);
		r.setLineWidth(3);
		renderer.addSeriesRenderer(r);
		return renderer;
	}

	@SuppressLint("ResourceAsColor") 
	protected void setChartSettings(XYMultipleSeriesRenderer renderer,
			String title, String xTitle, String yTitle, double xMin,
			double xMax, double yMin, double yMax, int axesColor,
			int labelsColor) {
		mXYRenderer.setBackgroundColor(getResources().getColor(
				R.color.cardio_bg_color));
		mXYRenderer.setShowGrid(true);// 
		mXYRenderer.setGridColor(Color.GRAY);
		mXYRenderer.setLabelsTextSize(30);//对应横纵坐标轴上的数字
		mXYRenderer.setXLabels(20);
		mXYRenderer.setYLabels(10);
		mXYRenderer.setYLabelsAlign(Align.RIGHT);// �Ҷ���
		// mXYRenderer.setPointSize((float) 2);
		mXYRenderer.setAxisTitleTextSize(30);//对应时间、呼吸值等坐标轴的标题
		mXYRenderer.setShowLegend(false);// ����ʾͼ��
		mXYRenderer.setZoomEnabled(false);
		mXYRenderer.setPanEnabled(true, false);
		mXYRenderer.setClickEnabled(false);
		renderer.setXLabelsAngle(45);
		renderer.setXLabels(0);
		renderer.addTextLabel(0, "18:00");
		renderer.addTextLabel(7, "20:00");
		renderer.addTextLabel(14, "21:00");
		renderer.addTextLabel(21, "22:00");
		renderer.addTextLabel(28, "0:00");
		renderer.addTextLabel(35, "2:00");
		renderer.addTextLabel(42, "4:00");
		renderer.addTextLabel(49, "6:00");
		renderer.addTextLabel(56, "8:00");
		
//		renderer.setMarginsColor(R.color.cardio_color3);
		renderer.setChartTitle(title);
		renderer.setXTitle(xTitle);
		renderer.setYTitle(yTitle);
		renderer.setXAxisMin(xMin);
		renderer.setXAxisMax(xMax);
		renderer.setYAxisMin(yMin);
		renderer.setYAxisMax(yMax);
		renderer.setAxesColor(axesColor);
		renderer.setLabelsColor(labelsColor);
		renderer.setMargins(new int[] { 10, 40, 40, 20 });
	}

	private void updateChart() {// 
		mDataset.removeSeries(series);// 
		series.clear();// 

		for (int k = 0; k < 56; k++) {// 
			int y =num[count++];
			int tempmid=count;
//			while(y==0){
//				y=num[tempmid--];
//			}
					//(int) (Math.random() * Y);
			series.add(k, y);
			if(count>=23){
				count=0;
			}
		}
		mDataset.addSeries(series);// �����ݼ�������µĵ㼯
		mViewChart.invalidate();// ߲�����ֶ�̬
	}

	List<NameValuePair> params;
	private String result;
	private String myurl;

	private void getConfirmData(){
		params =new ArrayList<NameValuePair>();
		
		SimpleDateFormat format=new SimpleDateFormat("yyyyMMdd");
		String requestDate = format.format(new java.util.Date());
		//passuseraccount代表的可能是老人或者子女账号，如果是子女登录的话先要选择某一位关联的老人，这里传递的只能是老人账号
		params.add(new BasicNameValuePair("Oldmanuseraccount",OldmanAccount));
		params.add(new BasicNameValuePair("breathDateTime",requestDate));
		
		myurl = Constants.SERVER_HOST+"/getYestMaxByOldAndDate.action";	
		new Thread(getJson2).start();
	}
	
	private Runnable getJson2 = new Runnable() {
		public void run() {
			// TODO Auto-generated method stub
			try {
				result = reqtoserver.GetResponseFromServer(myurl, params);
				handler.sendEmptyMessage(0x02);
			} catch (Exception e) {
				handler.sendEmptyMessage(0x01);
			}
		}
	};    
	
	private class MyThread extends Thread {
		@Override
		public void run() {
			task = new TimerTask() {
				@Override
				public void run() {
					Message message = new Message();
					message.what = 1;//־
					handler.sendMessage(message);
				}
			};
			timer.schedule(task, 10);
				}
			}
	
	private final class StartBtn implements OnClickListener {
		@Override
		public void onClick(View arg0) {
			handler = new Handler() {// 
				@Override
				public void handleMessage(Message msg) {
					if (msg.what == 1) {
						updateChart(); //
						super.handleMessage(msg);
					}
				}
			};
			task = new TimerTask() {// 
				@Override
				public void run() {
					Message message = new Message();
					message.what = 1;//־
					handler.sendMessage(message);
				}
			};
			timer.schedule(task, 10);//
		}
	}

	private final class StopBtn implements OnClickListener {
		@Override
		public void onClick(View arg0) {
			BreathNightMaxWave.this.finish();
		}
	}
	
	public int[] getNum(InputStream inputStream) {
		InputStreamReader inputStreamReader=null;
		inputStreamReader=new InputStreamReader(inputStream);
		BufferedReader br=new BufferedReader(inputStreamReader);
		StringBuffer sb=new StringBuffer("");
		int count=0;
		 int[] num=new int[1000000];
		
		String line;
		try{
			while((line = br.readLine())!=null){ //
				String[] arrs = line.split(" ");
				//
				int[] arr = new int[arrs.length];
				for(int i = 0; i< arr.length; i++){
				arr[i] = Integer.parseInt(arrs[i]);
				num[count++]=arr[i];
				}
				}
				if(br!= null){
					br.close();
					br = null;
					}		
		}catch(IOException e){
			e.printStackTrace();
		}
		return num;	
	}
}
