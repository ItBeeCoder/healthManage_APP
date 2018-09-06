/**
 *日期：2017年12月7日下午9:33:46
pillow
TODO
author：shaozq
 */
package com.pillow.notification;

import com.pillow.R;

import android.app.Activity;
import android.os.Bundle;

/**
 * @author shaozq
 *2017年12月7日下午9:33:46
 */
public class SecondNotificationActivity extends Activity {

	 private MyApplication app;
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.content_main1);
	        app = (MyApplication) getApplication(); // 获得MyApplication对象
	    }

	    public void onDestroy(){
	        super.onDestroy();
	        this.finish();
	    }
	
}
