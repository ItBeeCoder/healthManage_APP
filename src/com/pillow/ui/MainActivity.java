package com.pillow.ui;

import java.util.ArrayList; 
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.pillow.R;
import com.pillow.adapter.MyPagerAdapter;
import com.pillow.application.ExitAllActivity;
import com.pillow.bean.Constants;
import com.pillow.bean.RequestToServer;
import com.pillow.entity.Oldman;
import com.pillow.mainpage.ChannelItem;
import com.pillow.mainpage.DragAdapter;
import com.pillow.notification.FirstNotificationActivity;
import com.pillow.notification.FirstSuccessNotifiActivity;
import com.pillow.notification.NotificationActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
//对应APP的主界面，若用户类型为老人，在登录进入主界面后，APP会自动
public class MainActivity extends Activity {
	
	private TextView oldmantype,confirmPrompt;
	private Spinner mySpinner1;
	private GridView gv;
	String userCategory = "";
	ArrayList<ChannelItem> dragLists = new ArrayList<ChannelItem>();
//	九宫格中对应的九张图片
	public int[] imgs = { R.drawable.b1, R.drawable.b2,
						R.drawable.b3, R.drawable.b4,
						R.drawable.a21, R.drawable.b5,
						R.drawable.app_facepay, R.drawable.b6, R.drawable.b6 };
	private List<String> list = new ArrayList<String>();
	String useraccount="",usertype="";
	private ViewPager mViewPager;
    private List<ImageView> mImageViewList;
//    上面轮转图中对应的五张图片
    private int[] images={R.drawable.a6,R.drawable.a7,R.drawable.a8,R.drawable.a9,R.drawable.a10};
    private int currentPosition=1, dotPosition=0, prePosition=0;
    private LinearLayout mLinearLayoutDot,confirm_prompt_linearlayout;
    private List<ImageView> mImageViewDotList;
    public static Bundle bundle = new Bundle();
    public static String oldmanAccount = "",childAccount="",TAG="MainActivity";
    RequestToServer reqtoserver=new RequestToServer();
    
    List<Oldman> recomendGoods = new ArrayList<Oldman>();
	
    Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			 if(msg.what==1){
	                mViewPager.setCurrentItem(currentPosition,false);
	            }
			 else if (msg.what == 0x00) {//处理服务器返回的结果
				Log.v("PostGetJson", "" + Constants.result);
				if(null != Constants.result){
					try {
						JSONObject json= new JSONObject(Constants.result);  		
						String re = (String)json.get("result");
						String retmsg = (String)json.get("msg");
						int ret=Integer.parseInt(re);
						if(ret==1){
							 oldmanAccount = retmsg;
						 //oldmanAccount = (String)json.get("Oldmanuseraccount");这样写时会有问题							
							bundle.putString("oldmanAccount", oldmanAccount);
						}else if(ret==0){
							Toast.makeText(getApplicationContext(), "该子女与老人不存在关系", Toast.LENGTH_LONG).show();
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}  
				}
			}
			 else if (msg.what == 0x02) {
					Log.v("PostGetJson", "" + Constants.result);
					if(null != Constants.result){
						try {
							JSONObject json= new JSONObject(Constants.result);  		
							String re = (String)json.get("result");
							String retmsg = (String)json.get("msg");
							int ret=Integer.parseInt(re);
							if(ret==1){
								 childAccount = retmsg;
								bundle.putString("oldmanAccount", oldmanAccount);
								
								confirmPrompt.setVisibility(View.VISIBLE);
								confirm_prompt_linearlayout.setVisibility(View.VISIBLE);
								confirmPrompt.setText(childAccount+"向您发送关联请求，请点击确认关联");
//								定义的事件触发器
								ClickListener cl = new ClickListener();
								confirmPrompt.setOnClickListener(cl);
							}else if(ret==0){
								confirmPrompt.setVisibility(View.GONE);
								confirm_prompt_linearlayout.setVisibility(View.GONE);
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}  
					}
				}
			 else if (msg.what == 0x01) {
				Toast.makeText(getApplicationContext(), "获取失败",Toast.LENGTH_LONG).show();
			}
		}
	};
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);
		ExitAllActivity.getActivityInstance().addActivity(this);
