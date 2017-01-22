package net.will_co21.application.googleAPIImageSearch;

import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.util.function.Consumer;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;

public class HttpDownloadTaskConsumer implements Consumer<HttpDownloadTask> {
	public void accept(HttpDownloadTask task)
	{
		task.connection = null;

		try {
			if(task.isCancelled()) return;

			URL url = new URL(task.url);

			if(url.getProtocol().equals("https"))
			{
				SSLContext sslContext = SSLContext.getInstance("SSL");
				sslContext.init(null, new X509TrustManager[] {
					new LooseTrustManager()
				}, new SecureRandom());
				task.connection = (HttpURLConnection)url.openConnection();
				((HttpsURLConnection)task.connection).setSSLSocketFactory(sslContext.getSocketFactory());
			}
			else
			{
				task.connection = (HttpURLConnection)url.openConnection();
			}

			task.connection.setReadTimeout(3000);
			task.connection.setRequestMethod("GET");
			if(task.isCancelled()) return;
			task.connection.connect();
			if(task.isCancelled()) return;
			int status = task.connection.getResponseCode();
			try {
				if(status >= 200 && status < 300) task.onSuccess(task.connection, task.url,
														task.imageReader, task.logPrinter, task.logger, task.environment, task.settings);
				else task.onError(task.connection, task.url, task.logPrinter, task.logger, task.environment, task.settings);
			} catch (SocketTimeoutException e) {
				throw e;
			} catch (Exception e) {
				task.logger.write(e);
			}
		} catch (UnknownHostException e) {
			task.logger.write(String.format("ホストが見つかりません。: url = %s", task.url));
			task.logPrinter.print(String.format("ホストが見つかりません。: url = %s", task.url));
		} catch (SocketTimeoutException e) {
			task.logger.write(String.format("通信タイムアウト発生: url = %s", task.url));
			task.logPrinter.print(String.format("通信タイムアウト発生: url = %s", task.url));
		} catch (ConnectException e) {
			if(e.getMessage().equals("Connection timed out: connect"))
			{
				task.logger.write(String.format("通信タイムアウト発生: url = %s", task.url));
				task.logPrinter.print(String.format("通信タイムアウト発生: url = %s", task.url));
			}
			else
			{
				task.logger.write(e);
			}
		} catch (Exception e) {
			task.logger.write(String.format("通信時に例外発生: url = %s, 例外クラス名 = %s, message = %s",
								task.url, e.getClass().getName(), (e.getMessage() == null ? "null" : e.getMessage())));
		} catch (Error e) {
			task.logger.write(e);
		}
	}
}
