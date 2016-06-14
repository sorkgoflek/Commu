package com.it.insert;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import com.it.member.MemberDao;
import com.it.member.MemberVo;

@Controller
public class InsertController {
	
	private static MemberDao memberDaoImpl;
	
	public InsertController(MemberDao memberDaoImpl){
		this.memberDaoImpl = memberDaoImpl;
	}
	
	@RequestMapping(value = "/member/register.it", method = RequestMethod.GET) //회원가입 버튼 눌렀을때
	public ModelAndView insert(){
		
		ModelAndView mav = new ModelAndView();
		
		//-----------------------------------// fix
		mav.setViewName("join");
		
		//-----------------------------------//
		
		return mav;
	}
	
	@RequestMapping(value = "/member/register.it", method = RequestMethod.POST) //가입신청 버튼 눌렀을때 
	public ModelAndView insertMember(MemberVo vo){
		
		ModelAndView mav = new ModelAndView();
		 //여기서도 아이디 중복인지 확인해주는 작업이 필요하다 
		String dbId = memberDaoImpl.selectIdCheck(vo.getId());
		
		if(dbId == null){ //내가 선택한 아이디가 가능하니까 디비 저장으로 
			memberDaoImpl.insertMember(vo);
			mav.addObject("rst",true);
			mav.setViewName("/member/regJoinConfirm");
		}
		else{
			mav.addObject("rst",false);
			mav.setViewName("/member/regJoinConfirm");
		}
		
		return mav;
	}
}
