package net.will_co21.application.googleAPIImageSearch;

public class URLToken implements IContentToken {
	protected int position;
	protected int nextPosition;
	protected String url;

	public URLToken(int position, int nextPosition, String url)
	{
		this.position = position;
		this.nextPosition = nextPosition;
		this.url = url;
	}

	@Override
	public int getCurrentPosition() {
		return this.position;
	}

	@Override
	public int getNextPosition() {
		return this.nextPosition;
	}

	public String getURL()
	{
		return this.url;
	}
}
