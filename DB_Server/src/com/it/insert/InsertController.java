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
	
	@RequestMapping(value = "/member/register.it", method = RequestMethod.GET) //ȸ������ ��ư ��������
	public ModelAndView insert(){
		
		ModelAndView mav = new ModelAndView();
		
		//-----------------------------------// fix
		mav.setViewName("join");
		
		//-----------------------------------//
		
		return mav;
	}
	
	@RequestMapping(value = "/member/register.it", method = RequestMethod.POST) //���Խ�û ��ư �������� 
	public ModelAndView insertMember(MemberVo vo){
		
		ModelAndView mav = new ModelAndView();
		 //���⼭�� ���̵� �ߺ����� Ȯ�����ִ� �۾��� �ʿ��ϴ� 
		String dbId = memberDaoImpl.selectIdCheck(vo.getId());
		
		if(dbId == null){ //���� ������ ���̵� �����ϴϱ� ��� �������� 
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
