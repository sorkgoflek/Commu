package com.example.slidingsimplesample;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.os.Bundle;

public class FragmentMainActivity extends BaseActivity {

	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		//		WindowManager.LayoutParams.FLAG_FULLSCREEN);
		ActionBar actionBar = getActionBar();
		actionBar.setIcon(R.drawable.phonebook_lo);
		actionBar.setTitle("Commu");
		
		    
		setContentView(R.layout.fragment_main);

		fragmentReplace(0);
	}
	
	
}