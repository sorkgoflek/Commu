package com.lance.commu.extra;
import android.app.Activity;
import android.widget.Toast;

//�ϵ���� BackŰ �ι� ���� ������ ����ɼ� �ְ� ���ִ� ���
public class BackPressCloseHandler {
	 private long backKeyPressedTime = 0;
	    private Toast toast;
	 
	    private Activity activity;
	 
	    public BackPressCloseHandler(Activity context) {
	        this.activity = context;
	    }
	 
	    public void onBackPressed() {
	        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
	            backKeyPressedTime = System.currentTimeMillis();
	            showGuide();
	            return;
	        }
	        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
	            //activity.finish();
	        	android.os.Process.killProcess(android.os.Process.myPid());
	            toast.cancel();
	        }
	    }
	 
	    public void showGuide() {
	        toast = Toast.makeText(activity,
	                "\'�ڷ�\'��ư�� �ѹ� �� �����ø� ����˴ϴ�.", Toast.LENGTH_SHORT);
	        toast.show();
	    }
}
