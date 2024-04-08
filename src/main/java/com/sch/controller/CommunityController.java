package com.sch.controller;

import java.util.HashMap;
import java.util.List;
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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import com.sch.config.security.JwtTokenProvider;
import com.sch.service.CommonService;
import com.sch.service.CommunityService;
import com.sch.service.FileService;
import com.sch.util.CamelHashMap;
import com.sch.util.CommonResponse;
import com.sch.util.CommonUtil;
import com.sch.util.UserParam;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@Api(tags = "Community - 커뮤니티")
@RestController
@RequestMapping("/communityController")
public class CommunityController {
 
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	CommonService commonService;

	@Autowired
	CommunityService communityService;

	@Autowired
	FileService fileService;

	@Autowired
	JwtTokenProvider jwtTokenProvider;

	/**
	 * 커뮤니티 Insert
	 * 
	 * @PostMapping(value = "/insertCommunity.api") public ResponseEntity<?>
	 *                    insertCommunity( @RequestBody Map<String, Object>
	 *                    paramMap) throws Exception { ServletRequestAttributes
	 *                    attributes = (ServletRequestAttributes)
	 *                    RequestContextHolder.getRequestAttributes();
	 *                    HttpServletRequest request = attributes.getRequest();
	 *                    //communityService.insertCommunity(paramMap, file);
	 *                    communityService.insertCommunity(paramMap); return
	 *                    CommonResponse.statusResponse(HttpServletResponse.SC_OK);
	 *                    }
	 * 
	 */
	@PostMapping(value = "/insertCommunity.api", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "커뮤니티 INSERT", notes = "커뮤니티 게시글 등록합니다" + //
			",\t{\r\n" + //
			"\t\t\"communitySj\": \"군고구마 먹고싶다\",\r\n" + //
			"\t\t\"communityCn\": \"밤 고구마도 좋아\",\r\n" + //
			"\t\t\"division\": \"F\",\r\n" + //
			"\t\t\"tag\": \"video\"\r\n" + //
			"\t}\r\n" + //
			"\t")
	public ResponseEntity<?> insertCommunity(
			@RequestParam(value = "blogFiles", required = false) MultipartFile[] blogFiles,
			@RequestPart(value = "paramMap", required = true) Map<String, Object> paramMap) throws Exception {
		ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		HttpServletRequest request = attributes.getRequest();
		Map<String, Object> tmpMap = new CamelHashMap();
		tmpMap.put("userSeq", paramMap.get("registId"));

		// Map<String, Object> sessionMap =
		// CommonUtil.loginSession.get(jwtTokenProvider.getUserPk(jwtTokenProvider.resolveToken(request)));

		// Map<String, Object> loginMap = commonService.selectMap("login.selectLogin", (Map<String, Object>) paramMap.get("registId"));
		Map<String, Object> loginMap = commonService.selectMap("login.selectLogin", tmpMap);
		// loginMap 에 userSeq를 추가한다.
		paramMap.put("userSeq", tmpMap.get("userSeq"));

		// 로그인 검증 이후 메모리 loginSession세팅 후 데이터와 accessToken 리턴
		CommonUtil.loginSession.put((String) tmpMap.get("userSeq"), tmpMap);

		logger.info("tmpMap : " + tmpMap);
		logger.info("paramMap : " + paramMap);

		// String loginUserSeq = (String) sessionMap.get("userSeq");
		String loginUserSeq = (String) tmpMap.get("userSeq");
		logger.info("loginUserSeq : " + loginUserSeq);
		if (loginUserSeq.equals(paramMap.get("registId")) || CommonUtil.isEmpty(paramMap.get("registId"))) {
			Map<String, Object> userMap = new HashMap<String, Object>();
			userMap.put("userSeq", loginUserSeq);
			paramMap.put("user", userMap);
			communityService.insertCommunity(paramMap, blogFiles);
			return CommonResponse.statusResponse(HttpServletResponse.SC_OK);
		} else {
			return CommonResponse.statusResponse(HttpServletResponse.SC_BAD_REQUEST, "작성자만 수정 가능합니다.");
		}
		// communityService.insertCommunity(paramMap, file);
		// communityService.insertCommunity(paramMap, file);
	}

