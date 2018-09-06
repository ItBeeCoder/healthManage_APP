package com.pillow.ui;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.pillow.R;
import com.pillow.bean.Constants;
import com.pillow.bean.RequestToServer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
//这里对应老人添加、关联设备的界面
public class AddDevice extends Activity {
//定义的各参数
	private TextView addDevicePageBack;
	private EditText deviceID;
	private Button submit_relation,cancel_relation;
	String passuseraccount;
	private String usertype = "";
//	定义向服务器发送HTTP请求的类的实例
	RequestToServer reqtoserver=new RequestToServer();
	//进入页面之后，首先需要向服务器发送请求，获取是否已经有关联的设备，如果有，则获取关联的设备号
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.add_device);
		/*
		 * 首先通过 getIntent()方法获取用于启动 AddDevice的 Intent，然后调用
		 * getStringExtra()方法，传入相应的键值，就可以得到传递的数据了。由于传递的
		 * 是字符串，所以使用 getStringExtra()方法来获取传递的数据，如果传递的是整型数据，则
		 * 使用 getIntExtra()方法，传递的是布尔型数据，则使用 getBooleanExtra()方法
		 */
		Intent intent=getIntent();
		passuseraccount=intent.getStringExtra("useraccount");
		usertype = (String)getIntent().getStringExtra("usertype");
//		初始化各UI控件
		fingViewById();
		getData();
//		定义的事件触发器
		ClickListener cl = new ClickListener();		
		submit_relation.setOnClickListener(cl);
		cancel_relation.setOnClickListener(cl);
		addDevicePageBack.setOnClickListener(cl); 
	}
	
	private void fingViewById(){
		addDevicePageBack = (TextView) findViewById(R.id.adddevice_page_back);
		deviceID=(EditText) findViewById(R.id.deviceID);
		submit_relation=(Button) findViewById(R.id.submit_relation);
		cancel_relation =(Button) findViewById(R.id.cancel_relation);
	}
	
	private void getData(){
		Constants.params = new ArrayList<NameValuePair>();
			Constants.params.add(new BasicNameValuePair("useraccount",passuseraccount));
			Constants.requestUrl = Constants.SERVER_HOST+"/getDeviceInfo.action";	///
			new Thread(getJson2).start();//新建一个线程
	}
	
//	定义一个Runnable，其主要操作是调用向服务器发送HTTP请求的函数
	private Runnable getJson2 = new Runnable(){
		public void run() {
			// TODO Auto-generated method stub
			try{
				Constants.result= reqtoserver.GetResponseFromServer(Constants.requestUrl,Constants.params);
				handler.sendEmptyMessage(0x04);
			}catch(Exception e){
				handler.sendEmptyMessage(0x05);
			}
		}
	};
	
	//定义的事件触发器，当点击相应控件时，分别调用不同函数进行处理
	private final class ClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.submit_relation:
				addUser();
				break;
			case R.id.cancel_relation:
				cancelRelateDevice();
				break;
			case R.id.adddevice_page_back:
				returnToPriview();
				AddDevice.this.finish();
				break;
			default:
				break;
			}
		}
	}
//	返回到上一个界面
	private void returnToPriview(){
		Intent intent = new Intent();
		intent.putExtra("usertype",usertype);
		setResult(RESULT_OK,intent);
	}
