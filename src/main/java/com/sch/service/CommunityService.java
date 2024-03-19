package com.sch.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.sch.dao.CommonDao;

@Service
public class CommunityService {
	@Autowired
	private CommonDao commonDao;
	
    @Autowired
    FileService fileService;

    @Autowired
    CommonService commonService;
    
    public Map<String, Object> selectCommunityList(String sqlId, Map<String, Object> paramMap) { 
		return (Map) commonDao.selectOne(sqlId, paramMap);
	} 
    /*
     *
    public void insertCommunity(Map<String, Object> paramMap) throws Exception {
    //    fileService.filesRegist(bbsFiles, paramMap, "file");
        commonService.insert("community.insertCommunity", paramMap);
    }  
    
    * */
    public void insertCommunity(Map<String, Object> paramMap, MultipartFile[] blogFiles) throws Exception {
          fileService.filesRegist(blogFiles, paramMap, "file");
          System.out.println("blogFiles : " + blogFiles);
        commonService.insert("community.insertCommunity", paramMap);
    }

    public void updateCommunity(Map<String, Object> paramMap, MultipartFile[] blogFiles) throws Exception {
        fileService.fileRegist(blogFiles, paramMap, "file");
        commonService.update("community.updateCommunity", paramMap);
    }

    public Map<String, Object> selectCommunity(Map<String, Object> paramMap) throws Exception {
        Map<String, Object> resultMap = commonService.selectMap("community.selectCommunity", paramMap);
        Map<String, Object> prevNoticeMap = commonService.selectMap("community.selectPrevCommunity", paramMap);
        Map<String, Object> nextNoticeMap = commonService.selectMap("community.selectNextCommunity", paramMap);
        resultMap.put("prevCommunitySeq", prevNoticeMap.get("communitySeq"));
        resultMap.put("nextCommunitySeq", nextNoticeMap.get("communitySeq"));
        return resultMap;
    }

    public List<Map<String, Object>> selectCommunityList(Map<String, Object> paramMap) {
        int dataPerPage = Integer.parseInt(paramMap.get("dataPerPage").toString());
        int pageNo = Integer.parseInt(paramMap.get("pageNo").toString());
        int offsetNumber = (pageNo-1) * dataPerPage;
        paramMap.put("offsetNumber", offsetNumber);
        paramMap.put("dataPerPage", dataPerPage);
        return commonService.selectList("community.selectCommunityListPaging", paramMap);
    }

    public Map<String, Object> getPagination(Map<String, Object> paramMap) throws Exception {
        Double pageNo = Double.parseDouble(paramMap.get("pageNo").toString());
        Double dataPerPage = Double.parseDouble(paramMap.get("dataPerPage").toString());
        Double pageCount = Double.parseDouble(paramMap.get("pageLength").toString());

        Map<String, Object> resultMap = commonService.selectMap("community.getTotalCount", paramMap);
        Double totalData = Double.parseDouble(resultMap.get("totalData").toString());

        Double totalPage =  Math.ceil(totalData / dataPerPage); // Total number of pages
        Double pageGroup = Math.ceil(pageNo / pageCount); // Current page group

        Double last = pageGroup * pageCount; // Last page number to display

        Double first = last - (pageCount - 1) <= 0 ? 1 : last - (pageCount - 1); // 화면에 보여질 첫번째 페이지 번호
        if (last > totalPage) {
            last = totalPage;
        }

        resultMap.put("totalData", (int) Math.floor(totalData));
        resultMap.put("totalPage", (int) Math.floor(totalPage));
        resultMap.put("pageGroup", (int) Math.floor(pageGroup));
        resultMap.put("last", (int) Math.floor(last));
        resultMap.put("first", (int) Math.floor(first));

        return resultMap;
    }
}
