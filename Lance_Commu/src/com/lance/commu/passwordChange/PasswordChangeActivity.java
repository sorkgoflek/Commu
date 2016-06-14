package com.lance.commu.passwordChange;

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
import android.widget.EditText;
import android.widget.Toast;

import com.lance.commu.extra.CDialog;
import com.lance.commu.fragment.FragmentMainActivity;
import com.lance.commu.intro.IntroActivity;
import com.lance.commu.intro.R;
import com.lance.commu.login.LoginActivity;

@SuppressLint("NewApi")
public class PasswordChangeActivity extends Activity{
	
	String id ;
	String requestURL1; 
	String pwCheck1;
	String pwCheck2;
	String resultPassChange; 
	Button pwchange_button;
	EditText pw_change1,pw_change2;
	ConnectivityManager connect;
	HttpClient client;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.password_change);
		
		ActionBar actionBar = getActionBar();
		actionBar.setIcon(R.drawable.phonebook_lo);
		actionBar.setTitle("Commu");
		
		
		pwchange_button = (Button)findViewById(R.id.pwchange_button); //확인버튼
		pw_change1 = (EditText)findViewById(R.id.pw_change1);//비번변경1
		pw_change2 = (EditText)findViewById(R.id.pw_change2);//비번변경2
		
		requestURL1 = IntroActivity.url+"/lance/androidPasswordChange.jsp";
		connect = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE); //인터넷이 연결되어있나 확인
		
		//내가 접속한 아이디값 넘겨받음 
		Intent intent = getIntent();
		id = intent.getStringExtra("id");
		System.out.println("비번변경창 ID:"+id);
		
		pwchange_button.setOnClickListener(new View.OnClickListener() {
			//확인 버튼 클릭시	
				@Override
				public void onClick(View v) {
					
					//내가 입력한 비번 두개 얻어오고
					pwCheck1 = pw_change1.getText().toString();
					pwCheck2 = pw_change2.getText().toString();
					 
					//먼저 패스워드 두개값 입력했는지 확인
					if(pwCheck1.length() ==0 || pwCheck1.trim().length() ==0){
						Toast toast = Toast.makeText(PasswordChangeActivity.this , "변경할 비밀번호를 입력하세요", Toast.LENGTH_SHORT);
						toast.show();
						pw_change1.requestFocus();
					}
					
					else if(pwCheck2.length() ==0 || pwCheck2.trim().length() ==0){
						Toast toast = Toast.makeText(PasswordChangeActivity.this , "한 번 더 입력하세요", Toast.LENGTH_SHORT);
						toast.show();
						pw_change2.requestFocus();
					}	
					
					//입력한 두개 값이 일치하는지 비교하고 
					else if(pwCheck1.equals(pwCheck2) == false){ //일치하지 않으면 
						Toast toast = Toast.makeText(PasswordChangeActivity.this , "비밀번호 입력이 일치하지 않습니다", Toast.LENGTH_SHORT);
						toast.show();
						System.out.println("패스워드 값 불일치");
						
					}
					
					else{//패스워드 두개값 입력하고 그 값들이 일치하면 
						System.out.println("패스워드 값 일치");
						//인터넷이 연결돼 있나 확인하고 네트워킹 작업 
						if(connect.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED 
						|| connect.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED	
						 ){
							new Networking().execute();
						}
						
						else{
							Toast toast = Toast.makeText(PasswordChangeActivity.this , "인터넷연결이 끊겼습니다", Toast.LENGTH_SHORT);
							toast.show();
						}	
					}	
				}		
			});//end onClick()
	}//onCreate end
	
	
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

					result = sendData(id,pwCheck2,requestURL1);
					
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
				Toast toast = Toast.makeText(PasswordChangeActivity.this , "서버가 다운됐습니다", Toast.LENGTH_LONG);
				toast.show();
			}
			else{//서버 다운이 아니라면 정상적으로 파싱해서 결과값 도출해냄
				String[] list;
				list = result.split(">");
				resultPassChange = list[2].substring(2, 7);
				System.out.println(resultPassChange);
			
				if(resultPassChange.equals("notpc")){
					Toast toast = Toast.makeText(PasswordChangeActivity.this , "비밀번호가 변경 안됐습니다.", Toast.LENGTH_SHORT);
					toast.show();
				}
				
				else if(resultPassChange.equals("yespc")){
					Toast toast = Toast.makeText(PasswordChangeActivity.this , "비밀번호가 변경 됐습니다.", Toast.LENGTH_SHORT);
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
