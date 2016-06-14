package com.it.member;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;

public class MemberDaoImpl implements MemberDao{
	private SqlSessionTemplate session;
	
	
	public MemberDaoImpl(SqlSessionTemplate session){
		this.session = session;
	}
	
	
	@Override
	public int insertMember(MemberVo vo) {
		int rst = 0;
		rst = session.insert("com.it.member.insertMember",vo);
		return rst;
	}
	
	@Override
	public String selectMember(String id){
		
		String rst = session.selectOne("com.it.member.selectDBPass", id);
		
		return rst;
		
	}


	@Override
	public String selectIdCheck(String id) {
		
		String rst = session.selectOne("com.it.member.selectIdCheck", id);
		
		return rst;
	}

	@Override
	public String selectPhoneNumberCheck(String phone_number) {
		
		String rst = session.selectOne("com.it.member.selectPhoneNumberCheck", phone_number); 
		
		return rst;
	}
	
	@Override
	public List<MemberVo> selectAllMember() {
		
		List<MemberVo> list = session.selectList("com.it.member.selectAllMember");
		return list;
	}


	@Override
	public int deleteMember(String id) {
		int rst = 0;
		rst = session.insert("com.it.member.deleteMember",id);
		return rst;
	}


	@Override
	public int update(String id, String pass) {
		int rst =0;

		
		Map<String, String> map = new HashMap<String, String>();
		map.put("id", id);
		map.put("pass", pass);
		
		System.out.println("update 함수 쪽 map저장후 id "+id);
		System.out.println("update 함수 쪽 map저장후 pass "+pass);
		
		rst = session.update("com.it.member.passwordChange", map);
		
		System.out.println("update 함수 쪽 rst:"+rst);
		return rst;
	}


	

	
}
