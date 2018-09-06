package com.pillow.ui;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
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
//对应于心率显示界面
public class heart extends Activity {

	private TextView heartPageBack,maxheartvalue,minheartvalue,avgheartvalue;
	private Button look_heart_realchart,look_heart_nearweekchart,look_heart_nearmonthchart;
	private Button get_heart_data;
 
    private EditText editText;
    private EditText editText2,editText3,editText4;
    private int year, monthOfYear, dayOfMonth, hourOfDay, minute;
    private String OldmanAccount="";
    RequestToServer reqtoserver=new RequestToServer();
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.heart_main);
		
		findViewById();
//		获取从上一个界面传递过来的用户账号信息
		OldmanAccount = (String)getIntent().getStringExtra("oldmanAccount");
//		ClickListener为定义的事件监听器
		ClickListener cl = new ClickListener();
		heartPageBack.setOnClickListener(cl); 
		editText.setOnClickListener(cl);
		editText2.setOnClickListener(cl);
		editText3.setOnClickListener(cl);
		editText4.setOnClickListener(cl);
		get_heart_data.setOnClickListener(cl);
		look_heart_realchart.setOnClickListener(cl);
		look_heart_nearweekchart.setOnClickListener(cl);
		look_heart_nearmonthchart.setOnClickListener(cl);
		
		//这部分代码的作用是选择日期是显示的是当前日期，如果没有以下6行代码，选择日期和时间时，从1900年和00时开始，而不是从当前日期和时间开始
		Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        monthOfYear = calendar.get(Calendar.MONTH);
        dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);
	        editText.setFocusable(false);
	        editText2.setFocusable(false);
	        editText3.setFocusable(false);
	        editText4.setFocusable(false);
	        
	}
//	初始化各UI控件
	private void findViewById(){
		heartPageBack  = (TextView) findViewById(R.id.heart_page_back);
		maxheartvalue  = (TextView) findViewById(R.id.maxheartvalue);
		minheartvalue  = (TextView) findViewById(R.id.minheartvalue);
		avgheartvalue  = (TextView) findViewById(R.id.avgheartvalue);
		get_heart_data = (Button) findViewById(R.id.get_heart_data);
		look_heart_realchart=	(Button)findViewById(R.id.look_heart_yesterdaychart);
	    look_heart_nearweekchart =	(Button)findViewById(R.id.look_heart_nearweekchart);
	    look_heart_nearmonthchart =(Button)findViewById(R.id.look_heart_nearmonthchart);
        editText = (EditText)findViewById(R.id.editText1);
        editText2 = (EditText)findViewById(R.id.editText2);
	    editText3 =	(EditText)findViewById(R.id.editText3);
	    editText4 =	(EditText)findViewById(R.id.editText4);
	}	
	//定义的事件触发器，当点击各控件时，分别调用相应的事件处理函数
	private final class ClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.get_heart_data:
				get_heart_data();
				break;
			case R.id.editText1:
				endDateSet1();
				break;
			case R.id.editText2:
				endTimeSet1();
				break;
			case R.id.editText3:
				endDateSet2();
				break;
			case R.id.editText4:
				endTimeSet2();
				break;
			case R.id.look_heart_yesterdaychart:
				GotoHeartTotalNightchart();
				break;
			case R.id.look_heart_nearweekchart:
				GotoHeartnearweekchart();
				break;
			case R.id.look_heart_nearmonthchart:
				GotoHeartnearmonthchart();
				break;
			case R.id.heart_page_back:
				heart.this.finish();
				break;
			default:
				break;
			}
		}
	}
	//向服务器发送请求，获取某一时间段内的心率值
	/*
	 *功能：首先获取时间段的开始日期和结束日期，判断起始日期和时间是否为空，若为空，给出提示，
	 *否则， 将起止日期和时间以及用户账号以健值对的形式存储到params中，新建进程，调用向服务器发送HTTP请求的函数
	 */
		private void get_heart_data(){
		String	startDateTime = editText.getText().toString().replace("-", "")+editText2.getText().toString().replace(":", "");
		String	endDateTime = editText3.getText().toString().replace("-", "")+editText4.getText().toString().replace(":", "");
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
//		params参数负责保存需要传递给服务器的数据
			Constants.params = new ArrayList<NameValuePair>();
		    Constants.params.add(new BasicNameValuePair("startheartDateTime", startDateTime));
		    Constants.params.add(new BasicNameValuePair("endheartDateTime", endDateTime));
		    Constants.params.add(new BasicNameValuePair("OldmanAccount", OldmanAccount));
			Constants.requestUrl = Constants.SERVER_HOST+"/getHeartData.action";
			new Thread(getJson).start();//开启一个新的线程
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
				if (msg.what == 0x00) {//得到服务器端的返回结果并显示
					Log.v("PostGetJson", "" + Constants.result);
					if(null != Constants.result){
						try {
							JSONObject json= new JSONObject(Constants.result);  		
							Integer maxnum = (Integer)json.get("maxheartdata");	
							Integer minnum = (Integer)json.get("minheartdata");
							Integer avgnum = (Integer)json.get("avgheartdata");
//将服务器端返回的结果显示在APP界面上
							maxheartvalue.setText(" "+maxnum+"次/分钟");
							minheartvalue.setText(" "+minnum+"次/分钟");
							avgheartvalue.setText(" "+avgnum+"次/分钟");		
						} catch (JSONException e) {
							e.printStackTrace();
						}  
					}		
				} else if (msg.what == 0x01) {
					Toast.makeText(getApplicationContext(), "",Toast.LENGTH_LONG).show();
				}
			}
		};
		
//		以下四个函数处理时间显示，如果时间小于10，则在时间字符串前面补0，这样做是为了便于从数据库中去数据，因为数据库中时间的存储格式是：20171121055002012（年月日时分秒毫秒）
//		后面日期处理也类似，将数字小于10的日期处理为统一的两位数
		private void endDateSet1(){
			DatePickerDialog datePickerDialog = new DatePickerDialog(heart.this, new DatePickerDialog.OnDateSetListener(){
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
			 TimePickerDialog timePickerDialog = new TimePickerDialog(heart.this, new TimePickerDialog.OnTimeSetListener(){
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
		DatePickerDialog datePickerDialog = new DatePickerDialog(heart.this, new DatePickerDialog.OnDateSetListener(){
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
			 TimePickerDialog timePickerDialog = new TimePickerDialog(heart.this, new TimePickerDialog.OnTimeSetListener(){
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
		
		private void GotoHeartTotalNightchart(){
			Intent intent= new Intent();
			intent.putExtra("oldmanAccount",OldmanAccount);
			intent.setClass(getApplicationContext(), HeartYesterdayChoice.class);
			startActivity(intent);
		}
		//此处需要改
		private void GotoHeartnearweekchart(){
			Intent intent= new Intent();
			intent.putExtra("oldmanAccount",OldmanAccount);
			intent.setClass(getApplicationContext(), HeartNearweekChoice.class);
			startActivity(intent);
		}
		//此处需要改
		private void GotoHeartnearmonthchart(){
			Intent intent= new Intent();
			intent.putExtra("oldmanAccount",OldmanAccount);
			intent.setClass(getApplicationContext(), HeartNearmonthChoice.class);
			startActivity(intent);
		}
		
}
