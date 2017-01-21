package net.will_co21.application.googleAPIImageSearch;

import java.util.ArrayList;
import java.util.List;

public class JavaScriptContentScanner implements IContentScanner {
	protected String contentText;
	protected char[] contentChars;
	protected IContentParser parser;

	public JavaScriptContentScanner(String contentText)
	{
		this.contentText = contentText;
		this.contentChars = contentText.toCharArray();
		this.parser = new JavaScriptParser();
	}
	@Override
	public List<String> getURLList(URLNormalizer urlNormalizer) {
		IContentToken token;
		int position = 0;
		int length = contentChars.length;

		ArrayList<String> urls = new ArrayList<String>();

		while(position < length)
		{
			token = parser.parse(contentText, contentChars, position, urlNormalizer);

			if(token instanceof URLToken)
			{
				urls.add(((URLToken) token).getURL());
				position = token.getNextPosition();
			}
			else if(token instanceof OtherToken)
			{
				position = token.getNextPosition();
			}
			else if(token instanceof ContentEOF)
			{
				break;
			}
			else
			{
				throw new RuntimeException("The type of token is illegal.");
			}
		}

		return urls;
	}
}