/*
 * 这里首先通过 getIntent()方法获取到用于启动 MainActivity的 Intent，然后调用
	getStringExtra()方法，传入相应的键值，就可以得到传递的数据了。由于传递的
	是字符串，所以使用 getStringExtra()方法来获取传递的数据，同样，如果传递的是整型数据，则
	使用 getIntExtra()方法，传递的是布尔型数据，则使用 getBooleanExtra()方法
 */
		Intent intent=getIntent();
		useraccount=intent.getStringExtra("useraccount");
		usertype = intent.getStringExtra("usertype");
		
		bundle.putString("usertype", usertype);
		
		findViewById();
		if(usertype.equalsIgnoreCase("子女")||usertype=="子女"){
			showSpinner1();
			getData();	
		}
		
		if(usertype.equalsIgnoreCase("老人")||usertype=="老人"){
			bundle.putString("oldmanAccount", useraccount);
			getConfirmData();
		}
		
		initItem();
		initData();
        setDot();
        setViewPager();
//        APP界面上方的轮播图自动循环播放
        autoPlay();
        
		gv.setAdapter(new DragAdapter(this, dragLists));
//		这里根据用户在九宫格上点击的位置，APP由当前主界面跳转向不同界面
		gv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				// TODO Auto-generated method stub
				switch (position) {
				case 0:
					Intent intent0 = new Intent();
					//intent0.putExtra("oldmanAccount",oldmanAccount);
					intent0.putExtras(bundle);//存储的是老人的账号
					intent0.putExtra("useraccount",useraccount);
					intent0.setClass(getApplicationContext(),breath.class);	
					startActivity(intent0);		
					break;
				case 1:
					Intent intent1 = new Intent();
//					intent1.putExtra("oldmanAccount",oldmanAccount);//存储的是老人的账号
					intent1.putExtras(bundle);//存储的是老人的账号
					intent1.putExtra("useraccount",useraccount);
					intent1.setClass(getApplicationContext(),heart.class);	
					startActivity(intent1);
					break;
				case 2:
					Intent intent2= new Intent();
					//intent2.putExtra("oldmanAccount",oldmanAccount);//存储的是老人的账号
					intent2.putExtras(bundle);//存储的是老人的账号
					intent2.putExtra("useraccount",useraccount);
					intent2.setClass(getApplicationContext(),Bodymove.class);
					startActivity(intent2);
					break;
				case 3:
					Intent intent3= new Intent();
					intent3.putExtra("useraccount",useraccount);
					//intent3.putExtra("oldmanAccount",oldmanAccount);//存储的是老人的账号
					intent3.putExtras(bundle);//存储的是老人的账号
					intent3.setClass(getApplicationContext(),UserInfoActivity.class);	
					startActivity(intent3);
					break;
				case 4:
					Intent intent4= new Intent();
					intent4.putExtra("useraccount", useraccount);
					//intent4.putExtra("oldmanAccount",oldmanAccount);//存储的是老人的账号
					intent4.putExtras(bundle);//存储的是老人的账号
//					intent4.setClass(getApplicationContext(),NotificationActivity.class);
					intent4.setClass(getApplicationContext(),FirstSuccessNotifiActivity.class);
					startActivity(intent4);
					break;
				case 5:
					Intent intent5= new Intent();
					intent5.putExtra("useraccount", useraccount);
					intent5.setClass(getApplicationContext(),SettingPageActivity.class);
					startActivity(intent5);
					break;
				case 6:
					Intent intent6= new Intent();
					intent6.setClass(getApplicationContext(),TestSendData.class);
					startActivity(intent6);
					break;
				default:
					break;
				}
			}
			});
	}
	
	  @Override  
	    protected void onStart() {  
	        // TODO Auto-generated method stub  
	        super.onStart();  
	        Log.e(TAG, "onStart方法被调用");  
	        if(usertype.equalsIgnoreCase("老人")||usertype=="老人"){
				bundle.putString("oldmanAccount", useraccount);
				getConfirmData();
			}
	    }  
	  
	  @Override  
	  protected void onRestart() {
        // TODO Auto-generated method stub  
		  super.onRestart();
		  if(usertype.equalsIgnoreCase("老人")||usertype=="老人"){
				bundle.putString("oldmanAccount", useraccount);
				getConfirmData();
			}
	  }  
  
	  @Override  
	  protected void onResume() {  
        // TODO Auto-generated method stub  
		  super.onResume();  
		  Log.e(TAG, "onResume方法被调用");  
	  }  
    
	  @Override  
	    protected void onPause() {  
	        // TODO Auto-generated method stub  
	        super.onPause();  
	        Log.e(TAG, "onPause方法被调用");  
	    }  
	  
	    @Override  
	    protected void onStop() {  
	        // TODO Auto-generated method stub  
	        super.onStop();  
	        Log.e(TAG, "onStop方法被调用");  
	    }  
