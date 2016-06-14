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
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Button;
import android.widget.Toast;

import com.example.slidingsimplesample.R;
import com.lance.commu.imagetransfer.ImageTransferActivity;
import com.lance.commu.intro.IntroActivity;
import com.lance.commu.login.LoginActivity;
import com.lance.commu.logo.LogoActivity;
import com.lance.commu.passwordChange.PasswordChangeActivity;
import com.lance.commu.sqliteDB.DB_Handler;
 
public class Fragment_Set extends Fragment implements SensorEventListener{
	
	Button reset_button, image_button, pwchange_button ,delete_button;
	Button strong_button, middle_button, weak_button;
	ConnectivityManager connect;
	String requestURL1;
	String requestURL2;
	String deleteCheck;
	String id;
	int index =0;
	private static HashMap<String, String> contactList; //내 전화번호부안의 이름,번호 저장됨
	String[] dB_Phone_List; //네트워크 통신을 통해 Oracle DB에서 얻어진 번호들 저장
	String[] phone_Book_List = new String[2500]; //내 전화번호부안의 이름,번호 저장됨
	
	//친구목록 리스트뷰에 오름차순으로 정렬하기 위해 거치는 중간단계 
	ArrayList<String> for_Sort_List1 = new ArrayList<String>();
	ArrayList<String> for_Sort_List2 = new ArrayList<String>();
	static String[] for_Sort_Array1 ;
	
	//최종적으로 Oracle DB와 내 전화번호부와 일치하는 번호,이름 저장
	ArrayList<String> union_Phone_List_Number = new ArrayList<String>();
	ArrayList<String> union_Phone_List_Name = new ArrayList<String>();
	
	//Sqlite DB를 쓰기위한 설정
	DB_Handler db_Handler;
	Cursor cursor = null;
	//////////////////
	HttpClient client;
	HttpClient client2;
	SharedPreferences sp;
	
	//가속도 관련 변수/////////////////////
	private long lastTime;
    private float speed;
    private float lastX;
    private float lastY;
    private float lastZ;
    private float x, y, z;
 
	private static final int DATA_X = SensorManager.DATA_X;
    private static final int DATA_Y = SensorManager.DATA_Y;
    private static final int DATA_Z = SensorManager.DATA_Z;
 
    private SensorManager sensorManager;
    private Sensor accelerormeterSensor;
	///////////////////////////////////
	
	
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	View v = inflater.inflate(R.layout.fragment_set, container, false);
    	System.out.println("설정 onCreateView 시작");
    	
    	//가속도 
        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        accelerormeterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        //////////////////////////////////////////
    	
    	id = LoginActivity.rememberRealId; // LoginActivity를 통해서 내 아이디 값을 넘겨받음 
        if(id == null){//만약 자동로그인을 통해서 설정창에 들어오면 LoginActivity를 거치지 않고 LogoActivity에서 오기 때문에 null값이다 
        	//그래서 LogoActivity에서 얻은 아이디 값을 넘겨받는다. 
        	id = LogoActivity.rememberRealId2;
        	
        }
    	
        System.out.println("설정창임 ID:"+id);
        
