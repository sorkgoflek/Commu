package com.lance.commu.fragment;
 

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
import android.content.Context;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.slidingsimplesample.R;
import com.lance.commu.intro.IntroActivity;
import com.lance.commu.sqliteDB.DB_Handler;

@SuppressLint("NewApi")
public class Fragment_FriendList extends Fragment implements SensorEventListener{

	/////////���ӵ� ���� ����/////////////////////
	private long lastTime;
    private float speed;
	private float lastX;
    private float lastY;
    private float lastZ;
    private float x, y, z;
    public static int SHAKE_THRESHOLD = 3500;
	private static final int DATA_X = SensorManager.DATA_X;
	private static final int DATA_Y = SensorManager.DATA_Y;
	private static final int DATA_Z = SensorManager.DATA_Z;
 
    private SensorManager sensorManager;
    private Sensor accelerormeterSensor;
	///////////////////////////////////
    
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
	Activity mActivity;
    ////////////////////////////////////////////////
	ArrayList<String> fragment_Sqlite_Name = new ArrayList<String>(); //���ο��� ����� Sqlite���� �̸����� ���� ����Ʈ 
	ArrayList<String> fragment_Sqlite_Phone_Number = new ArrayList<String>(); //���ο��� ����� Sqlite���� ��ȣ���� ���� ����Ʈ 
	HttpClient client;
	//////////////////////////////////
	ArrayAdapter<String> adapt; //ListView
	ListView menuList;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_friendlist, container, false);
        

      //���� ���ӵ� 
      sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
      accelerormeterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
      //////////////////////////////////////////
      
      connect = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE); //���ͳ��� ����Ǿ��ֳ� Ȯ��
      requestURL1 = IntroActivity.url+"/lance/androidFriendList.jsp"; //��Ʈ��ũ ����� ���� �� �ּ�
      
      
      	//�� ��ȭ��ȣ�ο� �ִ� ��ȭ����� �� �������� �ϴ� �Լ�
		//�� ��ȭ��ȣ�ο� OracleDB�� ��ȭ��ȣ�ο� �񱳸� �Ұž� 
		//�׷��� ��ġ�ϴ°͸� �߷��� ListView�� ��Ÿ���� �Ұž� 
		getContactData();
      
		//Sqlite DB �������� ����
		//Oracle DB���� ������ ������ Sqlite�� �����Ұž� 
		//�׷��� �Ź� ��Ʈ��ũ ����� ���ؼ� Oracle DB����� ��°� �ƴ϶� 
		//Sqlite�� ������ �ٷ� ������ ���� ����
		//ó���� ���ΰ�ħ �������� ��Ʈ��ũ ����� ���� Oracle DB�� �ٽ� Sqlite�� ����
		try {
			db_Handler = DB_Handler.open(getActivity());
		} catch (SQLException e) {

			e.printStackTrace();
			System.out.println("db open�� �����־�");
		}
      
	   return v;
    }
	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		 mActivity = activity;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		
		System.out.println("ģ����� �����׸�Ʈ ����");
		
		 
		 
       //���� ��Ƽ��Ƽ���� ���� �̸�,��ȣ ���� �Űܴ�� (sqlite�� �ִ� ������ ������ ���°�)
       for(int i=0; i<IntroActivity.sqlite_Name.size();i++){
    	   fragment_Sqlite_Name.add(IntroActivity.sqlite_Name.get(i));
    	   fragment_Sqlite_Phone_Number.add(IntroActivity.sqlite_Phone_Number.get(i));   	  
       }
       
       	//����Ʈ�� �����ؼ� �׾ȿ� ����־� 
	    menuList = (ListView)getActivity().findViewById(R.id.contacts_listview_a_unique_lisetview);	   
		menuList.setAdapter(new CustomAdapter(mActivity,getActivity(),R.layout.menu_item, fragment_Sqlite_Name, fragment_Sqlite_Phone_Number));
		
	   System.out.println("����Ʈ�� ��¿Ϸ�");

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
  	       phoneCursor = getActivity().getContentResolver().query(uContactsUri, null, null, null, strProjection);
  	       phoneCursor.moveToFirst();
  	   
  	       String name = "";
  	       String phoneNumber = "";
  	         

  	        // �ּҷ��� �̸�
  	        int nameColumn = phoneCursor.getColumnIndex(Phone.DISPLAY_NAME);
  	        // �ּҷ��� ��ȭ��ȣ
  	        int phoneColumn = phoneCursor.getColumnIndex(Phone.NUMBER);
  	           
  	        while(!phoneCursor.isAfterLast()){
  	            name = phoneCursor.getString(nameColumn);
  	            phoneNumber = phoneCursor.getString(phoneColumn);
  	            
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
  	           
  	           for(Map.Entry<String, String> s : contactList.entrySet()){
  	        	   String phone_number = s.getKey();
  	        	   phone_number = phone_number.replaceAll("-", "");
  	        	   String name = s.getValue();
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
  		//doInBackground���� ���������� publishProgress( progress ) �� ȣ�� ���־����� ���� 
  		super.onProgressUpdate(values);
  	}
  		
  	@Override
  	protected void onPostExecute(String result) {
  		//doInBackground�� ó���� ��� ������ �ش� �޼ҵ尡 return �Ǿ����� �� ���ϰ��� ����� ���鼭 �� �޼ҵ尡 �����
  		//AsyncTask�� ��ҵǰų� ���� �߻��ÿ� parameter �� null �� ����
  		//�� onPostExecute �� ������ UIThread���� �����
  		super.onPostExecute(result);
  			
  		//System.out.println(result);
 
  		
  		
  		//���� result ���ڿ� ���� ServerDown�̸� ���� �ٿ� �佺Ʈ ���� ���� 
		if(result.equals("ServerDown")){
			Toast toast = Toast.makeText(getActivity() , "������ �ٿ�ƽ��ϴ�", Toast.LENGTH_LONG);
			toast.show();
		}
		else{//���� �ٿ��� �ƴ϶�� ���������� �Ľ��ؼ� ����� �����س� 
			//Oracle DB������ ��� result�� �Ľ��ؼ� dB_Phone_List�� ��´�
			
			//��Ʈ��ŷ�� ���Ӱ� ���������� ���� sqlite�κ��� ��� �����ߵ�.(���ΰ�ħ)
	  		//Sqlite �κ��� static�����ؼ� BaseActivity�� �����ؼ� ������ ����
	  		db_Handler.removeData();	
	  		IntroActivity.sqlite_Name.clear();
	  		IntroActivity.sqlite_Code.clear();
	  		IntroActivity.sqlite_Phone_Number.clear();
			
			try{
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
		  		if(cursor != null){
			  		while(cursor.moveToNext()){
			  			//Sqlite �κ��� static�����ؼ� �����ؼ� ������ ����
			  			IntroActivity.sqlite_Code.add(cursor.getString(0));
			  			IntroActivity.sqlite_Name.add(cursor.getString(1));
			  			IntroActivity.sqlite_Phone_Number.add(cursor.getString(2));
			  		}
		  		}
	  		
			}catch(Exception e){
				e.printStackTrace();

			}
			finally{
				System.out.println("Ŀ�� Ŭ��¡");
				
				if(cursor!=null && !cursor.isClosed()){
					cursor.close();
				}
			}
	  		//adapt.notifyDataSetChanged();
	  		//menuList.clearChoices();
	  		//menuList.invalidateViews();
	  		//menuList.postInvalidate();
			//Context c = getActivity();
				
			System.out.println("���ΰ�ħ �佺Ʈ ���� ");
	  		Toast toast = Toast.makeText(mActivity, "���ΰ�ħ�� �Ϸ�Ǿ����ϴ�", Toast.LENGTH_SHORT);
			toast.show();
			
			//recreate�� ���ؼ� ���ΰ�ħ �Ȱ��� ListView�� ���̰� �۾�
			mActivity.recreate();
			
			System.out.println("���ΰ�ħ ��Ʈ��ũ �۾��Ϸ�");
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

  	//������ Sqlite�� �ݾ��� �׸��� ģ�������� ��Ʈ�ʵ� �� null�� ��ȯ�� (�ȱ׷��� ȣ��ɶ����� �޸𸮽���)
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

	    if(CustomAdapter.friend_icon.size() != 0){
	    	for(int i=0; i<CustomAdapter.friend_icon.size();i++){
	    		CustomAdapter.friend_icon.get(i).setImageBitmap(null);
	    	}
	    }
	}

	//���� �Ʒ����ʹ� �ڵ��� ��鸲 ���� üũ �κ� 
	@Override
	 public void onStart() {
       super.onStart();
       if (accelerormeterSensor != null)
       	sensorManager.registerListener(this, accelerormeterSensor, SensorManager.SENSOR_DELAY_GAME);
	 }

	 @Override
	 public void onStop() {
		 super.onStop();
		 if (sensorManager != null)
			 sensorManager.unregisterListener(this);
	 }
	
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
           long currentTime = System.currentTimeMillis();
           long gabOfTime = (currentTime - lastTime);
           if (gabOfTime > 100) {
               lastTime = currentTime;
               x = event.values[SensorManager.DATA_X];
               y = event.values[SensorManager.DATA_Y];
               z = event.values[SensorManager.DATA_Z];

               speed = Math.abs(x + y + z - lastX - lastY - lastZ) / gabOfTime * 10000;

               if (speed > SHAKE_THRESHOLD) {
                   // �̺�Ʈ�߻�!!
	               	//Toast toast = Toast.makeText(getActivity(), "����~ �����~", Toast.LENGTH_SHORT);
	           		//toast.show();
	           		//�����߻� 
	               	Vibrator vibe = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
	           		vibe.vibrate(100);
	           		
	           		if(connect.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED 
							|| connect.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED	
							 ){
									new Networking().execute();
									System.out.println("��鸲 ���ΰ�ħ ��Ʈ��ŷ ����");
									System.out.println("��鸲 ����: "+SHAKE_THRESHOLD);
							}
							else{
								Toast toast = Toast.makeText(getActivity(), "���ͳ� ������ ������ϴ�", Toast.LENGTH_SHORT);
								toast.show();
							}		
	           		
               }

               lastX = event.values[DATA_X];
               lastY = event.values[DATA_Y];
               lastZ = event.values[DATA_Z];
           }
		}
	}
	
}