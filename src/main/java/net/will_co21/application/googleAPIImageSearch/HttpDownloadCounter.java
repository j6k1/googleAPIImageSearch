package net.will_co21.application.googleAPIImageSearch;

public class HttpDownloadCounter implements IDownloadCounter {
	protected volatile int count;
	protected volatile boolean cancelled;
	protected IOnSearchRequestCompleted onCompleted;

	public HttpDownloadCounter(IOnSearchRequestCompleted onCompleted)
	{
		this.count = 0;
		this.cancelled = false;
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
		if(this.count == 0) onCompleted.onSearchRequestCompleted(this.cancelled);
		return this.count;
	}

	@Override
	public int getCount() {
		return this.count;
	}

	@Override
	public synchronized void setCancelled(boolean cancelled)
	{
		this.cancelled = cancelled;
	}

	@Override
	public boolean isCancelled()
	{
		return this.cancelled;
	}
}
