package com.sch.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import com.sch.dao.CommonDao;
import com.sch.util.CamelHashMap;

@Service
public class NoticeService {


	@Autowired
	private CommonDao commonDao;
	
    @Autowired
    CommonService commonService;

	@Autowired
	FileService fileService;

	 
	
    public void insertNotice(Map<String, Object> paramMap, MultipartFile[] noticeFiles) throws Exception {
        fileService.filesRegist(noticeFiles, paramMap, "file");
        System.out.println("blogFiles : " + noticeFiles);
        commonService.insert("notice.insertNotice", paramMap);
    }

    public Map<String, Object> selectNotice(Map<String, Object> paramMap) throws Exception {
        Map<String, Object> resultMap = commonService.selectMap("notice.selectNotice", paramMap);
        Map<String, Object> prevNoticeMap = commonService.selectMap("notice.selectPrevNotice", paramMap);
        Map<String, Object> nextNoticeMap = commonService.selectMap("notice.selectNextNotice", paramMap);
        resultMap.put("prevNoticeSeq", prevNoticeMap.get("noticeSeq"));
        resultMap.put("nextNoticeSeq", nextNoticeMap.get("noticeSeq"));
        return resultMap;
    }

    public List<Map<String, Object>> selectNoticeList(Map<String, Object> paramMap) {
		System.out.println("selectList paramMap" + paramMap);
      //  int dataPerPage = Integer.parseInt(paramMap.get("dataPerPage").toString());
		//  int currentPage = Integer.parseInt(paramMap.get("currentPage").toString());
		//   int offsetNumber = (currentPage-1) * dataPerPage;
		//String noticeSeq = paramMap.get("noticeSeq").toString();
		//   paramMap.put("noticeSeq", noticeSeq);
		//	System.out.println("selectList paramMap" + paramMap);
        //   paramMap.put("dataPerPage", dataPerPage);
        //     paramMap.put("currentPage", currentPage);
        return commonService.selectList("notice.selectNoticeListPaging", paramMap);
    }

    public Map<String, Object> getPagination(Map<String, Object> paramMap) throws Exception {
        Double currentPage = Double.parseDouble(paramMap.get("currentPage").toString());
        Double dataPerPage = Double.parseDouble(paramMap.get("dataPerPage").toString());
        Double pageCount = Double.parseDouble(paramMap.get("pageCount").toString());

        Map<String, Object> resultMap = commonService.selectMap("notice.getTotalCount", paramMap);
        Double totalData = Double.parseDouble(resultMap.get("totalData").toString());

        Double totalPage =  Math.ceil(totalData / dataPerPage); // Total number of pages
        Double pageGroup = Math.ceil(currentPage / pageCount); // Current page group

        Double last = pageGroup * pageCount; // Last page number to display

        Double first = last - (pageCount - 1) <= 0 ? 1 : last - (pageCount - 1); // 화면에 보여질 첫번째 페이지 번호
        if (last > totalPage) {
            last = totalPage;
        }
        int temp = (int) Math.floor(totalData);

        resultMap.put("totalData", (int) Math.floor(totalData));
        resultMap.put("totalPage", (int) Math.floor(totalPage));
        resultMap.put("pageGroup", (int) Math.floor(pageGroup));
        resultMap.put("last", (int) Math.floor(last));
        resultMap.put("first", (int) Math.floor(first));

        return resultMap;
    }
    
    // 2024.03.14 커스텀

	public void insertnotice(Map<String, Object> paramMap) {
		commonService.insert("Notice.insertnotice", paramMap);

		List<Map<String, Object>> noticeList = (List<Map<String, Object>>) paramMap.get("noticeList");
		Map<String, Object> resultMap = new CamelHashMap();

		noticeList.forEach(x -> {
			resultMap.put("noticeSeq", paramMap.get("noticeSeq"));
			resultMap.put("noticeSn", x.get("noticeSn"));
			resultMap.put("noticeDt", x.get("noticeDt"));
			resultMap.put("noticeBeginTime", x.get("noticeBeginTime"));
			resultMap.put("noticeEndTime", x.get("noticeEndTime"));
			commonService.insert("Notice.insertnoticeFx", resultMap);
		});

	}

	public int updateCnstlAndCnstlFx(Map<String, Object> paramMap) throws Exception {
		if (update("notice.updateCnstlFx", paramMap) < 0) {
			return -1;
		}
		if (update("notice.updateCnstl", paramMap) < 0) {
			return -1;
		}
		return 1;
	}

