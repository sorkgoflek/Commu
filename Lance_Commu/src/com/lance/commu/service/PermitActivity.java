package com.lance.commu.service;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class PermitActivity extends Activity {
	
	static boolean permit=false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.i("NetworkWithVT", "PermitActivity onCreate()");

		permit=true;
		
		finish();
	}

	protected void onDestroy(){
		super.onDestroy();
		Log.i("NetworkWithVT", "PermitActivity onDestroy");
	} 
}

