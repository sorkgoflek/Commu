package com.lance.commu.imagetransfer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.UUID;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore.Images.Media;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.lance.commu.extra.CDialog;
import com.lance.commu.fragment.FragmentMainActivity;
import com.lance.commu.intro.IntroActivity;
import com.lance.commu.intro.R;

public class ImageTransferActivity extends Activity {

	Button gallery_button, transfer_button;
	EditText edit_title,edit_content;
	ImageView imageView;
	Uri imageUri;
	Bitmap imgBitmap;
	Bitmap clearBitmap;
	String id ;
	String requestURL1; 
	String uuid;
	String title,content;
	ConnectivityManager connect;
	ProgressDialog mDlg;
	Context mContext;
	long size; // 내가 선택한 파일크기 
	public static int LIMIT_SIZE = 1553000; //대략 1.5메가 
	public static boolean serverCheck = false;
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.imagetransfer);
			
		ActionBar actionBar = getActionBar();
		actionBar.setIcon(R.drawable.phonebook_lo);
		actionBar.setTitle("Commu");
		
		gallery_button = (Button)findViewById(R.id.gallery_button);
		transfer_button = (Button)findViewById(R.id.transfer_button);
		
		imageView = (ImageView) findViewById(R.id.imageView);
		requestURL1 = IntroActivity.url+"/lance/androidImageTransfer.jsp";
		connect = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE); //인터넷이 연결되어있나 확인
		
		//제목과 내용
		edit_title = (EditText)findViewById(R.id.title);
		edit_content = (EditText)findViewById(R.id.content);
		
		
		//내가 접속한 아이디값 넘겨받음 
		Intent intent = getIntent();
		id = intent.getStringExtra("id");	
		
		gallery_button.setOnClickListener(new View.OnClickListener() { //이미지버튼을 클릭시 	
			
				@Override
				public void onClick(View v) {
					//기존 이미지를 지우고
					imageView.setImageBitmap(null);
					
					//갤러리로 이동
					Intent intent = new Intent(Intent.ACTION_PICK);
					intent.setType("image/*");
					startActivityForResult(intent, 0);

				}		
			});//end onClick()
		
		
		transfer_button.setOnClickListener(new View.OnClickListener() {//전송 버튼을 클릭시 
				
				@Override
				public void onClick(View v) {
					
					if(imageUri != null){//사진을 선택하고 난후 작업 
						
						size = doSaveFile(); // 내가 선택한 사진을 /sdcard/webtransfer에 저장시킴
						System.out.println("파일 사이즈 : "+size);
						 
						
						
						
						 title = edit_title.getText().toString();
						 content = edit_content.getText().toString();
						
						 	if(title.length() ==0 || title.trim().length() ==0){
								Toast toast = Toast.makeText(ImageTransferActivity.this , "제목을 입력하세요", Toast.LENGTH_SHORT);
								toast.show();
								edit_title.requestFocus();
							}
							
							else if(content.length() ==0 || content.trim().length() ==0){
								Toast toast = Toast.makeText(ImageTransferActivity.this , "내용을 입력하세요", Toast.LENGTH_SHORT);
								toast.show();
								edit_content.requestFocus();
							}
						 	
						 	else if(size > LIMIT_SIZE){
						 		Toast toast = Toast.makeText(ImageTransferActivity.this , "이미지 파일 프레임 크기가 초과되었습니다", Toast.LENGTH_SHORT);
								toast.show();
								imageView.setImageBitmap(null);
						 	}
						 	
						 
							else{ // 제목과 내용을 모두 입력했을 시 그리고 용량이 통과됐을시 
								
								//인터넷이 연결돼 있나 확인 
								if(connect.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED 
								|| connect.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED	
								 ){
									new Networking().execute(); //이미지(UUID) , 제목 , 내용 , 내가 접속한 아이디 값 전송
									CDialog.showLoading(ImageTransferActivity.this); //전송시 다이얼로그 띄우기 
									
								}
								
								else{
									Toast toast = Toast.makeText(ImageTransferActivity.this , "인터넷연결이 끊겼습니다", Toast.LENGTH_SHORT);
									toast.show();
								}
							}
					}
					else{ //갤러리에서 사진을 선택 안했다면 
						Toast toast = Toast.makeText(ImageTransferActivity.this , "이미지를 선택하세요", Toast.LENGTH_SHORT);
						toast.show();
					}
					
				}		
			});//end onClick()
	}// end onCreate
	
	@Override
	@SuppressLint("SdCardPath")
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if(resultCode==RESULT_OK&&requestCode==0){//갤러리호출
			
			imgBitmap = null;
			imageUri = data.getData();//이미지 받아옴
			
			//이미지 비트맵으로 변환
			try {
				imgBitmap = Media.getBitmap(getContentResolver(), imageUri);
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
			
			//이미지 뷰 세팅
			imageView.setImageBitmap(imgBitmap);
			imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
		}
	}
	
	private class Networking extends AsyncTask<URL, Integer, String>{

		@Override
		protected void onPreExecute() { 
			//doInBackground 가 수행되기 전, 즉 AsyncTask.execute()	를 호출했을때 가정 먼저 수행되는 녀석 
			//보통 초기화 작업을 해준다
			super.onPreExecute();
			
		}
		
		@Override
		protected String doInBackground(URL... params) {
			//별개의 스레드에서 작업을 실행함 
			//AsyncTask.execute(params)를 통해서 수행이 됌 
			
			//리턴된 결과값을 reslut에 담아
			doFileUpload();
			onCancelled();
			return "";
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			//doInBackground에서 일처리를 하다가 중간에 UI 스레드에서 처리할 일이 있는 경우 호출됨 
			//doInBackground에서 수행되는 내용은 UI 스레드에서 작동함 
			//doInBackground에서 명시적으로 publishProgress( progress ) 를 호출 해주었을때 수행 
			super.onProgressUpdate(values);
		}
		
		@Override
		protected void onPostExecute(String result) {
			//doInBackground의 처리가 모두 끝나고 해당 메소드가 return 되었을때 그 리턴값이 여기로 오면서 이 메소드가 실행됨
			//AsyncTask가 취소되거나 예외 발생시에 parameter 로 null 이 들어옴
			//이 onPostExecute 의 내용은 UIThread에서 수행됨
			super.onPostExecute(result);
				//결과값이 ServerDown이면 서버가 다운됐다고 토스트 띄워 
				if(serverCheck){
					Toast toast = Toast.makeText(ImageTransferActivity.this , "전송이 실패되었습니다", Toast.LENGTH_LONG);
					toast.show();
				}
				else{//정상적으로 완료되었다면 정상완료 토스트 띄워
					Toast toast2 = Toast.makeText(ImageTransferActivity.this , "전송이 완료되었습니다", Toast.LENGTH_LONG);
					toast2.show();
					
					Intent intent = new Intent(ImageTransferActivity.this, FragmentMainActivity.class);
					startActivity(intent);
					finish();
				}
			}
		
		@Override
		protected void onCancelled() {
			//AsyncTask를 강제로 취소했을때 호출
			super.onCancelled();
			}
		}
	
	@SuppressLint("SdCardPath")
	public long doSaveFile(){
		File path = new File("/sdcard/webtransfer");
		FileOutputStream out = null;
		File f;
		long size = 0;
		if (!path.isDirectory()) {
			path.mkdirs();
		}
		
		try {
			 uuid = UUID.randomUUID().toString();
			 out = new FileOutputStream(
					"/sdcard/webtransfer/"+uuid);
			imgBitmap.compress(Bitmap.CompressFormat.PNG, 70, out);
			
			
			f = new File("/sdcard/webtransfer/"+uuid);
			size = f.length();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.println("FileNotFoundException");
		}finally{
			try {
				out.close();
			} catch (IOException e) {
			
				e.printStackTrace();
			}
		}
		return size;
	}
	
	@SuppressLint("SdCardPath")
	public void doFileUpload(){
		HttpClient httpClient = null;
		try{
			//CDialog.showLoading(ImageTransferActivity.this);
			httpClient = new DefaultHttpClient();
			HttpPost post = new HttpPost(requestURL1);
			
			//서버가 끊겼을경우 5초의 타임을 체크해서 catch문으로 간다 
			//HttpParams param = httpClient.getParams();
	    	//HttpConnectionParams.setConnectionTimeout(param, 1000000);
	    	//HttpConnectionParams.setSoTimeout(param, 1000000);
	    	
	    	//핸드폰 안의 webtransfer라는 디렉토리 생성하고 내가 선택한 파일을 uuid형태로 저장 
			File saveFile = new File("/sdcard/webtransfer/"+uuid);
			FileBody bin = new FileBody(saveFile);
			
			StringBody loginId = new StringBody(id, Charset.forName("utf-8"));
			StringBody uuId = new StringBody(uuid,Charset.forName("utf-8"));
			StringBody transferTitle = new StringBody(title,Charset.forName("utf-8"));
			StringBody transferContent = new StringBody(content,Charset.forName("utf-8"));
			
			//멀티파트엔티티로 웹에 전송 
			MultipartEntity multipart = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
			multipart.addPart("images", bin); //내가 선택한 이미지 
			multipart.addPart("id", loginId); // 내가 로그인한 아이디값
			multipart.addPart("uuid", uuId); //내가 선택한 이미지 파일명 
			multipart.addPart("title", transferTitle); //입력한 제목 
			multipart.addPart("content", transferContent);// 입력한 내용 
			//multipart.getContentLength();
			post.setEntity(multipart);
			HttpResponse response = httpClient.execute(post);
			HttpEntity resEntity = response.getEntity();
			//System.out.println(resEntity);
			//정상적으로 전송 완료되면 serverCheck에 completed 문자열 집어넣어 
			//serverCheck = "completed";
				
		}catch(Exception e){
			
			e.printStackTrace();
			System.out.println("doFileUpload error");
			//서버가 꺼졌을경우 여기로 오게된다. 그래서 serverCheck이라는 문자열에 ServerDown 집어넣어 
			serverCheck = true;
		}
		finally{
			httpClient.getConnectionManager().shutdown();
			}
		}	
	//return serverCheck;
	
	//액티비티 꺼졌을때 다이얼로그도 꺼
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		CDialog.hideLoading();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		
		//이미지 전송 페이지 취소할때는 메모리 환원
		if(imgBitmap != null){
			imgBitmap.recycle();
			imgBitmap =null;
		}
		super.onDestroy();   

	}




}
