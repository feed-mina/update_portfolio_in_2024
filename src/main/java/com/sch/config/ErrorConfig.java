package com.sch.config;

import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class ErrorConfig implements WebServerFactoryCustomizer<ConfigurableServletWebServerFactory>{

	@Override
	public void customize(ConfigurableServletWebServerFactory factory) {
		ErrorPage errorPage404 = new ErrorPage(HttpStatus.NOT_FOUND,"/error-page/404"); // response.sendError
		ErrorPage errorPage500 = new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/error-page/500"); // response.sendError

		ErrorPage errorPageException = new ErrorPage(RuntimeException.class, "/error-page/500"); // Exception
		
		factory.addErrorPages(errorPage404, errorPage500, errorPageException);
	}
}
