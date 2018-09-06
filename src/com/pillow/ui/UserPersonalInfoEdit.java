/**
 *日期：2018年1月26日下午2:10:18
pillow
TODO
author：shaozq
 */
package com.pillow.ui;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author shaozq
 *2018年1月26日下午2:10:18
 */
public class UserPersonalInfoEdit extends Activity {

	private TextView userPersonalInfoPageBack;
	private EditText oldman_account,oldman_gender,oldman_age,oldman_home_address;
	private EditText  oldman_telephone,oldman_height,oldman_weight;
	private Button submit_persinalInfo;
	private	RadioGroup sex;
	private RadioButton male,female;
	private String usersex="";
	private Spinner spinner_sheng;//省
    private Spinner spinner_shi;//市
    private Spinner spinner_qu;//区

    private EditText et_detailAddress,oldman_useraccount;//输入详细地址

    //全局的jsonObject
    private JSONObject jsonObject;//把全国的省市区的信息以json的格式保存，解析完成后赋值为null
    private String[] allProv;//所有的省

    private ArrayAdapter<String> provinceAdapter;//省份数据适配器
    private ArrayAdapter<String> cityAdapter;//城市数据适配器
    private ArrayAdapter<String> areaAdapter;//区县数据适配器

    private String[] allSpinList;//在spinner中选出来的地址，后面需要用空格隔开省市区
    
    private String address;//用来接收intent的参数
    private String allAddress;//用来接收intent参数
    private String provinceName;//省的名字
    private String areaName;//区的名字
    private Boolean isFirstLoad = true;//判断是不是最近进入对话框
    private Boolean ifSetFirstAddress = true;//判断是否已经设置了，初始的详细地址
    private String username="",usex="",homeaddress="",telephoneString="",age="",height="",weight="";
//	   
    //省市区的集合
    private Map<String, String[]> cityMap = new HashMap<String, String[]>();//key:省p---value:市n  value是一个集合
    private Map<String, String[]> areaMap = new HashMap<String, String[]>();//key:市n---value:区s    区也是一个集合
    String passuseraccount;
    private String OldmanAccount="",usertype = "";
    RequestToServer reqtoserver=new RequestToServer();
    
@Override
protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	requestWindowFeature(Window.FEATURE_NO_TITLE);
	setContentView(R.layout.user_pserson_info_edit);
	Intent intent=getIntent();
	passuseraccount=intent.getStringExtra("useraccount");
	usertype = (String)getIntent().getStringExtra("usertype");
	
//	初始化各UI控件
	fingViewById();
	initJsonData();
	initDatas();
	setSpinnerData();
	
	getData();
	//获取上一个页面传递过来的用户的账号
	OldmanAccount = intent.getStringExtra("oldmanAccount");
	oldman_useraccount.setText(" "+OldmanAccount);
	//定义的事件触发器
	ClickListener cl = new ClickListener();		
	submit_persinalInfo.setOnClickListener(cl);
	userPersonalInfoPageBack.setOnClickListener(cl); 
//	点击性别单选框时对应的触发事件，获取用户性别
	sex.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			// TODO Auto-generated method stub
			if(checkedId == male.getId()){
				usersex = male.getText().toString();
			}else if(checkedId == female.getId()){
				usersex = female.getText().toString();
			}
		}
	});
}
//初始化各UI控件
private void fingViewById(){
userPersonalInfoPageBack = (TextView) findViewById(R.id.userPersonal_info_page_back);
 oldman_account=(EditText) findViewById(R.id.oldman_username);
 sex=	(RadioGroup) findViewById(R.id.sex);
 male=	(RadioButton) findViewById(R.id.male);
 female=	(RadioButton) findViewById(R.id.female);
 oldman_useraccount = (EditText) findViewById(R.id.oldman_useraccount);
 oldman_age=(EditText) findViewById(R.id.oldman_age);
 oldman_telephone=(EditText) findViewById(R.id.oldman_telephone);
 oldman_height=(EditText) findViewById(R.id.oldman_height);
 oldman_weight=(EditText) findViewById(R.id.oldman_weight);
 submit_persinalInfo=(Button) findViewById(R.id.submit_persinalInfo);
 spinner_sheng = (Spinner) findViewById(R.id.spinner_sheng);
 spinner_shi = (Spinner) findViewById(R.id.spinner_shi);
 spinner_qu = (Spinner) findViewById(R.id.spinner_qu);
}
 /**
 * 从assert文件夹中获取json数据
 */
