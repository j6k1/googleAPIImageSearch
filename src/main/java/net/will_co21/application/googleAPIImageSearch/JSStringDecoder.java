package net.will_co21.application.googleAPIImageSearch;

import java.util.Arrays;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JSStringDecoder {
	private final static char[] unEscapeMap;
	static {
		unEscapeMap = new char[128];

		Arrays.fill(unEscapeMap, '\0');

		unEscapeMap[(int)'\''] = '\'';
		unEscapeMap[(int)'"'] = '"';
		unEscapeMap[(int)'\\'] = '\\';
		unEscapeMap[(int)'n'] = '\n';
		unEscapeMap[(int)'r'] = '\r';
		unEscapeMap[(int)'v'] = '\u000B';
		unEscapeMap[(int)'t'] = '\t';
		unEscapeMap[(int)'b'] = '\b';
		unEscapeMap[(int)'f'] = '\f';
	}

	public static int findStringEndNextPosition(String text, int index, char quote)
	{
		int quotePosition = text.indexOf(quote, index);
		int lfPosition = text.indexOf("\n", index);
		int crPosition = text.indexOf("\r", index);
		int length = text.length();

		return Math.min((quotePosition == - 1 ? length : quotePosition + 1),
					Math.min((lfPosition == -1 ? length : lfPosition + 1), (crPosition == -1 ? length : crPosition + 1)));
	}

	public static Pair<Result<String, Exception>, Integer> decode(String text, char[] textChars, int start) {
		int index = start;
		int currentStart = index;
		int length = text.length();

		char c = textChars[index];

		if(c != '"' && c!= '\'') throw new JSStringFormatErrorException("the position of input is illegal.");

		char quote = c;

		index++;

		if(index == length)
		{
			return new Pair<Result<String, Exception>, Integer>(
					Result.error(
						new JSStringFormatErrorException(
								"The format of this js string is not an js string format."),
							String.class),
					length);
		}

		currentStart++;

		StringBuilder sb = new StringBuilder();

		while(index < length && (c = textChars[index]) != quote)
		{
			if(c == '\\')
			{
				if(currentStart < index) sb.append(text.substring(currentStart, index));

				index++;

				if(index == length) return new Pair<Result<String, Exception>, Integer>(
											Result.error(new JSStringFormatErrorException(
												"The format of this js string is not an js string format."),
											String.class),
											findStringEndNextPosition(text, index, quote));

				c = textChars[index];

				if(c == 'x')
				{
					if(index + 2 >= length) return new Pair<Result<String, Exception>, Integer>(
												Result.error(new JSStringFormatErrorException(
													"The format of this js string is not an js string format.")
												, String.class),
												findStringEndNextPosition(text, index, quote));

					index++;

					int hexpos = index;

					while(hexpos < index + 2)
					{
						c = textChars[hexpos];

						if((c >= '0' && c <= '9') || (c >= 'A' && c <= 'F') || (c >= 'a' && c <= 'f')) hexpos++;
						else return new Pair<Result<String, Exception>, Integer>(
								Result.error(new JSStringFormatErrorException(
									"unexpected character \"" + c + "\" was found."
								), String.class)
								, findStringEndNextPosition(text, index, quote));
					}

					int code = Integer.parseInt(text.substring(index, index + 2), 16);

					sb.append((char)code);
					index += 2;

					currentStart = index;
				}
				else if(c == 'u' && index + 1 < length && textChars[index + 1] == '{')
				{
					int codeEndMarkIndex = text.indexOf('}', index + 1);

					if(codeEndMarkIndex == -1 || codeEndMarkIndex == index + 2 || codeEndMarkIndex > index + 8)
					{
						return new Pair<Result<String, Exception>, Integer>(
								Result.error(new JSStringFormatErrorException(
										"The format of this js string is not an js string format."
								), String.class)
								, findStringEndNextPosition(text, index, quote));
					}
					index += 2;

					int hexpos = index;

					while(hexpos < codeEndMarkIndex)
					{
						c = textChars[hexpos];

						if((c >= '0' && c <= '9') || (c >= 'A' && c <= 'F') || (c >= 'a' && c <= 'f')) hexpos++;
						else return new Pair<Result<String, Exception>, Integer>(
								Result.error(new JSStringFormatErrorException(
									"unexpected character \"" + c + "\" was found."
								), String.class)
								, findStringEndNextPosition(text, index, quote));
					}

					int code = Integer.parseInt(text.substring(index, codeEndMarkIndex), 16);

					if(code <= 0xFFFF)
					{
						sb.append((char)code);
					}
					else
					{
						code = code - 0x10000;

						int firstCode = code / 0x400 + 0xD800;
						int secondCode = (code % 0x400) + 0xDC00;
						sb.append(new String(new char[] { (char)firstCode, (char)secondCode }));
					}
					index = codeEndMarkIndex + 1;

					currentStart = index;
				}
				else if(c == 'u')
				{
					if(index + 4 >= length) return new Pair<Result<String, Exception>, Integer>(
												Result.error(new JSStringFormatErrorException(
														"The format of this js string is not an js string format."
													), String.class)
												, findStringEndNextPosition(text, index, quote));

					index++;

					int hexpos = index;

					while(hexpos < index + 4)
					{
						c = textChars[hexpos];

						if((c >= '0' && c <= '9') || (c >= 'A' && c <= 'F') || (c >= 'a' && c <= 'f')) hexpos++;
						else  return new Pair<Result<String, Exception>, Integer>(
								Result.error(new JSStringFormatErrorException(
									"unexpected character \"" + c + "\" was found."
								), String.class)
								, findStringEndNextPosition(text, index, quote));
					}

					int code = Integer.parseInt(text.substring(index, index + 4), 16);

					if(!(code >= 0xD800 && code <= 0xDBFF) || index + 4 == length)
					{
						sb.append((char)code);
						index += 4;
					}
					else if(index + 5 >= length || c != '\\' || textChars[index + 5] != 'u')
					{
						sb.append((char)code);
						index += 4;
					}
					else
					{
						index += 6;

						if(index + 3 >= length)
							return new Pair<Result<String, Exception>, Integer>(
									Result.error(new JSStringFormatErrorException(
										"The format of this js string is not an js string format.")
									, String.class)
									, findStringEndNextPosition(text, index, quote));

						c = textChars[index];

						hexpos = index;

						while(hexpos < index + 4)
						{
							c = textChars[hexpos];

							if((c >= '0' && c <= '9') || (c >= 'A' && c <= 'F') || (c >= 'a' && c <= 'f')) hexpos++;
							else  return new Pair<Result<String, Exception>, Integer>(
									Result.error(new JSStringFormatErrorException(
										"unexpected character \"" + c + "\" was found."
									), String.class)
									, findStringEndNextPosition(text, index, quote));
						}

						int secondCode = Integer.parseInt(text.substring(index, index + 4), 16);

						if(secondCode < 0xDC00 || secondCode > 0xDFFF)
						{
							sb.append((char)code);
							sb.append((char)secondCode);
						}
						else
						{
							sb.append(new String(new char[] { (char)code, (char)secondCode }));
						}
						index += 4;
					}

					currentStart = index;
				}
				else if(c == '0')
				{
					sb.append('\0');
					index++;
					currentStart = index;
				}
				else if(unEscapeMap[(int)c] != '\0')
				{
					sb.append(unEscapeMap[(int)c]);
					index++;
					currentStart = index;
				}
				else
				{
					 return new Pair<Result<String, Exception>, Integer>(
								Result.error(new JSStringFormatErrorException(
									"unexpected character \"" + c + "\" was found."
								), String.class)
							 , findStringEndNextPosition(text, index, quote));
				}
			}
			else
			{
				index++;
			}
		}

		if(currentStart < index) sb.append(text.substring(currentStart, index));

		if(index == length)  return new Pair<Result<String, Exception>, Integer>(
								Result.error(new JSStringFormatErrorException(
									"The format of this js string is not an js string format."),
								String.class)
								, findStringEndNextPosition(text, index, quote));

		c = textChars[index];

		if(c != quote) return new Pair<Result<String, Exception>, Integer>(
						Result.error(new JSStringFormatErrorException(
							"unexpected character \"" + c + "\" was found."
						), String.class)
						, findStringEndNextPosition(text, index, quote));

		index++;

		return new Pair<Result<String, Exception>, Integer>(Result.of(sb.toString()), index);
	}

}
