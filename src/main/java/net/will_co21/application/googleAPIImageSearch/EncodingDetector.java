package net.will_co21.application.googleAPIImageSearch;

import java.util.HashMap;

public class EncodingDetector {
	protected static final HashMap<String, String> encMap = new HashMap<String, String>() {/**
		 *
		 */
		private static final long serialVersionUID = -1372871172652855245L;

	{
		put("utf-8", "UTF-8");
		put("utf_8", "UTF-8");
		put("utf8", "UTF-8");
		put("sjis", "Shift_JIS");
		put("shift_jis", "Shift_JIS");
		put("shift-jis", "Shift_JIS");
		put("euc_jp", "EUC_JP");
		put("eucjp", "EUC_JP");
		put("iso-8859-1", "ISO-8859-1");
		put("iso88591", "ISO-8859-1");
		put("iso-2022-jp", "ISO-2022-JP");
		put("iso2022jp	", "ISO-2022-JP");
	}};

	public static String getEncoding(String contentType, byte[] data)
	{
		int separatorPosition = -1;
		int charSetStart = -1;
		String encoding = null;

		if(contentType != null && (separatorPosition = contentType.indexOf(';')) !=-1 &&
				(charSetStart = contentType.toLowerCase().indexOf("charset=")) != -1 && separatorPosition < charSetStart)
		{
			String charset = contentType.substring(charSetStart + "charset=".length(), contentType.length());

			if(encMap.containsKey(charset.toLowerCase()))
			{
				encoding = encMap.get(charset.toLowerCase());
			}
		}

		if(encoding != null)
		{
			return encoding;
		}
		else if(data.length < 2)
		{
			return "JISAutoDetect";
		}
		else if(data[0] == 0xFE && data[1] == 0xFF)
		{
			return "UTF-16BE";
		}
		else if(data.length >= 4 && data[0] == 0xFF && data[1] == 0xFE && data[2] == 0x00 && data[3] == 0x00)
		{
			return "UTF-32LE";
		}
		else if(data[0] == 0xFF && data[1] == 0xFE)
		{
			return "UTF-16LE";
		}
		else if(data.length >= 3 && data[0] == 0xEF && data[1] == 0xBB && data[2] == 0xBF)
		{
			return "UTF-8";
		}
		else if(data.length >= 4 && data[0] == 0x00 && data[1] == 0x00 && data[2] == 0xFE && data[3] == 0xFF)
		{
			return "UTF-32BE";
		}
		else
		{
			return "JISAutoDetect";
		}
	}
}
