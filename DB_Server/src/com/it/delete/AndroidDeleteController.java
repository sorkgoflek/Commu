package com.it.delete;

import org.springframework.stereotype.Controller;

import com.it.member.MemberDao;

@Controller
public class AndroidDeleteController {
	
	private static MemberDao memberDaoImpl;
	
	public AndroidDeleteController(MemberDao memberDaoImpl){
		this.memberDaoImpl = memberDaoImpl;
	}
	
	
	public static int androidDelete(String id){
		
			
		int deleteCheck = memberDaoImpl.deleteMember(id);
		
		return deleteCheck;
	}
	

}
