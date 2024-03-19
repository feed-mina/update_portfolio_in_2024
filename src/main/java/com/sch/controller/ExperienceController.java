package com.sch.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sch.service.CommonService;
import com.sch.service.ExperienceService;
import com.sch.util.CommonResponse;
import com.sch.util.UserParam;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

@Api(tags = "06 Experience - 체험예약 ")
@RestController
@RequestMapping("/experience")
public class ExperienceController {

	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Value("${sch.baseUrl}")
	private String baseUrl;

	@Autowired
	CommonService commonService;

	@Autowired
	ExperienceService experienceService;

	@PostMapping(value = "/insertexprn.api")
	@ApiOperation(value = "experience 공통 UPSERT", notes = "exprn(체험예약, 체험신청 공통사용하는 insert) ")
	@ApiImplicitParam(name = "paramMap", value = "체험예약 시퀀스(exprnSeq) 들어있는 맵", dataTypeClass = Map.class)
	public ResponseEntity<?> insertexprn(@UserParam Map<String, Object> paramMap) {
		experienceService.insertexprn(paramMap);
		return CommonResponse.statusResponse(HttpServletResponse.SC_OK);
	}

	@PostMapping(value = "/selectExperienceList.api")
	@ApiOperation(value = "체험예약 개설 리스트 조회", notes = "교수상당 목록을 불러옵니다.")
	@ApiImplicitParam(name = "paramMap", value = "체험예약 시퀀스(exprnSeq) 들어있는 맵", dataTypeClass = Map.class)
	public ResponseEntity<?> SelectExprnList(@RequestBody Map<String, Object> paramMap) throws Exception {
		return CommonResponse.statusResponse(HttpServletResponse.SC_OK,
		experienceService.selectExprnList(paramMap));
	}

	@PostMapping(value = "/selectExprnFx.api")
	@ApiOperation(value = "tb_exprn_fx 목록", notes = "해당 체험예약의 tb_exprn_fx을 조회합니다.")
	@ApiImplicitParam(name = "paramMap", value = "체험예약 시퀀스(lctreSeq) 들어있는 맵", dataTypeClass = Map.class)
	public ResponseEntity<?> selectExprnFx(@RequestBody Map<String, Object> paramMap) throws Exception {
		return CommonResponse.statusResponse(HttpServletResponse.SC_OK,
				commonService.selectList("Experience.selectExprnFx", paramMap));
	}

	@PostMapping(value = "/selectExprn.api")
	@ApiOperation(value = "체험예약테이블, 체험예약일정테이블, 체험예약키워드 테이블 출력", notes = "현재 DB에 저장된 체험예약내용을 출력합니다.")
	public ResponseEntity<?> selectExprn(@RequestBody Map<String, Object> paramMap) throws Exception {
		return CommonResponse.statusResponse(HttpServletResponse.SC_OK,
				experienceService.selectExprn("Experience.selectExprn", paramMap));
	}

	@PostMapping(value = "/selectExprnOperateList.api")
	@ApiOperation(value = "모든 체험예약 리스트 출력", notes = "현재 DB에 저장된 모든 체험예약 리스트를 출력합니다 .")
	public ResponseEntity<?> selectExprnOperateList(@RequestBody Map<String, Object> paramMap) throws Exception {
		return CommonResponse.statusResponse(HttpServletResponse.SC_OK,
				commonService.selectPaging("Experience.selectExprnOperateListPaging", paramMap));
	}

	/**
	 * 체험예약 Detail
	 */
	@PostMapping(value = "/selectExperienceDetail.api")
	@ApiOperation(value = "Experience상세보기", notes = "선택한 Experience의 상세정보를 불러옵니다.")
	@ApiImplicitParam(name = "paramMap", value = "체험예약 시퀀스(exprnSeq) 들어있는 맵", dataTypeClass = Map.class)
	public ResponseEntity<?> selectExperienceDetail(@UserParam Map<String, Object> paramMap) {
		return CommonResponse.statusResponse(HttpServletResponse.SC_OK,
				experienceService.selectExperienceDetail("Experience.selectExperienceDetail", paramMap));
	}

	@PostMapping(value = "/deleteExprnOne.api")
	@ApiOperation(value = "체험예약 삭제", notes = "체험예약 게시물을 삭제합니다")
	public ResponseEntity<?> deleteexprnOne(@RequestBody Map<String, Object> paramMap) throws Exception {
		int result = experienceService.deleteExprnOne(paramMap);
		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put("result", result);
		if (result == -1) {
			return CommonResponse.statusResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
		return CommonResponse.statusResponse(HttpServletResponse.SC_OK, resultMap);
	}
}
