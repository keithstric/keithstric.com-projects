package com.keithstric.utils;

import java.util.Arrays;
import java.util.List;

public class StringUtils {

	/**
	 * This is an @Word equivalent
	 * @param startString - The string you want to get a specific word from
	 * @param separator - The separators between the words of the startString
	 * @param wordNum - The number element you want to return
	 * @return String
	 */
	public static String word(String startString, String separator, Integer wordNum) {
		String result = null;
		List<String> startList = Arrays.asList(startString.split(separator));
		Integer element = wordNum -1;	
		if(startList.size() >= wordNum){
			result = startList.get(element);
		}
		if(result == null || result.isEmpty()){
			result = "";
		}
		return result;
	}
	
	/**
	 * This is an @Word equivalent except accepting a list of strings to search
	 * @param startList - A java.util.List<String> that you want to get a specific word from
	 * @param separator - The separators between the words
	 * @param wordNum - The number element you want to return
	 * @return String with multiple words separated by ":::"
	 */
	public static String word(List<String> startList, String separator, Integer wordNum) {
		String result = null;
		Integer element = wordNum -1;
		for (String startString : startList) {
			List<String> elementList = Arrays.asList(startString);
			if (result != null) {
				result = result + ":::" + elementList.get(element);
			}else{
				result = elementList.get(element);
			}
		}
		return result;
	}
	
	public static String strLeft(String input, String delimiter) {
		return input.substring(0, input.indexOf(delimiter));
	}
	public static String strRight(String input, String delimiter) {
		return input.substring(input.indexOf(delimiter) + delimiter.length());
	}
	public static String strLeftBack(String input, String delimiter) {
		return input.substring(0, input.lastIndexOf(delimiter));
	}
	public static String strLeftBack(String input, int chars) {
		return input.substring(0, input.length() - chars);
	}
	public static String strRightBack(String input, String delimiter) {
		return input.substring(input.lastIndexOf(delimiter) + delimiter.length());
	}
	public static String strRightBack(String input, int chars) {
		return input.substring(input.length() - chars);
	}
	
}
