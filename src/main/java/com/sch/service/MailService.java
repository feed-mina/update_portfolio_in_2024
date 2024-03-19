package com.sch.service;

import java.io.UnsupportedEncodingException;

import javax.mail.MessagingException;

public interface MailService {
	String makeCode(int size);

	String makeHtml(String type, String code);
 

	String sendMail(String type, String email) throws MessagingException, UnsupportedEncodingException;

	//String sendMail(Object object, Object object2);
}
