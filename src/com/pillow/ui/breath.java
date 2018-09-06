package com.pillow.ui;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.pillow.R;
import com.pillow.bean.Constants;
import com.pillow.bean.RequestToServer;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
//这部分对应呼吸界面
public class breath extends Activity {

	private TextView breathPageBack,maxbreathvalue,minbreathvalue,avgbreathvalue;
	private Button add,look_breath_realchart,breath_realwave,look_breath_nearweekchart,look_breath_nearmonthchart;
	private EditText editText,editText2,editText3,editText4;
	private int year, monthOfYear, dayOfMonth, hourOfDay, minute;
	private String OldmanAccount="",passuseraccount="";
	RequestToServer reqtoserver=new RequestToServer();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.breath_main);
		findViewById();
		//获取从主页面跳转向该页面时传递过来的参数
		OldmanAccount = (String)getIntent().getStringExtra("oldmanAccount");
		passuseraccount = (String)getIntent().getStringExtra("useraccount");
//		ClickListener为定义的事件监听器
		
		ClickListener cl = new ClickListener();
		
		breathPageBack.setOnClickListener(cl); 
		add.setOnClickListener(cl);
		editText.setOnClickListener(cl);
		editText2.setOnClickListener(cl);
		editText3.setOnClickListener(cl);
		editText4.setOnClickListener(cl);
		look_breath_realchart.setOnClickListener(cl);
		look_breath_nearweekchart.setOnClickListener(cl);
	    look_breath_nearmonthchart.setOnClickListener(cl);
//		breath_realwave.setOnClickListener(cl);
		
		//这部分代码的作用是选择日期是显示的是当前日期，如果没有以下6行代码，选择日期和时间时，从1900年和00时开始，而不是从当前日期和时间开始
	        Calendar calendar = Calendar.getInstance();
	        year = calendar.get(Calendar.YEAR);
	        monthOfYear = calendar.get(Calendar.MONTH);
	        dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
	        hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
	        minute = calendar.get(Calendar.MINUTE);
	        editText.setFocusable(false);//
	        editText2.setFocusable(false);//
	        editText3.setFocusable(false);//
	        editText4.setFocusable(false);
	        
	}
	/*
	 * 功能：初始化各种UI控件
	 */
	private void findViewById(){
		breathPageBack  = (TextView) findViewById(R.id.breath_page_back);
		maxbreathvalue  = (TextView) findViewById(R.id.maxbreathvalue);
		minbreathvalue  = (TextView) findViewById(R.id.minbreathvalue);
		avgbreathvalue  = (TextView) findViewById(R.id.avgbreathvalue);
		add = (Button) findViewById(R.id.get_breath_data);
	    look_breath_realchart=	(Button)findViewById(R.id.look_breath_yesterdaychart);
	    look_breath_nearweekchart =	(Button)findViewById(R.id.look_breath_nearweekchart);
	    look_breath_nearmonthchart =(Button)findViewById(R.id.look_breath_nearmonthchart);
//	    breath_realwave = (Button)findViewById(R.id.look_breath_realwave);
	    editText = (EditText)findViewById(R.id.editText1);
	    editText2 = (EditText)findViewById(R.id.editText2);
	    editText3 =	(EditText)findViewById(R.id.editText3);
	    editText4 =	(EditText)findViewById(R.id.editText4);
	}
//	向服务器发送请求时需要用到的各参数
	
	
	//定义的事件触发器，当点击各控件时，分别调用相应的事件处理函数
	private final class ClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.get_breath_data://调用各种对应的事件触发函数
				DateAndTimeOfSelection();
				break;
			case R.id.editText1://点击选择开始日期文本框时
				endDateSet1();
				break;
			case R.id.editText2://点击选择开始时间文本框时
				endTimeSet1();
				break;
			case R.id.editText3://点击选择结束日期文本框时
				endDateSet2();
				break;
			case R.id.editText4://点击选择结束时间文本框时
				endTimeSet2();
				break;
			case R.id.look_breath_yesterdaychart:
				GotoBreathTotalNightchart();
				break;
			case R.id.look_breath_nearweekchart:
				GotoBreathnearweekchart();
				break;
			case R.id.look_breath_nearmonthchart:
				GotoBreathnearmonthchart();
				break;
