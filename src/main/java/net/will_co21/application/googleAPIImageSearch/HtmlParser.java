package net.will_co21.application.googleAPIImageSearch;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.management.RuntimeErrorException;

public class HtmlParser implements IContentParser {
	protected static final Pattern regexPattern = Pattern.compile(
			"(?:(?:\\<img .*?src|<a .*?href)=(\"(?:\\\"|.)*?\").*?>)|" +
			"(?:(?:\\<img .*?src|<a .*?href)=('(?:\\\"|.)*?').*?>)|" +
			"(?:(?:\\<script .*?src)=(\"(?:\\\"|.)*?\").*?>)|" +
			"(?:(?:\\<script .*?src)=('(?:\\\"|.)*?').*?>)|" +
			"(<script .*?>|<script>)|" +
			"(https?://([\\-._~@:a-zA-Z0-9!\\$()*+,;=]|(%[0-9A-F]{2}))+([\\/?#]([\\-._~@:a-zA-Z0-9!\\$&()*+,;=\\/?\\[\\]#\\|])*)?)");
	protected Matcher matcher = null;

	@Override
	public IContentToken parse(String text, char[] textChars, int start, URLNormalizer urlNormalizer) {
		if(matcher == null) matcher = regexPattern.matcher(text);

		if(!matcher.find(start))
		{
			return new ContentEOF();
		}
		else if(matcher.group(0).equals(""))
		{
			throw new RuntimeException("The matched string is an empty string. There is an error in the regular expression pattern.");
		}
		else if(matcher.group(1) != null)
		{
			String url = urlNormalizer.normalizedUrl(decodeHtmlString(matcher.group(1), 0, '"'));

			if(UrlRegExp.pattern.matcher(url).find()) return new URLToken(matcher.start(1), matcher.end(1), url);
			else if(matcher.end() == textChars.length - 1) return new ContentEOF();
			else return new OtherToken(matcher.start(1), matcher.end(1));
		}
		else if(matcher.group(2) != null)
		{
			String url = urlNormalizer.normalizedUrl(decodeHtmlString(matcher.group(2), 0, '\''));

			if(UrlRegExp.pattern.matcher(url).find()) return new URLToken(matcher.start(2), matcher.end(2), url);
			else if(matcher.end() == textChars.length - 1) return new ContentEOF();
			else return new OtherToken(matcher.start(2), matcher.end(2));
		}
		else if(matcher.group(3) != null)
		{
			String url = urlNormalizer.normalizedUrl(decodeHtmlString(matcher.group(3), 0, '"'));
			if(UrlRegExp.pattern.matcher(url).find()) return new URLToken(matcher.start(3), matcher.end(3), url);
			else if(matcher.end() == textChars.length - 1) return new ContentEOF();
			else return new OtherToken(matcher.start(3), matcher.end(3));
		}
		else if(matcher.group(4) != null)
		{
			String url = urlNormalizer.normalizedUrl(decodeHtmlString(matcher.group(4), 0, '\''));

			if(UrlRegExp.pattern.matcher(url).find()) return new URLToken(matcher.start(4), matcher.end(4), url);
			else if(matcher.end() == textChars.length - 1) return new ContentEOF();
			else return new OtherToken(matcher.start(4), matcher.end(4));
		}
		else if(matcher.group(5) != null)
		{
			if(matcher.end(5) == textChars.length - 1) return new ContentEOF();
			else return new OtherToken(matcher.start(5), matcher.end(5));
		}
		else if(matcher.group(6) != null)
		{
			return new URLToken(matcher.start(6), matcher.end(6), matcher.group(6));
		}
		else
		{
			throw new RuntimeException("It seems that there is a problem with regular expressions.");
		}
	}

	protected String decodeHtmlString(String text, int start, char quote)
	{
		int stringStart = text.indexOf(quote, start);

		if(stringStart == -1 || stringStart == text.length() - 1) throw new RuntimeException("The input is illegal.");

		int stringEnd = text.indexOf(quote, stringStart + 1);

		while(stringEnd != -1 && text.charAt(stringEnd - 1) == '\\')
		{
			if(stringEnd == text.length() - 1)
			{
				stringEnd = -1;
				break;
			}

			stringEnd = text.indexOf(quote, stringEnd + 1);
		}

		if(stringEnd == -1) throw new RuntimeException("The input is illegal.");

		if(stringEnd == stringStart + 1) return "";

		String stringBody = text.substring(stringStart + 1, stringEnd);

		return stringBody.replaceAll("\\\\\"|&quot;", "\"")
				.replaceAll("\\\\'|&apos;", "'")
				.replaceAll("&lt;", "<")
				.replaceAll("&gt;", ">")
				.replaceAll("&amp;", "&");
	}
}
