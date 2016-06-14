package com.lance.commu.service;

import com.lance.commu.intro.R;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

public class AlertDialogActivity extends Activity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i("NetworkWithVT", "Dlg »£√‚");
		  
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.alertdialog);

		Button checkButton = (Button)findViewById(R.id.check);
		checkButton.setOnClickListener(new DlgOnClickListener());
	}
 
	private class DlgOnClickListener implements OnClickListener {
		public void onClick(View v) {
			switch(v.getId()){
			  	case R.id.check:
			  		finish();
			  		break;
			  }
		}
	}
}

