package com.lance.commu.visualtalk;

import com.lance.commu.intro.R;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class WriteDlg extends Dialog implements OnClickListener {
	Button id_ok;
	Button id_cancle;
	
	EditText file_name;
	String name;
	public WriteDlg(Context context) {
		super(context);

		setContentView(R.layout.write);

		id_ok = (Button)findViewById(R.id.okbtn);
		id_cancle = (Button)findViewById(R.id.canbtn);
		file_name = (EditText)findViewById(R.id.file_name);
		
		id_ok.setOnClickListener(this);
		id_cancle.setOnClickListener(this);
	}

	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.okbtn:
			name = file_name.getText().toString();
						
			try {
				VTActivity.screenshot(VTActivity.act.getWindow().getDecorView(), name);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			dismiss();
			
			break;
		case R.id.canbtn:
			dismiss();
			break;
			
		}
	}
}
