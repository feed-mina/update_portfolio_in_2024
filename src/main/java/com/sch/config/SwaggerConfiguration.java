package com.sch.config;

import com.fasterxml.classmate.TypeResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Configuration 	// 스프링 실행시 설정파일 읽어드리기 위한 어노테이션 
@EnableSwagger2 // swagger2를 사용하겠다는 어노테이션 
public class SwaggerConfiguration {
//http://52.78.212.203:8189/swagger-ui/index.html
	@Bean
	public Docket schApi(TypeResolver resolver) {

		return new Docket(DocumentationType.SWAGGER_2)
				.consumes(getConsumeContentTypes())
				.produces(getProduceContentTypes())
				.apiInfo(schAPIInfo()).select()
				.apis(RequestHandlerSelectors.basePackage("com.sch")).paths(PathSelectors.any()).build() // 패키지에 속한 컨트롤러 클래스에서만 문서화할 API를 선택합니다. 이 패키지 아래에 있는 컨트롤러 클래스들의 API만 문서화 .apis(RequestHandlerSelectors.any()) basePackage대신 any도 가능
				.useDefaultResponseMessages(false)
				// .host(baseUrl) // host를 설정하지 않았으면 baseUrl을 추론하게 되는데 reverse proxy를 쓰면서 도메인을 쓸떄 proxy_set_header 를 추가로 세팅해줘야 swagger가 baseUrl을 알맞게 추론할수있다
				.securityContexts(Arrays.asList(securityContext()))
        .securitySchemes(Arrays.asList(apiKey()));
	}

	private Set<String> getConsumeContentTypes() {
		Set<String> consumes = new HashSet<>();
		consumes.add("application/json;charset=UTF-8");
		return consumes;
	}

	private Set<String> getProduceContentTypes() {
		Set<String> produces = new HashSet<>();
		produces.add("application/json;charset=UTF-8");
		return produces;
	}

	private ApiInfo schAPIInfo() {
		return new ApiInfoBuilder().title("SCH API").description("<span >SCH API sjh유저 토큰 :eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJVU0VSXzAwMDAwMTMyIiwicm9sZXMiOiJTVCIsImlhdCI6MTY5MTUwMDY3MSwiZXhwIjo1MjkxNTAwNjcxfQ.ZDajJOpA17ScP5DpHMztadY6ZNQYbCwANNnwi93TTXI </span><br/>").version("1.0").build();
	}

	private SecurityContext securityContext() {
		return springfox
		.documentation
		.spi.service
		.contexts
		.SecurityContext
		.builder()
		.securityReferences(defaultAuth()).forPaths(PathSelectors.any()).build();
    }

	List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return Arrays.asList(new SecurityReference("JwtToken", authorizationScopes));
    }
	private ApiKey apiKey() {
        return new ApiKey("JwtToken", "X-AUTH-TOKEN", "header");
    }

	// TODO: 스웨거 운영환경 on/off 하기 https://velog.io/@u-nij/Spring-Boot-Swagger-3.0-%EC%84%A4%EC%A0%95-JWT-%EC%9D%B8%EC%A6%9D-%EC%84%A4%EC%A0%95-Profile%EB%A1%9C-%ED%99%98%EA%B2%BD%EB%B3%84-%EC%84%A4%EC%A0%95
	// 스웨거 운영환경 
		//     @Profile({"test || dev"})
    // @Bean
    // public Docket indexApi() {
    //     return docket("default", PathSelectors.ant("/api/**"));
    // }

    // @Bean
    // @Profile({"!test && !dev"})
    // public Docket disable() {
    //     return new Docket(DocumentationType.OAS_30).enable(false);
    // }
}
