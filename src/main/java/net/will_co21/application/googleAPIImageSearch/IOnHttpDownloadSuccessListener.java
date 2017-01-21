package net.will_co21.application.googleAPIImageSearch;

import java.net.HttpURLConnection;

public interface IOnHttpDownloadSuccessListener {
	public void onSuccess(HttpURLConnection con, String url,
							IImageReader imageReader, ISwingLogPrinter logPrinter, ILogger logger, IEnvironment environment, ISettings settings);
}
