package com.sch.encrypt;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class AES256Encrypt {

	private static String key = "realcastlemusiceniwillb987654321";

	/**
	 * 문자열을 암호화 한다.
	 *
	 * @param key          암호화할 키
	 * @param strToEncrypt 암호화할 문자열
	 * @return 암호화된 문자열을 바이트 배열로 반환한다.
	 * @throws Exception
	 */

	private static byte[] encryptToBytes(String strToEncrypt) throws Exception {
		SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "AES");
		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, secretKey);
		return cipher.doFinal(strToEncrypt.getBytes("UTF-8"));

	}// :

	/**
	 * 문자열을 암호화 한다.
	 *
	 * @param key          암호화할 키
	 * @param strToEncrypt 암호화할 문자열
	 * @return 암호화된 문자열을 base64로 encode해서 반환한다
	 * @throws Exception
	 */
	public static String encrypt(String strToEncrypt) throws Exception {
		String encryptedStr = Base64.getEncoder().encodeToString(encryptToBytes(strToEncrypt));
		encryptedStr = encryptedStr.replaceAll("/", "REAL");
		return encryptedStr;
	}// :

	/**
	 * 암호화된 바이트 배열을 복호화 한다.
	 *
	 * @param key            암호화 키
	 * @param bytesToDecrypt 복호화할 바이트 배열
	 * @return 복호화된 바이트 배열
	 * @throws Exception
	 */
	private static byte[] decryptToBytes(byte[] bytesToDecrypt) throws Exception {
		SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "AES");
		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
		cipher.init(Cipher.DECRYPT_MODE, secretKey);
		return cipher.doFinal(bytesToDecrypt);
	}// :

	/**
	 * Base 64로 감싸진 문자열을 복호화 한다.
	 *
	 * @param key          암호화 키
	 * @param strToDecrypt 복호화할 문자열
	 * @return 복호화된 문자열
	 * @throws Exception
	 */
	public static String decrypt(String strToDecrypt) throws Exception {
		strToDecrypt = strToDecrypt.replaceAll("REAL", "/");
		byte[] bytesToDecrypt = Base64.getDecoder().decode(strToDecrypt);
		String decreptedStr = new String(decryptToBytes(bytesToDecrypt));
		return decreptedStr;
	}// :

}
