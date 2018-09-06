package com.pillow.ui;

import java.util.ArrayList;

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
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
//对应于用户注册页面,完成用户的信息注册功能
public class register extends Activity {
//定义需要用到的各参数
	private Button register;
	private EditText account,password;
	private TextView regreturnresult,addDevicePageBack;
	private	RadioGroup rg_user_type;
	private RadioButton Oldman,child;
	private String usertype="";
	RequestToServer reqtoserver=new RequestToServer();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.register);
		findViewById();
//		定义的事件触发器
		ClickListener cl = new ClickListener();		
		register.setOnClickListener(cl);
		addDevicePageBack.setOnClickListener(cl); 
//		判断选中的用户类型是老人还是子女
		if(Oldman.getId() == rg_user_type.getCheckedRadioButtonId()){
			usertype = Oldman.getText().toString();
		}else if(child.getId() == rg_user_type.getCheckedRadioButtonId()){
			usertype = child.getText().toString();
		}
//		判断用户类型是老人还是子女
		rg_user_type.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				if(checkedId == Oldman.getId()){
					usertype = Oldman.getText().toString();
				}else if(checkedId == child.getId()){
					usertype = child.getText().toString();
				}		
			}
		});
		
	}
	//初始化各种UI控件
	private void findViewById(){
		addDevicePageBack = (TextView) findViewById(R.id.adddevice_page_back);
		account		 = (EditText) findViewById(R.id.account);
		password 	 = (EditText) findViewById(R.id.password);
		register = (Button) findViewById(R.id.register);
		rg_user_type=	(RadioGroup) findViewById(R.id.rg_user_type);
		Oldman=	(RadioButton) findViewById(R.id.Oldman);
		child=	(RadioButton) findViewById(R.id.child);
		regreturnresult=(TextView) findViewById(R.id.regreturnresult);
	}
	
//	这一部分是按钮触发事件
	private final class ClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.register://点击注册按钮时，调用注册函数
				registerUser();
				break;
			case R.id.adddevice_page_back://点击返回按钮时，结束当前活动
				register.this.finish();
				break;
			default:
				break;
			}
		}
	}
	/*
	 * 功能：当用户点击注册按钮时，首先获取文本框中输入的账号和密码，若账号或密码为空，则给出提示，
	 * 然后将账号和密码以健值对的形式添加到params中，通过params将参数的值传递给服务器，再新建一个线程，调用向服务器发送HTTP请求的函数
	 */
		private void registerUser(){
//			获取输入的用户名和密码
			String useraccount = account.getText().toString().trim(); 
			String userpassword= password.getText().toString().trim();
//			判断用户名和密码是否为空，如果为空，则给出提示信息
			if(null == useraccount || useraccount.length() == 0){
				Toast.makeText(getApplicationContext(), "用户名不能为空", Toast.LENGTH_SHORT).show();
				return;
			}
			if(null == userpassword || userpassword.length() == 0){
				Toast.makeText(getApplicationContext(), "密码不能为空", Toast.LENGTH_SHORT).show();
				return;
			}
//			把需要传递给服务器的参数添加到params参数中
			Constants.params = new ArrayList<NameValuePair>();
			Constants.params.add(new BasicNameValuePair("useraccount", useraccount));
			Constants.params.add(new BasicNameValuePair("password", userpassword));
//		判断用户类型是老人还是子女
			if(usertype.equalsIgnoreCase("老人")){
				Constants.requestUrl = Constants.SERVER_HOST+"/userAdd.action";
			}else if(usertype.equalsIgnoreCase("子女")){
				Constants.requestUrl = Constants.SERVER_HOST+"/childUserAdd.action";
			}
			new Thread(getJson).start();//新建一个线程
		}
//		定义一个Runnable，调用向服务器发送HTTP请求的函数
		private Runnable getJson=new Runnable(){
			public void run() {
				// TODO Auto-generated method stub
				try{
					Constants.result= reqtoserver.GetResponseFromServer(Constants.requestUrl, Constants.params);
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
//							这一部分处理服务器的返回结果
							JSONObject json= new JSONObject(Constants.result);  		
							String flag = (String)json.get("result");	
							String returnMsg = (String)json.get("msg");
							int flagnum=Integer.parseInt(flag);
							if(flagnum==1){//如果服务器返回值为1，则说明注册成功
							regreturnresult.setText(returnMsg);
							jumptologin();
							}else{//如果服务器返回值为0，则说明注册失败，在注册界面上显示服务器返回的提示消息
								regreturnresult.setText(returnMsg);
							}		
						} catch (JSONException e) {
							e.printStackTrace();
						}  
					}		
				} else if (msg.what == 0x01) {
					Toast.makeText(getApplicationContext(), "用户注册失败",Toast.LENGTH_SHORT).show();
				}
			}
		};
		
//		注册成功后，跳转到登录界面
		public void jumptologin(){
			Intent intent = new Intent();
			intent.setClass(getApplicationContext(),login.class);
			startActivity(intent);
		}
}