	public int update(String sqlId, Map<String, Object> paramMap) throws Exception {
		if (sqlId.equals("notice.updateNoticeFx")) {
			Map<String, Object> parsedMap = parseMapBetweenFrontAndDbForNoticeFxTable(paramMap);
			return commonDao.update(sqlId, parsedMap);
		} else if (sqlId.equals("notice.updateCnstlFx")) {
			int originDetailCnstlCo = (int) paramMap.get("originDetailCnstlCo"); // 상담수
			int CnstlCo = (int) paramMap.get("CnstlCo");
			int addedCnstlCo = CnstlCo - originDetailCnstlCo;
			for (int i = 0; i < Integer.parseInt(paramMap.get("originDetailCnstlCo").toString()); i++) {
				paramMap.put("cnstlSn", i);

				ArrayList<String> noticeFxList = (ArrayList<String>) paramMap.get("cnstlCountArray");
				paramMap.put("noticeDt", noticeFxList.get(i).replaceAll("-", ""));

				ArrayList<String> cnstlBeginHourList = (ArrayList<String>) paramMap.get("cnstlBeginHourArr");
				paramMap.put("currentBeginHour", cnstlBeginHourList.get(i));

				ArrayList<String> cnstlBeginMinuteList = (ArrayList<String>) paramMap.get("cnstlBeginMinuteArr");
				paramMap.put("currentBeginMinute", cnstlBeginMinuteList.get(i));

				ArrayList<String> cnstlEndHourList = (ArrayList<String>) paramMap.get("cnstlEndHourArr");
				paramMap.put("currentEndHour", cnstlEndHourList.get(i));

				ArrayList<String> cnstlEndMinuteList = (ArrayList<String>) paramMap.get("cnstlEndMinuteArr");
				paramMap.put("currentEndMinute", cnstlEndMinuteList.get(i));

				commonDao.update(sqlId, paramMap);
			}
			for (int i = 0; i < addedCnstlCo; i++) {
				paramMap.put("cnstlSn", (originDetailCnstlCo + i));

				ArrayList<String> noticeFxList = (ArrayList<String>) paramMap.get("cnstlCountArray");
				paramMap.put("noticeDt", noticeFxList.get(originDetailCnstlCo + i).replaceAll("-", ""));

				ArrayList<String> cnstlBeginHourList = (ArrayList<String>) paramMap.get("cnstlBeginHourArr");
				paramMap.put("startHour", cnstlBeginHourList.get(originDetailCnstlCo + i));

				ArrayList<String> cnstlBeginMinuteList = (ArrayList<String>) paramMap.get("cnstlBeginMinuteArr");
				paramMap.put("startMinute", cnstlBeginMinuteList.get(originDetailCnstlCo + i));

				ArrayList<String> cnstlEndHourList = (ArrayList<String>) paramMap.get("cnstlEndHourArr");
				paramMap.put("endHour", cnstlEndHourList.get(originDetailCnstlCo + i));

				ArrayList<String> cnstlEndMinuteList = (ArrayList<String>) paramMap.get("cnstlEndMinuteArr");
				paramMap.put("endMinute", cnstlEndMinuteList.get(originDetailCnstlCo + i));

				commonDao.insert("notice.insertCnstlFx", paramMap);
			}
			return Integer.parseInt(paramMap.get("CnstlCo").toString()) + addedCnstlCo;
		} else if (sqlId.equals("notice.updateCnstl")) {
			Map<String, Object> parsedMap = parseMapBetweenFrontAndDbForCnstlTable(paramMap);
			return commonDao.update(sqlId, parsedMap);
		}
		return 0;
	}

	public Map<String, Object> parseMapBetweenFrontAndDbForNoticeFxTable(Map<String, Object> paramMap) {
		ArrayList<String> noticeFxList = (ArrayList<String>) paramMap.get("noticeFxArray");
		String noticeFxStr = noticeFxList.toString();
		noticeFxStr = noticeFxStr.replace("[", "").replace("]", "");
		paramMap.put("noticeFxStr", noticeFxStr);
		return paramMap;
	}

	public Map<String, Object> parseMapBetweenFrontAndDbForCnstlTable(Map<String, Object> paramMap) {
		ArrayList<String> cnstlNumList = (ArrayList<String>) paramMap.get("cnstlNum");
		int CnstlCo = cnstlNumList.size();
		paramMap.put("CnstlCo", CnstlCo);

		ArrayList<String> noticeDtList = (ArrayList<String>) paramMap.get("noticeDtArray");
		String noticeDtStr = noticeDtList.toString().replace("[", "").replace("]", "");
		paramMap.put("noticeDtStr", noticeDtStr);
		String startDtStr = paramMap.get("startDt").toString().replaceAll("-", "");
		paramMap.put("startDtStr", startDtStr);

		String endDtStr = paramMap.get("endDt").toString().replaceAll("-", "");
		paramMap.put("endDtStr", endDtStr);

		String startPeriodDtStr = paramMap.get("startPeriodDt").toString().replaceAll("-", "");
		paramMap.put("startPeriodDtStr", startPeriodDtStr);

		String endPeriodDtStr = paramMap.get("endPeriodDt").toString().replaceAll("-", "");
		paramMap.put("endPeriodDtStr", endPeriodDtStr);
		return paramMap;
	}

	public List<Map<String, Object>> noticeSeqList(Map<String, Object> paramMap) throws Exception {
		if (ObjectUtils.isEmpty(paramMap.get("noticeSeq"))) {
			return null;
		}
		// get
		return commonDao.selectList("Notice.selectNoticeDetail", paramMap);
	}

	/*
	 * public void deleteNoticeOne(Map<String, Object> paramMap) throws Exception {
	 * commonService.delete("Notice.deleteNoticeOne", paramMap); if
	 * (!CommonUtil.isEmpty(paramMap.get("noticeSeq"))) {
	 * commonService.delete("Notice.deleteNoticeOneFx", paramMap); }
	 *
	 * return; }
	 *
	 */

	public Map<String, Object> selectNoticeDetail(String sqlId, Map<String, Object> paramMap) {
		return commonDao.selectMap(sqlId, paramMap);
	}

	public int deleteNoticeOne(Map<String, Object> paramMap) throws Exception {
		if (delete("Notice.deleteNoticeOneFx", paramMap) < 0) {
			return -1;
		}
		if (delete("Notice.deleteNoticeOne", paramMap) < 0) {
			return -1;
		}
		return 1;
	}

	public int delete(String sqlId, Map<String, Object> paramMap) throws Exception {
		if (sqlId.equals("Notice.deleteNoticeOneFx")) {
			return commonDao.delete(sqlId, paramMap);
		} else if (sqlId.equals("Notice.deleteNoticeOne")) {
			return commonDao.delete(sqlId, paramMap);
		}
		return 0;
	}

	public Map<String, Object> selectNotice(String sqlId, Map<String, Object> paramMap) {
		return (Map) commonDao.selectOne(sqlId, paramMap);
	}

 
}
