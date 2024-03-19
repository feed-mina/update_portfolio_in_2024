package com.sch.enums;

public enum DispathcerType {
	// Apply the filter on "RequestDispathcer.forwarld()" calls 서블릿에서 다른 서블릿을 호출할때
	FORWARD,
	// Apply the filter on "RequestDispatcher.include()" calls 서블릿에서 다른 서블릿의 결과를 포함할때
	INCLUDE,
	// Apply the filter on ordinary client calls 클라이언트의요청
	REUQEST,
	// Apply the filter under calls dispathched from on AsyncContext 서블릿 비동기 호출
	ASYNC,
	// Apply the filter when an error is handled 오류요청
	ERROR

}
