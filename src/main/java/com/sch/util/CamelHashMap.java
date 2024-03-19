package com.sch.util;

import org.springframework.jdbc.support.JdbcUtils;

import java.util.LinkedHashMap;

@SuppressWarnings("serial")
public class CamelHashMap extends LinkedHashMap<String, Object> {

	@Override
	public Object put(String key, Object value) {
		// _(underscore가 있는 경우만 camel 변경)
		if (!key.contains("_")) {
			return super.put(key, value);
		}

		return super.put(JdbcUtils.convertUnderscoreNameToPropertyName(key), value);
	}

}
