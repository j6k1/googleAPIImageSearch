package net.will_co21.application.googleAPIImageSearch;

import java.util.ArrayList;
import java.util.List;

public class HtmlContentScanner implements IContentScanner {
	protected String contentText;
	protected char[] contentChars;
	protected IContentParser parser;

	public HtmlContentScanner(String contentText)
	{
		this.contentText = contentText;
		this.contentChars = contentText.toCharArray();
		this.parser = new HtmlParser();
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
				position = token.getCurrentPosition();

				if(contentText.startsWith("<script ", position) || contentText.startsWith("<script>", position))
				{
					position = contentText.indexOf('>', position + 1);

					if(position == -1) break;
					else
					{
						parser = new JavaScriptParser(new String[] { "</script>" });
					}
				}
				else if(contentText.startsWith("</script>", position))
				{
					parser = new HtmlParser();
				}

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