        connect = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE); //인터넷이 연결되어있나 확인
        requestURL1 = IntroActivity.url+"/lance/androidFriendList.jsp"; //새로고침을 위해 네트워크 통신을 통해 갈 주소
        requestURL2 = IntroActivity.url+"/lance/androidDelete.jsp"; //회원탈퇴를 위해 네트워크 통신을 통해 갈 주소 
        
        reset_button = (Button)v.findViewById(R.id.reset_button); //새로고침 버튼
        strong_button = (Button)v.findViewById(R.id.strong_button); //흔들림 감지 강
        middle_button = (Button)v.findViewById(R.id.middle_button); //흔들림 감지 중
        weak_button = (Button)v.findViewById(R.id.weak_button); //흔들림 감지 약
        image_button = (Button)v.findViewById(R.id.imagetranfer_button); //이미지 전송 버튼
        pwchange_button = (Button)v.findViewById(R.id.pwchange_button); //비번변경 버튼
        delete_button = (Button)v.findViewById(R.id.delete_button); //회원탈퇴 버튼
        
        //내 전화번호부에 있는 전화목록을 다 가져오게 하는 함수
		//내 전화번호부와 OracleDB의 전화번호부와 비교를 할거야 
		//그래서 일치하는것만 추려서 ListView에 나타내게 할거야 
		getContactData();
        
		//Sqlite DB 쓰기위해 오픈
		//Oracle DB에서 가져온 내용을 Sqlite에 저장할거야 
		//그래서 매번 네트워크 통신을 통해서 Oracle DB결과를 얻는게 아니라 
		//Sqlite의 내용을 바로 가져와 쓰는 형식
		//처음과 새로고침 누를때만 네트워크 통신을 통해 Oracle DB를 다시 Sqlite에 저장
		try {
			db_Handler = DB_Handler.open(getActivity());
		} catch (SQLException e) {

			e.printStackTrace();
			System.out.println("db open에 문제있어");
		}
		

		//새로고침 버튼이 눌리면 여기서 네트워크 작업 실시해야함 
		reset_button.setOnClickListener(new View.OnClickListener() { 		
				@Override
				public void onClick(View v) {
					
					if(connect.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED 
							|| connect.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED	
							 ){
									new Networking1().execute();
									System.out.println("새로고침 네트워킹 시작");
								
							}
							
							else{
								Toast toast = Toast.makeText(getActivity(), "인터넷연결이 끊겼습니다", Toast.LENGTH_SHORT);
								toast.show();
							}		
					
				}		
			});//end onClick()
		
		//흔들림 감지 강 버튼이 눌리면  
		strong_button.setOnClickListener(new View.OnClickListener() { 		
				@Override
				public void onClick(View v) {
					Fragment_FriendList.SHAKE_THRESHOLD = 1500;
					Toast toast = Toast.makeText(getActivity(), "약하게 흔들면 새로고침", Toast.LENGTH_SHORT);
					toast.show();
					
					
				}		
			});//end onClick()
		
		//흔들림 감지 중 버튼이 눌리면  
		middle_button.setOnClickListener(new View.OnClickListener() { 		
				@Override
				public void onClick(View v) {
					Fragment_FriendList.SHAKE_THRESHOLD = 2500;
					Toast toast = Toast.makeText(getActivity(), "중간세기로 흔들면 새로고침", Toast.LENGTH_SHORT);
					toast.show();
					
				}		
			});//end onClick()
		
		//흔들림 감지 약 버튼이 눌리면  
		weak_button.setOnClickListener(new View.OnClickListener() { 		
				@Override
				public void onClick(View v) {
					Fragment_FriendList.SHAKE_THRESHOLD = 3500;
					Toast toast = Toast.makeText(getActivity(), "강하게 흔들면 새로고침", Toast.LENGTH_SHORT);
					toast.show();
					
					
				}		
			});//end onClick()
		
		
		
		//이미지전송 버튼이 눌리면 ImageTransferActivity로 이동 
		image_button.setOnClickListener(new View.OnClickListener() { 		
				@Override
				public void onClick(View v) {
							
							//로그인 성공후 아이디를 기억해서 DB의 Gallery writer가 내 아이디값이 되도록 설정해야함 
							Intent intent = new Intent(getActivity(),ImageTransferActivity.class);
							intent.putExtra("id", id);
							startActivity(intent);
							//getActivity().finish();
				}		
			});//end onClick()
		
		//비번변경 버튼이 눌리면 PasswordChangeActivity로 이동 
		pwchange_button.setOnClickListener(new View.OnClickListener() { 		
				@Override
				public void onClick(View v) {
							
							//로그인 성공후 아이디를 기억해서 비번 변경할 내 계정을 찾아야함  
							Intent intent = new Intent(getActivity(),PasswordChangeActivity.class);
							intent.putExtra("id", id);
							startActivity(intent);
				}		
			});//end onClick()
		

		
		//회원탈퇴 버튼이 눌리면 여기서 네트워크 작업 실시해야함 
		delete_button.setOnClickListener(new View.OnClickListener() { 		
				@Override
				public void onClick(View v) {
					
					//회원탈퇴 시 다이얼로그 처리해서 한번 더 확인함
			        AlertDialog.Builder alert_confirm = new AlertDialog.Builder(getActivity());
			        alert_confirm.setTitle("Commu 탈퇴"); 
			        alert_confirm.setMessage("Commu 탈퇴 하시겠어요?").setCancelable(false).setPositiveButton("Commu 탈퇴",
			        new DialogInterface.OnClickListener() {

						@Override
			            public void onClick(DialogInterface dialog, int which) {
			                // 회원탈퇴
							if(connect.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED 
									|| connect.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED	
									 ){
											new Networking2().execute();
											System.out.println("회원탈퇴 네트워킹 시작");
										
									}
									
									else{
										Toast toast = Toast.makeText(getActivity(), "인터넷연결이 끊겼습니다", Toast.LENGTH_SHORT);
										toast.show();
									}		
							
			            }
			        }).setNegativeButton("취소",
			        new DialogInterface.OnClickListener() {
			            @Override
			            public void onClick(DialogInterface dialog, int which) {
			                // 'No'
			            return;
			            }
			        });
			        AlertDialog alert = alert_confirm.create();
			        alert.show();
						
				}		
			});//end onClick()
		
		System.out.println("설정 onCreateView 끝");
        return v;
    }//onCreateView end
 
    //내 전화번호부의 이름과 번호를 가져오게 하는함수 
  	private void getContactData(){
  		Cursor phoneCursor = null;
  	     contactList = new HashMap<String,String>();
  	     
  	   try{
  	          // 주소록이 저장된 URI
  	        Uri uContactsUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
  	   
  	       // 주소록의 이름과 전화번호의 열 이름
  	       String strProjection = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME;
  	   
  	       // 주소록을 얻기 위한 쿼리문을 날리고 커서를 리턴
  	       phoneCursor = getActivity().getContentResolver().query(uContactsUri, null, null, null, strProjection);
  	       phoneCursor.moveToFirst();
  	   
  	       String name = "";
  	       String phoneNumber = "";
  	         

  	        // 주소록의 이름
  	        int nameColumn = phoneCursor.getColumnIndex(Phone.DISPLAY_NAME);
  	        // 주소록의 전화번호
  	        int phoneColumn = phoneCursor.getColumnIndex(Phone.NUMBER);
  	           
  	        while(!phoneCursor.isAfterLast()){
  	            name = phoneCursor.getString(nameColumn);
  	            phoneNumber = phoneCursor.getString(phoneColumn);
  	            
  	            // HashMap에 data 넣음 
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
  	        	   //최종적으로 번호와 이름을 집어넣음
  	        	   phone_Book_List[index++] = phone_number;
  	        	   phone_Book_List[index++] = name;
  	           }
  	           
  	        }
  	     }
  	}// getContactData end    
    
  //AsyncTask를 써서 네트워킹 작업 
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
  					//Oracle DB통해 얻어진 결과가 result에 담겨진다
  					result = sendData1(requestURL1);
  					
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
  			
  		//System.out.println(result);
 
  		
  		
  		//만약 result 문자열 값이 ServerDown이면 서버 다운 토스트 띄우고 끝냄 
		if(result.equals("ServerDown")){
			Toast toast = Toast.makeText(getActivity() , "서버가 다운됐습니다", Toast.LENGTH_LONG);
			toast.show();
		} 		
		else{//서버 다운이 아니라면 정상적으로 파싱해서 결과값 도출해냄 
	  		//Oracle DB내용이 담긴 result를 파싱해서 dB_Phone_List에 담는다
			
			//네트워킹을 새롭게 긁을때마다 기존 sqlite부분을 모두 지워야돼.(새로고침)
	  		//Sqlite 부분은 static으로해서 BaseActivity와 공유해서 쓰도록 설정
	  		db_Handler.removeData();	
	  		IntroActivity.sqlite_Name.clear();
	  		IntroActivity.sqlite_Code.clear();
	  		IntroActivity.sqlite_Phone_Number.clear();
			
			try{
		  		dB_Phone_List = result.split(",");
		  		dB_Phone_List[0] = dB_Phone_List[0].substring(25, 38);
		  		dB_Phone_List[dB_Phone_List.length-1] = dB_Phone_List[dB_Phone_List.length-1].substring(1,14);
		  		
		  		//핸펀 번호의 '-' 붙은것을 모두 제거 
		  		for(int i=0;i<dB_Phone_List.length;i++){
		  			dB_Phone_List[i] = dB_Phone_List[i].trim();
		  			dB_Phone_List[i] = dB_Phone_List[i].replaceAll("-", "");
		
		  		}
		  		
		  		//OracleDB번호와 내 전화번호부의 번호와 하나씩 비교해서 일치하는것들만 추려낸다
		  		for(int i=0;i<index;i++){
		  			for(int j=0; j<dB_Phone_List.length; j++){
		  				if(phone_Book_List[i].equals(dB_Phone_List[j])){
		  					
		  					//일치하는 것들을 모아서 오름차순 정렬을 위해 
		  					//for_Sort_List1에 이름과 번호를 같이 옮겨담는다
		  					//(이름과 번호가 한꺼번에 정렬되게 하기위해서)
		  					for_Sort_List1.add(phone_Book_List[i+1]+","+phone_Book_List[i]);
		  					
		  				}
		  			}
		  		}
		  		//정렬작업
		  		Collections.sort(for_Sort_List1);
		  		
		  		//정렬된것을 , 로 구분지어서 for_Sort_List2에 담는다 (이름따로 번호따로)
		  		for(int i=0; i<for_Sort_List1.size();i++){
		  			for_Sort_Array1 = for_Sort_List1.get(i).split(",");
		  		
		  			for_Sort_List2.add(for_Sort_Array1[0]);
		  			for_Sort_List2.add(for_Sort_Array1[1]);
		  		}
		  				
		  		//최종적으로 산출된 결과를 저장한다. 
		  		for(int i=0; i<for_Sort_List2.size();i+=2){
		  			union_Phone_List_Name.add(for_Sort_List2.get(i));
		  			union_Phone_List_Number.add(for_Sort_List2.get(i+1));
		  		
		  		}
		  	
		  		//네트워크 통신을 통해 얻은 결과를 Sqlite에 저장한다
		  		//최종 산출된 이름과 번호를 sqlite에 옮겨서 저장한다
		  		for(int i=0; i<union_Phone_List_Name.size();i++){
		  			db_Handler.insert(union_Phone_List_Name.get(i), union_Phone_List_Number.get(i));
		  		}
		  		
		  		cursor = db_Handler.selectAll();
		  		while(cursor.moveToNext()){
		  			//Sqlite 부분은 static으로해서 공유해서 쓰도록 설정
		  			IntroActivity.sqlite_Code.add(cursor.getString(0));
		  			IntroActivity.sqlite_Name.add(cursor.getString(1));
		  			IntroActivity.sqlite_Phone_Number.add(cursor.getString(2));
		  		}
	  		
			}catch(Exception e){
				e.printStackTrace();

			}
	  		finally{
	  			cursor.close();
	  		}
	  		Toast toast = Toast.makeText(getActivity(), "새로고침이 완료되었습니다", Toast.LENGTH_SHORT);
			toast.show();
			System.out.println("새로고침 네트워크 작업완료");
		}
  	}
  		
  		
  	@Override
  	protected void onCancelled() {
  		//AsyncTask를 강제로 취소했을때 호출
  		//껐을때 다시 초기화
  		for_Sort_List1.clear();
  		for_Sort_List2.clear();
  		union_Phone_List_Name.clear();
  		union_Phone_List_Number.clear();
  		super.onCancelled();
  		}
  	}// Networking1 end
  	
  	//새로고침 네트워킹 
  	private String sendData1(String requestURL1) throws ClientProtocolException, IOException{
  		
  		client = new DefaultHttpClient();
  		HttpPost request = makeHttpPost1(requestURL1);
  		ResponseHandler<String> reshandler = new BasicResponseHandler();
  		String result = client.execute(request, reshandler);
  		return result;
  	}
  		
  	private HttpPost makeHttpPost1(String requestURL1){
  		
  		HttpPost request = new HttpPost(requestURL1);
  		List<NameValuePair> dataList = new ArrayList<NameValuePair>();
  		
  		//서버가 끊겼을경우 5초의 타임을 체크
  		HttpParams param = client.getParams();
    	HttpConnectionParams.setConnectionTimeout(param, 5000);
    	HttpConnectionParams.setSoTimeout(param, 5000);
    	
      	request.setEntity(makeEntity1(dataList));
      	return request;
  	}
  	
  	private HttpEntity makeEntity1(List<NameValuePair> dataList){
  		
  		HttpEntity result = null;
  		
  		try{
  			result = new UrlEncodedFormEntity(dataList,"UTF-8");
  		}catch(UnsupportedEncodingException e){
  			e.printStackTrace();
  			System.out.println("언서포티드인코딩 exception");
  		}

  		return result;
  	}    
  	/////////////////////////////////////////////////////
  	/////////////////////////////////////////////////////
  	
  	//회원탈퇴 네크워킹
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
				Toast toast = Toast.makeText(getActivity() , "서버가 다운됐습니다", Toast.LENGTH_LONG);
				toast.show();
			}
			else{//서버 다운이 아니라면 정상적으로 파싱해서 결과값 도출해냄 
				//회원탈퇴작업 후에 return되는 값이 1이면 정상탈퇴됨 
				
				String[] list;
				list = result.split(">");
				deleteCheck = list[2].substring(2, 3);  
				//System.out.println(deleteCheck);
				
				if(deleteCheck.equals("1") ){
					
					Toast toast = Toast.makeText(getActivity() , "Commu 탈퇴되었습니다.", Toast.LENGTH_SHORT);
					toast.show();
					LoginActivity.flag = false; //자동로그인 해제
					
					sp = getActivity().getSharedPreferences("PreName", getActivity().MODE_PRIVATE);
					SharedPreferences.Editor editor = sp.edit();
					editor.putString("sp_id", "1"); //값 초기화 
					editor.putString("sp_pw", "1"); //값 초기화 
					editor.commit();
						
					System.out.println("탈퇴후 sp_id:"+sp.getString("sp_id", "1"));
					System.out.println("탈퇴후 sp_pw:"+sp.getString("sp_pw", "1"));
					
					getActivity().finish();
				}
				
				else{
					Toast toast = Toast.makeText(getActivity() , "다른문제가 생겨 탈퇴처리가 안됐습니다.", Toast.LENGTH_SHORT);
					toast.show();
				}
				
				System.out.println("회원탈퇴 네트워킹 끝");
			}
		}
		
		@Override
		protected void onCancelled() {
			//AsyncTask를 강제로 취소했을때 호출
			super.onCancelled();
			sp = getActivity().getSharedPreferences("PreName", getActivity().MODE_PRIVATE);
			SharedPreferences.Editor editor = sp.edit();
			editor.putString("sp_id", "1"); //값 초기화 
			editor.putString("sp_pw", "1"); //값 초기화 
			editor.commit();
		}

	}  	  	
  	//회원탈퇴 네트워킹
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
	
	
	//끝나면 Sqlite를 닫아줘
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
	
	//여기 아래부터는 핸드폰 흔들림 감지 체크 부분 
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

               if (speed > Fragment_FriendList.SHAKE_THRESHOLD) {
                   // 이벤트발생!!
	               	//Toast toast = Toast.makeText(getActivity(), "흔들어~ 흔들어봐~", Toast.LENGTH_SHORT);
	           		//toast.show();
	           		//진동발생 
            	    System.out.println("설정창에서 강도크기 : "+Fragment_FriendList.SHAKE_THRESHOLD);
	               	Vibrator vibe = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
	           		vibe.vibrate(100);
	           				
               }

               lastX = event.values[DATA_X];
               lastY = event.values[DATA_Y];
               lastZ = event.values[DATA_Z];
           }
		}
	}		
}