//	当用户在屏幕界面上点击返回键时，调用以下函数，结束当前活动
	public void onBackPressed(){
		returnToPriview();
		AddDevice.this.finish();
	}
	
	/*
	 * 功能：当用户点击提交按钮时，首先获取文本框中的设备编号，若编号为空，则向用户给出提示，
	 * 然后，将用户名和设备编号以健值对的形式保存到params中，通过params传递给服务器，再新建一个线程，调用向服务器发送http请求的函数
	 */
		private void addUser(){
			String deviceid=deviceID.getText().toString();
			if(null == deviceid || deviceid.length() == 0){
				Toast.makeText(getApplicationContext(), "设备编号不能为空", Toast.LENGTH_SHORT).show();
				return;
			}
			Constants.params = new ArrayList<NameValuePair>();
			Constants.params.add(new BasicNameValuePair("useraccount", passuseraccount));
			Constants.params.add(new BasicNameValuePair("pillowNumber", deviceid));
			
			Constants.requestUrl = Constants.SERVER_HOST+"/AddPillowDevice.action";
			new Thread(getJson).start();//创建一个新的线程
		}
		private Runnable getJson=new Runnable(){
			public void run() {
				// TODO Auto-generated method stub
				try{
					Constants.result=reqtoserver.GetResponseFromServer(Constants.requestUrl,Constants.params);
					handler.sendEmptyMessage(0x00);
				}
				catch(Exception e){
					handler.sendEmptyMessage(0x01);
				}
			}
		};
		
		//这一部分代码要重新实现
		private void cancelRelateDevice(){
			String deviceid=deviceID.getText().toString();
			if(null == deviceid || deviceid.length() == 0){
				Toast.makeText(getApplicationContext(), "设备编号不能为空", Toast.LENGTH_SHORT).show();
				return;
			}
			Constants.params = new ArrayList<NameValuePair>();
			Constants.params.add(new BasicNameValuePair("useraccount", passuseraccount));
//			Constants.params.add(new BasicNameValuePair("pillowNumber", deviceid));
			
			Constants.requestUrl = Constants.SERVER_HOST+"/cancelRelateDevice.action";
			new Thread(getJson1).start();//创建一个新的线程
		}
		
		private Runnable getJson1=new Runnable(){
			public void run() {
				// TODO Auto-generated method stub
				try{
					Constants.result=reqtoserver.GetResponseFromServer(Constants.requestUrl,Constants.params);
					handler.sendEmptyMessage(0x02);
				}
				catch(Exception e){
					handler.sendEmptyMessage(0x03);
				}
			}
		};

		Handler handler = new Handler() {
			public void handleMessage(android.os.Message msg) {
				if (msg.what == 0x00) {
					if(null != Constants.result){
						try {
							JSONObject json= new JSONObject(Constants.result);
							String result = (String)json.get("result");	
							if(result.equalsIgnoreCase("ok")){
								Toast.makeText(getApplicationContext(), "设备添加成功", Toast.LENGTH_SHORT).show();
//				添加设备成功后，返回上一界面
								Intent intent = new Intent();
								intent.putExtra("usertype",usertype);
								setResult(RESULT_OK,intent);
								AddDevice.this.finish();
							}		
						} catch (JSONException e) {
							e.printStackTrace();
						}  
					}		
				} else if (msg.what == 0x01) {
					Toast.makeText(getApplicationContext(), "获取失败",Toast.LENGTH_LONG).show();
				}else if (msg.what == 0x02) {
					if(null != Constants.result){
						try {
							JSONObject json= new JSONObject(Constants.result);
							String result = (String)json.get("result");	
							//String initmsg = (String)json.get("msg");
							if(result.equalsIgnoreCase("1")){
								
								cancel_relation.setText("目前无关联设备");
								Toast.makeText(getApplicationContext(), "取消关联成功",Toast.LENGTH_LONG).show();
							}		
						} catch (JSONException e) {
							e.printStackTrace();
						}  
					}		
				} else if (msg.what == 0x03) {
					Toast.makeText(getApplicationContext(), "取消关联失败",Toast.LENGTH_LONG).show();
				}
				else if (msg.what == 0x04) {
					if(null != Constants.result){
						try {
							JSONObject json= new JSONObject(Constants.result);
							String result = (String)json.get("result");	
							String initmsg = (String)json.get("msg");
							if(result.equalsIgnoreCase("1")){
								deviceID.setText(initmsg);
							}		
						} catch (JSONException e) {
							e.printStackTrace();
						}  
					}		
				} else if (msg.what == 0x05) {
					Toast.makeText(getApplicationContext(), "获取失败",Toast.LENGTH_LONG).show();
				}
			}
		};
		
}
