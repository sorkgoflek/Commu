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
			//��ȭ�� �︮��(�����ڸ� ����)
			if (phoneState.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
				Log.i("NetworkWithVT","PhoneStateReceiver -> EXTRA_STATE_RINGING");
				
				incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
				Log.i("NetworkWithVT","PhoneStateReceiver -> incomingNumber: "+incomingNumber);
			}
			
			//�߽���: ��ȭ�� �ɸ�, ������: ��ȭ�� ������
			if (phoneState.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
				Log.i("NetworkWithVT","PhoneStateReceiver -> EXTRA_STATE_OFFHOOK");
				
				//�߰� 1030
				NetworkManager nm = NetworkManager.getInstance();
				if(nm.isRunning()){
					Log.e("NetworkWithVT", "�̹� vt�� �������Դϴ�.");

					incomingNumber=null;
					outgoingNumber=null;
					
					return;
				}
				//�������
				
				Intent serviceIntent = new Intent(context, CommuService.class);
				
				serviceIntent.putExtra("incomingNumber", incomingNumber);//incomingNumber�� null�̸� �߽���, null�� �ƴϸ� ������
				serviceIntent.putExtra("outgoingNumber", outgoingNumber);//outgoingNumber�� null�̸� ������, null�� �ƴϸ� �߽���
					
				//���� ��� ����
				CommuService.step=CommuService.DB_CHECK_STEP;
				
				context.stopService(serviceIntent);//����
				
				context.startService(serviceIntent);//����
				
				incomingNumber=null;
				outgoingNumber=null;
			}
			
			//��ȭ�� ������
			if (phoneState.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
				Log.i("NetworkWithVT","PhoneStateReceiver -> EXTRA_STATE_IDLE");
				
				if(VTActivity.isOn()){
					Toast.makeText(context, "������ ��ȭ�� ����Ǿ����ϴ�.", Toast.LENGTH_SHORT).show();
				}
				
				//���񽺰� �����ϵ��� ��
				CommuService.step=CommuService.EXIT_STEP;
			}
		}
	}
}
 
