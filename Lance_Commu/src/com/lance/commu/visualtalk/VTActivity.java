package com.lance.commu.visualtalk;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;

import com.android.internal.telephony.ITelephony;
import com.lance.commu.intro.R;
import com.lance.commu.network.NetworkManager;

import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnDismissListener;
import android.database.Cursor;
import android.graphics.*;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.*;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

public class VTActivity extends Activity implements ColorPickerDialog.OnColorChangedListener, OnDismissListener {
	
	NetworkManager nm;
	PartnerScreenShower pss;
	
	static boolean on;
	
	static Context mContext;

	static Paint mPaint;
	static MaskFilter mEmboss;
	static MaskFilter mBlur;
	static Point mScreenSize;

	static Activity act;
	static LinearLayout layout ;
	static boolean gal_img;
	private final int REQ_CODE_GALLERY = 100;
	static String path;
	
	// ��ɹ�ư��(why static?)
	static ImageButton pallet_btn;
	static ImageButton penStyle_btn;
	static ImageButton erase_btn;
	static ImageButton camera_btn;
	static ImageButton exit_btn;

	int penStyle=0; // ��ư�ϳ��� ��, ����, �� ��� �����ȯ��ų ����
	
	Bitmap pen;
	Bitmap blur;
	Bitmap emboss;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// ��üȭ�� ������ ��������
		mScreenSize = new Point();
		getWindowManager().getDefaultDisplay().getSize(mScreenSize);
				
		//Log.i("NetworkWithVT", "����: "+mScreenSize.y+", ����: "+mScreenSize.x);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.vt_activity);//MyView���� mScreenSize�� ����ϱ� ������ ����� Ȯ���� ���� ȣ��Ǿ���
		
		mContext=VTActivity.this;
		act=this;
		layout = (LinearLayout)findViewById(R.id.container);
		
		// �׸��� ����(���� ó�� ���..)
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		mPaint.setColor(0xFFFF0000);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setStrokeWidth(12);
		
		// �׸��� ȿ��(������,����)
		mEmboss = new EmbossMaskFilter(new float[] { 1, 1, 1 }, 0.4f, 6, 3.5f);
		mBlur = new BlurMaskFilter(8, BlurMaskFilter.Blur.NORMAL);

		//�� ��ư ���������� �ٸ� �̹����� ������.
		pen = BitmapFactory.decodeResource(getResources(), R.drawable.pen);
		blur = BitmapFactory.decodeResource(getResources(), R.drawable.spray);
		emboss = BitmapFactory.decodeResource(getResources(), R.drawable.marker);
		
		// ��ɹ�ư
		pallet_btn = (ImageButton) findViewById(R.id.on);
		penStyle_btn = (ImageButton) findViewById(R.id.on1);
		erase_btn = (ImageButton) findViewById(R.id.on2);
		camera_btn = (ImageButton) findViewById(R.id.on3);
		exit_btn = (ImageButton) findViewById(R.id.on4);
		
