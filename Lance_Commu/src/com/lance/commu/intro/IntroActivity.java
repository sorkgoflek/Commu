package com.lance.commu.intro;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.lance.commu.extra.ViewPagerAdapter;
import com.lance.commu.login.LoginActivity;
import com.lance.commu.logo.LogoActivity;
import com.lance.commu.network.ServerIP;
import com.lance.commu.sqliteDB.DB_Handler;


public class IntroActivity extends Activity{
	
	SharedPreferences introCheck;//������ ó�� ������������ ��Ʈ�ξ�Ƽ��Ƽ�� ������ �ι�°���ʹ� �ΰ��Ƽ��Ƽ�� �������� �����ϱ� ���� SharedPreferences
	String check;
	Button login_go_button;//��Ʈ�ο��� �α���ȭ������ ���� ���� ��ư 
	HttpClient client;
	private int imageArra[] = { R.drawable.intro_clock2, R.drawable.intro_phone};//��Ʈ�� ȭ�� ä���� �̹�������
	
	
	//��� url�� ���������� ����ϴ� �κ�////////////////// 
	//public static String url = "http://121.157.84.63:80";
	//public static String url = "http://113.198.80.185:80";
	public static String url = ServerIP.DB_SERVER;
	///////////////////////////////////////////////////////

	ConnectivityManager connect; 
	String requestURL1; 	
	int index =0; 
	private static HashMap<String, String> contactList; //�� ��ȭ��ȣ�ξ��� �̸�,��ȣ �����
	String[] dB_Phone_List; //��Ʈ��ũ ����� ���� Oracle DB���� ����� ��ȣ�� ����
	String[] phone_Book_List = new String[2500]; //�� ��ȭ��ȣ�ξ��� �̸�,��ȣ �����
	
	//ģ����� ����Ʈ�信 ������������ �����ϱ� ���� ��ġ�� �߰��ܰ� 
	ArrayList<String> for_Sort_List1 = new ArrayList<String>();
	ArrayList<String> for_Sort_List2 = new ArrayList<String>();
	static String[] for_Sort_Array1 ;
	
	//���������� Oracle DB�� �� ��ȭ��ȣ�ο� ��ġ�ϴ� ��ȣ,�̸� ����
	ArrayList<String> union_Phone_List_Number = new ArrayList<String>();
	ArrayList<String> union_Phone_List_Name = new ArrayList<String>();
	
	//Sqlite DB�� �������� ����
	DB_Handler db_Handler;
	Cursor cursor = null;
	public static ArrayList<String> sqlite_Code = new ArrayList<String>();
	public static ArrayList<String> sqlite_Name = new ArrayList<String>();
	public static ArrayList<String> sqlite_Phone_Number = new ArrayList<String>();	


	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		System.out.println("��Ʈ�ν���");
		//ȭ�� ��ü�� �������� ���� 
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		//introCheck�� �ʱⰪ ���ڿ� check�� ����  
		introCheck = getSharedPreferences("introChecking", MODE_PRIVATE);
		check = introCheck.getString("sp_check", "check");
		
		if(check == "check"){//"�� ��Ƽ��Ƽ�� ó���̸�" �̶�� if���� 
			setContentView(R.layout.intro); //�������� intro ��Ƽ��Ƽ �����ض� 
			
			login_go_button = (Button)findViewById(R.id.login_go__button);
			
			//��Ʈ�� ȭ�� ����
			ViewPagerAdapter adapter = new ViewPagerAdapter(this, imageArra);
			ViewPager myPager = (ViewPager) findViewById(R.id.myfivepanelpager);
			myPager.setAdapter(adapter);
			myPager.setCurrentItem(0);
			
			//ó�� ��Ʈ�� ��Ƽ��Ƽ�� ���۵ǰ� SharedPreferences�� checkAgain �̶�� ���ڿ��� �߰�����
			//�׷��� ������ �ι�° ������������ �Ȱ��� ��Ʈ�� ��Ƽ��Ƽ�� �ƴ� �ΰ� ��Ƽ��Ƽ�� ������ �����ϴ°� 
			SharedPreferences.Editor editor = introCheck.edit();
			editor.putString("sp_check", "checkAgain");
			editor.commit();
			
			//Ŀ�� �����ϱ� ��ư�� �������ٸ� 
			login_go_button.setOnClickListener(new View.OnClickListener() {
				//�α��� â���� ȸ������ ��ư Ŭ���� 		
					@Override
					public void onClick(View v) {
						startActivity(new Intent(IntroActivity.this,LoginActivity.class));
						finish();
					}		
				});//end onClick()

			connect = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE); //���ͳ��� ����Ǿ��ֳ� Ȯ��
			requestURL1 = url+"/lance/androidFriendList.jsp"; //��Ʈ��ũ ����� ���� �� �ּ�		
		
			//�� ��ȭ��ȣ�ο� �ִ� ��ȭ����� �� �������� �ϴ� �Լ�
			//�� ��ȭ��ȣ�ο� OracleDB�� ��ȭ��ȣ�ο� �񱳸� �Ұž� 
			//�׷��� ��ġ�ϴ°͸� �߷��� ListView�� ��Ÿ���� �Ұž� 
			System.out.println("��ȭ��ȣ�� �������� �Լ�ȣ������");
			getContactData();
			
