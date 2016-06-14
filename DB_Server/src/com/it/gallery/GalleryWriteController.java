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
	public ModelAndView writeRequest(){ // ���������� ��ư ������ �� 
		
		//����鼭 �ٷ� �� ���� 
		ModelAndView mav = new ModelAndView("writeForm"); // writeForm.jsp�� ������ ���� template ��ſ� writeForm�� ���� �־� !! 
		
		return mav;
	}
	
	@RequestMapping(method = RequestMethod.POST) //��� ��ư �������� 
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
		
		File parent = new File(path+"/"+id); //���κ��� ������ ����� 
		if(!parent.exists()){ //���丮�� ������ 
			parent.mkdir(); //���丮 ����� 
		}
		
		if(!file1.isEmpty()){ //file1�� ������� ������ 
			String uuid = UUID.randomUUID().toString();
			File target = new File(parent, uuid);
			file1.transferTo(target); //�ӽ÷� ����� ������ ���� �ش� ���Ϸ� ���۽�Ű�� ���� !! 
			vo.setLink1("/gallery/"+id+"/"+uuid);
		}
		if(!file2.isEmpty()){ //file2�� ������� ������
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
