package com.sch.util;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

@Component
public class JwtAccessDeniedHandler {

	public void handle(HttpServletRequest request, HttpServletResponse response,  AccessDeniedException accessDeniedException)throws IOException {
		// 필요한 권한이 없이 접근할고 할때 403
		response.sendError(response.SC_FORBIDDEN);
	}
}
