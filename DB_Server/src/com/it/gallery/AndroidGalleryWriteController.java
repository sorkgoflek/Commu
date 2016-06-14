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
		  
		//���⼭ ��� ���� ���Ѿߵ� !! 
		GalleryVo vo = new GalleryVo();
		
		File parent = new File(dir+"/"+id); //���κ��� ������ ����� 
		if(!parent.exists()){ //���丮�� ������ 
			parent.mkdir(); //���丮 ����� 
		}
		
		File oldFile = new File(dir+"\\"+uuid); 
		File newFile = new File(dir+"\\"+id);
		oldFile.renameTo(new File(newFile,uuid));//������ �ش� ���������� �̵����� !!
		
		vo.setWriter(id);
		vo.setTitle(title);
		vo.setContent(content);
		vo.setLink1("/gallery/"+id+"/"+uuid);
		
		galleryDaoImpl.insertOne(vo);
	}
}
