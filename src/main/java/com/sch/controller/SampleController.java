package com.sch.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sch.service.CommonService;

import io.swagger.annotations.Api;

@Api(tags = "03 Sample", description = "Sample 컨트롤러")
@RestController
@RequestMapping("/sample")
public class SampleController {

@Autowired
CommonService commonService;

@RequestMapping("/sample1")
public String user(@AuthenticationPrincipal User user) {
	return null;
	
}

@RequestMapping("/sample2")
public String sample2(Principal principal, User User) {
	Authentication autentication = (Authentication) principal; 
	User = (User) autentication.getPrincipal();
	return null;
}

@GetMapping("/home")
public String home(Principal principal, Model model){
    model.addAttribute("user", principal.getName());
    model.addAttribute("roles", ((UsernamePasswordAuthenticationToken) principal).getAuthorities());
    return "/home";
}

@GetMapping("/admin")
public String admin(Principal principal, Model model){
    model.addAttribute("user", principal.getName());
    model.addAttribute("roles", ((UsernamePasswordAuthenticationToken) principal).getAuthorities());
    return "/admin";
}

@GetMapping("/403")
public String forbidden() {
    return "/403";
}

}
