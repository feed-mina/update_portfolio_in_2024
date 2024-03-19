package com.sch.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sch.config.security.JwtTokenProvider;
import com.sch.encrypt.AES256Encrypt;
import com.sch.encrypt.SHA256Encrypt;
import com.sch.service.CommonService;
import com.sch.service.LoginService;
import com.sch.service.MailService;
import com.sch.service.UserManageService;
import com.sch.util.CamelHashMap;
import com.sch.util.CommonResponse;
import com.sch.util.CommonUtil;
import com.sch.util.JSONObject;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

@Api(tags = "01 Login", description = "로그인 & 회원가입")
@RestController
@RequestMapping("/login")
public class LoginController {
	private Logger log = LoggerFactory.getLogger(this.getClass());
	private final UserManageService userManageService = new UserManageService();

	@Autowired
	private JwtTokenProvider jwtTokenProvider;

	@Autowired
	MailService mailServices;

	@Autowired
	CommonService commonService;

	@Autowired
	LoginService loginService;
	

	 @PostMapping(value = "/policyList.api")
	 @ApiOperation(value = "약관 조회", notes = "약관 안내 문구를 조회합니다.")
	 public ResponseEntity<?> policyList(@RequestBody Map<String, Object> paramMap) throws Exception {
		 List<Map<String, Object>> policyList = commonService.selectList("login.policyList", paramMap);
		 return CommonResponse.statusResponse(HttpServletResponse.SC_OK, policyList);
	 }
 
