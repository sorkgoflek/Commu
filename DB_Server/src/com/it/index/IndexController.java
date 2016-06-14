package com.it.index;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class IndexController {

	@RequestMapping("/index.it")
	public ModelAndView intro(){
		
		ModelAndView mav = new ModelAndView();
		
		//-----------------------------------// fix
		mav.setViewName("main");
		
		//-----------------------------------//
		  
		return mav;
	} 
	
}
