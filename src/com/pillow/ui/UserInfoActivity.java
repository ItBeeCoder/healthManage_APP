package com.pillow.ui;


import com.pillow.R;
import com.pillow.bean.RequestToServer;
import com.pillow.layout.CustomScrollView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class UserInfoActivity extends Activity implements OnClickListener {

	private ImageView mBackgroundImageView = null;
	private CustomScrollView mScrollView = null;
	private Intent mIntent = null;
	private LinearLayout afterlogin;
	private LinearLayout login,integrationview,childrelateoldman,oldmanConfirmRelate,lookRelateObject;
	private RelativeLayout personal,orderselect;
	private TextView username,current_login_usertype,userPersonalInfoPageBack;
	private int LOGIN_CODE = 101;
	private String OldmanAccount="";
	String passuseraccount="",usertype="";
	Bundle bundle = new Bundle();
	RequestToServer reqtoserver=new RequestToServer();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.userinfo);
		Intent intent=getIntent();
		passuseraccount=intent.getStringExtra("useraccount");
		usertype = (String)getIntent().getStringExtra("usertype");
		 
		findViewById();
		
		OldmanAccount = (String)getIntent().getStringExtra("oldmanAccount");
		bundle.putString("oldmanAccount", OldmanAccount);
		
		initView();
	}
//初始化各UI控件
	protected void findViewById() {
		userPersonalInfoPageBack = (TextView) findViewById(R.id.userPersonal_info_page_back);
		mBackgroundImageView = (ImageView) findViewById(R.id.personal_background_image);
		
		mScrollView = (CustomScrollView) findViewById(R.id.personal_scrollView);

		afterlogin = (LinearLayout) findViewById(R.id.afterlogin);
		
		login = (LinearLayout) findViewById(R.id.login);
		integrationview = (LinearLayout) findViewById(R.id.add_device);
		lookRelateObject = (LinearLayout) findViewById(R.id.lookRelateObject);
		
		personal = (RelativeLayout) findViewById(R.id.personal);
		orderselect = (RelativeLayout) findViewById(R.id.orderselect);
		childrelateoldman = (LinearLayout) findViewById(R.id.childrelateoldman);
		oldmanConfirmRelate = (LinearLayout) findViewById(R.id.oldmanConfirmRelate);
		username = (TextView) findViewById(R.id.username);
		current_login_usertype = (TextView) findViewById(R.id.userype);
		
		if(usertype.equalsIgnoreCase("老人")||usertype=="老人"){
			childrelateoldman.setVisibility(View.GONE);
		}
		if(usertype.equalsIgnoreCase("子女")||usertype=="子女"){
			oldmanConfirmRelate.setVisibility(View.GONE);
			integrationview.setVisibility(View.GONE);
		}
	}

	protected void initView() {
		username.setText(passuseraccount);
		current_login_usertype.setText("用户类型："+usertype);
		mScrollView.setImageView(mBackgroundImageView);

		orderselect.setOnClickListener(this);
		userPersonalInfoPageBack.setOnClickListener(this); 
		lookRelateObject.setOnClickListener(this); 
		
		if(usertype.equalsIgnoreCase("子女")||usertype=="子女"){
			childrelateoldman.setOnClickListener(this);
		}
		if(usertype.equalsIgnoreCase("老人")||usertype=="老人"){
			oldmanConfirmRelate.setOnClickListener(this);
			integrationview.setOnClickListener(this);
		}
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.orderselect:
			Intent intent= new Intent();
			intent.putExtra("useraccount", passuseraccount);
			intent.putExtra("oldmanAccount", OldmanAccount);
//			intent.putExtras(bundle);//存储的是老人的账号
			intent.putExtra("usertype",usertype);
			if(usertype.equalsIgnoreCase("老人")||usertype=="老人"){
			intent.setClass(UserInfoActivity.this, UserPersonalInfo.class);
			}else{
				intent.setClass(UserInfoActivity.this, ChildPersonalInfo.class);
			}
			startActivityForResult(intent,98);
			break;
		case R.id.add_device:
			Intent intent1= new Intent();
			intent1.putExtra("useraccount", passuseraccount);
			intent1.putExtra("usertype",usertype);
			intent1.setClass(UserInfoActivity.this, AddDevice.class);
			startActivityForResult(intent1,99);
			break;
			
		case R.id.childrelateoldman:
			mIntent = new Intent(UserInfoActivity.this, ChildrenRelateOldman.class);
			mIntent.putExtra("childAccount",passuseraccount);
			mIntent.putExtra("usertype",usertype);
			startActivityForResult(mIntent, 100);
			break;
		
		case R.id.oldmanConfirmRelate:
			mIntent = new Intent(UserInfoActivity.this, OldmanConfirmRelate.class);
			mIntent.putExtra("oldmanAccount",passuseraccount); 
			startActivityForResult(mIntent, LOGIN_CODE);
			break;
		case R.id.lookRelateObject:
			mIntent = new Intent(UserInfoActivity.this,LookRelateObject.class);
			mIntent.putExtra("usertype",usertype);
			mIntent.putExtra("oldmanAccount",passuseraccount); 
			mIntent.putExtra("childAccount", passuseraccount);
			startActivityForResult(mIntent, LOGIN_CODE);
			break;
		
		case R.id.userPersonal_info_page_back:
			UserInfoActivity.this.finish();
			break;
		default:
			break;
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch(requestCode){
		case 98:
			if(resultCode == RESULT_OK){
				//usertype = data.getStringExtra("usertype");
			}
			break;
		case 99:
			if(resultCode == RESULT_OK){
				//usertype = data.getStringExtra("usertype");
			}
			break;
		case 100:
			if(resultCode == RESULT_OK){
				usertype = data.getStringExtra("usertype");
			}
			break;
		}
	}
}
