package net.will_co21.application.googleAPIImageSearch;

import java.net.HttpURLConnection;

public interface IOnHttpDownloadErrorListener {
	public void onError(HttpURLConnection con, String url, ISwingLogPrinter logPrinter, ILogger logger, IEnvironment environment, ISettings settings);
}
