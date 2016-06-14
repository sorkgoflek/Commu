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
@RequestMapping("/gallery/write.it")
public class GalleryWriteController {
	private GalleryDao galleryDaoImpl; 
	
	public GalleryWriteController(GalleryDao galleryDaoImpl){
		this.galleryDaoImpl = galleryDaoImpl;
	}
	
	
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView writeRequest(){ // 갤러리쓰기 버튼 눌렀을 때 
		
		//만들면서 바로 뷰 설정 
		ModelAndView mav = new ModelAndView("writeForm"); // writeForm.jsp를 밖으로 빼고 template 대신에 writeForm을 집어 넣어 !! 
		
		return mav;
	}
	
	@RequestMapping(method = RequestMethod.POST) //등록 버튼 눌렀을때 
	public ModelAndView writeProcess(@RequestParam(value = "file1") MultipartFile file1,
									 @RequestParam(value = "file2") MultipartFile file2, 
									 GalleryVo vo,
									 HttpServletRequest request )
	throws Exception{
		
		HttpSession session = request.getSession();
		String id = (String) session.getAttribute("loginId");
		String path = request.getServletContext().getRealPath("/gallery");
		System.out.println(path);
		vo.setWriter(id);
		
		File parent = new File(path+"/"+id); //개인별로 폴더를 만들어 
		if(!parent.exists()){ //디렉토리가 없으면 
			parent.mkdir(); //디렉토리 만들어 
		}
		
		if(!file1.isEmpty()){ //file1이 비어있지 않으면 
			String uuid = UUID.randomUUID().toString();
			File target = new File(parent, uuid);
			file1.transferTo(target); //임시로 저장된 파일을 실제 해당 파일로 전송시키는 역할 !! 
			vo.setLink1("/gallery/"+id+"/"+uuid);
		}
		if(!file2.isEmpty()){ //file2이 비어있지 않으면
			String uuid = UUID.randomUUID().toString();
			File target = new File(parent, uuid);
			file2.transferTo(target);
			vo.setLink2("/gallery/"+id+"/"+uuid);
		}
		
		galleryDaoImpl.insertOne(vo);
		ModelAndView mav = new ModelAndView("redirect:/gallery/list.it");
		return mav;
	}
}
