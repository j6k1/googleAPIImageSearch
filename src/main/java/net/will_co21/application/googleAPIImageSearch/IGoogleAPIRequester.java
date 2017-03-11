package net.will_co21.application.googleAPIImageSearch;

import java.util.List;

public interface IGoogleAPIRequester extends IOnSearchRequestCompleted {
	public void request(IDownloadService downloader, boolean changeKeyword) throws Exception;
	public void setKeyword(String keyword);
	public void reset();
	public void cancel();
	public void shutdown();
	public void addCacheImage(SavedImageInfo info);
}
