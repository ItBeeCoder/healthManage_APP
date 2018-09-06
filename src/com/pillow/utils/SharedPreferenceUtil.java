/**
 *日期：2017年8月30日下午4:38:55
pillow
TODO
author：shaozq
 */
package com.pillow.utils;

import android.app.Activity;
import android.content.SharedPreferences;

import com.pillow.bean.Constants;
import com.pillow.entity.Oldman;

/**
 * @author shaozq
 *2017年8月30日下午4:38:55
 */
public class SharedPreferenceUtil {

	/**
	 * 清除登录状态
	 */
	public static void clearLoginInfo(Activity context) {
		SharedPreferences mySharedPreferences = context.getSharedPreferences(
				Constants.SHARED_PREFERENCE_NAME, Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = mySharedPreferences.edit();
		// 用putString的方法保存数据
		editor.putString("loginState", "");
		// 提交当前数据
		editor.commit();
	}

	/**
	 * 保存登录状态
	 */
	public static void saveLoginInfo(Activity context, String loginState) {
		SharedPreferences mySharedPreferences = context.getSharedPreferences(
				Constants.SHARED_PREFERENCE_NAME, Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = mySharedPreferences.edit();
		// 用putString的方法保存数据
		editor.putString("loginState", loginState);
		// 提交当前数据
		editor.commit();
	}

	/**
	 * 功能：获取登录状态
	 */
	public static String getLoginInfo(Activity context) {
		SharedPreferences mySharedPreferences = context.getSharedPreferences(
				Constants.SHARED_PREFERENCE_NAME, Activity.MODE_PRIVATE);
		return mySharedPreferences.getString("loginState", "");
	}

	/**
	 * 清除working3DESKey
	 * 功能：清除登录状态
	 */
	public static void clearUser(Activity context) {
		SharedPreferences mySharedPreferences = context.getSharedPreferences(
				Constants.SHARED_PREFERENCE_NAME, Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = mySharedPreferences.edit();
		// 用putString的方法保存数据
		editor.putString("account", "");
		editor.putString("password", "");
		// 提交当前数据
		editor.commit();
	}

	/**
	 * 保存working3DESKey
	 * 功能：保存登录状态
	 */
	public static void saveUser(Activity context, Oldman user) {
		SharedPreferences mySharedPreferences = context.getSharedPreferences(
				Constants.SHARED_PREFERENCE_NAME, Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = mySharedPreferences.edit();
		// 用putString的方法保存数据
		editor.putString("account", user.getOldmanuseraccount());
		editor.putString("password", user.getPassword());
		// 提交当前数据
		editor.commit();
	}

	/**
	 * 获取working3DESKey
	 * 功能：获取登录状态
	 */
	public static Oldman getUser(Activity context) {
		SharedPreferences mySharedPreferences = context.getSharedPreferences(
				Constants.SHARED_PREFERENCE_NAME, Activity.MODE_PRIVATE);
		Oldman u = new Oldman();
		u.setOldmanuseraccount(mySharedPreferences.getString("account", ""));
		u.setPassword(mySharedPreferences.getString("password", ""));
		return u;
	}
	
}

