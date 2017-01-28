package net.will_co21.application.googleAPIImageSearch;

import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;

public class HttpDownloadTaskConsumer implements Consumer<HttpDownloadTask> {
	protected final BiFunction<String, ILogger, Optional<HttpURLConnection>> delegate = new HttpDownloadDelegate();

	public void accept(HttpDownloadTask task)
	{
		task.connection = null;

		if(task.isCancelled()) return;

		Optional<HttpURLConnection> connection = delegate.apply(task.url, new HttpConnectLogger(task.url, task.logger, task.logPrinter));
		if(task.isCancelled()) return;

		connection.ifPresent(con -> {
			task.connection = con;
			try {
				int status = task.connection.getResponseCode();
				if(status >= 200 && status < 300) task.onSuccess(task.connection, task.url,
														task.imageReader, task.logPrinter, task.logger, task.environment, task.settings);
				else task.onError(task.connection, task.url, task.logPrinter, task.logger, task.environment, task.settings);
			} catch (SocketTimeoutException e) {
				task.logger.write(String.format("通信タイムアウト発生: url = %s", task.url));
				task.logPrinter.print(String.format("通信タイムアウト発生: url = %s", task.url));
			} catch (Exception e) {
				task.logger.write(e);
			} catch (Error e) {
				task.logger.write(String.format("致命的な例外: %s, message = %s", e.getClass().getName(), e.getMessage()));
				task.logger.write(e);
			} catch (Throwable t) {
				task.logger.write(String.format("ExceptionでもErrorでもない例外がスローされました。 %s, message = %s", t.getClass().getName(), t.getMessage()));
				task.logger.write(t);
			}
		});
	}
}
