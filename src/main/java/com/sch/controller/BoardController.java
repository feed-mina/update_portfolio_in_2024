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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import com.sch.config.security.JwtTokenProvider;
import com.sch.service.BoardService;
import com.sch.service.CommonService;
import com.sch.service.FileService;
import com.sch.service.GalleryService;
import com.sch.util.CommonResponse;
import com.sch.util.CommonUtil;
import com.sch.util.UserParam;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * 게시판 API
 */
@Api(tags = "04 Board", description = "게시판 관리 - 공지사항, Q&A, FAQ, 공자루")
@RestController
@RequestMapping("/board")
public class BoardController {
	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	CommonService commonService;

	@Autowired
	BoardService boardService;

	@Autowired
	GalleryService GalleryService;

	@Autowired
	FileService fileService;

	@Autowired
	JwtTokenProvider jwtTokenProvider;

	/**
	 * 게시판공통 Insert, Update
	 */
	@PostMapping(value = "/upsertBbs.api", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "게시판탭 공통 UPSERT", notes = "게시판탭( 공지사항, Q&A, FAQ) 게시물 공통사용하는 UPSERT")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "bbsFiles", required = false, paramType = "formData", value = "files", dataTypeClass = Object.class),
			@ApiImplicitParam(name = "paramMap", required = true, paramType = "formData", dataTypeClass = String.class, value = "{\r\n"
					+ //
					"\t\"bbsCode\": \"게시판코드\",\r\n" + //
					"\t\"bbsSeq\": \"게시판일련번호 (insert시 null)\"\r\n" + //
					"}", example = "{\r\n" + //
							"\t\"bbsCode\": \"NOTICE\",\r\n" + //
							"\t\"bbsSeq\": \"1\"\r\n" + //
							"}") })
	public ResponseEntity<?> upsertBbs(@RequestParam(value = "bbsFiles", required = false) MultipartFile[] bbsFiles,
			@RequestPart(value = "paramMap", required = true) Map<String, Object> paramMap) throws Exception {

		ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		HttpServletRequest request = attributes.getRequest();
		Map<String, Object> sessionMap = CommonUtil.loginSession
				.get(jwtTokenProvider.getUserPk(jwtTokenProvider.resolveToken(request)));
	//	Map<String, Object> loginMap = commonService.selectMap("login.selectLogin",  paramMap.get("registId"));
		/*
		 * String accessToken = jwtTokenProvider.resolveToken(request); String userSeq =
		 * jwtTokenProvider.getUserPk(accessToken);
		 */

		String loginUserSeq = (String) sessionMap.get("userSeq");
		// String loginUserAuthor = (String) sessionMap.get("userAuthor");

		if (loginUserSeq.equals(paramMap.get("registId"))  || CommonUtil.isEmpty(paramMap.get("registId"))) {
			Map<String, Object> userMap = new HashMap<String, Object>();
			userMap.put("userSeq", loginUserSeq);
			//userMap.put("userAuthor", loginUserAuthor);
			paramMap.put("user", userMap);
			boardService.upsertBbs(paramMap, bbsFiles);
			return CommonResponse.statusResponse(HttpServletResponse.SC_OK);
		} else {
			return CommonResponse.statusResponse(HttpServletResponse.SC_BAD_REQUEST, "작성자 또는 관리자만 수정 가능합니다.");
		}

	}

	/**
	 * 광고판 영상 및 이미지 Insert, Update
	 */
	@PostMapping(value = "/upsertBanner.api", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "광고판 영상 및 이미지 저장", notes = "광고판용 영상 및 이미지 UPSERT")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "bbsFiles", required = false, paramType = "formData", value = "files", dataTypeClass = Object.class),
			@ApiImplicitParam(name = "paramMap", required = true, paramType = "body", dataTypeClass = String.class, value = "{\r\n"
					+ //
					"\t\"bannerType\": \"VIDEO OR IMAGE\",\r\n" + //
					"\t\"bannerNm\": \"영상 및 이미지 고정 이름부여\"\r\n" + //
					"}", example = "{\r\n" + //
							"\t\"bannerType\": \"VIDEO\",\r\n" + //
							"\t\"bannerNm\": \"adbanner.mp4\"\r\n" + //
							"}") })
	public ResponseEntity<?> upsertBanner(
			@RequestPart(value = "paramMap", required = true) Map<String, Object> paramMap,
			@RequestPart(value = "bbsFiles", required = false) MultipartFile[] bbsFiles) throws Exception {

		boardService.upsertBanner(paramMap, bbsFiles);
		return CommonResponse.statusResponse(HttpServletResponse.SC_OK);
	}

	/**
	 * 게시판공통 Delete
	 */
	@PostMapping(value = "/deleteBbsOne.api")
	@ApiOperation(value = "게시판탭 공통 삭제", notes = "게시판탭( 공지사항, Q&A, FAQ) 게시물 공통사용하는 삭제코드")
	@ApiImplicitParam(name = "paramMap", required = true, dataTypeClass = String.class, value = "{\r\n" + //
			"\t\"bbsCode\": \"게시판코드\",\r\n" + //
			"\t\"bbsSeq\": \"게시판일련번호\"\r\n" + //
			"}", example = "{\r\n" + //
					"\t\"bbsCode\": \"NOTICE\",\r\n" + //
					"\t\"bbsSeq\": \"BBS_00000001\"\r\n" + //
					"}")
	public ResponseEntity<?> deleteBbsOne(@UserParam Map<String, Object> paramMap) throws Exception {
		boardService.deleteBbsOne(paramMap);
		return CommonResponse.statusResponse(HttpServletResponse.SC_OK);
	}

	/**
	 * 공지사항 List
	@PostMapping(value = "/selectNoticeList.api")
	@ApiOperation(value = "공지사항 목록", notes = "공지사항 목록을 불러옵니다.")
	@ApiImplicitParam(name = "paramMap", required = true, dataTypeClass = String.class, value = "{\r\n" + //
			"\t\"searchText\": \"검색 시 검색어\"\r\n" + //
			"}", example = "{\r\n" + //
					"\t\"searchText\": \"\"\r\n" + //
					"}")
	public ResponseEntity<?> selectNoticeList(@UserParam Map<String, Object> paramMap) {
		return CommonResponse.statusResponse(HttpServletResponse.SC_OK,
				commonService.selectPaging("bbsNotice.selectNoticeListPaging", paramMap));
	}
*/
	/**
	 * 공지사항 Detail
	@PostMapping(value = "/selectNoticeDetail.api")
	@ApiOperation(value = "공지사항 상세보기", notes = "선택한 공지사항의 상세정보를 불러옵니다.")
	@ApiImplicitParam(name = "paramMap", required = true, dataTypeClass = String.class, value = "{\r\n" + //
			"\t\"bbsSeq\": \"게시판 일련번호\"\r\n" + //
			"}", example = "{\r\n" + //
					"\t\"bbsSeq\": \"BBS_00000121\"\r\n" + //
					"}")
	public ResponseEntity<?> selectNoticeDetail(@UserParam Map<String, Object> paramMap) {
	 
		commonService.update("commonBbs.inquireUpdt", paramMap);

		Map<String, Object> noticeMap = commonService.selectMap("bbsNotice.selectNoticeDetail", paramMap);
		if (!CommonUtil.isEmpty(noticeMap.get("fileSeq"))) {
			paramMap.put("fileSeq", noticeMap.get("fileSeq"));
			List<Map<String, Object>> fileList = commonService.selectList("file.selectFileDetail", paramMap);

			noticeMap.put("fileList", fileList);
		}
		return CommonResponse.statusResponse(HttpServletResponse.SC_OK, noticeMap);
	}
	*/

	/**
	 * QNA upsert API (유저용)
	 *
	 * @throws Exception
	 */
	@PostMapping(value = "/upsertUserQna.api")
	@ApiOperation(value = "유저용 QNA UPSERT", notes = "유저의 질문글을 등록 및 수정합니다..")
	@ApiImplicitParam(name = "paramMap", required = true, dataTypeClass = String.class, value = "{\r\n" + //
			"\t\"bbsSeq\": \"update 할 경우에만\",\r\n" + //
			"\t\"bbsSj\": \"짊문글 제목\",\r\n" + //
			"\t\"bbsCn\": \"짊문글 내용\",\r\n" + //
			"\t\"useAt\": \"비밀글여부(일반글:Y, 비밀글:S)\",\r\n" + //
			"\t\"bbsSe\": \"게시판구분 (질문글 카테고리구분(J:회원가입,L:로그인,U:이용문의,E:기타))\"\r\n" + //
			"}", example = "{\r\n" + //
					"\t\"bbsSeq\": \"\",\r\n" + //
					"\t\"bbsSj\": \"질문입니다.\",\r\n" + //
					"\t\"bbsCn\": \"답변입니다.\",\r\n" + //
					"\t\"useAt\": \"Y\",\r\n" + //
					"\t\"bbsSe\": \"J\"\r\n" + //
					"}")
	public ResponseEntity<?> upsertUserQna(@UserParam Map<String, Object> paramMap) throws Exception {
		if (CommonUtil.isEmpty(paramMap.get("bbsCode"))) {
			paramMap.put("bbsCode", "QNA");
		}
		commonService.insert("bbsQna.upsertUserQna", paramMap);
		return CommonResponse.statusResponse(HttpServletResponse.SC_OK);
	}

	/**
	 * Q&A List
	 */
	@PostMapping(value = "/selectQnaList.api")
	@ApiOperation(value = "Q&A 목록", notes = "Q&A 목록을 불러옵니다.")
	@ApiImplicitParam(name = "paramMap", required = true, dataTypeClass = String.class, value = "{\r\n" + //
			"\t\"bbsSe\": \"게시판코드 (특정카테고리만 검색시 게시판구분(J:회원가입,L:로그인,U:이용문의,E:기타))\",\r\n" + //
			"\t\"searchText\": \"검색 시 검색어\",\r\n" + //
			"\t\"userSeq\": \"유저일련번호 (유저페이지용)\"\r\n" + //
			"}", example = "{\r\n" + //
					"\t\"bbsSe\": \"\",\r\n" + //
					"\t\"searchText\": \"\",\r\n" + //
					"\t\"userSeq\": \"\"\r\n" + //
					"}")
	public ResponseEntity<?> selectQnaList(@UserParam Map<String, Object> paramMap) {
		return CommonResponse.statusResponse(HttpServletResponse.SC_OK,
				commonService.selectPaging("bbsQna.selectQnaListPaging", paramMap));
	}

	/**
	 * Q&A Detail
	 */
	@PostMapping(value = "/selectQnaDetail.api")
	@ApiOperation(value = "Q&A 상세보기", notes = "선택한 Q&A의 상세정보를 불러옵니다.")
	@ApiImplicitParam(name = "paramMap", required = true, dataTypeClass = String.class, value = "{\r\n" + //
			"\t\"bbsSeq\": \"게시판일련번호\",\r\n" + //
			"\t\"userSeq\": \"유저일련번호 (유저페이지용)\"\r\n" + //
			"}", example = "{\r\n" + //
					"\t\"bbsSeq\": \"BBS_00000032\",\r\n" + //
					"\t\"userSeq\": \"\"\r\n" + //
					"}")
	public ResponseEntity<?> selectQnaDetail(@UserParam Map<String, Object> paramMap) {
		return CommonResponse.statusResponse(HttpServletResponse.SC_OK,
				commonService.selectMap("bbsQna.selectQnaDetail", paramMap));
	}

	/**
	 * 파일 단건 삭제
	 */
	@PostMapping(value = "/deleteOneFile.api")
	@ApiOperation(value = "첨부파일 삭제", notes = "선택한 첨부파일을 삭제합니다.")
	@ApiImplicitParam(name = "paramMap", required = true, dataTypeClass = String.class, value = "{\r\n" + //
			"\t\"fileDetailSn\": \"파일상세순전\",\r\n" + //
			"\t\"fileSeq\": \"파일 일련번호\"\r\n" + //
			"}", example = "{\r\n" + //
					"\t\"fileDetailSn\": \"\",\r\n" + //
					"\t\"fileSeq\": \"\"\r\n" + //
					"}")
	public ResponseEntity<?> deleteOneFile(@UserParam Map<String, Object> paramMap) throws Exception {
		fileService.fileDelete(paramMap);
		return CommonResponse.statusResponse(HttpServletResponse.SC_OK);
	}

	/**
	 * FAQ List
	 */
	@PostMapping(value = "/selectFaqList.api")
	@ApiOperation(value = "FAQ 목록", notes = "FAQ 목록을 불러옵니다.")
	@ApiImplicitParam(name = "paramMap", required = true, dataTypeClass = String.class, value = "{\r\n" + //
			"\t\"bbsSe\": \"게시판코드 (특정카테고리만 검색시 게시판구분(J:회원가입,L:로그인,U:이용문의,E:기타))\",\r\n" + //
			"\t\"searchText\": \"검색 시 검색어\",\r\n" + //
			"\t\"userSeq\": \"유저일련번호 (유저페이지용)\"\r\n" + //
			"}", example = "{\r\n" + //
					"\t\"bbsSe\": \"\",\r\n" + //
					"\t\"searchText\": \"\",\r\n" + //
					"\t\"userSeq\": \"\"\r\n" + //
					"}")
	public ResponseEntity<?> selectFaqList(@UserParam Map<String, Object> paramMap) {

		Map<String, Object> faqMap = commonService.selectPaging("bbsFaq.selectFaqListPaging", paramMap);
		List<Map<String, Object>> faqList = (List<Map<String, Object>>) faqMap.get("list");
		faqList.forEach(x -> {
			if (!CommonUtil.isEmpty(x.get("fileSeq"))) {
				List<Map<String, Object>> fileList = commonService.selectList("file.selectFileDetail", x);
				x.put("fileList", fileList);
			}
		});
		return CommonResponse.statusResponse(HttpServletResponse.SC_OK, faqMap);
	}

	/**
	 * FAQ Detail
	 */
	@PostMapping(value = "/selectFaqDetail.api")
	@ApiOperation(value = "FAQ 상세보기", notes = "선택한 FAQ의 상세정보를 불러옵니다.")
	@ApiImplicitParam(name = "paramMap", required = true, dataTypeClass = String.class, value = "{\r\n" + //
			"\t\"bbsSeq\": \"게시판 일련번호\"\r\n" + //
			"}", example = "{\r\n" + //
					"\t\"bbsSeq\": \"BBS_00000121\"\r\n" + //
					"}")
	public ResponseEntity<?> selectFaqDetail(@UserParam Map<String, Object> paramMap) {
		/* 조회수 증가 */
		commonService.update("commonBbs.inquireUpdt", paramMap);

		Map<String, Object> faqMap = commonService.selectMap("bbsFaq.selectFaqDetail", paramMap);
		if (!CommonUtil.isEmpty(faqMap.get("fileSeq"))) {
			paramMap.put("fileSeq", faqMap.get("fileSeq"));
			List<Map<String, Object>> fileList = commonService.selectList("file.selectFileDetail", paramMap);

			faqMap.put("fileList", fileList);
		}
		return CommonResponse.statusResponse(HttpServletResponse.SC_OK, faqMap);
	}

	/**
	 * 공자루 API
	 */

	/**
	 * 공자루 목록 불러오기
	 *
	 * @param paramMap
	 * @return
	 */
	@PostMapping(value = "/selectGalleryList.api")
	@ApiOperation(value = "공자루 이미지리스트", notes = "공자루 목록을 불러옵니다.")
	@ApiImplicitParam(name = "paramMap", required = true, dataTypeClass = String.class, value = "")
	public ResponseEntity<?> selectGalleryList(@UserParam Map<String, Object> paramMap) {
		return CommonResponse.statusResponse(HttpServletResponse.SC_OK,
				commonService.selectList("Gallery.selectGalleryList", paramMap));
	}

	/**
	 * 공자루 detail 불러오기
	 *
	 * @param paramMap
	 * @return
	 */
	@PostMapping(value = "/selectGalleryOne.api")
	@ApiOperation(value = "공자루 detail", notes = "1개의 공자루 데이터를 불러옵니다.")
	@ApiImplicitParam(name = "paramMap", required = true, dataTypeClass = String.class, value = "")
	public ResponseEntity<?> selectGalleryOne(@UserParam Map<String, Object> paramMap) {
		return CommonResponse.statusResponse(HttpServletResponse.SC_OK,
				commonService.selectList("Gallery.selectGalleryOne", paramMap));
	}

	/**
	 * 공자루 사진 설명, 제목 upsert
	 *
	 * @param paramMap
	 * @return
	 */
	@PostMapping(value = "/usertContents.api")
	@ApiOperation(value = "공자루 사진 설명 업데이트 및 추가", notes = "공자루 사진 설명 및 제목 upsert")
	@ApiImplicitParam(name = "paramMap", required = true, dataTypeClass = String.class, value = "")
	public ResponseEntity<?> usertContents(@UserParam Map<String, Object> paramMap) {
		GalleryService.usertContents(paramMap);
		return CommonResponse.statusResponse(HttpServletResponse.SC_OK);
	}

	@PostMapping(value = "/upsertGallery.api", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "공자루 UPSERT", notes = "공자루 upsert")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "fileList", required = false, paramType = "formData", value = "files", dataTypeClass = Object.class),
			@ApiImplicitParam(name = "paramMap", required = true, paramType = "formData", dataTypeClass = String.class, value = "", example = "") })
	public ResponseEntity<?> upsertGallery2(@RequestParam(value = "fileList", required = false) MultipartFile[] fileList,
											@RequestPart(value = "paramMap", required = true) Map<String, Object> paramMap) throws Exception {

		ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		HttpServletRequest request = attributes.getRequest();

		String accessToken = jwtTokenProvider.resolveToken(request);
		String userSeq = jwtTokenProvider.getUserPk(accessToken);
		Map<String, Object> userMap = new HashMap<String, Object>();
		userMap.put("userSeq", userSeq);
		paramMap.put("user", userMap);
		GalleryService.upsert(paramMap, fileList);
		return CommonResponse.statusResponse(HttpServletResponse.SC_OK);
	}

	/**
	 * 유니티 전달 전시관 이미지 리스트 전달
	 *
	 * @param paramMap
	 * @return
	 */
	@PostMapping(value = "/galleryImageList.api")
	@ApiOperation(value = "공자루 detail", notes = "1개의 공자루 데이터를 불러옵니다.")
	@ApiImplicitParam(name = "paramMap", required = true, dataTypeClass = Object.class, value = "")
	public ResponseEntity<?> selectGalleryImageList(@UserParam Map<String, Object> paramMap) {
		List<Map<String, Object>> resultMap = commonService.selectList("Gallery.selectGalleryList", paramMap);
		resultMap.forEach(x -> {
			String fileSeq = (String) x.get("fileSeq");
			// 유니티요청 ==> sj, cn의 값이 NULL 일경우 공백으로 치환해서 return 할 것.
			x.putIfAbsent("sj", "");
			x.putIfAbsent("cn", "");
			x.putIfAbsent("fileSeq", "");
			if (fileSeq != null) {
				x.put("imgUrl", "cmmn/fileDownload.api?fileSeq=" + fileSeq);
				x.remove("fileSeq");
			}
		});
		return CommonResponse.statusResponse(HttpServletResponse.SC_OK, resultMap);
	}
}
