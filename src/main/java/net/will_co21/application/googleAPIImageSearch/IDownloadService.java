package net.will_co21.application.googleAPIImageSearch;

public interface IDownloadService {
	public IDownloadCounter getCounter();
	public void download(String url, int depth);
	public void cancel();
}
