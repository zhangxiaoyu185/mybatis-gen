package com.xiaoyu.util;

import org.apache.commons.lang3.StringUtils;

public class StringUtil {

	public static String javaShortTypeName(String fullname) {
		int indx = fullname.lastIndexOf('.');
		return fullname.substring(indx + 1);
	}

	/**
	 * FEE_AA fee_aa -> feeAa
	 * 
	 * @return
	 */
	public static String nameFormat(String input) {
		input = input.toLowerCase();
		String[] words = input.split("_");
		StringBuilder res = new StringBuilder();
		for (int i = 0; i < words.length; i++) {
			String word = firstCharUpcase(words[i]);
			res.append(word);
		}
		return firstCharLowcase(res.toString());
	}

	public static String firstCharUpcase(String input) {
		byte[] items = input.getBytes();
		items[0] = (byte) ((char) items[0] - 'a' + 'A');
		return new String(items);
	}

	public static String firstCharLowcase(String input) {
		byte[] items = input.getBytes();
		items[0] = (byte) ((char) items[0] - 'A' + 'a');
		return new String(items);
	}

	public static boolean like(String input, String charset) {
		if(input.toLowerCase().indexOf(charset) > 0) {
			return true;
		}
		return false;
	}

	public static String charLowcase(String input) {
		return input.toLowerCase();
	}

	public static String cutLastChar(String input) {
		if(StringUtils.isEmpty(input)) {
			return "";
		}
		return input.substring(0, input.length()-1);
	}
}
