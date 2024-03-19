package com.sch.service;
 
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sch.dao.CommonDao;
import com.sch.util.CamelHashMap;

@Service
public class ExperienceService { 

	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private CommonDao commonDao;

	@Autowired
	CommonService commonService;
 
public List<Map<String, Object>> selectExprnList(Map<String, Object> paramMap) {
	int dataPerPage = Integer.parseInt(paramMap.get("dataPerPage").toString());
	int pageNo = Integer.parseInt(paramMap.get("pageNo").toString());
	int offsetNumber = (pageNo-1) * dataPerPage;
	paramMap.put("offsetNumber", offsetNumber);
	paramMap.put("dataPerPage", dataPerPage);
	return commonService.selectList("Experience.selectExprnListPaging", paramMap);
}
	public void insertexprn(Map<String, Object> paramMap) {
		commonService.insert("Experience.insertexprn", paramMap);

		List<Map<String, Object>> exprnList = (List<Map<String, Object>>) paramMap.get("exprnList");
		Map<String, Object> resultMap = new CamelHashMap();

		exprnList.forEach(x -> {
			resultMap.put("exprnSeq", paramMap.get("exprnSeq"));
			resultMap.put("exprnSn", x.get("exprnSn"));
			resultMap.put("exprnDt", x.get("exprnDt"));
			resultMap.put("exprnBeginTime", x.get("exprnBeginTime"));
			resultMap.put("exprnEndTime", x.get("exprnEndTime"));
			commonService.insert("Experience.insertexprnFx", resultMap);
		});

	}

	public  Map<String, Object>  selectExprn(String sqlId, Map<String, Object> paramMap) {
		return (Map) commonDao.selectOne(sqlId, paramMap);
	}

	public  Map<String, Object> selectExperienceDetail(String sqlId, Map<String, Object> paramMap) {
		return commonDao.selectMap(sqlId, paramMap);
	}

	public int delete(String sqlId, Map<String, Object> paramMap) throws Exception {
		if (sqlId.equals("Experience.deleteExprnOneFx")) {
			return commonDao.delete(sqlId, paramMap);
		} else if (sqlId.equals("Experience.deleteExprnOne")) {
			return commonDao.delete(sqlId, paramMap);
		}
		return 0;
	}
	
	public int deleteExprnOne(Map<String, Object> paramMap) throws Exception {
		if (delete("Experience.deleteExprnOneFx", paramMap) < 0) {
			return -1;
		}
		if (delete("Experience.deleteExprnOne", paramMap) < 0) {
			return -1;
		}
		return 1;
	}


}
