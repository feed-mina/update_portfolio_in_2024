package com.sch.util;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.sch.config.AopConfig;

public class LogInterceptor implements HandlerInterceptor {
	Logger logger = LoggerFactory.getLogger(AopConfig.class);

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		logger.info("---스프링 인터셉터(preHandle) 호출 ---");
		String uuid = UUID.randomUUID().toString();
		logger.info("Request -> [{}][{}]", uuid, request.getDispatcherType());
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		logger.info("---스프링 인터셉터(postHandle) 호출 ---");
		String uuid = UUID.randomUUID().toString();
		logger.info("RESPONSE -> [{}}[{}}]", uuid, request.getDispatcherType());
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		logger.info("---스프링 인터셉터(afterCompletion) 호출 ---");
		if (ex == null) {
			logger.info("컨트롤러에서 예외발생x!!");
		} else {
			logger.info("컨트롤러에서 예외발생", ex);
		}
	}
}
