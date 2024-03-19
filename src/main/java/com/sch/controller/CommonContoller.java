package com.sch.controller;

import com.sch.config.security.JwtTokenProvider;
import com.sch.service.CommonService;
import com.sch.service.FileService; 
import com.sch.util.CamelHashMap;
import com.sch.util.CommonResponse;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(tags = " 공통 - 코드", description = "공통 코드를 불러옵니다")
@RestController
@RequestMapping("/cmmn")
public class CommonContoller {
	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	CommonService commonService;

	@Autowired
	FileService fileService;

	@Autowired
	JwtTokenProvider jwtTokenProvider;
 

	@PostMapping(value = "/userInfo.api")
	@ApiOperation(value = "유저 기본데이터", notes = "유저 기본데이터")
	@ApiImplicitParam(name = "paramMap", required = true, dataTypeClass = String.class, value = "'userSeq': 'USER_00000389'", example = "{}")
	public ResponseEntity<?> userInfo(@RequestBody Map<String, Object> paramMap) throws Exception {
		Map<String, Object> tmpMap = new HashMap<>();
		tmpMap.put("userSeq", paramMap.get("userSeq"));
		paramMap.put("user", tmpMap);
		Map<String, Object> result = commonService.selectMap("user.selectUserInfo", paramMap);
		List<Map<String, Object>> selectUserStplatList = commonService.selectList("user.selectUserStplat", paramMap);
		result.put("stplatCode", selectUserStplatList);
		// login user 당월 등급( 당월등급 null일 때 '브론즈' )
		paramMap = commonService.selectMap("userManage.selectUserOneGrade", paramMap);
		result.put("gradeNm", paramMap.get("gradCodeNm"));
		return CommonResponse.statusResponse(HttpServletResponse.SC_OK, result);
	}

