/**
 *日期：2018年1月27日下午10:12:11
pillow
TODO
author：shaozq
 */
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author shaozq
 *2018年1月27日下午10:12:11
 */
public class LookRelateObject extends Activity {

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
		setContentView(R.layout.look_relate_object);
		
		fingViewById();
		childAccount = getIntent().getStringExtra("childAccount");
		oldmanAccount = getIntent().getStringExtra("oldmanAccount");
		usertype = getIntent().getStringExtra("usertype");
		
		ClickListener cl = new ClickListener();
		confirmrelate_page_back.setOnClickListener(cl);
		
		getRelateObject();
		
	}
	
	 @Override  
	    protected void onStart() {  
	        // TODO Auto-generated method stub  
	        super.onStart();  
	        Log.e(TAG, "onStart方法被调用"); 
	        	oldmanAccount = getIntent().getStringExtra("oldmanAccount");
	        	getRelateObject();
	    }  
	  
	  @Override  
	  protected void onRestart() {
     // TODO Auto-generated method stub  
		  super.onRestart();  
		  Log.e(TAG, "onRestart方法被调用");  
		  oldmanAccount = getIntent().getStringExtra("oldmanAccount");
		  getRelateObject();
	  }  

	  @Override  
	  protected void onResume() {
     // TODO Auto-generated method stub  
		  super.onResume();  
		  Log.e(TAG, "onResume方法被调用");  
		  oldmanAccount = getIntent().getStringExtra("oldmanAccount");
		  getRelateObject();
		}  
//	初始化各函数控件
	private void fingViewById(){
		second_linear_layout = (LinearLayout) findViewById(R.id.second_linear_layout);	
		confirmrelate_page_back = (TextView) findViewById(R.id.confirmrelate_page_back);
		already_relate = (TextView) findViewById(R.id.already_relate);
	}
	

	
	//界面初始化函数
			private void getRelateObject(){
				Constants.params = new ArrayList<NameValuePair>();
				if(usertype.equalsIgnoreCase("老人")){
					Constants.params.add(new BasicNameValuePair("useraccount",oldmanAccount));
					Constants.requestUrl = Constants.SERVER_HOST+"/getRelateObject.action";	
				}else{
					Constants.params.add(new BasicNameValuePair("useraccount",childAccount));
					Constants.requestUrl = Constants.SERVER_HOST+"/getChildRelateObject.action";	
				}
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
	
	
	private final class ClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.confirmrelate_page_back:
				LookRelateObject.this.finish();
				break;
			default:
				break;
			}
		}
	}
	
		Handler handler = new Handler() {
			public void handleMessage(android.os.Message msg) {
				/*处理服务器的返回信息*/
				if (msg.what == 0x05) {
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
