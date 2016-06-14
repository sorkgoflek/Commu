package com.it.login;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.it.member.MemberDao;
import com.it.member.MemberVo;

@Controller
public class LoginController {
	
	private MemberDao memberDaoImpl;
	
	public LoginController(MemberDao memberDaoImpl){
		this.memberDaoImpl = memberDaoImpl;
	}
	
	@RequestMapping(value = "/member/login.it", method = RequestMethod.POST) //로그인 버튼 눌렀을때 작동 
	public ModelAndView login(HttpSession session,
			@RequestParam() String id,
			@RequestParam() String pass
			){
		
		ModelAndView mav = new ModelAndView();
		
		String dbPass = memberDaoImpl.selectMember(id);
		
		if(dbPass == null){ // 아이디 자체가 없다 
			mav.addObject("rst",false);
			mav.setViewName("/log/loginProc");
			System.out.println("아이디없다");
		}
		else if(!dbPass.equals(pass)){ //아이디는 있으나 비번 오류 
			mav.addObject("rst",false);
			mav.setViewName("/log/loginProc");
			System.out.println("비번틀리다");
		}
		else{
			mav.addObject("rst",true);
			session.setAttribute("loginId", id);
			mav.setViewName("redirect:/index.it");
		}
		
		return mav;
	}
	
	@RequestMapping(value = "/member/login.it", method = RequestMethod.GET) //로그아웃 버튼 눌렀을때 작업하는 곳 
	public String logout(HttpSession session){
		session.removeAttribute("loginId");
		return "redirect:/index.it";
	}
}
