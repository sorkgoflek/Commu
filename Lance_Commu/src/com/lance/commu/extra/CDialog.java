package com.lance.commu.extra;

import com.lance.commu.intro.R;  
import android.app.Dialog;
import android.content.Context;
import android.view.WindowManager.LayoutParams;
import android.widget.ProgressBar;

public class CDialog {
	private static Dialog m_loadingDialog = null;
	
	//�޼ҵ� �ȿ��� �����ؾ� �ϴ� Dialog ��ü�� ProgressBar ��ü�� Context�� ������ �Ķ���ͷ� �䱸
	public static void showLoading(Context context){
		if(m_loadingDialog == null){// ���� �� ��ü�� null�̶�� 
			//���� Dialog�� ȭ��� ǥ�õǰ� ���� �������̶� ����� �ε��� ǥ���ϱ� ���� ��ü�� ��Ʈ�� ����
			m_loadingDialog = new Dialog(context, R.style.TransDialog); //���̾�α�â����
			ProgressBar pb = new ProgressBar(context); //���̾�α� â�� ���� ProgressBar ��Ʈ���� ������
			//��Ʈ�� ũ�� �����ϱ����� LayoutParams ��ü ���� 
			LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
			m_loadingDialog.addContentView(pb, params);
			//���̾�α� â�� �ϵ���� backŰ�� ���� ���� �� �ִ��� ���� ���� 
			//false�� �����߱� ������ ���� �� ����
			m_loadingDialog.setCancelable(false);
		}
		m_loadingDialog.show(); //���̾�α� â�� ȭ�鿡 ǥ��
	}
	
	//Dialog ��ü�� null ���θ� Ȯ���Ͽ�, null�� �ƴҰ�� ���̾�α� â�� �ݱ� ���� dismiss()�޼ҵ带 ȣ���Ѵ�
	//�׷� ���� Dialog ��ü�� null�� �Ҵ��Ͽ� �����ϰ� �ִ� �޸𸮸� �ݳ��Ѵ�. 
	public static void hideLoading(){
		if(m_loadingDialog !=null){
			m_loadingDialog.dismiss();
		}
		m_loadingDialog = null;
	}
}
