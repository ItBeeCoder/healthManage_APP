package com.pillow.ui;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
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
import com.pillow.application.AllApplication;
import com.pillow.application.ExitAllActivity;
import com.pillow.bean.Constants;
import com.pillow.bean.RequestToServer;
import com.pillow.utils.SharedPreferenceUtil;
import com.pillow.utils.ToastUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
//对应登录界面
public class login extends Activity {

//	定义各种参数
		private TextView userregister,forgetPassword;
		private Button login;
		private EditText loginaccount,loginpassword;
		private CheckBox rememberme,autologin;
		private	RadioGroup rg_user_type;
		private RadioButton Oldman,child;
		private String usertype="";
		private SharedPreferences sp;
		RequestToServer reqtoserver=new RequestToServer();
		
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			setContentView(R.layout.login);
			findViewById();
			ExitAllActivity.getActivityInstance().addActivity(this);
//			定义的事件触发器
			ClickListener cl = new ClickListener();		
			login.setOnClickListener(cl);
			userregister.setOnClickListener(cl);//点击用户注册时的触发事件
			forgetPassword.setOnClickListener(cl);//点击忘记密码时的触发事件
	
//			判断用户类型，老人还是子女
			if(Oldman.getId() == rg_user_type.getCheckedRadioButtonId()){
				usertype = Oldman.getText().toString();
			}else if(child.getId() == rg_user_type.getCheckedRadioButtonId()){
				usertype = child.getText().toString();
			}

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
			
			sp = this.getSharedPreferences("userInfo", Context.MODE_WORLD_READABLE);
			
			//这一部分实现自动登录功能，保存用户名和密码，在用户下次登录时，直接进入APP主界面
			if(sp.getBoolean("ISCHECK", false)){
				
				rememberme.setChecked(true);
				loginaccount.setText(sp.getString("USER_NAME", ""));
				loginpassword.setText(sp.getString("PASSWORD", ""));
	    	  
				if(sp.getBoolean("AUTO_ISCHECK", false)){
					autologin.setChecked(true);
					useraccount = loginaccount.getText().toString().trim(); 
					userpassword= loginpassword.getText().toString().trim();
				if(null == useraccount || useraccount.length() == 0){
					ToastUtil.showToast(this, "username can not be empty");
					return;
				}
				if(null == userpassword || userpassword.length() == 0){
					ToastUtil.showToast(this, "password can not be empty");
					return;
			}
	       		Intent intent = new Intent();
				intent.putExtra("useraccount",useraccount);
				intent.putExtra("usertype",usertype);
				intent.setClass(getApplicationContext(),MainActivity.class);
				startActivity(intent);	
	       	  }
	        }
			//这一部分实现记住用户名和密码的功能
			rememberme.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
					if (rememberme.isChecked()) {
						sp.edit().putBoolean("ISCHECK", true).commit();
					}else {
						sp.edit().putBoolean("ISCHECK", false).commit();	
					}
				}
			});
			
			autologin.setOnCheckedChangeListener(new OnCheckedChangeListener() {
	            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
					if (autologin.isChecked()) {
						sp.edit().putBoolean("AUTO_ISCHECK", true).commit();
					} else {
						sp.edit().putBoolean("AUTO_ISCHECK", false).commit();
					}
				}
			});
		}
		
		//初始化各种UI控件
		private void findViewById(){
			loginaccount	 = (EditText) findViewById(R.id.loginaccount);
			loginpassword	 = (EditText) findViewById(R.id.loginpassword);
			login = (Button) findViewById(R.id.login);
			rg_user_type=	(RadioGroup) findViewById(R.id.rg_user_type);
			Oldman=	(RadioButton) findViewById(R.id.Oldman);
			child=	(RadioButton) findViewById(R.id.child);
			rememberme=(CheckBox)findViewById(R.id.rememberme);
			autologin =(CheckBox)findViewById(R.id.autologin);
			userregister=(TextView)findViewById(R.id.userregister);
			forgetPassword =  (TextView)findViewById(R.id.forget_password);
		}

		private final class ClickListener implements OnClickListener{
			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.login://点击登录按钮时调用addUser()函数，接着向服务器发送请求，在服务器端判断用户名和密码是否正确
					addUser();
					break;
				case R.id.userregister://点击用户注册时，转向注册页面
					registerUser();
					break;
				case R.id.forget_password://点击忘记密码时，转向找回密码页面
					forgetPassword();
					break;
				default:
					break;
				}
			}
		}
		String useraccount="",userpassword="";
		/*
		 * 函数功能：当用户在APP的登录界面点击登录时，调用addUser()函数，首先获取用户输入的账号和密码，然后判断账号和密码是否为空，
		 * 若为空，则向用户发送提示，否则，将用户账号和密码保存到params这个参数中，新建一个线程，向服务器端发送请求
		 */
		
		private void addUser(){
				useraccount = loginaccount.getText().toString().trim(); 
				userpassword= loginpassword.getText().toString().trim();
//				Toast.makeText(getApplicationContext(), "useraccount=="+useraccount+"userpassword=="+userpassword, Toast.LENGTH_SHORT).show();
				if(null == useraccount || useraccount.length() == 0){
					Toast.makeText(getApplicationContext(), "用户名不能为空", Toast.LENGTH_SHORT).show();
					return;
				}
				if(null == userpassword || userpassword.length() == 0){
					Toast.makeText(getApplicationContext(), "密码不能为空", Toast.LENGTH_SHORT).show();
					return;
				}
				if(rememberme.isChecked()){//如果选中了“记住我”这个复选框，则保存用户名和密码
				  Editor editor = sp.edit();
				  editor.putString("USER_NAME", useraccount);
				  editor.putString("PASSWORD",userpassword);
				  editor.commit();
				}
				Constants.params = new ArrayList<NameValuePair>();
				Constants.params.add(new BasicNameValuePair("useraccount",useraccount));//将用户账号和密码保存到params这个参数中
				Constants.params.add(new BasicNameValuePair("password", userpassword));
				if(usertype.equalsIgnoreCase("老人")){
					Constants.requestUrl = Constants.SERVER_HOST+"/login.action";
				}else if(usertype.equalsIgnoreCase("子女")){
					Constants.requestUrl = Constants.SERVER_HOST+"/ChildUserlogin.action";
				}
				new Thread(getJson).start();
			}
