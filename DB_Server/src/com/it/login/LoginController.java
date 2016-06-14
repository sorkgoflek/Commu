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
	
	@RequestMapping(value = "/member/login.it", method = RequestMethod.POST) //�α��� ��ư �������� �۵� 
	public ModelAndView login(HttpSession session,
			@RequestParam() String id,
			@RequestParam() String pass
			){
		
		ModelAndView mav = new ModelAndView();
		
		String dbPass = memberDaoImpl.selectMember(id);
		
		if(dbPass == null){ // ���̵� ��ü�� ���� 
			mav.addObject("rst",false);
			mav.setViewName("/log/loginProc");
			System.out.println("���̵����");
		}
		else if(!dbPass.equals(pass)){ //���̵�� ������ ��� ���� 
			mav.addObject("rst",false);
			mav.setViewName("/log/loginProc");
			System.out.println("���Ʋ����");
		}
		else{
			mav.addObject("rst",true);
			session.setAttribute("loginId", id);
			mav.setViewName("redirect:/index.it");
		}
		
		return mav;
	}
	
	@RequestMapping(value = "/member/login.it", method = RequestMethod.GET) //�α׾ƿ� ��ư �������� �۾��ϴ� �� 
	public String logout(HttpSession session){
		session.removeAttribute("loginId");
		return "redirect:/index.it";
	}
}
