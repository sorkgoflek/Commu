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
						
						//������ ��ȭ��ȣ�� ���� ���� ģ������ Ȯ�� �Ŀ� ��Ƽ ����
						String incomingNumber = intent.getStringExtra("incomingNumber");
						String outgoingNumber = intent.getStringExtra("outgoingNumber");
						
						if(incomingNumber != null){//�����ڶ��
							Log.i("NetworkWithVT", "�� ������");
							
							if(incomingNumber.charAt(0)=='+'){
								incomingNumber='0' + incomingNumber.substring(3);
							}
							
							Log.i("NetworkWithVT", "incomingNumber: "+ incomingNumber);
							
							//��밡 ģ������ Ȯ�� �� noti ����
							if(isFriend(incomingNumber)){
								Log.i("NetworkWithVT", incomingNumber + "�� ģ��");
								
								NetworkManager.partnerPhoneNumber=incomingNumber;
							}
							else{
								Log.i("NetworkWithVT", incomingNumber + "�� ģ�� �ƴ�");
								
								step=NOTHING_STEP;
								ex_step=step;
								
								return;
							}
						}
						else if(outgoingNumber != null){//�߽��ڶ��
					        Log.i("NetworkWithVT", "�� �߽���");
					        
					        if(outgoingNumber.charAt(0)=='+'){
								outgoingNumber='0' + outgoingNumber.substring(3);
							}
					        
					        Log.i("NetworkWithVT", "outgoingNumber: "+ outgoingNumber);
					        
					        //��밡 ģ������ Ȯ�� �� noti ����
							if(isFriend(outgoingNumber)){
								Log.i("NetworkWithVT", outgoingNumber + "�� ģ��");
								
								NetworkManager.partnerPhoneNumber=outgoingNumber;
							}
							else{
								Log.i("NetworkWithVT", outgoingNumber + "�� ģ�� �ƴ�");
								
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
						
						while(true){//���� ��ȭ���̸� ����ڰ� noti�� Ŭ���ϴ��� ��� ����. ���񽺷� ��ü����?
							if(step != PERMIT_CHECK_STEP){//��ȭ�� ����Ǹ�
								Log.i("NetworkWithVT", "step != PERMIT_CHECK_STEP");
								break;
							}
							
							if(PermitActivity.permit==true){//����ڰ� VT�� �����ϰ� �;��ϸ�

								Log.i("NetworkWithVT", "PermitActivity.permit==true");
								
								/*
								toast=Toast.makeText(CommuService.this, "null", Toast.LENGTH_SHORT);
								toast.setText("������ Commu ������ ���� ������ ����մϴ�.");
								toastHandler.sendEmptyMessage(0);
								*/
								
								
								Message msg=new Message();
								msg.obj="������ Commu ������ ���� ������ ����մϴ�.";
								toastHandler.sendMessage(msg);
								
								//��Ʈ�ʿ� ����
								nm = NetworkManager.getInstance();
								nm.permit=true;
								
								int result=nm.connectWithPartner();
								if(result==NetworkManager.CONNECT_SUCCESS){
									
						            //��Ʈ�ʿ� ��� ����
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
			            				pie.send();//�� ��
			            			} catch (CanceledException e) {
			            				Log.i("NetworkWithVT", "CanceledException");
			            			}
			            			
			            			/*
				            		if(result==NetworkManager.FAIL_TO_CONNECT_WITH_RELAYSERVER){
				            			Intent popupIntent = new Intent(getApplicationContext(), AlertDialogActivity.class);
				            			PendingIntent pie= PendingIntent.getActivity(getApplicationContext(), 0, popupIntent, PendingIntent.FLAG_ONE_SHOT);
				            			
				            			//toast.setText("�������� ������ �����߽��ϴ�.");
										//toastHandler.sendEmptyMessage(0);
				            			Message msg1=new Message();
				            			msg1.obj="�������� ������ �����߽��ϴ�.";
				            			toastHandler.sendMessage(msg1);
				            		}
				            		else if(result==NetworkManager.FAIL_TO_ACCEPT_WITH_TIMEOUT){
				            			//toast.setText("������� ���� �ð� �ʰ��� ������ �����Ͽ����ϴ�.");
										//toastHandler.sendEmptyMessage(0);
										
				            			msg.obj="������� ���� �ð� �ʰ��� ������ �����Ͽ����ϴ�.";
				            			toastHandler.sendMessage(msg);
				            		}
				            		else if(result==NetworkManager.FAIL_TO_CONNECT_WITH_SERVER_USER){
				            			//toast.setText("������� ���� �ð� �ʰ��� ������ �����Ͽ����ϴ�.");
										//toastHandler.sendEmptyMessage(0);
										
				            			msg.obj="������� ���� �ð� �ʰ��� ������ �����Ͽ����ϴ�.";
				            			toastHandler.sendMessage(msg);
				            		}
				            		else if(result==NetworkManager.FAIL_WITH_CANCEL_MODE){
				            			if(nm.permit==true){
				            				//toast.setText("������ Commu ������ ������ �ʽ��ϴ�.");
											//toastHandler.sendEmptyMessage(0);
											
				            				msg.obj="������ Commu ������ ������ �ʽ��ϴ�.";
				            				toastHandler.sendMessage(msg);
				            			}
				            		}
				            		*/
				            		Log.i("NetworkWithVT", "CommuService ��Ʈ�ʿ� ���� ����  -code: "+result);
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
					
					case EXIT_STEP: //����ڰ� VT ������ ������ �ʰ� ��ȭ�� ����Ǿ��� ��
						Log.i("NetworkWithVT", "EXIT_STEP");
						
						//��Ƽ ����
						NotificationManager notiManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
						notiManager.cancel(NotiID);
						
						String outgoingNumber2 = intent.getStringExtra("outgoingNumber");
						if(outgoingNumber2 != null && nm != null){//�߽��ڸ鼭 ��ȭ������ �ȵǾ��µ� VT������ �õ��ϴ� ��ȭ�� ����Ǹ�
							//������ ��������
							nm.closeAll();
						}
						else{//��ȭ�� ���� ������ VT������ ������ �ʾҴٸ� 
							//���濡�� VT���� �ǻ������ ����
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
	static final int NotiID = 5; //�˸��� ID
	
	Handler notiHandler = new Handler(new Handler.Callback(){
		@SuppressLint("NewApi")
		public boolean handleMessage(Message msg) {
	
			//����ڰ� �˶��� Ȯ���ϰ� Ŭ�������� ���ο� ��Ƽ��Ƽ�� ������ ����Ʈ ��ü
			Intent intent = new Intent(CommuService.this, PermitActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            
			//����Ʈ ��ü�� �����ؼ� ������ ����Ʈ ������ ��ü
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
				.setContentText("����� Commu�� �����Ϸ��� Ŭ�����ּ���.")
				.setSmallIcon(R.drawable.phonebook_lo)
				.setTicker("Commu �˸�")
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
		
  		//�Ķ���ͷ� �� ��ȣ�� sqlite��ȣ�� ��ġ�ϴ��� ��ȸ 
  		for(int i=0; i<IntroActivity.sqlite_Phone_Number.size();i++){
  	  		
  			if(phone_number.equals(IntroActivity.sqlite_Phone_Number.get(i))){
  				return true;
  			}
  		}
  		
  		return false;
  	}
}
 
