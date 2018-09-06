package com.pillow.ui;

import com.pillow.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

public class LaunchActivity extends Activity {

	@Override  
	  protected void onCreate(Bundle savedInstanceState) {  
	     super.onCreate(savedInstanceState);  
	       setContentView(R.layout.launch);  
	       requestWindowFeature(Window.FEATURE_NO_TITLE);
	       Handler x = new Handler();//定义一个handle对象  
	          
	        x.postDelayed(new splashhandler(), 3000);//设置3秒钟延迟执行splashhandler线程。其实你这里可以再新建一个线程去执行初始化工作，如判断SD,网络状态等  
	    }  
	 
	    class splashhandler implements Runnable{  
	       public void run() {  
	            startActivity(new Intent(getApplication(),MainActivity.class));// 这个线程的作用3秒后就是进入到你的主界面  
	           LaunchActivity.this.finish();// 把当前的LaunchActivity结束掉  
	       }  
	   }  
	
	
}
