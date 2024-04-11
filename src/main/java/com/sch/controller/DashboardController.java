package com.sch.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.rowset.serial.SerialException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sch.service.CommonService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(tags = "03 DashBoard", description = "메인 & 대쉬보드")
@RestController
@RequestMapping("/dashBoard")
public class DashboardController {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	// 	private final UserManageService userManageService = new UserManageService();

	@Autowired
	CommonService commonService;

	@GetMapping(value = "/dispatcher.api")
	@ApiOperation(value = "", notes = "")
	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws SerialException, IOException, ServletException { 
	 
	 	logger.info("디스패처 진입"); 
		response.sendRedirect(request.getContextPath() + "/sch/huss/dashBoard/main.html");
	 
	}
}
