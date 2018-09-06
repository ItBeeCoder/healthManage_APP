/**
 *日期：2017年8月30日下午4:22:30
pillow
TODO
author：shaozq
 */
package com.pillow.application;


import com.pillow.entity.Child;
import com.pillow.entity.Oldman;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;

/**
 * @author shaozq
 *2017年8月30日下午4:22:30
 */
public class AllApplication extends Application {

	private static AllApplication app;
	public static Oldman oldman = new Oldman();
	public static Child child = new Child();
	public static AllApplication getInstance(){		
		return app;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		app = this;
		Context context = this.getApplicationContext();
		try {
			PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			System.out.println("版本代码(int): " + packageInfo.versionCode);
			System.out.println("版本名称(String): " + packageInfo.versionName);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}     
	}
	
}
