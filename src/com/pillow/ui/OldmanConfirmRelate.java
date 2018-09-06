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
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
//当有子女请求关联老人时，该页面老人对子女的请求进行确认
public class OldmanConfirmRelate extends Activity {

	private TextView confirmrelate_page_back,confirm_prompt,already_relate;
	private EditText oldman_account,child_account_Edittext;
	private Button submit_relation,cancel_relation;
	private static String childAccount="",oldmanAccount="",usertype ="",TAG="MainActivity";
	private LinearLayout first_linear_layout,second_linear_layout,total_linear_layout;
	private	RelativeLayout personal_top_layout;
	RequestToServer reqtoserver=new RequestToServer();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.oldman_confirm_relate);
		
		fingViewById();
		childAccount = getIntent().getStringExtra("childAccount");
		oldmanAccount = getIntent().getStringExtra("oldmanAccount");
		usertype = getIntent().getStringExtra("usertype");
		
		ClickListener cl = new ClickListener();		
		submit_relation.setOnClickListener(cl);
		cancel_relation.setOnClickListener(cl);
		confirmrelate_page_back.setOnClickListener(cl);
		
		getConfirmData();
//		getRelateObject();
		initDate();
	}
	
	 @Override  
	    protected void onStart() {  
	        // TODO Auto-generated method stub  
	        super.onStart();  
	        Log.e(TAG, "onStart方法被调用"); 
	        	oldmanAccount = getIntent().getStringExtra("oldmanAccount");
				getConfirmData();
	    }  
	  
	  @Override  
	  protected void onRestart() {
     // TODO Auto-generated method stub  
		  super.onRestart();  
		  Log.e(TAG, "onRestart方法被调用");  
		  oldmanAccount = getIntent().getStringExtra("oldmanAccount");
		  getConfirmData();
	  }  

	  @Override  
	  protected void onResume() {  
     // TODO Auto-generated method stub  
		  super.onResume();  
		  Log.e(TAG, "onResume方法被调用");  
		  oldmanAccount = getIntent().getStringExtra("oldmanAccount");
		  getConfirmData();
	  }  
//	初始化各函数控件
	private void fingViewById(){
		first_linear_layout = (LinearLayout) findViewById(R.id.first_linear_layout);
		
		total_linear_layout = (LinearLayout) findViewById(R.id.total_linear_layout);
		personal_top_layout = (RelativeLayout) findViewById(R.id.personal_top_layout);
		confirmrelate_page_back = (TextView) findViewById(R.id.confirmrelate_page_back);
		confirm_prompt = (TextView) findViewById(R.id.confirm_prompt);
	
		child_account_Edittext = (EditText) findViewById(R.id.child_account);
		submit_relation=(Button) findViewById(R.id.submit_relation);
		cancel_relation=(Button) findViewById(R.id.cancel_relation);
	}
	
	private void initDate(){
		child_account_Edittext.setText(childAccount);
	}
	
	//界面初始化函数
			private void getRelateObject(){
				Constants.params = new ArrayList<NameValuePair>();
					Constants.params.add(new BasicNameValuePair("useraccount",oldmanAccount));
					Constants.requestUrl = Constants.SERVER_HOST+"/getRelateObject.action";	///
					new Thread(getJson3).start();//新建一个线程
			}
			
