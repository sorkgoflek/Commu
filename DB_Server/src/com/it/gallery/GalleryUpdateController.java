package com.it.gallery;

import java.io.File;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/gallery/update.it")
public class GalleryUpdateController {


	private GalleryDao galleryDaoImpl; 
	int number;
	public GalleryUpdateController(GalleryDao galleryDaoImpl){
		this.galleryDaoImpl = galleryDaoImpl;
	}
	
	@RequestMapping(method = RequestMethod.GET) //글 수정 버튼 눌렀을때 
	public ModelAndView updateClick(@RequestParam(value="num") int num){
		number = num;
		ModelAndView mav = new ModelAndView("updateWriteForm");
		return mav;
	}
	
	
	
	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView update(@RequestParam(value = "file1") MultipartFile file1,
							   @RequestParam(value = "file2") MultipartFile file2,
							   GalleryVo parameterVo,
							   HttpServletRequest request)throws Exception{
		
		HttpSession session = request.getSession();
		String id = (String) session.getAttribute("loginId");
		String path = request.getServletContext().getRealPath("/gallery");

		GalleryVo vo = galleryDaoImpl.selectOne(number); //수정할 거의 vo를 받아와
		
		
		if(!file1.isEmpty()){ //file1이 비어있지 않으면 
			String uuid = UUID.randomUUID().toString();
			File target = new File(path+"/"+id, uuid);
			file1.transferTo(target); //임시로 저장된 파일을 실제 해당 파일로 전송시키는 역할 !! 
			vo.setLink1("/gallery/"+id+"/"+uuid);
		}
		
		if(!file2.isEmpty()){ //file2이 비어있지 않으면
			String uuid = UUID.randomUUID().toString();
			File target = new File(path+"/"+id, uuid);
			file2.transferTo(target);
			vo.setLink2("/gallery/"+id+"/"+uuid);
		} 
		
		vo.setTitle(parameterVo.getTitle());
		vo.setContent(parameterVo.getContent());
		
		galleryDaoImpl.update(vo,number);  //해당 vo를 update메소드로 보내주고 
		
		ModelAndView mav = new ModelAndView();
		mav.setViewName("redirect:/gallery/list.it");
		
		return mav;
	}
}
