/**
 *日期：2017年12月7日下午9:31:44
pillow
TODO
author：shaozq
 */
package com.pillow.notification;

import java.nio.channels.NotYetConnectedException;

import com.pillow.R;

import android.app.Activity;
import android.os.Bundle;

/**
 * @author shaozq
 *2017年12月7日下午9:31:44
 */
public class FirstNotificationActivity extends Activity {

	private MyApplication app;
    private NotificationClient  webSocketClient=null;
    
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);

        app = (MyApplication) getApplication(); // 获得MyApplication对象
        System.out.println( "开始执行app.getWebSocketClient()");
        webSocketClient = app.getWebSocketClient();
        System.out.println( "结束执行app.getWebSocketClient()");
    }

    public void onDestroy(){
        super.onDestroy();

        if(webSocketClient!=null){
//        	sendMsg("_user_logout_xzzzqscy");
            webSocketClient.close();
        }
        this.finish();
      //  System.exit(0);
    }
    
    public void sendMsg(String text) throws NotYetConnectedException {
        //write a message
    	webSocketClient.send(text);
    }
	
}

