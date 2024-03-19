package com.sch.service;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.JsonObject;
import com.sch.encrypt.AES256Encrypt;
import com.sch.util.CommonUtil;
import com.sch.util.JSONObject;

@Service
public class LoginService {


	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	CommonService commonService;
 

	// @Autowired
	// AlarmService alarmService;
	
	public void useAtUpdate(Map<String, Object> paramMap) {
		System.out.println("parmaMap"+paramMap);
		commonService.insert("login.useAtUpdate", paramMap);
	}
	
	
	public void doSave(Map<String, Object> paramMap, HttpServletRequest request) throws Exception {
		// 유저 생성
		commonService.insert("login.userRegist", paramMap);

		// 동의여부 insert
		// Map<String, String> stplatCode = (Map<String, String>) paramMap.get("stplatCode");
/*
 * 		for (Entry<String, String> stplatcode : stplatCode.entrySet()){
			paramMap.put("stplatCode", stplatcode.getKey());
			paramMap.put("agreAt", stplatcode.getValue());
			commonService.insert("login.userStplat", paramMap);
		}
 */

		JSONObject rewardJson = new JSONObject();
		 rewardJson.put("userSeq", paramMap.get("userSeq"));
		String jsonString = AES256Encrypt.encrypt(rewardJson.toString());

		// 이메일 전송
		// String targetUrl = CommonUtil.getHostAddress(request) + "/sch/user/registerEmailComplete.html?encData=" + jsonString;
		// alarmService.insertUserRegistAlarm((String) paramMap.get("userSeq"), targetUrl);

	}
	


	public void doUpdate(Map<String, Object> paramMap) throws Exception {
		// userSeq가 있으면 유저필드 update
		commonService.update("login.userUpdate", paramMap);

		// 동의여부 update
		/*
		 * Map<String, String> stplatCode = (Map<String, String>) paramMap.get("stplatCode");
		if(CommonUtil.isNotEmpty(stplatCode)){
			for (Entry<String, String> stplatcode : stplatCode.entrySet()){
				paramMap.put("stplatCode", stplatcode.getKey());
				paramMap.put("agreAt", stplatcode.getValue());
				commonService.insert("login.userStplatUpdate", paramMap);
			}
		}
		 * */
	}


	public void sendVerificationMail(String userSeq, HttpServletRequest request) throws Exception{
		System.out.println("userSeq"+userSeq);
		System.out.println("request"+request);
		JSONObject emailJson = new JSONObject();
		emailJson.put("emailJson",emailJson);
		System.out.println("request"+request);
		String  jsonString = AES256Encrypt.encrypt(emailJson.toString());

		System.out.println("jsonString"+jsonString);
		// 이메일 전송
		String targetUrl = CommonUtil.getHostAddress(request) + "/sch/user/registerEmailComplete.html?encData=" + jsonString;
	}
	
}
