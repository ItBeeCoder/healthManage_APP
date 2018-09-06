/**
 *日期：2017年12月11日下午10:02:54
pillow
TODO
author：shaozq
 */
package com.pillow.notification;

import java.net.URI;
import java.net.URISyntaxException;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import com.pillow.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/**
 * @author shaozq
 *2017年12月11日下午10:02:54
 */
public class FirstSuccessNotifiActivity extends Activity implements
		OnClickListener {

	private String  tag = "FirstSuccessNotifiActivity";
    private Button btn_sendMsg;
    private String initmsg = "";
    private TextView tv_content,tv_status,tv_date,tv_address;
    //WebSocketClient 和 address
    private WebSocketClient mWebSocketClient;
    private String address = "ws://192.168.43.93:8881/healthCare1126/init";
//    private String address = "ws://192.168.43.93:8889/websocketServerclient/init";
    private String username="",time="",exceptype="",homeaddress="";
    private static int flag=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.notifi_first_success_activity);
        try {
            initSocketClient();
            connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        
        //初始化各UI控件
        initViews();
        //初始化按钮的点击事件
        initEvents();
        if(flag==1){
        	tv_status.setText("服务已经成功开启");
        }
    }
  //初始化WebSocketClient
    /**
     *
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

                Handler myhandler = new Handler(){
                	public void handleMessage(Message msg) {
                		super.handleMessage(msg);
                		switch (msg.what){
                			case 1:
                            String[] strsArray = initmsg.split("=");
                            System.out.println("strsArray .length=="+strsArray .length+" strsArray=="+strsArray[0]+"  initmsg=="+initmsg);
                            username=strsArray[0];
                            time=strsArray[1];
                            exceptype=strsArray[2];
                           homeaddress=strsArray[3];
                           tv_content.setText("用户姓名： "+username);
                        //   tv_number.setText("日期： "+time);
                           tv_date.setText("异常类型： "+exceptype);
                           tv_address.setText("家庭住址： "+homeaddress);
                        break;
                        }
                    }
                };
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
    
    private void initEvents() {
        btn_sendMsg.setOnClickListener(this);
    }

    private void initViews() {
        btn_sendMsg = (Button) findViewById(R.id.btn_sendmsg);
        tv_content = (TextView) this.findViewById(R.id.tv_content);
        tv_status = (TextView) this.findViewById(R.id.tv_status);
		tv_date = (TextView) this.findViewById(R.id.tv_date);
		tv_address=(TextView) this.findViewById(R.id.tv_address);
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
 
    @Override
    public void onClick(View v) {
    	switch (v.getId()){  
    		case R.id.btn_sendmsg:
    			String msg = "APP客户端发来的消息";
    			sendMsg(msg);
    			flag=1;
    			tv_status.setText("服务已经成功开启");
    			Intent intent = new Intent(this, PushSmsService.class);
    			startService(intent);
                break;
        }
   }
}
