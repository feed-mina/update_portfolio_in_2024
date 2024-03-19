package com.sch.encrypt;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHA256Encrypt {

	public static String encrypt(String str) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] passBytes = str.getBytes();
			md.reset();

			byte[] digested = md.digest(passBytes);
			StringBuffer sb = new StringBuffer();
			for (byte element : digested) {
				String hex = Integer.toHexString(0xff & element);
				if (hex.length() == 1) {
					hex = "0" + hex;
				}
				sb.append(hex);
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			return str;
		}
	}

}
