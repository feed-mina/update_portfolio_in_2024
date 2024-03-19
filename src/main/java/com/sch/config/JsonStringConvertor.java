package com.sch.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class JsonStringConvertor implements Converter<String, Map<String, Object>> {
	ObjectMapper objectMapper;
	
	public JsonStringConvertor(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	/*
	 * @RequestParam 일때만 이부분이 태워진다(http://host?paramMap={a:1,b:2})
	 */
	@Override
	public Map<String, Object> convert(String value) {
		try {
			return objectMapper.readValue(value, new TypeReference<Map<String, Object>>() {});
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}


}
