package com.it.login;

import org.springframework.stereotype.Controller;

import com.it.member.MemberDao;

@Controller
public class AndroidLoginController {
	
	private static MemberDao memberDaoImpl;
	
	public AndroidLoginController(MemberDao memberDaoImpl){
		this.memberDaoImpl = memberDaoImpl;
	}
	
	
	public static String androidLogin(String id, String pass){
		
			
		String dbPass = memberDaoImpl.selectMember(id);
		
		if(dbPass == null){ // ���̵� ��ü�� ���� 
			System.out.println("(�ȵ���̵�)���̵����");
			return "notid";
		}
		else if(!dbPass.equals(pass)){ //���̵�� ������ ��� ���� 
			
			System.out.println("(�ȵ���̵�)���Ʋ����");
			return "notps";
		}
		else{
			System.out.println("(�ȵ���̵�)�α��� ����");
			return "login";
		}
	}
	

}