	// 질문목록 html에서 select/option태그 로 만들어서 value전달예정 requireSelector 먼저 질문목록에서 flag를
	// 받은 후 회원가입 페이지로 넘어간다.
	@PostMapping(value = "/userRegist.api")
	@ApiOperation(value = "회원가입", notes = "회원동의api 먼저 진행 후 api 값을 session으로 저장 > DB 저장 ")
	@ApiImplicitParam(name = "paramMap", required = true, dataTypeClass = String.class, value = "{\r\n" + //
			"\t\"userEmail\": \"sample@email.com\",\r\n" + //
			"\t\"userNm\": \"tester\",\r\n" + //
			"\t\"mbtlnum\": \"01012345678\",\r\n" + //
			"\t\"userPassword\": \"glasowk!@\",\r\n" + //
			"\t\"userPasswordCheck\": \"glasowk!@\",\r\n" + //
			"\t\"qestnCode\": \"가장좋아하는색은\",\r\n" + //
			"\t\"qestnRspns\": \"빨강색\" \r\n" + //
			"}", example = "{\r\n" + //
					"\t\"userEmail\": \"sample3@musicen.com\",\r\n" + //
					"\t\"userNm\": \"tester\",\r\n" + //
					"\t\"mbtlnum\": \"01012345678\",\r\n" + //
					"\t\"userPassword\": \"eoqkr!@34\",\r\n" + //
					"\t\"userPasswordCheck\": \"eoqkr!@34\",\r\n" + //
					"\t\"qestnCode\": \"QUESTION_1\",\r\n" + //
					"\t\"qestnRspns\": \"기타설명\"\r\n" + //
					"}") 
	public ResponseEntity<?> userRegist(@RequestBody Map<String, Object> paramMap, HttpServletRequest request)
			throws Exception {
		List<String> requiredList = Arrays.asList("userEmail", "userNm", "userPassword","userPasswordCheck", "mbtlnum", "qestnCode","qestnRspns");
		// requiredList 의 값이 없다면 null return

		// getWriter 호출
	//	if (!CommonUtil.validation(paramMap, requiredList)) {
	//		return null;
	//	}

		// email 기 가입자 체크
		paramMap.put("checkEntity", "userEmail");
		if (!ObjectUtils.isEmpty(commonService.selectMap("login.selectCheckExist", paramMap))) {
			return CommonResponse.statusResponse(HttpServletResponse.SC_BAD_REQUEST, "이미 가입된 email이 존재합니다.");
		}


		if(CommonUtil.isEmpty(paramMap.get("userEmail"))){
			return CommonResponse.statusResponse(HttpServletResponse.SC_BAD_REQUEST, "이메일을 입력해주세요.");
		}else if(CommonUtil.isEmpty(paramMap.get("userPassword"))) {
			return CommonResponse.statusResponse(HttpServletResponse.SC_BAD_REQUEST, "비밀번호를 입력해주세요.");
		}else if(CommonUtil.isEmpty(paramMap.get("userPasswordCheck"))) {
			return CommonResponse.statusResponse(HttpServletResponse.SC_BAD_REQUEST, "비밀번호를 확인해주세요.");
		}else if(CommonUtil.isEmpty(paramMap.get("userNm"))) {
			return CommonResponse.statusResponse(HttpServletResponse.SC_BAD_REQUEST, "이름을 입력해주세요.");
		}else if(CommonUtil.isNull(paramMap.get("mbtlnum"))) {
			return CommonResponse.statusResponse(HttpServletResponse.SC_BAD_REQUEST, "휴대폰 번호를 입력해주세요");
		}else if(CommonUtil.isEmpty(paramMap.get("qestnCode"))){
			return CommonResponse.statusResponse(HttpServletResponse.SC_BAD_REQUEST, "질문을 선택해주세요.");
		}else if(CommonUtil.isEmpty(paramMap.get("qestnRspns"))){
			return CommonResponse.statusResponse(HttpServletResponse.SC_BAD_REQUEST, "답변을 입력해주세요.");
		/*
		 * else if(paramMap.get("userPassword") != paramMap.get("userPasswordCheck")){
				return CommonResponse.statusResponse(HttpServletResponse.SC_BAD_REQUEST, "패스워드를 일치해야합니다.");
				*/
			};
		
		paramMap.put("userPasswordEnc", SHA256Encrypt.encrypt(paramMap.get("userPassword").toString()));
		HttpSession session = request.getSession();

		// getAttribute 저장한 변수값 가져오기 , setAttribute 변수값 저장하기

		// 일단 session에 동의여부 저장하기 , 이후 동의여부 저장한 값을 가져오기
		// 동의할때 session에 저장하는 api 따로 만들기, 여기서는 session에서 저장한 값을 가져오는거로만 한다.
		
		String policyMarktFlag = (String) session.getAttribute("sessionAgreementId1");
		String policyMberFlag = (String) session.getAttribute("sessionAgreementId2");
		System.out.println("세션에 저장도있는 policyMarkt 동의변수 :" + policyMarktFlag);
		System.out.println("세션에 저장도있는 policyMber 동의변수 :" + policyMberFlag);
		paramMap.put("policyMarkt", policyMarktFlag); 
		paramMap.put("policyMber", policyMberFlag); 
		if (policyMarktFlag != null) {
			paramMap.put("policyMarktFlag", paramMap.get("policyMarkt").toString());

		} else if (policyMberFlag != null) {
			paramMap.put("policyMberFlag", paramMap.get("policyMber").toString());
			//
		}  
 
		loginService.doSave(paramMap, request);

		return CommonResponse.statusResponse(HttpServletResponse.SC_OK, paramMap);
	}


