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
		
		
		pwchange_button = (Button)findViewById(R.id.pwchange_button); //Ȯ�ι�ư
		pw_change1 = (EditText)findViewById(R.id.pw_change1);//�������1
		pw_change2 = (EditText)findViewById(R.id.pw_change2);//�������2
		
		requestURL1 = IntroActivity.url+"/lance/androidPasswordChange.jsp";
		connect = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE); //���ͳ��� ����Ǿ��ֳ� Ȯ��
		
		//���� ������ ���̵� �Ѱܹ��� 
		Intent intent = getIntent();
		id = intent.getStringExtra("id");
		System.out.println("�������â ID:"+id);
		
		pwchange_button.setOnClickListener(new View.OnClickListener() {
			//Ȯ�� ��ư Ŭ����	
				@Override
				public void onClick(View v) {
					
					//���� �Է��� ��� �ΰ� ������
					pwCheck1 = pw_change1.getText().toString();
					pwCheck2 = pw_change2.getText().toString();
					 
					//���� �н����� �ΰ��� �Է��ߴ��� Ȯ��
					if(pwCheck1.length() ==0 || pwCheck1.trim().length() ==0){
						Toast toast = Toast.makeText(PasswordChangeActivity.this , "������ ��й�ȣ�� �Է��ϼ���", Toast.LENGTH_SHORT);
						toast.show();
						pw_change1.requestFocus();
					}
					
					else if(pwCheck2.length() ==0 || pwCheck2.trim().length() ==0){
						Toast toast = Toast.makeText(PasswordChangeActivity.this , "�� �� �� �Է��ϼ���", Toast.LENGTH_SHORT);
						toast.show();
						pw_change2.requestFocus();
					}	
					
					//�Է��� �ΰ� ���� ��ġ�ϴ��� ���ϰ� 
					else if(pwCheck1.equals(pwCheck2) == false){ //��ġ���� ������ 
						Toast toast = Toast.makeText(PasswordChangeActivity.this , "��й�ȣ �Է��� ��ġ���� �ʽ��ϴ�", Toast.LENGTH_SHORT);
						toast.show();
						System.out.println("�н����� �� ����ġ");
						
					}
					
					else{//�н����� �ΰ��� �Է��ϰ� �� ������ ��ġ�ϸ� 
						System.out.println("�н����� �� ��ġ");
						//���ͳ��� ����� �ֳ� Ȯ���ϰ� ��Ʈ��ŷ �۾� 
						if(connect.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED 
						|| connect.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED	
						 ){
							new Networking().execute();
						}
						
						else{
							Toast toast = Toast.makeText(PasswordChangeActivity.this , "���ͳݿ����� ������ϴ�", Toast.LENGTH_SHORT);
							toast.show();
						}	
					}	
				}		
			});//end onClick()
	}//onCreate end
	
	
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

					result = sendData(id,pwCheck2,requestURL1);
					
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
				Toast toast = Toast.makeText(PasswordChangeActivity.this , "������ �ٿ�ƽ��ϴ�", Toast.LENGTH_LONG);
				toast.show();
			}
			else{//���� �ٿ��� �ƴ϶�� ���������� �Ľ��ؼ� ����� �����س�
				String[] list;
				list = result.split(">");
				resultPassChange = list[2].substring(2, 7);
				System.out.println(resultPassChange);
			
				if(resultPassChange.equals("notpc")){
					Toast toast = Toast.makeText(PasswordChangeActivity.this , "��й�ȣ�� ���� �ȵƽ��ϴ�.", Toast.LENGTH_SHORT);
					toast.show();
				}
				
				else if(resultPassChange.equals("yespc")){
					Toast toast = Toast.makeText(PasswordChangeActivity.this , "��й�ȣ�� ���� �ƽ��ϴ�.", Toast.LENGTH_SHORT);
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
