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
	String phone_number; //ÃÖÁ¾ÀûÀ¸·Î ³×Æ®¿öÅ© ÅëÇØ º¸³»´Â ÇÚÆÝ¹øÈ£ 
	String PhoneNumber; //³» ÇÚÆÝ¹øÈ£ ÀÚµ¿À¸·Î ¾ò¾î¿Í¼­ ÀúÀåµÇ´Â º¯¼ö
	String gender;
	String idCheck; //¾ÆÀÌµð°¡ Áßº¹ÀÎÁö Ã¼Å©ÇÏ´Â º¯¼ö 
	String idCheck_Hangel; //1Â÷·Î ¾ÆÀÌµð ÇÑ±ÛÃ¼Å©
	static boolean idPass = false; //1Â÷ ¾ÆÀÌµð ÇÑ±ÛÅë°ú ¿©ºÎÈ®ÀÎ
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
		edit_id.setPrivateImeOptions("defaultInputmode=english;"); //±âº» ÀÔ·ÂÃ¢À» ¿µ¾îÆÇÀ¸·Î
		
		edit_pass = (EditText)findViewById(R.id.pass);
		edit_name = (EditText)findViewById(R.id.name);
		//edit_phone_number  = (EditText)findViewById(R.id.phone_number);
		rb_man = (RadioButton)findViewById(R.id.radiobutton_man);
		rb_woman = (RadioButton)findViewById(R.id.radiobutton_woman);
		
		insert_button = (Button)findViewById(R.id.insert_button);
		idCheck_button = (Button)findViewById(R.id.idCheck_button);
		
		requestURL1 = IntroActivity.url+"/lance/androidJoin.jsp";
		requestURL2 = IntroActivity.url+"/lance/androidIdCheck.jsp";
		
		connect = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE); //ÀÎÅÍ³ÝÀÌ ¿¬°áµÇ¾îÀÖ³ª È®ÀÎ 
		
		//³» ÇÚµåÆù ¹øÈ£ °¡Á®¿À±â 
		TelephonyManager systemService = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
		PhoneNumber = systemService.getLine1Number();
		PhoneNumber = PhoneNumber.substring(PhoneNumber.length()-10,PhoneNumber.length());
		PhoneNumber="0"+PhoneNumber;
		PhoneNumber = PhoneNumberUtils.formatNumber(PhoneNumber);
		//edit_phone_number.setHint(PhoneNumber);
		//edit_phone_number.setEnabled(false); 
		
		//////////////////////////////////////////////////////////////
		idCheck_button.setOnClickListener(new View.OnClickListener() {
		//¾ÆÀÌµð Áßº¹Ã¼Å© ¹öÆ° Å¬¸¯½Ã 	
			@Override
			public void onClick(View v) {
				
				id = edit_id.getText().toString(); //³»°¡ ÀÔ·ÂÇÑ ¾ÆÀÌµð °ª 
				
				if(id.length() ==0 || id.trim().length() ==0){
					Toast toast = Toast.makeText(SignUpActivity.this , "¾ÆÀÌµð¸¦ ÀÔ·ÂÇÏ¼¼¿ä", Toast.LENGTH_SHORT);
					toast.show();
					edit_id.requestFocus();
				}
				
				else{
					//1Â÷·Î ÇÑ±Û °É·¯³¿ 
					for(int i=0;i<id.length();i++){
					     if(Character.getType(id.charAt(i)) == 5){ 
					    	 System.out.println("ÇÑ±ÛÀº ¾ÈµË´Ï´Ù");
					    	 Toast toast = Toast.makeText(SignUpActivity.this , "¿µ¹® or ¼ýÀÚ or ¿µ¹®+¼ýÀÚ ¸¸ °¡´É", Toast.LENGTH_SHORT);
							 toast.show();
					    	 idPass = false;
					     	break;
					     }
					     else{
					    	 idCheck_Hangel = id;
					    	 idPass = true;
					     }
					  }
					
					if(idPass == true){//1Â÷·Î °É·¯Áø°Í¸¸ 
						//2Â÷·Î Æ¯¼ö¹®ÀÚ °É·¯³¿ 
						Pattern p = Pattern.compile(".*[^°¡-ÆRa-zA-Z0-9].*");
						Matcher m = p.matcher(idCheck_Hangel);
				   
						if(m.matches()) {
							Toast toast = Toast.makeText(SignUpActivity.this , "¿µ¹® or ¼ýÀÚ or ¿µ¹®+¼ýÀÚ ¸¸ °¡´É", Toast.LENGTH_SHORT);
							toast.show();
							System.out.println("¿µ¹® or ¼ýÀÚ or ¿µ¹®+¼ýÀÚ ¸¸ °¡´É");
						} 
						else {
						        System.out.println("¾ÆÀÌµð °¡´É ÀÌÁ¦ Áßº¹È®ÀÎ¸¸ÇÏ¸é‰Î");
						
						      //ÀÎÅÍ³ÝÀÌ ¿¬°áµÅ ÀÖ³ª È®ÀÎ 
								if(connect.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED 
								|| connect.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED	
								 ){
									new Networking2().execute();
								}
								
								else{
									Toast toast = Toast.makeText(SignUpActivity.this , "ÀÎÅÍ³Ý¿¬°áÀÌ ²÷°å½À´Ï´Ù", Toast.LENGTH_SHORT);
									toast.show();
								}	
						
						}
					}

			}

		}		
	});//end onClick()
		

		/////////////////////////////////////////////////////////////
		insert_button.setOnClickListener(new View.OnClickListener() {
		//È¸¿ø°¡ÀÔ ¹öÆ° Å¬¸¯½Ã 		
				@Override
				public void onClick(View v) {
					
					 id = edit_id.getText().toString();
					 pass = edit_pass.getText().toString();
					 name = edit_name.getText().toString();
					 //phone_number = edit_phone_number.getText().toString();
					 phone_number = PhoneNumber;
					 if(rb_man.isChecked())
						gender="³²ÀÚ";			
					if(rb_woman.isChecked())
						gender="¿©ÀÚ";

					 
					 
					if(id.length() ==0 || id.trim().length() ==0){
						Toast toast = Toast.makeText(SignUpActivity.this , "¾ÆÀÌµð¸¦ ÀÔ·ÂÇÏ¼¼¿ä", Toast.LENGTH_SHORT);
						toast.show();
						edit_id.requestFocus();
					}
					
					else if(pass.length() ==0 || pass.trim().length() ==0){
						Toast toast = Toast.makeText(SignUpActivity.this , "ÆÐ½º¿öµå¸¦ ÀÔ·ÂÇÏ¼¼¿ä", Toast.LENGTH_SHORT);
						toast.show();
						edit_pass.requestFocus();
					}
					
					else if(name.length() ==0 || name.trim().length() ==0){
						Toast toast = Toast.makeText(SignUpActivity.this , "ÀÌ¸§À» ÀÔ·ÂÇÏ¼¼¿ä", Toast.LENGTH_SHORT);
						toast.show();
						edit_name.requestFocus();
					}

					else{
						//1Â÷·Î ÇÑ±Û °É·¯³¿ 
						for(int i=0;i<id.length();i++){
						     if(Character.getType(id.charAt(i)) == 5){ 
						    	 System.out.println("ÇÑ±ÛÀº ¾ÈµË´Ï´Ù");
						    	 Toast toast = Toast.makeText(SignUpActivity.this , "¿µ¹® or ¼ýÀÚ or ¿µ¹®+¼ýÀÚ ¸¸ °¡´É", Toast.LENGTH_SHORT);
								 toast.show();
						    	 idPass = false;
						     	break;
						     }
						     else{
						    	 idCheck_Hangel = id;
						    	 idPass = true;
						     }
						  }
						
						
						if(idPass == true){//1Â÷·Î °É·¯Áø°Í¸¸ 
							//2Â÷·Î Æ¯¼ö¹®ÀÚ °É·¯³¿ 
							Pattern p = Pattern.compile(".*[^°¡-ÆRa-zA-Z0-9].*");
							Matcher m = p.matcher(idCheck_Hangel);
					   
							if(m.matches()) {
								Toast toast = Toast.makeText(SignUpActivity.this , "¿µ¹® or ¼ýÀÚ or ¿µ¹®+¼ýÀÚ ¸¸ °¡´É", Toast.LENGTH_SHORT);
								toast.show();
								System.out.println("¿µ¹® or ¼ýÀÚ or ¿µ¹®+¼ýÀÚ ¸¸ °¡´É");
							} 
							else {
							        System.out.println("¾ÆÀÌµð °¡´É ÀÌÁ¦ Áßº¹È®ÀÎ¸¸ÇÏ¸é‰Î");
							
							      //ÀÎÅÍ³ÝÀÌ ¿¬°áµÅ ÀÖ³ª È®ÀÎ 
									if(connect.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED 
									|| connect.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED	
									 ){
										new Networking1().execute();
									}
									
									else{
										Toast toast = Toast.makeText(SignUpActivity.this , "ÀÎÅÍ³Ý¿¬°áÀÌ ²÷°å½À´Ï´Ù", Toast.LENGTH_SHORT);
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
			//doInBackground °¡ ¼öÇàµÇ±â Àü, Áï AsyncTask.execute()	¸¦ È£ÃâÇßÀ»¶§ °¡Á¤ ¸ÕÀú ¼öÇàµÇ´Â ³à¼® 
			//º¸Åë ÃÊ±âÈ­ ÀÛ¾÷À» ÇØÁØ´Ù
			super.onPreExecute();
		}
		
		@Override
		protected String doInBackground(URL... params) {
			//º°°³ÀÇ ½º·¹µå¿¡¼­ ÀÛ¾÷À» ½ÇÇàÇÔ 
			//AsyncTask.execute(params)¸¦ ÅëÇØ¼­ ¼öÇàÀÌ ‰Î 
			String result =null;
			
			try {
					result = sendData(id,pass,name,phone_number,gender,requestURL1);
					
			} catch (ClientProtocolException e) {
				
				e.printStackTrace();
				System.out.println("clientprotocol exception");
			} catch (IOException e) {
				
				//¼­¹ö°¡ ²¨Áö¸é IOExceptionÂÊÀ¸·Î ¿À°Ô‰Î 
				//±×·³ result°ª¿¡ ServerDown ÀÌ¶ó´Â ¹®ÀÚ¿­À» ³Ö°ÔÇØ 
				return "ServerDown"; 
			}

			onCancelled();
			return result;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			//doInBackground¿¡¼­ ÀÏÃ³¸®¸¦ ÇÏ´Ù°¡ Áß°£¿¡ UI ½º·¹µå¿¡¼­ Ã³¸®ÇÒ ÀÏÀÌ ÀÖ´Â °æ¿ì È£ÃâµÊ 
			//doInBackground¿¡¼­ ¼öÇàµÇ´Â ³»¿ëÀº UI ½º·¹µå¿¡¼­ ÀÛµ¿ÇÔ 
			//doInBackground¿¡¼­ ¸í½ÃÀûÀ¸·Î publishProgress( progress ) ¸¦ È£Ãâ ÇØÁÖ¾úÀ»¶§ ¼öÇà 
			super.onProgressUpdate(values);
		}
		
		@Override
		protected void onPostExecute(String result) {
			//doInBackgroundÀÇ Ã³¸®°¡ ¸ðµÎ ³¡³ª°í ÇØ´ç ¸Þ¼Òµå°¡ return µÇ¾úÀ»¶§ ±× ¸®ÅÏ°ªÀÌ ¿©±â·Î ¿À¸é¼­ ÀÌ ¸Þ¼Òµå°¡ ½ÇÇàµÊ
			//AsyncTask°¡ Ãë¼ÒµÇ°Å³ª ¿¹¿Ü ¹ß»ý½Ã¿¡ parameter ·Î null ÀÌ µé¾î¿È
			//ÀÌ onPostExecute ÀÇ ³»¿ëÀº UIThread¿¡¼­ ¼öÇàµÊ
			super.onPostExecute(result);
			
			
			//¸¸¾à result ¹®ÀÚ¿­ °ªÀÌ ServerDownÀÌ¸é ¼­¹ö ´Ù¿î Åä½ºÆ® ¶ç¿ì°í ³¡³¿ 
			if(result.equals("ServerDown")){
				Toast toast = Toast.makeText(SignUpActivity.this , "¼­¹ö°¡ ´Ù¿îµÆ½À´Ï´Ù", Toast.LENGTH_LONG);
				toast.show();
			}
			else{//¼­¹ö ´Ù¿îÀÌ ¾Æ´Ï¶ó¸é Á¤»óÀûÀ¸·Î ÆÄ½ÌÇØ¼­ °á°ú°ª µµÃâÇØ³¿ 
				//result °ª¿¡´Â jsp ³»¿ëÀÌ ¸ðµÎ µé¾î°¡ ÀÖ´Ù ±×Áß¿¡¼­ rsultºÎºÐ¸¸ ÃßÃâÇØ³»¾ß ÇÏ´Âµ¥ ... 
				//¿©±â¼­ result³»¿ëÀ» ÂÉ°³´Â ÀÛ¾÷ ½ÇÇà 
				String[] list;
				list = result.split(">");
				idCheck = list[2].substring(2, 7); // Áßº¹ or °¡´É  µÎ°³ÀÇ string °ªÀÌ ³ª¿È 
				System.out.println(idCheck);
				
				Toast toast_wait = Toast.makeText(SignUpActivity.this , "Àá½Ã¸¸ ±â´Ù·ÁÁÖ¼¼¿ä", Toast.LENGTH_SHORT);
				toast_wait.show();
				
				if(idCheck.equals("dupli") ){
					
					Toast toast = Toast.makeText(SignUpActivity.this , "¾ÆÀÌµð Áßº¹ÀÔ´Ï´Ù", Toast.LENGTH_SHORT);
					toast.show();
				}
				
				else if(idCheck.equals("duphn")){
					Toast toast = Toast.makeText(SignUpActivity.this , "1ÀÎ 1°èÁ¤¸¸ °¡´ÉÇÕ´Ï´Ù", Toast.LENGTH_SHORT);
					toast.show();
				}
				
				else{
					Toast toast = Toast.makeText(SignUpActivity.this , "ÃàÇÏÇÕ´Ï´Ù È¸¿ø°¡ÀÔÀÌ µÇ¼Ì½À´Ï´Ù", Toast.LENGTH_SHORT);
					toast.show();
					startActivity(new Intent(SignUpActivity.this,LoginActivity.class));
					finish();
				}
			}
		}	
		
		
		@Override
		protected void onCancelled() {
			//AsyncTask¸¦ °­Á¦·Î Ãë¼ÒÇßÀ»¶§ È£Ãâ
			super.onCancelled();
		}
	}
	///////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private class Networking2 extends AsyncTask<URL, Integer, String>{

		@Override
		protected void onPreExecute() { 
			//doInBackground °¡ ¼öÇàµÇ±â Àü, Áï AsyncTask.execute()	¸¦ È£ÃâÇßÀ»¶§ °¡Á¤ ¸ÕÀú ¼öÇàµÇ´Â ³à¼® 
			//º¸Åë ÃÊ±âÈ­ ÀÛ¾÷À» ÇØÁØ´Ù
			super.onPreExecute();
		}
		
		@Override
		protected String doInBackground(URL... params) {
			//º°°³ÀÇ ½º·¹µå¿¡¼­ ÀÛ¾÷À» ½ÇÇàÇÔ 
			//AsyncTask.execute(params)¸¦ ÅëÇØ¼­ ¼öÇàÀÌ ‰Î 
			String result =null;
			
			try {
				
					result = sendData2(id,requestURL2);
					
			
			} catch (ClientProtocolException e) {
				
				e.printStackTrace();
				System.out.println("clientprotocol exception");
			} catch (IOException e) {
				
				//¼­¹ö°¡ ²¨Áö¸é IOExceptionÂÊÀ¸·Î ¿À°Ô‰Î 
				//±×·³ result°ª¿¡ ServerDown ÀÌ¶ó´Â ¹®ÀÚ¿­À» ³Ö°ÔÇØ 
				return "ServerDown"; 
			}

			onCancelled();
			return result;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			//doInBackground¿¡¼­ ÀÏÃ³¸®¸¦ ÇÏ´Ù°¡ Áß°£¿¡ UI ½º·¹µå¿¡¼­ Ã³¸®ÇÒ ÀÏÀÌ ÀÖ´Â °æ¿ì È£ÃâµÊ 
			//doInBackground¿¡¼­ ¼öÇàµÇ´Â ³»¿ëÀº UI ½º·¹µå¿¡¼­ ÀÛµ¿ÇÔ 
			//doInBackground¿¡¼­ ¸í½ÃÀûÀ¸·Î publishProgress( progress ) ¸¦ È£Ãâ ÇØÁÖ¾úÀ»¶§ ¼öÇà 
			super.onProgressUpdate(values);
		}
		
		@Override
		protected void onPostExecute(String result) {
			//doInBackgroundÀÇ Ã³¸®°¡ ¸ðµÎ ³¡³ª°í ÇØ´ç ¸Þ¼Òµå°¡ return µÇ¾úÀ»¶§ ±× ¸®ÅÏ°ªÀÌ ¿©±â·Î ¿À¸é¼­ ÀÌ ¸Þ¼Òµå°¡ ½ÇÇàµÊ
			//AsyncTask°¡ Ãë¼ÒµÇ°Å³ª ¿¹¿Ü ¹ß»ý½Ã¿¡ parameter ·Î null ÀÌ µé¾î¿È
			//ÀÌ onPostExecute ÀÇ ³»¿ëÀº UIThread¿¡¼­ ¼öÇàµÊ
			super.onPostExecute(result);
			
			//¸¸¾à result ¹®ÀÚ¿­ °ªÀÌ ServerDownÀÌ¸é ¼­¹ö ´Ù¿î Åä½ºÆ® ¶ç¿ì°í ³¡³¿ 
			if(result.equals("ServerDown")){
				Toast toast = Toast.makeText(SignUpActivity.this , "¼­¹ö°¡ ´Ù¿îµÆ½À´Ï´Ù", Toast.LENGTH_LONG);
				toast.show();
			}
			else{//¼­¹ö ´Ù¿îÀÌ ¾Æ´Ï¶ó¸é Á¤»óÀûÀ¸·Î ÆÄ½ÌÇØ¼­ °á°ú°ª µµÃâÇØ³¿ 
				//result °ª¿¡´Â jsp ³»¿ëÀÌ ¸ðµÎ µé¾î°¡ ÀÖ´Ù ±×Áß¿¡¼­ rsultºÎºÐ¸¸ ÃßÃâÇØ³»¾ß ÇÏ´Âµ¥ ... 
				//¿©±â¼­ result³»¿ëÀ» ÂÉ°³´Â ÀÛ¾÷ ½ÇÇà 
				String[] list;
				list = result.split(">");
				idCheck = list[2].substring(2, 7); // Áßº¹ or °¡´É  µÎ°³ÀÇ string °ªÀÌ ³ª¿È 
				System.out.println(idCheck);
				
				if(idCheck.equals("dupli") ){
					
					Toast toast = Toast.makeText(SignUpActivity.this , "¾ÆÀÌµð Áßº¹ÀÔ´Ï´Ù", Toast.LENGTH_SHORT);
					toast.show();
				}
				
				if(idCheck.equals("possi")){
					Toast toast = Toast.makeText(SignUpActivity.this , "»ç¿ë°¡´ÉÇÑ ¾ÆÀÌµðÀÔ´Ï´Ù", Toast.LENGTH_SHORT);
					toast.show();
				}
			}
		}
		
		@Override
		protected void onCancelled() {
			//AsyncTask¸¦ °­Á¦·Î Ãë¼ÒÇßÀ»¶§ È£Ãâ
			super.onCancelled();
			
		}

	}
	////////////////////////////////////////////////////////////////////////////////////////////////////////
	//È¸¿ø°¡ÀÔÀ» À§ÇÑ Åë½Å
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
		
		//¼­¹ö°¡ ²÷°åÀ»°æ¿ì 5ÃÊÀÇ Å¸ÀÓÀ» Ã¼Å©
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
			System.out.println("¾ð¼­Æ÷Æ¼µåÀÎÄÚµù exception");
		}
		return result;
	}
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////
	//¾ÆÀÌµð Áßº¹Ã¼Å©¸¦ À§ÇÑ Åë½Å
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
    	
		//¼­¹ö°¡ ²÷°åÀ»°æ¿ì 5ÃÊÀÇ Å¸ÀÓÀ» Ã¼Å©
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
			System.out.println("¾ð¼­Æ÷Æ¼µåÀÎÄÚµù exception");
		}
		return result;
	}
	
}
