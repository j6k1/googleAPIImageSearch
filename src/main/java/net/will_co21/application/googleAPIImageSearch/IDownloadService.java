package net.will_co21.application.googleAPIImageSearch;

import java.util.List;

public interface IDownloadService {
	public IDownloadCounter getCounter();
	public boolean download(String url, int depth);
	public boolean download(String url, int depth, boolean enforce);
	public void cansel();
	public void shutdown();
	public void onRequestCompleted();
	public boolean isCancelled();
	public void addAlreadyDownloads(String filename);
	public boolean alreadyDownload(String filename);
	public void restoreRequestedUrls(List<String> urls);
	public void resetAlreadyDownloads();
	public void resetRequestedUrls();
}
