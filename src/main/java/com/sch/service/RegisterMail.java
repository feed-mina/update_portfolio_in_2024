package com.sch.service;

import java.io.UnsupportedEncodingException;
import java.util.Random;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class RegisterMail implements MailServiceInter {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    JavaMailSender emailSender; // MailConfig에서 등록해둔 Bean을 autowired하여 사용하기
    private static final String ADMIN_ADDRESS = "myelin24@naver.com";
    
    public  final String code = createKey(); // 인증번호
    
    private String ePw; // 사용자가 메일로 받을 인증번호

    // 메일 내용 작성
    @Override
    public MimeMessage creatMessage(String to) throws MessagingException, UnsupportedEncodingException { 
        try { // 예외처리
        	   logger.info("메일받을 사용자" + to);
               logger.info("인증번호" + ePw);

               MimeMessage message = emailSender.createMimeMessage();

               message.addRecipients(RecipientType.TO, to); // 메일 받을 사용자
               message.setSubject("[Fligent] 비밀번호 변경을 위한 이메일 인증코드 입니다"); // 이메일 제목

               String msgg = ""; 
               msgg += "<h1>안녕하세요</h1>";
               msgg += "<h1>최애 연애인이랑 함께하는 포토부스 테스트 이메일입니다.</h1>";
               msgg += "<br>";  
               msgg += "<br>";
               msgg += "<div align='center' style='border:1px solid black'>";
               msgg += "<h3 style='color:blue'>회원가입 인증코드 입니다</h3>";
               msgg += "<div style='font-size:130%'>";
               msgg += "<strong>" + ePw + "</strong></div><br/>" ; // 메일에 인증번호 ePw 넣기
               msgg += "</div>"; 

               message.setText(msgg, "utf-8", "html"); // 메일 내용, charset타입, subtype
               // 보내는 사람의 이메일 주소, 보내는 사람 이름
               message.setFrom(new InternetAddress("myelin24@naver.com", "testcase"));
               logger.info("********creatMessage 함수에서 생성된 msgg 메시지********" + msgg);

               logger.info("********creatMessage 함수에서 생성된 리턴 메시지********" + message);
               return message;
        } catch (MailException mailException) {
        	mailException.printStackTrace();
            throw new IllegalArgumentException();
        }

    }

    // 랜덤 인증코드 생성 
	@Override
	public  String createKey() {
    	 StringBuffer key = new StringBuffer();
         Random rnd = new Random();
  
         for (int i = 0; i < 8; i++) { // 인증코드 8자리
             int index = rnd.nextInt(3); // 0~2 까지 랜덤
  
             switch (index) {
                 case 0:
                     key.append((char) ((rnd.nextInt(26)) + 97));
                     //  a~z  (ex. 1+97=98 => (char)98 = 'b')
                     break;
                 case 1:
                     key.append((char) ((rnd.nextInt(26)) + 65));
                     //  A~Z
                     break;
                 case 2:
                     key.append((rnd.nextInt(10)));
                     // 0~9
                     break;
             }
         }
         return key.toString();
    }

    // 메일 발송
    // sendSimpleMessage 의 매개변수 to는 이메일 주소가 되고,
    // MimeMessage 객체 안에 내가 전송할 메일의 내용을 담는다
    // bean으로 등록해둔 javaMail 객체를 사용하여 이메일을 발송한다
    @Override
    public String sendSimpleMessage(String to) throws Exception {

        ePw = createKey(); // 랜덤 인증코드 생성
        logger.info("********생성된 랜덤 인증코드******** => " + ePw);

        MimeMessage message = creatMessage(to); // "to" 로 메일 발송

        logger.info("********생성된 메시지******** => " + message);

        try { // 예외처리
            emailSender.send(message);
        } catch (MailException mailException) {
        	mailException.printStackTrace();
            throw new IllegalArgumentException();
        }

        return ePw; // 메일로 사용자에게 보낸 인증코드를 서버로 반환! 인증코드 일치여부를 확인하기 위함
    }

}
