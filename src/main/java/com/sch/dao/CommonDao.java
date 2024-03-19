package com.sch.dao;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Repository
public class CommonDao {

	@Autowired
	private SqlSession sqlSession;

	public List<Map<String, Object>> selectList(String sqlId, Map<String, Object> paramMap) {
		return sqlSession.selectList(sqlId, paramMap);
	}

	public Map<String, Object> selectMap(String sqlId, Map<String, Object> paramMap) {
		List<Map<String, Object>> resultMap = sqlSession.selectList(sqlId, paramMap);
		if (resultMap == null || resultMap.size() < 1) {
			return new LinkedHashMap<String, Object>();
		}
		return resultMap.get(0);
	}

	public Object selectOne(String sqlId, Map<String, Object> paramMap) {
		return sqlSession.selectOne(sqlId, paramMap);
	}

	public int insert(String sqlId, Map<String, Object> paramMap) {
		return sqlSession.insert(sqlId, paramMap);
	}

	public int update(String sqlId, Map<String, Object> paramMap) {
		return sqlSession.update(sqlId, paramMap);
	}

	public int delete(String sqlId, Map<String, Object> paramMap) {
		return sqlSession.delete(sqlId, paramMap);
	}

}
