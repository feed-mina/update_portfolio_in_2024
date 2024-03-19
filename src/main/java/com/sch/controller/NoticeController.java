package com.sch.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.sch.config.security.JwtTokenProvider;
import com.sch.service.CommonService;
import com.sch.service.FileService;
import com.sch.service.NoticeService;
import com.sch.util.CommonResponse;
import com.sch.util.UserParam;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@Api(tags = "Notice - 공지사항")
@RestController
@RequestMapping("/noticeController")
public class NoticeController {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    CommonService commonService;

    @Autowired
    NoticeService noticeService;

    @Autowired
    FileService fileService;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    /**
     * 공지사항 List
     *
     * @param paramMap
     * @return
     * @throws Exception
     */
    @PostMapping(value = "/selectNoticeList.api")
    @ApiOperation(value = "공지사항 리스트 출력", notes = "현재 DB에 저장된 모든 공지사항 리스트를 출력합니다."+ //
            "\t{\r\n" + //
            "\t\t\"searchDivision\": \"noticeSj\",\r\n" + //
            "\t\t\"searchText\": \"설\",\r\n" + //
            "\t\t\"dataPerPage\": \"12\",\r\n" + //
            "\t\t\"currentPage\": \"2\"\r\n" + //
            "\t}\r\n" + //
            "\t")
    public ResponseEntity<?> selectNoticeList(@RequestBody Map<String, Object> paramMap) throws Exception {
        return CommonResponse.statusResponse(HttpServletResponse.SC_OK,
                noticeService.selectNoticeList(paramMap));
    }

