package com.it.gallery;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/gallery/list.it")
public class GalleryListController {

	private GalleryDao galleryDaoImpl; 
	
	public GalleryListController(GalleryDao galleryDaoImpl){
		this.galleryDaoImpl = galleryDaoImpl;
	}
	
	@RequestMapping(method = RequestMethod.GET) // ������ ��ư ������ �� 
	public ModelAndView getList(@RequestParam(required = false, defaultValue = "1") int page){  
		//int�� �Ѿ�ö��� required �� false�� ����� �ȴ� ... �׸��� �⺻���� 1�� �����Ѵ�
		
		
		
		
		int cnt = galleryDaoImpl.selectCount(); // �ѱۼ�
		/*int size = 4; // �� ������ �ۼ� 
		
		int totalPage =  cnt/size;//��Ż ��������
		if(cnt%size !=0){
			totalPage++;
		}*/
		
		// 1�������� ��ŸƮ 1 end 4
		// 2�������� ��ŸƮ 5 end 8
		// 3�������� ��ŸƮ 9 end 12
		
		/*int start = (page-1)*size+1;
		int end = page*size;*/
		
		List<GalleryVo> list = galleryDaoImpl.selectAll();
	
		ModelAndView mav = new ModelAndView();
		mav.setViewName("list"); //template �� ���ڴ� ��,�ڴ� viewresolver ���ؼ� ������
		mav.addObject("cnt",cnt); //�ű� ���� ���� ������ �����͵� 
		//mav.addObject("totalPage",totalPage); //�ű� ���� ���� ������ �����͵� 
		
		mav.addObject("list",list); 
		
		return mav;
		
	}
}
