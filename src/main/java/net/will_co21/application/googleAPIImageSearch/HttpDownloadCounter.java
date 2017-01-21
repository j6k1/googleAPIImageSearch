package net.will_co21.application.googleAPIImageSearch;

public class HttpDownloadCounter implements IDownloadCounter {
	protected int count;
	protected Runnable onCompleted;

	public HttpDownloadCounter(Runnable onCompleted)
	{
		this.count = 0;
		this.onCompleted = onCompleted;
	}
	@Override
	public synchronized int countUp() {
		this.count++;
		return this.count;
	}

	@Override
	public synchronized int countDown() {
		this.count--;
		if(this.count == 0) onCompleted.run();
		return this.count;
	}

	@Override
	public int getCount() {
		return this.count;
	}
}