	@PostMapping(value = "/login.api")
	@ApiOperation(value = "login(이메일/패스워드 방식)", notes = "로그인 후 <b>schToken</b> loginMap값을 리턴합니다.\n"
			+ "토큰값은 <b>request header</b> 부의 <b>X-AUTH-TOKEN</b> 값에 넣어서 자격인증합니다.\n" + "Spring Security가 적용되어 있습니다.")
	@ApiImplicitParam(name = "paramMap", required = true, dataTypeClass = String.class, value = "{\r\n" + //
			"\t\"userEmail\":\"이용자이메일\",\r\n" + //
			"\t\"userPassword\":\"이용자 비밀번호\"\r\n" + //
			"}", example = "{\r\n" + //
					"\t\"userEmail\":\"minyerin@musicen.com\",\r\n" + //
					"\t\"userPassword\":\"eoqkr!@34\"\r\n" + //
					"}")
	public ResponseEntity<?> login(@RequestBody Map<String, Object> paramMap) throws Exception {
		List<String> requiredList = Arrays.asList("userEmail", "userPassword");
		if (!CommonUtil.validation(paramMap, requiredList)) {
			return null;
		}

		Map<String, Object> loginMap = commonService.selectMap("login.selectLogin", paramMap);

		// 유저가없거나 패스워드가 없을경우
		if (ObjectUtils.isEmpty(loginMap) || ObjectUtils.isEmpty(loginMap.get("userPassword"))) {
			return CommonResponse.statusResponse(HttpServletResponse.SC_UNAUTHORIZED, "로그인 정보를 확인하세요.");
		}
		System.out.println("SHA256Encrypt userPassword:	"+(SHA256Encrypt.encrypt((String) paramMap.get("userPassword"))));
		// 비밀번호가 틀렸을경우
		if (!loginMap.get("userPassword").equals(SHA256Encrypt.encrypt((String) paramMap.get("userPassword")))) {
			return CommonResponse.statusResponse(HttpServletResponse.SC_UNAUTHORIZED, "비밀번호가 일치하지않습니다.");
		}

		// 탈퇴한 유저인경우
		if (loginMap.get("useAt").equals("D")) {
			return CommonResponse.statusResponse(HttpServletResponse.SC_UNAUTHORIZED, "탈퇴한 유저입니다.");
		}

		// 이메일인증을 받지않은 경우
		if (loginMap.get("useAt").equals("N")) {
			return CommonResponse.statusResponse(HttpServletResponse.SC_UNAUTHORIZED, "이메일 인증이 필요합니다.");
		}

		// loginMap 에 userSeq를 추가한다.
		paramMap.put("userSeq", loginMap.get("userSeq"));

		// tb_user_login 체류시간테이블에 loginDt insert commonService.insert("login.loginDt", loginMap);

		// 로그인 검증 이후 메모리 loginSession세팅 후 데이터와 accessToken 리턴
		CommonUtil.loginSession.put((String) loginMap.get("userSeq"), loginMap);

		// userPassword값을 지운다.
		loginMap.remove("userPassword");

		// loginMap값은 login.selectLogin xml에서 값을 map값으로 받은것. 여기에 userSeq값을 추가한다.
		// 로그인 검증 이후 메모리 loginSession세팅 후 데이터와 accessToken 리턴
		String accessToken = jwtTokenProvider.createToken((String) loginMap.get("userSeq"),
				(String) loginMap.get("userAuthor"));
		loginMap.put("accessToken", accessToken);
		System.out.println("accessToken : " + accessToken);

		return CommonResponse.statusResponse(HttpServletResponse.SC_OK, loginMap);
	}

