package com.github.bordertech.wcomponents.util;

import java.io.UnsupportedEncodingException;

/**
 * A Base64 Encoder/Decoder.
 * <p>
 * This class is used to encode and decode data in Base64 format as described in RFC 1521.
 * <p>
 * This is "Open Source" software and released under the
 * <a href="http://www.gnu.org/licenses/lgpl.html">GNU/LGPL </a>
 * license. <br>
 * It is provided "as is" without warranty of any kind. <br>
 * Copyright 2003: Christian d'Heureuse, Inventec Informatik AG, Switzerland.
 * <br>
 * Home page: <a href="http://www.source-code.biz">www.source-code.biz </a> <br>
 * <p>
 * Original name <b>Base64Coder.java </b>
 * <p>
 * Version history: <br>
 * 2003-07-22 Christian d'Heureuse (chdh): Module created. <br>
 * 2005-08-11 chdh: Lincense changed from GPL to LGPL. <br>
 * 2006-11-21 chdh: <br>
 * &nbsp; Method encode(String) renamed to encodeString(String). <br>
 * &nbsp; Method decode(String) renamed to decodeString(String). <br>
 * &nbsp; New method encode(byte[],int) added. <br>
 * &nbsp; New method decode(String) added. <br>
 * 2007-04-30 francis.naoum: Added to sfp_lib. <br>
 * &nbsp; byte[] encode(byte[]) changed to return a String. <br>
 *
 * @author Francis Naoum
 * @since 1.0.0
 */
public final class Base64Util {

	/**
	 * Mapping table from 6-bit nibbles to Base64 characters.
	 */
	private static final char[] MAP1 = new char[64];

	static {
		int i = 0;

		for (char c = 'A'; c <= 'Z'; c++) {
			MAP1[i++] = c;
		}

		for (char c = 'a'; c <= 'z'; c++) {
			MAP1[i++] = c;
		}

		for (char c = '0'; c <= '9'; c++) {
			MAP1[i++] = c;
		}

		MAP1[i++] = '+';
		MAP1[i++] = '/';
	}

	/**
	 * Mapping table from Base64 characters to 6-bit nibbles.
	 */
	private static final byte[] MAP2 = new byte[128];

	static {
		for (int i = 0; i < MAP2.length; i++) {
			MAP2[i] = -1;
		}

		for (int i = 0; i < 64; i++) {
			MAP2[MAP1[i]] = (byte) i;
		}
	}

	/**
	 * Encodes a string into Base64 format. No blanks or line breaks are inserted.
	 *
	 * @param string a String to be encoded.
	 * @return A String with the Base64 encoded data.
	 */
	public static String encodeString(final String string) {
		String encodedString = null;

		try {
			encodedString = encodeString(string, "UTF-8");
		} catch (UnsupportedEncodingException uue) {
			// Should never happen, java has to support "UTF-8".
		}

		return encodedString;
	}

	/**
	 * Encodes a string into Base64 format. No blanks or line breaks are inserted.
	 *
	 * @param string a String to be encoded.
	 * @param encoding The character encoding of the string.
	 * @return A String with the Base64 encoded data.
	 *
	 * @throws UnsupportedEncodingException if the java runtime does not support <code>encoding</code>.
	 */
	public static String encodeString(final String string, final String encoding) throws
			UnsupportedEncodingException {
		byte[] stringBytes = string.getBytes(encoding);
		return encode(stringBytes);
	}

	/**
	 * Encodes a byte array into Base64 format. No blanks or line breaks are inserted.
	 * <p>
	 * This method has been modified to return a string not a char[].
	 *
	 * @param input an array containing the data bytes to be encoded.
	 * @return A character array with the Base64 encoded data.
	 */
	public static String encode(final byte[] input) {
		char[] chars = encode(input, input.length);
		return new String(chars);
	}

