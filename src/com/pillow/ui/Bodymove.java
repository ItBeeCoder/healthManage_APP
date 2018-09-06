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
//这部分程序对应于体动信息获取界面
public class Bodymove extends Activity {

	private TextView bodyMoveNum,bodyMoveStartTime,bodyMoveEndTime;
	private Button add;
	private Button button;
    private Button button2,button3,button4;
    private EditText editText;
    private EditText editText2,editText3,editText4;
    private int year, monthOfYear, dayOfMonth, hourOfDay, minute;
    private String OldmanAccount="";
    RequestToServer reqtoserver=new RequestToServer();
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.bodymove_main);
		
		findViewById();
		
		//获取从主页面传递过来的用户账号
		OldmanAccount = (String)getIntent().getStringExtra("oldmanAccount");
//		定义的事件触发器
		ClickListener cl = new ClickListener();
		add.setOnClickListener(cl);
//		点击各种按钮时对应的触发事件
		editText.setOnClickListener(cl);
		editText2.setOnClickListener(cl);
		editText3.setOnClickListener(cl);
		editText4.setOnClickListener(cl);
		
//		以下五行代码的作用：选择日期和时间时，从当前时间开始，而不是从1900年和00时开始，
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
//	初始化各种UI控件
	private void findViewById(){
		bodyMoveNum = (TextView) findViewById(R.id.bodyMoveNum );
		bodyMoveStartTime = (TextView) findViewById(R.id.bodyMoveStartTime);
		bodyMoveEndTime  = (TextView) findViewById(R.id.bodyMoveEndTime);
		add = (Button) findViewById(R.id.get_move_data);
		button = (Button)findViewById(R.id.button1);
        button2 = (Button)findViewById(R.id.button2);
        editText = (EditText)findViewById(R.id.editText1);
        editText2 = (EditText)findViewById(R.id.editText2);
	    editText3 =	(EditText)findViewById(R.id.editText3);
	    editText4 =	(EditText)findViewById(R.id.editText4);
	}
	
	//定义的事件监听触发器
	private final class ClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.get_move_data:
				BodyMoveData();
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
			default:
				break;
			}
		}
	}
//	点击提交按钮时，将起止日期、时间传递给服务器，获取该时间段内的体动次数
		private void BodyMoveData(){
			String 	startDateTime = editText.getText().toString().replace("-", "")+editText2.getText().toString().replace(":", "");
			String 	endDateTime = editText3.getText().toString().replace("-", "")+editText4.getText().toString().replace(":", "");
//			要求起止日期、时间不能为空，为空的话，给出提示
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
//			params参数负责保存需要传递给服务器的数据
			Constants.params = new ArrayList<NameValuePair>();
			Constants.params.add(new BasicNameValuePair("movementDateTime", startDateTime));
			Constants.params.add(new BasicNameValuePair("OldmanAccount", OldmanAccount));
			Constants.requestUrl = Constants.SERVER_HOST+"/getBodyMoveData.action";
			new Thread(getJson).start();//开启一个新的线程，
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
					if(null != Constants.result){
						try {
							JSONObject json= new JSONObject(Constants.result);  		
							String totalnum = (String)json.get("totalnum");	
							String starttiime = (String)json.get("starttiime");
							String endtiime = (String)json.get("endtime");
//							将服务器返回的数据显示在APP界面上
							bodyMoveNum.setText(" "+totalnum+"次");
							bodyMoveStartTime.setText(starttiime);
							bodyMoveEndTime.setText(endtiime);		
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
			DatePickerDialog datePickerDialog = new DatePickerDialog(Bodymove.this, new DatePickerDialog.OnDateSetListener(){
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
			 TimePickerDialog timePickerDialog = new TimePickerDialog(Bodymove.this, new TimePickerDialog.OnTimeSetListener(){
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
		DatePickerDialog datePickerDialog = new DatePickerDialog(Bodymove.this, new DatePickerDialog.OnDateSetListener(){
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
			 TimePickerDialog timePickerDialog = new TimePickerDialog(Bodymove.this, new TimePickerDialog.OnTimeSetListener(){
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
		
}
