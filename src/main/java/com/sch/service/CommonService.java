package com.sch.service;

import com.sch.dao.CommonDao;
import com.sch.util.CamelHashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Map;

@Service("commonService")
public class CommonService {

	@Autowired
	private  CommonDao commonDao;

	public List<Map<String, Object>> selectList(String sqlId, Map<String, Object> paramMap) {
		return commonDao.selectList(sqlId, paramMap);
	}

	public Map<String, Object> selectMap(String sqlId, Map<String, Object> tmpMap) {
		return commonDao.selectMap(sqlId, tmpMap);
	}


	public Object selectOne(String sqlId, Map<String, Object> paramMap) {
		return commonDao.selectOne(sqlId, paramMap);
	}

	public Map<String, Object> selectPaging(String sqlId, Map<String, Object> paramMap) {
		Map<String, Object> resultMap = new CamelHashMap();

		if (ObjectUtils.isEmpty(paramMap.get("pageNo"))) {
			paramMap.put("pageNo", "1");
		}
		if (ObjectUtils.isEmpty(paramMap.get("pageLength"))) {
			paramMap.put("pageLength", "99999");
		}
		paramMap.put("pageNo", String.valueOf(paramMap.get("pageNo")));
		paramMap.put("pageLength", String.valueOf(paramMap.get("pageLength")));

		Map<String, Object> countMap = commonDao.selectMap(sqlId + "Count", paramMap);

		resultMap.put("totalCount", countMap.get("count"));
		resultMap.put("pageNo", Integer.valueOf((String) paramMap.get("pageNo")));
		resultMap.put("totalPage", Math.ceil(Double.valueOf(countMap.get("count").toString())
				/ Double.valueOf((String) paramMap.get("pageLength"))));

		resultMap.put("list", commonDao.selectList(sqlId, paramMap));

		return resultMap;
	}

	public int insert(String sqlId, Map<String, Object> paramMap) {
		return commonDao.insert(sqlId, paramMap);
	}

	public int update(String sqlId, Map<String, Object> paramMap) {
		return commonDao.update(sqlId, paramMap);
	}

	public int delete(String sqlId, Map<String, Object> paramMap) {
		return commonDao.delete(sqlId, paramMap);
	}

}