	/**
	 * Encodes a byte array into Base64 format. No blanks or line breaks are inserted.
	 *
	 * @param input an array containing the data bytes to be encoded.
	 * @param iLen number of bytes to process in <code>in</code>.
	 * @return A character array with the Base64 encoded data.
	 */
	public static char[] encode(final byte[] input, final int iLen) {
		int oDataLen = (iLen * 4 + 2) / 3; // output length without padding
		int oLen = ((iLen + 2) / 3) * 4; // output length including padding
		char[] out = new char[oLen];
		int ip = 0;
		int op = 0;

		while (ip < iLen) {
			int i0 = input[ip++] & 0xff;
			int i1 = ip < iLen ? input[ip++] & 0xff : 0;
			int i2 = ip < iLen ? input[ip++] & 0xff : 0;
			int o0 = i0 >>> 2;
			int o1 = ((i0 & 3) << 4) | (i1 >>> 4);
			int o2 = ((i1 & 0xf) << 2) | (i2 >>> 6);
			int o3 = i2 & 0x3F;
			out[op++] = MAP1[o0];
			out[op++] = MAP1[o1];
			out[op] = op < oDataLen ? MAP1[o2] : '=';
			op++;
			out[op] = op < oDataLen ? MAP1[o3] : '=';
			op++;
		}

		return out;
	}

	/**
	 * Decodes a string from Base64 format.
	 *
	 * @param string a Base64 String to be decoded.
	 * @return A String containing the decoded data.
	 * @throws IllegalArgumentException if the input is not valid Base64 encoded data.
	 */
	public static String decodeString(final String string) {
		String decodedString = null;

		try {
			byte[] decodedBytes = decode(string);
			decodedString = new String(decodedBytes, "UTF-8");
		} catch (UnsupportedEncodingException uue) {
			// Should never happen, java has to support "UTF-8".
		}

		return decodedString;
	}

	/**
	 * Decodes a byte array from Base64 format.
	 *
	 * @param string a Base64 String to be decoded.
	 * @return An array containing the decoded data bytes.
	 * @throws IllegalArgumentException if the input is not valid Base64 encoded data.
	 */
	public static byte[] decode(final String string) {
		return decode(string.toCharArray());
	}

	/**
	 * Decodes a byte array from Base64 format. No blanks or line breaks are allowed within the Base64 encoded data.
	 *
	 * @param input a character array containing the Base64 encoded data.
	 * @return An array containing the decoded data bytes.
	 * @throws IllegalArgumentException if the input is not valid Base64 encoded data.
	 */
	public static byte[] decode(final char[] input) {
		int iLen = input.length;

		if (iLen % 4 != 0) {
			throw new IllegalArgumentException(
					"Length of Base64 encoded input string is not a multiple of 4.");
		}

		while (iLen > 0 && input[iLen - 1] == '=') {
			iLen--;
		}

		int oLen = (iLen * 3) / 4;
		byte[] out = new byte[oLen];
		int ip = 0;
		int op = 0;

		while (ip < iLen) {
			int i0 = input[ip++];
			int i1 = input[ip++];
			int i2 = ip < iLen ? input[ip++] : 'A';
			int i3 = ip < iLen ? input[ip++] : 'A';

			if (i0 > 127 || i1 > 127 || i2 > 127 || i3 > 127) {
				throw new IllegalArgumentException("Illegal character in Base64 encoded data.");
			}

			int b0 = MAP2[i0];
			int b1 = MAP2[i1];
			int b2 = MAP2[i2];
			int b3 = MAP2[i3];

			if (b0 < 0 || b1 < 0 || b2 < 0 || b3 < 0) {
				throw new IllegalArgumentException("Illegal character in Base64 encoded data.");
			}

			int o0 = (b0 << 2) | (b1 >>> 4);
			int o1 = ((b1 & 0xf) << 4) | (b2 >>> 2);
			int o2 = ((b2 & 3) << 6) | b3;
			out[op++] = (byte) o0;

			if (op < oLen) {
				out[op++] = (byte) o1;
			}

			if (op < oLen) {
				out[op++] = (byte) o2;
			}
		}

		return out;
	}

	/**
	 * Dummy constructor.
	 */
	private Base64Util() {
	}
}
