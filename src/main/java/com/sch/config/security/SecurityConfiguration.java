package com.sch.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Autowired
	private JwtTokenProvider jwtTokenProvider;

	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Override
	public void configure(HttpSecurity http) throws Exception {
			
		http.httpBasic().disable() // rest api 이므로 기본설정 사용안함. 기본설정은 비인증시 로그인폼 화면으로 리다이렉트 된다.
				.csrf().disable() // rest api이므로 csrf 보안이 필요없으므로 disable처리.
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // jwt token 세션필요없으므로 생성안함.
				.and().authorizeRequests()
				.antMatchers(HttpMethod.OPTIONS, "/**").permitAll() 
				 
				.antMatchers("/","/index","/sch/js/huss/**","/error","/error/**","/sch/test/**","/sch/huss/**","/sch/huss/auth/**","/sch/huss/login/**","/sch/huss/community/**", "/sch/huss/dashBoard/**","/sch/huss/user/**","/mail/**","/sch/huss/mail/**","/sch/huss/notice/**","/cmmn/**","/communityController/**" , "/noticeController/**", "/login/**","/admin/**","/auth/**","/login/**","/dashBoard/**","/experience/**","/notice/**","/board/**")
				.permitAll() // 이하 라우팅은 인증필요없음(X-AUTH-TOKEN);
				.anyRequest().authenticated() // 위 request외 모두인증필요
				.and().exceptionHandling().accessDeniedHandler(new CustomAccessDeniedHandler()).and()
				.exceptionHandling().authenticationEntryPoint(new CustomAuthenticationEntryPoint())
				.and().addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider),UsernamePasswordAuthenticationFilter.class);
	}
			 
/**		http.formLogin()
				.loginPage("/sch/huss/login")
				// .loginProcessingUrl("/sch/huss/auth/kakao")
				.defaultSuccessUrl("/sch/main/dashBoard", true)
				.failureUrl("/login?error=true")
				.permitAll()
				.usernameParameter("email")
				.passwordParameter("password")
				.and()
				.logout()
				.logoutSuccessUrl("/huss/dashBoard")
				.invalidateHttpSession(true);
	
	} */
	
@Override
protected void configure(AuthenticationManagerBuilder auth) throws Exception{
	auth.inMemoryAuthentication()
		.passwordEncoder(passwordEncoder())
		.withUser("user")
		.password(passwordEncoder().encode("user123"))
		.roles("G");
}
	@Bean
	public PasswordEncoder passwordEncoder() {
	return new BCryptPasswordEncoder();
}

	@Override
	public void configure(WebSecurity web) {
		web.ignoring().antMatchers("/v2/api-docs","/api/v2/api-docs", "/swagger-resources/**","/swagger-ui/**", "/swagger-ui/index.html", "/swagger/**", "/sign-api/exception","/fonts/pretendard/**","/fonts/**","/js/**","/css/**","/index","/include/**","/include/header.html","/sch/js/**","/image/**", "/**/favicon.ico","/webjars/**","/image/promotion","/images/pc/**");
		web.ignoring().mvcMatchers("/favicon.ico");
	web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());	
	}

}