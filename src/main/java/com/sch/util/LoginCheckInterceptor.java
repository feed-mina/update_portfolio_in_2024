package com.sch.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.servlet.HandlerInterceptor;

import com.sch.config.security.JwtTokenProvider;

public class LoginCheckInterceptor implements HandlerInterceptor {

	@Autowired
	UserDetailsService userDetailsService;
	
	@Autowired
	JwtTokenProvider jwtTokenProvider;
    public boolean preHandle(HttpServletRequest request, String token, HttpServletResponse response, Object handler) throws Exception {
    	// 로그인이 되어있지 않는 유저가 url검색으로 들어오려고 하면 login페이지로 이동시키기
    	String accessToken = jwtTokenProvider.resolveToken(request);
        // 1. 세션에서 회원 정보 조회
        HttpSession session = request.getSession();
      //  MemberResponse member = (MemberResponse) session.getAttribute("loginMember");
        // 2. 회원 정보 체크
        if (CommonUtil.isNotEmpty(accessToken)) {
            response.sendRedirect("/sch/huss/login/login.html");
            return false;
        }

        return HandlerInterceptor.super.preHandle(request, response, handler);
    }
}
