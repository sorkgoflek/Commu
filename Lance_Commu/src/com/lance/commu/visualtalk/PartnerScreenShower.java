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
	
	//����� ȭ�鿡 �׷����� ������ ǥ���ϱ� ���� ������
	View myView;
	float otherX, otherY;
	Path otherPath;
	Paint otherPaint;
	float changeRateX, changeRateY;//��� ȭ����� ����
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
	
	//�ؼ�, �� ��ũ���� �׸�
	@SuppressLint("NewApi")
	public void run(){
		running=true;
		
		int count=0;
		
		//���� ��ũ�� ����� ����, Ȯ���� ������ �ؾ���
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
			
			try {sleep(1);} catch (InterruptedException e) {}//�̰� �־�� �� ���ư�
			
			synchronized(dataList){//�ҿ���� �׷��� �ϴ°� ���� ��
				if(dataList.isEmpty()==false){

					String data=dataList.pollFirst().toString();//������ ���ÿ� ������ ���� 
					
					int data_int;
					int othersize_x, othersize_y;
					float data_float;
					float x, y;
					
					Message msg;
					
					Log.i("NetworkWithVT", "pss class - �ϳ� ���� "+data);
					
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
							
							otherPaint.setStrokeWidth(otherPaint.getStrokeWidth()*changeRateX);//���⸦ ������ ���� ����
							
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
							
							if(data_int % 3==0){//�Ϲ� ��
								otherPaint.setXfermode(null);
								otherPaint.setAlpha(0xFF);
								otherPaint.setMaskFilter(null);
							}
							else if(data_int % 3==1){//����(������)
								otherPaint.setXfermode(null);
								otherPaint.setAlpha(0xFF);
								otherPaint.setMaskFilter(VTActivity.mEmboss);
							}
							else if(data_int % 3==2){//��������(��)
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
							msg.obj="������ ȭ���� ��� ������ �������ϴ�.";
							toastHandler.sendMessage(msg);
							
							Log.i("NetworkWithVT", "erase all");
							break;
							
						case DATA_TYPE_BACKGROUND: 
							data_int=Integer.parseInt(data.substring(1));
							
							BackgroundDialog.ImgNumber=data_int;
							
							setBackgroundHandler.sendEmptyMessage(0);
							invalidateHandler.sendEmptyMessage(0);
							
							msg=new Message();
							msg.obj="������ ����� �ٲ���ϴ�.";
							toastHandler.sendMessage(msg);
							
							Log.i("NetworkWithVT", "����̹��� ��ü: " + data_int);
							break;
							
						case DATA_TYPE_FILE: 
							data_String=null;
							data_String=data.substring(1);
							
							Log.i("NetworkWithVT", "data_String.length: "+data_String.length());
							Log.i("NetworkWithVT", "�̹��� ���� ����");
							break;
							
						case DATA_TYPE_FILE_ING: 
							data_String+=data.substring(1);
							
							Log.i("NetworkWithVT", "data_String.length: "+data_String.length());
							Log.i("NetworkWithVT", "�̹��� ���� ��");
							break;
							
						case DATA_TYPE_FILE_END: 
							data_String+=data.substring(1);
							Log.i("NetworkWithVT", "data_String.length: "+data_String.length());
							
							byte data_byte[]=Base64.decode(data_String, Base64.DEFAULT);
							Log.i("NetworkWithVT", "data_byte.length: "+data_byte.length);
							
							msg=new Message();
							if(MyView.change_back(data_byte)){
								msg.obj="�������κ��� �̹����� ���� �޾ҽ��ϴ�.";
							}
							else{
								msg.obj="�������κ��� �߸��� �̹��� ������ �޾ҽ��ϴ�.";
							}
							
							data_byte=null;
							data_String=null;
							
							BackgroundDialog.ImgNumber=0;
							invalidateHandler.sendEmptyMessage(0);
							toastHandler.sendMessage(msg);
							
							Log.i("NetworkWithVT", "�̹��� ���� ��");
							break;
							
						case DATA_TYPE_QUIT: 
							
							VTActivity.turnOff();
							
							Log.i("NetworkWithVT", "������ VT ����");
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
		
		Log.i("NetworkWithVT", "pss�� run ����");
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
