package com.pillow.notification;

import com.pillow.R;
import com.pillow.ui.UserInfoActivity;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

public class NotificationActivity extends Activity {

	String passuseraccount;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_notification);
		Intent intent=getIntent();
		passuseraccount=intent.getStringExtra("useraccount");
	}

	public void open(View view) {
		Intent intent = new Intent(this, PushSmsService.class);
		startService(intent);
	}

	public void close(View view) {
		Intent intent = new Intent(this, PushSmsService.class);
		stopService(intent);
	}
}
