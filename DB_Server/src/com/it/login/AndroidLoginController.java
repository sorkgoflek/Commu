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
		
		if(dbPass == null){ // 아이디 자체가 없다 
			System.out.println("(안드로이드)아이디없다");
			return "notid";
		}
		else if(!dbPass.equals(pass)){ //아이디는 있으나 비번 오류 
			
			System.out.println("(안드로이드)비번틀리다");
			return "notps";
		}
		else{
			System.out.println("(안드로이드)로그인 성공");
			return "login";
		}
	}
	

}
