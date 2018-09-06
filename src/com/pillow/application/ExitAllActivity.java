/**
 * author:shaozq
 * 2017-9-5上午10:14:20
 * ExitAllActivity
 * TODO
 */
package com.pillow.application;

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.Application;

/**
 * author:shaozq
 * 2017-9-5上午10:14:20
 * ExitAllActivity
 * 作用：对应用中的所有活动进行管理，创建所有活动实例，退出系统时关闭所有所动等
 * TODO
 */
public class ExitAllActivity extends Application {

	private List<Activity> activityList = new LinkedList<Activity>();
	private static ExitAllActivity instance;
	
	/**
	 * author:shaozq
	 * 2017-9-5上午10:14:20
	 * TODO
	 */

	public ExitAllActivity() {
		// TODO Auto-generated constructor stub
	}
	
	public static ExitAllActivity getActivityInstance() {
		if (null == instance) {
		instance = new ExitAllActivity();
		}
		return instance;
		}

	public void addActivity(Activity activity) {
		activityList.add(activity);
		}

//   Activity  finish
		public void exit() {

		for (Activity activity : activityList) {
		activity.finish();
		}
		System.exit(0);

		}

		@Override
		public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		}

	
	
}
