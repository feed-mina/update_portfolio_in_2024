package com.sch.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sch.util.CamelHashMap;

@Service
public class UserManageService {

	@Autowired
	CommonService commonService;


	public Map<String, Object> selectUserOneLctreList(Map<String, Object> paramMap) throws Exception {

		List<Map<String, Object>> lctreList = commonService.selectList("userManage.selectUserOneLctreList", paramMap);
		Map<String, Object> resultMap = commonService.selectMap("userManage.selectUserOneDetail", paramMap);
		resultMap.put("lctreList", lctreList);
		return resultMap;
	}

	public Map<String, Object> selectUserOneSeminaList(Map<String, Object> paramMap) throws Exception {

		List<Map<String, Object>> seminaList = commonService.selectList("userManage.selectUserOneSeminaList", paramMap);
		Map<String, Object> resultMap = commonService.selectMap("userManage.selectUserOneDetail", paramMap);
		resultMap.put("seminaList", seminaList);
		return resultMap;
	}

	public Map<String, Object> selectUserOneAnFLctreList(Map<String, Object> paramMap) throws Exception {

		List<Map<String, Object>> attendList = commonService.selectList("userManage.selectUserOneAttendLctreList",
				paramMap);
		List<Map<String, Object>> finishedList = commonService.selectList("userManage.selectUserOneFinishedLctreList",
				paramMap);
		Map<String, Object> resultMap = commonService.selectMap("userManage.selectUserOneDetail", paramMap);
		resultMap.put("attendList", attendList);
		resultMap.put("finishedList", finishedList);
		return resultMap;
	}

	public Map<String, Object> selectUserOneAnFSeminaList(Map<String, Object> paramMap) throws Exception {

		List<Map<String, Object>> attendList = commonService.selectList("userManage.selectUserOneAttendSeminaList",
				paramMap);
		List<Map<String, Object>> finishedList = commonService.selectList("userManage.selectUserOneFinishedSeminaList",
				paramMap);
		Map<String, Object> resultMap = commonService.selectMap("userManage.selectUserOneDetail", paramMap);
		resultMap.put("attendList", attendList);
		resultMap.put("finishedList", finishedList);
		return resultMap;
	}

	public Map<String, Object> selectLctreOneDetail(Map<String, Object> paramMap) throws Exception {

		Map<String, Object> lctreOneDetail = commonService.selectMap("userManage.selectLctreOneDetail", paramMap);
		List<Map<String, Object>> lctreFxList = commonService.selectList("userManage.selectLctreOneFxList", paramMap);
		Map<String, Object> resultMap = new CamelHashMap();
		resultMap.put("lctreOneDetail", lctreOneDetail);
		resultMap.put("lctreFxList", lctreFxList);
		return resultMap;
	}

	public void upsertUserGrade(Map<String, Object> paramMap) throws Exception {

		List<Map<String, Object>> gradeList = (List<Map<String, Object>>) paramMap.get("gradeList");
		Map<String, Object> resultMap = new CamelHashMap();

		gradeList.forEach(x -> {
			resultMap.put("gradYm", x.get("gradYm"));
			resultMap.put("userSeq", x.get("userSeq"));
			resultMap.put("gradCode", x.get("gradCode"));
			resultMap.put("user", paramMap.get("user"));
			commonService.insert("userManage.upsertUserGrade", resultMap);
		});
		return;
	}

	public void exportExcelUserGradeList(Map<String, Object> paramMap, HttpServletResponse response) throws Exception {

		List<Map<String, Object>> list = (List<Map<String, Object>>) paramMap.get("userList");
		List months = (List) paramMap.get("months");
		int num = 0;
		for (Map<String, Object> map : list) {
			map.put("userSeq", num + 1);
			num++;
		}

		List<Map<String, Object>> datas = new ArrayList<>();
		Map<String, Object> dataMap = new CamelHashMap();
		dataMap.put("subTitle", paramMap.get("listDate") + " 회원등급목록");
		dataMap.put("headers",
				new String[] { "순번", "회원종류", "회원이름", "닉네임", "아이디", "전월방문횟수", "전월이용시간", months.get(0).toString(),
						months.get(1).toString(), months.get(2).toString(), months.get(3).toString(),
						months.get(4).toString(), months.get(5).toString(), months.get(6).toString() });
		dataMap.put("list", list);
		datas.add(dataMap);
		return;
	}

}