	@PostMapping(value = "/userIdFind.api")
	@ApiOperation(value = "유저 아이디찾기", notes = "유저 아이디를 이름과 질문/답변으로 찾는다")
	@ApiImplicitParam(name = "paramMap", required = true, dataTypeClass = String.class, value = "{\r\n" + //
			"\t\"userNm\": \"이름1\",\r\n" + //
			"\t\"qestnCode\": \"QUESTION_1\",\r\n" + //
			"\t\"qestnRspns\": \"대답\"\r\n" + //
			"}", example = "{\r\n" + //
					"\t\"userNm\": \"테스터\",\r\n" + //
					"\t\"qestnCode\": \"QUESTION_1\",\r\n" + //
					"\t\"qestnRspns\": \"대답\"\r\n" + //
					"}")
	public ResponseEntity<?> userIdFind(@RequestBody Map<String, Object> paramMap) throws Exception {
		List<String> requiredList = Arrays.asList("userNm", "qestnCode", "qestnRspns");
		Map<String, Object> tmpMap = new CamelHashMap();

		Map<String, Object> userMap = commonService.selectMap("login.userIdFind", paramMap);
		tmpMap.put("userNm", userMap.get("userNm"));
		tmpMap.put("userEmail", userMap.get("userEmail")); 
 
	
		if(CommonUtil.isEmpty(paramMap.get("userNm"))){
			return CommonResponse.statusResponse(HttpServletResponse.SC_BAD_REQUEST, "이름을 입력해주세요.");
		}else if(CommonUtil.isEmpty(paramMap.get("qestnCode"))){
			return CommonResponse.statusResponse(HttpServletResponse.SC_BAD_REQUEST, "질문을 선택해주세요.");
		}else if(CommonUtil.isEmpty(paramMap.get("qestnRspns"))){
			return CommonResponse.statusResponse(HttpServletResponse.SC_BAD_REQUEST, "답변을 입력해주세요.");
			}else if(CommonUtil.isEmpty(tmpMap.get("userNm"))) {
				return CommonResponse.statusResponse(HttpServletResponse.SC_BAD_REQUEST, "계정을 찾을수 없습니다.");
			};
			System.out.println(tmpMap.get("userNm"));
System.out.println(tmpMap);
		return CommonResponse.statusResponse(HttpServletResponse.SC_OK, tmpMap);
	}

	@PostMapping(value = "/userPwFind.api")
	@ApiOperation(value = "로그인 할때 비밀번호찾기 페이지", notes = "이메일, 질문, 답변, 이름을 받아서 비밀번호 변경 메일을 보낸다")
	@ApiImplicitParam(name = "paramMap", required = true, dataTypeClass = String.class, value = "{\r\n" + //
			"\t\"userNm\": \"이름1\",\r\n" + //
			"\t\"qestnCode\": \"QUESTION_1\",\r\n" + //
			"\t\"qestnRspns\": \"대답\",\r\n" + //
			"\t\"userEmail\": \"sjh@musicen.com\"\r\n" + //
			"}", example = "{\r\n" + //
					"\t\"userNm\": \"민예린\",\r\n" + //
					"\t\"qestnCode\": \"QUESTION_1\",\r\n" + //
					"\t\"qestnRspns\": \"대답\",\r\n" + //
					"\t\"userEmail\": \"minyerin@musicen.com\"\r\n" + //
					"}")
	public ResponseEntity<?> userPwFind(@RequestBody Map<String, Object> paramMap, HttpServletRequest request)
			throws Exception {
		List<String> requiredList = Arrays.asList("userEmail", "userNm", "qestnCode", "qestnRspns");
	  
		paramMap.put("checkEntity", "userEmail");

		Map<String, Object> checkMap = commonService.selectMap("login.selectCheckExist", paramMap);
		Map<String, Object> tmpMap = new CamelHashMap();

		Map<String, Object> userMap = commonService.selectMap("login.userIdFind", paramMap);
		tmpMap.put("userNm", userMap.get("userNm"));
		tmpMap.put("userEmail", userMap.get("userEmail")); 

		if(CommonUtil.isEmpty(paramMap.get("userEmail"))){
			return CommonResponse.statusResponse(HttpServletResponse.SC_BAD_REQUEST, "이메일을 입력해주세요.");
		}else if(CommonUtil.isEmpty(paramMap.get("userNm"))){
			return CommonResponse.statusResponse(HttpServletResponse.SC_BAD_REQUEST, "이름을 입력해주세요.");
		}else if(CommonUtil.isEmpty(paramMap.get("qestnCode"))){
			return CommonResponse.statusResponse(HttpServletResponse.SC_BAD_REQUEST, "질문을 선택해주세요.");
		}else if(CommonUtil.isEmpty(paramMap.get("qestnRspns"))){
			return CommonResponse.statusResponse(HttpServletResponse.SC_BAD_REQUEST, "답변을 입력해주세요.");}else if(CommonUtil.isEmpty(tmpMap.get("userNm"))) {
				return CommonResponse.statusResponse(HttpServletResponse.SC_BAD_REQUEST, "계정을 찾을수 없습니다.");
			};
		

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("userSeq", checkMap.get("userSeq"));
		String jsonString = AES256Encrypt.encrypt(jsonObject.toString());

		// 이메일 전송
		String targetUrl = CommonUtil.getHostAddress(request) + "/sch/user/userPwFind.html?encData=" + jsonString;
		String receiver = checkMap.get("userEmail").toString();

		// 이메일 콘텐츠 생성
		Map<String, Object> mailData = new HashMap<>();
		mailData.put("subject", "Huss 관리자");
		mailData.put("title", "Huss 비밀번호변경");
		mailData.put("contents", "아래 링크는 비밀번호를 재설정할 수 있는 링크입니다.<br>링크를 클릭하여 비밀번호를 재설정해주세요.");
		mailData.put("targetUrl", targetUrl);
		mailData.put("targetUrlMsg", "비밀번호 변경하기");

		// emailService.sendMailNoTansaction(receiver, mailData);

		return CommonResponse.statusResponse(HttpServletResponse.SC_OK, tmpMap);
	}

