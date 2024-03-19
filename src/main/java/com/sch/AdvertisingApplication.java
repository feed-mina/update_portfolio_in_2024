package com.sch;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Controller;

@MapperScan(basePackageClasses = AdvertisingApplication.class)
@SpringBootApplication
@EnableCaching
@EnableScheduling
@EnableAsync
@Controller
public class AdvertisingApplication {
    public static void main(String[] args) {
        SpringApplication.run(AdvertisingApplication.class, args);
    } 
    
	public String redirectAdmin() {
		return "redirect:/sch/huss/dashBoard/main.html";
	}
}