//			case R.id.look_breath_realwave:
//				GotoBreathrealtimechart();
//				break;
			case R.id.breath_page_back://点击左上角的返回时
				breath.this.finish();
				break;
			default:
				break;
			}
		}
	}
	/*
	 *功能：首先获取时间段的开始日期和结束日期，判断起始日期和时间是否为空，若为空，给出提示，
	 *否则， 将起止日期和时间以及用户账号以健值对的形式存储到params中，新建进程，调用向服务器发送HTTP请求的函数
	 */
		private void DateAndTimeOfSelection(){
//		处理时间，去掉时间之间的"-"，因为数据库中的时间格式是：20171121055002012（年月日时分秒毫秒）
			String 	startBreathDateTime = editText.getText().toString().replace("-", "")+editText2.getText().toString().replace(":", "");
			String 	endBreathDateTime = editText3.getText().toString().replace("-", "")+editText4.getText().toString().replace(":", "");
			if(null == editText.getText().toString() || editText.getText().toString().length() == 0){
				Toast.makeText(getApplicationContext(), "起始日期不能为空", Toast.LENGTH_SHORT).show();
				return;
			}
			if(null == editText2.getText().toString() || editText2.getText().toString().length() == 0){
				Toast.makeText(getApplicationContext(), "起始时间不能为空", Toast.LENGTH_SHORT).show();
				return;
			}
			if(null == editText3.getText().toString() || editText3.getText().toString().length() == 0){
				Toast.makeText(getApplicationContext(), "结束日期不能为空", Toast.LENGTH_SHORT).show();
				return;
			}
			if(null == editText4.getText().toString() || editText4.getText().toString().length() == 0){
				Toast.makeText(getApplicationContext(), "结束时间不能为空", Toast.LENGTH_SHORT).show();
				return;
			}
			Constants.params = new ArrayList<NameValuePair>();
			Constants.params.add(new BasicNameValuePair("startbreathDateTime",startBreathDateTime));
			Constants.params.add(new BasicNameValuePair("endbreathDateTime",endBreathDateTime));
			Constants.params.add(new BasicNameValuePair("OldmanAccount", OldmanAccount));
			
			Constants.requestUrl = Constants.SERVER_HOST+"/getBreathData.action";
			new Thread(getJson).start();
		}
		private Runnable getJson=new Runnable(){
			public void run() {
				// TODO Auto-generated method stub
				try{
					Constants.result = reqtoserver.GetResponseFromServer(Constants.requestUrl, Constants.params);
					handler.sendEmptyMessage(0x00);
				}
				catch(Exception e){
					handler.sendEmptyMessage(0x01);
				}
			}
		};

		Handler handler = new Handler() {
			public void handleMessage(android.os.Message msg) {
				if (msg.what == 0x00) {
					Log.v("PostGetJson", "" + Constants.result);
					if(null != Constants.result){//获取服务器的返回结果并显示
						try {
							JSONObject json= new JSONObject(Constants.result);  		
							Integer maxnum = (Integer)json.get("maxbreathdata");	
							Integer minnum = (Integer)json.get("minbreathdata");
							Double avgnum = (Double)json.get("avgbreathdata");
		
							maxbreathvalue.setText(maxnum+"次/分钟");
							minbreathvalue.setText(minnum+"次/分钟");
							avgbreathvalue.setText(avgnum+"次/分钟");
							} catch (JSONException e) {
							e.printStackTrace();
						}  
					}		
				} else if (msg.what == 0x01) {
					Toast.makeText(getApplicationContext(), "获取失败",Toast.LENGTH_LONG).show();
				}
			}
		};
		
