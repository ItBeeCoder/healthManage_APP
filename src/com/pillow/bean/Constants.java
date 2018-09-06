/**
 *日期：2017年8月30日下午4:41:39
pillow
TODO
author：shaozq
 */
package com.pillow.bean;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;

/**
 * @author shaozq
 *2017年8月30日下午4:41:39
 */
public class Constants {

	//保存参数文件夹名称，具体参数为：用户账号，密码
		public static final String SHARED_PREFERENCE_NAME = "logininfo";
	    public static 	List<NameValuePair> params = null;//该变量保存APP客户端需要传递给服务器各种数据
		public static String result = "";//该变量保存服务器返回的处理结果
		public static String requestUrl = "";//该变量保存向服务器发送请求的url路径地址
		//这里是局域网服务器的地址，具体为：IP地址：/端口号/服务器项目名称
		public static final String SERVER_HOST = "http://192.168.43.93:8080/healthCare1126";
		public static final boolean DEBUG = true; 
}
