package com.pillow.ui;

import com.pillow.R;
import com.pillow.application.ExitAllActivity;
import com.pillow.layout.CustomScrollView;
import com.pillow.utils.ToastUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SettingPageActivity extends Activity implements OnClickListener {

	private ImageView mBackgroundImageView = null;
	private CustomScrollView mScrollView = null;
	private Intent mIntent = null;
	private LinearLayout login,integrationview,childrelateoldman,oldmanConfirmRelate;
	private RelativeLayout exitCurrentPage;
	private TextView username,current_login_usertype,userPersonalInfoPageBack;

	private String OldmanAccount="";
	String passuseraccount="",usertype="";
	Bundle bundle = new Bundle();
	private SharedPreferences sp;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.setting_page);
		Intent intent=getIntent();
		passuseraccount=intent.getStringExtra("useraccount");

		ExitAllActivity.getActivityInstance().addActivity(this);
		
		findViewById();
		initView();
		sp = this.getSharedPreferences("userInfo", Context.MODE_WORLD_READABLE);
		
	}

	protected void findViewById() {
		userPersonalInfoPageBack = (TextView) findViewById(R.id.set_page_back);
		mBackgroundImageView = (ImageView) findViewById(R.id.personal_background_image1);
		exitCurrentPage = (RelativeLayout) findViewById(R.id.exit_current_page);
		
	}

	protected void initView() {
		exitCurrentPage.setOnClickListener(this);
		userPersonalInfoPageBack.setOnClickListener(this); 
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.exit_current_page:
			exitCurrentSystem();
			break;
		case R.id.set_page_back:
			SettingPageActivity.this.finish();
			break;
		default:
			break;
		}

	}

	private void exitCurrentSystem(){
		new AlertDialog.Builder(this)   
        .setTitle("提示....")  
        .setMessage("你确定退出本系统?")  
        .setPositiveButton("确认", new DialogInterface.OnClickListener() {  

            public void onClick(DialogInterface dialog, int which) {
            	
    			if(sp.getBoolean("ISCHECK", false)){
    				sp.edit().putBoolean("ISCHECK", false).commit();		
    	    	  if(sp.getBoolean("AUTO_ISCHECK", false)) {
    	    		  sp.edit().putBoolean("AUTO_ISCHECK", false).commit();
    	       	  }
    	        }
            	Intent intent= new Intent();
    			intent.setClass(SettingPageActivity.this, login.class);
    			startActivity(intent);
    			ExitAllActivity ea=ExitAllActivity.getActivityInstance();
    			ea.exit();
           }  
       })  
       .setNegativeButton("取消", new DialogInterface.OnClickListener() { 
            public void onClick(DialogInterface dialog, int which) {  
               dialog.dismiss();
            }
    }).show();  

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

