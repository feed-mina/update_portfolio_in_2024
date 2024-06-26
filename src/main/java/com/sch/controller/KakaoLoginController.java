package com.sch.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sch.config.security.JwtTokenProvider;
import com.sch.service.CommonService;
import com.sch.service.KaKaoAPIService;
import com.sch.util.CommonUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

@Api(tags = "Auth", description = "카카오로그인")
@RestController
@RequestMapping("/auth")
public class KakaoLoginController {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private JwtTokenProvider jwtTokenProvider;

	@Autowired
	CommonService commonService;

	@Autowired
	KaKaoAPIService kakaoService;

	public enum HttpMethodType {
		POST, GET, DELETE
	}

	private static final String KAKAO_CLIENT_ID = "bc6f76bb8856c35bd57a3fa6a4331069";
	private static final String KAKAO_REDIRECT_URI = "http://localhost:8189/auth/register.api";
	private static final String KAKAO_TOKEN_URI = "https://kauth.kakao.com/oauth/token";
	private static final String USER_SIGNUP_PATH = "/v1/user/signup"; // 연결하기
	private static final String USER_UNLINK_PATH = "/v1/user/unlink"; // 연결끊기
	private static final String USER_LOGOUT_PATH = "/v1/user/logout"; // 로그아웃
	private static final String USER_ME_PATH = "/v2/user/me"; // 사용자 정보 가져오기
	private static final String USER_UPDATE_PROFILE_PATH = "/v1/user/update_profile"; // 사용자 정보 저장하기
	private static final String USER_IDS_PATH = "/v1/user/ids"; // 사용자 목록 가져오기
	private static final String USER_TOKENS_PATH = "/v1/user/access_token_info"; // 토큰 정보 보기

	private static final String USER_POLICY = "/v2/user/scopes"; // 동의 내역 확인하기
	private static final String SERVICE_TERMS = "/v2/user/service_terms"; // 서비스 약관 동의 내역 확인

	private static final List<String> adminApiPaths = new ArrayList<String>();

	static {
		adminApiPaths.add(USER_ME_PATH);
		adminApiPaths.add(USER_IDS_PATH);
		adminApiPaths.add(USER_POLICY);
		adminApiPaths.add(SERVICE_TERMS);
		adminApiPaths.add(USER_SIGNUP_PATH);
		adminApiPaths.add(USER_LOGOUT_PATH);
	}

