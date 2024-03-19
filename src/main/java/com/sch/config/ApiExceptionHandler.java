package com.sch.config;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.sch.util.CommonResponse;

@ControllerAdvice
public class ApiExceptionHandler {

	private Logger log = LoggerFactory.getLogger(this.getClass());

 /*   @ExceptionHandler(value = {Exception.class})
	public void handleUserNotExistException(Exception ex) throws IOException, ServletException {

		// video 스트리밍시 로딩바 드래그하면 이 에러가 나온다
		if ((ex instanceof ClientAbortException)) {
			return;
		}
		log.error(null, ex);
        CommonResponse.responseWriter(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.getMessage());
        return;
    }
 */
    @ExceptionHandler(AuthenticationException.class)
	public void handleAuthenticationException(AuthenticationException ex) throws ServletException, IOException {
		CommonResponse.responseWriter(HttpServletResponse.SC_UNAUTHORIZED, ex.getMessage());
		return;
	}
	
 
}
