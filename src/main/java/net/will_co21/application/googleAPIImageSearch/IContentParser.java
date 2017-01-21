package net.will_co21.application.googleAPIImageSearch;

public interface IContentParser {
	public IContentToken parse(String text, char[] textChars, int start, URLNormalizer urlNormalizer);
}