	// code가 헤더값에 들어가야 한다.
	// u8lnlrJoNmU2NLGgR5E3YfRkmY9KhvGXfM_j7AFJqCLWifOcRENskCzi3NwKPXWcAAABjYD2Oqv_A_o_BVb6-Q
	@GetMapping("/register.api")
	@ApiOperation(value = "카카오 가입/로그인할때 호출되는 api", notes = "http://52.78.212.203:8189/auth/kakao/callback?code={코드값}")
	@ApiImplicitParam(name = "code", required = true, dataTypeClass = String.class, value = "{\r\n" + //
			"\t\"code\": \"u8lnlrJoNmU2NLGgR5E3YfRkmY9KhvGXfM_j7AFJqCLWifOcRENskCzi3NwKPXWcAAABjYD2Oqv_A_o_BVb6-Q \"\r\n"
			+ //
			"}", example = "{\r\n" + //
					"\t\"code\": \"u8lnlrJoNmU2NLGgR5E3YfRkmY9KhvGXfM_j7AFJqCLWifOcRENskCzi3NwKPXWcAAABjYD2Oqv_A_o_BVb6-Q \"\r\n"
					+ //
					"}")
	public @ResponseBody  void kakaoCallback(String code, HttpSession session, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException { // Data를
																											 
		// 1. RestTemplte 객체 생성										 
		RestTemplate restTemplate = new RestTemplate();
		logger.info("카카오 인증 완료 code값" + code);
		//2. header설정을 위해 HttpHeaders 클래스를 생성한 후 HttpEntity 객체에 넣는다. 
		HttpHeaders httpHeaders = new HttpHeaders();
		// 2-1. 헤더설정 : ContentType, Accept 설정

		httpHeaders.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

		// 3. Body만들기 Body는 보통 key, value의 쌍으로 이루어지기 때문에 Java에서 제공해주는 MultiValueMap 타입을 사용한다.
		// 만들어진 Body는 SpringFramework에서 제공하는 HttpEntity 클래스에 추가하여 사용한다.
		
		// 3-1 MultiValueMap 객체 생성
		MultiValueMap<String, String> kakaoParamMap = new LinkedMultiValueMap<>();
		
		// 3-2 body 요청 파라미터 설정 body(ex kakaoParamMap).add("키","값");
		
		kakaoParamMap.add("grant_type", "authorization_code");
		kakaoParamMap.add("client_id", "bc6f76bb8856c35bd57a3fa6a4331069"); 
		// kakaoParamMap.add("redirect_uri", "http://localhost:8189/auth/register.api");  
		kakaoParamMap.add("redirect_uri", "http://ec2-52-78-212-203.ap-northeast-2.compute.amazonaws.com:8189/auth/register.api"); 
		kakaoParamMap.add("code", code);
		
		// 3-3. 만들어진 header와 body를 가진 HttpEntity(ex kakaoTokenRequest) 객체 생성
		HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(kakaoParamMap, httpHeaders); 

		// 3-4 요청 URL 을 정의한다.
		// String url = "https://kauth.kakao.com/oauth/token";
		// UriComponents uri = UriComponentsBuilder.fromHttpUrl(url).build();
		
 		// 4. exchange() 메소드로 api를 호출한다.  
		// RestTemplate.exchange("요청하는url",요청방식GET or POST, header+body값, response타입)
		// ResponseEntity<String> registerRes = restTemplate.exchange("https://kauth.kakao.com/oauth/token", HttpMethod.POST, 	kakaoTokenRequest, String.class);
		 ResponseEntity<String> registerRes = restTemplate.exchange( "https://kauth.kakao.com/oauth/token", HttpMethod.POST, kakaoTokenRequest, String.class);
		 //	ResponseEntity<Map> registerRes = restTemplate.exchange(uri.toString(), HttpMethod.POST, kakaoTokenRequest,Map.class);
		
		// 5. 결과값을 담을 객체를 생성한다.
		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		
		// 6. 요청한 결과를 HashMap에 추가한다.
		resultMap.put("statusCode", registerRes.getStatusCodeValue());
		resultMap.put("header", registerRes.getHeaders());
		resultMap.put("body", registerRes.getBody());
		
		// Retrofut2 , okHttp, RestTemplate 
		logger.info("카카오 토큰 요청 완료 : 토큰요청에 대한 응답값" + registerRes.getBody());

		// Jackson ObjectMapper를 사용하여 JSON 파싱
		ObjectMapper objectMapper = new ObjectMapper();
 
	 
		JsonNode jsonNodeToken = objectMapper.readTree( registerRes.getBody()); 
		// JSON 값에 개별적으로 접근
		String kakaoaccessToken = jsonNodeToken.get("access_token").textValue();
		// Json 문자열을 객체로 변환
		// MyObject myObject = objectMapper.readValue(jsonString, MyObject.class);

		logger.info("========0 ");
		logger.info("accessToken값 : " + kakaoaccessToken);
		/*
		 * response.getBody() 값은 아래와 같다. id_token과 scope는 openID 설정과 관련이 있다.
		 *
		 */
		// post방식으로 key-value 데이터를 요청(카카오쪽으로)

		// JSONObject jSONObject = new JSONObject();
		logger.info("========1 ");
		RestTemplate rt2 = new RestTemplate();
		// HttpHeaders2 오브젝트 생성
		HttpHeaders httpHeaders2 = new HttpHeaders();
		httpHeaders2.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
		httpHeaders2.add("Authorization", "Bearer " + kakaoaccessToken);
		logger.info("========2 ");
		// HttpHeader와 HttpBody를 하나의 오브젝트에 담기
		HttpEntity<MultiValueMap<String, String>> kakaoProfileRequest = new HttpEntity<>(httpHeaders2); // 오버로딩
		logger.info("====kakaoProfileRequest===3 " + kakaoProfileRequest);
		ResponseEntity<String> responseUserInfo = rt2.exchange("https://kapi.kakao.com/v2/user/me", HttpMethod.GET,
				kakaoProfileRequest, String.class);

		logger.info("responseUserInfo : " + responseUserInfo);
		logger.info("/v2/user/me 결과 :  " + responseUserInfo.getBody()); 
		// ObjectMapper를 생성할 때 JsonFactory 사용 예시
		ObjectMapper objectJsonMapper2 = new ObjectMapper(new JsonFactory());
		logger.info("========4 " + objectJsonMapper2);

		JsonNode jsonNodeUserInfo = objectMapper.readTree(responseUserInfo.getBody());
		logger.info("jsonNodeUserInfo : " + jsonNodeUserInfo);
		Number id = jsonNodeUserInfo.get("id").numberValue();
		logger.info("id: " + jsonNodeUserInfo.get("id").numberValue()); // 카카오는 비밀번호를 안알려 주므로 고유값인 id를 비밀번호로 저장한다.
		logger.info("connected_at: " + jsonNodeUserInfo.get("connected_at")); // 로그인한 날짜
		logger.info("properties: " + jsonNodeUserInfo.get("properties"));
		logger.info("kakao_account: " + jsonNodeUserInfo.get("kakao_account"));

		// "properties" 필드에 해당하는 하위 JsonNode 가져오기
		JsonNode propertiesNode = jsonNodeUserInfo.get("properties");
		JsonNode kakaoAccountNode = jsonNodeUserInfo.get("kakao_account");
		
		// 만약 /v2/user/me의 get메서드 결과 body값으로 properties의 값이 있다면 "properties"의 하위 필드 중 "nickname"과 "profile_image" 값 가져오기
		if (propertiesNode != null && propertiesNode.isObject()) { 
			String nickname = propertiesNode.get("nickname").asText();
			String profileImage = propertiesNode.get("profile_image").asText();

			// 추출된 값 출력 또는 활용
			logger.info("Nickname: " + nickname);
			logger.info("Profile Image: " + profileImage);
		} else {
			// "properties"가 없거나 객체가 아닌 경우 처리 로직 추가
			logger.info("가입이나 로그인을 할 수 없습니다. Invalid JSON properties structure");
		}
		

		// 만약 /v2/user/me의 get메서드 결과 body값으로 kakao_account의 값이 있다면 "kakao_account"의 하위 필드 중  값 가져오기
		if (kakaoAccountNode != null && kakaoAccountNode.isObject()) {
			// kakaoAccountNode값은 카카오 개발자 도구에서 앱 권한 신청으로 권한을 받아야 사용할 수 있다.
			String email = kakaoAccountNode.get("email").asText();
			String phone_number = kakaoAccountNode.get("phone_number").asText();
			String birthday = kakaoAccountNode.get("birthday").asText();
			String gender = kakaoAccountNode.get("gender").asText();
			Boolean agreementEmail = kakaoAccountNode.get("email_needs_agreement").asBoolean();
			Boolean agreementBirthday = kakaoAccountNode.get("birthday_needs_agreement").asBoolean();
			Boolean agreementGender = kakaoAccountNode.get("gender_needs_agreement").asBoolean();
			String name = kakaoAccountNode.get("name").asText();
			String cookieToken = id.toString();

			logger.info("email: " + email);
			logger.info("phone_number: " + phone_number);
			logger.info("email: " + email);
			logger.info("phone_number: " + phone_number);
			logger.info("birthday: " + birthday);
			logger.info("gender: " + gender);
			logger.info("agreementEmail: " + agreementEmail);
			logger.info("agreementGender: " + agreementGender);
			logger.info("name: " + name);
			
			Map<String, Object> paramMap = new HashMap<String, Object>(); 
			paramMap.put("email", email);
			paramMap.put("name", name);
			paramMap.put("id", id);
			paramMap.put("phone_number", phone_number);
			paramMap.put("name", name);
			paramMap.put("id", id);  
			
			Cookie Token = new Cookie("accessToken",  cookieToken);
			// Token.setDomain("localhost");
			Token.setDomain("52.78.212.203");
			Token.setPath("/");
			// 30초간 저장
			Token.setMaxAge( 60 * 60 * 10); 
			Token.setSecure(true);
			response.addCookie(Token);
			

			Cookie loginMode = new Cookie("loginMode", "true");
			// loginMode.setDomain("localhost");
			loginMode.setDomain("52.78.212.203");
			loginMode.setPath("/");
			loginMode.setMaxAge( 60 * 60 * 10);
			loginMode.setSecure(true);
			response.addCookie(loginMode);

			Cookie userEmail = new Cookie("userEmail", email);
			// userEmail.setDomain("localhost");
			userEmail.setDomain("52.78.212.203");
			userEmail.setPath("/");
			userEmail.setMaxAge( 60 * 60 * 10);
			userEmail.setSecure(true);
			response.addCookie(userEmail);

		
			// user_email 과 user_password 를 조회해서 이미 값이 있다면 tb_user테이블에는 넣을수 없게 한다. 대신 tb_login 에 기록을 남긴다.
			Map<String, Object> loginMap = commonService.selectMap("login.kakaoUserFind", paramMap);

			// logger.info("login Map param email : "+loginMap.get("userEmail"));
			if (!ObjectUtils.isEmpty(loginMap.get("userEmail"))) {
				logger.info("카카오로 이미 인증 하였습니다." + email);
				logger.info("userSeq : " + loginMap.get("userSeq"));
				commonService.update("login.kakaoUserUpdate", loginMap);
				
				Cookie userSeq = new Cookie("userSeq", (String) loginMap.get("userSeq")); // kakaoaccessToken
				// userSeq.setDomain("localhost");
				userSeq.setDomain("52.78.212.203");
				userSeq.setPath("/");
				userSeq.setMaxAge( 60 * 60 * 10);
				userSeq.setSecure(true);
				response.addCookie(userSeq);
				
				
				//logger.info("SHA256Encrypt userPassword:	" + (SHA256Encrypt.encrypt((String) loginMap.get("userPassword"))));

				CommonUtil.loginSession.put((String) loginMap.get("userSeq"), loginMap);
				// userPassword값을 지운다.
				loginMap.remove("userPassword");
				// 로그인 검증 이후 메모리 loginSession세팅 후 데이터와 암호화된 비밀번호인 accessToken 리턴 
				String accessToken = jwtTokenProvider.createToken((String) loginMap.get("userSeq"),
						(String) loginMap.get("userAuthor"));
				loginMap.put("accessToken", accessToken);
				logger.info("accessToken : " + accessToken);



				session.setAttribute("loginMember", loginMap.get("userSeq"));
				session.setAttribute("kakaoToken",accessToken);
				session.setMaxInactiveInterval(60 * 30);
				
				// 일반 login.api 에서는 res값으로 api값을 200코드로 login.api 받는데 바로 보내즌다.
				// 카카오api는 결과를 받고 이 결과를 /sch/huss/dashBoard/main.html 에 보내줘야한다.
				// forward는 서블릿(클래스)끼리 값 공유를 할 수 있다.

				Map<String, Object> loginbody = new HashMap();
				loginbody.put("send_token", accessToken);
			 
				// getRequestDispatcher에 보통 jsp를 주는데 html을 받을때는 rest api를 받을때는 api 값을 써야하는걸까?
				// W_ETXVpUBq_ABZ9WCApUK6DVNjVcDbWQ1Kx9hi29Nn1ZlsU88GcDdflaR8AKPXRpAAABjejHp1qUJG13ldIf8A
				// 로 똑같이 이동하는데 이번에는 페이지가 로그인페이지html표시되지 않고 메인으로 마지막에 redirect된다.
				  System.out.println("dispatcher forward전");
					
				 RequestDispatcher dispatcher =  request.getRequestDispatcher("/dashBoard/dispatcher.api");
				dispatcher.forward(request, response);
				// response.setStatus(HttpStatus.OK.value());
				// response.setContentType(MediaType.APPLICATION_JSON_VALUE);

				// new ObjectMapper().writeValue(response.getOutputStream(), loginbody);
				// request.setCharacterEncoding("UTF-8");
				// request.setAttribute("access_token", accessToken);

				// include에서 /auth/register.api 로 페이지 이동 ??
				// http://52.78.212.203:8189/auth/register.api?code=geHFzLHo7UhmJstZq28rDAd9TJoCRBylywqbLftbt8M2nfFQjLyGUfKYsVAKPXKXAAABjejBAwPRDLJpR7eCqA
				  // dispatcher.include(request, response);
				// logger.info(dispatcher);
				// logger.info("res : " +
				//  CommonResponse.statusResponse(HttpServletResponse.SC_OK, loginMap));
				// accesssToken을 준다. (일반 로그인) userSeq > acessToken 암호화
				// 리다이렉트 / forward
				// forward에서 getoutputStream 값과 dispathcer.api에서 getoutputStream() 값이 같다.
				// forward시 dispatcher.api로 진입한다. 그리고 모든 return이 끝날때는 메인으로 가지 않고
				// register.api?code={값}에서 끝났다.
				// 근데 getRequestDispathcer("../main.html")로 할때는 밑에까지 쭉 내려가서 string 값 redirect 가
				// 만나서 redirect로 메인에 갔다.

				// logger.info("카카오 response.getOutputStream() : " +response.getOutputStream());
				// logger.info("redirect전");
				// response.sendRedirect("/sch/huss/dashBoard/main.html");
				// getOutputStream 이나 getWriter 둘 중 하나만 사용 할 수 있는거 같다.
				// logger.info("response.getWriter() : "+response.getWriter());
			 
			} else {
				logger.info("카카오로 인증한 적 없는 이메일 입니다." + email);
				commonService.insert("login.kakaoUserRegist", paramMap);

				logger.info("paramMap : " + paramMap);
				
				logger.info("카카오 가입 후 로그인 시도 이메일 : " + email);
				session.setAttribute("loginMember", paramMap.get("email"));
				session.setAttribute("kakaoToken",paramMap.get("id"));
				session.setMaxInactiveInterval(60 * 30);
				  System.out.println("dispatcher forward전");	
				 RequestDispatcher dispatcher =  request.getRequestDispatcher("/dashBoard/dispatcher.api");
				 dispatcher.forward(request, response);
			}

		}
		// return CommonResponse.statusResponse(HttpServletResponse.SC_OK);
		return ;
	}

	// 카카오 로그아웃

	@PostMapping(value = "/kakaoLogout.api")
	public void kakaoLogout(HttpSession session, HttpServletRequest request, HttpServletResponse response)
			throws IOException {
	// 	kakaoService.kakaoLogout((String) session.getAttribute("access_token"));
	 	Cookie[] cookies = request.getCookies();
	  
		    			Cookie JSESSIONID = new Cookie("JSESSIONID", null);
		    			// JSESSIONID.setDomain("localhost");
		    			JSESSIONID.setDomain("52.78.212.203");
		    			JSESSIONID.setPath("/");
		    			JSESSIONID.setMaxAge(0);
		    			JSESSIONID.setSecure(true);
		    			response.addCookie(JSESSIONID);
		    			Cookie Token = new Cookie("accessToken", null);
		    			
						// Token.setDomain("localhost");
		    			Token.setDomain("52.78.212.203");
		    			Token.setPath("/");
		    			Token.setMaxAge(0);
		    			Token.setSecure(true);
		    			response.addCookie(Token);

		    			Cookie loginMode = new Cookie("loginMode", null);
		    			// loginMode.setDomain("localhost");
		    			loginMode.setDomain("52.78.212.203");
		    			loginMode.setPath("/");
		    			loginMode.setMaxAge(0);
		    			loginMode.setSecure(true);
		    			response.addCookie(loginMode);

		    			Cookie userEmail = new Cookie("userEmail", null);
		    			// userEmail.setDomain("localhost");
		    			userEmail.setDomain("52.78.212.203");
		    			userEmail.setPath("/");
		    			userEmail.setMaxAge(0);
		    			userEmail.setSecure(true);
		    			response.addCookie(userEmail);

		    			Cookie userSeq = new Cookie("userSeq", null);
		    			// userSeq.setDomain("localhost");
		    			userSeq.setDomain("52.78.212.203");
		    			userSeq.setPath("/");
		    			userSeq.setMaxAge(0);
		    			userSeq.setSecure(true);
		    			response.addCookie(userSeq);
		    			
		    			// session.invalidate(); 
		    	 
		       
		} 
		 
 

	private void expireCookie(HttpServletResponse response, String cookieName) {
		Cookie cookie = new Cookie(cookieName, null);
		cookie.setMaxAge(0);
		response.addCookie(cookie);
	}
}