	/**
	 * 코드 리스트
	 *
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	@PostMapping(value = "/selectCmmnCode.api")
	@ApiOperation(value = "공통코드 리스트출력")
	@ApiImplicitParam(name = "paramMap", required = true, dataTypeClass = String.class, value = "{\r\n" + //
			"\t\"cmmnCode\": \"코드명\",\r\n" + //
			"\t\"cmmnCodeNm\": \"코드이름\",\r\n" + //
			"\t\"upperCmmnCode\": \"상위코드명\",\r\n" + //
			"\t\"cmmnCodeEtc\": \"기타설명\"\r\n" + //
			"}", example = "{\r\n" + //
					"\t\"cmmnCode\": \"PERSNAL_REASON\",\r\n" + //
					"\t\"cmmnCodeNm\": \"개인 사유\",\r\n" + //
					"\t\"upperCmmnCode\": \"LCTRE_CANCEL_REASON\",\r\n" + //
					"\t\"cmmnCodeEtc\": \"\"\r\n" + //
					"}")
	public ResponseEntity<?> selectCmmnCode(@RequestBody Map<String, Object> paramMap) throws Exception {
		return CommonResponse.statusResponse(HttpServletResponse.SC_OK,
				commonService.selectList("cmmn.selectCmmnCode", paramMap));
	}

	/**
	 * 코드 리스트
	 *
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	@PostMapping(value = "/updateCmmnCode.api")
	@ApiOperation(value = "공통코드 업데이트")
	@ApiImplicitParam(name = "paramMap", required = true, dataTypeClass = String.class, value = "{\r\n" + //
			"\t\"cmmnCode\": \"코드명\",\r\n" + //
			"\t\"cmmnCodeEtc\": \"기타설명\"\r\n" + //
			"}", example = "{\r\n" + //
					"\t\"cmmnCode\": \"PERSNAL_REASON\",\r\n" + //
					"\t\"cmmnCodeEtc\": \"\"\r\n" + //
					"}")
	public ResponseEntity<?> updateCmmnCode(@RequestBody Map<String, Object> paramMap) throws Exception {
		return CommonResponse.statusResponse(HttpServletResponse.SC_OK,
				commonService.selectList("cmmn.updateCmmnCode", paramMap));
	}

	@GetMapping(value = "/fileDownload.api")
	@ApiOperation(value = "999-1 파일 다운로드(비로그인 for get)", notes = "파일 다운로드.(비로그인 app에서 사용 for get)")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "fileSeq", value = "FILE_00000176", example = "FILE_00000176", required = true, dataTypeClass = String.class)
	// ,
	// @ApiImplicitParam(name = "fileSn", value = "1", example = "1", required =
	// false, dataTypeClass = String.class)
	})
	public void fileDowndloadForGet(
			@RequestParam String fileSeq,
			@RequestParam(defaultValue = "1") String fileSn,
			HttpServletResponse response) throws Exception {
		Map<String, Object> paramMap = new CamelHashMap();
		// paramMap.put("fileId", fileId);
		// paramMap.put("fileSn", fileSn);

		// Map<String, Object> fileMap = commonService.selectMap("item.filedownselect",
		// paramMap);

		paramMap.put("fileSeq", fileSeq);
		paramMap.put("fileDetailSn", fileSn);

		Map<String, Object> fileMap = fileService.fileMap(paramMap);

		String fileCours = (String) fileMap.get("fileCours");
		File uFile = new File(fileCours);

		int fSize = (int) uFile.length();

		if (fSize > 0) {
			BufferedInputStream in = new BufferedInputStream(new FileInputStream(uFile));
			String mimetype = "application/octet-stream; charset=utf-8";
			response.setBufferSize(fSize);
			response.setContentType(mimetype);
			// 브라우저 별 한글 인코딩
			String orginlFileNm = (String) fileMap.get("orignlFileNm");
			String name = URLEncoder.encode(orginlFileNm, "UTF-8").replaceAll("\\+", "%20");
			response.setHeader("Content-Disposition", "attachment; filename=" + name + "");

			response.setContentLength(fSize);

			FileCopyUtils.copy(in, response.getOutputStream());
			in.close();
			response.getOutputStream().flush();
			response.getOutputStream().close();
		}
	}

	@PostMapping("/jwtForJitsi.api")
	public ResponseEntity<?> genJwtForJitsi(@RequestBody Map<String, Object> paramMap) {
		String originSecretKey = "jitsimeetsecret";
		String secretKey = Base64.getEncoder().encodeToString(originSecretKey.getBytes());
		String jwtToken = Jwts.builder()
				.setHeaderParam("typ", "JWT")
				.addClaims(paramMap)
				.signWith(SignatureAlgorithm.HS256, secretKey)
				.compact();
		Map<String, Object> resultMap = new CamelHashMap();
		resultMap.put("token", jwtToken);
		return CommonResponse.statusResponse(HttpServletResponse.SC_OK, resultMap);
	}

 

	/*
	 * base64인 유저 이미지 리턴하는 컨트롤러 /userImage?userSeq={userSeq}
	 */
	@GetMapping("/userImage")
	public void wer(@RequestParam String userSeq, HttpServletResponse response) throws IOException {
		//
		Map<String, Object> userMap = new CamelHashMap();
		userMap.put("userSeq", userSeq);
		
		Map<String, Object> paramMap = new CamelHashMap();
		paramMap.put("user", userMap);

		Map<String, Object> userInfo = commonService.selectMap("user.selectUserInfo", paramMap);
		String userBase64 = (String) userInfo.get("proflImageCn");

		userBase64 = (userBase64.split(","))[1];

		byte[] imageBytes = Base64.getDecoder().decode(userBase64);

		response.setContentType("image/jpeg"); // Change this to match your image format
        response.setContentLength(imageBytes.length);

        // Write the byte array to the response output stream
        response.getOutputStream().write(imageBytes);
	}

	@PostMapping("/getSeminaTranslator")
	public ResponseEntity<?> getSeminaTranslator(@RequestBody Map<String, Object> paramMap) {
		return CommonResponse.statusResponse(HttpServletResponse.SC_OK,
				commonService.selectMap("cmmn.seminaTranslator", paramMap));
	}
	
	@PostMapping("/getLctreTranslator")
	public ResponseEntity<?> getLctreTranslator(@RequestBody Map<String, Object> paramMap){
		return CommonResponse.statusResponse(HttpServletResponse.SC_OK
			,commonService.selectMap("cmmn.lctreTranslator", paramMap));
	}

}
