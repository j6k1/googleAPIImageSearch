package net.will_co21.application.googleAPIImageSearch;

public interface IDownloadCounter {
	public int countUp();
	public int countDown();
	public int getCount();
	public void setCancelled(boolean cancelled);
	public boolean isCancelled();
}
