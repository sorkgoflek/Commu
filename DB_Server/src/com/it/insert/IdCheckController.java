package com.it.insert;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.it.member.MemberDao;

@Controller
public class IdCheckController {
	
	private static MemberDao memberDaoImpl;
	
	public IdCheckController(MemberDao memberDaoImpl){
		this.memberDaoImpl = memberDaoImpl;
	}
	
	
	@RequestMapping("/member/idcheck.it")
	public ModelAndView idCheck(@RequestParam(value="id") String id){
		//여기서 내가 작성한 아이디와 DB에 있는 아이디를 검사하는 작업을 해야돼 
	
		ModelAndView mav = new ModelAndView();
		//System.out.println(id); //이제 내가 선택한 아이디 값이 넘어 왔어
		
		//디비 쿼리 문을 이용해서 내가 선택한 id가 있는지 확인작업
		
		String dbId = memberDaoImpl.selectIdCheck(id);
		
		if(dbId == null){
			mav.addObject("rst",true);
			mav.setViewName("/member/regIdConfirm");
			System.out.println("내가 선택한 아이디 사용가능");
		}
		else{
			mav.addObject("rst",false);
			mav.setViewName("/member/regIdConfirm");
			System.out.println("아이디 중복");
		}
		
		return mav;
	}
}
