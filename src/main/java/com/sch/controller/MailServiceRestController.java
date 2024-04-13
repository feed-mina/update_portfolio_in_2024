package com.sch.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.sch.service.RegisterMail;

@RestController
@RequestMapping(value = "/mail")
public class MailServiceRestController {

    @Autowired
    RegisterMail registerMail;

    //127.0.0.1:8189/ROOT/mail/confirm.json?email
    @PostMapping(value = "/confirm.json")
    public String emailCode(@RequestParam("email") String email) throws Exception{
        String code = registerMail.sendSimpleMessage(email);
        System.out.println("사용자에게 발송한 인증코드 ==> " + code);

        return code;
    }

/*
 *     
    @PostMapping("/emailConfirm")
    @ApiOperation(value = "회원 가입시 이메인 인증", notes = "기존사용하고 있는 이메일을 통해 인증")
    @ApiResponses({
            @ApiResponse(code = 200, message = "성공"),
            @ApiResponse(code = 401, message = "인증 실패"),
            @ApiResponse(code = 404, message = "사용자 없음"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    public ResponseEntity<?> emailConfirm(
            @RequestBody @ApiParam(value="이메일정보 정보", required = true) String email)  {
 
        MimeMessage confirm = null;
		try {
			confirm = registerMail.creatMessage(email);
		} catch (UnsupportedEncodingException | MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
 
        return CommonResponse.statusResponse(HttpServletResponse.SC_OK, confirm);   
    }
 * */

    @PostMapping("/verifyCode")
    @ResponseBody
    public int verifyCode(@RequestParam("code") String code) {
    	int result = 0;
    	System.out.println("code : "+ code);
    	System.out.println("code match : " + registerMail.code.equals(code));
    	if(registerMail.code.equals(code)) {
    		result = 1;
    	}
    	return result;
    }
    
}