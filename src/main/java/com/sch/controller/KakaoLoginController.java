package com.sch.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
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
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sch.config.security.JwtTokenProvider;
import com.sch.encrypt.SHA256Encrypt;
import com.sch.service.CommonService;
import com.sch.service.KaKaoAPIService;
import com.sch.util.CamelHashMap;
import com.sch.util.CommonResponse;
import com.sch.util.CommonUtil;
import com.sch.util.JSONObject;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

@Api(tags = "Auth", description = "카카오로그인")
@RestController
@RequestMapping("/auth")
public class KakaoLoginController {
	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private JwtTokenProvider jwtTokenProvider;
	
	@Autowired
	CommonService commonService;

	@Autowired
	KaKaoAPIService kakaoService;

	public enum HttpMethodType { POST, GET, DELETE }

	private static final String API_SERVER_HOST  = "https://kapi.kakao.com";

	private static final String USER_SIGNUP_PATH = "/v1/user/signup"; // 연결하기
	private static final String USER_UNLINK_PATH = "/v1/user/unlink"; //연결끊기
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

	
	// code가 헤더값에 들어가야 한다. u8lnlrJoNmU2NLGgR5E3YfRkmY9KhvGXfM_j7AFJqCLWifOcRENskCzi3NwKPXWcAAABjYD2Oqv_A_o_BVb6-Q
	@GetMapping("/register.api")
	@ApiOperation(value = "카카오 가입/로그인할때 호출되는 api", notes = "http://localhost:8189/auth/kakao/callback?code={코드값}")
	@ApiImplicitParam(name = "code", required = true, dataTypeClass = String.class, value = "{\r\n" + //
			"\t\"code\": \"u8lnlrJoNmU2NLGgR5E3YfRkmY9KhvGXfM_j7AFJqCLWifOcRENskCzi3NwKPXWcAAABjYD2Oqv_A_o_BVb6-Q \"\r\n" + //
			"}", example = "{\r\n" + //
			"\t\"code\": \"u8lnlrJoNmU2NLGgR5E3YfRkmY9KhvGXfM_j7AFJqCLWifOcRENskCzi3NwKPXWcAAABjYD2Oqv_A_o_BVb6-Q \"\r\n" + //
			"}")
	public @ResponseBody ResponseEntity<?> kakaoCallback( String code, ServletRequest req, ServletResponse res, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException { // Data를 리턴해주는 함수 
	 
		System.out.println("카카오 인증 완료 code값"+code);
		// HttpHeaders 오브젝트 생성
		HttpHeaders headers = new HttpHeaders();
		HttpHeaders httpHeaders = new HttpHeaders();
		// Jackson ObjectMapper를 사용하여 JSON 파싱
		ObjectMapper objectMapper = new ObjectMapper();
		httpHeaders.add("Content-Type","application/x-www-form-urlencoded");
		MultiValueMap<String, String> kakaoParamMap = new LinkedMultiValueMap<>();

		kakaoParamMap.add("grant_type", "authorization_code");
	
		kakaoParamMap.add("client_id", "142c700061d20152ca151a489428cf6e"); // 테스트 앱 
		//  kakaoParamMap.add("redirect_uri", "http://localhost:8189/auth/register.api");
		kakaoParamMap.add("redirect_uri", "http://localhost:8189/auth/register.api"); 
		// 카카오 로그인에서 사용할 Logout Redirect URI 
		//http://localhost:8189/auth/kakao/terms.html 서비스약관
		// 약관 TAG service_20240212 영문약관제목 Terms of Service
		//http://localhost:8189/sch/huss/auth/kakao/sinkCallback 카카오싱크 플러그인이 서비스에 인가코드를 전달하기 위한 리다이렉트
		// http://localhost:8189/auth/kakao/callback
		kakaoParamMap.add("code", code);

		// HttpHeader와 HttpBody를 하나의 오브젝트에 담기
		HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(kakaoParamMap, httpHeaders);

		// Retrofut2 , okHttp, RestTemplate
		RestTemplate rt= new RestTemplate();
		// Http요청하기 - post방식으로 
		// RestTemplate.exchange("요청하는url",요청방식GET or POST, header+body값, response타입)
		ResponseEntity<String> registerRes = rt.exchange("https://kauth.kakao.com/oauth/token", HttpMethod.POST, kakaoTokenRequest, String.class);

		System.out.println("카카오 토큰 요청 완료 : 토큰요청에 대한 응답값"+registerRes.getBody());

		JsonNode jsonNodeToken = objectMapper.readTree(registerRes.getBody());
		// ObjectMapper를 생성할 때 JsonFactory 사용 예시
		ObjectMapper objectJsonMapper = new ObjectMapper(new JsonFactory());
		// JSON 값에 개별적으로 접근
		String kakaoaccessToken = jsonNodeToken.get("access_token").textValue();
		// Json 문자열을 객체로 변환
		//	MyObject myObject = objectMapper.readValue(jsonString, MyObject.class);
		System.out.println("accessToken값 : "+kakaoaccessToken);
		//System.out.println(request(USER_ME_PATH));
		/*response.getBody() 값은 아래와 같다. id_token과 scope는 openID 설정과 관련이 있다.
		 *
		 */
		// post방식으로 key-value 데이터를 요청(카카오쪽으로)

		JSONObject jSONObject = new JSONObject();
		RestTemplate rt2= new RestTemplate();
		// HttpHeaders2 오브젝트 생성
		HttpHeaders httpHeaders2 = new HttpHeaders();
		httpHeaders2.add("Content-Type","application/x-www-form-urlencoded");
		httpHeaders2.add("Authorization", "Bearer "+kakaoaccessToken);
		// HttpHeader와 HttpBody를 하나의 오브젝트에 담기
		HttpEntity<MultiValueMap<String, String>> kakaoProfileRequest = new HttpEntity<>(httpHeaders2); //오버로딩
		ResponseEntity<String> responseUserInfo = rt2.exchange("https://kapi.kakao.com/v2/user/me", HttpMethod.GET, kakaoProfileRequest, String.class);
		System.out.println("/v2/user/me 결과 :  "+responseUserInfo.getBody());

		JsonNode jsonNodeUserInfo = objectMapper.readTree(responseUserInfo.getBody());
		System.out.println("jsonNodeUserInfo : "+jsonNodeUserInfo);
		Number id = jsonNodeUserInfo.get("id").numberValue();
		System.out.println("id: "+jsonNodeUserInfo.get("id").numberValue()); // 카카오는 비밀번호를 안알려 주므로 고유값인 id를 비밀번호로 저장한다.
		System.out.println("connected_at: "+jsonNodeUserInfo.get("connected_at")); // 로그인한 날짜
		System.out.println("properties: "+jsonNodeUserInfo.get("properties"));

		// "properties" 필드에 해당하는 하위 JsonNode 가져오기
		JsonNode propertiesNode = jsonNodeUserInfo.get("properties");
		JsonNode kakaoAccountNode = jsonNodeUserInfo.get("kakao_account");
		System.out.println("kakao_account: "+jsonNodeUserInfo.get("kakao_account"));
		if (propertiesNode != null && propertiesNode.isObject()) {
			// "properties"의 하위 필드 중 "nickname"과 "profile_image" 값 가져오기
			String nickname = propertiesNode.get("nickname").asText();
			String profileImage = propertiesNode.get("profile_image").asText();

			// 추출된 값 출력 또는 활용
			System.out.println("Nickname: " + nickname);
			System.out.println("Profile Image: " + profileImage);
		} else {
			// "properties"가 없거나 객체가 아닌 경우 처리 로직 추가
			System.out.println("가입이나 로그인을 할 수 없습니다. Invalid JSON properties structure");
		}
		if(kakaoAccountNode != null && kakaoAccountNode.isObject()){
			// kakaoAccountNode값은 카카오 개발자 도구에서  앱 권한 신청으로 권한을 받아야 사용할 수 있다.
			String email = kakaoAccountNode.get("email").asText();
			String phone_number = kakaoAccountNode.get("phone_number").asText();
			String birthday = kakaoAccountNode.get("birthday").asText();
			String gender = kakaoAccountNode.get("gender").asText();
			Boolean agreementEmail = kakaoAccountNode.get("email_needs_agreement").asBoolean();
			Boolean agreementBirthday = kakaoAccountNode.get("birthday_needs_agreement").asBoolean();
			Boolean agreementGender = kakaoAccountNode.get("gender_needs_agreement").asBoolean();
			String name = kakaoAccountNode.get("name").asText(); 
			Map<String, Object> paramMap = new HashMap<String, Object>();
			Map<String, Object> userMap = new HashMap<String, Object>();
			paramMap.put("email", email);
			paramMap.put("name", name);
			paramMap.put("id", id);
			paramMap.put("phone_number", phone_number);
			 userMap.put("name", name);
			userMap.put("id", id);
			userMap.put("phone_number", phone_number);
			 paramMap.put("user", userMap);
// user_email 과 user_password 를 조회해서 이미 값이 있다면 tb_user테이블에는 넣을수 없게 한다. 대신 tb_login 에 기록을 남긴다.
			Map<String, Object> loginMap = commonService.selectMap("login.kakaoUserFind", paramMap);

			// 	System.out.println("login Map param email : "+loginMap.get("userEmail"));
			if (!ObjectUtils.isEmpty(loginMap.get("userEmail"))) {
				System.out.println("카카오로 이미 인증 하였습니다."+email);
				System.out.println("userSeq : "+loginMap.get("userSeq"));

				System.out.println("SHA256Encrypt userPassword:	"+(SHA256Encrypt.encrypt((String) loginMap.get("userPassword"))));

				CommonUtil.loginSession.put((String) loginMap.get("userSeq"), loginMap);
				// userPassword값을 지운다.
				loginMap.remove("userPassword");
				// 로그인 검증 이후 메모리 loginSession세팅 후 데이터와 accessToken 리턴
				String accessToken = jwtTokenProvider.createToken((String) loginMap.get("userSeq"),
						(String) loginMap.get("userAuthor"));
				loginMap.put("accessToken", accessToken);
				System.out.println("accessToken : " + accessToken);

				// 일반 login.api 에서는 res값으로 api값을 200코드로 login.api 받는데 바로 보내즌다.
				// 카카오api는 결과를 받고 이 결과를 /sch/huss/dashBoard/main.html 에 보내줘야한다.
				// forward는 서블릿(클래스)끼리 값 공유를 할 수 있다. 
				
				Map<String, Object> loginbody = new HashMap();
				loginbody.put("send_token", accessToken);
				
				// response.setStatus(HttpStatus.OK.value());
				//response.setContentType(MediaType.APPLICATION_JSON_VALUE);
				
				// new ObjectMapper().writeValue(response.getOutputStream(), loginbody);
				request.setCharacterEncoding("UTF-8");
				request.setAttribute("access_token", accessToken);
				

				System.out.println(loginbody);
				// getRequestDispatcher에 보통 jsp를 주는데 html을 받을때는 rest api를 받을때는 api 값을 써야하는걸까?
			RequestDispatcher dispatcher = request.getRequestDispatcher("/dashBoard/dispatcher.api");
			// W_ETXVpUBq_ABZ9WCApUK6DVNjVcDbWQ1Kx9hi29Nn1ZlsU88GcDdflaR8AKPXRpAAABjejHp1qUJG13ldIf8A 로 똑같이 이동하는데 이번에는 페이지가 로그인페이지html표시되지 않고 메인으로 마지막에 redirect된다. 
			System.out.println("dispatcher forward전");
			
			dispatcher.forward(request, response); 
			// include에서 /auth/register.api 로 페이지 이동 ??  http://localhost:8189/auth/register.api?code=geHFzLHo7UhmJstZq28rDAd9TJoCRBylywqbLftbt8M2nfFQjLyGUfKYsVAKPXKXAAABjejBAwPRDLJpR7eCqA
			//	System.out.println("dispatcher include전");
			//	dispatcher.include(request, response);	
			//	System.out.println(dispatcher);
			// 	System.out.println("res : " + CommonResponse.statusResponse(HttpServletResponse.SC_OK, loginMap));
				// accesssToken을 준다. (일반 로그인) userSeq > acessToken 암호화 
				// 리다이렉트 		/ forward
			// forward에서 getoutputStream 값과 dispathcer.api에서 getoutputStream() 값이 같다.
			// forward시 dispatcher.api로 진입한다. 그리고 모든 return이 끝날때는 메인으로 가지 않고 register.api?code={값}에서 끝났다.
			// 근데 getRequestDispathcer("../main.html")로 할때는 밑에까지 쭉 내려가서 string 값 redirect 가 만나서 redirect로 메인에 갔다.
			
			//	System.out.println("카카오 response.getOutputStream() : " +response.getOutputStream());
				// System.out.println("redirect전");
			//	response.sendRedirect("/sch/huss/dashBoard/main.html");
				// getOutputStream 이나 getWriter 둘 중 하나만 사용 할 수 있는거 같다. 
				//System.out.println("response.getWriter() : "+response.getWriter());
			//	return ;
			}else{
				System.out.println("카카오로 인증한 적 없는 이메일 입니다."+email);
				commonService.insert("login.kakaoUserRegist", paramMap);
				if(!ObjectUtils.isEmpty(loginMap.get("userEmail"))) {
					System.out.println("카카오 가입 후 로그인 시도 이메일 : "+email);
					System.out.println("userSeq : "+loginMap.get("userSeq"));
					// accesssToken을 준다. (일반 로그인)
					// 리다이렉트 
				}
			}
			// 	System.out.println("메인으로 이동 바로 전");
			// 	System.out.println("loginMap : "+loginMap);
			// 	// 여기서도 reedirect가 되지 않는다. 위에서 forward로 response를 보내줫기때문에
			
		}
		// 밑에 redirect되지 않는다.
	//	return ;
		return  responseUserInfo; 
	}
 
	// 서비스와 컨트롤러 분리 
	@GetMapping(value="/kakaoTest1.api")
	@ApiOperation(value = "카카오 로그인할때 호출되는 api", notes = "http://localhost:8189/auth/kakao/callback?code={코드값}")
	public ResponseEntity<?> kakaoTest1(String code, HttpServletRequest request, HttpServletResponse response) throws IOException {

		HttpSession session = request.getSession();
		String access_token = kakaoService.getKakaoCallback(code);
		String getUser = kakaoService.getUser(access_token);
		HashMap<String, Object> userInfo = kakaoService.getUserInfo(access_token);
		Map<String, Object> sessionMap = new CamelHashMap();
		Map<String, Object> tmpMap = new CamelHashMap();
// 클라이언트의 이메일이 존재할때 세션에 해당 이메일과 토큰 등록
		if(userInfo.get("email") != null) {
			session.setAttribute("userId", userInfo.get("email"));
			session.setAttribute("access_token", access_token);
			session.setAttribute("getUser", getUser);
			//System.out.println(getUser);
			System.out.println(tmpMap);
			String userEmail = (String) session.getAttribute("userId");
			String userToken = (String) session.getAttribute("access_token");
			String user = (String) session.getAttribute("getUser");
			System.out.println("JSONObject.escape(access_token) : " + JSONObject.escape(access_token));

			tmpMap.put("email",userEmail);
			Map<String, Object> loginMap = commonService.selectMap("login.kakaoUserFind", tmpMap);
			System.out.println("loginMap : "+loginMap);
			session.setAttribute("userSeq", loginMap.get("userSeq"));
			sessionMap.put("user", user);
			sessionMap.put("userEmail", userEmail);
			sessionMap.put("accessToken", userToken);
			sessionMap.put("userSeq", loginMap.get("userSeq"));
			sessionMap.put("userPassword", loginMap.get("userPassword"));
			JSONObject jSONObject = new JSONObject();
			ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder
					.currentRequestAttributes();
			// null 이외의 값 즉 member 객체라면 로그인 성공 처리
			// 쿠키에 시간 정보를 주지 않았기 때문에 세션 쿠키로 인식됨 -> 브라우저 종료시 모두 종료


			Cookie Token = new Cookie("accessToken",userToken);
			Cookie Email = new Cookie("userEmail",userEmail);
			//Cookie userNm = new Cookie("userNm",);
			Cookie userSeq = new Cookie("userSeq",(String) loginMap.get("userSeq"));
			Cookie loginMode = new Cookie("loginMode","true");
			System.out.println("Token : "+Token.getValue()); 
//http://localhost/
			Token.setDomain("localhost");
			// Token.setDomain("localhost");
			Token.setPath("/");
			// 30초간 저장
			Token.setMaxAge(30*60*60);
			Token.setSecure(true);
			response.addCookie(Token);
			System.out.println("쿠키 정보 전달 완료 : "+ Token);



			Email.setDomain("211.37.148.144");
			// Email.setDomain("localhost");
			Email.setPath("/");
			// 30초간 저장
			Email.setMaxAge( 60 * 60 * 10);
			Email.setSecure(true);
			response.addCookie(Email);
			System.out.println("쿠키 정보 전달 완료 : "+ Email);


			loginMode.setDomain("211.37.148.144");
			// loginMode.setDomain("localhost");
			loginMode.setPath("/");
			// 30초간 저장
			loginMode.setMaxAge(60 * 60 * 10);
			loginMode.setSecure(true);
			response.addCookie(loginMode);
			System.out.println("쿠키 정보 전달 완료 : "+ loginMode);


			userSeq.setDomain("localhost");
			// userSeq.setDomain("localhost");
			userSeq.setPath("/");
			// 30초간 저장
			userSeq.setMaxAge(30*60);
			userSeq.setSecure(true);
			response.addCookie(userSeq);
			System.out.println("쿠키 정보 전달 완료 : "+ userSeq);

			System.out.println("response : "+response);
			if(user != null) {
				response.setStatus(HttpServletResponse.SC_OK);
				response.setContentType("application/json");
				response.setCharacterEncoding("UTF-8");
				ResponseEntity resEntity = CommonResponse.statusResponse(HttpServletResponse.SC_OK,sessionMap);
				System.out.println("response : "+response);

				//	System.out.println("resEntity : "+resEntity);
				//	System.out.println("sessionMap : "+sessionMap);
				jSONObject.putAll((Map) resEntity.getBody());
				//	System.out.println("jSONObject : "+jSONObject);
				System.out.println("request.getCookies()1 : "+request.getCookies());

			//	response.sendRedirect(request.getContextPath() + "/sch/huss/dashBoard/main.html");
				request.getCookies();

				//forward 나 sendRedirect로 세션이나 쿠키 파라미터를 main.html에 보내기
				// main.api에서 파라미터를 받거나, 받은 파라미터를 로컬스토리지에 저장하기

				//	request.getCookies();

			}

		}
		return CommonResponse.statusResponse(HttpServletResponse.SC_OK, sessionMap);
	}
	// 카카오 로그아웃

	@PostMapping(value="/kakaoLogout.api")
	public String kakaoLogout(HttpSession session,  HttpServletRequest request, HttpServletResponse response) throws IOException {
		kakaoService.kakaoLogout((String) session.getAttribute("access_token"));
		session.removeAttribute("access_token");
		session.removeAttribute("userId");
		session.removeAttribute("getUser");
		session.removeAttribute("userSeq");

		Cookie Token = new Cookie("accessToken",null);
		Token.setDomain("localhost");
		// Token.setDomain("localhost");
		Token.setPath("/");
		Token.setMaxAge(0);
		Token.setSecure(true);
		response.addCookie(Token);

		Cookie loginMode = new Cookie("loginMode",null);
		loginMode.setDomain("localhost");
		// loginMode.setDomain("localhost");
		loginMode.setPath("/");
		loginMode.setMaxAge(0);
		loginMode.setSecure(true);
		response.addCookie(loginMode);

		Cookie userEmail = new Cookie("userEmail",null);
		userEmail.setDomain("localhost");
		// userEmail.setDomain("localhost");
		userEmail.setPath("/");
		userEmail.setMaxAge(0);
		userEmail.setSecure(true);
		response.addCookie(userEmail);

		Cookie userSeq = new Cookie("userSeq",null);
		userSeq.setDomain("localhost");
		// userSeq.setDomain("localhost");
		userSeq.setPath("/");
		userSeq.setMaxAge(0);
		userSeq.setSecure(true);
		response.addCookie(userSeq);

		if (session != null) session.invalidate();
		expireCookie(response, "accessToken");
		expireCookie(response, "loginMode");
		expireCookie(response, "userEmail");
		expireCookie(response, "userSeq");

		response.sendRedirect(request.getContextPath() + "/sch/huss/dashBoard/main.html");
		return "로그아웃완료";
	}
	private void expireCookie(HttpServletResponse response, String cookieName) {
		Cookie cookie = new Cookie(cookieName, null);
		cookie.setMaxAge(0);
		response.addCookie(cookie);
	}
}

