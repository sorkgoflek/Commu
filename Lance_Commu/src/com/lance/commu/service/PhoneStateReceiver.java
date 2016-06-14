package com.lance.commu.service;
 
import com.lance.commu.network.NetworkManager;
import com.lance.commu.visualtalk.VTActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class PhoneStateReceiver extends BroadcastReceiver {
	
	static String incomingNumber=null;
	static String outgoingNumber=null;
	
	@Override
	public void onReceive(Context context, Intent intent) {

		// Check phone state
		String phoneState = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
		
		if(phoneState!=null){
			//전화가 울리면(수신자만 가능)
			if (phoneState.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
				Log.i("NetworkWithVT","PhoneStateReceiver -> EXTRA_STATE_RINGING");
				
				incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
				Log.i("NetworkWithVT","PhoneStateReceiver -> incomingNumber: "+incomingNumber);
			}
			
			//발신자: 전화를 걸면, 수신자: 전화를 받으면
			if (phoneState.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
				Log.i("NetworkWithVT","PhoneStateReceiver -> EXTRA_STATE_OFFHOOK");
				
				//추가 1030
				NetworkManager nm = NetworkManager.getInstance();
				if(nm.isRunning()){
					Log.e("NetworkWithVT", "이미 vt가 진행중입니다.");

					incomingNumber=null;
					outgoingNumber=null;
					
					return;
				}
				//여기까지
				
				Intent serviceIntent = new Intent(context, CommuService.class);
				
				serviceIntent.putExtra("incomingNumber", incomingNumber);//incomingNumber가 null이면 발신자, null이 아니면 수신자
				serviceIntent.putExtra("outgoingNumber", outgoingNumber);//outgoingNumber가 null이면 수신자, null이 아니면 발신자
					
				//서비스 모드 설정
				CommuService.step=CommuService.DB_CHECK_STEP;
				
				context.stopService(serviceIntent);//끄고
				
				context.startService(serviceIntent);//시작
				
				incomingNumber=null;
				outgoingNumber=null;
			}
			
			//통화가 끝나면
			if (phoneState.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
				Log.i("NetworkWithVT","PhoneStateReceiver -> EXTRA_STATE_IDLE");
				
				if(VTActivity.isOn()){
					Toast.makeText(context, "상대와의 통화가 종료되었습니다.", Toast.LENGTH_SHORT).show();
				}
				
				//서비스가 종료하도록 함
				CommuService.step=CommuService.EXIT_STEP;
			}
		}
	}
}
 
