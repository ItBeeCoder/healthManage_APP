package com.pillow.notification;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
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
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.pillow.R;
import com.pillow.http.AsyncHttpClient;
import com.pillow.ui.MainActivity;
import com.pillow.utils.ToastUtil;


public class PushSmsService extends Service {
	private MyThread myThread;
	private NotificationManager manager;
	private Notification notification;
	private PendingIntent pi;
	private AsyncHttpClient client;
	private boolean flag = true;
	String myurl,result;
	
	private String  tag = "FirstSuccessNotifiActivity";
    private Button btn_sendMsg;
    private String initmsg = "";
    private TextView tv_content,tv_number,tv_date,tv_address;
    //WebSocketClient 和 address
    private WebSocketClient mWebSocketClient;
    private String address = "ws://192.168.43.93:8881/healthCare1126/init";
//    private String address = "ws://192.168.43.93:8889/websocketServerclient/init";
    private String username="",time="",exceptype="",homeaddress="";
    
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		this.client = new AsyncHttpClient();
		this.myThread = new MyThread();
		this.myThread.start();//启动线程
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		this.flag = false;
		super.onDestroy();
	}

	private class MyThread extends Thread {
		@Override
		public void run() {
			try {
				initSocketClient();
			    connect();
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				}
			}

    /**
     * 作用：初始化WebSocketClient
     * @throws URISyntaxException
     */
    private void initSocketClient() throws URISyntaxException {
        if(mWebSocketClient == null) {
            mWebSocketClient = new WebSocketClient(new URI(address)) {
                @Override
                public void onOpen(ServerHandshake serverHandshake) {//连接成功
                    Log.d(tag,"opened connection");
                }
                @Override
                public void onMessage(String s) {//服务端消息
//                    initmsg += s + "\n";
                	initmsg = s ;
                    Message msg = new Message();
                    msg.what = 1;
                    myhandler.sendMessage(msg);
                    Log.d(tag,"received:" + s);
                }

                @Override    
                public void onClose(int i, String s, boolean remote) {
                	//连接断开，remote判定是客户端断开还是服务端断开
                    Log.d(tag,"Connection closed by " + ( remote ? "remote peer" : "us" ) + ", info=" + s);
                    closeConnect();
                }
                @Override
                public void onError(Exception e) {
                    Log.d(tag,"error:" + e);
                }
            };
        }
    }

    //连接
    private void connect() {
        new Thread(){
            @Override
            public void run() {
                mWebSocketClient.connect();
            }
        }.start();
    }

    //断开连接
    private void closeConnect() {
        try {
            mWebSocketClient.close();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        finally {
            mWebSocketClient = null;
        }
    }

//发送消息
    @SuppressWarnings("unused")
	private void sendMsg(String msg) {
        mWebSocketClient.send(msg);
    }
	
	/*
	 * 作用：定义与推送消息有关的各种参数
	 */
	@SuppressWarnings("deprecation")
	private void notification(String username,String time,String exceptype,String homeaddress){
		manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		notification = new Notification(R.drawable.ic_menu_compose, "异常报警消息",
				System.currentTimeMillis());
		notification.defaults = Notification.DEFAULT_ALL; // 
		notification.flags = Notification.FLAG_AUTO_CANCEL; // 
		notification.flags |= Notification.FLAG_NO_CLEAR;// 
	
		Intent intent = new Intent(getApplicationContext(),
				ContentActivity.class);
		intent.putExtra("username", username);
		intent.putExtra("time", time);
		intent.putExtra("exceptype", exceptype);
		intent.putExtra("homeaddress", homeaddress);
	
		pi = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
		notification.setLatestEventInfo(getApplicationContext(), username
				+ "的异常报警消息", "异常报警消息", pi);
		manager.notify(0, notification);
		
	}


	 Handler myhandler = new Handler(){
	        public void handleMessage(Message msg) {
	            super.handleMessage(msg);
	            switch (msg.what){
	                case 1:
//	                	处理接收到的异常消息，首先通过处理得到服务器的推送消息
	                	String[] strsArray = initmsg.split("=");
	                	System.out.println("strsArray .length=="+strsArray .length);
	                	username=strsArray[0];
	                	time=strsArray[1];
	                	exceptype=strsArray[2];
	                	homeaddress=strsArray[3];
//	                	接收到服务器的推送消息时，调用客户端的推送消息服务
	                	notification(username, time,exceptype,homeaddress);
	                	
//	                    tv_content.setText("用户姓名： "+username);
//	                    tv_number.setText("日期： "+time);
//	                    tv_date.setText("异常类型： "+exceptype);
//	                    tv_address.setText("家庭住址： "+homeaddress);
	                    break;
	            }
	        }
	    };
	
}
