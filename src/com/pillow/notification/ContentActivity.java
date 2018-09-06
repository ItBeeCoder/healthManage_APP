package com.pillow.notification;


import com.pillow.R;

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
import android.widget.TextView;

public class ContentActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_content);
		Intent intent = getIntent();
		TextView tv_content = (TextView) this.findViewById(R.id.tv_content);
		TextView tv_date = (TextView) this.findViewById(R.id.tv_date);
		TextView  tv_address=(TextView) this.findViewById(R.id.tv_address);
		TextView  tv_telephone=(TextView) this.findViewById(R.id.tv_telephone);
		
		if (intent != null) {
			String username = intent.getStringExtra("username");
			String time = intent.getStringExtra("time");
			String exceptype = intent.getStringExtra("exceptype");
			String homeaddress= intent.getStringExtra("homeaddress");
			
			tv_content.setText("用户姓名: " + username);
			tv_date.setText("异常时间: " + time);
			tv_address.setText("异常类型:"+exceptype);
			tv_telephone.setText("家庭地址:"+homeaddress);
		}
	}
}
