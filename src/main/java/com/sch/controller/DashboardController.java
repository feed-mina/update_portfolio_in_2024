package com.sch.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.rowset.serial.SerialException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sch.service.CommonService;
import com.sch.service.UserManageService;
import com.sch.util.CommonResponse;
import com.sch.util.JSONObject;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(tags = "03 DashBoard", description = "메인 & 대쉬보드")
@RestController
@RequestMapping("/dashBoard")
public class DashboardController { 
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private final UserManageService userManageService = new UserManageService();

	@Autowired
	CommonService commonService;
	
	@GetMapping(value="/main.api")
	@ApiOperation(value = "", notes = "")
	public String loginCheck(HttpServletResponse response, HttpServletRequest request, HttpSession session) {
		String accessToken = (String)session.getAttribute("access_token");
		logger.info("accessToken"+ accessToken);
		
		//logger.info("request.getCookies()"+request.getCookies());
		//Cookie[] cookies = request.getCookies();// 저장된 쿠키들을 담기 위한 배열
		/*
if(cookies != null) {
	String accessToken = request.getParameter("accessToken");
	logger.info("userToken: " + accessToken);
	String userToken = request.getParameter("userToken");
	logger.info("userToken: " + userToken);
	String loginMode = request.getParameter("loginMode");
	logger.info("loginMode: " + loginMode);
	Cookie accessTokenCookie = new Cookie("accessToken",accessToken);
	response.addCookie(accessTokenCookie);
	Cookie userTokenCookie = new Cookie("userToken",userToken);
	response.addCookie(userTokenCookie);
	Cookie cookie = null;
	for(Cookie c : cookies) {
		logger.info(" getName: "+ c.getName());

		logger.info(" getValue: "+ c.getValue());
		
		if(c.getName().equals("access_token")) {
			cookie = c;
		logger.info("c :"+c);
		}
	}
	//CommonResponse.statusResponse(HttpServletResponse.SC_OK);
	// 쿠키 값을 추가한다. response.addCookie(cookie);
	// 쿠키 생명 시간 설정 (1시간) cookie.setMaxAge(60 * 60);
	// view 페이지로 응답 response.sendRedirect("");
}
if(cookies == null) {
	logger.info("cookie is null");
}
*/
		
		return accessToken;
	}
	
	
	@GetMapping(value="/dispatcher.api")
	@ApiOperation(value = "", notes = "")
	protected void service(HttpServletResponse response, HttpServletRequest request) throws SerialException, IOException, ServletException{
		HttpSession session = request.getSession();
		logger.info("/dashboard/dispathcer.api 진입"); 
request.getAttribute("access_token");

// request.getAttribute("access_token" 값은 다음처럼 생김 {send_token=eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJVU0VSXzAwMDAwMDgyIiwiaWF0IjoxNzA5MDE4MjgyLCJleHAiOjE3MDkwNTQyODJ9.0x8mh2XzA0B7WGmWTuhI9Tv4JrvKmOZvS7v00XXNy-E}
 // 따라서 LinkedHashMap 값이 String으로 강제 형변환이 되지 않는다는 오류가 뜸 그래서 loginbody 값에서 accesstoken값으로 바꿔줌 string일까?ㄴ
logger.info("req access_token : " + request.getAttribute("access_token"));
//JSONObject.escape((String) request.getAttribute("access_token"));

		response.setContentType("application/json");
		//	response.getWriter();
		 response.getOutputStream();
		 //	response.resetBuffer();
		logger.info("메인 res getOutputStream : " + response.getOutputStream());
	 logger.info("forward 전");
	 // sendRedirect 하면 값이 메인에서 다시 get 불러올때 사라진다. res로 메인html을 가져온다. 
	 //	response.sendRedirect("/sch/huss/dashBoard/main.html");
	// include하라니까 html 을 string으로 전체를 가져오네..
		 // 안되면 여기서 쿠키 값을 저장하기 
	 ObjectMapper mapper = new ObjectMapper();
//	 String parse = mapper.convertValue(request.getAttribute("access_token"), String.class);
//	logger.info(parse);
	 String userToken =  (String) request.getAttribute("access_token");
		 Cookie Token = new Cookie("accessToken",userToken);

	 	Token.setDomain("52.78.212.203");
			// Token.setDomain("52.78.212.203");
	 Token.setPath("/");
			// 30초간 저장
	 	Token.setMaxAge(30*60*60);
		Token.setSecure(true);
	 	response.addCookie(Token);
	 	logger.info("쿠키 정보 전달 완료 : "+ Token);

			JSONObject jSONObject = new JSONObject();
			if(Token != null) {
				response.setStatus(HttpServletResponse.SC_OK);
			response.setContentType("application/json");
					response.setCharacterEncoding("UTF-8");

			ResponseEntity resEntity = CommonResponse.statusResponse(HttpServletResponse.SC_OK,(List<Map<String, Object>>) Token);
			logger.info("response : "+response);
					jSONObject.putAll((Map) resEntity.getBody());
					logger.info("request.getCookies()1 : "+request.getCookies());
			request.getCookies();
				}
			  RequestDispatcher dispatcher = request.getRequestDispatcher("/sch/huss/dashBoard/main.html");
				dispatcher.forward(request, response);	
				 logger.info("forward 후 메인으로 이동");
				 
				
	}
  
}