			//Sqlite DB �������� ����
			//Oracle DB���� ������ ������ Sqlite�� �����Ұž� 
			//�׷��� �Ź� ��Ʈ��ũ ����� ���ؼ� Oracle DB����� ��°� �ƴ϶� 
			//Sqlite�� ������ �ٷ� ������ ���� ����
			//ó���� ���ΰ�ħ �������� ��Ʈ��ũ ����� ���� Oracle DB�� �ٽ� Sqlite�� ����
			try {
				db_Handler = DB_Handler.open(this);
			} catch (SQLException e) {
				
				e.printStackTrace();
				System.out.println("db open�� �����־�");
			}
			
			//���ͳ��� ����Ǿ� �ִ��� üũ�ϰ� ����Ǿ��ٸ� ��Ʈ��ŷ�� ���� 
			//Oracle DB����� ���ðž� 
			if(connect.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED 
					|| connect.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED	
					 ){
						
							new Networking().execute();
							System.out.println("��Ʈ��ŷ ����");
							
					}
							
						else{
							Toast toast = Toast.makeText(IntroActivity.this , "���ͳݿ����� ������ϴ�", Toast.LENGTH_SHORT);
							toast.show();
						}	
		}//check end
		
		else{//���� ó���� �ƴϸ� �ΰ� ��Ƽ��Ƽ�� ���� 
			startActivity(new Intent(IntroActivity.this,LogoActivity.class));
			finish();
		}
	}
	
	//�� ��ȭ��ȣ���� �̸��� ��ȣ�� �������� �ϴ��Լ� 
	private void getContactData(){
		Cursor phoneCursor = null;
	     contactList = new HashMap<String,String>();
	     
	   try{
	        // �ּҷ��� ����� URI
	        Uri uContactsUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
	   
	       // �ּҷ��� �̸��� ��ȭ��ȣ�� �� �̸�
	       String strProjection = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME;
	   
	       // �ּҷ��� ��� ���� �������� ������ Ŀ���� ����
	       phoneCursor = getContentResolver().query(uContactsUri, null, null, null, strProjection);
	       phoneCursor.moveToFirst();
	       
	       System.out.println("��ȭ��ȣ�� �Լ� ������ ������ ���� ����");
	       
	       String name = "";
	       String phoneNumber = "";
	         

	        // �ּҷ��� �̸�
	        int nameColumn = phoneCursor.getColumnIndex(Phone.DISPLAY_NAME);
	        // �ּҷ��� ��ȭ��ȣ
	        int phoneColumn = phoneCursor.getColumnIndex(Phone.NUMBER);
	           
	        System.out.println("��ȭ��ȣ�� �Լ� �ּҷ� �̸�,��ȣ �Ű� ������� �Ϸ�");
	        
	        while(!phoneCursor.isAfterLast()){
	            name = phoneCursor.getString(nameColumn);
	            phoneNumber = phoneCursor.getString(phoneColumn);
	            
	            ///////////////Ȯ�ο� ���///////////
	            //System.out.println("��ȭ��ȣ�� ��ȣ : "+phoneNumber);
	            //System.out.println("��ȭ��ȣ�� �̸� : "+name);
	              
	            // HashMap�� data ���� 
	            contactList.put(phoneNumber, name);
	            phoneCursor.moveToNext();
	         }
	     }
	     catch(Exception e){
	         e.printStackTrace();
	     }
	     finally{
	        if(phoneCursor != null){
	           phoneCursor.close();
	           phoneCursor = null;
	           System.out.println("���� finally���� ������ ����");
	           for(Map.Entry<String, String> s : contactList.entrySet()){
	        	   String phone_number = s.getKey();
	        	   phone_number = phone_number.replaceAll("-", "");
	        	   String name = s.getValue();
	        	   
	        	   //System.out.println("���� ������� ��ȣ : "+phone_number);
	        	   //System.out.println("���� ������� �̸� : "+name);
	        	   
	        	   //���������� ��ȣ�� �̸��� �������
	        	   phone_Book_List[index++] = phone_number;
	        	   phone_Book_List[index++] = name;
	           }   
	        }
	        
	     }
	}// getContactData end	
	
	//AsyncTask�� �Ἥ ��Ʈ��ŷ �۾� 
	private class Networking extends AsyncTask<URL, Integer, String>{

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
					//Oracle DB���� ����� ����� result�� �������
					result = sendData(requestURL1);
					
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
			
		//System.out.println(result);
		/*
		//��Ʈ��ŷ�� ���Ӱ� ���������� ���� sqlite�κ��� ��� �����ߵ�.(���ΰ�ħ)
		db_Handler.removeData();	
		sqlite_Name.clear();
		sqlite_Code.clear();
		sqlite_Phone_Number.clear();
		*/
		//���� result ���ڿ� ���� ServerDown�̸� ���� �ٿ� �佺Ʈ ���� ���� 
		if(result.equals("ServerDown")){
			Toast toast = Toast.makeText(IntroActivity.this , "������ �ٿ�ƽ��ϴ�", Toast.LENGTH_LONG);
			toast.show();
		}
		else{//���� �ٿ��� �ƴ϶�� ���������� �Ľ��ؼ� ����� �����س� 	
			//Oracle DB������ ��� result�� �Ľ��ؼ� dB_Phone_List�� ��´�
			
			//��Ʈ��ŷ�� ���Ӱ� ���������� ���� sqlite�κ��� ��� �����ߵ�.(���ΰ�ħ)
			db_Handler.removeData();	
			sqlite_Name.clear();
			sqlite_Code.clear();
			sqlite_Phone_Number.clear();
			
			
			try{
			
			//System.out.println(result);
			
			dB_Phone_List = result.split(",");
			dB_Phone_List[0] = dB_Phone_List[0].substring(25, 38);
			dB_Phone_List[dB_Phone_List.length-1] = dB_Phone_List[dB_Phone_List.length-1].substring(1,14);
			
			//���� ��ȣ�� '-' �������� ��� ���� 
			for(int i=0;i<dB_Phone_List.length;i++){
				dB_Phone_List[i] = dB_Phone_List[i].trim();
				dB_Phone_List[i] = dB_Phone_List[i].replaceAll("-", "");
	
			}
			
			//OracleDB��ȣ�� �� ��ȭ��ȣ���� ��ȣ�� �ϳ��� ���ؼ� ��ġ�ϴ°͵鸸 �߷�����
			for(int i=0;i<index;i++){
				for(int j=0; j<dB_Phone_List.length; j++){
					if(phone_Book_List[i].equals(dB_Phone_List[j])){
						
						//��ġ�ϴ� �͵��� ��Ƽ� �������� ������ ���� 
						//for_Sort_List1�� �̸��� ��ȣ�� ���� �Űܴ�´�
						//(�̸��� ��ȣ�� �Ѳ����� ���ĵǰ� �ϱ����ؼ�)
						for_Sort_List1.add(phone_Book_List[i+1]+","+phone_Book_List[i]);
						
					}
				}
			}
			//�����۾�
			Collections.sort(for_Sort_List1);
			
			//���ĵȰ��� , �� ������� for_Sort_List2�� ��´� (�̸����� ��ȣ����)
			for(int i=0; i<for_Sort_List1.size();i++){
				for_Sort_Array1 = for_Sort_List1.get(i).split(",");
			
				for_Sort_List2.add(for_Sort_Array1[0]);
				for_Sort_List2.add(for_Sort_Array1[1]);
			}
					
			//���������� ����� ����� �����Ѵ�. 
			for(int i=0; i<for_Sort_List2.size();i+=2){
				union_Phone_List_Name.add(for_Sort_List2.get(i));
				union_Phone_List_Number.add(for_Sort_List2.get(i+1));
			
			}
		
			//��Ʈ��ũ ����� ���� ���� ����� Sqlite�� �����Ѵ�
			//���� ����� �̸��� ��ȣ�� sqlite�� �Űܼ� �����Ѵ�
			for(int i=0; i<union_Phone_List_Name.size();i++){
				db_Handler.insert(union_Phone_List_Name.get(i), union_Phone_List_Number.get(i));
			}
			
			cursor = db_Handler.selectAll();
			while(cursor.moveToNext()){
				sqlite_Code.add(cursor.getString(0));
				sqlite_Name.add(cursor.getString(1));
				sqlite_Phone_Number.add(cursor.getString(2));
			}
			}catch(Exception e){
				e.printStackTrace();
			}
			finally{
			cursor.close();
			}
				
			System.out.println("��Ʈ��ũ �۾��Ϸ�");
		}
	}
		
		
	@Override
	protected void onCancelled() {
		//AsyncTask�� ������ ��������� ȣ��
		//������ �ٽ� �ʱ�ȭ
		for_Sort_List1.clear();
		for_Sort_List2.clear();
		union_Phone_List_Name.clear();
		union_Phone_List_Number.clear();
		super.onCancelled();
		}
	}// Networking1 end
	
	private String sendData(String requestURL1) throws ClientProtocolException, IOException{
		
		client = new DefaultHttpClient();
		HttpPost request = makeHttpPost(requestURL1);
		ResponseHandler<String> reshandler = new BasicResponseHandler();
		String result = client.execute(request, reshandler);
		return result;
	}
		
	private HttpPost makeHttpPost(String requestURL1){
		
		HttpPost request = new HttpPost(requestURL1);
		List<NameValuePair> dataList = new ArrayList<NameValuePair>();
		
		//������ ��������� 5���� Ÿ���� üũ
		HttpParams param = client.getParams();
    	HttpConnectionParams.setConnectionTimeout(param, 5000);
    	HttpConnectionParams.setSoTimeout(param, 5000);
    	
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
	
	//������ Sqlite�� �ݾ���
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (db_Handler != null) {
			db_Handler.close();
	    }
	    if (cursor != null) {
	    	cursor.close();
	    }
	}
	
}
