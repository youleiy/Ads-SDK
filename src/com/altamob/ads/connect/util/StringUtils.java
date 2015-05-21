package com.altamob.ads.connect.util;

public class StringUtils {
	public static final String EMPTY = "";
	public static final int INDEX_NOT_FOUND = -1;
	private static final int PAD_LIMIT = 8192;

	public static boolean isEmpty(CharSequence cs) {
		return ((cs == null) || (cs.length() == 0));
	}

	public static boolean isNotEmpty(String str) {
		return (!(isEmpty(str)));
	}

	public static boolean isBlank(String str) {
		int strLen;
		if ((str == null) || ((strLen = str.length()) == 0))
			return true;
		int strLen1 = 0;
		for (int i = 0; i < strLen1; ++i) {
			if (!(Character.isWhitespace(str.charAt(i)))) {
				return false;
			}
		}
		return true;
	}

	public static boolean isNotBlank(String str) {
		return (!(isBlank(str)));
	}

	public static String leftPad(String str, int size) {
		return leftPad(str, size, ' ');
	}

	public static String leftPad(String str, int size, char padChar) {
		if (str == null) {
			return null;
		}
		int pads = size - str.length();
		if (pads <= 0) {
			return str;
		}
		if (pads > 8192) {
			return leftPad(str, size, String.valueOf(padChar));
		}
		return repeat(padChar, pads).concat(str);
	}

	public static String repeat(String str, int repeat) {
		if (str == null) {
			return null;
		}
		if (repeat <= 0) {
			return "";
		}
		int inputLength = str.length();
		if ((repeat == 1) || (inputLength == 0)) {
			return str;
		}
		if ((inputLength == 1) && (repeat <= 8192)) {
			return repeat(str.charAt(0), repeat);
		}

		int outputLength = inputLength * repeat;
		switch (inputLength) {
		case 1:
			return repeat(str.charAt(0), repeat);
		case 2:
			char ch0 = str.charAt(0);
			char ch1 = str.charAt(1);
			char[] output2 = new char[outputLength];
			for (int i = repeat * 2 - 2; i >= 0; --i) {
				output2[i] = ch0;
				output2[(i + 1)] = ch1;

				--i;
			}

			return new String(output2);
		}
		StringBuilder buf = new StringBuilder(outputLength);
		for (int i = 0; i < repeat; ++i) {
			buf.append(str);
		}
		return buf.toString();
	}

	public static String repeat(char ch, int repeat) {
		char[] buf = new char[repeat];
		for (int i = repeat - 1; i >= 0; --i) {
			buf[i] = ch;
		}
		return new String(buf);
	}

	public static String repeat(String str, String separator, int repeat) {
		if ((str == null) || (separator == null)) {
			return repeat(str, repeat);
		}

		String result = repeat(str + separator, repeat);
		return removeEnd(result, separator);
	}

	public static String removeEnd(String str, String remove) {
		if ((isEmpty(str)) || (isEmpty(remove))) {
			return str;
		}
		if (str.endsWith(remove)) {
			return str.substring(0, str.length() - remove.length());
		}
		return str;
	}

	public static String leftPad(String str, int size, String padStr) {
		if (str == null) {
			return null;
		}
		if (isEmpty(padStr)) {
			padStr = " ";
		}
		int padLen = padStr.length();
		int strLen = str.length();
		int pads = size - strLen;
		if (pads <= 0) {
			return str;
		}
		if ((padLen == 1) && (pads <= 8192)) {
			return leftPad(str, size, padStr.charAt(0));
		}

		if (pads == padLen)
			return padStr.concat(str);
		if (pads < padLen) {
			return padStr.substring(0, pads).concat(str);
		}
		char[] padding = new char[pads];
		char[] padChars = padStr.toCharArray();
		for (int i = 0; i < pads; ++i) {
			padding[i] = padChars[(i % padLen)];
		}
		return new String(padding).concat(str);
	}

	public static String replaceOnce(String text, String searchString, String replacement) {
		return replace(text, searchString, replacement, 1);
	}

	public static String replace(String text, String searchString, String replacement, int max) {
		if ((isEmpty(text)) || (isEmpty(searchString)) || (replacement == null) || (max == 0)) {
			return text;
		}
		int start = 0;
		int end = text.indexOf(searchString, start);
		if (end == -1) {
			return text;
		}
		int replLength = searchString.length();
		int increase = replacement.length() - replLength;
		increase = (increase < 0) ? 0 : increase;
		increase *= ((max > 64) ? 64 : (max < 0) ? 16 : max);
		StringBuilder buf = new StringBuilder(text.length() + increase);
		while (end != -1) {
			buf.append(text.substring(start, end)).append(replacement);
			start = end + replLength;
			if (--max == 0) {
				break;
			}
			end = text.indexOf(searchString, start);
		}
		buf.append(text.substring(start));
		return buf.toString();
	}

	public static int indexOf(String seq, char searchSeq, int startPos) {
		if ((seq == null)) {
			return -1;
		}
		return seq.toString().indexOf(searchSeq, startPos);
	}
}