private void initJsonData() {
    try {
        StringBuffer sb = new StringBuffer();
        InputStream is = getAssets().open("city.json");//打开json数据
        byte[] by = new byte[is.available()];//转字节
        int len = -1;
        while ((len = is.read(by)) != -1) {
            sb.append(new String(by, 0, len, "gb2312"));//根据字节长度设置编码
        }
        is.close();//关闭流
        jsonObject = new JSONObject(sb.toString());//为json赋值
    } catch (Exception e) {
        e.printStackTrace();
    }
}

//初始化省市区数据
private void initDatas() {
    try {
        JSONArray jsonArray = jsonObject.getJSONArray("citylist");//获取整个json数据
        allProv = new String[jsonArray.length()];//封装数据
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonP = jsonArray.getJSONObject(i);//jsonArray转jsonObject
            String provStr = jsonP.getString("p");//获取所有的省
            allProv[i] = provStr;//封装所有的省
            JSONArray jsonCity = null;

            try {
                jsonCity = jsonP.getJSONArray("c");//在所有的省中取出所有的市，转jsonArray
            } catch (Exception e) {
                continue;
            }
            //所有的市
            String[] allCity = new String[jsonCity.length()];//所有市的长度
            for (int c = 0; c < jsonCity.length(); c++) {
                JSONObject jsonCy = jsonCity.getJSONObject(c);//转jsonObject
                String cityStr = jsonCy.getString("n");//取出所有的市
                allCity[c] = cityStr;//封装市集合

                JSONArray jsonArea = null;
                try {
                    jsonArea = jsonCy.getJSONArray("a");//在从所有的市里面取出所有的区,转jsonArray
                } catch (Exception e) {
                    continue;
                }
                String[] allArea = new String[jsonArea.length()];//所有的区
                for (int a = 0; a < jsonArea.length(); a++) {
                    JSONObject jsonAa = jsonArea.getJSONObject(a);
                    String areaStr = jsonAa.getString("s");//获取所有的区
                    allArea[a] = areaStr;//封装起来
                }
                areaMap.put(cityStr, allArea);//某个市取出所有的区集合
            }
            cityMap.put(provStr, allCity);//某个省取出所有的市,
        }
    } catch (JSONException e) {
        e.printStackTrace();
    }
    jsonObject = null;//清空所有的数据
}

//根据当前的市，更新区的信息
private void updateArea(Object object, Object myArea) {
    boolean isArea = false;//判断第三个地址是地区还是详细地址
    int selectPosition = 0;//当有数据时，进行匹配地区，默认选中
    String[] area = areaMap.get(object);
    areaAdapter.clear();//清空
    if (area != null) {
        for (int i = 0; i < area.length; i++) {
            if (myArea != null && myArea.toString().equals(area[i])) {//去集合中匹配
                selectPosition = i;
                isArea = true;//地区
            }
            areaAdapter.add(area[i]);//填入到这个列表
        }
        areaAdapter.notifyDataSetChanged();//刷新
        spinner_qu.setSelection(selectPosition);//默认选中
    }
    //第三个地址是详细地址，并且是第一次设置edtext值，正好，地址的长度为3的时候，设置详细地址
    if (!isArea && ifSetFirstAddress && address != null && !address.equals("") && allAddress != null && !allAddress.equals("") && allSpinList.length == 3) {
        et_detailAddress.setText(allSpinList[2]);
        ifSetFirstAddress = false;
    }
}

/**
 * 根据当前的省，更新市和区的信息
 */
private void updateCityAndArea(Object object, Object city, Object area) {
    int selectPosition = 0;//有数据时，进行匹配城市，默认选中
    String[] cities = cityMap.get(object);
    cityAdapter.clear();//清空adapter的数据
    for (int i = 0; i < cities.length; i++) {
        if (city != null && city.toString().equals(cities[i])) {//判断传入的市在集合中匹配
            selectPosition = i;
        }
        cityAdapter.add(cities[i]);//将这个列表“市”添加到adapter中
    }
    cityAdapter.notifyDataSetChanged();//刷新
    if (city == null) {
        updateArea(cities[0], null);//更新区,没有市则默认第一个给它
    } else {
        spinner_shi.setSelection(selectPosition);
        updateArea(city, area);//穿入的区去集合中匹配
    }
}

/**
 * spinner设置值（默认设置值）
 */
