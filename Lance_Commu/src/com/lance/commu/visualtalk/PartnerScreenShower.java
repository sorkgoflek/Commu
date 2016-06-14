package com.lance.commu.visualtalk;


import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.LinkedList;

import com.lance.commu.network.NetworkManager;

public class PartnerScreenShower extends Thread{
	public static final char DATA_TYPE_SCREENSIZE='Z';
	
	public static final char DATA_TYPE_COORDINATE_START='S';
	public static final char DATA_TYPE_COORDINATE_ING='N';
	public static final char DATA_TYPE_COORDINATE_END='F';
	
	public static final char DATA_TYPE_PEN_STYLE='P';
	public static final char DATA_TYPE_PEN_COLOR='C';
	public static final char DATA_TYPE_PEN_WIDTH='W';
	
	public static final char DATA_TYPE_ERASE='R';
	public static final char DATA_TYPE_ERASE_ALL='T';
	
	public static final char DATA_TYPE_BACKGROUND='X';
	
	public static final char DATA_TYPE_FILE='L';
	public static final char DATA_TYPE_FILE_ING='A';
	public static final char DATA_TYPE_FILE_END='B';
	
	public static final char DATA_TYPE_QUIT='Q';
	
	NetworkManager nm;
	
	public LinkedList<String> dataList;
	
	//상대의 화면에 그려지는 내용을 표시하기 위한 변수들
	View myView;
	float otherX, otherY;
	Path otherPath;
	Paint otherPaint;
	float changeRateX, changeRateY;//상대 화면과의 비율
	private static final float TOUCH_TOLERANCE = 4;
	
	boolean running;
	
	public PartnerScreenShower(){
		nm=NetworkManager.getInstance();
		dataList=new LinkedList<String>();
		
		otherPath = new Path();
		otherPaint = new Paint();
		otherPaint.setAntiAlias(true);
		otherPaint.setDither(true);
		otherPaint.setColor(0xFFFF0000);
		otherPaint.setStyle(Paint.Style.STROKE);
		otherPaint.setStrokeJoin(Paint.Join.ROUND);
		otherPaint.setStrokeCap(Paint.Cap.ROUND);
		otherPaint.setStrokeWidth(12);
		
		running=false;
	}
	
	public PartnerScreenShower(LinkedList<String> dataList, View myView){
		nm=NetworkManager.getInstance();
		this.dataList=dataList;
		
		this.myView=myView;
		otherPath = new Path();
		otherPaint = new Paint();
		otherPaint.setAntiAlias(true);
		otherPaint.setDither(true);
		otherPaint.setColor(0xFFFF0000);
		otherPaint.setStyle(Paint.Style.STROKE);
		otherPaint.setStrokeJoin(Paint.Join.ROUND);
		otherPaint.setStrokeCap(Paint.Cap.ROUND);
		otherPaint.setStrokeWidth(12);
		
		running=false;
	}
	
	public void terminate(){
		running=false;
	}
	
	public boolean isRunning(){
		return running;
	}
	
