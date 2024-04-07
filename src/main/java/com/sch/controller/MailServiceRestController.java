package com.sch.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sch.service.RegisterMail;

@RestController
@RequestMapping(value = "/api/mail")
public class MailServiceRestController {

    @Autowired
    RegisterMail registerMail;

    //127.0.0.1:8080/ROOT/api/mail/confirm.json?email
    @PostMapping(value = "/confirm.json")
    public String mailConfirm(@RequestParam(name = "email") String email) throws Exception{
        String code = registerMail.sendSimpleMessage(email);
        System.out.println("사용자에게 발송한 인증코드 ==> " + code);

        return code;
    }

}