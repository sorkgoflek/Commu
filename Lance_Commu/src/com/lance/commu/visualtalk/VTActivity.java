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
	
	// 기능버튼들(why static?)
	static ImageButton pallet_btn;
	static ImageButton penStyle_btn;
	static ImageButton erase_btn;
	static ImageButton camera_btn;
	static ImageButton exit_btn;

	int penStyle=0; // 버튼하나로 펜, 매직, 블러 등등 기능전환시킬 변수
	
	Bitmap pen;
	Bitmap blur;
	Bitmap emboss;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// 전체화면 사이즈 가져오기
		mScreenSize = new Point();
		getWindowManager().getDefaultDisplay().getSize(mScreenSize);
				
		//Log.i("NetworkWithVT", "세로: "+mScreenSize.y+", 가로: "+mScreenSize.x);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.vt_activity);//MyView에서 mScreenSize를 사용하기 때문에 사이즈가 확정된 다음 호출되야함
		
		mContext=VTActivity.this;
		act=this;
		layout = (LinearLayout)findViewById(R.id.container);
		
		// 그리기 설정(끝선 처리 등등..)
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		mPaint.setColor(0xFFFF0000);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setStrokeWidth(12);
		
		// 그리기 효과(엠보싱,블러링)
		mEmboss = new EmbossMaskFilter(new float[] { 1, 1, 1 }, 0.4f, 6, 3.5f);
		mBlur = new BlurMaskFilter(8, BlurMaskFilter.Blur.NORMAL);

		//펜 버튼 누를때마다 다른 이미지를 보여줌.
		pen = BitmapFactory.decodeResource(getResources(), R.drawable.pen);
		blur = BitmapFactory.decodeResource(getResources(), R.drawable.spray);
		emboss = BitmapFactory.decodeResource(getResources(), R.drawable.marker);
		
		// 기능버튼
		pallet_btn = (ImageButton) findViewById(R.id.on);
		penStyle_btn = (ImageButton) findViewById(R.id.on1);
		erase_btn = (ImageButton) findViewById(R.id.on2);
		camera_btn = (ImageButton) findViewById(R.id.on3);
		exit_btn = (ImageButton) findViewById(R.id.on4);
		
		//색상버튼 클릭시 다이얼로그 생성.
		pallet_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new ColorPickerDialog(VTActivity.this, VTActivity.this, mPaint.getColor()).show();
			}
		});

		penStyle_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if(mPaint.getXfermode()==null){//이전에 지우개 모드가 아니었으면
					penStyle++;//펜 스타일 바꾸기
				}
	
				if(penStyle%3==0){//일반 펜
					mPaint.setXfermode(null);
					mPaint.setAlpha(0xFF);
					mPaint.setMaskFilter(null);
					
					Message msg=new Message();
					msg.obj="볼펜 모드입니다.";
					toastHandler.sendMessage(msg);
					
					penStyle_btn.setBackgroundResource(R.drawable.pen);
					penStyle_btn.invalidate();
				}			
				else if(penStyle%3==1){//마커(엠보싱)
					mPaint.setXfermode(null);
					mPaint.setAlpha(0xFF);
					mPaint.setMaskFilter(mEmboss);
					
					Message msg=new Message();
					msg.obj="마커 모드입니다.";
					toastHandler.sendMessage(msg);
						
					penStyle_btn.setBackgroundResource(R.drawable.marker);
					penStyle_btn.invalidate();
				}		
				else if(penStyle%3==2){//스프레이(블러)
					mPaint.setXfermode(null);
					mPaint.setAlpha(0xFF);
					mPaint.setMaskFilter(mBlur);
					
					Message msg=new Message();
					msg.obj="스프레이 모드입니다.";
					toastHandler.sendMessage(msg);

					penStyle_btn.setBackgroundResource(R.drawable.spray);
					penStyle_btn.invalidate();
				}
				
				nm.sendData(PartnerScreenShower.DATA_TYPE_PEN_STYLE, ""+penStyle);
				
				Log.i("NetworkWithVT", "펜 스타일 교체: "+penStyle);
			}
		});

		erase_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
				mPaint.setMaskFilter(null);
						
				nm.sendData(PartnerScreenShower.DATA_TYPE_ERASE, "0");
				
				Message msg=new Message();
				msg.obj="지우개 모드입니다.";
				toastHandler.sendMessage(msg);
						
				Log.i("NetworkWithVT", "지우개 모드");
			}
		});


		erase_btn.setOnLongClickListener(new Button.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				Builder dlg = new AlertDialog.Builder(VTActivity.this);
				
				dlg.setTitle("Commu")
				.setMessage("화면 전체를 지우시겠습니까?")
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
								
								Log.i("NetworkWithVT", "전체 지우기");
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
				dialog.setTitle("배경 선택할 도구");
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
		            
		            Log.i("NetworkWithVT", "통화 종료");
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		});
		
		on=true;

		//네트워크 관련 부분
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
		.setMessage("Commu를 종료하시겠습니까?")
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
			msg.obj="상대가 Commu를 종료해\n3초 뒤 Commu를 종료합니다.";
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
		menu.add(0, BACK_IMG, 0, "배경이미지").setShortcut('3', 'c');
		menu.add(0, SCREEN_SHOT, 0, "스크린 샷").setShortcut('3', 'c');
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
				dlg.setTitle("저장할 이미지의 이름을 입력해주세요");
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

	//컬러 다이얼로그에서 받아온 색상으로 설정함.
	@Override
	public void colorChanged(int color) {
		mPaint.setColor(color);
		
		nm.sendData(PartnerScreenShower.DATA_TYPE_PEN_COLOR, ""+color);
		
		Log.i("NetworkWithVT", "색 설정: "+color);
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
		Log.i("NetworkWithVT", "onDestroy() 호출됨");
		
		on=false;
		
		MyView.freeImages();
		BackgroundDialog.ImgNumber=6;
		
		nm.sendData(PartnerScreenShower.DATA_TYPE_QUIT, "0");
		
		if(pss!=null && pss.isRunning()){
			pss.terminate();
			Log.i("NetworkWithVT", "onDestroy()에서 pss.terminate() 호출됨");
		}
		
		if(nm!=null && nm.isRunning()){
			nm.terminate();
			Log.i("NetworkWithVT", "onDestroy()에서 nm.terminate() 호출됨");
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
					Log.i("NetworkWithVT", "올바르지 않은 파일의 위치입니다. path: " + path);
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
