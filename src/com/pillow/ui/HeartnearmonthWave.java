/**
 *日期：2018年1月27日下午1:25:17
pillow
TODO
author：shaozq
 */
package com.pillow.ui;

import com.pillow.R;
import com.pillow.bean.RequestToServer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/**
 * @author shaozq
 *2018年1月27日下午1:25:17
 */
public class HeartnearmonthWave extends Activity {

	private TextView heartPageBack,maxheartvalue,minheartvalue,avgheartvalue;
	private Button look_max_heart,look_min_heart,look_avg_heart;
	private Button get_heart_data;
 
    private String OldmanAccount="";
    RequestToServer reqtoserver=new RequestToServer();
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.heart_nearmonth_choice);
		
		findViewById();
//		获取从上一个界面传递过来的用户账号信息
		OldmanAccount = (String)getIntent().getStringExtra("oldmanAccount");
//		ClickListener为定义的事件监听器
		ClickListener cl = new ClickListener();
		heartPageBack.setOnClickListener(cl); 
		look_max_heart.setOnClickListener(cl); 
		look_min_heart.setOnClickListener(cl); 
		look_avg_heart.setOnClickListener(cl); 
	}
//	初始化各UI控件
	private void findViewById(){
		heartPageBack  = (TextView) findViewById(R.id.heart_page_back);
		look_max_heart =	(Button)findViewById(R.id.look_max_heart);
		look_min_heart =	(Button)findViewById(R.id.look_min_heart);
		look_avg_heart =	(Button)findViewById(R.id.look_avg_heart);
        
	}	
	//定义的事件触发器，当点击各控件时，分别调用相应的事件处理函数
	private final class ClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.look_max_heart:
				GotoHeartMaxchart();
				break;
			case R.id.look_min_heart:
				GotoHeartMinchart();
				break;
			case R.id.look_avg_heart:
				GotoHeartAvgchart();
				break;
			case R.id.heart_page_back:
				HeartnearmonthWave.this.finish();
				break;
			default:
				break;
			}
		}
	}
		private void GotoHeartMaxchart(){
			Intent intent= new Intent();
			intent.putExtra("oldmanAccount",OldmanAccount);
			intent.setClass(getApplicationContext(),HeartNearmonthMaxWave.class);
			startActivity(intent);
		}
		//此处需要改
		private void GotoHeartMinchart(){
			Intent intent= new Intent();
			intent.putExtra("oldmanAccount",OldmanAccount);
			intent.setClass(getApplicationContext(),HeartNearmonthMinWave.class);
			startActivity(intent);
		}
		//此处需要改
		private void GotoHeartAvgchart(){
			Intent intent= new Intent();
			intent.putExtra("oldmanAccount",OldmanAccount);
			intent.setClass(getApplicationContext(),HeartNearmonthAvgWave.class);
			startActivity(intent);
		}
}