	// 회원정보 수정 1단계
	@PostMapping(value = "/agreeChangePw.api")
	@ApiOperation(value = "로그인 후 회원정보 수정 페이지 전 현재 비밀번호로 회원정보를 확인합니다.", notes = "로그인 세션이 있어야 됩니다. 로그인 세션이 없다면 401 에러가 납니다. 비밀번호로 이용자가 맞는지 확인합니다.")
	@ApiImplicitParam(name = "paramMap", required = true, dataTypeClass = String.class, value = "{\r\n" + //
			"\t\"userSeq\": \"USER_00000018\",\r\n" + //
			"\t\"userPassword\": \"eoqkr!@34\",\r\n" + //
			"}\r\n" + //
			"", example = "{\r\n" + //
					"\t\"userSeq\": \"USER_00000019\",\r\n" + //
					"\t\"userPassword\": \"eoqkr!@34\"\r\n" + //
					"}\r\n" + //
					"")
	public ResponseEntity<?> agreeChange(@RequestBody Map<String, Object> paramMap, HttpServletRequest request) throws Exception {
		List<String> requiredList = Arrays.asList("userSeq"); 


		System.out.println("userPassword : "+paramMap.get("userPassword")); 
		Map<String, Object> loginMap = commonService.selectMap("login.selectLoginMap", paramMap);

		System.out.println("loginMap userPassword : "+loginMap.get("userPassword")); 
		// System.out.println("paramMap.get(\"userSeq\") : "+ SHA256Encrypt.encrypt((String) loginMap.get("userPassword")));
		// System.out.println("SHA256Encrypt userPassword:	"+(SHA256Encrypt.encrypt((String) paramMap.get("userPassword"))));
		// 비밀번호가 틀렸을경우
		if (!loginMap.get("userPassword").equals(SHA256Encrypt.encrypt((String) paramMap.get("userPassword")))) {
			return CommonResponse.statusResponse(HttpServletResponse.SC_UNAUTHORIZED, "비밀번호가 틀렸습니다. 다시 입력해주세요.");
		}  

		return CommonResponse.statusResponse(HttpServletResponse.SC_OK, "회원정보 수정페이지로 이동합니다.");
	}

// 회원정보 수정 2단계
	@PostMapping(value = "/modifyMembership.api")
	@ApiOperation(value = "회원정보 수정페이지", notes = "로그인 세션이 필요합니다. 로그인 세션이 없다면 401 에러가 납니다. 유저 비밀번호를 변경한다")
	@ApiImplicitParam(name = "paramMap", required = true, dataTypeClass = String.class, value = "{\r\n" + //
	"\t\"userSeq\": \"USER_00000019\",\r\n" + //
			"\t\"userPassword\": \"glasowk!@\",\r\n" + //
			"\t\"userNewPassword\": \"newglasowk!@\"\r\n" + //
			"\t\"newpasswordCheck\": \"newglasowk!@\"\r\n" + //
			"}", example = "{\r\n" + //
			"\t\"userSeq\": \"USER_00000019\",\r\n" + //
					"\t\"userPassword\": \"eoqkr!@34\",\r\n" + //
					"\t\"userNewPassword\": \"newglasowk!@\"\r\n" + //
					"\t\"newpasswordCheck\": \"newglasowk!@\"\r\n" + //
					"}")
	public ResponseEntity<?> modifyMembership(@RequestBody Map<String, Object> paramMap, HttpServletRequest request) throws Exception {

		//String userSeq = ((Map<String, Object>) paramMap.get("user")).get("userSeq").toString();


		Map<String, Object> loginMap = commonService.selectMap("login.selectLoginMap", paramMap);
		
System.out.println("loginMap: "+loginMap);
System.out.println("paramMap: "+paramMap);
System.out.println("SHA256Encrypt.encrypt((String) paramMap.get(\"userNewPassword\")): "+SHA256Encrypt.encrypt((String) paramMap.get("userNewPassword")) );

System.out.println("SHA256Encrypt.encrypt((String) paramMap.get(\"userPassword\")): "+SHA256Encrypt.encrypt((String) paramMap.get("userPassword")) );

		List<String> requiredList = Arrays.asList("userSeq");
		if (!CommonUtil.validation(paramMap, requiredList)) {
			return null;
		}

		 // 회원정보 수정페이지에서 새로운 비밀번호를 입력할때 기존 비밀번호와 같은지 확인한다.	

		// 기존 비밀번호가 틀렸을경우
		if (!loginMap.get("userPassword").equals(SHA256Encrypt.encrypt((String) paramMap.get("userPassword")))) {
			return CommonResponse.statusResponse(HttpServletResponse.SC_UNAUTHORIZED, "기존 비밀번호가 틀렸습니다. 다시 입력해주세요.");
		} 
		
		// userPassword 중복체크
		if (loginMap.get("userPassword").equals(SHA256Encrypt.encrypt((String) paramMap.get("userNewPassword")))) {
			return CommonResponse.statusResponse(HttpServletResponse.SC_UNAUTHORIZED, "기존의 비밀번호와 동일합니다.");
		}
	// userNewPassword 중복체크
	if (!(loginMap.get("userNewPassword").equals(SHA256Encrypt.encrypt((String) paramMap.get("newpasswordCheck"))))) {
		return CommonResponse.statusResponse(HttpServletResponse.SC_UNAUTHORIZED, "새 비밀번호와 확인 비밀번호가 동일해야합니다.");
	}
		
		paramMap.put("userSeq", paramMap.get("userSeq"));
		
		Map<String, Object> tmpMap = new CamelHashMap();
		paramMap.put("checkEntity", paramMap.get("userPassword"));
		paramMap.put("exceptMe", paramMap.get("userSeq")); 
 
		tmpMap.put("userSeq",paramMap.get("userSeq"));
		String userPasswordEnc = SHA256Encrypt.encrypt(paramMap.get("userNewPassword").toString());
		tmpMap.put("userNewPassword", userPasswordEnc);
		loginService.doUpdate(tmpMap); // 비밀번호 수정 update sql
		return CommonResponse.statusResponse(HttpServletResponse.SC_OK);
	}