//		以下四个函数处理时间显示，如果时间小于10，则在时间字符串前面补0，这样做是为了便于从数据库中去数据，因为数据库中时间的存储格式是：20171121055002012（年月日时分秒毫秒）
//		后面日期处理也类似，将数字小于10的日期处理为统一的两位数
		private void endDateSet1(){
			DatePickerDialog datePickerDialog = new DatePickerDialog(breath.this, new DatePickerDialog.OnDateSetListener(){
           @Override
           public void onDateSet(DatePicker view, int year, int monthOfYear,
                   int dayOfMonth){
           	if((monthOfYear + 1)< 10 && dayOfMonth < 10){
           		editText.setText( year + "-0" + (monthOfYear + 1) + "-0" + dayOfMonth);
           	}else if((monthOfYear + 1)< 10 && dayOfMonth >= 10){
           		editText.setText( year + "-0" + (monthOfYear + 1) + "-" + dayOfMonth);
           	}else if((monthOfYear + 1) >= 10 && dayOfMonth < 10){
           		editText.setText( year + "-" + (monthOfYear + 1) + "-0" + dayOfMonth);
           	}else if((monthOfYear + 1) >= 10 && dayOfMonth >= 10){
           		editText.setText( year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
           	}
           }
       }, year, monthOfYear, dayOfMonth);
			datePickerDialog.show();
		}
		
		private void endTimeSet1(){
			 TimePickerDialog timePickerDialog = new TimePickerDialog(breath.this, new TimePickerDialog.OnTimeSetListener(){
                 @Override
                 public void onTimeSet(TimePicker view, int hourOfDay, int minute){
                		if(hourOfDay < 10 && minute < 10){
                    		editText2.setText("0"+hourOfDay + ":0" + minute);
                    	}else if(hourOfDay < 10 && minute >= 10){
                    		editText2.setText("0"+hourOfDay + ":" + minute);
                    	}else if(hourOfDay >= 10 && minute < 10){
                    		editText2.setText(hourOfDay + ":0" + minute);
                    	}else if(hourOfDay >= 10 && minute >= 10){
                    		editText2.setText(hourOfDay + ":" + minute);
                    	}
                 }
             }, hourOfDay, minute, true);
             
             timePickerDialog.show();
		}
	
		private void endDateSet2(){
		DatePickerDialog datePickerDialog = new DatePickerDialog(breath.this, new DatePickerDialog.OnDateSetListener(){
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                    int dayOfMonth){
            	if((monthOfYear + 1)< 10 && dayOfMonth < 10){
            		editText3.setText( year + "-0" + (monthOfYear + 1) + "-0" + dayOfMonth);
            	}else if((monthOfYear + 1)< 10 && dayOfMonth >= 10){
            		editText3.setText( year + "-0" + (monthOfYear + 1) + "-" + dayOfMonth);
            	}else if((monthOfYear + 1) >= 10 && dayOfMonth < 10){
            		editText3.setText( year + "-" + (monthOfYear + 1) + "-0" + dayOfMonth);
            	}else if((monthOfYear + 1) >= 10 && dayOfMonth >= 10){
            		editText3.setText( year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
            	}
            }
        }, year, monthOfYear, dayOfMonth);
        datePickerDialog.show();
		}
		
		private void endTimeSet2(){
			 TimePickerDialog timePickerDialog = new TimePickerDialog(breath.this, new TimePickerDialog.OnTimeSetListener(){
               @Override
               public void onTimeSet(TimePicker view, int hourOfDay, int minute){
              		if(hourOfDay < 10 && minute < 10){
                  		editText4.setText("0"+hourOfDay + ":0" + minute);
                  	}else if(hourOfDay < 10 && minute >= 10){
                  		editText4.setText("0"+hourOfDay + ":" + minute);
                  	}else if(hourOfDay >= 10 && minute < 10){
                  		editText4.setText(hourOfDay + ":0" + minute);
                  	}else if(hourOfDay >= 10 && minute >= 10){
                  		editText4.setText(hourOfDay + ":" + minute);
                  	}
               }
           }, hourOfDay, minute, true);
           
           timePickerDialog.show();
		}
		
		private void GotoBreathTotalNightchart(){
			Intent intent= new Intent();
			intent.putExtra("oldmanAccount",OldmanAccount);
			intent.putExtra("useraccount ",passuseraccount);
			intent.setClass(breath.this, BreathYesterdayChoice.class);
			startActivity(intent);
		}
		//此处需要改
		private void GotoBreathnearweekchart(){
			Intent intent= new Intent();
			intent.putExtra("oldmanAccount",OldmanAccount);
			intent.putExtra("useraccount ",passuseraccount);
			intent.setClass(breath.this, BreathNearweekChoice.class);
			startActivity(intent);
		}
		//此处需要改
		private void GotoBreathnearmonthchart(){
			Intent intent= new Intent();
			intent.putExtra("oldmanAccount",OldmanAccount);
			intent.putExtra("useraccount ",passuseraccount);
			intent.setClass(breath.this, BreathNearmonthChoice.class);
			startActivity(intent);
		}
		
//		private void GotoBreathrealtimechart(){
//			Intent intent= new Intent();
//			intent.putExtra("oldmanAccount",OldmanAccount);
//			intent.setClass(getApplicationContext(), BluetoothSetActivity.class);
//			startActivity(intent);
//		}
}
