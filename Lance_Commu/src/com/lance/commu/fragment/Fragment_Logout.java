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
        
        //로그아웃 시 다이얼로그 처리해서 한번 더 확인함
        AlertDialog.Builder alert_confirm = new AlertDialog.Builder(getActivity());
        alert_confirm.setTitle("로그아웃"); 
        alert_confirm.setMessage("지금 로그아웃 하시겠어요?").setIcon(R.drawable.abs__ic_logout).setCancelable(false).setPositiveButton("로그아웃",
        new DialogInterface.OnClickListener() {

			@Override
            public void onClick(DialogInterface dialog, int which) {
                // 로그아웃
            	//자동 로그인을 했을경우 해제해야함 
            	SharedPreferences sp = getActivity().getSharedPreferences("PreName", Context.MODE_PRIVATE);
				SharedPreferences.Editor editor = sp.edit();
				editor.clear();
            	editor.commit();
            	
            	
            	startActivity(new Intent(getActivity(),LoginActivity.class));
				getActivity().finish();
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
        
        return v;
    }
}