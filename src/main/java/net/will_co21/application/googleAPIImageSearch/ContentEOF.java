package net.will_co21.application.googleAPIImageSearch;

public class ContentEOF implements IContentToken {
	public ContentEOF()
	{

	}

	@Override
	public int getCurrentPosition() {
		return -1;
	}

	@Override
	public int getNextPosition() {
		return -1;
	}
}
