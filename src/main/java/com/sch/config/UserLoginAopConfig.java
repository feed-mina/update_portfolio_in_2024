package com.sch.config;

import com.sch.config.security.JwtTokenProvider;
import com.sch.service.CommonService;
import com.sch.util.CommonUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Aspect
@Component
public class UserLoginAopConfig {
	Logger logger = LoggerFactory.getLogger(UserLoginAopConfig.class);

	@Autowired
	CommonService commonService;

	@Autowired
	JwtTokenProvider jwtTokenProvider;

	//@After("execution(* com.sch..controller.*Controller.*(..))")
	public void userLogin(JoinPoint join){
		ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

		String accessToken = jwtTokenProvider.resolveToken(request);
		// System.out.println(accessToken);

		// X-AUTH-TOKEN 있으면 tb_user_login에 endDt 업데이트
		if(CommonUtil.isNotEmpty(accessToken)){
			try{
				String userSeq = jwtTokenProvider.getUserPk(accessToken);
				// System.out.println(userSeq);

				Map<String, Object> paramMap = new HashMap<String, Object>();
				Map<String, Object> userMap = new HashMap<String, Object>();
				userMap.put("userSeq", userSeq);
				paramMap.put("user", userMap);
				
				commonService.insert("login.updateLoginDt", paramMap);
			}catch(Exception e){
				logger.info( accessToken + "토큰에 문제가있어 체류시간 남기지못함 >> " + e.getMessage());
			}
		}
	}

}
