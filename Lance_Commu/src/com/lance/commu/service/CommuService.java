package com.lance.commu.service;

import com.lance.commu.intro.IntroActivity;
import com.lance.commu.intro.R;
import com.lance.commu.network.NetworkManager;
import com.lance.commu.visualtalk.VTActivity;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

 
public class CommuService extends IntentService {
	static final int NOTHING_STEP=0;
	static final int DB_CHECK_STEP=1;
	static final int PERMIT_CHECK_STEP=2;
	static final int EXIT_STEP=3;
	
	static int step=0;
	int ex_step=0;
	
	NetworkManager nm;
	
	Toast toast;//=Toast.makeText(CommuService.this, "null", Toast.LENGTH_SHORT);
	
	public CommuService() {
		super("CommuService");
		//toast=Toast.makeText(CommuService.this, "null", Toast.LENGTH_SHORT);
		
        Log.i("NetworkWithVT", "ActivityIntentService start");
    }
 
	@Override
	protected void onHandleIntent(Intent intent) {
		Log.i("NetworkWithVT", "onHandleIntent()");

		while(true){
			if(ex_step < step){
				ex_step=step;
				
				switch(step){
					case DB_CHECK_STEP:
						Log.i("NetworkWithVT", "DB_CHECK_STEP");
						
						//전해진 전화번호로 상대와 내가 친구인지 확인 후에 노티 띄우기
						String incomingNumber = intent.getStringExtra("incomingNumber");
						String outgoingNumber = intent.getStringExtra("outgoingNumber");
						
						if(incomingNumber != null){//수신자라면
							Log.i("NetworkWithVT", "난 수신자");
							
							if(incomingNumber.charAt(0)=='+'){
								incomingNumber='0' + incomingNumber.substring(3);
							}
							
							Log.i("NetworkWithVT", "incomingNumber: "+ incomingNumber);
							
							//상대가 친구인지 확인 후 noti 띄우기
							if(isFriend(incomingNumber)){
								Log.i("NetworkWithVT", incomingNumber + "는 친구");
								
								NetworkManager.partnerPhoneNumber=incomingNumber;
							}
							else{
								Log.i("NetworkWithVT", incomingNumber + "는 친구 아님");
								
								step=NOTHING_STEP;
								ex_step=step;
								
								return;
							}
						}
						else if(outgoingNumber != null){//발신자라면
					        Log.i("NetworkWithVT", "난 발신자");
					        
					        if(outgoingNumber.charAt(0)=='+'){
								outgoingNumber='0' + outgoingNumber.substring(3);
							}
					        
					        Log.i("NetworkWithVT", "outgoingNumber: "+ outgoingNumber);
					        
					        //상대가 친구인지 확인 후 noti 띄우기
							if(isFriend(outgoingNumber)){
								Log.i("NetworkWithVT", outgoingNumber + "는 친구");
								
								NetworkManager.partnerPhoneNumber=outgoingNumber;
							}
							else{
								Log.i("NetworkWithVT", outgoingNumber + "는 친구 아님");
								
								step=NOTHING_STEP;
								ex_step=step;
								
								return;
							}
						}
						else{
					        Log.e("NetworkWithVT", "incomingNumber == null && outgoingNumber == null");
					        
					        step=NOTHING_STEP;
							ex_step=step;
							
							return;
						}
						
						Context context1=getBaseContext();
						TelephonyManager tm = (TelephonyManager) context1.getSystemService(Context.TELEPHONY_SERVICE);
						NetworkManager.myPhoneNumber = tm.getLine1Number();
						
						step=PERMIT_CHECK_STEP;
						PermitActivity.permit=false;
						try {
							notiHandler.sendEmptyMessage(0);
						}catch (Exception e) {
							e.printStackTrace();
						}
						
						break;
				
					case PERMIT_CHECK_STEP:
						Log.i("NetworkWithVT", "PERMIT_CHECK_STEP");
						
						while(true){//현재 통화중이며 사용자가 noti를 클릭하는지 계속 감시. 서비스로 대체가능?
							if(step != PERMIT_CHECK_STEP){//통화가 종료되면
								Log.i("NetworkWithVT", "step != PERMIT_CHECK_STEP");
								break;
							}
							
							if(PermitActivity.permit==true){//사용자가 VT를 실행하고 싶어하면

								Log.i("NetworkWithVT", "PermitActivity.permit==true");
								
								/*
								toast=Toast.makeText(CommuService.this, "null", Toast.LENGTH_SHORT);
								toast.setText("상대방이 Commu 실행을 원할 때까지 대기합니다.");
								toastHandler.sendEmptyMessage(0);
								*/
								
								
								Message msg=new Message();
								msg.obj="상대방이 Commu 실행을 원할 때까지 대기합니다.";
								toastHandler.sendMessage(msg);
								
								//파트너와 연결
								nm = NetworkManager.getInstance();
								nm.permit=true;
								
								int result=nm.connectWithPartner();
								if(result==NetworkManager.CONNECT_SUCCESS){
									
						            //파트너와 통신 시작
						            nm.start();
						            
						            //vt
						            Context context=getBaseContext();
						            Intent ActivityIntent = new Intent(context, VTActivity.class);
						            ActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						            ActivityIntent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
						            context.startActivity(ActivityIntent);
								}
				            	else{
				            		Intent popupIntent = new Intent(getApplicationContext(), AlertDialogActivity.class);
			            			PendingIntent pie= PendingIntent.getActivity(getApplicationContext(), 0, popupIntent, PendingIntent.FLAG_ONE_SHOT);
			            			
			            			try {
			            				pie.send();//안 떠
			            			} catch (CanceledException e) {
			            				Log.i("NetworkWithVT", "CanceledException");
			            			}
			            			
			            			/*
				            		if(result==NetworkManager.FAIL_TO_CONNECT_WITH_RELAYSERVER){
				            			Intent popupIntent = new Intent(getApplicationContext(), AlertDialogActivity.class);
				            			PendingIntent pie= PendingIntent.getActivity(getApplicationContext(), 0, popupIntent, PendingIntent.FLAG_ONE_SHOT);
				            			
				            			//toast.setText("서버와의 연결이 실패했습니다.");
										//toastHandler.sendEmptyMessage(0);
				            			Message msg1=new Message();
				            			msg1.obj="서버와의 연결이 실패했습니다.";
				            			toastHandler.sendMessage(msg1);
				            		}
				            		else if(result==NetworkManager.FAIL_TO_ACCEPT_WITH_TIMEOUT){
				            			//toast.setText("상대방과의 연결 시간 초과로 연결이 실패하였습니다.");
										//toastHandler.sendEmptyMessage(0);
										
				            			msg.obj="상대방과의 연결 시간 초과로 연결이 실패하였습니다.";
				            			toastHandler.sendMessage(msg);
				            		}
				            		else if(result==NetworkManager.FAIL_TO_CONNECT_WITH_SERVER_USER){
				            			//toast.setText("상대방과의 연결 시간 초과로 연결이 실패하였습니다.");
										//toastHandler.sendEmptyMessage(0);
										
				            			msg.obj="상대방과의 연결 시간 초과로 연결이 실패하였습니다.";
				            			toastHandler.sendMessage(msg);
				            		}
				            		else if(result==NetworkManager.FAIL_WITH_CANCEL_MODE){
				            			if(nm.permit==true){
				            				//toast.setText("상대방이 Commu 실행을 원하지 않습니다.");
											//toastHandler.sendEmptyMessage(0);
											
				            				msg.obj="상대방이 Commu 실행을 원하지 않습니다.";
				            				toastHandler.sendMessage(msg);
				            			}
				            		}
				            		*/
				            		Log.i("NetworkWithVT", "CommuService 파트너와 연결 실패  -code: "+result);
				            	}
								
				            	PermitActivity.permit=false;
				            	
				            	return;
				            }
				            else{
				            	Log.i("NetworkWithVT", "PermitActivity.permit==false");
				            	try {
									Thread.sleep(500);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
				            }
						}
						
						break;
					
					case EXIT_STEP: //사용자가 VT 실행을 원하지 않고 통화가 종료되었을 때
						Log.i("NetworkWithVT", "EXIT_STEP");
						
						//노티 삭제
						NotificationManager notiManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
						notiManager.cancel(NotiID);
						
						String outgoingNumber2 = intent.getStringExtra("outgoingNumber");
						if(outgoingNumber2 != null && nm != null){//발신자면서 통화연결이 안되었는데 VT연결을 시도하다 전화가 종료되면
							//서버와 연결종료
							nm.closeAll();
						}
						else{//통화가 끝날 때까지 VT연결을 원하지 않았다면 
							//상대방에게 VT연결 의사없음을 전달
							nm = NetworkManager.getInstance();
							nm.permit=false;
							nm.connectWithPartner();
						}
						
						step=NOTHING_STEP;
						ex_step=step;
						
						return;
					
					default:
						break;
				}//switch(step)
			}//if(ex_step < step)
			else{
				try {
					Thread.sleep(300);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}//while(true)
	}

	NotificationManager notiManager;
	static Notification noti;
	static final int NotiID = 5; //알림의 ID
	
	Handler notiHandler = new Handler(new Handler.Callback(){
		@SuppressLint("NewApi")
		public boolean handleMessage(Message msg) {
	
			//사용자가 알람을 확인하고 클릭했을때 새로운 액티비티를 시작할 인텐트 객체
			Intent intent = new Intent(CommuService.this, PermitActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            
			//인텐트 객체를 포장해서 전달할 인텐트 전달자 객체
			PendingIntent pendingI ;
			if(android.os.Build.VERSION.SDK_INT < 16){
				pendingI = PendingIntent.getActivity(CommuService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);
			}
			else{
				pendingI = PendingIntent.getActivity(CommuService.this,0,intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT, null);
			}
			
			notiManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
				      
			noti = new NotificationCompat.Builder(getApplicationContext())
				.setContentTitle("Commu")
				.setContentText("상대방과 Commu를 실행하려면 클릭해주세요.")
				.setSmallIcon(R.drawable.phonebook_lo)
				.setTicker("Commu 알림")
				.setAutoCancel(true)
				.setContentIntent(pendingI)
				.build();
			
			notiManager.notify(NotiID, noti);
			
			return true;
		}
	});

	Handler toastHandler = new Handler()  {
    	public void handleMessage(Message msg)  {
    		if(msg!=null){
	    		if(toast==null){
	    			toast=Toast.makeText(CommuService.this, msg.obj.toString(), Toast.LENGTH_SHORT);
	    			toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
	    		}
	    		else{
	    			toast.setText(msg.obj.toString());
	    		}
	    		
	    		toast.show();
    		}
    		
    		/*if(msg!=null){
    			Toast t=Toast.makeText(CommuService.this, msg.obj.toString(), Toast.LENGTH_SHORT);
				t.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
				t.show();
    		}*/
    	}
    };
	
	boolean isFriend(String phone_number){
		Log.i("NetworkWithVT", "IntroActivity.sqlite_Phone_Number.size(): " + IntroActivity.sqlite_Phone_Number.size());
		
  		//파라미터로 온 번호와 sqlite번호가 일치하는지 조회 
  		for(int i=0; i<IntroActivity.sqlite_Phone_Number.size();i++){
  	  		
  			if(phone_number.equals(IntroActivity.sqlite_Phone_Number.get(i))){
  				return true;
  			}
  		}
  		
  		return false;
  	}
}
 
