/**
 *日期：2018年1月28日下午1:09:50
pillow
TODO
author：shaozq
 */
package com.pillow.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.pillow.R;
import com.pillow.bean.Constants;
import com.pillow.bean.RequestToServer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author shaozq
 *2018年1月28日下午1:09:50
 */
public class ChildPersonalInfo extends Activity {

	private TextView userPersonalInfoPageBack,userPersonalInfoPageEdit;
	private EditText oldman_username,oldman_gender,oldman_age,oldman_home_address;
	private EditText  oldman_telephone,oldman_height,oldman_weight;
	
	private	RadioGroup sex;
	private RadioButton male,female;


    private EditText et_detailAddress,oldman_useraccount;//输入详细地址

    private String address;//用来接收intent的参数
    private Boolean isFirstLoad = true;//判断是不是最近进入对话框
    private Boolean ifSetFirstAddress = true;//判断是否已经设置了，初始的详细地址
    private String username="",usersex="",homeaddress="",telephoneString="",age="",height="",weight="";
//    private int ;
//    private double 
    //省市区的集合
    private Map<String, String[]> cityMap = new HashMap<String, String[]>();//key:省p---value:市n  value是一个集合
    private Map<String, String[]> areaMap = new HashMap<String, String[]>();//key:市n---value:区s    区也是一个集合
    String passuseraccount;
    private String OldmanAccount="",usertype = "";
    RequestToServer reqtoserver=new RequestToServer();
    //该页面初始化的时候需要从服务器端获取信息
@Override
protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	requestWindowFeature(Window.FEATURE_NO_TITLE);
	setContentView(R.layout.child_pserson_info);
	Intent intent=getIntent();
	passuseraccount=intent.getStringExtra("useraccount");
	usertype = (String)getIntent().getStringExtra("usertype");
//	初始化各UI控件
	fingViewById();
	
	getData();
	//获取上一个页面传递过来的用户的账号
	OldmanAccount = intent.getStringExtra("oldmanAccount");
	oldman_useraccount.setText(" "+passuseraccount);
	//定义的事件触发器
	ClickListener cl = new ClickListener();		
	userPersonalInfoPageBack.setOnClickListener(cl); 
	userPersonalInfoPageEdit.setOnClickListener(cl);

	
}
//初始化各UI控件
private void fingViewById(){
userPersonalInfoPageBack = (TextView) findViewById(R.id.userPersonal_info_page_back);
userPersonalInfoPageEdit = (TextView) findViewById(R.id.userPersonal_info_page_edit);
oldman_username=(EditText) findViewById(R.id.oldman_username);
 
 oldman_useraccount = (EditText) findViewById(R.id.oldman_useraccount);

 oldman_telephone=(EditText) findViewById(R.id.oldman_telephone);

 oldman_home_address = (EditText) findViewById(R.id.oldman_home_address);
}
 
//定义的事件触发器
private final class ClickListener implements OnClickListener{
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
	case R.id.userPersonal_info_page_back:
		returnToPriview();
		ChildPersonalInfo.this.finish();
		break;
	case R.id.userPersonal_info_page_edit:
		returnToNext();
		ChildPersonalInfo.this.finish();
		break;
		default:
			break;
		}
	}
}
	private void returnToPriview(){
		Intent intent = new Intent();
		intent.putExtra("usertype",usertype);
		setResult(RESULT_OK,intent);
	}
	//这里需要改
	private void returnToNext(){
		Intent intent = new Intent();
		intent.putExtra("usertype",usertype);
		intent.putExtra("useraccount", passuseraccount);
		//intent.putExtra("oldmanAccount", OldmanAccount);
		intent.setClass(getApplicationContext(),ChildPersonalInfoEdit.class);
		startActivity(intent);	
	}
//	点击返回按钮时对应的事件，结束当前活动
	public void onBackPressed(){
		returnToPriview();
		ChildPersonalInfo.this.finish();
	}
	
	//界面初始化函数
	private void getData(){
		Constants.params = new ArrayList<NameValuePair>();
			Constants.params.add(new BasicNameValuePair("childuseraccount",passuseraccount));
			Constants.requestUrl = Constants.SERVER_HOST+"/getChPersInf.action";	///
			new Thread(getJson).start();//新建一个线程
	}
	
//	定义一个Runnable，其主要操作是调用向服务器发送HTTP请求的函数
	private Runnable getJson = new Runnable(){
		public void run() {
			// TODO Auto-generated method stub
			try{
				Constants.result= reqtoserver.GetResponseFromServer(Constants.requestUrl,Constants.params);
				handler.sendEmptyMessage(0x00);
			}
			catch(Exception e){
				handler.sendEmptyMessage(0x01);
			}
		}
	};

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 0x00) {
				if(null != Constants.result){
					try {//处理从服务器端返回的数据
						JSONObject json= new JSONObject(Constants.result); 
						String result = (String)json.get("result");	
						String initmsg = (String)json.get("msg");
						if(result.equalsIgnoreCase("1")){
							String[] strsArray = initmsg.split("=");
                            System.out.println("strsArray .length=="+strsArray .length+" strsArray=="+strsArray[0]+"  initmsg=="+initmsg);
                            username=strsArray[0];
                           
                    		oldman_username.setText(username);
                    		 oldman_home_address.setText(homeaddress);
                    		 oldman_telephone.setText(telephoneString);
                    		
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}  
				}		
			} else if (msg.what == 0x01) {
				Toast.makeText(getApplicationContext(), "获取失败",Toast.LENGTH_LONG).show();
			}
		}
	};
	
}
