package com.keithstric.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * This class is for constructing an MD5 Hash from a string (in this case
 * the email address) for the Gravatar
 * 
 * @author Keith Strickland
 * 
 */
public class MD5Util {

	/**
	 * This class is for converting the email address into an MD5 hash to
	 * send to gravatar
	 */

	public static String hex(byte[] array) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < array.length; i++) {
			sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
		}
		return sb.toString();
	}

	public static String md5Hex(String email) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			return hex(md.digest(email.getBytes("CP1252")));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
}
