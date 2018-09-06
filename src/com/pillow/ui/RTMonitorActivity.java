/**
 *日期：2017年12月4日上午9:09:54
pillow
TODO
author：shaozq
 */
package com.pillow.ui;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;













import java.util.UUID;

import com.pillow.R;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceView;
import android.widget.TextView;

/**
 * @author shaozq
 *2017年12月4日上午9:09:54
 */
public class RTMonitorActivity extends Activity {

	private String dataStr = new String();
	private boolean enRead = false;
	public TextView tvDeviceName;
	public static TextView tvDeviceStatus;
	private SurfaceView sfvECG;
	
	public ArrayList<Integer> list = new ArrayList<Integer>();
	
//	private BTReadThread mReadThread = new BTReadThread(1000);
	AcceptThread mReadThread = new AcceptThread();
	
	private Handler msgHandler;
	private DrawRealtimeWave mECGWF;
	
	private String revTmpStr = new String();
	public List<Float> ECGDataList = new ArrayList<Float>();
	public boolean ECGDLIsAvailable = true;
	private float ECGData = 0;
	BluetoothAdapter mBluetoothAdapter = null;
	public static final UUID MY_UUID = UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");

	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.realtimewave);
		// Init Resources
		tvDeviceName = (TextView)findViewById(R.id.tvDeviceName);
		tvDeviceStatus = (TextView)findViewById(R.id.tvDeviceStatus);
		sfvECG = (SurfaceView)findViewById(R.id.sfvECG);
		mECGWF = new DrawRealtimeWave(sfvECG);
		tvDeviceStatus.setText("设备状态：");
		try{
			if(BluetoothSetActivity.mBTSocket.getInputStream()!=null){
				enRead = true;  //private BTReadThread mReadThread = new BTReadThread(1000);
				mReadThread.start();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		Looper lp = Looper.myLooper();
		msgHandler = new MsgHandler(lp);//	private Handler msgHandler;
//		mReadThread.start();
		// Setting Timer to Draw and Save data
		Timer timer = new Timer();
		TimerTask task = new TimerTask(){
			public void run(){
				Message msg = Message.obtain();
				msg.what = 1;
				msgHandler.sendMessage(msg);
			}
		};
		// Set Timer
		timer.schedule(task,1000,200);
	}
	
	@Override
	public void onDestroy() {
		//Close Socket
		try {
			BluetoothSetActivity.mBTSocket.close();
			enRead = false;
			this.finish();
			
//			mReadThread.destroy();
		} catch (IOException e) {
			e.printStackTrace();
		}
        super.onDestroy();  
    }
	
//	@Override
//	public void onStop(){		
//		mReadThread.stop();
//		super.onStop();
//	}
//	
//	@Override
//	public void onResume(){
//		super.onResume();
//		mReadThread.resume();
//	}
	
	// MsgHandler class to Update UI
	class MsgHandler extends Handler{
		public MsgHandler(Looper lp){
			super(lp);
		}
		
		@Override
		public void handleMessage(Message msg){
			switch(msg.what){
			case 0:
				tvDeviceStatus.setText((String)msg.obj);
				if(BluetoothSetActivity.mBTSocket!=null)
					try{
						tvDeviceName.setText(BluetoothSetActivity.mBTSocket.getRemoteDevice().getName());
					}catch(Exception e){
						e.printStackTrace();
					}
					break;
			case 1:
				if (ECGDataList.size() > 1){
					List<Float> ECGCacheData = new ArrayList<Float>();
					ECGCacheData.addAll(ECGDataList);   //public List<Float> ECGDataList = new ArrayList<Float>();
					ECGDLIsAvailable = false;
					ECGDataList.clear();
					ECGDLIsAvailable = true;
					// Draw To View    private DrawECGWaveForm mECGWF;
					mECGWF.DrawtoView(ECGCacheData);					
				}
				break;
			}
		}
	}			
	// Bluetooth Reading data thread    private BTReadThread mReadThread = new BTReadThread(1000);
	
	 private class AcceptThread extends Thread {
	        // The local server socket
	        private final BluetoothServerSocket serverSocket;

	        public AcceptThread() {
	            BluetoothServerSocket tmp = null;
	            // Create a new listening server socket
	            try {
	                tmp = BluetoothSetActivity.mBTAdapter.listenUsingRfcommWithServiceRecord("BluetoothDemo",MY_UUID);
	            } catch (IOException e) {
	            	System.out.println("Failed to start server\n");
	            }
	            serverSocket = tmp;
	        }

	        public void run() {
	        	while(true){
	            BluetoothSocket socket = null;
	            try {
	                // This is a blocking call and will only return on a
	                // successful connection or an exception
	                socket = serverSocket.accept();
	            } catch (IOException e) {
	            	System.out.println("Failed to accept\n");
	            }
	            // If a connection was accepted
	            if (socket != null) {
	            	System.out.println("Remote device address: " + socket.getRemoteDevice().getAddress() + "\n");
	                //Note this is copied from the TCPdemo code.
	                try {
	                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	                    String str = in.readLine();
	                    System.out.println("开始调用printHexString(str)");
	                    System.out.println("str==="+str);
	                    printHexString(str);
	                    System.out.println("调用printHexString(str)结束");
	                    
	                } catch (Exception e) {
	                    System.out.println("Error happened sending/receiving\n");
	                } finally {
	                    try {
	                        socket.close();
	                    } catch (IOException e) {
	                    	System.out.println("Unable to close socket" + e.getMessage() + "\n");
	                    }
	                }
	            } else {
	            	System.out.println("Made connection, but socket is null\n");
	            }
	            System.out.println("Server ending \n");
	        }
	        }

	        @SuppressWarnings("unused")
			public void cancel() {
	            try {
	                serverSocket.close();
	            } catch (IOException e) {
	            	System.out.println( "close() of connect socket failed: "+e.getMessage() +"\n");
	            }
	        }    
	    }
	
	
//	public void printHexString( byte[] b,int len) {
	 public void printHexString(String str) {
		
		System.out.println("开始调用printHexString(readB,nBytes)");
        int tempmiddledata=-1,count=0;
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");//设置日期格式这里决定存储数据的文件名格式是按照年月日时分还是年月日
	    String date = df.format(new Date());// new Date()为获取当前系统时间
	    
        String fileName="E:/pillow/"+date+".txt";
        
//		  for (int i = 0; i < len; i++) {  
//		         String hex = Integer.toHexString(b[i] & 0xFF)+"";  
//		         if (hex.length() == 1) {  
//		        	 hex = '0' + hex;  
//		         }         
//		         int tempnum=Integer.parseInt(hex.toUpperCase(),16);
//		         if(tempnum<=57&&tempnum>=48){
//		        	 tempnum-=48;
//		        	list.add(tempnum);
//		         }else if (tempnum==13){
//		        	 tempmiddledata=tempnum;
//		         }else if ((tempnum==10)&&(tempmiddledata==13)){
//		        	 for(int j = 0;j < list.size(); j ++){
////		        		   System.out.println("list.get(i)=="+list.get(j));
//		        		   count=count*10+list.get(j);
//		        		}
////		        		String msg="";  
////				         Date date1 = new Date();  
////				         SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");  
////				         msg+=sdf.format(date1);  
//		        	 String str=count+"	";
//		        	 
//		        	 System.out.println("len=="+len+" "); 
//		        	
//			         writeMethod2(fileName,str);
//			       
//			         count=0;
//			         list.clear();
//		         } 
//		   }
		  writeMethod2(fileName,str);
		}

 	public void writeMethod2(String fileName,String str){
//        String fileName="E:/pillow/"+date+".txt";
 		System.out.println("开始调用writeMethod2(String fileName,String str)");
        try {
           FileWriter writer=new FileWriter(fileName,true);
           writer.write(str);
           System.out.println(str);  
           writer.close();
        } catch (IOException e){
           e.printStackTrace();
        }
	}
	
	
	
}
