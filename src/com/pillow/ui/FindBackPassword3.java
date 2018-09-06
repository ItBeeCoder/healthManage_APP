package com.pillow.ui;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

import com.pillow.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class FindBackPassword3 extends Activity implements OnClickListener {

//找回密码成功界面
	private Button commitBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.find_back_password3);

		init();
	}

	private void init() {
		commitBtn = (Button) findViewById(R.id.submit_relation);
		commitBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.submit_relation:
			returnToLoginPage();
			break;
		}
	}
	
	private void returnToLoginPage(){
		Intent intent = new Intent();
		intent.setClass(getApplicationContext(),login.class);
		startActivity(intent);
	}
	

}
