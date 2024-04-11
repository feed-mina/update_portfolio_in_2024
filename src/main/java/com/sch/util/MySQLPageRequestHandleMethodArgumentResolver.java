package com.sch.util;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import lombok.extern.slf4j.Slf4j;

// Mysql 쿼리 페이징 Limit, Offset 값을 자동 계싼하여 mysqlpageREquest 클래스 담아서  컨트롤러에서 받을 수 있게함.

@Slf4j
public class MySQLPageRequestHandleMethodArgumentResolver  implements HandlerMethodArgumentResolver{
    
    private static final String DEFAULT_PARAMETER_PAGE="page";
    private static final String DEFAULT_PARAMETER_SIZE="size";
    private static final int DEFAULT_SIZE =20;
     
     
 
    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {     
        HttpServletRequest request=(HttpServletRequest) webRequest.getNativeRequest();
         
        //현재 페이지
        int page =NumberUtils.toInt(request.getParameter(DEFAULT_PARAMETER_PAGE));
         
        //리스트 갯수
        int offset =NumberUtils.toInt(request.getParameter(DEFAULT_PARAMETER_SIZE), DEFAULT_SIZE);
         
        //시작지점
        int limit=(offset * page) - offset;    
         
        return new MySQLPageRequest();
    }
 
     
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return MySQLPageRequest.class.isAssignableFrom(parameter.getParameterType());
    }
     
     
}
