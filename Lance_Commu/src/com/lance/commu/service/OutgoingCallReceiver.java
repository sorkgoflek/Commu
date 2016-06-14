package com.lance.commu.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class OutgoingCallReceiver extends BroadcastReceiver {
	
	@Override
	public void onReceive(Context context, Intent intent) {//전화 발신시 PhoneStateReceiver보다 빨리 반응함
		PhoneStateReceiver.outgoingNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
		Log.i("NetworkWithVT","OutgoingCallReceiver -> outgoingNumber: " + PhoneStateReceiver.outgoingNumber);
	}
}
