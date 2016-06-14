package com.it.gallery;

import java.io.File;

import org.springframework.stereotype.Controller;

@Controller
public class AndroidGalleryWriteController {
	private static GalleryDao galleryDaoImpl; 
	
	public AndroidGalleryWriteController(GalleryDao galleryDaoImpl){
		this.galleryDaoImpl = galleryDaoImpl;
	}
	
	public static void androidGalleryWrite(String id, String uuid, String title, String content, String dir){
		  
		//여기서 디비에 저장 시켜야돼 !! 
		GalleryVo vo = new GalleryVo();
		
		File parent = new File(dir+"/"+id); //개인별로 폴더를 만들어 
		if(!parent.exists()){ //디렉토리가 없으면 
			parent.mkdir(); //디렉토리 만들어 
		}
		
		File oldFile = new File(dir+"\\"+uuid); 
		File newFile = new File(dir+"\\"+id);
		oldFile.renameTo(new File(newFile,uuid));//파일을 해당 계정폴더로 이동시켜 !!
		
		vo.setWriter(id);
		vo.setTitle(title);
		vo.setContent(content);
		vo.setLink1("/gallery/"+id+"/"+uuid);
		
		galleryDaoImpl.insertOne(vo);
	}
}
