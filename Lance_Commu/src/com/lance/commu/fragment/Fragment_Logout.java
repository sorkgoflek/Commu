package com.lance.commu.fragment;
 
import com.example.slidingsimplesample.R;

import com.lance.commu.login.LoginActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

 
public class Fragment_Logout extends Fragment {
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_logout, container, false);
        
        //�α׾ƿ� �� ���̾�α� ó���ؼ� �ѹ� �� Ȯ����
        AlertDialog.Builder alert_confirm = new AlertDialog.Builder(getActivity());
        alert_confirm.setTitle("�α׾ƿ�"); 
        alert_confirm.setMessage("���� �α׾ƿ� �Ͻðھ��?").setIcon(R.drawable.abs__ic_logout).setCancelable(false).setPositiveButton("�α׾ƿ�",
        new DialogInterface.OnClickListener() {

			@Override
            public void onClick(DialogInterface dialog, int which) {
                // �α׾ƿ�
            	//�ڵ� �α����� ������� �����ؾ��� 
            	SharedPreferences sp = getActivity().getSharedPreferences("PreName", Context.MODE_PRIVATE);
				SharedPreferences.Editor editor = sp.edit();
				editor.clear();
            	editor.commit();
            	
            	
            	startActivity(new Intent(getActivity(),LoginActivity.class));
				getActivity().finish();
            }
        }).setNegativeButton("���",
        new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 'No'
            return;
            }
        });
        AlertDialog alert = alert_confirm.create();
        alert.show();
        
        return v;
    }
}