	 @PostMapping(value = "/logout.api")
	@ApiOperation(value = "로그아웃", notes = "체류시간테이블에 endDt 설정후 loginSession 메모리 정리")
	public ResponseEntity<?> logout(@RequestBody Map<String, Object> paramMap) throws Exception {
		 
// @RequestBody Map<String, Object> paramMap
		// loginSession메모리 삭제
			String userSeq =  (String) paramMap.get("userSeq");
			CommonUtil.loginSession.put(userSeq, null);

			return CommonResponse.statusResponse(HttpServletResponse.SC_OK);

	}


	@PostMapping(value = "/refreshToken.api")
	@ApiOperation(value = "토큰 갱신", notes = "기존의 토큰을 이용해 신규토큰을 발급합니다. expireTime은 계산하지않음")
	@ApiImplicitParam(name = "paramMap", required = true, dataTypeClass = String.class, value = "{\r\n" + //
			"\t\"accessToken\": \"eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiaWF0IjoxNjg3MTMwMTY4LCJleHAiOjE2ODcxNjYxNjh9._9m_NliD12Ypw_NufF_TBMEndfi1tNrm-eHKYTeTZak\"\r\n"
			+ //
			"}", example = "{\r\n" + //
					"\t\"accessToken\": \"eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiaWF0IjoxNjg3MTMwMTY4LCJleHAiOjE2ODcxNjYxNjh9._9m_NliD12Ypw_NufF_TBMEndfi1tNrm-eHKYTeTZak\"\r\n"
					+ //
					"}")