//	   初始化各种UI控件
	private void findViewById(){
		oldmantype = (TextView) findViewById(R.id.oldmantype);
		confirmPrompt = (TextView) findViewById(R.id.confirm_prompt);
		mySpinner1 = (Spinner) findViewById(R.id.spinner01);
		mViewPager= (ViewPager) findViewById(R.id.vp_main);
		mLinearLayoutDot= (LinearLayout) findViewById(R.id.ll_main_dot);
		confirm_prompt_linearlayout = (LinearLayout) findViewById(R.id.confirm_prompt_ll);
		gv = (GridView) findViewById(R.id.userGridView);
		
		if(usertype.equalsIgnoreCase("老人")||usertype=="老人"){
			oldmantype.setVisibility(View.GONE);//View.INVISIBLE View不可以见，但仍然占据可见时的大小和位置
//			View.GONE View不可见，且不占据空间。
			mySpinner1.setVisibility(View.GONE);
//			confirmPrompt.setVisibility(View.VISIBLE);
//			confirm_prompt_linearlayout.setVisibility(View.VISIBLE);
		}
	}
//	定义的事件触发器
	private final class ClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.confirm_prompt:
				GotoConfirmRelate();
				break;
			default:
				break;
			}
		}
	}
//	跳转向下一个界面：老人确认关联
	private void GotoConfirmRelate(){
		Intent  intent = new Intent(MainActivity.this, OldmanConfirmRelate.class);
		intent.putExtra("childAccount",childAccount);
		intent.putExtra("oldmanAccount",useraccount);
		intent.putExtra("usertype",usertype);
		
		startActivityForResult(intent, 100);
	}
	
	/**
	 * 这一部分设置九宫格的内容和图片，ChannelItem为对九宫格内容格式的定义
	 */
	private void initItem(){
		ChannelItem item1 = new ChannelItem(1, "查看呼吸" ,imgs[0]);
		ChannelItem item2 = new ChannelItem(2, "查看心率" ,imgs[1]);
		ChannelItem item3 = new ChannelItem(3, "查看体动" ,imgs[2]);
		ChannelItem item4 = new ChannelItem(4, "信息管理" ,imgs[3]);
		ChannelItem item5 = new ChannelItem(5, "异常报警" ,imgs[4]);
		ChannelItem item6 = new ChannelItem(6, "设置" ,imgs[5]);
		ChannelItem item7 = new ChannelItem(7, "其它" ,imgs[6]);
		ChannelItem item8 = new ChannelItem(8, "其它3" ,imgs[7]);
		ChannelItem item9 = new ChannelItem(9, "其它4" ,imgs[8]);
		
		dragLists.add(item1);
		dragLists.add(item2);
		dragLists.add(item3);
		dragLists.add(item4);
		dragLists.add(item5);
		dragLists.add(item6);
		dragLists.add(item7);
		dragLists.add(item8);
		dragLists.add(item9);
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		DragAdapter.selectedPos = -1;
	}
	
	private void initData() {
        mImageViewList=new ArrayList<>();
        mImageViewDotList=new ArrayList();
        ImageView imageView;
        for(int i=0;i<images.length+2;i++){
            if(i==0){   //
                imageView=new ImageView(this);
                imageView.setBackgroundResource(images[images.length-1]);
                mImageViewList.add(imageView);
            }else if(i==images.length+1){   //
                imageView=new ImageView(this);
                imageView.setBackgroundResource(images[0]);
                mImageViewList.add(imageView);
            }else{  //
                imageView=new ImageView(this);
                imageView.setBackgroundResource(images[i-1]);
                mImageViewList.add(imageView);
            }
        }
    }