private void setSpinnerData() {
    int selectPosition = 0;//有数据传入时
    address = getIntent().getStringExtra("address");
    allAddress = getIntent().getStringExtra("allAddress");
    if (address != null && !address.equals("") && allAddress != null && !allAddress.equals("")) {
        allSpinList = address.split(" ");//用空格隔开allSpinList地址
    }
    /**
     * 设置省市区的适配器，进行动态设置
     */
    provinceAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);//系统默认的
    for (int i = 0; i < allProv.length; i++) {
        //给spinner省赋值,设置默认值
        if (address != null && !address.equals("") && allAddress != null && !allAddress.equals("")&& allSpinList.length > 0 && allSpinList[0].equals(allProv[i])) {
            selectPosition = i;
        }
        provinceAdapter.add(allProv[i]);//添加每一个省
    }
    provinceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);//按下的效果
    spinner_sheng.setAdapter(provinceAdapter);
    spinner_sheng.setSelection(selectPosition);//设置选中的省，默认
    //市
    cityAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
    cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    spinner_shi.setAdapter(cityAdapter);
    //区县
    areaAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
    areaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    spinner_qu.setAdapter(areaAdapter);
    setListener();//设置spinner的点击监听
}

//界面初始化函数
		private void getData(){
			Constants.params = new ArrayList<NameValuePair>();
			Constants.params.add(new BasicNameValuePair("useraccount",passuseraccount));
			Constants.requestUrl = Constants.SERVER_HOST+"/getUserPersonalInfo.action";	///
			new Thread(getJson1).start();//新建一个线程
		}
		
//		定义一个Runnable，其主要操作是调用向服务器发送HTTP请求的函数
		private Runnable getJson1 = new Runnable(){
			public void run() {
				// TODO Auto-generated method stub
				try{
					Constants.result= reqtoserver.GetResponseFromServer(Constants.requestUrl,Constants.params);
					handler.sendEmptyMessage(0x02);
				}
				catch(Exception e){
					handler.sendEmptyMessage(0x03);
				}
			}
		};

//设置spinner的点击监听
private void setListener() {
    //省
    spinner_sheng.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            provinceName = parent.getSelectedItem() + "";//获取点击列表spinner item的省名字
            if (isFirstLoad) {
                // 判断是否省市区都存在
                if (address != null && !address.equals("") && allAddress != null && !allAddress.equals("")&& allSpinList.length > 1 && allSpinList.length < 3) {
                    updateCityAndArea(provinceName, allSpinList[1], null);//更新市和区
                } else if (address != null && !address.equals("") && allAddress != null && !allAddress.equals("") && allSpinList.length >= 3) {
                    //存在省市区
                    //去更新
                    updateCityAndArea(provinceName, allSpinList[1], allSpinList[2]);
                } else {
                    updateCityAndArea(provinceName, null, null);
                }
            } else {
                updateCityAndArea(provinceName, null, null);
            }
        }
        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    });
    //市
    spinner_shi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (!isFirstLoad) {
                //更新区
                updateArea(parent.getSelectedItem() + "", null);
            } else {
                if (address != null && !address.equals("") && allAddress != null && !allAddress.equals("") && allSpinList.length == 4) {
                    et_detailAddress.setText(allSpinList[3]);//没有进入区的对话框，
                }
            }
            isFirstLoad = false;
        }
        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    });

    //区
    spinner_qu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            //直接获取区的名字
            areaName = parent.getSelectedItem() + "";
        }
        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    });

}

//定义的事件触发器
private final class ClickListener implements OnClickListener{
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
	case R.id.submit_persinalInfo:
//			addUserPersonalInfo();
			updateUserPersonInfo();
			break;
	case R.id.userPersonal_info_page_back:
		returnToPriview();
		UserPersonalInfoEdit.this.finish();
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
//	点击返回按钮时对应的事件，结束当前活动
	public void onBackPressed(){
		returnToPriview();
		UserPersonalInfoEdit.this.finish();
	}
/*
 * 功能：获取文本输入框中的用户信息，将输入的数据以健值对的形式保存到params参数中，并传递给服务器
 */
	private void addUserPersonalInfo(){
//	获取文本输入框的内容
		String OldmanUsername=oldman_account.getText().toString();
		String OldmanGender=usersex;
		String OldmanAge=oldman_age.getText().toString();
		String areaname=spinner_qu.getSelectedItem()==null ? "":spinner_qu.getSelectedItem().toString();
		String OldmanAddress=spinner_sheng.getSelectedItem().toString()+spinner_shi.getSelectedItem().toString()+areaname;
		String OldmanTelephone=oldman_telephone.getText().toString();
		String OldmanHeight=oldman_height.getText().toString();
		String OldmanWeight=oldman_weight.getText().toString();
//		把需要传递给服务器的数据添加到params参数中
		Constants.params = new ArrayList<NameValuePair>();
		Constants.params.add(new BasicNameValuePair("useraccount", passuseraccount));
		Constants.params.add(new BasicNameValuePair("username", OldmanUsername));
		Constants.params.add(new BasicNameValuePair("gender", OldmanGender));
		Constants.params.add(new BasicNameValuePair("age", OldmanAge));
		Constants.params.add(new BasicNameValuePair("address", OldmanAddress));
		Constants.params.add(new BasicNameValuePair("telephone", OldmanTelephone));
		Constants.params.add(new BasicNameValuePair("height", OldmanHeight));
		Constants.params.add(new BasicNameValuePair("weight", OldmanWeight));
//		将在APP端填写的用户信息传递给服务器
		Constants.requestUrl = Constants.SERVER_HOST+"/addoldmanInfo1.action";
		new Thread(getJson).start();//建立一个新的线程
	}
	
