package com.it.insert;

import java.io.File;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;

import com.it.member.MemberDao;
import com.it.member.MemberVo;

@Controller
public class AndroidInsertController {
	
	private static MemberDao memberDaoImpl;
	
	public AndroidInsertController(MemberDao memberDaoImpl){
		this.memberDaoImpl = memberDaoImpl;
	}
	
	public static String androidInsertData(String id, String pass, String name, String phone_number, String gender, String dir){
		
		MemberVo vo = new MemberVo();
		vo.setId(id);
		vo.setPass(pass);
		vo.setName(name);
		vo.setPhone_number(phone_number);
		vo.setGender(gender);
		
		String dbId = memberDaoImpl.selectIdCheck(vo.getId()); //아이디 중복 체크
		//System.out.println("아이디 중복체크:"+dbId);
		
		String dbPhoneNumber = memberDaoImpl.selectPhoneNumberCheck(vo.getPhone_number()); //핸드폰 번호 중복 체크
		//System.out.println("폰넘버 체크:"+dbPhoneNumber);
		
		if(dbId == null){ //내가 선택한 아이디가 가능하니까 디비 저장으로
			if(dbPhoneNumber == null){// 핸펀번호가 중복이 아니라는 뜻 = 처음 계정생성한다는 뜻
				memberDaoImpl.insertMember(vo);//디비에 삽입하고 
			
				//여기서 개인별 폴더를 생성해줘야함 
				File parent = new File(dir+"/"+id); //개인별로 폴더를 만들어 
				if(!parent.exists()){ //디렉토리가 없으면 
					parent.mkdir(); //디렉토리 만들어 
				}	
				System.out.println("(안드로이드) 아이디 사용가능");
				return "possi";
			}
			else{//아이디는 선택가능한데 번호가 중복이면 
				System.out.println("(안드로이드) 아이디는 사용가능하지만 번호중복");
				return "duphn";
			}
		}
		else{
			System.out.println("(안드로이드) 아이디 중복");
			return "dupli";
		}
	}
	
	public static String androidIdCheck(String id){

		String dbId = memberDaoImpl.selectIdCheck(id);
		
		if(dbId == null){ 
			System.out.println("(안드로이드) 아이디 사용가능");
			return "possicheck";
		}
		else{
			System.out.println("(안드로이드) 아이디 중복");
			return "duplicheck";
		}
	}
	
}
