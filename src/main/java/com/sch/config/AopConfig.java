package com.sch.config;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Aspect
@Component
public class AopConfig {
	Logger logger = LoggerFactory.getLogger(AopConfig.class);

	@Around("execution(* com.sch..controller.*Controller.*(..))")
	public Object logging(ProceedingJoinPoint pjp) throws Throwable {
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();

		logger.info("▒▒▒▒▒▒▒ [  start  ]- " + pjp.getSignature().getDeclaringTypeName() + " / "
				+ pjp.getSignature().getName() + " ▒▒▒▒▒▒▒");
		Object result = pjp.proceed();
		stopWatch.stop();
		logger.info("■■■■■■■ [ finished ] - " + pjp.getSignature().getDeclaringTypeName() + " / "
				+ pjp.getSignature().getName() + " / [ TotalTimeMillis ] => " + stopWatch.getTotalTimeMillis()
				+ " ■■■■■■■");
		return result;
	}

}
