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
		
		System.out.println("비번변경 업데이트 하고 난 rst 값:"+rst);
		
		if(rst == 1){ //변경성공
			System.out.println("비번 변경성공");
			return "yespc";
		}
		else{ 
			System.out.println("비번 변경실패");
			return "notpc";
		}
	}

}