//			定义一个Runnable，其主要操作是调用向服务器发送HTTP请求的函数
			private Runnable getJson=new Runnable(){
				public void run() {
					// TODO Auto-generated method stub
					try{
						Constants.result= reqtoserver.GetResponseFromServer(Constants.requestUrl,Constants.params);
//						result= GetJson(myurl, params);//向服务器发送请求，Constants.requestUrl为服务器地址，Constants.params中保存向服务器传送的参数
						handler.sendEmptyMessage(0x00);
					}
					catch(Exception e){
						handler.sendEmptyMessage(0x01);
					}
				}
			};
//			Handler处理消息
			Handler handler = new Handler() {
				public void handleMessage(android.os.Message msg) {
					if (msg.what == 0x00) {
						Log.v("PostGetJson", "" + Constants.result);
						if(null != Constants.result){
							try {
								JSONObject json= new JSONObject(Constants.result);  		
								String re = (String)json.get("result");
//								String returnMsg = (String)json.get("msg");
								int ret=Integer.parseInt(re);
								if(ret==1){//如果服务器返回值为1，则说明用户名和密码正确，登陆成功
									Intent intent = new Intent();
									intent.putExtra("useraccount",useraccount);
									intent.putExtra("usertype",usertype);
									intent.setClass(getApplicationContext(),MainActivity.class);
									startActivity(intent);
								}else if(ret==0){
									Toast.makeText(getApplicationContext(), "The user is not exitsing", Toast.LENGTH_LONG).show();
								}else if(ret==2){
									Toast.makeText(getApplicationContext(), "Username or password is wrong", Toast.LENGTH_LONG).show();
								}	
							} catch (JSONException e) {
								e.printStackTrace();
							}  
						}		
					} else if (msg.what == 0x01) {
						Toast.makeText(getApplicationContext(), "请求错误",Toast.LENGTH_LONG).show();
					}
				}
			};
			
			private void forgetPassword(){
				Intent intent = new Intent();
				intent.setClass(getApplicationContext(),FindBackPassword1.class);
				startActivity(intent);
			}
			
			private void registerUser(){
				Intent intent = new Intent();
				intent.putExtra("username", useraccount);
				intent.setClass(getApplicationContext(),register.class);
				startActivity(intent);
			}
			
			@Override
			public boolean onKeyDown(int keyCode, KeyEvent event) {
				switch (keyCode) {
				case KeyEvent.KEYCODE_BACK:
					existGame();
					return false;
				}
				return super.onKeyDown(keyCode, event);
			}
			
			private void existGame(){
				TextView textView = new TextView(this);
		    	textView.setText("你确定要退出系统吗？");
		    	textView.setTextSize(24);
		    	textView.setGravity(Gravity.CENTER);
		    	textView.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
		    	textView.setTextColor(getResources().getColor(R.color.white));
				new AlertDialog.Builder(this)
					.setCustomTitle(textView)
					.setPositiveButton("是", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							finish();
						}
					})
					.setNegativeButton("否", new DialogInterface.OnClickListener() { 
					public void onClick(DialogInterface dialog, int which) {  
						dialog.dismiss();
            	}
				}).show();  
//				login.this.finish();
			}

				
}

