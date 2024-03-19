package com.sch.util;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sch.config.AopConfig;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LogFilter  implements Filter {
	Logger logger = LoggerFactory.getLogger(AopConfig.class);
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException{
		logger.info("---서블릿 필터 (doFilter) 호출 [By Request]---");
	String uuid = UUID.randomUUID().toString();
	try{
		logger.info("Request -> [{}][{}]", uuid, request.getDispatcherType());
		chain.doFilter(request,response);
	}catch(Exception e){
		throw e;
	}finally{
		logger.info("---서블릿 필터(do Filter)호출 [BY RESPONSE]----");
		logger.info("RESPONSE -> [{}}[{}}]", uuid, request.getDispatcherType());
		}
	}

}
