package com.sch.service;

import java.io.UnsupportedEncodingException;
import java.util.Properties;
import java.util.Random;

import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.context.annotation.Primary;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
@Primary
public class MailServiceImpl implements MailService {
 
	final String HOST = "smtps.hiworks.com";
	final int PORT = 465;
	final String PASSWORD = "eoqkr!@345";
	final String FROM = "system@musicen.com";
	/*
	 * @Bean
	public JavaMailSender mailSender(){
		  JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        // 필요한 설정 구성
        return mailSender;
	};
*/
	// 만드려는 인증코드의 사이즈를 파라미터로 받는다. 랜덤으로 48부터 122까지 숫자를 뽑는다.
	// 뽑은 숫자가 소문자거나 (48~57), 대문자이거나 (97~122), 숫자거나 (65~90) StringBuilder에 char로 변환해서
	// 더한다. 이 외의 나머지 숫자를 뽑는다면 continue로 다시 뽑는다.
	// StringBuilder의 길이가 size와 같아질때까지 반복한다. sb를 반환한다.
	@Override
	public String makeCode(int size) {
		Random ran = new Random();
		StringBuffer sb = new StringBuffer();
		int num = 0;
		do {
			num = ran.nextInt(75) + 48;
			if ((num >= 48 && num <= 57) || (num >= 65 && num <= 90) || (num >= 97 && num <= 122)) {
				sb.append((char) num);
			} else {
				continue;
			}
		} while (sb.length() < size);
		return sb.toString();
	}

	@Override
	public String makeHtml(String type, String code) {
		String html = null;
		switch (type) {
			case "register":
				html = "register";
				break;
			case "findPw":
				html = "findPw";
				break;
		}
		return html;
	}

	@Override
	public String sendMail(String type, String email) throws MessagingException, UnsupportedEncodingException {
		// 공통 - 메일 보내기
		  JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		MimeMessage mail = javaMailSender().createMimeMessage();

		MimeMessageHelper helper = new MimeMessageHelper(mail, true, "UTF-8");
		InternetAddress to = new InternetAddress();

        JavaMailSender sender = javaMailSender(); 

		// 타입에 따라 인증코드랑 html 만들기
		String code = null, html = null, subject = null;
		switch (type) {
			case "register":
				code = makeCode(6);
				html = makeHtml(type, code);
				subject = "가입인증 이메일";
				break;
			case "findPw":
				code = makeCode(10);
				html = makeHtml(type, code);
				subject = "비밀번호찾기";
				break;
		}
		try {

			// String emailTo = "이메일 받을 주소";
			// to.setAddress(emailTo);
			// to.setPersonal(emailTo,"UTF-8");
			//	helper.setFrom(FROM);
			//	helper.setTo(to);
			//	helper.setSubject(subject);
			//	helper.setText("email text");
			 mail.setSubject(subject, "utf-8"); // 주제
			mail.setText(html, "utf-8", "html");
			mail.setFrom(new InternetAddress("system@musicen.com"));
			mail.addRecipient(RecipientType.TO, new InternetAddress(email));

			Properties props = mailSender.getJavaMailProperties();
			props.setProperty("mail.transport.protocol", "smtp");
			props.setProperty("mail.smtp.auth", String.valueOf(true));
			
			props.setProperty("mail.smtp.starttls.enable", "true");
			props.setProperty("mail.smtp.ssl.trust", "*");
			props.setProperty("mail.smtp.ssl.enable", "true");
			
			props.setProperty("mail.smtp.ssl.protocols", "TLSv1.2");
			javaMailSender();
		} catch (MessagingException e) {
			e.printStackTrace();
			return "error";
		} catch (MailException e) {
			e.printStackTrace();
			return "error";
		}
		return code;
	}
	
	public JavaMailSender javaMailSender() {
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setHost(HOST);
		mailSender.setPort(PORT);
		mailSender.setUsername(FROM);
		mailSender.setPassword(PASSWORD);
		
		Properties props = mailSender.getJavaMailProperties();
		props.setProperty("mail.transport.protocol", "smtp");
		props.setProperty("mail.smtp.auth", String.valueOf(true));
		
		props.setProperty("mail.smtp.starttls.enable", "true");
		props.setProperty("mail.smtp.ssl.trust", "*");
		props.setProperty("mail.smtp.ssl.enable", "true");
		
		props.setProperty("mail.smtp.ssl.protocols", "TLSv1.2");
		return mailSender;
	}
}
