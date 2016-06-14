package com.lance.commu.extra;

import com.lance.commu.intro.R;  
import android.app.Dialog;
import android.content.Context;
import android.view.WindowManager.LayoutParams;
import android.widget.ProgressBar;

public class CDialog {
	private static Dialog m_loadingDialog = null;
	
	//메소드 안에서 생성해야 하는 Dialog 객체와 ProgressBar 객체가 Context를 생성자 파라미터로 요구
	public static void showLoading(Context context){
		if(m_loadingDialog == null){// 만약 이 객체가 null이라면 
			//현재 Dialog가 화면상에 표시되고 있지 않은것이라 여기고 로딩을 표시하기 위한 객체와 컨트롤 생성
			m_loadingDialog = new Dialog(context, R.style.TransDialog); //다이얼로그창생성
			ProgressBar pb = new ProgressBar(context); //다이얼로그 창에 넣을 ProgressBar 컨트롤의 생성자
			//컨트롤 크기 결정하기위해 LayoutParams 객체 생성 
			LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
			m_loadingDialog.addContentView(pb, params);
			//다이얼로그 창을 하드웨어 back키를 통해 닫을 수 있는지 여부 결정 
			//false로 설정했기 때문에 닫을 수 없음
			m_loadingDialog.setCancelable(false);
		}
		m_loadingDialog.show(); //다이얼로그 창을 화면에 표시
	}
	
	//Dialog 객체의 null 여부를 확인하여, null이 아닐경우 다이얼로그 창을 닫기 위해 dismiss()메소드를 호출한다
	//그런 다음 Dialog 객체에 null를 할당하여 점유하고 있는 메모리를 반납한다. 
	public static void hideLoading(){
		if(m_loadingDialog !=null){
			m_loadingDialog.dismiss();
		}
		m_loadingDialog = null;
	}
}