//	showSpinner1()对应主界面上方的用户类型列表
	public void showSpinner1() {
		// 第一步：添加一个下拉列表项的list，这里添加的项就是下拉列表的菜单项
		//private List<String> list = new ArrayList<String>();
		list.add("请选择关联的老人");
		list.add("父亲");
		list.add("母亲");
		list.add("岳父");
		list.add("岳母");
		list.add("其他");
		// 第二步：为下拉列表定义一个适配器，这里就用到里前面定义的list。
		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, list);
		// 第三步：为适配器设置下拉列表下拉时的菜单样式。 simple_spinner_item
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
		// 第四步：将适配器添加到下拉列表上 	mySpinner1 = (Spinner) findViewById(R.id.spinner01);
		mySpinner1.setAdapter(adapter);
		// 第五步：为下拉列表设置各种事件的响应，这个事响应菜单被选中
		mySpinner1
				.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int position, long arg3) {
						/* 将所选mySpinner 的值带入myTextView 中 */
						userCategory  = adapter.getItem(position);//String category = "";
						/* 将mySpinner 显示 */
						arg0.setVisibility(View.VISIBLE);		
						getData();
					}
					public void onNothingSelected(AdapterView<?> arg0) {
//						myTextView.setText("NONE");
						arg0.setVisibility(View.VISIBLE);
						
					}
				});
		/* 下拉菜单弹出的内容选项焦点改变事件处理 */
		mySpinner1
				.setOnFocusChangeListener(new Spinner.OnFocusChangeListener() {
					public void onFocusChange(View v, boolean hasFocus) {
						v.setVisibility(View.VISIBLE);
					}
				});
	}
	
	//最上面的下拉列表对应的事件函数
		private void getData(){
			Constants.params = new ArrayList<NameValuePair>();
			if("请选择关联的老人".equals(userCategory)){
				Toast.makeText(getApplicationContext(), "请选择用户关系", Toast.LENGTH_SHORT).show();
			}else{
				if("父亲".equals(userCategory)){
					Constants.params.add(new BasicNameValuePair("relationship", "父亲"));
				}else if("母亲".equals(userCategory)){
					Constants.params.add(new BasicNameValuePair("relationship", "母亲"));
				}else if("岳父".equals(userCategory)){
					Constants.params.add(new BasicNameValuePair("relationship", "岳父"));
				}else if("岳母".equals(userCategory)){
					Constants.params.add(new BasicNameValuePair("relationship", "岳母"));
				}else if("其他".equals(userCategory)){
					Constants.params.add(new BasicNameValuePair("relationship", "其他"));
				}
				Constants.params.add(new BasicNameValuePair("childuseraccount",useraccount));
				Constants.requestUrl = Constants.SERVER_HOST+"/oldmanSelectByRelation.action";	
				new Thread(getJson).start();//新建一个线程	
			}
		}
		
		private void getConfirmData(){
			Constants.params =new ArrayList<NameValuePair>();
			Constants.params.add(new BasicNameValuePair("Oldmanuseraccount",useraccount));
			Constants.requestUrl = Constants.SERVER_HOST+"/FirstPageGetConfirmData.action";	
			new Thread(getJson2).start();
		}
		
		private Runnable getJson = new Runnable() {

			public void run() {
				// TODO Auto-generated method stub
				try {
					Constants.result = reqtoserver.GetResponseFromServer(Constants.requestUrl,Constants.params);
					handler.sendEmptyMessage(0x00);
				} catch (Exception e) {
					handler.sendEmptyMessage(0x01);
				}
			}
		};
		
		private Runnable getJson2 = new Runnable() {
			public void run() {
				// TODO Auto-generated method stub
				try {
					Constants.result = reqtoserver.GetResponseFromServer(Constants.requestUrl,Constants.params);
					handler.sendEmptyMessage(0x02);
				} catch (Exception e) {
					handler.sendEmptyMessage(0x01);
				}
			}
		};
		
    //设置显示轮转图最下面对应的五个小点  
    private void setDot() { 
        LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(15,15);
        params.rightMargin=20;
        // 
        for(int i=0;i<images.length;i++){
            ImageView  imageViewDot=new ImageView(this);
            imageViewDot.setLayoutParams(params);
            // 
            imageViewDot.setBackgroundResource(R.drawable.red_dot_night);
            mLinearLayoutDot.addView(imageViewDot);
            mImageViewDotList.add(imageViewDot);
        }
        mImageViewDotList.get(dotPosition).setBackgroundResource(R.drawable.red_dot);
    }

    private void setViewPager() {
        MyPagerAdapter adapter=new MyPagerAdapter(mImageViewList);

        mViewPager.setAdapter(adapter);

        mViewPager.setCurrentItem(currentPosition);
        //
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }
/*
 * 功能：这部分完成轮播图最下面的小圆点显示功能
 * @see android.support.v4.view.ViewPager.OnPageChangeListener#onPageSelected(int)
 *	author:shaozq
 * 2017年11月23日下午5:07:36
 */
            public void onPageSelected(int position) {
                if(position==0){    //
                    currentPosition=images.length;
                    dotPosition=images.length-1;
                }else if(position==images.length+1){    //
                    currentPosition=1;
                    dotPosition=0;
                }else{
                    currentPosition=position;
                    dotPosition=position-1;
                }
                mImageViewDotList.get(prePosition).setBackgroundResource(R.drawable.red_dot_night);
                mImageViewDotList.get(dotPosition).setBackgroundResource(R.drawable.red_dot);
                prePosition=dotPosition;
            }

            public void onPageScrollStateChanged(int state) {
                if(state==ViewPager.SCROLL_STATE_IDLE){
                    mViewPager.setCurrentItem(currentPosition,false);
                }
            }
        });
    }
    //最上面的轮转图一直循环播放 
    private void autoPlay() {
        new Thread(){
            @Override
            public void run() {
                super.run();
                while(true){
                    SystemClock.sleep(3000);
                    currentPosition++;
                    handler.sendEmptyMessage(1);
                }
            }
        }.start();
    }
    
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch(requestCode){
		case 100:
			if(resultCode == RESULT_OK){
				oldmanAccount = getIntent().getStringExtra("oldmanAccount");
				usertype = getIntent().getStringExtra("usertype");
			}
			break;
		}
	}
    
 // 事件点击监听器
 	private final class ClickListener1 implements OnClickListener {
 		@Override
 		public void onClick(View v) {
 			switch (v.getId()) {
 			case R.id.oldmantype: // 返回
 				MainActivity.this.finish();
 				break;
 			}
 		}
 	}
    
}

