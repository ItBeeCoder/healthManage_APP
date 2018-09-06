/**
 *日期：2017年12月7日下午9:29:53
pillow
TODO
author：shaozq
 */
package com.pillow.notification;

import java.net.URI;

import org.java_websocket.drafts.Draft_10;

import android.app.Application;
import android.content.Context;
import android.util.Log;

/**
 * @author shaozq
 *2017年12月7日下午9:29:53
 */
public class MyApplication extends Application {

	 private static NotificationClient webSocketClient;
	    private static Context context;
	    @Override
	    public void onCreate() {
	        super.onCreate();
	        try {
	        	 System.out.println( "开始执行new NotificationClient()");
	            webSocketClient= new NotificationClient( new URI( "ws://192.168.43.93:8080" ), new Draft_10() ); // more about drafts here: http://github.com/TooTallNate/Java-WebSocket/wiki/Drafts
	            System.out.println( "开始执行webSocketClient.connectBlocking()");
	            webSocketClient.connectBlocking();
	            System.out.println( "开始执行 webSocketClient.send()");
	            webSocketClient.send("_user_login_xzzzqscy");
	        }catch (Exception e){
	            Log.e("webSecket", "WebSecket连接异常" + e.getMessage());
	        }
	        context=getApplicationContext();
	    }

	    public NotificationClient getWebSocketClient() {
	        return webSocketClient;
	    }

	    public static Context getContext(){
	        return context;
	    }
}


