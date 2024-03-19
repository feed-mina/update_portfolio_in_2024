package com.sch.controller;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.WebRequest;

import com.google.api.client.util.Value;
import com.sch.config.AopConfig;

import io.jsonwebtoken.lang.Assert;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class CustomErrorController<JsonError> implements  ErrorController {

	@Value("${server.error.path:${error.path:/error}}")
	private String errorPath;
	
	private final ErrorAttributes errorAttributes;
	

	Logger logger = LoggerFactory.getLogger(AopConfig.class);

	// RequestDispatcher 상수로 정의되어 있음
	public static final String ERROR_EXCEPTION = "javax.servlet.error.exception";
	public static final String ERROR_EXCEPTION_TYPE = "javax.servlet.error.exeption_type";
	public static final String ERROR_MESSAGE = "javax.servlet.error.message";
	public static final String ERROR_REQUEST_URI = "javax.servlet.error.request_uri";
	public static final String ERROR_SERVLET_NAME = "javax.servlet.error.servlet_name";
	public static final String ERROR_STATUS_CODE = "javax.servlet.error.status_code";

	  

	private void printErrorInfo(HttpServletRequest request) {
		logger.info("ERROR_EXCEPTION: {}", request.getAttribute(ERROR_EXCEPTION));
		logger.info("ERROR_SERVLET_NAME: {}", request.getAttribute(ERROR_SERVLET_NAME));
		logger.info("ERROR_MESSAGE: {}", request.getAttribute(ERROR_MESSAGE));
		logger.info("ERROR_EXCEPTION_TYPE: {}", request.getAttribute(ERROR_EXCEPTION_TYPE));
		logger.info("ERROR_REQUEST_URI: {}", request.getAttribute(ERROR_REQUEST_URI));
		logger.info("ERROR_STATUS_CODE: {}", request.getAttribute(ERROR_STATUS_CODE));
		logger.info("dispatcherType={}", request.getDispatcherType());

	}

	@RequestMapping("/error404")
	public String error404(HttpServletResponse response, HttpServletRequest request) throws IOException {
		response.sendError(HttpServletResponse.SC_NOT_FOUND, "404");
		printErrorInfo(request);
		return "error-page/404";
	}

	@RequestMapping("/error500")
	public String error500(HttpServletResponse response, HttpServletRequest request) throws IOException {
		response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "500");
		printErrorInfo(request);
		return "error-page/500";
	}

	@RequestMapping("/error4xx")
	public String error4xx(HttpServletResponse response, HttpServletRequest request) throws IOException {
		response.sendError(HttpServletResponse.SC_BAD_REQUEST, "400");
		printErrorInfo(request);
		return "error-page/4xx";
	}

	@RequestMapping("/error5xx")
	public String error5xx(HttpServletResponse response, HttpServletRequest request) throws IOException {
		response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "502");
		printErrorInfo(request);
		return "error-page/5xx";
	}
 
	
	public CustomErrorController(ErrorAttributes errorAttributes) { 
		Assert.notNull(errorAttributes, "ErrorAsstributes must not be null");
		this.errorAttributes = errorAttributes;
	}

	
	@RequestMapping("${server.error.path:${error.path:/error}}")
    public ResponseEntity<JsonError> handleError(HttpServletRequest request) {
		return null;
    }
	
	public Map<String, Object> getErrorAttributes(WebRequest webRequest, boolean includeStackTrace){
		return this.errorAttributes.getErrorAttributes(webRequest, includeStackTrace);
	}
 

	@Override
	public String getErrorPath() {
		// TODO Auto-generated method stub
		return this.errorPath;
	}

}
