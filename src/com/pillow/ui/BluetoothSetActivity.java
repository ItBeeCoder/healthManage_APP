/**
 *日期：2017年12月4日上午9:06:13
pillow
TODO
author：shaozq
 */
package com.pillow.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.pillow.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

/**
 * @author shaozq
 *2017年12月4日上午9:06:13
 */
public class BluetoothSetActivity extends Activity {

	/** Called when the activity is first created. */
	public static final int REQUEST_ENABLE_BT = 8807;
	public BroadcastReceiver mBTReceiver;
	public static BluetoothSocket mBTSocket;
	public static BluetoothAdapter mBTAdapter;
	private Button btnSearchDevices;
	private BluetoothDevice mBTDevice;
	private ArrayAdapter<String> adtDvcs;
	private List<String> lstDvcsStr = new ArrayList<String>();	
	private ListView lvDevicesList;
	private Button open, close, visible;
	
    @SuppressLint("NewApi")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bluetooth_main);
//        setTitle("设备蓝牙操作界面");
        // Init Bluetooth Adapter
        mBTAdapter = BluetoothAdapter.getDefaultAdapter();
        
        if (mBTAdapter == null){
        	Toast.makeText(getApplicationContext(),"No devices supporting Bluetooth",Toast.LENGTH_SHORT).show();
        	this.finish();
        }
        
        open = (Button) this.findViewById(R.id.open);
		close = (Button) this.findViewById(R.id.close);
		visible = (Button) this.findViewById(R.id.visible);
		btnSearchDevices = (Button) this.findViewById(R.id.search);
		
        //public BroadcastReceiver mBTReceiver;
        // Set up BroadCast Receiver
        mBTReceiver = new BroadcastReceiver(){
        	@SuppressLint("NewApi")
			public void onReceive(Context context,Intent intent){
        		String act = intent.getAction();
        		// To see whether the action is that already found devices
        		if(act.equals(BluetoothDevice.ACTION_FOUND)){
        			// If found one device, get the device object
        			BluetoothDevice tmpDvc = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        			int rssi= intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);
        		
        			// Put the name & address into a string
        			String tmpDvcStr = tmpDvc.getName()+"|"+tmpDvc.getAddress();
        			if (lstDvcsStr.indexOf(tmpDvcStr)==-1){
        				// Avoid duplicate add devices
        				//private List<String> lstDvcsStr = new ArrayList<String>();
        				lstDvcsStr.add(tmpDvcStr);
        				//private ArrayAdapter<String> adtDvcs;
        				adtDvcs.notifyDataSetChanged();
        				Toast.makeText(getApplicationContext(), "Find a new device!", Toast.LENGTH_SHORT).show();
        			}
        		}
        		if(act.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)){
        			Toast.makeText(getApplicationContext(), " Searching Complete!", Toast.LENGTH_SHORT).show();
        		}       		
        		if (act.equals(BluetoothAdapter.ACTION_DISCOVERY_STARTED)){
    				Toast.makeText(getApplicationContext(), "Begin Searching Devices", Toast.LENGTH_SHORT).show();
        		}
         	}
        };        
        //Register the broadcastReceiver
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mBTReceiver,filter);
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        registerReceiver(mBTReceiver,filter);
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mBTReceiver,filter); 
        
        // Init Buttons
        btnSearchDevices = (Button)findViewById(R.id.search);
    	lvDevicesList = (ListView)findViewById(R.id.lv);

