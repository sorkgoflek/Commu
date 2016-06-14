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
	
	SharedPreferences introCheck;//어플을 처음 실행했을때는 인트로액티비티가 나오고 두번째부터는 로고액티비티가 나오도록 설정하기 위한 SharedPreferences
	String check;
	Button login_go_button;//인트로에서 로그인화면으로 가기 위한 버튼 
	HttpClient client;
	private int imageArra[] = { R.drawable.intro_clock2, R.drawable.intro_phone};//인트로 화면 채워질 이미지파일
	
	
	//모든 url이 공통적으로 사용하는 부분////////////////// 
	//public static String url = "http://121.157.84.63:80";
	//public static String url = "http://113.198.80.185:80";
	public static String url = ServerIP.DB_SERVER;
	///////////////////////////////////////////////////////

	ConnectivityManager connect; 
	String requestURL1; 	
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
	public static ArrayList<String> sqlite_Code = new ArrayList<String>();
	public static ArrayList<String> sqlite_Name = new ArrayList<String>();
	public static ArrayList<String> sqlite_Phone_Number = new ArrayList<String>();	


	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		System.out.println("인트로시작");
		//화면 전체로 쓰기위한 설정 
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		//introCheck에 초기값 문자열 check로 설정  
		introCheck = getSharedPreferences("introChecking", MODE_PRIVATE);
		check = introCheck.getString("sp_check", "check");
		
		if(check == "check"){//"이 액티비티가 처음이면" 이라는 if문임 
			setContentView(R.layout.intro); //정상적인 intro 액티비티 시작해라 
			
			login_go_button = (Button)findViewById(R.id.login_go__button);
			
			//인트로 화면 설정
			ViewPagerAdapter adapter = new ViewPagerAdapter(this, imageArra);
			ViewPager myPager = (ViewPager) findViewById(R.id.myfivepanelpager);
			myPager.setAdapter(adapter);
			myPager.setCurrentItem(0);
			
			//처음 인트로 액티비티가 시작되고 SharedPreferences에 checkAgain 이라는 문자열을 추가시켜
			//그래서 어플을 두번째 시작했을때는 똑같은 인트로 액티비티가 아닌 로고 액티비티로 가도록 설정하는것 
			SharedPreferences.Editor editor = introCheck.edit();
			editor.putString("sp_check", "checkAgain");
			editor.commit();
			
			//커뮤 시작하기 버튼이 눌러진다면 
			login_go_button.setOnClickListener(new View.OnClickListener() {
				//로그인 창에서 회원가입 버튼 클릭시 		
					@Override
					public void onClick(View v) {
						startActivity(new Intent(IntroActivity.this,LoginActivity.class));
						finish();
					}		
				});//end onClick()

			connect = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE); //인터넷이 연결되어있나 확인
			requestURL1 = url+"/lance/androidFriendList.jsp"; //네트워크 통신을 통해 갈 주소		
		
			//내 전화번호부에 있는 전화목록을 다 가져오게 하는 함수
			//내 전화번호부와 OracleDB의 전화번호부와 비교를 할거야 
			//그래서 일치하는것만 추려서 ListView에 나타내게 할거야 
			System.out.println("전화번호부 가져오기 함수호출직전");
			getContactData();
			
			//Sqlite DB 쓰기위해 오픈
			//Oracle DB에서 가져온 내용을 Sqlite에 저장할거야 
			//그래서 매번 네트워크 통신을 통해서 Oracle DB결과를 얻는게 아니라 
			//Sqlite의 내용을 바로 가져와 쓰는 형식
			//처음과 새로고침 누를때만 네트워크 통신을 통해 Oracle DB를 다시 Sqlite에 저장
			try {
				db_Handler = DB_Handler.open(this);
			} catch (SQLException e) {
				
				e.printStackTrace();
				System.out.println("db open에 문제있어");
			}
			
			//인터넷이 연결되어 있는지 체크하고 연결되었다면 네트워킹을 통해 
			//Oracle DB결과를 얻어올거야 
			if(connect.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED 
					|| connect.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED	
					 ){
						
							new Networking().execute();
							System.out.println("네트워킹 시작");
							
					}
							
						else{
							Toast toast = Toast.makeText(IntroActivity.this , "인터넷연결이 끊겼습니다", Toast.LENGTH_SHORT);
							toast.show();
						}	
		}//check end
		
		else{//만약 처음이 아니면 로고 액티비티로 가라 
			startActivity(new Intent(IntroActivity.this,LogoActivity.class));
			finish();
		}
	}
	
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
	       phoneCursor = getContentResolver().query(uContactsUri, null, null, null, strProjection);
	       phoneCursor.moveToFirst();
	       
	       System.out.println("전화번호부 함수 쿼리문 날리기 까지 성공");
	       
	       String name = "";
	       String phoneNumber = "";
	         

	        // 주소록의 이름
	        int nameColumn = phoneCursor.getColumnIndex(Phone.DISPLAY_NAME);
	        // 주소록의 전화번호
	        int phoneColumn = phoneCursor.getColumnIndex(Phone.NUMBER);
	           
	        System.out.println("전화번호부 함수 주소록 이름,번호 옮겨 저장까지 완료");
	        
	        while(!phoneCursor.isAfterLast()){
	            name = phoneCursor.getString(nameColumn);
	            phoneNumber = phoneCursor.getString(phoneColumn);
	            
	            ///////////////확인용 출력///////////
	            //System.out.println("전화번호부 번호 : "+phoneNumber);
	            //System.out.println("전화번호부 이름 : "+name);
	              
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
	           System.out.println("최종 finally까지 무사히 도착");
	           for(Map.Entry<String, String> s : contactList.entrySet()){
	        	   String phone_number = s.getKey();
	        	   phone_number = phone_number.replaceAll("-", "");
	        	   String name = s.getValue();
	        	   
	        	   //System.out.println("최종 집어넣을 번호 : "+phone_number);
	        	   //System.out.println("최종 집어넣을 이름 : "+name);
	        	   
	        	   //최종적으로 번호와 이름을 집어넣음
	        	   phone_Book_List[index++] = phone_number;
	        	   phone_Book_List[index++] = name;
	           }   
	        }
	        
	     }
	}// getContactData end	
	
	//AsyncTask를 써서 네트워킹 작업 
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
			String result =null;
			
			try {
					//Oracle DB통해 얻어진 결과가 result에 담겨진다
					result = sendData(requestURL1);
					
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
		/*
		//네트워킹을 새롭게 긁을때마다 기존 sqlite부분을 모두 지워야돼.(새로고침)
		db_Handler.removeData();	
		sqlite_Name.clear();
		sqlite_Code.clear();
		sqlite_Phone_Number.clear();
		*/
		//만약 result 문자열 값이 ServerDown이면 서버 다운 토스트 띄우고 끝냄 
		if(result.equals("ServerDown")){
			Toast toast = Toast.makeText(IntroActivity.this , "서버가 다운됐습니다", Toast.LENGTH_LONG);
			toast.show();
		}
		else{//서버 다운이 아니라면 정상적으로 파싱해서 결과값 도출해냄 	
			//Oracle DB내용이 담긴 result를 파싱해서 dB_Phone_List에 담는다
			
			//네트워킹을 새롭게 긁을때마다 기존 sqlite부분을 모두 지워야돼.(새로고침)
			db_Handler.removeData();	
			sqlite_Name.clear();
			sqlite_Code.clear();
			sqlite_Phone_Number.clear();
			
			
			try{
			
			//System.out.println(result);
			
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
				
			System.out.println("네트워크 작업완료");
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
		
		//서버가 끊겼을경우 5초의 타임을 체크
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
	
}
