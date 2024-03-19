package com.sch.config.security;

import com.sch.util.CommonUtil;
import com.sch.service.CommonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class UserUserDetailsService implements UserDetailsService {

	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	CommonService commonService;

	/**
	 * 각 서비스 로직에맞게 유저데이터를 끌어와서(우리 전략은 CommonUtil.loginSession 메모리에 있으면 쓰고 없으면 db에서 로드)
	 * UserDetails 형태로 가공해서 리턴
	 */
	@Override
	public UserDetails loadUserByUsername(String userPk) throws UsernameNotFoundException {
		
		// jwt 값이 메모리에 있으면 먼저 load, 없으면 DB load
		Map<String, Object> loginMap = CommonUtil.loginSession.get(userPk);

		if (ObjectUtils.isEmpty(loginMap)) {
			Map<String, Object> userMap = new LinkedHashMap<String, Object>();
			userMap.put("userSeq", userPk);
			
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			paramMap.put("user", userMap);
			// userSeq 기준으로 db조회
			loginMap = commonService.selectMap("login.selectLoginMap", paramMap);

			CommonUtil.loginSession.put((String) loginMap.get("userSeq"), loginMap);
		}

		return User.builder().username((String) loginMap.get("userSeq")).password("")
				.roles((String) loginMap.get("userAuthor")).build();
	}
}