	public ResponseEntity<?> refreshToken(@RequestBody Map<String, Object> paramMap) {
		String oldToken = (String) paramMap.get("accessToken");

		try {
			String newToken = jwtTokenProvider.refreshToken(oldToken);
			paramMap.put("newToken", newToken);
			return CommonResponse.statusResponse(HttpServletResponse.SC_OK, paramMap);
		} catch (Exception e) {
			e.getStackTrace();
			return CommonResponse.statusResponse(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
		}
	}

	@PostMapping(value = "/sendRegisterMail.api")
	@ApiOperation(value = "회원가입 이메일 인증", notes = "회원 가입 또는 비밀번호 찾기 이메일 인증을 한다/0207 기준 202에러가 발생합니다.")
	@ApiImplicitParam(name = "paramMap", required = true, dataTypeClass = String.class, value = "{\r\n" + //
			"\t\"type\":\"register\",\r\n" + //
			"\t\"email\":\"이용자 이메일\",\r\n" + //
			"\t\"userPassword\":\"이용자 비밀번호\"\r\n" + //
			"}", example = "{\r\n" + //
					"\t\"type\":\"register\",\r\n" + //
					"\t\"email\":\"minyerin@musicen.com\",\r\n" + //
					"\t\"userPassword\":\"이용자 비밀번호\"\r\n" + //
					"}")
	public ResponseEntity<?> sendMail(@RequestBody Map<String, Object> paramMap, Map<String, String> typeMap ,HttpServletRequest request) throws Exception {
		Map<String, Object> resultMap = new HashMap<>();
		HttpStatus status = null;


		System.out.println("paramMap" + paramMap.toString());
		List<String> requiredList = Arrays.asList("userEmail", "userPassword");
		System.out.println("requiredList" + requiredList);
		try {
			if (!CommonUtil.validation(paramMap, requiredList)) {
				return null;
			}
		} catch (Exception e) {
			
		Map<String, Object> loginMap = (Map<String, Object>) commonService.selectOne("login.selectLogin", paramMap);


		loginService.sendVerificationMail((String) loginMap.get("userSeq"), request);
		// 유저가 없거나 패스워드가 없을 경우
		if (ObjectUtils.isEmpty(loginMap) || ObjectUtils.isEmpty(loginMap.get("userPassword"))) {
			return CommonResponse.statusResponse(HttpServletResponse.SC_UNAUTHORIZED, "로그인 정보를 확인하세요");
		}
		// 비밀번호가 틀렸을 경우
		if (!loginMap.get("userPassword").equals(SHA256Encrypt.encrypt((String) paramMap.get("userPassword")))) {
			return CommonResponse.statusResponse(HttpServletResponse.SC_UNAUTHORIZED, "비밀번호가 일치하지않습니다.");
		}
		// 이메일 인증을 받지 않은 경우외에 이미 인증했꺼나 탈퇴했거나 등등
		if (!loginMap.get("useAt").equals("N")) {
			return CommonResponse.statusResponse(HttpServletResponse.SC_UNAUTHORIZED, "이미 등록된 회원입니다. 관리자에게 문의해주세요");
		}
			e.printStackTrace();
		}

		String code = mailServices.sendMail(typeMap.get("type"), typeMap.get("userEmail"));
		if (code.equals("error")) {
			resultMap.put("message", "FAIL");
			status = HttpStatus.ACCEPTED;
		} else {
			resultMap.put("message", "SUCCESS");
			resultMap.put("code", code);
			status = HttpStatus.ACCEPTED;

		}

		return new ResponseEntity<Map<String, Object>>(resultMap, status);
	}



/*
	@PostMapping(value = "/sendFindPwMail.api")
	@ApiOperation(value = "비밀번호 찾기에서 이용자에게 코드 전송", notes = "비밀번호 찾기 이메일 인증을 한다/0209 기준 message SUCCESS, 202에러가 발생합니다.")
	@ApiImplicitParam(name = "paramMap", required = true, dataTypeClass = String.class, value = "{\r\n" + //
			"\t\"type\":\"findPw\",\r\n" + //
			"\t\"email\":\"이용자 이메일\"\r\n" + //
			"}", example = "{\r\n" + //
					"\t\"type\":\"findPw\",\r\n" + //
					"\t\"email\":\"minyerin@musicen.com\"\r\n" + //
					"}")
	public ResponseEntity<Map<String, Object>> sendFindPwMail(@RequestBody Map<String, String> paramMap) throws UnsupportedEncodingException, MessagingException {
		Map<String, Object> resultMap = new HashMap<>();
		HttpStatus status = null;

		String code = mailServices.sendMail(paramMap.get("type"), paramMap.get("email"));
		if (code.equals("error")) {
			resultMap.put("message", "FAIL");
			status = HttpStatus.ACCEPTED;
		} else {
			resultMap.put("message", "SUCCESS");
			resultMap.put("code", code);
			status = HttpStatus.ACCEPTED;

		}

		return new ResponseEntity<Map<String, Object>>(resultMap, status);
	}

 */

	/*
	 * 
	 * 
	@PostMapping(value = "/registerEmailComplete.api")
	@ApiOperation(value = "이메일인증", notes = "이메일을 통해받은 encData로 userSeq를 얻어서 useAt을 N->Y로 변경해 인증을 활성화합니다")
	@ApiImplicitParam(name = "paramMap", required = true, dataTypeClass = String.class, value = "{\r\n" + //
			"\t\"encData\": \"{\"userSeq\":\"USER_00000389\"} 값을 암호화하면 아래 예시가 됩니다 테스트(http://sjhtest.musicen.com/aes256.html) \"\r\n"
			+ //
			"}", example = "{\r\n" + //
					"\t\"encData\": \"p7o2IbkXbnIJiUy8DIVLSAuriREsAEOxO9KL8NqB4Y0=\"\r\n" + //
					"}")
	public ResponseEntity<?> registerEmailComplete(@RequestBody Map<String, Object> paramMap) throws Exception {
		String encData = AES256Encrypt.decrypt((String) paramMap.get("encData"));
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> encDatamap = mapper.readValue(encData, Map.class);
		System.out.println("encDatamap" + encDatamap);
		// 이미 이메일 인증을 한 유저인경우 처리
		Map<String, Object> tmpMap = new CamelHashMap();
		tmpMap.put("userSeq", encDatamap.get("userSeq"));
		encDatamap.put("user", tmpMap);
		List<Map<String, Object>> userList = commonService.selectList("login.selectLoginMap", encDatamap);
		System.out.println("userList" + userList);
		System.out.println("userList.size()" + userList.size());
		if (userList.size() == 1) {
			return CommonResponse.statusResponse(HttpServletResponse.SC_OK, "유저가 존재");
		}

		// 유저 use_at 업데이트 처리 {"userSeq":"USER_00000389"}
		encDatamap.put("useAt", "Y");
		loginService.useAtUpdate(encDatamap);

		return CommonResponse.statusResponse(HttpServletResponse.SC_OK, encDatamap);
	}

	 */
}