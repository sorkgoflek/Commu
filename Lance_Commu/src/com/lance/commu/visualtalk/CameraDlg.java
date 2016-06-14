package com.lance.commu.visualtalk;

import com.lance.commu.intro.R;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class CameraDlg extends Dialog implements OnClickListener {

	Button camera;
	Button gallery;

	//MainActivity dlg;
	VTActivity dlg;
	
	int REQ_CODE_GALLERY=100;
	Context context;
	
	public CameraDlg(Context context) {
		super(context);
		this.context = context;
		setContentView(R.layout.camera);

		dlg = new VTActivity();
		
		camera = (Button) findViewById(R.id.camera);
		gallery = (Button) findViewById(R.id.gallery);

		camera.setOnClickListener(this);
		gallery.setOnClickListener(this);
	}

	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
			case R.id.camera:
	            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	            getContext().startActivity(intent);
	            
				break;
				
			case R.id.gallery:
				Intent i = new Intent(Intent.ACTION_GET_CONTENT);
				i.setType("image/*");
				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				((VTActivity)context).startActivityForResult(i, REQ_CODE_GALLERY);
				dismiss();
				
				break;
		}
	}
}
