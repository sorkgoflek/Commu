package com.lance.commu.visualtalk;

import com.lance.commu.intro.R;
import com.lance.commu.network.NetworkManager;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

@SuppressLint("HandlerLeak") public class BackgroundDialog extends Dialog implements OnClickListener {
	static int ImgNumber;
	
	ImageButton btn1;
	ImageButton btn2;
	ImageButton btn3;
	ImageButton btn4;
	ImageButton btn5;
	ImageButton btn6;
	
	View myView;
	
	NetworkManager nm;
	
	public BackgroundDialog(Context context, View myView) {
		super(context);

		setContentView(R.layout.background_dlg);

		btn1 = (ImageButton) findViewById(R.id.back1_img);
		btn2 = (ImageButton) findViewById(R.id.back2_img);
		btn3 = (ImageButton) findViewById(R.id.back3_img);
		btn4 = (ImageButton) findViewById(R.id.back4_img);
		btn5 = (ImageButton) findViewById(R.id.back5_img);
		btn6 = (ImageButton) findViewById(R.id.back6_img);

		btn1.setOnClickListener(this);
		btn2.setOnClickListener(this);
		btn3.setOnClickListener(this);
		btn4.setOnClickListener(this);
		btn5.setOnClickListener(this);
		btn6.setOnClickListener(this);


		this.myView=myView;
		
		nm=NetworkManager.getInstance();
	}

	
	@Override
	public void onClick(View v) {
	
		switch(v.getId()){
			case R.id.back1_img:
				ImgNumber=1;
				break;
			case R.id.back2_img:
				ImgNumber=2;
				break;
			case R.id.back3_img:
				ImgNumber=3;
				break;
			case R.id.back4_img:
				ImgNumber=4;
				break;
			case R.id.back5_img:
				ImgNumber=5;
				break;
			case R.id.back6_img:
				ImgNumber=6;
				break;
		}
		
		invalidateHandler.sendEmptyMessage(0);
		
		nm.sendData(PartnerScreenShower.DATA_TYPE_BACKGROUND, "" + ImgNumber);
		
		Log.i("NetworkWithVT", "배경이미지 교체: " + ImgNumber);
		
		dismiss();
	}
	

	Handler invalidateHandler = new Handler()  {
    	@SuppressWarnings("deprecation")
		@SuppressLint("NewApi") public void handleMessage(Message msg)  {
    		if(android.os.Build.VERSION.SDK_INT < 16){
				VTActivity.layout.setBackgroundDrawable(null);
			}
			else{
				VTActivity.layout.setBackground(null);
			}
    		
    		myView.invalidate();
    	}
    };
}
