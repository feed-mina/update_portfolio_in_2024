package com.sch.util;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRange;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class CommonResponse {

	public static ResponseEntity<?> statusResponse(int resultCode) {
		Map<String, Object> resultMap = new LinkedHashMap<String, Object>();
		resultMap.put("code", resultCode);

		if (resultCode == 200 || resultCode == 201) {
			resultMap.put("status", "success");
		} else {
			resultMap.put("status", "fail");
			return ResponseEntity.status(resultCode).body(resultMap);
		}

		return ResponseEntity.ok().body(resultMap);
	}

	public static ResponseEntity<?> statusResponse(int resultCode, List<Map<String, Object>> mapList) {
		Map<String, Object> resultMap = new LinkedHashMap<String, Object>();
		resultMap.put("code", resultCode);

		if (resultCode == 200 || resultCode == 201) {
			resultMap.put("status", "success");
		} else {
			resultMap.put("status", "fail");
			return ResponseEntity.status(resultCode).body(resultMap);
		}

		JSONArray jSONArray = new JSONArray();
		jSONArray.addAll(mapList);
		resultMap.put("data", jSONArray);

		return ResponseEntity.ok().body(resultMap);
	}

	public static ResponseEntity<?> statusResponse(int resultCode, Map<String, Object> resMap) {
		Map<String, Object> resultMap = new LinkedHashMap<String, Object>();
		resultMap.put("code", resultCode);

		JSONObject jSONObject = new JSONObject();
		jSONObject.putAll(resMap);

		resultMap.put("data", jSONObject);
		if (resultCode == 200 || resultCode == 201) {
			resultMap.put("status", "success");
			return ResponseEntity.ok().body(resultMap);
		} else {
			resultMap.put("status", "fail");
			return ResponseEntity.status(resultCode).body(resultMap);
		}

	}

	public static ResponseEntity<?> statusResponse(int resultCode, String message) {
		Map<String, Object> resultMap = new LinkedHashMap<String, Object>();
		resultMap.put("code", resultCode);
		resultMap.put("message", message);

		if (resultCode == 200 || resultCode == 201) {
			resultMap.put("status", "success");
		} else {
			resultMap.put("status", "fail");
			return ResponseEntity.status(resultCode).body(resultMap);
		}

		return ResponseEntity.ok().body(resultMap);
	}

	@SuppressWarnings("rawtypes")
	public static ResponseEntity<?> responseWriter(int statusCode) throws IOException, ServletException {

		Map<String, Object> resultMap = new LinkedHashMap<String, Object>();
		resultMap.put("code", statusCode);
		if (statusCode == 200 || statusCode == 201) {
			resultMap.put("status", "success");
		} else {
			resultMap.put("status", "fail");
			return ResponseEntity.status(statusCode).body(resultMap);
		}

	ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		HttpServletResponse res = attr.getResponse();
		res.setStatus(statusCode);
		res.setContentType("application/json");
		res.setCharacterEncoding("UTF-8");

		ResponseEntity<?> resEntity = statusResponse(statusCode, HttpStatus.resolve(statusCode).getReasonPhrase());

		JSONObject jSONObject = new JSONObject();
		jSONObject.putAll((Map) resEntity.getBody());
		res.getWriter().write(jSONObject.toString());
		resultMap.put("data", jSONObject);
		return ResponseEntity.status(statusCode).body(resultMap);
	}
	
	

	@SuppressWarnings("rawtypes")
	public static void responseWriter(int statusCode, String message) throws IOException, ServletException {
		ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		HttpServletResponse res = attr.getResponse();
		res.setStatus(statusCode);
		res.setContentType("application/json");
		res.setCharacterEncoding("UTF-8");

		ResponseEntity<?> resEntity = statusResponse(statusCode, message);

		JSONObject jSONObject = new JSONObject();
		jSONObject.putAll((Map) resEntity.getBody());
		res.getWriter().write(jSONObject.toString());

		return;
	}

	public static ResourceRegion resourceRegion(Resource video, HttpHeaders headers) throws IOException {
		final long chunkSize = 1000000L;
		long contentLength = video.contentLength();
		HttpRange httpRange = headers.getRange().stream().findFirst().get();
		if (httpRange != null) {
			long start = httpRange.getRangeStart(contentLength);
			long end = httpRange.getRangeEnd(contentLength);
			long rangeLength = Long.min(chunkSize, end - start + 1);
			return new ResourceRegion(video, start, rangeLength);
		} else {
			long rangeLength = Long.min(chunkSize, contentLength);
			return new ResourceRegion(video, 0, rangeLength);
		}
	}

}
