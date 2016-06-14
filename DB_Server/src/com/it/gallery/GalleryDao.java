package com.it.gallery;

import java.util.List;

public interface GalleryDao {
	public abstract int insertOne(GalleryVo vo);
	public abstract List<GalleryVo> selectAll();
	public abstract GalleryVo selectOne(int num); 
	public abstract int selectCount();
	public abstract void delete(int num);
	public abstract List<GalleryVo> selectAll(int start, int end);
	public abstract void update(GalleryVo vo, int num);
}
   