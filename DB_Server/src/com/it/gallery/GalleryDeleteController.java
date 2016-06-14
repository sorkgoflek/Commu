package com.it.gallery;

import java.io.File;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class GalleryDeleteController {

	private GalleryDao galleryDaoImpl; 
	
	public GalleryDeleteController(GalleryDao galleryDaoImpl){
		this.galleryDaoImpl = galleryDaoImpl;
	}
	
	@RequestMapping("/gallery/delete.it")
	public ModelAndView delete(@RequestParam(value="num") int num, HttpServletRequest request){
		
		HttpSession session = request.getSession();
		String id = (String) session.getAttribute("loginId");
		String path = request.getServletContext().getRealPath("/gallery");
		GalleryVo vo = galleryDaoImpl.selectOne(num);
		
		
			String link1 = vo.getLink1();
			String link2 = vo.getLink2();
			
			if(link1 != null){
				int start1 = link1.lastIndexOf("/");
				String uuid1 = link1.substring(start1+1);
				File file1 = new File(path+"/"+id,uuid1);
			
				if(file1.exists()){
					file1.delete(); //실제파일 삭제
				}
			}
			
			if(link2 != null){
				int start2 = link2.lastIndexOf("/");
				String uuid2 = link2.substring(start2+1);
				File file2 = new File(path+"/"+id,uuid2);
			
				if(file2.exists()){
					file2.delete(); //실제파일 삭제
				}
			}
					
		galleryDaoImpl.delete(num); //디비에서 지워라 
		ModelAndView mav = new ModelAndView();
		mav.setViewName("redirect:/gallery/list.it");
		
		return mav;
	}
}
