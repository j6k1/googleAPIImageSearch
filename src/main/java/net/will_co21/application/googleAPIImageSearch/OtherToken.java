package net.will_co21.application.googleAPIImageSearch;

public class OtherToken implements IContentToken {
	protected int position;
	protected int nextPosition;

	public OtherToken(int position, int nextPosition)
	{
		this.position = position;
		this.nextPosition = nextPosition;
	}

	@Override
	public int getCurrentPosition() {
		return this.position;
	}

	@Override
	public int getNextPosition() {
		return this.nextPosition;
	}
}
