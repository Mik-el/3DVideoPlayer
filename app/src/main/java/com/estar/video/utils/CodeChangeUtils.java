package com.estar.video.utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.Character.UnicodeBlock;

import android.os.Environment;
import android.util.Log;
/**
 * 文本转码工具类
 * @author zgl
 *
 */
public class CodeChangeUtils {

	public static void CheckCodeMode(String str_filepath) {

	}

	public static String JudgeString(String string) {
		try {
			if (string.trim().equals(
					new String(string.trim().getBytes("UTF-8")))) {
				return "UTF-8";
			} else if (string.trim().equals(
					new String(string.trim().getBytes("ISO-8859-1")))) {
				return "ISO-8859-1";
			} else if (string.trim().equals(
					new String(string.trim().getBytes("US-ASCII")))) {
				return "US-ASCII";
			} else if (string.trim().equals(
					new String(string.trim().getBytes("UTF-16BE")))) {
				return "UTF-16BE";
			} else if (string.trim().equals(
					new String(string.trim().getBytes("unicode")))) {
				return "unicode";
			} else if (string.trim().equals(
					new String(string.trim().getBytes("GBK")))) {
				return "GBK";
			}
		} catch (Exception e) {
			Log.d("liudong", "start thr2222" + e.toString());
			e.printStackTrace();
		}
		return null;
	}

	public static void convertCodeAndGetText(String str_filepath) {// 转码

		File file = new File(str_filepath);
		BufferedReader reader;
		try {
			FileInputStream fis = new FileInputStream(file);
			BufferedInputStream in = new BufferedInputStream(fis);
			in.mark(4);
			byte[] first3bytes = new byte[3];
			in.read(first3bytes);// 找到文档的前三个字节并自动判断文档类型。
			in.reset();
			Log.d("liudong", "first3bytes[0]=" + first3bytes[0]
					+ "first3bytes[1]=" + first3bytes[1] + "first3bytes[2]="
					+ first3bytes[2]);
			if (first3bytes[0] == (byte) 0xEF && first3bytes[1] == (byte) 0xBB
					&& first3bytes[2] == (byte) 0xBF) {// utf-8
				reader = new BufferedReader(new InputStreamReader(in, "utf-8"));
			} else if (first3bytes[0] == (byte) 0xFF
					&& first3bytes[1] == (byte) 0xFE) {
				reader = new BufferedReader(
						new InputStreamReader(in, "unicode"));
			} else if (first3bytes[0] == (byte) 0xFE
					&& first3bytes[1] == (byte) 0xFF) {
				reader = new BufferedReader(new InputStreamReader(in,
						"utf-16be"));
			} else if (first3bytes[0] == (byte) 0xFF
					&& first3bytes[1] == (byte) 0xFF) {
				reader = new BufferedReader(new InputStreamReader(in,
						"utf-16le"));
			} else {
				reader = new BufferedReader(new InputStreamReader(in, "GBK"));
			}
			StringBuffer content = new StringBuffer();
			String str;
			String line_separator = System.getProperty("line.separator");
			while ((str = reader.readLine()) != null) {
				content.append(str + line_separator);
			}
			// Log.d("liudong", "content="+content);
			reader.close();
			File outFile = new File(Environment.getExternalStorageDirectory()
					+ "/Subtitle.srt");
			if (outFile.exists()) {
				outFile.delete();
			}
			Writer ow = new OutputStreamWriter(
					new FileOutputStream(
							Environment.getExternalStorageDirectory()
									+ "/Subtitle.srt"), "utf-8");
			ow.write(content.toString());
			ow.close();
		} catch (Exception e) {
			Log.d("liudong", "erro!!!!!!!!!!!!");
			e.printStackTrace();
		}
	}

	public String gbk2utf8(String gbk) {
		String l_temp = GBK2Unicode(gbk);
		l_temp = unicodeToUtf8(l_temp);

		return l_temp;
	}

	public String utf82gbk(String utf) {
		String l_temp = utf8ToUnicode(utf);
		l_temp = Unicode2GBK(l_temp);
		return l_temp;
	}

	/**
	 * 
	 * @param str
	 * @return String
	 */

	public static String GBK2Unicode(String str) {
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < str.length(); i++) {
			char chr1 = (char) str.charAt(i);

			if (!isNeedConvert(chr1)) {
				result.append(chr1);
				continue;
			}

			result.append("\\u" + Integer.toHexString((int) chr1));
		}

		return result.toString();
	}

	/**
	 * 
	 * @param dataStr
	 * @return String
	 */

	public static String Unicode2GBK(String dataStr) {
		int index = 0;
		StringBuffer buffer = new StringBuffer();

		int li_len = dataStr.length();
		while (index < li_len) {
			if (index >= li_len - 1
					|| !"\\u".equals(dataStr.substring(index, index + 2))) {
				buffer.append(dataStr.charAt(index));

				index++;
				continue;
			}

			String charStr = "";
			charStr = dataStr.substring(index + 2, index + 6);

			char letter = (char) Integer.parseInt(charStr, 16);

			buffer.append(letter);
			index += 6;
		}

		return buffer.toString();
	}

	public static boolean isNeedConvert(char para) {
		return ((para & (0x00FF)) != para);
	}

	/**
	 * utf-8 转unicode
	 * 
	 * @param inStr
	 * @return String
	 */
	public static String utf8ToUnicode(String inStr) {
		char[] myBuffer = inStr.toCharArray();

		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < inStr.length(); i++) {
			UnicodeBlock ub = UnicodeBlock.of(myBuffer[i]);
			if (ub == UnicodeBlock.BASIC_LATIN) {
				sb.append(myBuffer[i]);
			} else if (ub == UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
				int j = (int) myBuffer[i] - 65248;
				sb.append((char) j);
			} else {
				short s = (short) myBuffer[i];
				String hexS = Integer.toHexString(s);
				String unicode = "\\u" + hexS;
				sb.append(unicode.toLowerCase());
			}
		}
		return sb.toString();
	}

	/**
	 * 
	 * @param theString
	 * @return String
	 */
	public static String unicodeToUtf8(String theString) {
		char aChar;
		int len = theString.length();
		StringBuffer outBuffer = new StringBuffer(len);
		for (int x = 0; x < len;) {
			aChar = theString.charAt(x++);
			if (aChar == '\\') {
				aChar = theString.charAt(x++);
				if (aChar == 'u') {
					// Read the xxxx
					int value = 0;
					for (int i = 0; i < 4; i++) {
						aChar = theString.charAt(x++);
						switch (aChar) {
						case '0':
						case '1':
						case '2':
						case '3':
						case '4':
						case '5':
						case '6':
						case '7':
						case '8':
						case '9':
							value = (value << 4) + aChar - '0';
							break;
						case 'a':
						case 'b':
						case 'c':
						case 'd':
						case 'e':
						case 'f':
							value = (value << 4) + 10 + aChar - 'a';
							break;
						case 'A':
						case 'B':
						case 'C':
						case 'D':
						case 'E':
						case 'F':
							value = (value << 4) + 10 + aChar - 'A';
							break;
						default:
							throw new IllegalArgumentException(
									"Malformed   \\uxxxx   encoding.");
						}
					}
					outBuffer.append((char) value);
				} else {
					if (aChar == 't')
						aChar = '\t';
					else if (aChar == 'r')
						aChar = '\r';
					else if (aChar == 'n')
						aChar = '\n';
					else if (aChar == 'f')
						aChar = '\f';
					outBuffer.append(aChar);
				}
			} else
				outBuffer.append(aChar);
		}
		return outBuffer.toString();
	}

}
