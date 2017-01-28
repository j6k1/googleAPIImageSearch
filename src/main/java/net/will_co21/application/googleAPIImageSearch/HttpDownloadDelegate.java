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
import java.util.function.Function;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;

public class HttpDownloadDelegate implements BiFunction<String, ILogger, Optional<HttpURLConnection>> {

	@Override
	public Optional<HttpURLConnection> apply(String strUrl, ILogger logger) {
		try {
			URL url = new URL(strUrl);
			HttpURLConnection connection = null;

			if(url.getProtocol().equals("https"))
			{
				SSLContext sslContext = SSLContext.getInstance("SSL");
				sslContext.init(null, new X509TrustManager[] {
					new LooseTrustManager()
				}, new SecureRandom());
				connection = (HttpURLConnection)url.openConnection();
				((HttpsURLConnection)connection).setSSLSocketFactory(sslContext.getSocketFactory());
			}
			else
			{
				connection = (HttpURLConnection)url.openConnection();
			}

			connection.setReadTimeout(3000);
			connection.setRequestMethod("GET");
			connection.connect();

			return Optional.of(connection);
		} catch (Exception e) {
			logger.write(e);
			return Optional.empty();
		} catch (Error e) {
			logger.write(e);
			return Optional.empty();
		} catch (Throwable e) {
			logger.write(e);
			return Optional.empty();
		}
	}
}
