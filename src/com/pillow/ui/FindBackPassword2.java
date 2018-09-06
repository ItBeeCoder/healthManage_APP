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

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

import com.pillow.R;
import com.pillow.bean.Constants;
import com.pillow.bean.RequestToServer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.FrameLayout.LayoutParams;

public class FindBackPassword2 extends Activity {

	
	private TextView addDevicePageBack;
	private EditText user_account,new_password,confirm_new_password;
	private Button submit_relation;
	String passuseraccount;
	private String usertype = "";
	private	RadioGroup rg_user_type;
	private RadioButton Oldman,child;
	RequestToServer reqtoserver=new RequestToServer();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.find_back_password2);
		
		Intent intent=getIntent();
		passuseraccount=intent.getStringExtra("useraccount");
		usertype = (String)getIntent().getStringExtra("usertype");
		
		fingViewById();
		
		ClickListener cl = new ClickListener();		
		submit_relation.setOnClickListener(cl);
		addDevicePageBack.setOnClickListener(cl); 
		
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
	}
	
	private void fingViewById(){
		addDevicePageBack = (TextView) findViewById(R.id.find_pass_page2_back);
		user_account = (EditText) findViewById(R.id.user_account);
		new_password = (EditText) findViewById(R.id.new_password);
		confirm_new_password = (EditText) findViewById(R.id.confirm_new_password);
		submit_relation=(Button) findViewById(R.id.submit_relation);
		rg_user_type=	(RadioGroup) findViewById(R.id.rg_user_type);
		Oldman=	(RadioButton) findViewById(R.id.Oldman);
		child=	(RadioButton) findViewById(R.id.child);
	}
	
	
	//定义的事件监听器
	private final class ClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.submit_relation:
				addUser();
				break;
			case R.id.find_pass_page2_back:
				returnToPriview();
				FindBackPassword2.this.finish();
				break;
			default:
				break;
			}
		}
	}
	
	private void returnToPriview(){
		Intent intent = new Intent();
		intent.putExtra("usertype",usertype);
		setResult(RESULT_OK,intent);
	}
	public void onBackPressed(){
		returnToPriview();
		
	}
	
	//
		private void addUser(){
			String userAccount=user_account.getText().toString();
			String newPassword= new_password.getText().toString();
			
			String confirmNewPassword = confirm_new_password.getText().toString();
			Constants.params = new ArrayList<NameValuePair>();
			Constants.params.add(new BasicNameValuePair("useraccount", userAccount));
			Constants.params.add(new BasicNameValuePair("newpassword", newPassword));
			
			if(usertype.equalsIgnoreCase("老人")){
				Constants.requestUrl = Constants.SERVER_HOST+"/updateOldmanPassWord.action";
			}else if(usertype.equalsIgnoreCase("子女")){
				Constants.requestUrl = Constants.SERVER_HOST+"/updateChildPassWord.action";
			}
			
			new Thread(getJson).start();
		}
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
					if(null != Constants.result){
						try {
							JSONObject json= new JSONObject(Constants.result);  		
//							得到从服务器端返回的结果
							String result = (String)json.get("result");	
							if(result.equalsIgnoreCase("ok")){
								
								Intent intent = new Intent();
								intent.setClass(getApplicationContext(),FindBackPassword3.class);
								startActivity(intent);
							}	
						} catch (JSONException e) {
							e.printStackTrace();
						}  
					}		
				} else if (msg.what == 0x01) {
					Toast.makeText(getApplicationContext(), "获取失败",Toast.LENGTH_LONG).show();
				}
			}
		};

}
