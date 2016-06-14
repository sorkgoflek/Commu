package com.it.friendlist;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;

import com.it.member.MemberDao;
import com.it.member.MemberVo;

@Controller
public class AndroidFriendListController {
	
	private static MemberDao memberDaoImpl;
	
	public AndroidFriendListController(MemberDao memberDaoImpl){
		this.memberDaoImpl = memberDaoImpl;
	}
	
	
	public static ArrayList<String> androidFriendList(){
		
		List<MemberVo> list = memberDaoImpl.selectAllMember();
		ArrayList<String> phone_number = new ArrayList<String>();
		int index =0;
		for(MemberVo s : list){
			phone_number.add(index, s.getPhone_number());
			index++;
		}
		
		return phone_number;
	}
	

}
