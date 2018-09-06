package com.pillow.ui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.pillow.R;
import com.pillow.application.ExitAllActivity;
import com.pillow.bean.Constants;
import com.pillow.bean.RequestToServer;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

public class TestSendData extends Activity {

	List<NameValuePair> params;
	private String result;
	private String myurl;
	private String curtime;
	private int flag = 1; 
	public static String TAG="TestSendData";
	RequestToServer reqtoserver=new RequestToServer();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.testsenddata);
		ExitAllActivity.getActivityInstance().addActivity(this);
		
		while (flag==1) {
			getData();	
		}
	}
	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		flag = 0;
		super.onDestroy();
	}
	
	@Override  
    protected void onStart() {  
        // TODO Auto-generated method stub  
        super.onStart();  
        Log.e(TAG, "onStart方法被调用");
    }  
  
  @Override  
  protected void onRestart() {
    // TODO Auto-generated method stub  
	  super.onRestart();
	  Log.e(TAG, "onRestart方法被调用");  
  }  

  @Override  
  protected void onResume() {  
    // TODO Auto-generated method stub  
	  super.onResume();  
	  flag = 0;
	  Log.e(TAG, "onResume方法被调用");  
  }  

  @Override  
    protected void onPause() {  
        // TODO Auto-generated method stub  
        super.onPause();  
        flag = 0;
        Log.e(TAG, "onPause方法被调用");  
    }  
  
    @Override
    protected void onStop() {  
        // TODO Auto-generated method stub  
    	flag = 0;
        super.onStop();  
        Log.e(TAG, "onStop方法被调用");  
    }  
   
	
	
	private void getData(){
		params =new ArrayList<NameValuePair>();
		
		SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		String curtime=format.format(new java.util.Date());

		params.add(new BasicNameValuePair("curtime",curtime));
		
		myurl = Constants.SERVER_HOST+"/testSendData.action";	
		new Thread(getJson).start();
	}
	
	private Runnable getJson = new Runnable() {

		public void run() {
			// TODO Auto-generated method stub
			try {
				result =  reqtoserver.GetResponseFromServer(myurl, params);
				handler.sendEmptyMessage(0x00);
			} catch (Exception e) {
				handler.sendEmptyMessage(0x01);
			}
		}
	};
	
	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			 if (msg.what == 0x00) {
				Log.v("PostGetJson", "" + result);
				if(null != result){
					try {
						JSONObject json= new JSONObject(result);  		
						String re = (String)json.get("result");
						int ret=Integer.parseInt(re);
			if(ret==1){	
				Toast.makeText(getApplicationContext(), "传递数据成功", Toast.LENGTH_LONG).show();
							
						}else if(ret==0){
							Toast.makeText(getApplicationContext(), "传递数据失败", Toast.LENGTH_LONG).show();
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
