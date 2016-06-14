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
		//���⼭ ���� �ۼ��� ���̵�� DB�� �ִ� ���̵� �˻��ϴ� �۾��� �ؾߵ� 
	
		ModelAndView mav = new ModelAndView();
		//System.out.println(id); //���� ���� ������ ���̵� ���� �Ѿ� �Ծ�
		
		//��� ���� ���� �̿��ؼ� ���� ������ id�� �ִ��� Ȯ���۾�
		
		String dbId = memberDaoImpl.selectIdCheck(id);
		
		if(dbId == null){
			mav.addObject("rst",true);
			mav.setViewName("/member/regIdConfirm");
			System.out.println("���� ������ ���̵� ��밡��");
		}
		else{
			mav.addObject("rst",false);
			mav.setViewName("/member/regIdConfirm");
			System.out.println("���̵� �ߺ�");
		}
		
		return mav;
	}
}
