package com.sch.util;

import java.sql.Connection;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component

public class Logging {

	@Autowired
	DataSource dataSource;
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	// @Override
	public void run(ApplicationArguments args) throws Exception{
		
		Connection connection = dataSource.getConnection();
		
		System.out.println("DBCP : " + dataSource.getClass());
		System.out.println("Url " + connection.getMetaData().getURL());
		System.out.println("UserName: "+connection.getMetaData().getUserName());
		
		// jdbcTemplate
		// jdbcTemplate.execute("INSERT INTO tb_test(test_seq, test_name) values (1,'연결테스트')");
		//log.info("Url " + connection.getMetaData().getURL());
		//log.info("UserName: "+connection.getMetaData().getUserName());
	}
}
