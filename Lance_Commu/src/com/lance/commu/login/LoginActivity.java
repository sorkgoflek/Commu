package com.lance.commu.login;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.lance.commu.extra.BackPressCloseHandler;
import com.lance.commu.extra.CDialog;
import com.lance.commu.fragment.FragmentMainActivity;
import com.lance.commu.intro.IntroActivity;
import com.lance.commu.intro.R;
import com.lance.commu.signup.SignUpActivity;

public class LoginActivity extends Activity {

	Button insert_button, login_button;
	EditText edit_id,edit_pass;
	String id; // 내가 입력하는 아이디들 (옳은 아이디일수 있고 틀릴수 있는 아이디값들) 
	public static String rememberRealId; //로그인 된 후 세션처럼 기억하고 있어야 할 아이디 
	String pass;
	String requestURL1; 
	String login;
	ConnectivityManager connect;
	SharedPreferences sp;
	private BackPressCloseHandler backPressCloseHandler;
	public static boolean flag = false;
	HttpClient client;
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		
		ActionBar actionBar = getActionBar();
		actionBar.setIcon(R.drawable.phonebook_lo);
		actionBar.setTitle("Commu");
		
		
		backPressCloseHandler = new BackPressCloseHandler(this);//두번 뒤로가기 시 종료 작업
		
		insert_button = (Button)findViewById(R.id.insert_button);
		login_button = (Button)findViewById(R.id.login_button);
		
		edit_id = (EditText)findViewById(R.id.id);
		edit_id.setPrivateImeOptions("defaultInputmode=english;"); //기본 입력창을 영어판으로
		
