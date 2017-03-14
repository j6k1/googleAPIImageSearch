package net.will_co21.application.googleAPIImageSearch;

import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;

public interface IDownloadTask extends Runnable, ICanceled {
	public void onSuccess(HttpURLConnection con, String url,
							IImageReader imageReader, ISwingLogPrinter logPrinter,
							ILogger logger, IEnvironment environment, ISettings settings) throws SocketTimeoutException;
	public void onError(HttpURLConnection con, String url, ISwingLogPrinter logPrinter, ILogger logger, IEnvironment environment, ISettings settings);
	public void cansel();
	public String getUrl();
}
