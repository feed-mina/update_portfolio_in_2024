package com.sch.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class KaKaoAPIService {

	@Autowired
	CommonService commonService;

	private static final String KAKAO_OAUTH_PATH = "https://kauth.kakao.com/oauth/authorize";
	private static final String USER_ME_PATH = "https://kapi.kakao.com/v2/user/me"; // 사용자 정보 가져오기
	private static final String USER_SIGNUP_PATH = "/v1/user/signup"; // 연결하기
	private static final String USER_UNLINK_PATH = "/v1/user/unlink"; // 연결끊기
	private static final String USER_LOGOUT_PATH = "https://kapi.kakao.com/v1/user/logout"; // 로그아웃
	private static final String USER_UPDATE_PROFILE_PATH = "/v1/user/update_profile"; // 사용자 정보 저장하기
	private static final String USER_IDS_PATH = "/v1/user/ids"; // 사용자 목록 가져오기
	private static final String USER_TOKENS_PATH = "/v1/user/access_token_info"; // 토큰 정보 보기

	private static final String USER_POLICY = "/v2/user/scopes"; // 동의 내역 확인하기
	private static final String SERVICE_TERMS = "/v2/user/service_terms"; // 서비스 약관 동의 내역 확인

	@Value("${rest-api-key}")
	private String REST_API_KEY;

	@Value("${redirect-uri}")
	private String REDIRECT_URI;

	@Value("${authorize-uri}")
	private String AUTHORIZE_URI;

	@Value("${token-uri}")
	public String TOKEN_URI;

	@Value("${client-secret}")
	private String CLIENT_SECRET;

	@Value("${kakao-api-host}")
	private String KAKAO_API_HOST;

 
	public String getKakaoCallback(String code) throws IOException {
 
		// HttpHeaders 오브젝트 생성
		HttpHeaders headers = new HttpHeaders();
		HttpHeaders httpHeaders = new HttpHeaders();
		// Jackson ObjectMapper를 사용하여 JSON 파싱
		ObjectMapper objectMapper = new ObjectMapper();
		httpHeaders.add("Content-Type", "application/x-www-form-urlencoded");
		MultiValueMap<String, String> kakaoParamMap = new LinkedMultiValueMap<>();

		kakaoParamMap.add("grant_type", "authorization_code");
		// kakaoParamMap.add("client_id", "eb95cf99e5aa0fda9503becaf235c123"); // 앱
		kakaoParamMap.add("client_id", "142c700061d20152ca151a489428cf6e"); // 테스트 앱
		kakaoParamMap.add("redirect_uri", "http://localhost:8189/auth/kakaoTest1.api");
		// kakaoParamMap.add("redirect_uri",
		// "http://localhost:8189/auth/login-callback");
		// kakaoParamMap.add("redirect_uri",
		// "http://localhost:8189/auth/kakaoTest1.api");

		kakaoParamMap.add("code", code);

		// HttpHeader와 HttpBody를 하나의 오브젝트에 담기
		HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(kakaoParamMap, httpHeaders);

		// Retrofut2 , okHttp, RestTemplate
		RestTemplate rt = new RestTemplate();
		// Http요청하기 - post방식으로
		// RestTemplate.exchange("요청하는url",요청방식GET or POST, header+body값, response타입)
		ResponseEntity<String> response = rt.exchange("https://kauth.kakao.com/oauth/token", HttpMethod.POST,
				kakaoTokenRequest, String.class);

		System.out.println("카카오 토큰 요청 완료 : 토큰요청에 대한 응답값" + response.getBody());

		JsonNode jsonNodeToken = objectMapper.readTree(response.getBody());
		// ObjectMapper를 생성할 때 JsonFactory 사용 예시
		ObjectMapper objectJsonMapper = new ObjectMapper(new JsonFactory());

		// JSON 값에 개별적으로 접근
		String accessToken = jsonNodeToken.get("access_token").textValue();
		// Json 문자열을 객체로 변환
		// MyObject myObject = objectMapper.readValue(jsonString, MyObject.class);
		System.out.println("accessToken값 : " + accessToken); 
		return accessToken;
	}

	public String getUser(String accessToken) throws IOException {

		// post방식으로 key-value 데이터를 요청(카카오쪽으로)
		// accessToken = getKakaoCallback(accessToken);
		RestTemplate rt2 = new RestTemplate();
		ObjectMapper objectMapper = new ObjectMapper();
		// HttpHeaders2 오브젝트 생성
		HttpHeaders httpHeaders2 = new HttpHeaders();
		httpHeaders2.add("Content-Type", "application/x-www-form-urlencoded");
		httpHeaders2.add("Authorization", "Bearer " + accessToken);
		// HttpHeader와 HttpBody를 하나의 오브젝트에 담기
		HttpEntity<MultiValueMap<String, String>> kakaoProfileRequest = new HttpEntity<>(httpHeaders2); // 오버로딩
		ResponseEntity<String> responseUserInfo = rt2.exchange("https://kapi.kakao.com/v2/user/me", HttpMethod.GET,
				kakaoProfileRequest, String.class);
		System.out.println("/v2/user/me 결과 :  " + responseUserInfo.getBody());
		JsonNode jsonNodeUserInfo = objectMapper.readTree(responseUserInfo.getBody());
		System.out.println("jsonNodeUserInfo : " + jsonNodeUserInfo);
		Number id = jsonNodeUserInfo.get("id").numberValue();
		System.out.println("id: " + jsonNodeUserInfo.get("id").numberValue()); // 카카오는 비밀번호를 안알려 주므로 고유값인 id를 비밀번호로 저장한다.
		System.out.println("connected_at: " + jsonNodeUserInfo.get("connected_at")); // 로그인한 날짜
		System.out.println("properties: " + jsonNodeUserInfo.get("properties"));

		// "properties" 필드에 해당하는 하위 JsonNode 가져오기
		JsonNode propertiesNode = jsonNodeUserInfo.get("properties");
		JsonNode kakaoAccountNode = jsonNodeUserInfo.get("kakao_account");
		System.out.println("kakao_account: " + jsonNodeUserInfo.get("kakao_account"));
		if (propertiesNode != null && propertiesNode.isObject()) {
			// "properties"의 하위 필드 중 "nickname"과 "profile_image" 값 가져오기
			String nickname = propertiesNode.get("nickname").asText();
			String profileImage = propertiesNode.get("profile_image").asText();
  
		} else {
			// "properties"가 없거나 객체가 아닌 경우 처리 로직 추가
			System.out.println("Invalid JSON properties structure");
		}

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
			 
			Map<String, Object> paramMap = new HashMap<String, Object>();
			Map<String, Object> userMap = new HashMap<String, Object>();
			// paramMap.put("userSeq", userSeq);
			paramMap.put("email", email);
			paramMap.put("name", name);
			paramMap.put("id", id);
			paramMap.put("phone_number", phone_number);
			// userMap.put("userSeq", userSeq);
			userMap.put("name", name);
			userMap.put("id", id);
			userMap.put("phone_number", phone_number);
			paramMap.put("user", userMap);

			// user_email 과 user_password 를 조회해서 이미 값이 있다면 tb_user테이블에는 넣을수 없게 한다. 대신
			// tb_login 에 기록을 남긴다.
			Map<String, Object> loginMap = commonService.selectMap("login.kakaoUserFind", paramMap);

			System.out.println("login Map param email : " + loginMap.get("userEmail"));
			if (!ObjectUtils.isEmpty(loginMap.get("userEmail"))) {
				System.out.println("카카오로 이미 인증 하였습니다." + email);
			} else {
				System.out.println("카카오로 인증한 적 없는 이메일 입니다." + email);
				commonService.insert("login.kakaoUserRegist", paramMap);
			}

		}
		return jsonNodeUserInfo.toString();

	}

	public HashMap<String, Object> getUserInfo(String access_token) {

		HashMap<String, Object> userInfo = new HashMap<>();

		try {
			URL url = new URL(USER_ME_PATH);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");

			// 요청에 필요한 header애 포함될 내용
			conn.setRequestProperty("Authorization", "Bearer " + access_token);
			int responseCode = conn.getResponseCode();
			// System.out.println("responseCode : "+responseCode);

			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

			String line = "";
			String result = "";

			while ((line = br.readLine()) != null) {
				result += line;
			}
			// System.out.println("response body : "+result);

			JsonParser parser = new JsonParser();
			JsonElement element = parser.parse(result);

			JsonObject properties = element.getAsJsonObject().get("properties").getAsJsonObject();
			JsonObject kakao_account = element.getAsJsonObject().get("kakao_account").getAsJsonObject();

			String nickname = properties.getAsJsonObject().get("nickname").getAsString();
			String email = kakao_account.getAsJsonObject().get("email").getAsString();
			userInfo.put("nickname", nickname); // map 형태
			userInfo.put("email", email); // map 형태
		} catch (IOException e) {
			e.printStackTrace();
		}
		return userInfo;
	}

	public void kakaoLogout(String access_token) {
		try {
			URL url = new URL(USER_LOGOUT_PATH);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");

			// 요청에 필요한 header애 포함될 내용
			conn.setRequestProperty("Authorization", "Bearer " + access_token);

			// 결과 코드가 200이라면 성공
			int responseCode = conn.getResponseCode();
			System.out.println("responseCode : " + responseCode);

			// 요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line = "";
			String result = "";
			while ((line = br.readLine()) != null) {
				result += line;
			}
			System.out.println(result);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