	private void updateUserPersonInfo(){
//		获取文本输入框的内容
			String OldmanUsername=oldman_account.getText().toString();
			String OldmanGender=usersex;
			String OldmanAge=oldman_age.getText().toString();
			String areaname=spinner_qu.getSelectedItem()==null ? "":spinner_qu.getSelectedItem().toString();
			String OldmanAddress=spinner_sheng.getSelectedItem().toString()+spinner_shi.getSelectedItem().toString()+areaname;
			String OldmanTelephone=oldman_telephone.getText().toString();
			String OldmanHeight=oldman_height.getText().toString();
			String OldmanWeight=oldman_weight.getText().toString();
//			把需要传递给服务器的数据添加到params参数中
			Constants.params = new ArrayList<NameValuePair>();
			Constants.params.add(new BasicNameValuePair("useraccount", passuseraccount));
			Constants.params.add(new BasicNameValuePair("username", OldmanUsername));
			Constants.params.add(new BasicNameValuePair("gender", OldmanGender));
			Constants.params.add(new BasicNameValuePair("age", OldmanAge));
			Constants.params.add(new BasicNameValuePair("address", OldmanAddress));
			Constants.params.add(new BasicNameValuePair("telephone", OldmanTelephone));
			Constants.params.add(new BasicNameValuePair("height", OldmanHeight));
			Constants.params.add(new BasicNameValuePair("weight", OldmanWeight));
//			将在APP端填写的用户信息传递给服务器
		
		Constants.requestUrl = Constants.SERVER_HOST+"/updPerInfo.action";
		new Thread(getJson).start();//创建一个新的线程
	}
	
	private Runnable getJson=new Runnable(){
		public void run() {
			// TODO Auto-generated method stub
			try{
				Constants.result= reqtoserver.GetResponseFromServer(Constants.requestUrl, Constants.params);
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
						if(result.equalsIgnoreCase("ok")){
//							如果用户信息添加成功，则返回上一页面，同时结束当前活动
							Intent intent = new Intent();
							intent.putExtra("usertype",usertype);
							setResult(RESULT_OK,intent);
							UserPersonalInfoEdit.this.finish();
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}  
				}		
			} else if (msg.what == 0x01) {
				Toast.makeText(getApplicationContext(), "获取失败",Toast.LENGTH_LONG).show();
			}else if (msg.what == 0x02) {
				if(null != Constants.result){
					try {//处理从服务器端返回的数据
						JSONObject json= new JSONObject(Constants.result); 
						String result = (String)json.get("result");	
						String initmsg = (String)json.get("msg");
						if(result.equalsIgnoreCase("1")){
							String[] strsArray = initmsg.split("=");
                            System.out.println("strsArray .length=="+strsArray .length+" strsArray=="+strsArray[0]+"  initmsg=="+initmsg);
                            username=strsArray[0];
                            usex=strsArray[1];
                            age=strsArray[2];
                            homeaddress=strsArray[3];
                            telephoneString=strsArray[4];
                    	    height=strsArray[5];
                    		weight=strsArray[6];
                    		
                    		 oldman_account.setText(username);
                    		 oldman_age.setText(age+" 岁");
                    		// oldman_home_address.setText(homeaddress);
                    		 oldman_telephone.setText(telephoneString);
                    		 oldman_height.setText(height+" 厘米");
                    		 oldman_weight.setText(weight+" 千克");
                    		if(usex.equalsIgnoreCase("男")){
                    			male.setChecked(true);
                    		}else{
                    			female.setChecked(true);
                    		}
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}  
				}		
			} else if (msg.what == 0x03) {
				Toast.makeText(getApplicationContext(), "获取失败",Toast.LENGTH_LONG).show();
			}
		}
	};
	
	
}
