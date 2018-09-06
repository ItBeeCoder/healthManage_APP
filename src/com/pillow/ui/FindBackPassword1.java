package com.pillow.ui;

import com.pillow.R;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.FrameLayout.LayoutParams;

//功能：当用户忘记密码时，实现找回用户密码的功能，借助于第三方（Mob）发送短信验证码

public class FindBackPassword1 extends Activity implements OnClickListener {

	private EditText inputPhoneEt;
	private EditText inputCodeEt;
	private Button requestCodeBtn;
	private Button commitBtn;
	private TextView find_password_back;
	//
	int i = 60;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.find_back_password1);
		init();
	}

	private void init() {
		find_password_back = (TextView) findViewById(R.id.find_password_back);
		inputPhoneEt = (EditText) findViewById(R.id.login_input_phone_et);
		inputCodeEt = (EditText) findViewById(R.id.login_input_code_et);
		requestCodeBtn = (Button) findViewById(R.id.login_request_code_btn);
		commitBtn = (Button) findViewById(R.id.login_commit_btn);
		requestCodeBtn.setOnClickListener(this);
		commitBtn.setOnClickListener(this);
		find_password_back.setOnClickListener(this);
		//
		SMSSDK.initSDK(this, "20ad52ee10d7e",
				"e32dda64d3360f3cc06a463a8bdc1ee6");
		EventHandler eventHandler = new EventHandler() {
			@Override
			public void afterEvent(int event, int result, Object data) {
				Message msg = new Message();
				msg.arg1 = event;
				msg.arg2 = result;
				msg.obj = data;
				handler.sendMessage(msg);
			}
		};
		SMSSDK.registerEventHandler(eventHandler);
	}

	@Override
	public void onClick(View v) {
		String phoneNums = inputPhoneEt.getText().toString();
		switch (v.getId()) {
		case R.id.login_request_code_btn:
			// 1.
			if (!judgePhoneNums(phoneNums)) {
				return;
			} // ֤
			SMSSDK.getVerificationCode("86", phoneNums);

			// 3.
			requestCodeBtn.setClickable(false);
			requestCodeBtn.setText("重新发送(" + i + ")");
			new Thread(new Runnable() {
				@Override
				public void run() {
					for (; i > 0; i--) {
						handler.sendEmptyMessage(-9);
						if (i <= 0) {
							break;
						}
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					handler.sendEmptyMessage(-8);
				}
			}).start();
			break;
		case R.id.login_commit_btn:
			SMSSDK.submitVerificationCode("86", phoneNums, inputCodeEt
					.getText().toString());
			createProgressBar();
			break;
		case R.id.find_password_back:
			FindBackPassword1.this.finish();
			break;
		}
	}

	/**
		 * 
		 */
	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == -9) {
				requestCodeBtn.setText("重新发送(" + i + ")");
			} else if (msg.what == -8) {
				requestCodeBtn.setText("获取验证码");
				requestCodeBtn.setClickable(true);
				i = 60;
			} else {
				Log.e("执行else msg.what == ", "msg.what =====" + msg.what);
				int event = msg.arg1;
				int result = msg.arg2;
				Object data = msg.obj;
				
				if (result == SMSSDK.RESULT_COMPLETE) {
					// 短信注册成功后，返回MainActivity,然后提示新好友
					if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {// 提交验证码成功
						Toast.makeText(getApplicationContext(), "提交验证码成功",
								Toast.LENGTH_SHORT).show();
						Intent intent = new Intent(FindBackPassword1.this,
								FindBackPassword2.class);
						startActivity(intent);
					} else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
						Toast.makeText(getApplicationContext(), "验证码已经发送",
								Toast.LENGTH_SHORT).show();
					} else {
						((Throwable) data).printStackTrace();
					}
				}
			}
		}
	};

	/**
	 * 
	 * 功能：判断格式电话号码是否正确
	 * @param phoneNums
	 */
	private boolean judgePhoneNums(String phoneNums) {
		if (isMatchLength(phoneNums, 11) && isMobileNO(phoneNums)) {
			return true;
		}
		Toast.makeText(this, "电话号码格式不正确", Toast.LENGTH_SHORT).show();
		return false;
	}

	/**
	 * 
	 * @param str
	 * @param length
	 * @return
	 */
	@SuppressLint("NewApi")
	public static boolean isMatchLength(String str, int length) {
		if (str.isEmpty()) {
			return false;
		} else {
			return str.length() == length ? true : false;
		}
	}

	public static boolean isMobileNO(String mobileNums) {
		String telRegex = "[1][3578]\\d{9}";//
		if (TextUtils.isEmpty(mobileNums))
			return false;
		else
			return mobileNums.matches(telRegex);
	}

	/**
	 * progressbar
	 */
	private void createProgressBar() {
		FrameLayout layout = (FrameLayout) findViewById(android.R.id.content);
		FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		layoutParams.gravity = Gravity.CENTER;
		ProgressBar mProBar = new ProgressBar(this);
		mProBar.setLayoutParams(layoutParams);
		mProBar.setVisibility(View.VISIBLE);
		layout.addView(mProBar);
	}

	@Override
	protected void onDestroy() {
		SMSSDK.unregisterAllEventHandler();
		super.onDestroy();
	}

}
