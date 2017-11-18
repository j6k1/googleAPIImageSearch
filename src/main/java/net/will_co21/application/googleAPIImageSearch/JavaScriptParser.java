package net.will_co21.application.googleAPIImageSearch;

import java.util.Arrays;
import java.util.regex.Pattern;

public class JavaScriptParser implements IContentParser {
	protected final static boolean[] whiteSpaceTable;
	protected static final Pattern numberRegexp = Pattern.compile("^(?:[1-9]\\d*|0)(?:\\.(?:\\d+))?$");
	protected final String[] endTokens;

	static {
		whiteSpaceTable = new boolean[128];

		Arrays.fill(whiteSpaceTable, false);

		whiteSpaceTable[(int)' '] = true;
		whiteSpaceTable[(int)'\t'] = true;
		whiteSpaceTable[(int)'\n'] = true;
		whiteSpaceTable[(int)'\r'] = true;
	}

	public JavaScriptParser()
	{
		this(new String[] {});
	}

	public JavaScriptParser(String[] endTokens)
	{
		this.endTokens = endTokens;
	}

	@Override
	public IContentToken parse(String text, char[] textChars, int start, URLNormalizer urlNormalizer) {
		int startPosition = skipWhiteSpace(textChars, start);
		int position = startPosition;

		if(position == textChars.length) return new ContentEOF();

		if(textChars[position] == '"' || textChars[position] == '\'')
		{
			StringBuilder sb = new StringBuilder();

			Pair<Result<String, Exception>, Integer> pair;

			pair = JSStringDecoder.decode(text, textChars, position);

			boolean hasError = false;

			boolean foundNewLine = false;

			if(!pair.fst.hasResult()) hasError = true;
			else pair.fst.ifHasResult((str) -> sb.append(str));

			position = pair.snd;

			int next = position;

			while(position < textChars.length)
			{
				next = skipWhiteSpace(textChars, position, false);
				boolean hasAppended = false;

				if(textChars[next] == '+' && next < textChars.length - 1 && textChars[next + 1] == '=')
				{
					next += 2;
					hasAppended = true;
				}
				else if(textChars[next] == '+')
				{
					next += 1;
					hasAppended = true;
				}

				if(hasAppended)
				{
					foundNewLine = false;

					position = skipWhiteSpace(textChars, next);

					if(position == textChars.length)
					{
						hasError = true;
						break;
					}
					else if(isEndToken(text, position))
					{
						position += 1;
						hasError = true;
						break;
					}
					else if(textChars[position] >= '0' && textChars[position] <= '9')
					{
						int numberEnd = findNumberEnd(textChars, position + 1);

						String strNumber = text.substring(position, numberEnd);

						if(!numberRegexp.matcher(strNumber).find())
						{
							hasError = true;
							position = numberEnd;
						}
						else
						{
							position = numberEnd;
							sb.append(strNumber);
						}
					}
					else if(textChars[position] == '"' || textChars[position] == '\'')
					{
						pair = JSStringDecoder.decode(text, textChars, position);

						if(!pair.fst.hasResult())
						{
							hasError = true;
							position = pair.snd;
						}
						else
						{
							pair.fst.ifHasResult((str) -> sb.append(str));
							position = pair.snd;
						}
					}
					else
					{
						position += 1;
						hasError = true;
					}
				}
				else if(textChars[next] == '\n' || textChars[next] == '\r')
				{
					position = next + 1;
					foundNewLine = true;
				}
				else if(textChars[next] == ';' || textChars[next] == ',')
				{
					position = next + 1;
					break;
				}
				else if(foundNewLine)
				{
					position = (next == startPosition)? next + 1 : next;
					break;
				}
				else
				{
					position = (next == startPosition)? next + 1 : next;
					hasError = true;
					break;
				}
			}

			if(hasError)
			{
				if(position == textChars.length - 1) return new ContentEOF();
				else return new OtherToken(startPosition, position);
			}
			else
			{
				String url = URLNormalizer.decodeHtmlEntity(sb.toString());
				if(UrlRegExp.pattern.matcher(url).find()) return new URLToken(startPosition, position, url);
				else if(position == textChars.length - 1) return new ContentEOF();
				else return new OtherToken(startPosition, position);
			}
		}
		else if(position < textChars.length)
		{
			return new OtherToken(startPosition, startPosition + 1);
		}
		else
		{
			return new ContentEOF();
		}
	}

	protected int skipWhiteSpace(char[] textChars, int start)
	{
		return skipWhiteSpace(textChars, start, true);
	}

	protected int skipWhiteSpace(char[] textChars, int start, boolean skipNewLine)
	{
		for(int i = start, length = textChars.length; i < length; i++)
		{
			if(textChars[i] >= 128) return i;
			else if(!whiteSpaceTable[(int)textChars[i]]) return i;
			else if(!skipNewLine && (textChars[i] == '\n' || textChars[i] == '\r')) return i;
		}

		return textChars.length;
	}

	protected int findNumberEnd(char[] textChars, int start)
	{
		for(int i = start, length = textChars.length; i < length; i++)
		{
			if(textChars[i] != '.' && (textChars[i] < '0' || textChars[i] > '9')) return i;
		}

		return textChars.length;
	}

	protected boolean isEndToken(String text, int start)
	{
		for(String token: endTokens) if(text.startsWith(token, start)) return true;

		return false;
	}
}
