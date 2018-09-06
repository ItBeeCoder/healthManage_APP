package com.pillow.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.pillow.R;
import com.pillow.bean.Constants;
import com.pillow.bean.RequestToServer;
import com.pillow.entity.Child;
import com.pillow.entity.Oldman;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
//这个程序完成的功能是子女提交老人和自己的账号，在数据库关联表中将二者建立关联
public class ChildrenRelateOldman extends Activity {

	private TextView relatePageBack;
	private EditText oldman_account,child_account;
	private Button submit_relation,cancel_relation;
	//界面控件
		private ImageButton spinner;
		private EditText et_name;
		//构造关系列表需要用到的集合
		private List<String> names = new ArrayList<String>();
		//布局加载器
		private LayoutInflater mInflater;
		//自定义适配器
		private MyAdapter mAdapter;
		//PopupWindow
		private PopupWindow pop;
		//是否显示PopupWindow，默认不显示
		private boolean isPopShow = false;
		private static Integer add_relation =1;
		private String usertype = "",childAccount="";
		RequestToServer reqtoserver=new RequestToServer();
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.children_relate_oldman);
		
		fingViewById();
		
		usertype = (String)getIntent().getStringExtra("usertype");
		childAccount = getIntent().getStringExtra("childAccount");
		
		child_account.setText(childAccount);
		
		ClickListener cl = new ClickListener();		
		submit_relation.setOnClickListener(cl);
		cancel_relation.setOnClickListener(cl);
		relatePageBack.setOnClickListener(cl);
//		目前确定的老人和子女的关系包括以下五种
		names.add("父亲");
		names.add("母亲");
		names.add("岳父");
		names.add("岳母");
		names.add("其他");
		
		mInflater = LayoutInflater.from(ChildrenRelateOldman.this);
		mAdapter = new MyAdapter();
		spinner.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(pop == null){
					ListView listView = new ListView(ChildrenRelateOldman.this);
					listView.setCacheColorHint(0x00000000);
					listView.setAdapter(mAdapter);
					pop = new PopupWindow(listView, et_name.getWidth(), LayoutParams.WRAP_CONTENT, true);
					pop.setBackgroundDrawable(new ColorDrawable(0x00000000));
					isPopShow = true;
				}
				if(isPopShow){
					pop.showAsDropDown(et_name, 0, 0);
					isPopShow = false;
				}else{
					pop.dismiss();
					isPopShow = true;
				}
			}
		});
	}
//	初始化各种控件
	private void fingViewById(){
		relatePageBack=(TextView) findViewById(R.id.relation_page_back);
		oldman_account=(EditText) findViewById(R.id.oldman_account);
		child_account=(EditText) findViewById(R.id.child_account);
//		child_account.setText(childAccount);
		submit_relation=(Button) findViewById(R.id.submit_relation);
		cancel_relation = (Button) findViewById(R.id.cancel_relation);
		spinner = (ImageButton) findViewById(R.id.spinner);
		et_name = (EditText) findViewById(R.id.et_name);
	}
	

	Map<String,Object> map=new HashMap<String,Object>();
	
	List<Map<String,Object>> listparams=new ArrayList<Map<String,Object>>();
	//
	private final class ClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.submit_relation:
				addChildrenAndOldmanRelation();
				break;
			case R.id.relation_page_back:	
				returnToPriview();
				ChildrenRelateOldman.this.finish();
				break;
			case R.id.cancel_relation:	
				returnToPriview();
				ChildrenRelateOldman.this.finish();
				break;
				
			default:
				break;
			}
		}
	}
	//
		private void returnToPriview(){
			Intent intent = new Intent();
			intent.putExtra("usertype",usertype);
			setResult(RESULT_OK,intent);
		}
	
		public void onBackPressed(){
			returnToPriview();
			ChildrenRelateOldman.this.finish();
		}
		/*
		 * 首先获取老人、子女账号及他们之间的关系，将这些数据以健值对的形式保存到变量params中，
		 * 传递给服务器，再新建一个线程，调用向服务器发送HTTP请求的函数
		 */
		private void addChildrenAndOldmanRelation(){
			Oldman oldman=new Oldman();
			Child child=new Child();
			String oldmanUsername=oldman_account.getText().toString();
			String childUsername=child_account.getText().toString();
			String childOldmanRelation=et_name.getText().toString();
			if(null == oldmanUsername || oldmanUsername.length() == 0){
				Toast.makeText(getApplicationContext(), "老人账号不能为空", Toast.LENGTH_SHORT).show();
				return;
			}
			if(null == childUsername || childUsername.length() == 0){
				Toast.makeText(getApplicationContext(), "子女账号不能为空", Toast.LENGTH_SHORT).show();
				return;
			}
			Constants.params = new ArrayList<NameValuePair>();
			Constants.params.add(new BasicNameValuePair("Oldmanuseraccount", oldmanUsername));
			Constants.params.add(new BasicNameValuePair("childuseraccount", childUsername));
			Constants.params.add(new BasicNameValuePair("relationship", childOldmanRelation));
			Constants.params.add(new BasicNameValuePair("flag", String.valueOf(add_relation)));
			
			Constants.requestUrl = Constants.SERVER_HOST+"/AddChildOldmanRelation.action";
			Log.i("Oldmanuseraccount1111", oldmanUsername);
			Log.i("childUsername1111", childUsername);
			new Thread(getJson).start();
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
						try {
							JSONObject json= new JSONObject(Constants.result);  		
								
							String result = (String)json.get("result");	
							if(result.equalsIgnoreCase("1")){
								
								Toast.makeText(getApplicationContext(), "关联成功", Toast.LENGTH_SHORT).show();
								Intent intent = new Intent();
								intent.putExtra("usertype",usertype);
								setResult(RESULT_OK,intent);
								ChildrenRelateOldman.this.finish();
								
								//startActivity(intent);
							}else if(result.equalsIgnoreCase("0")){
							Toast.makeText(getApplicationContext(), "不能重复关联老人和子女", Toast.LENGTH_LONG).show();
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
		
		private class MyAdapter extends BaseAdapter{

			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				return names.size();
			}

			@Override
			public Object getItem(int position) {
				// TODO Auto-generated method stub
				return names.get(position);
			}

			@Override
			public long getItemId(int position) {
				// TODO Auto-generated method stub
				return position;
			}

			@Override
			public View getView(final int position, View convertView, ViewGroup parent) {
				View view = mInflater.inflate(R.layout.item, null);
				final TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
				tv_name.setText(names.get(position));
				ImageButton delete = (ImageButton) view.findViewById(R.id.delete);
				//设置按钮的监听事件
				delete.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						names.remove(position);
						isPopShow = true;
						mAdapter.notifyDataSetChanged();
					}
				});
				//设置按钮的监听事件
				tv_name.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						et_name.setText(names.get(position));
						pop.dismiss();
					}
				});
				return view;
			}
			
		} 
}