    /**
     * 공지사항 Detail
     *
     * @param paramMap 공지사항 시퀀스(noticeSeq) 들어있는 맵
     * @return Map<String, Object> resultMap 공지사항 테이블
     * @throws Exception
     */
    @PostMapping(value = "/selectNotice.api")
    @ApiOperation(value = "공지사항 상세 페이지", notes = "공지사항 상세내용을 출력합니다.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "paramMap", required = true, value = "{\"noticeSeq\": \"NOTICE_00000002\"}", example = "{\"noticeSeq\": \"NOTICE_00000002\"}", dataTypeClass = String.class) })
    public ResponseEntity<?> selectNotice(@RequestBody Map<String, Object> paramMap) throws Exception {
        return CommonResponse.statusResponse(HttpServletResponse.SC_OK, noticeService.selectNotice(paramMap));
    }

    /**
     * 공지사항 Insert
     */
    @PostMapping(value = "/insertNotice.api", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "공지사항 INSERT", notes = "공지사항 게시글 등록합니다"+ //
            ",\t{\r\n" + //
            "\t\t\"noticeSj\": \"이번 설에 다들 즐거운 주말 보내세요\",\r\n" + //
            "\t\t\"noticeImageCn\": \"data\",\r\n" + //
            "\t\t\"noticeCn\": \"드디어 쉬는구나아\"\r\n" + //
            "\t}\r\n" + //
            "\t")
    public ResponseEntity<?> insertNotice(
            @RequestParam(value = "paramMap", required = true) Map<String, Object> paramMap) throws Exception {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        String accessToken = jwtTokenProvider.resolveToken(request);
        String userSeq = jwtTokenProvider.getUserPk(accessToken);
        Map<String, Object> userMap = new HashMap<String, Object>();
        userMap.put("userSeq", userSeq);
        paramMap.put("user", userMap);
        noticeService.insertNotice(paramMap);
        return CommonResponse.statusResponse(HttpServletResponse.SC_OK);
    }

    /**
     * 페이징
     *
     * @param paramMap 페이징 들어있는 맵
     * @return Map<String, Object> resultMap 페이징 데이터 맵
     * @throws Exception
     */
    @PostMapping(value = "/getPagination.api")
    @ApiOperation(value = "페이징 객체", notes = "페이징 내용을 출력합니다.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "paramMap", required = true,
                    value = //
                            "{\r\n" + //
                            "\t\t\"currentPage\": \"1\",\r\n" + //
                            "\t\t\"dataPerPage\": \"12\",\r\n" + //
                            "\t\t\"pageCount\": \"10\"\r\n" + //
                            "\t}\r\n" + //
                            "\t",
                    example = //
                            "{\r\n" + //
                            "\t\t\"currentPage\": \"1\",\r\n" + //
                            "\t\t\"dataPerPage\": \"12\",\r\n" + //
                            "\t\t\"pageCount\": \"10\"\r\n" + //
                            "\t}\r\n" + //
                            "\t",
                    dataTypeClass = String.class) })
    public ResponseEntity<?> getPagination(@RequestBody Map<String, Object> paramMap) throws Exception {
        return CommonResponse.statusResponse(HttpServletResponse.SC_OK, noticeService.getPagination(paramMap));
    }
    
    // 2024.03.14 커스텀

	@PostMapping(value = "/insertnotice.api")
	@ApiOperation(value = "notice 공통 UPSERT", notes = "notice(, 체험신청 공통사용하는 insert) ")
	@ApiImplicitParam(name = "paramMap", value = " 시퀀스(noticeSeq) 들어있는 맵", dataTypeClass = Map.class)
	public ResponseEntity<?> insertnotice(@UserParam Map<String, Object> paramMap) {
		noticeService.insertnotice(paramMap);
		return CommonResponse.statusResponse(HttpServletResponse.SC_OK);
	}

	@PostMapping(value = "/selectCusotmNoticeList.api")
	@ApiOperation(value = " 개설 리스트 조회", notes = "custom 공지 목록을 불러옵니다.")
	@ApiImplicitParam(name = "paramMap", value = " 시퀀스(noticeSeq) 들어있는 맵", dataTypeClass = String.class)
	public ResponseEntity<?> SelectCusotmNotice(@RequestBody Map<String, Object> paramMap) throws Exception {
		return CommonResponse.statusResponse(HttpServletResponse.SC_OK,
				noticeService.selectNotice("Notice.selectNoticeListPaging", paramMap));
	}
 
	@PostMapping(value = "/selectNoticeFx.api")
	@ApiOperation(value = "tb_notice_fx 목록", notes = "해당 의 tb_notice_fx을 조회합니다.")
	@ApiImplicitParam(name = "paramMap", value = " 시퀀스(lctreSeq) 들어있는 맵", dataTypeClass = Map.class)
	public ResponseEntity<?> selectNoticeFx(@RequestBody Map<String, Object> paramMap) throws Exception {
		return CommonResponse.statusResponse(HttpServletResponse.SC_OK,
				commonService.selectList("Notice.selectNoticeFx", paramMap));
	}
 
	@PostMapping(value = "/selectNoticeOperateList.api")
	@ApiOperation(value = "모든 상담운영 리스트 출력", notes = "현재 DB에 저장된 모든 상담운영 리스트를 출력합니다 .")
	public ResponseEntity<?> selectNoticeOperateList(@RequestBody Map<String, Object> paramMap) throws Exception {
		return CommonResponse.statusResponse(HttpServletResponse.SC_OK,
				commonService.selectPaging("Notice.selectNoticeOperateListPaging", paramMap));
	}

	/**
	 *  Detail
	 */
	@PostMapping(value = "/selectNoticeDetail.api")
	@ApiOperation(value = "Notice상세보기", notes = "선택한 Notice의 상세정보를 불러옵니다.")
	@ApiImplicitParam(name = "paramMap", value = " 시퀀스(noticeSeq) 들어있는 맵", dataTypeClass = Map.class)
	public ResponseEntity<?> selectNoticeDetail(@UserParam Map<String, Object> paramMap) {
		return CommonResponse.statusResponse(HttpServletResponse.SC_OK,
				noticeService.selectNoticeDetail("Notice.selectNoticeDetail", paramMap));
	}

	/**
	 *  Delete
	 */
	@PostMapping(value = "/deleteNoticeOne.api")
	@ApiOperation(value = " 삭제", notes = " 게시물을 삭제합니다")
	public ResponseEntity<?> deletenoticeOne(@RequestBody Map<String, Object> paramMap) throws Exception {
		int result = noticeService.deleteNoticeOne(paramMap);
		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put("result", result);
		if (result == -1) {
			return CommonResponse.statusResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
		return CommonResponse.statusResponse(HttpServletResponse.SC_OK, resultMap);
	}
    
}
