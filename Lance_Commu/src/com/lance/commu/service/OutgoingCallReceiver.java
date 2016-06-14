package com.lance.commu.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class OutgoingCallReceiver extends BroadcastReceiver {
	
	@Override
	public void onReceive(Context context, Intent intent) {//��ȭ �߽Ž� PhoneStateReceiver���� ���� ������
		PhoneStateReceiver.outgoingNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
		Log.i("NetworkWithVT","OutgoingCallReceiver -> outgoingNumber: " + PhoneStateReceiver.outgoingNumber);
	}
}
