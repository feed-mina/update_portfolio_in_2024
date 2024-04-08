package com.sch.config;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.annotation.PrincipalMethodArgumentResolver;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sch.config.security.JwtTokenProvider;
import com.sch.util.CommonUtil;
import com.sch.util.MySQLPageRequestHandleMethodArgumentResolver;
import com.sch.util.UserParam;
@Configuration
public class WebConfig<CustomRequestMappingHandlerAdapter> implements WebMvcConfigurer {

	@Autowired
	private JwtTokenProvider jwtTokenProvider;
 
	@Bean     
    public PrincipalMethodArgumentResolver principalMethodArgumentResolver() {
    	return new PrincipalMethodArgumentResolver();    
    	}
 
	
    @Override
	public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer.defaultContentType(MediaType.APPLICATION_JSON);
        // ContentNegotiationConfigurer를 사용하여 기본 Content-Type을 JSON으로 설정합니다.
    }

    @Override
	public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.removeIf(converter -> converter instanceof MappingJackson2HttpMessageConverter);
        converters.add(mappingJackson2HttpMessageConverter());
        //extendMessageConverters 메서드를 사용하여 기본으로 등록된 MappingJackson2HttpMessageConverter를 제거하고, 
        // LinkedHashMap를 올바르게 변환하기 위해 새로운 MappingJackson2HttpMessageConverter를 추가합니다.

    }

    private MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setSupportedMediaTypes(Collections.singletonList(MediaType.APPLICATION_JSON));
        //새로 추가된 MappingJackson2HttpMessageConverter에는 
        // APPLICATION_JSON 미디어 타입이 지정되어 있으므로 JSON 형식의 데이터를 처리할 수 있습니다.
        return converter;
    }
	/**
	 * Argment Resolver userSession이 있는 경우만 map값을 채움
	 */
	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
		resolvers.add(loginUserResolver());
		/** 페이징 처리 */
	    // 페이지 리졸버 등록
	    resolvers.add(new MySQLPageRequestHandleMethodArgumentResolver());
	}

	public HandlerMethodArgumentResolver loginUserResolver() {
		return new HandlerMethodArgumentResolver() {

			@Override
			public boolean supportsParameter(MethodParameter parameter) {
				return parameter.hasParameterAnnotation(UserParam.class);
			}

			@SuppressWarnings({ "rawtypes", "unchecked" })
			@Override
			public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
					NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
				Object resolved = null;

				HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();

				String token = jwtTokenProvider.resolveToken(request); // token을 가져옴.
				if (CommonUtil.isEmpty(token)) {
					throw new AuthenticationCredentialsNotFoundException("Unauthorized 토큰이없어 UserParam사용할수없음");
				}
				String userPk = jwtTokenProvider.getUserPk(token);

				// 메모리에서 세션 load
				Map<String, Object> user = CommonUtil.loginSession.get(userPk);

				if (user != null) {
					if (Map.class.isAssignableFrom(parameter.getParameterType())) {
						// 1. 입력된 파라미터의 default Map 생성
						ObjectMapper mapper = new ObjectMapper();
						Map paramMap = mapper.readValue(getRequestBody(webRequest), Map.class);
						// 2. session 값을 덮어 씀
						paramMap.put("user", user);
						resolved = paramMap;
					} else if (String.class.isAssignableFrom(parameter.getParameterType())) {
						UserParam annotation = parameter.getParameterAnnotation(UserParam.class);
						resolved = annotation.value().isEmpty() ? null : user.get(annotation.value());
					}
				} else {
					throw new AuthenticationCredentialsNotFoundException(
							"Unauthorized in CommonUtil.loginSession 로그인먼저하세요");
				}

				return resolved;
			}
		};
	}

	private String getRequestBody(NativeWebRequest webRequest) {
		HttpServletRequest servletRequest = webRequest.getNativeRequest(HttpServletRequest.class);

		String jsonBody = (String) webRequest.getAttribute("JSON_REQUEST_BODY", NativeWebRequest.SCOPE_REQUEST);
		if (jsonBody == null) {
			try {
				jsonBody = IOUtils.toString(servletRequest.getInputStream(), "UTF-8");
				webRequest.setAttribute("JSON_REQUEST_BODY", jsonBody, NativeWebRequest.SCOPE_REQUEST);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return jsonBody;

	}
	@Override
	public void addFormatters(FormatterRegistry registry) {
		// new ObjectMapper().registerModule(new SimpleModule()).registerModule(new
		// JavaTimeModule()).configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
		// false);
		ObjectMapper objectMapper = new ObjectMapper();
		registry.addConverter(new JsonStringConvertor(objectMapper));
	}
}
