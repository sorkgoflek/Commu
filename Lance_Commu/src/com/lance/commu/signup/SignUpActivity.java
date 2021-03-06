package com.lance.commu.signup;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.lance.commu.intro.IntroActivity;
import com.lance.commu.intro.R;
import com.lance.commu.login.LoginActivity;
import com.lance.commu.logo.LogoActivity;

@SuppressLint("NewApi")
public class SignUpActivity extends Activity {

	EditText edit_id,edit_pass,edit_name; //,edit_phone_number;
	Button insert_button, idCheck_button;
	RadioButton rb_man,rb_woman;
	String id;
	String pass;
	String name;
	String phone_number; //최종적으로 네트워크 통해 보내는 핸펀번호 
	String PhoneNumber; //내 핸펀번호 자동으로 얻어와서 저장되는 변수
	String gender;
	String idCheck; //아이디가 중복인지 체크하는 변수 
	String idCheck_Hangel; //1차로 아이디 한글체크
	static boolean idPass = false; //1차 아이디 한글통과 여부확인
	String requestURL1;
	String requestURL2;
	ConnectivityManager connect;
	HttpClient client1;
	HttpClient client2;
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.signup);
		  
		ActionBar actionBar = getActionBar();
		actionBar.setIcon(R.drawable.phonebook_lo);
		actionBar.setTitle("Commu");
		
		/*if(android.os.Build.VERSION.SDK_INT >9){
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}	*/
		
		edit_id = (EditText)findViewById(R.id.id);
		edit_id.setPrivateImeOptions("defaultInputmode=english;"); //기본 입력창을 영어판으로
		
		edit_pass = (EditText)findViewById(R.id.pass);
		edit_name = (EditText)findViewById(R.id.name);
		//edit_phone_number  = (EditText)findViewById(R.id.phone_number);
		rb_man = (RadioButton)findViewById(R.id.radiobutton_man);
		rb_woman = (RadioButton)findViewById(R.id.radiobutton_woman);
		
		insert_button = (Button)findViewById(R.id.insert_button);
		idCheck_button = (Button)findViewById(R.id.idCheck_button);
		
		requestURL1 = IntroActivity.url+"/lance/androidJoin.jsp";
		requestURL2 = IntroActivity.url+"/lance/androidIdCheck.jsp";
		
		connect = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE); //인터넷이 연결되어있나 확인 
		
		//내 핸드폰 번호 가져오기 
		TelephonyManager systemService = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
		PhoneNumber = systemService.getLine1Number();
		PhoneNumber = PhoneNumber.substring(PhoneNumber.length()-10,PhoneNumber.length());
		PhoneNumber="0"+PhoneNumber;
		PhoneNumber = PhoneNumberUtils.formatNumber(PhoneNumber);
		//edit_phone_number.setHint(PhoneNumber);
		//edit_phone_number.setEnabled(false); 
		
		//////////////////////////////////////////////////////////////
		idCheck_button.setOnClickListener(new View.OnClickListener() {
		//아이디 중복체크 버튼 클릭시 	
			@Override
			public void onClick(View v) {
				
				id = edit_id.getText().toString(); //내가 입력한 아이디 값 
				
				if(id.length() ==0 || id.trim().length() ==0){
					Toast toast = Toast.makeText(SignUpActivity.this , "아이디를 입력하세요", Toast.LENGTH_SHORT);
					toast.show();
					edit_id.requestFocus();
				}
				
				else{
					//1차로 한글 걸러냄 
					for(int i=0;i<id.length();i++){
					     if(Character.getType(id.charAt(i)) == 5){ 
					    	 System.out.println("한글은 안됩니다");
					    	 Toast toast = Toast.makeText(SignUpActivity.this , "영문 or 숫자 or 영문+숫자 만 가능", Toast.LENGTH_SHORT);
							 toast.show();
					    	 idPass = false;
					     	break;
					     }
					     else{
					    	 idCheck_Hangel = id;
					    	 idPass = true;
					     }
					  }
					
					if(idPass == true){//1차로 걸러진것만 
						//2차로 특수문자 걸러냄 
						Pattern p = Pattern.compile(".*[^가-힣a-zA-Z0-9].*");
						Matcher m = p.matcher(idCheck_Hangel);
				   
						if(m.matches()) {
							Toast toast = Toast.makeText(SignUpActivity.this , "영문 or 숫자 or 영문+숫자 만 가능", Toast.LENGTH_SHORT);
							toast.show();
							System.out.println("영문 or 숫자 or 영문+숫자 만 가능");
						} 
						else {
						        System.out.println("아이디 가능 이제 중복확인만하면됌");
						
						      //인터넷이 연결돼 있나 확인 
								if(connect.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED 
								|| connect.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED	
								 ){
									new Networking2().execute();
								}
								
								else{
									Toast toast = Toast.makeText(SignUpActivity.this , "인터넷연결이 끊겼습니다", Toast.LENGTH_SHORT);
									toast.show();
								}	
						
						}
					}

			}

		}		
	});//end onClick()
		

		/////////////////////////////////////////////////////////////
		insert_button.setOnClickListener(new View.OnClickListener() {
		//회원가입 버튼 클릭시 		
				@Override
				public void onClick(View v) {
					
					 id = edit_id.getText().toString();
					 pass = edit_pass.getText().toString();
					 name = edit_name.getText().toString();
					 //phone_number = edit_phone_number.getText().toString();
					 phone_number = PhoneNumber;
					 if(rb_man.isChecked())
						gender="남자";			
					if(rb_woman.isChecked())
						gender="여자";

					 
					 
					if(id.length() ==0 || id.trim().length() ==0){
						Toast toast = Toast.makeText(SignUpActivity.this , "아이디를 입력하세요", Toast.LENGTH_SHORT);
						toast.show();
						edit_id.requestFocus();
					}
					
					else if(pass.length() ==0 || pass.trim().length() ==0){
						Toast toast = Toast.makeText(SignUpActivity.this , "패스워드를 입력하세요", Toast.LENGTH_SHORT);
						toast.show();
						edit_pass.requestFocus();
					}
					
					else if(name.length() ==0 || name.trim().length() ==0){
						Toast toast = Toast.makeText(SignUpActivity.this , "이름을 입력하세요", Toast.LENGTH_SHORT);
						toast.show();
						edit_name.requestFocus();
					}

					else{
						//1차로 한글 걸러냄 
						for(int i=0;i<id.length();i++){
						     if(Character.getType(id.charAt(i)) == 5){ 
						    	 System.out.println("한글은 안됩니다");
						    	 Toast toast = Toast.makeText(SignUpActivity.this , "영문 or 숫자 or 영문+숫자 만 가능", Toast.LENGTH_SHORT);
								 toast.show();
						    	 idPass = false;
						     	break;
						     }
						     else{
						    	 idCheck_Hangel = id;
						    	 idPass = true;
						     }
						  }
						
						
						if(idPass == true){//1차로 걸러진것만 
							//2차로 특수문자 걸러냄 
							Pattern p = Pattern.compile(".*[^가-힣a-zA-Z0-9].*");
							Matcher m = p.matcher(idCheck_Hangel);
					   
							if(m.matches()) {
								Toast toast = Toast.makeText(SignUpActivity.this , "영문 or 숫자 or 영문+숫자 만 가능", Toast.LENGTH_SHORT);
								toast.show();
								System.out.println("영문 or 숫자 or 영문+숫자 만 가능");
							} 
							else {
							        System.out.println("아이디 가능 이제 중복확인만하면됌");
							
							      //인터넷이 연결돼 있나 확인 
									if(connect.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED 
									|| connect.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED	
									 ){
										new Networking1().execute();
									}
									
									else{
										Toast toast = Toast.makeText(SignUpActivity.this , "인터넷연결이 끊겼습니다", Toast.LENGTH_SHORT);
										toast.show();
									}	
							}		
						}			
					}	
				}
		});//end onClick()
	}//oncreate end
	

	private class Networking1 extends AsyncTask<URL, Integer, String>{
	
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
			String result =null;
			
			try {
					result = sendData(id,pass,name,phone_number,gender,requestURL1);
					
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
				Toast toast = Toast.makeText(SignUpActivity.this , "서버가 다운됐습니다", Toast.LENGTH_LONG);
				toast.show();
			}
			else{//서버 다운이 아니라면 정상적으로 파싱해서 결과값 도출해냄 
				//result 값에는 jsp 내용이 모두 들어가 있다 그중에서 rsult부분만 추출해내야 하는데 ... 
				//여기서 result내용을 쪼개는 작업 실행 
				String[] list;
				list = result.split(">");
				idCheck = list[2].substring(2, 7); // 중복 or 가능  두개의 string 값이 나옴 
				System.out.println(idCheck);
				
				Toast toast_wait = Toast.makeText(SignUpActivity.this , "잠시만 기다려주세요", Toast.LENGTH_SHORT);
				toast_wait.show();
				
				if(idCheck.equals("dupli") ){
					
					Toast toast = Toast.makeText(SignUpActivity.this , "아이디 중복입니다", Toast.LENGTH_SHORT);
					toast.show();
				}
				
				else if(idCheck.equals("duphn")){
					Toast toast = Toast.makeText(SignUpActivity.this , "1인 1계정만 가능합니다", Toast.LENGTH_SHORT);
					toast.show();
				}
				
				else{
					Toast toast = Toast.makeText(SignUpActivity.this , "축하합니다 회원가입이 되셨습니다", Toast.LENGTH_SHORT);
					toast.show();
					startActivity(new Intent(SignUpActivity.this,LoginActivity.class));
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
	///////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private class Networking2 extends AsyncTask<URL, Integer, String>{

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
			String result =null;
			
			try {
				
					result = sendData2(id,requestURL2);
					
			
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
				Toast toast = Toast.makeText(SignUpActivity.this , "서버가 다운됐습니다", Toast.LENGTH_LONG);
				toast.show();
			}
			else{//서버 다운이 아니라면 정상적으로 파싱해서 결과값 도출해냄 
				//result 값에는 jsp 내용이 모두 들어가 있다 그중에서 rsult부분만 추출해내야 하는데 ... 
				//여기서 result내용을 쪼개는 작업 실행 
				String[] list;
				list = result.split(">");
				idCheck = list[2].substring(2, 7); // 중복 or 가능  두개의 string 값이 나옴 
				System.out.println(idCheck);
				
				if(idCheck.equals("dupli") ){
					
					Toast toast = Toast.makeText(SignUpActivity.this , "아이디 중복입니다", Toast.LENGTH_SHORT);
					toast.show();
				}
				
				if(idCheck.equals("possi")){
					Toast toast = Toast.makeText(SignUpActivity.this , "사용가능한 아이디입니다", Toast.LENGTH_SHORT);
					toast.show();
				}
			}
		}
		
		@Override
		protected void onCancelled() {
			//AsyncTask를 강제로 취소했을때 호출
			super.onCancelled();
			
		}

	}
	////////////////////////////////////////////////////////////////////////////////////////////////////////
	//회원가입을 위한 통신
	private String sendData(String id, String pass, String name, String phone_number, String gender, String requestURL1) throws ClientProtocolException, IOException{
		
		client1 = new DefaultHttpClient();
		HttpPost request = makeHttpPost(id,pass,name,phone_number,gender,requestURL1);
		ResponseHandler<String> reshandler = new BasicResponseHandler();
		String result = client1.execute(request, reshandler);
		return result;
	}
		
	private HttpPost makeHttpPost(String id, String pass, String name, String phone_number, String gender, String requestURL1){
		
		HttpPost request = new HttpPost(requestURL1);
		List<NameValuePair> dataList = new ArrayList<NameValuePair>();
		
		//서버가 끊겼을경우 5초의 타임을 체크
		HttpParams param = client1.getParams();
    	HttpConnectionParams.setConnectionTimeout(param, 5000);
    	HttpConnectionParams.setSoTimeout(param, 5000);
    	
    	dataList.add(new BasicNameValuePair("id", id));
    	dataList.add(new BasicNameValuePair("pass", pass));
    	dataList.add(new BasicNameValuePair("name", name));
    	dataList.add(new BasicNameValuePair("phone_number", PhoneNumber));
    	dataList.add(new BasicNameValuePair("gender", gender));
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
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////
	//아이디 중복체크를 위한 통신
	private String sendData2(String id, String requestURL2) throws ClientProtocolException, IOException{
		
		client2 = new DefaultHttpClient();
		HttpPost request = makeHttpPost2(id,requestURL2);
		ResponseHandler<String> reshandler = new BasicResponseHandler();
		String result = client2.execute(request, reshandler);
		return result;
	}
		
	private HttpPost makeHttpPost2(String id, String requestURL2){
		
		HttpPost request = new HttpPost(requestURL2);
		List<NameValuePair> dataList = new ArrayList<NameValuePair>();
    	
		//서버가 끊겼을경우 5초의 타임을 체크
		HttpParams param = client2.getParams();
    	HttpConnectionParams.setConnectionTimeout(param, 5000);
    	HttpConnectionParams.setSoTimeout(param, 5000);
    	
		dataList.add(new BasicNameValuePair("id", id));
    	request.setEntity(makeEntity2(dataList));
    	return request;
	}
	
	private HttpEntity makeEntity2(List<NameValuePair> dataList){
		
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
