package com.it.gallery;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;

	public class GalleryDaoImpl implements GalleryDao {
	
	private SqlSessionTemplate session;
	
	public GalleryDaoImpl(SqlSessionTemplate session){
		this.session = session;
	}
	
	@Override
	public int insertOne(GalleryVo vo) {
	
		int rst = 0;
		rst = session.insert("com.it.gallery.insertGallery",vo);
		return rst;
	}

	@Override
	public List<GalleryVo> selectAll() {
		
		List<GalleryVo> list = session.selectList("com.it.gallery.selectAllGallery");
		
		return list;
	}

	@Override
	public GalleryVo selectOne(int num) {
		
		GalleryVo vo = session.selectOne("com.it.gallery.selectOneGallery",num);
		return vo;
	}

	@Override
	public int selectCount() {
		
		Integer cnt = session.selectOne("com.it.gallery.selectCountGallery");
		return cnt;
		
	}

	@Override
	public void delete(int num) {
		
		session.delete("com.it.gallery.deleteOneGallery",num);
		
	}

	@Override
	public List<GalleryVo> selectAll(int start, int end) {
		Map<String,Object> map = new HashMap<String, Object>();
		
		map.put("start", start);
		map.put("end", end);
		
		List<GalleryVo> list = session.selectList("com.it.gallery.selectByPage",map);
		
		return list;
	}

	@Override
	public void update(GalleryVo vo, int num) {
		Map<String,Object> map = new HashMap<String, Object>();
		
		map.put("title", vo.getTitle());
		map.put("content", vo.getContent());
		map.put("num",num);
		map.put("link1", vo.getLink1());
		map.put("link2", vo.getLink2());
		session.update("com.it.gallery.updateGallery", map);
	}
	
	
}