// ListView And Data Adapter   
    	//1���Ƚ�ɨ�赽���豸��ƺ͵�ַ�ӵ��ַ��б�lstDvcsStr�У��ٽ��豸�ַ��б���ӵ��ַ���������
    	//private ArrayAdapter<String> adtDvcs; 
    	//private List<String> lstDvcsStr = new ArrayList<String>()
        adtDvcs = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,lstDvcsStr);
        lvDevicesList.setAdapter(adtDvcs);//2����ListView����������
        // Create Click listener for the items on devices list
        //private ListView lvDevicesList= (ListView)findViewById(R.id.lvDevicesList);;
        lvDevicesList.setOnItemClickListener(new AdapterView.OnItemClickListener(){
        	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3){
        		if (mBTAdapter == null)
        			Toast.makeText(getApplicationContext(), "No devices support BlueTooth", Toast.LENGTH_SHORT).show();
        		else{
        			// stop searching
        			mBTAdapter.cancelDiscovery();
        			// Get address of remote device  private List<String> lstDvcsStr = new ArrayList<String>();
        			String str = lstDvcsStr.get(arg2);//���λ�ú�����ȡ�б�ĵڼ���
        			String[] dvcValues = str.split("\\|");
        			String dvcAddr = dvcValues[1];
        			//UUID dvcUUID = UUID.randomUUID();
        			//00001101-0000-1000-8000-00805F9B34FB SPP protocal
        			UUID dvcUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
        			// Set BT device  dvcAddrΪ�豸��������ַ�����Զ�������豸
        			mBTDevice = mBTAdapter.getRemoteDevice(dvcAddr);//���Զ�������豸�ĵ�ַ����ȡԶ�������豸
        			// Connect Device
        			try{//public static BluetoothSocket mBTSocket;
        				mBTSocket = mBTDevice.createRfcommSocketToServiceRecord(dvcUUID);
        				mBTSocket.connect();
        				Intent mInt = new Intent(getApplicationContext(),RTMonitorActivity.class);
        				startActivity(mInt);
        			}
        			catch(IOException e){
        				e.printStackTrace();
        			}
        		}        		
        	}
        });   
        open.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mBTAdapter == null) {
					Toast.makeText(getApplicationContext(), "没有搜索到可用的蓝牙", 1).show();
					return;
				} else {
					if (!mBTAdapter.isEnabled()) {
						Intent intent = new Intent();
						intent.setAction(BluetoothAdapter.ACTION_REQUEST_ENABLE);
						startActivity(intent);
					} else {
						Toast.makeText(getApplicationContext(),"蓝牙开启失败", 1).show();
						return;
					}
				}
			}
		});
		close.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mBTAdapter != null && mBTAdapter.isEnabled()) {
					mBTAdapter.disable();
				}
			}
		});
		visible.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mBTAdapter != null && mBTAdapter.isEnabled()) {
					final Intent intent = new Intent();
					intent.setAction(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
					new AlertDialog.Builder(getApplicationContext()).setTitle("设备蓝牙可见性...").setIcon(
						     android.R.drawable.ic_dialog_info).setSingleChoiceItems(
						     new String[] { "可见60秒", "可见120秒","可见180秒","可见240秒","可见300秒" }, -1,
						     new DialogInterface.OnClickListener() {
						      public void onClick(DialogInterface dialog, int which) {
						       if( which==0)
						       {
						    	   intent.putExtra(
											BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 60);
									startActivity(intent);
									dialog.dismiss();
						       }
						       else if(which==1)
						       {
						    	   intent.putExtra(
											BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 120);
									startActivity(intent);
									dialog.dismiss();
						       }
						       else if(which==2)
						       {
						    	   intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 180);
									startActivity(intent);
									dialog.dismiss();
						       }
						       else if(which==3)
						       {
						    	   intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 240);
									startActivity(intent);
									dialog.dismiss();
						       }
						       else if(which==4)
						       {
						    	   intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
									startActivity(intent);
									dialog.dismiss();
						       }
						      }
						     }).setNegativeButton("取消", null).show();
				} 
				else {
					Toast.makeText(getApplicationContext(),"没有搜索到可用的蓝牙", 1).show();//
					return;
				}
			}
		});
	
        
        // Search Devices
        btnSearchDevices.setOnClickListener(new OnClickListener(){
        	public void onClick(View view){
        		if (mBTAdapter.isDiscovering()){
        			Toast.makeText(getApplicationContext(), "Already Searching", Toast.LENGTH_SHORT).show();
        		}
    			else{
    				//private List<String> lstDvcsStr = new ArrayList<String>();
    				//private ArrayAdapter<String> adtDvcs;  	//1���Ƚ�ɨ�赽���豸��ƺ͵�ַ�ӵ��豸�ַ��б�lstDvcsStr��
    				lstDvcsStr.clear();
    				adtDvcs.notifyDataSetChanged();
    				mBTDevice = null;
    				mBTAdapter.startDiscovery();
    			}
        	}
        });
        
            
    }
    
    public void onActivityResult(int RequestCode, int ResultCode,Intent data){
    	switch(RequestCode){
    	case REQUEST_ENABLE_BT:
    		if(ResultCode == RESULT_OK){
    			Toast.makeText(this.getApplicationContext(), "BT Launched!", Toast.LENGTH_SHORT).show();
    		}
    		else if(ResultCode == RESULT_CANCELED){
        			Toast.makeText(this.getApplicationContext(), "Launched BT cancled!", Toast.LENGTH_SHORT).show();   				
    			}
    		break;
    	}
    }    
    @Override
	protected void onDestroy() {
	    this.unregisterReceiver(mBTReceiver);
		super.onDestroy();
		//android.os.Process.killProcess(android.os.Process.myPid());
		this.finish();
	}
	
	
}
