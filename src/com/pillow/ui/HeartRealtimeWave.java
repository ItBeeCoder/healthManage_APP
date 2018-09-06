package com.pillow.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Timer;
import java.util.TimerTask;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import com.pillow.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;

public class HeartRealtimeWave extends Activity {

	private Button startBtn = null;
	private Button stopBtn = null;
	private Timer timer = new Timer();//
	private TimerTask task;// 
	private Handler handler;// 
	private String title = "";
	private XYSeries series;//
	private XYMultipleSeriesDataset mDataset;//
	private GraphicalView mViewChart;// 
	private XYMultipleSeriesRenderer mXYRenderer;// 
	private Context context;//
	private LinearLayout mLayout;
	private int X = 60;//
	private int Y = 50;//
	int[] num = new int[10000];
	int count = 0;// 每隔多少包更新一次心电图
	private MyThread myThread;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.heart_realtime_wave);
		findViewById();
		
		startBtn.setOnClickListener(new StartBtn());
		stopBtn.setOnClickListener(new StopBtn());
		
		initCardiograph();
		
		InputStream inputStream = getResources().openRawResource(R.raw.hr_30); 
		num=this.getNum(inputStream);
		
		this.myThread = new MyThread();
		this.myThread.start();
		handler = new Handler() {// 
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == 1) {
					updateChart(); //
					super.handleMessage(msg);
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
		
		setChartSettings(mXYRenderer, title, "时间/时", "心率值", 0, X, 60, 75,
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

	protected void setChartSettings(XYMultipleSeriesRenderer renderer,
			String title, String xTitle, String yTitle, double xMin,
			double xMax, double yMin, double yMax, int axesColor,
			int labelsColor) {
		mXYRenderer.setBackgroundColor(getResources().getColor(
				R.color.cardio_bg_color));
		mXYRenderer.setShowGrid(true);// 
		mXYRenderer.setGridColor(Color.GRAY);
		mXYRenderer.setLabelsTextSize(30);
		mXYRenderer.setXLabels(20);
		mXYRenderer.setYLabels(10);
		mXYRenderer.setYLabelsAlign(Align.RIGHT);//
		// mXYRenderer.setPointSize((float) 2);
		mXYRenderer.setAxisTitleTextSize(30);
		mXYRenderer.setShowLegend(false);//
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
		
		renderer.setChartTitle(title);
		renderer.setXTitle(xTitle);
		renderer.setYTitle(yTitle);
		renderer.setXAxisMin(xMin);
		renderer.setXAxisMax(xMax);
		renderer.setYAxisMin(yMin);
		renderer.setYAxisMax(yMax);
		renderer.setAxesColor(axesColor);
		renderer.setLabelsColor(labelsColor);
		renderer.setMargins(new int[] { 10, 40, 20, 20 });
	}

	private void updateChart() {// 
		mDataset.removeSeries(series);// 
		series.clear();// 

		for (int k = 0; k < 56; k++) {// 
			int y =num[count++];
			int tempmid=count;
			while(y==0){
				y=num[tempmid--];
			}
					//(int) (Math.random() * Y);
			series.add(k, y);
			if(count>=1800){
				count=0;
			}
		}
		mDataset.addSeries(series);//
		mViewChart.invalidate();
	}

	private class MyThread extends Thread {
		@Override
		public void run() {
			
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
			timer.schedule(task, 1000, 2000);//
		}

	}

	private final class StopBtn implements OnClickListener {
		@Override
		public void onClick(View arg0) {
			HeartRealtimeWave.this.finish();
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
