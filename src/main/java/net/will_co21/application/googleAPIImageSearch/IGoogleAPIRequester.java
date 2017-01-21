package net.will_co21.application.googleAPIImageSearch;

import java.util.List;

public interface IGoogleAPIRequester {
	public void request(IDownloadService downloader) throws Exception;
	public void reset();
	public void cancel();
}
