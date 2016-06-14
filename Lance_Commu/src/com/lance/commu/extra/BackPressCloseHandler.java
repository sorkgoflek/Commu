package com.lance.commu.extra;
import android.app.Activity;
import android.widget.Toast;

//하드웨어 Back키 두번 연속 누르면 종료될수 있게 해주는 모듈
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
	                "\'뒤로\'버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT);
	        toast.show();
	    }
}
