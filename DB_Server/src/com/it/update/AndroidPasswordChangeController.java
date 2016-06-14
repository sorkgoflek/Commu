package com.it.update;

import org.springframework.stereotype.Controller;

import com.it.member.MemberDao;
import com.it.member.MemberVo;

@Controller
public class AndroidPasswordChangeController {
	
	private static MemberDao memberDaoImpl;
	
	public AndroidPasswordChangeController(MemberDao memberDaoImpl){
		this.memberDaoImpl = memberDaoImpl;
	}
	
	
	public static String androidPasswordChange(String id, String pass){
		
		int rst;
		rst = memberDaoImpl.update(id,pass);
		
		System.out.println("������� ������Ʈ �ϰ� �� rst ��:"+rst);
		
		if(rst == 1){ //���漺��
			System.out.println("��� ���漺��");
			return "yespc";
		}
		else{ 
			System.out.println("��� �������");
			return "notpc";
		}
	}

}
