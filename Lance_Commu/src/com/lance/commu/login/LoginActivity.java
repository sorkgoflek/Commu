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
	String id; // ���� �Է��ϴ� ���̵�� (���� ���̵��ϼ� �ְ� Ʋ���� �ִ� ���̵𰪵�) 
	public static String rememberRealId; //�α��� �� �� ����ó�� ����ϰ� �־�� �� ���̵� 
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
		
		
		backPressCloseHandler = new BackPressCloseHandler(this);//�ι� �ڷΰ��� �� ���� �۾�
		
		insert_button = (Button)findViewById(R.id.insert_button);
		login_button = (Button)findViewById(R.id.login_button);
		
		edit_id = (EditText)findViewById(R.id.id);
		edit_id.setPrivateImeOptions("defaultInputmode=english;"); //�⺻ �Է�â�� ����������
		
		edit_pass = (EditText)findViewById(R.id.pass);
		requestURL1 = IntroActivity.url+"/lance/androidLogin.jsp";
		connect = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE); //���ͳ��� ����Ǿ��ֳ� Ȯ�� 

		//�ڵ��α��� üũ�ڽ�
		findViewById(R.id.checkbox).setOnClickListener
									(new Button.OnClickListener(){
						@Override
						public void onClick(View v){
							checkAutoLogin(v);
						}	
		});
		
		
		insert_button.setOnClickListener(new View.OnClickListener() {
			//�α��� â���� ȸ������ ��ư Ŭ���� 		
				@Override
				public void onClick(View v) {
					startActivity(new Intent(LoginActivity.this,SignUpActivity.class));
					
				}		
			});//end onClick()
		
		
		login_button.setOnClickListener(new View.OnClickListener() {
			//�α��� ��ư Ŭ����	
				@Override
				public void onClick(View v) {
					
					 id = edit_id.getText().toString();
					 pass = edit_pass.getText().toString();
					 
					//���� ���̵� �н����� �Է��ߴ��� Ȯ��
					if(id.length() ==0 || id.trim().length() ==0){
						Toast toast = Toast.makeText(LoginActivity.this , "���̵� �Է��ϼ���", Toast.LENGTH_SHORT);
						toast.show();
						edit_id.requestFocus();
					}
					
					else if(pass.length() ==0 || pass.trim().length() ==0){
						Toast toast = Toast.makeText(LoginActivity.this , "�н����带 �Է��ϼ���", Toast.LENGTH_SHORT);
						toast.show();
						edit_pass.requestFocus();
					}	
					
					 
					else{//���̵�, �н����� ���� ��� �Է��ϰ� �� �� Ŭ���ϸ� 
							//���ͳ��� ����� �ֳ� Ȯ�� 
							if(connect.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED 
							|| connect.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED	
							 ){
								new Networking().execute();
							}
							
							else{
								Toast toast = Toast.makeText(LoginActivity.this , "���ͳݿ����� ������ϴ�", Toast.LENGTH_SHORT);
								toast.show();
							}		
					}
				}		
			});//end onClick()
	}//onCreate end 
	
	public void checkAutoLogin(View v){
		CheckBox ch = (CheckBox)findViewById(R.id.checkbox);
		if(ch.isChecked()){//�ڵ��α����� üũ�ƴٸ�
			flag = true; //�̰����� ��Ʈ��ũ ����ϸ鼭 �Ƶ�,����� ����ϳ� ���ϳ� ����
		}
		else{//�ڵ��α����� üũ �ȵƴٸ�
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
			//doInBackground �� ����Ǳ� ��, �� AsyncTask.execute()	�� ȣ�������� ���� ���� ����Ǵ� �༮ 
			//���� �ʱ�ȭ �۾��� ���ش�
			super.onPreExecute();	
		}
		
		@Override
		protected String doInBackground(URL... params){
			//������ �����忡�� �۾��� ������ 
			//AsyncTask.execute(params)�� ���ؼ� ������ �� 
			String result =null;
			
			try {

					result = sendData(id,pass,requestURL1);
					
			} catch (ClientProtocolException e) {
				
				e.printStackTrace();
				System.out.println("clientprotocol exception");
			} catch (IOException e) {
				
				//������ ������ IOException������ ���ԉ� 
				//�׷� result���� ServerDown �̶�� ���ڿ��� �ְ��� 
				return "ServerDown"; 
			}

			onCancelled();
			return result;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			//doInBackground���� ��ó���� �ϴٰ� �߰��� UI �����忡�� ó���� ���� �ִ� ��� ȣ��� 
			//doInBackground���� ����Ǵ� ������ UI �����忡�� �۵��� 
			//doInBackground���� ��������� publishProgress( progress ) �� ȣ�� ���־����� ���� 
			super.onProgressUpdate(values);
		}
		
		@Override
		protected void onPostExecute(String result) {
			//doInBackground�� ó���� ��� ������ �ش� �޼ҵ尡 return �Ǿ����� �� ���ϰ��� ����� ���鼭 �� �޼ҵ尡 �����
			//AsyncTask�� ��ҵǰų� ���� �߻��ÿ� parameter �� null �� ����
			//�� onPostExecute �� ������ UIThread���� �����
			super.onPostExecute(result);
			
			//���� result ���ڿ� ���� ServerDown�̸� ���� �ٿ� �佺Ʈ ���� ���� 
			if(result.equals("ServerDown")){
				Toast toast = Toast.makeText(LoginActivity.this , "������ �ٿ�ƽ��ϴ�", Toast.LENGTH_LONG);
				toast.show();
			}
			else{//���� �ٿ��� �ƴ϶�� ���������� �Ľ��ؼ� ����� �����س�
				String[] list;
				list = result.split(">");
				login = list[2].substring(2, 7);
				System.out.println(login);
			
				if(login.equals("notid")){
					Toast toast = Toast.makeText(LoginActivity.this , "���̵� �߸� �Է��ϼ̽��ϴ�", Toast.LENGTH_SHORT);
					toast.show();
				}
				
				else if(login.equals("notps")){
					Toast toast = Toast.makeText(LoginActivity.this , "��й�ȣ�� �߸� �Է��ϼ̽��ϴ�", Toast.LENGTH_SHORT);
					toast.show();
				}
				
				else if(login.equals("login")){
					Toast toast = Toast.makeText(LoginActivity.this , id+" �� ȯ���մϴ�", Toast.LENGTH_SHORT);
					toast.show();
					CDialog.showLoading(LoginActivity.this);
					rememberRealId = id;
					if(flag == true){//�ڵ��α��� üũ�ڽ��� üũ�Ǿ��ִٸ� flag ���� true
						
						//������ �ڵ��α����� ���Ͽ� sharedpreference�� ����
						sp = getSharedPreferences("PreName", MODE_PRIVATE);
						SharedPreferences.Editor editor = sp.edit();
						
						editor.putString("sp_id", id); 
						editor.putString("sp_pw", pass); 
						editor.commit();
						System.out.println("�α��� sp ����� id:"+id);
						System.out.println("�α��� sp ����� pass:"+pass);
						
					}
					
					Intent intent = new Intent(LoginActivity.this,FragmentMainActivity.class);
					startActivity(intent);
					finish();

					}
			}
		}

		@Override
		protected void onCancelled() {
			//AsyncTask�� ������ ��������� ȣ��
			super.onCancelled();
		}
	}
	
	//��Ƽ��Ƽ �������� ���̾�α׵� ��
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
    	
		//������ ��������� 5���� Ÿ���� üũ
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
			System.out.println("����Ƽ�����ڵ� exception");
		}
		return result;
	}
}
