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
	String phone_number; //���������� ��Ʈ��ũ ���� ������ ���ݹ�ȣ 
	String PhoneNumber; //�� ���ݹ�ȣ �ڵ����� ���ͼ� ����Ǵ� ����
	String gender;
	String idCheck; //���̵� �ߺ����� üũ�ϴ� ���� 
	String idCheck_Hangel; //1���� ���̵� �ѱ�üũ
	static boolean idPass = false; //1�� ���̵� �ѱ���� ����Ȯ��
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
		edit_id.setPrivateImeOptions("defaultInputmode=english;"); //�⺻ �Է�â�� ����������
		
		edit_pass = (EditText)findViewById(R.id.pass);
		edit_name = (EditText)findViewById(R.id.name);
		//edit_phone_number  = (EditText)findViewById(R.id.phone_number);
		rb_man = (RadioButton)findViewById(R.id.radiobutton_man);
		rb_woman = (RadioButton)findViewById(R.id.radiobutton_woman);
		
		insert_button = (Button)findViewById(R.id.insert_button);
		idCheck_button = (Button)findViewById(R.id.idCheck_button);
		
		requestURL1 = IntroActivity.url+"/lance/androidJoin.jsp";
		requestURL2 = IntroActivity.url+"/lance/androidIdCheck.jsp";
		
		connect = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE); //���ͳ��� ����Ǿ��ֳ� Ȯ�� 
		
		//�� �ڵ��� ��ȣ �������� 
		TelephonyManager systemService = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
		PhoneNumber = systemService.getLine1Number();
		PhoneNumber = PhoneNumber.substring(PhoneNumber.length()-10,PhoneNumber.length());
		PhoneNumber="0"+PhoneNumber;
		PhoneNumber = PhoneNumberUtils.formatNumber(PhoneNumber);
		//edit_phone_number.setHint(PhoneNumber);
		//edit_phone_number.setEnabled(false); 
		
		//////////////////////////////////////////////////////////////
		idCheck_button.setOnClickListener(new View.OnClickListener() {
		//���̵� �ߺ�üũ ��ư Ŭ���� 	
			@Override
			public void onClick(View v) {
				
				id = edit_id.getText().toString(); //���� �Է��� ���̵� �� 
				
				if(id.length() ==0 || id.trim().length() ==0){
					Toast toast = Toast.makeText(SignUpActivity.this , "���̵� �Է��ϼ���", Toast.LENGTH_SHORT);
					toast.show();
					edit_id.requestFocus();
				}
				
				else{
					//1���� �ѱ� �ɷ��� 
					for(int i=0;i<id.length();i++){
					     if(Character.getType(id.charAt(i)) == 5){ 
					    	 System.out.println("�ѱ��� �ȵ˴ϴ�");
					    	 Toast toast = Toast.makeText(SignUpActivity.this , "���� or ���� or ����+���� �� ����", Toast.LENGTH_SHORT);
							 toast.show();
					    	 idPass = false;
					     	break;
					     }
					     else{
					    	 idCheck_Hangel = id;
					    	 idPass = true;
					     }
					  }
					
					if(idPass == true){//1���� �ɷ����͸� 
						//2���� Ư������ �ɷ��� 
						Pattern p = Pattern.compile(".*[^��-�Ra-zA-Z0-9].*");
						Matcher m = p.matcher(idCheck_Hangel);
				   
						if(m.matches()) {
							Toast toast = Toast.makeText(SignUpActivity.this , "���� or ���� or ����+���� �� ����", Toast.LENGTH_SHORT);
							toast.show();
							System.out.println("���� or ���� or ����+���� �� ����");
						} 
						else {
						        System.out.println("���̵� ���� ���� �ߺ�Ȯ�θ��ϸ��");
						
						      //���ͳ��� ����� �ֳ� Ȯ�� 
								if(connect.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED 
								|| connect.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED	
								 ){
									new Networking2().execute();
								}
								
								else{
									Toast toast = Toast.makeText(SignUpActivity.this , "���ͳݿ����� ������ϴ�", Toast.LENGTH_SHORT);
									toast.show();
								}	
						
						}
					}

			}

		}		
	});//end onClick()
		

		/////////////////////////////////////////////////////////////
		insert_button.setOnClickListener(new View.OnClickListener() {
		//ȸ������ ��ư Ŭ���� 		
				@Override
				public void onClick(View v) {
					
					 id = edit_id.getText().toString();
					 pass = edit_pass.getText().toString();
					 name = edit_name.getText().toString();
					 //phone_number = edit_phone_number.getText().toString();
					 phone_number = PhoneNumber;
					 if(rb_man.isChecked())
						gender="����";			
					if(rb_woman.isChecked())
						gender="����";

					 
					 
					if(id.length() ==0 || id.trim().length() ==0){
						Toast toast = Toast.makeText(SignUpActivity.this , "���̵� �Է��ϼ���", Toast.LENGTH_SHORT);
						toast.show();
						edit_id.requestFocus();
					}
					
					else if(pass.length() ==0 || pass.trim().length() ==0){
						Toast toast = Toast.makeText(SignUpActivity.this , "�н����带 �Է��ϼ���", Toast.LENGTH_SHORT);
						toast.show();
						edit_pass.requestFocus();
					}
					
					else if(name.length() ==0 || name.trim().length() ==0){
						Toast toast = Toast.makeText(SignUpActivity.this , "�̸��� �Է��ϼ���", Toast.LENGTH_SHORT);
						toast.show();
						edit_name.requestFocus();
					}

					else{
						//1���� �ѱ� �ɷ��� 
						for(int i=0;i<id.length();i++){
						     if(Character.getType(id.charAt(i)) == 5){ 
						    	 System.out.println("�ѱ��� �ȵ˴ϴ�");
						    	 Toast toast = Toast.makeText(SignUpActivity.this , "���� or ���� or ����+���� �� ����", Toast.LENGTH_SHORT);
								 toast.show();
						    	 idPass = false;
						     	break;
						     }
						     else{
						    	 idCheck_Hangel = id;
						    	 idPass = true;
						     }
						  }
						
						
						if(idPass == true){//1���� �ɷ����͸� 
							//2���� Ư������ �ɷ��� 
							Pattern p = Pattern.compile(".*[^��-�Ra-zA-Z0-9].*");
							Matcher m = p.matcher(idCheck_Hangel);
					   
							if(m.matches()) {
								Toast toast = Toast.makeText(SignUpActivity.this , "���� or ���� or ����+���� �� ����", Toast.LENGTH_SHORT);
								toast.show();
								System.out.println("���� or ���� or ����+���� �� ����");
							} 
							else {
							        System.out.println("���̵� ���� ���� �ߺ�Ȯ�θ��ϸ��");
							
							      //���ͳ��� ����� �ֳ� Ȯ�� 
									if(connect.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED 
									|| connect.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED	
									 ){
										new Networking1().execute();
									}
									
									else{
										Toast toast = Toast.makeText(SignUpActivity.this , "���ͳݿ����� ������ϴ�", Toast.LENGTH_SHORT);
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
			//doInBackground �� ����Ǳ� ��, �� AsyncTask.execute()	�� ȣ�������� ���� ���� ����Ǵ� �༮ 
			//���� �ʱ�ȭ �۾��� ���ش�
			super.onPreExecute();
		}
		
		@Override
		protected String doInBackground(URL... params) {
			//������ �����忡�� �۾��� ������ 
			//AsyncTask.execute(params)�� ���ؼ� ������ �� 
			String result =null;
			
			try {
					result = sendData(id,pass,name,phone_number,gender,requestURL1);
					
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
				Toast toast = Toast.makeText(SignUpActivity.this , "������ �ٿ�ƽ��ϴ�", Toast.LENGTH_LONG);
				toast.show();
			}
			else{//���� �ٿ��� �ƴ϶�� ���������� �Ľ��ؼ� ����� �����س� 
				//result ������ jsp ������ ��� �� �ִ� ���߿��� rsult�κи� �����س��� �ϴµ� ... 
				//���⼭ result������ �ɰ��� �۾� ���� 
				String[] list;
				list = result.split(">");
				idCheck = list[2].substring(2, 7); // �ߺ� or ����  �ΰ��� string ���� ���� 
				System.out.println(idCheck);
				
				Toast toast_wait = Toast.makeText(SignUpActivity.this , "��ø� ��ٷ��ּ���", Toast.LENGTH_SHORT);
				toast_wait.show();
				
				if(idCheck.equals("dupli") ){
					
					Toast toast = Toast.makeText(SignUpActivity.this , "���̵� �ߺ��Դϴ�", Toast.LENGTH_SHORT);
					toast.show();
				}
				
				else if(idCheck.equals("duphn")){
					Toast toast = Toast.makeText(SignUpActivity.this , "1�� 1������ �����մϴ�", Toast.LENGTH_SHORT);
					toast.show();
				}
				
				else{
					Toast toast = Toast.makeText(SignUpActivity.this , "�����մϴ� ȸ�������� �Ǽ̽��ϴ�", Toast.LENGTH_SHORT);
					toast.show();
					startActivity(new Intent(SignUpActivity.this,LoginActivity.class));
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
	///////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private class Networking2 extends AsyncTask<URL, Integer, String>{

		@Override
		protected void onPreExecute() { 
			//doInBackground �� ����Ǳ� ��, �� AsyncTask.execute()	�� ȣ�������� ���� ���� ����Ǵ� �༮ 
			//���� �ʱ�ȭ �۾��� ���ش�
			super.onPreExecute();
		}
		
		@Override
		protected String doInBackground(URL... params) {
			//������ �����忡�� �۾��� ������ 
			//AsyncTask.execute(params)�� ���ؼ� ������ �� 
			String result =null;
			
			try {
				
					result = sendData2(id,requestURL2);
					
			
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
				Toast toast = Toast.makeText(SignUpActivity.this , "������ �ٿ�ƽ��ϴ�", Toast.LENGTH_LONG);
				toast.show();
			}
			else{//���� �ٿ��� �ƴ϶�� ���������� �Ľ��ؼ� ����� �����س� 
				//result ������ jsp ������ ��� �� �ִ� ���߿��� rsult�κи� �����س��� �ϴµ� ... 
				//���⼭ result������ �ɰ��� �۾� ���� 
				String[] list;
				list = result.split(">");
				idCheck = list[2].substring(2, 7); // �ߺ� or ����  �ΰ��� string ���� ���� 
				System.out.println(idCheck);
				
				if(idCheck.equals("dupli") ){
					
					Toast toast = Toast.makeText(SignUpActivity.this , "���̵� �ߺ��Դϴ�", Toast.LENGTH_SHORT);
					toast.show();
				}
				
				if(idCheck.equals("possi")){
					Toast toast = Toast.makeText(SignUpActivity.this , "��밡���� ���̵��Դϴ�", Toast.LENGTH_SHORT);
					toast.show();
				}
			}
		}
		
		@Override
		protected void onCancelled() {
			//AsyncTask�� ������ ��������� ȣ��
			super.onCancelled();
			
		}

	}
	////////////////////////////////////////////////////////////////////////////////////////////////////////
	//ȸ�������� ���� ���
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
		
		//������ ��������� 5���� Ÿ���� üũ
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
			System.out.println("����Ƽ�����ڵ� exception");
		}
		return result;
	}
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////
	//���̵� �ߺ�üũ�� ���� ���
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
    	
		//������ ��������� 5���� Ÿ���� üũ
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
			System.out.println("����Ƽ�����ڵ� exception");
		}
		return result;
	}
	
}