		edit_pass = (EditText)findViewById(R.id.pass);
		requestURL1 = IntroActivity.url+"/lance/androidLogin.jsp";
		connect = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE); //인터넷이 연결되어있나 확인 

		//자동로그인 체크박스
		findViewById(R.id.checkbox).setOnClickListener
									(new Button.OnClickListener(){
						@Override
						public void onClick(View v){
							checkAutoLogin(v);
						}	
		});
		
		
		insert_button.setOnClickListener(new View.OnClickListener() {
			//로그인 창에서 회원가입 버튼 클릭시 		
				@Override
				public void onClick(View v) {
					startActivity(new Intent(LoginActivity.this,SignUpActivity.class));
					
				}		
			});//end onClick()
		
		
		login_button.setOnClickListener(new View.OnClickListener() {
			//로그인 버튼 클릭시	
				@Override
				public void onClick(View v) {
					
					 id = edit_id.getText().toString();
					 pass = edit_pass.getText().toString();
					 
					//먼저 아이디 패스워드 입력했는지 확인
					if(id.length() ==0 || id.trim().length() ==0){
						Toast toast = Toast.makeText(LoginActivity.this , "아이디를 입력하세요", Toast.LENGTH_SHORT);
						toast.show();
						edit_id.requestFocus();
					}
					
					else if(pass.length() ==0 || pass.trim().length() ==0){
						Toast toast = Toast.makeText(LoginActivity.this , "패스워드를 입력하세요", Toast.LENGTH_SHORT);
						toast.show();
						edit_pass.requestFocus();
					}	
					
					 
					else{//아이디, 패스워드 값을 모두 입력하고 난 후 클릭하면 
							//인터넷이 연결돼 있나 확인 
							if(connect.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED 
							|| connect.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED	
							 ){
								new Networking().execute();
							}
							
							else{
								Toast toast = Toast.makeText(LoginActivity.this , "인터넷연결이 끊겼습니다", Toast.LENGTH_SHORT);
								toast.show();
							}		
					}
				}		
			});//end onClick()
	}//onCreate end 
	
	public void checkAutoLogin(View v){
		CheckBox ch = (CheckBox)findViewById(R.id.checkbox);
		if(ch.isChecked()){//자동로그인이 체크됐다면
			flag = true; //이걸통해 네트워크 통신하면서 아디,비번값 기억하냐 안하냐 차이
		}
		else{//자동로그인이 체크 안됐다면
			flag = false;
		}
	}
	
	@Override
	public void onBackPressed() { 
		//super.onBackPressed();
		backPressCloseHandler.onBackPressed();
	}

	private class Networking extends AsyncTask<URL, Integer, String>{

		@Override
		protected void onPreExecute() { 
			//doInBackground 가 수행되기 전, 즉 AsyncTask.execute()	를 호출했을때 가정 먼저 수행되는 녀석 
			//보통 초기화 작업을 해준다
			super.onPreExecute();	
		}
		
		@Override
		protected String doInBackground(URL... params){
			//별개의 스레드에서 작업을 실행함 
			//AsyncTask.execute(params)를 통해서 수행이 됌 
			String result =null;
			
			try {

					result = sendData(id,pass,requestURL1);
					
			} catch (ClientProtocolException e) {
				
				e.printStackTrace();
				System.out.println("clientprotocol exception");
			} catch (IOException e) {
				
				//서버가 꺼지면 IOException쪽으로 오게됌 
				//그럼 result값에 ServerDown 이라는 문자열을 넣게해 
				return "ServerDown"; 
			}

			onCancelled();
			return result;
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
			
			//만약 result 문자열 값이 ServerDown이면 서버 다운 토스트 띄우고 끝냄 
			if(result.equals("ServerDown")){
				Toast toast = Toast.makeText(LoginActivity.this , "서버가 다운됐습니다", Toast.LENGTH_LONG);
				toast.show();
			}
			else{//서버 다운이 아니라면 정상적으로 파싱해서 결과값 도출해냄
				String[] list;
				list = result.split(">");
				login = list[2].substring(2, 7);
				System.out.println(login);
			
				if(login.equals("notid")){
					Toast toast = Toast.makeText(LoginActivity.this , "아이디를 잘못 입력하셨습니다", Toast.LENGTH_SHORT);
					toast.show();
				}
				
				else if(login.equals("notps")){
					Toast toast = Toast.makeText(LoginActivity.this , "비밀번호를 잘못 입력하셨습니다", Toast.LENGTH_SHORT);
					toast.show();
				}
				
				else if(login.equals("login")){
					Toast toast = Toast.makeText(LoginActivity.this , id+" 님 환영합니다", Toast.LENGTH_SHORT);
					toast.show();
					CDialog.showLoading(LoginActivity.this);
					rememberRealId = id;
					if(flag == true){//자동로그인 체크박스가 체크되어있다면 flag 값이 true
						
						//다음번 자동로그인을 위하여 sharedpreference에 저장
						sp = getSharedPreferences("PreName", MODE_PRIVATE);
						SharedPreferences.Editor editor = sp.edit();
						
						editor.putString("sp_id", id); 
						editor.putString("sp_pw", pass); 
						editor.commit();
						System.out.println("로그인 sp 저장된 id:"+id);
						System.out.println("로그인 sp 저장된 pass:"+pass);
						
					}
					
					Intent intent = new Intent(LoginActivity.this,FragmentMainActivity.class);
					startActivity(intent);
					finish();

					}
			}
		}

		@Override
		protected void onCancelled() {
			//AsyncTask를 강제로 취소했을때 호출
			super.onCancelled();
		}
	}
	
	//액티비티 꺼졌을때 다이얼로그도 꺼
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		CDialog.hideLoading();
	}

	private String sendData(String id, String pass, String requestURL1) throws ClientProtocolException, IOException{
		
		client = new DefaultHttpClient();
		HttpPost request = makeHttpPost(id, pass, requestURL1);
		ResponseHandler<String> reshandler = new BasicResponseHandler();
		String result = client.execute(request, reshandler);
		return result;
	}
		
	private HttpPost makeHttpPost(String id, String pass, String requestURL1){
		
		HttpPost request = new HttpPost(requestURL1);
		List<NameValuePair> dataList = new ArrayList<NameValuePair>();
    	
		//서버가 끊겼을경우 5초의 타임을 체크
		HttpParams param = client.getParams();
    	HttpConnectionParams.setConnectionTimeout(param, 5000);
    	HttpConnectionParams.setSoTimeout(param, 5000);
    	
		dataList.add(new BasicNameValuePair("id", id));
    	dataList.add(new BasicNameValuePair("pass", pass));
    	request.setEntity(makeEntity(dataList));
    	return request;
	}
	
	private HttpEntity makeEntity(List<NameValuePair> dataList){
		
		HttpEntity result = null;
		
		try{
			result = new UrlEncodedFormEntity(dataList,"UTF-8");
		}catch(UnsupportedEncodingException e){
			e.printStackTrace();
			System.out.println("언서포티드인코딩 exception");
		}
		return result;
	}
}
