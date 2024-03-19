package com.sch.config.security;

import com.sch.util.CommonResponse;
import com.sch.util.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException exception)
			throws IOException {
		response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");

		ResponseEntity<?> resEntity = CommonResponse.statusResponse(HttpServletResponse.SC_FORBIDDEN);

		JSONObject jSONObject = new JSONObject();
		jSONObject.putAll((Map) resEntity.getBody());
		response.getWriter().write(jSONObject.toString());
	}
}