//			定义一个Runnable，其主要操作是调用向服务器发送HTTP请求的函数
			private Runnable getJson3= new Runnable(){
				public void run() {
					// TODO Auto-generated method stub
					try{
						Constants.result= reqtoserver.GetResponseFromServer(Constants.requestUrl,Constants.params);
						handler.sendEmptyMessage(0x05);
					}
					catch(Exception e){
						handler.sendEmptyMessage(0x06);
					}
				}
			};
	
	private void getConfirmData(){
		Constants.params = new ArrayList<NameValuePair>();
		Constants.params.add(new BasicNameValuePair("Oldmanuseraccount",oldmanAccount));
		Constants.requestUrl = Constants.SERVER_HOST+"/FirstPageGetConfirmData.action";	
		new Thread(getJson2).start();
	}
	
	private Runnable getJson2 = new Runnable() {
		public void run() {
			// TODO Auto-generated method stub
			try {
				Constants.result = reqtoserver.GetResponseFromServer(Constants.requestUrl, Constants.params);
				handler.sendEmptyMessage(0x02);
			} catch (Exception e) {
				handler.sendEmptyMessage(0x01);
			}
		}
	};
	
	private final class ClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.submit_relation:
				addUser();
				break;
			case R.id.cancel_relation:
				cancelRelation();
				break;
			case R.id.confirmrelate_page_back:
				OldmanConfirmRelate.this.finish();
				break;
			default:
				break;
			}
		}
	}
	
		private void addUser(){
			Constants.params = new ArrayList<NameValuePair>();
			Constants.params.add(new BasicNameValuePair("Oldmanuseraccount", oldmanAccount));
			Constants.params.add(new BasicNameValuePair("childuseraccount", childAccount));
			Constants.requestUrl = Constants.SERVER_HOST+"/OldmanConfirmRelate.action";
			new Thread(getJson).start();
		}
		
		private Runnable getJson=new Runnable(){
			public void run() {
				// TODO Auto-generated method stub
				try{
					Constants.result= reqtoserver.GetResponseFromServer(Constants.requestUrl,Constants.params);
					handler.sendEmptyMessage(0x00);
				}
				catch(Exception e){
					handler.sendEmptyMessage(0x01);
				}
			}
		};
		private void cancelRelation(){
			Constants.params = new ArrayList<NameValuePair>();
			Constants.params.add(new BasicNameValuePair("Oldmanuseraccount", oldmanAccount));
			Constants.params.add(new BasicNameValuePair("childuseraccount", childAccount));
			Constants.requestUrl = Constants.SERVER_HOST+"/cancelOldmanRelateChild.action";
			new Thread(getJson1).start();
		}
		
		private Runnable getJson1=new Runnable(){
			public void run() {
				// TODO Auto-generated method stub
				try{
					Constants.result= reqtoserver.GetResponseFromServer(Constants.requestUrl,Constants.params);
					handler.sendEmptyMessage(0x03);
				}
				catch(Exception e){
					handler.sendEmptyMessage(0x04);
				}
			}
		};
		

		Handler handler = new Handler() {
			public void handleMessage(android.os.Message msg) {
				/*处理服务器的返回信息*/
				if (msg.what == 0x00) {
					Log.v("PostGetJson", "" + Constants.result);
					if(null != Constants.result){
						try {
							JSONObject json= new JSONObject(Constants.result);  		
								
							String result = (String)json.get("result");	
							if(result.equalsIgnoreCase("1")){
								
								Toast.makeText(getApplicationContext(), "关联成功", Toast.LENGTH_SHORT).show();
							
								Intent intent = new Intent();
								intent.putExtra("useraccount",oldmanAccount);
								intent.putExtra("usertype",usertype);
								setResult(RESULT_OK,intent);
								OldmanConfirmRelate.this.finish();
							}else if(result.equalsIgnoreCase("0")){
							Toast.makeText(getApplicationContext(), "不能重复关联老人和子女", Toast.LENGTH_LONG).show();
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}  
					}		
				}
				else if (msg.what == 0x02) {
					Log.v("PostGetJson", "" + Constants.result);
					if(null != Constants.result){
						try {
							JSONObject json= new JSONObject(Constants.result);  		
							String re = (String)json.get("result");
							String retmsg = (String)json.get("msg");
							int ret=Integer.parseInt(re);
							if(ret==1){
								 childAccount = retmsg;
								 child_account_Edittext.setText(childAccount);
								 confirm_prompt.setText(childAccount+"请求关联您的账号");		
							}else if(ret==0){
								confirm_prompt.setText("目前没有用户请求关联您的账号");	
//								total_linear_layout.setVisibility(View.GONE);
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}  
					}
				}
				else if (msg.what == 0x01) {
					Toast.makeText(getApplicationContext(), "获取失败",Toast.LENGTH_LONG).show();
				}else if (msg.what == 0x03) {
					Log.v("PostGetJson", "" + Constants.result);
					if(null != Constants.result){
						try {
							JSONObject json= new JSONObject(Constants.result);  		
								
							String result = (String)json.get("result");	
							if(result.equalsIgnoreCase("1")){
								
								Toast.makeText(getApplicationContext(), "取消关联成功", Toast.LENGTH_SHORT).show();
								cancel_relation.setText("取消关联成功");
								Intent intent = new Intent();
								intent.putExtra("useraccount",oldmanAccount);
								intent.putExtra("usertype",usertype);
								setResult(RESULT_OK,intent);
								OldmanConfirmRelate.this.finish();
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}  
					}		
				}else if (msg.what == 0x05) {
					if(null != Constants.result){
						try {//处理从服务器端返回的数据
							JSONObject json= new JSONObject(Constants.result); 
							String result = (String)json.get("result");	
							String initmsg = (String)json.get("msg");
							if(result.equalsIgnoreCase("1")){
								//String[] strsArray = initmsg.split("=");
	                           // System.out.println("strsArray .length=="+strsArray .length+" strsArray=="+strsArray[0]+"  initmsg=="+initmsg);
	                            
	                            already_relate.setText("已关联对象： "+initmsg);
	                    		
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}  
					}		
				} else if (msg.what == 0x06) {
					Toast.makeText(getApplicationContext(), "暂无关联对象",Toast.LENGTH_LONG).show();
					already_relate.setText("暂无关联对象");
				}
			}
		};
	
}
