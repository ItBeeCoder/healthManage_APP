/**
 *日期：2017年12月4日上午9:35:26
pillow
TODO
author：shaozq
 */
package com.pillow.ui;

import java.util.List;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * @author shaozq
 *2017年12月4日上午9:35:26
 */
public class DrawRealtimeWave {

	private SurfaceView sfv;
	private SurfaceHolder sfh;
	private int sfHeight;
	private int sfWidth;
	private int tmpX = 0, tmpY = 0;
	private int scaleX = 2, scaleY = 80;
	private Paint mPaint;
	public DrawRealtimeWave(SurfaceView msfv){
		this.sfv = msfv;
	}
	
	public void InitCanvas(){
		sfh = sfv.getHolder();  //private SurfaceView sfv;
		sfHeight = sfv.getHeight();
		sfWidth = sfv.getWidth();
		mPaint = new Paint(); //private Paint mPaint;
		mPaint.setColor(Color.BLUE);
		mPaint.setStrokeWidth(3);
		mPaint.setAntiAlias(true);
	}
	
	public void CleanCanvas(){	//private SurfaceHolder sfh;  private SurfaceView sfv;
		Canvas mCanvas = sfh.lockCanvas(new Rect(0,0,sfv.getWidth(),sfv.getHeight()));
		mCanvas.drawColor(Color.WHITE);
		tmpX = 0;
		tmpY = sfHeight/2;
		sfh.unlockCanvasAndPost(mCanvas);
	}
	
	public void DrawtoView(List<Float> ECGDataList){
		if (sfh == null){	//private SurfaceHolder sfh;  
			this.InitCanvas();
			this.CleanCanvas();
		}
		int ptsNumber = ECGDataList.size();
		int posLst = 0;
		while(posLst < ptsNumber){
			int posCan = 0;
			float drawPoints[] = new float[(ptsNumber)*4];
			float ECGValue = tmpY;			
			if (tmpX ==0){
				drawPoints[0] = 0;drawPoints[1] = tmpY;
			}
			else{
				drawPoints[0] = tmpX; drawPoints[1] = tmpY;
			}
			//	private int scaleX = 2, scaleY = 80;  sfWidth = sfv.getWidth();	 int ptsNumber = ECGDataList.size();
			for (posCan = tmpX ; posCan < sfWidth && posLst < ptsNumber; posCan+=scaleX){
				try{//	sfHeight = sfv.getHeight();
					ECGValue = -ECGDataList.get(posLst)*scaleY + sfHeight/2;
				}catch(Exception e){
					e.printStackTrace();
					posLst++;
					continue;
				}
				drawPoints[4*posLst+2] = posCan;
				drawPoints[4*posLst+3] = ECGValue;
				if(posLst <= ptsNumber -2){
				drawPoints[4*posLst+4] = posCan;
				drawPoints[4*posLst+5] = ECGValue;
				}
				posLst++;
			}
			if (posCan >= sfWidth){
				this.CleanCanvas();
			}else{
			Canvas mCanvas = sfh.lockCanvas(new Rect(tmpX,0,posCan-scaleX,sfHeight));
			mCanvas.drawColor(Color.WHITE);			
			mCanvas.drawLines(drawPoints,mPaint);
			sfh.unlockCanvasAndPost(mCanvas);
			tmpX = posCan-scaleX;
			tmpY = (int)ECGValue; 
			}
		}
	}
	
}