	/**
	 * board List
	 */
	@PostMapping(value = "/selectBoardList.api")
	@ApiOperation(value = "board 목록", notes = "board 목록을 불러옵니다.")
	public ResponseEntity<?> selectBoardList(@UserParam Map<String, Object> paramMap) {

		Map<String, Object> faqMap = commonService.selectPaging("community.selectFaqListPaging", paramMap);
		List<Map<String, Object>> faqList = (List<Map<String, Object>>) faqMap.get("list");
		faqList.forEach(x -> {
			if (!CommonUtil.isEmpty(x.get("fileSeq"))) {
				List<Map<String, Object>> fileList = commonService.selectList("file.selectFileDetail", x);
				x.put("fileList", fileList);
			}
		});
		return CommonResponse.statusResponse(HttpServletResponse.SC_OK,
				commonService.selectPaging("community.selectFaqListPaging", paramMap));
	}

	/**
	 * 커뮤니티 List
	 *
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	@PostMapping(value = "/selectCommunityList.api")
	@ApiOperation(value = "커뮤니티 리스트 출력(페이징)", notes = "현재 DB에 저장된 모든 커뮤니티 리스트를 출력합니다.")
	public ResponseEntity<?> selectCommunityList(@RequestBody Map<String, Object> paramMap) throws Exception {

		return CommonResponse.statusResponse(HttpServletResponse.SC_OK,
				commonService.selectPaging("community.selectCommunityListPaging", paramMap));
	}

	/**
	 * 커뮤니티 Detail
	 *
	 * @param paramMap 커뮤니티 시퀀스(communitySeq) 들어있는 맵
	 * @return Map<String, Object> resultMap 커뮤니티 테이블
	 * @throws Exception
	 */
	@PostMapping(value = "/selectCommunity.api")
	@ApiOperation(value = "커뮤니티 상세 페이지", notes = "커뮤니티 상세내용을 출력합니다.")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "paramMap", required = true, value = "{\"communitySeq\": \"COMMUNITY_00000005\"}", example = "{\"communitySeq\": \"COMMUNITY_00000005\"}", dataTypeClass = String.class) })
	public ResponseEntity<?> selectNotice(@RequestBody Map<String, Object> paramMap) throws Exception {
		return CommonResponse.statusResponse(HttpServletResponse.SC_OK, communityService.selectCommunity(paramMap));
	}

	/**
	 * 커뮤니티 Update
	 */
	@PostMapping(value = "/updateCommunity.api", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "커뮤니티 UPDATE", notes = "커뮤니티 게시글 수정합니다" + //
			",\t{\r\n" + //
			"\t\t\"communitySeq\": \"COMMUNITY_00000005\",\r\n" + //
			"\t\t\"communitySj\": \"군고구마 먹고싶다\",\r\n" + //
			"\t\t\"communityCn\": \"밤 고구마도 좋아\",\r\n" + //
			"\t\t\"division\": \"F\",\r\n" + //
			"\t\t\"tag\": \"poster\"\r\n" + //
			"\t}\r\n" + //
			"\t")
	public ResponseEntity<?> updateCommunity(@RequestPart(value = "file", required = true) MultipartFile[] file,
			@RequestParam(value = "paramMap", required = true) Map<String, Object> paramMap) throws Exception {
		ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		HttpServletRequest request = attributes.getRequest();

		String accessToken = jwtTokenProvider.resolveToken(request);
		String userSeq = jwtTokenProvider.getUserPk(accessToken);
		Map<String, Object> userMap = new HashMap<String, Object>();
		userMap.put("userSeq", userSeq);
		paramMap.put("user", userMap);
		communityService.updateCommunity(paramMap, file);
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
	@ApiImplicitParams({ @ApiImplicitParam(name = "paramMap", required = true, value = //
	"{\r\n" + //
			"\t\t\"pageNo\": \"1\",\r\n" + //
			"\t\t\"dataPerPage\": \"12\",\r\n" + //
			"\t\t\"pageCount\": \"10\"\r\n" + //
			"\t\t\"division\": \"F\"\r\n" + //
			"\t}\r\n" + //
			"\t", example = //
	"{\r\n" + //
			"\t\t\"pageNo\": \"1\",\r\n" + //
			"\t\t\"dataPerPage\": \"12\",\r\n" + //
			"\t\t\"pageCount\": \"10\"\r\n" + //
			"\t\t\"division\": \"F\"\r\n" + //
			"\t}\r\n" + //
			"\t", dataTypeClass = String.class) })
	public ResponseEntity<?> getPagination(@RequestBody Map<String, Object> paramMap) throws Exception {
		return CommonResponse.statusResponse(HttpServletResponse.SC_OK, communityService.getPagination(paramMap));
	}
}