	//해석, 내 스크린에 그림
	@SuppressLint("NewApi")
	public void run(){
		running=true;
		
		int count=0;
		
		//나의 스크린 사이즈를 전송, 확실히 전송을 해야함
		while(count<100){
			if(nm.sendData(DATA_TYPE_SCREENSIZE, VTActivity.mScreenSize.x+"/"+VTActivity.mScreenSize.y)){
				break;
			}
			else{
				count++;
				try {sleep(1000);} catch (InterruptedException e) {}
			}
		}
		
		String data_String=null;
		
		while(running){
			
			try {sleep(1);} catch (InterruptedException e) {}//이게 있어야 잘 돌아감
			
			synchronized(dataList){//소용없음 그래도 하는게 좋을 듯
				if(dataList.isEmpty()==false){

					String data=dataList.pollFirst().toString();//삭제와 동시에 데이터 리턴 
					
					int data_int;
					int othersize_x, othersize_y;
					float data_float;
					float x, y;
					
					Message msg;
					
					Log.i("NetworkWithVT", "pss class - 하나 꺼냄 "+data);
					
					switch(data.charAt(0)){
						case DATA_TYPE_SCREENSIZE: 
							othersize_x=Integer.parseInt(data.substring(1, data.indexOf("/")));
							othersize_y=Integer.parseInt(data.substring(data.indexOf("/")+1));
							
							Log.i("NetworkWithVT", "other screen size x: "+othersize_x+", y: "+othersize_y);
							Log.i("NetworkWithVT", "my screen size x: "+VTActivity.mScreenSize.x+", y: "+VTActivity.mScreenSize.y);
							
							changeRateX=VTActivity.mScreenSize.x /  (float)othersize_x;
							changeRateY=VTActivity.mScreenSize.y / (float)othersize_y;
							
							Log.i("NetworkWithVT", "changeRateX: "+changeRateX);
							Log.i("NetworkWithVT", "changeRateY: "+changeRateY);
							
							otherPaint.setStrokeWidth(otherPaint.getStrokeWidth()*changeRateX);//굵기를 비율에 따라 설정
							
							break;
							
						case DATA_TYPE_COORDINATE_START: 
							x=Float.parseFloat(data.substring(1, data.indexOf("/")));
							y=Float.parseFloat(data.substring(data.indexOf("/")+1));
							
							x*=changeRateX;
							y*=changeRateY;
							
							Log.i("NetworkWithVT", "coodinate_start x: "+x+", y: "+y);
							
							otherPath.reset();
							otherPath.moveTo(x, y);
							otherX = x;
							otherY = y;
							
							MyView.mCanvas.drawPath(otherPath, otherPaint);
							
							invalidateHandler.sendEmptyMessage(0);
							
							break;
							
						case DATA_TYPE_COORDINATE_ING: 
							x=Float.parseFloat(data.substring(1, data.indexOf("/")));
							y=Float.parseFloat(data.substring(data.indexOf("/")+1));
							
							x*=changeRateX;
							y*=changeRateY;
							
							Log.i("NetworkWithVT", "coodinate_ing x: "+x+", y: "+y);

							float dx = Math.abs(x - otherX);
							float dy = Math.abs(y - otherY);
							
							if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
								otherPath.quadTo(otherX, otherY, (x + otherX) / 2, (y + otherY) / 2);
								otherX = x;
								otherY = y;
							}
							
							MyView.mCanvas.drawPath(otherPath, otherPaint);
							
							invalidateHandler.sendEmptyMessage(0);
							
							break;
							
						case DATA_TYPE_COORDINATE_END: 
							x=Float.parseFloat(data.substring(1, data.indexOf("/")));
							y=Float.parseFloat(data.substring(data.indexOf("/")+1));
							
							x*=changeRateX;
							y*=changeRateY;
							
							Log.i("NetworkWithVT", "coodinate_end x: "+x+", y: "+y);

							otherPath.lineTo(otherX, otherY);
							MyView.mCanvas.drawPath(otherPath, otherPaint);
							otherPath.reset();
							
							invalidateHandler.sendEmptyMessage(0);
							
							break;
							
						case DATA_TYPE_PEN_COLOR: 
							data_int=Integer.parseInt(data.substring(1));
							
							Log.i("NetworkWithVT", "color: "+data_int);
							
							otherPaint.setColor(data_int);
							
							break;
							
						case DATA_TYPE_PEN_WIDTH: 
							data_float=Float.parseFloat(data.substring(1));
							
							Log.i("NetworkWithVT", "width: "+data_float);

							otherPaint.setStrokeWidth(data_float*changeRateX);
							
							break;
							
						case DATA_TYPE_PEN_STYLE: 
							data_int=Integer.parseInt(data.substring(1));
							
							Log.i("NetworkWithVT", "pen style: "+data_int);
							
							if(data_int % 3==0){//일반 펜
								otherPaint.setXfermode(null);
								otherPaint.setAlpha(0xFF);
								otherPaint.setMaskFilter(null);
							}
							else if(data_int % 3==1){//메직(엠보싱)
								otherPaint.setXfermode(null);
								otherPaint.setAlpha(0xFF);
								otherPaint.setMaskFilter(VTActivity.mEmboss);
							}
							else if(data_int % 3==2){//스프레이(블러)
								otherPaint.setXfermode(null);
								otherPaint.setAlpha(0xFF);
								otherPaint.setMaskFilter(VTActivity.mBlur);
							}
			
							break;

						case DATA_TYPE_ERASE: 
							
							Log.i("NetworkWithVT", "erase");
							
							otherPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
							otherPaint.setMaskFilter(null);
							
							break;
							
						case DATA_TYPE_ERASE_ALL: 
							
							MyView.mBitmap = Bitmap.createBitmap(VTActivity.mScreenSize.x, VTActivity.mScreenSize.y, Bitmap.Config.ARGB_8888);
							MyView.mCanvas = new Canvas(MyView.mBitmap);
							
							invalidateHandler.sendEmptyMessage(0);
							
							msg=new Message();
							msg.obj="상대방이 화면의 모든 내용을 지웠습니다.";
							toastHandler.sendMessage(msg);
							
							Log.i("NetworkWithVT", "erase all");
							break;
							
						case DATA_TYPE_BACKGROUND: 
							data_int=Integer.parseInt(data.substring(1));
							
							BackgroundDialog.ImgNumber=data_int;
							
							setBackgroundHandler.sendEmptyMessage(0);
							invalidateHandler.sendEmptyMessage(0);
							
							msg=new Message();
							msg.obj="상대방이 배경을 바꿨습니다.";
							toastHandler.sendMessage(msg);
							
							Log.i("NetworkWithVT", "배경이미지 교체: " + data_int);
							break;
							
						case DATA_TYPE_FILE: 
							data_String=null;
							data_String=data.substring(1);
							
							Log.i("NetworkWithVT", "data_String.length: "+data_String.length());
							Log.i("NetworkWithVT", "이미지 수신 시작");
							break;
							
						case DATA_TYPE_FILE_ING: 
							data_String+=data.substring(1);
							
							Log.i("NetworkWithVT", "data_String.length: "+data_String.length());
							Log.i("NetworkWithVT", "이미지 수신 중");
							break;
							
						case DATA_TYPE_FILE_END: 
							data_String+=data.substring(1);
							Log.i("NetworkWithVT", "data_String.length: "+data_String.length());
							
							byte data_byte[]=Base64.decode(data_String, Base64.DEFAULT);
							Log.i("NetworkWithVT", "data_byte.length: "+data_byte.length);
							
							msg=new Message();
							if(MyView.change_back(data_byte)){
								msg.obj="상대방으로부터 이미지를 전송 받았습니다.";
							}
							else{
								msg.obj="상대방으로부터 잘못된 이미지 형식을 받았습니다.";
							}
							
							data_byte=null;
							data_String=null;
							
							BackgroundDialog.ImgNumber=0;
							invalidateHandler.sendEmptyMessage(0);
							toastHandler.sendMessage(msg);
							
							Log.i("NetworkWithVT", "이미지 수신 끝");
							break;
							
						case DATA_TYPE_QUIT: 
							
							VTActivity.turnOff();
							
							Log.i("NetworkWithVT", "상대방이 VT 종료");
							break;
						
						default:
							Log.i("NetworkWithVT", "Not Invalid Data");
							break;
					}
				}
				else{
					yield();
				}
			}
		}
		
		Log.i("NetworkWithVT", "pss의 run 종료");
	}
	
	Handler setBackgroundHandler = new Handler()  {
    	@SuppressWarnings("deprecation")
		@SuppressLint("NewApi") public void handleMessage(Message msg)  {
    		if(android.os.Build.VERSION.SDK_INT < 16){
				VTActivity.layout.setBackgroundDrawable(null);
			}
			else{
				VTActivity.layout.setBackground(null);
			}
    	}
    };
	
	Handler invalidateHandler = new Handler()  {
    	public void handleMessage(Message msg)  {
    		myView.invalidate();
    	}
    };
    
    static Handler toastHandler = new Handler()  {
    	public void handleMessage(Message msg)  {
    		if(msg!=null){
    			Toast.makeText(VTActivity.mContext, msg.obj.toString(), Toast.LENGTH_SHORT).show();
    		}
    	}
    };
}
