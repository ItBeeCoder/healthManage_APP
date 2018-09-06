/**
 *日期：2017年12月7日下午9:25:05
pillow
TODO
author：shaozq
 */
package com.pillow.notification;

import java.net.URI;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ServerHandshake;

import com.pillow.R;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

/**
 * @author shaozq
 *2017年12月7日下午9:25:05
 */
public class NotificationClient extends WebSocketClient {

	
	public NotificationClient(URI serverUri, Draft draft) {
		super(serverUri, draft);
		// TODO Auto-generated constructor stub
	}
	
	public NotificationClient(URI serverURI) {
		super(serverURI);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onClose( int code, String reason, boolean remote) {
		// TODO Auto-generated method stub
		// The codecodes are documented in class org.java_websocket.framing.CloseFrame
				System.out.println( "Connection closed by " + ( remote ? "remote peer" : "us" ) );
	}

	@Override
	public void onError(Exception ex) {
		// TODO Auto-generated method stub
		ex.printStackTrace();
		// if the error is fatal then onClose will be called additionally
	}

	@Override
	public void onMessage(String message) {
		// TODO Auto-generated method stub
		System.out.println( "received: " + message );

		showNotifictionIcon(MyApplication.getContext(),message);
	}

	@Override
	public void onOpen(ServerHandshake arg0) {
		// TODO Auto-generated method stub
		System.out.println("opened connection");
		// if you plan to refuse connection based on ip or httpfields overload: onWebsocketHandshakeReceivedAsClient
	}

	public static void showNotifictionIcon(Context context,String msg) {
		NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
		Intent intent = new Intent(context, SecondNotificationActivity.class);//将要跳转的界面
		builder.setAutoCancel(true);//点击后消失
		builder.setSmallIcon(R.drawable.ic_launcher);//设置通知栏消息标题的头像
		builder.setDefaults(NotificationCompat.PRIORITY_HIGH);//设置通知铃声
		builder.setTicker("你好");
		builder.setContentText(msg);//通知内容
		builder.setContentTitle("来自服务端的提醒");
		//利用PendingIntent来包装我们的intent对象,使其延迟跳转
		PendingIntent intentPend = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
		builder.setContentIntent(intentPend);
		NotificationManager manager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
		manager.notify(0, builder.build());
	}

//	private void notification(String username,String gender,String address,String telephone,int age) {
	
//		NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//		Notification notification = new Notification(R.drawable.ic_menu_compose, "异常报警消息",
//				System.currentTimeMillis());
//		notification.defaults = Notification.DEFAULT_ALL; // 
//		notification.flags = Notification.FLAG_AUTO_CANCEL; // 
//		notification.flags |= Notification.FLAG_NO_CLEAR;// 
	
//		Intent intent = new Intent(getApplicationContext(),
//				ContentActivity.class);
//		intent.putExtra("username", username);
//		intent.putExtra("gender", gender);
//		intent.putExtra("address", address);
//		intent.putExtra("telephone", telephone);
//		intent.putExtra("age", age);
//		PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
//		notification.setLatestEventInfo(getApplicationContext(), username
//				+ "的异常报警消息", "异常报警消息", pi);
//		manager.notify(0, notification);
//	}
	
}
