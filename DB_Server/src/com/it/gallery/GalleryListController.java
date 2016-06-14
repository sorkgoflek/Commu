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
	
	@RequestMapping(method = RequestMethod.GET) // 갤러리 버튼 눌렀을 때 
	public ModelAndView getList(@RequestParam(required = false, defaultValue = "1") int page){  
		//int로 넘어올때는 required 꼭 false로 해줘야 된다 ... 그리고 기본값을 1로 설정한다
		
		
		
		
		int cnt = galleryDaoImpl.selectCount(); // 총글수
		/*int size = 4; // 한 페이지 글수 
		
		int totalPage =  cnt/size;//토탈 페이지수
		if(cnt%size !=0){
			totalPage++;
		}*/
		
		// 1페이지가 스타트 1 end 4
		// 2페이지가 스타트 5 end 8
		// 3페이지가 스타트 9 end 12
		
		/*int start = (page-1)*size+1;
		int end = page*size;*/
		
		List<GalleryVo> list = galleryDaoImpl.selectAll();
	
		ModelAndView mav = new ModelAndView();
		mav.setViewName("list"); //template 로 가겠다 앞,뒤는 viewresolver 통해서 맞춰짐
		mav.addObject("cnt",cnt); //거기 갈때 같이 가져갈 데이터들 
		//mav.addObject("totalPage",totalPage); //거기 갈때 같이 가져갈 데이터들 
		
		mav.addObject("list",list); 
		
		return mav;
		
	}
}