		//�����ư Ŭ���� ���̾�α� ����.
		pallet_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new ColorPickerDialog(VTActivity.this, VTActivity.this, mPaint.getColor()).show();
			}
		});

		penStyle_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if(mPaint.getXfermode()==null){//������ ���찳 ��尡 �ƴϾ�����
					penStyle++;//�� ��Ÿ�� �ٲٱ�
				}
	
				if(penStyle%3==0){//�Ϲ� ��
					mPaint.setXfermode(null);
					mPaint.setAlpha(0xFF);
					mPaint.setMaskFilter(null);
					
					Message msg=new Message();
					msg.obj="���� ����Դϴ�.";
					toastHandler.sendMessage(msg);
					
					penStyle_btn.setBackgroundResource(R.drawable.pen);
					penStyle_btn.invalidate();
				}			
				else if(penStyle%3==1){//��Ŀ(������)
					mPaint.setXfermode(null);
					mPaint.setAlpha(0xFF);
					mPaint.setMaskFilter(mEmboss);
					
					Message msg=new Message();
					msg.obj="��Ŀ ����Դϴ�.";
					toastHandler.sendMessage(msg);
						
					penStyle_btn.setBackgroundResource(R.drawable.marker);
					penStyle_btn.invalidate();
				}		
				else if(penStyle%3==2){//��������(��)
					mPaint.setXfermode(null);
					mPaint.setAlpha(0xFF);
					mPaint.setMaskFilter(mBlur);
					
					Message msg=new Message();
					msg.obj="�������� ����Դϴ�.";
					toastHandler.sendMessage(msg);

					penStyle_btn.setBackgroundResource(R.drawable.spray);
					penStyle_btn.invalidate();
				}
				
				nm.sendData(PartnerScreenShower.DATA_TYPE_PEN_STYLE, ""+penStyle);
				
				Log.i("NetworkWithVT", "�� ��Ÿ�� ��ü: "+penStyle);
			}
		});

		erase_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
				mPaint.setMaskFilter(null);
						
				nm.sendData(PartnerScreenShower.DATA_TYPE_ERASE, "0");
				
				Message msg=new Message();
				msg.obj="���찳 ����Դϴ�.";
				toastHandler.sendMessage(msg);
						
				Log.i("NetworkWithVT", "���찳 ���");
			}
		});


		erase_btn.setOnLongClickListener(new Button.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				Builder dlg = new AlertDialog.Builder(VTActivity.this);
				
				dlg.setTitle("Commu")
				.setMessage("ȭ�� ��ü�� ����ðڽ��ϱ�?")
				.setIcon(R.drawable.phonebook_lo_half)
				.setPositiveButton("NO",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {}
						}
				)
				.setNegativeButton("YES",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								
								MyView.mBitmap = Bitmap.createBitmap(mScreenSize.x, mScreenSize.y, Bitmap.Config.ARGB_8888);
								MyView.mCanvas = new Canvas(MyView.mBitmap);
								
								View myView=(View) findViewById(R.id.myview);
								myView.invalidate();
								
								nm.sendData(PartnerScreenShower.DATA_TYPE_ERASE_ALL, "0");
								
								Log.i("NetworkWithVT", "��ü �����");
							}
						}
				)
				.show();
				
				return false;
			}
		});
		
		camera_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				CameraDlg dialog = new CameraDlg(mContext);
				dialog.setTitle("��� ������ ����");
				dialog.show();
			}
		});

		exit_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try{
					TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		            Class<?> c = Class.forName(tm.getClass().getName());
		            Method m = c.getDeclaredMethod("getITelephony");
		            m.setAccessible(true);
					ITelephony telephonyService;
		            telephonyService = (ITelephony)m.invoke(tm);
		 
		            telephonyService.endCall();
		            
		            Log.i("NetworkWithVT", "��ȭ ����");
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		});
		
		on=true;

		//��Ʈ��ũ ���� �κ�
		View myView=(View) findViewById(R.id.myview);
		
		nm = NetworkManager.getInstance();
		pss= new PartnerScreenShower(nm.dataList, myView);

		if(pss!=null && pss.isRunning()==false){
			pss.start();
		}
	}
	
	public void onBackPressed(){
		Builder dlg = new AlertDialog.Builder(mContext);
		
		dlg.setTitle("Commu")
		.setMessage("Commu�� �����Ͻðڽ��ϱ�?")
		.setIcon(R.drawable.phonebook_lo_half)
		.setPositiveButton("NO",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {}
				}
		)
		.setNegativeButton("YES",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				}
		)
		.show();
	}
	
	public static boolean isOn(){
		return on;
	}
	
	public static void turnOff(){
		if(act != null){
			Message msg=new Message();
			msg.obj="��밡 Commu�� ������\n3�� �� Commu�� �����մϴ�.";
			toastHandler.sendMessage(msg);
			
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			act.finish();
		}
	}

	private static final int BACK_IMG = Menu.FIRST;
	private static final int SCREEN_SHOT = Menu.FIRST+1;
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, BACK_IMG, 0, "����̹���").setShortcut('3', 'c');
		menu.add(0, SCREEN_SHOT, 0, "��ũ�� ��").setShortcut('3', 'c');
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public boolean onPrepareOptionMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case BACK_IMG:
				View myView=(View) findViewById(R.id.myview);
				
				BackgroundDialog dialog = new BackgroundDialog(VTActivity.this, myView);
				dialog.show();
				
				return true;
			
			case SCREEN_SHOT:
				WriteDlg dlg = new WriteDlg(VTActivity.this);
				dlg.setOnDismissListener(this);
				dlg.setTitle("������ �̹����� �̸��� �Է����ּ���");
				dlg.show();
				
				return true;
		}
		
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container, false);
			return rootView;
		}
	}

	//�÷� ���̾�α׿��� �޾ƿ� �������� ������.
	@Override
	public void colorChanged(int color) {
		mPaint.setColor(color);
		
		nm.sendData(PartnerScreenShower.DATA_TYPE_PEN_COLOR, ""+color);
		
		Log.i("NetworkWithVT", "�� ����: "+color);
	}
	
	static void screenshot(View view, String name) throws Exception {
		view.setDrawingCacheEnabled(true);

		Bitmap screenshot = view.getDrawingCache();

		String filename = name + ".png";
		try {
			String str = Environment.getExternalStorageDirectory() + "/Commu";
			File file = new File(str);
			if (!file.exists()) {
				file.mkdirs();
			}

			File f = new File(str, filename);
			f.createNewFile();
			OutputStream outStream = new FileOutputStream(f);

			screenshot.compress(Bitmap.CompressFormat.PNG, 100, outStream);

			outStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		view.setDrawingCacheEnabled(false);
	}
	
	public void onDestroy() {
		Log.i("NetworkWithVT", "onDestroy() ȣ���");
		
		on=false;
		
		MyView.freeImages();
		BackgroundDialog.ImgNumber=6;
		
		nm.sendData(PartnerScreenShower.DATA_TYPE_QUIT, "0");
		
		if(pss!=null && pss.isRunning()){
			pss.terminate();
			Log.i("NetworkWithVT", "onDestroy()���� pss.terminate() ȣ���");
		}
		
		if(nm!=null && nm.isRunning()){
			nm.terminate();
			Log.i("NetworkWithVT", "onDestroy()���� nm.terminate() ȣ���");
		}
		
		super.onDestroy();
	}

	@Override
	public void onDismiss(DialogInterface dialog) {
		sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
				Uri.parse("file://" + Environment.getExternalStorageDirectory()
						+ "/Commu/")));
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQ_CODE_GALLERY) {
			if (resultCode == RESULT_OK) {
				Uri uri = data.getData();
				path = getRealPathFromURI(uri);
				
				Bitmap bitmap=null;
				try {
					bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
				} catch (FileNotFoundException e) {
					Log.i("NetworkWithVT", "�ùٸ��� ���� ������ ��ġ�Դϴ�. path: " + path);
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				if(bitmap != null){
					ByteArrayOutputStream stream = new ByteArrayOutputStream();
					bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
	                byte[] byteArray = stream.toByteArray();
	                String temp = Base64.encodeToString(byteArray, Base64.DEFAULT);
	                
	                Log.i("NetworkWithVT", "bitmap.getByteCount(): "+bitmap.getByteCount());
	                Log.i("NetworkWithVT", "byteArray.length: " + byteArray.length);
	                Log.i("NetworkWithVT", "temp.length: " + temp.length());
	                
	                //System.out.println(temp);
	                
	                nm.sendData(PartnerScreenShower.DATA_TYPE_FILE, temp);
				}
				
				Log.i("NetworkWithVT", "path: " + path);
				gal_img = true;
				BackgroundDialog.ImgNumber=0;
				
				View myView=(View) findViewById(R.id.myview);
				myView.invalidate();
			}
		}
	}
	
	public String getRealPathFromURI(Uri contentUri) {
		// can post image
		String[] proj = { MediaStore.Images.Media.DATA };

		Cursor cursor = managedQuery(contentUri, proj, // Which columns to
														// return
				null, // WHERE clause; which rows to return (all rows)
				null, // WHERE clause selection arguments (none)
				null); // Order-by clause (ascending by name)

		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

		cursor.moveToFirst();

		return cursor.getString(column_index);
	}
	
	static Handler toastHandler = new Handler(new Handler.Callback(){
    	public boolean handleMessage(Message msg)  {
    		if(msg!=null){
    			Toast.makeText(mContext, msg.obj.toString(), Toast.LENGTH_SHORT).show();
    		}
    		
			return true;
    	}
    });

}
