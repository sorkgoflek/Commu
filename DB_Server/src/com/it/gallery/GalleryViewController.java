package com.it.gallery;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class GalleryViewController {

	private GalleryDao galleryDaoImpl; 
	
	public GalleryViewController(GalleryDao galleryDaoImpl){
		this.galleryDaoImpl = galleryDaoImpl;
	}
	
	@RequestMapping("/gallery/view.it")
	public ModelAndView view(@RequestParam(value="num") int num){
		
		GalleryVo vo = galleryDaoImpl.selectOne(num);
		
		ModelAndView mav = new ModelAndView();
		mav.setViewName("view"); //view.jsp�� ������ ���� template ��ſ� view.jsp�� ���� �ִ´� 
		mav.addObject("vo",vo);
		return mav;
	}
	
}
