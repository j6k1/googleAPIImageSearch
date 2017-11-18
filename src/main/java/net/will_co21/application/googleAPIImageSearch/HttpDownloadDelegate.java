package net.will_co21.application.googleAPIImageSearch;

import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.BiFunction;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;

public class HttpDownloadDelegate implements BiFunction<String, ILogger, Optional<HttpURLConnection>> {
	protected static final boolean[] ofContinue = new boolean[600];
	protected static final int redirectMax = 3;
	static {
		Arrays.fill(ofContinue, false);
		ofContinue[HttpURLConnection.HTTP_MOVED_PERM] = true;
		ofContinue[HttpURLConnection.HTTP_MOVED_TEMP] = true;
		ofContinue[HttpURLConnection.HTTP_SEE_OTHER] = true;
	}

	@Override
	public Optional<HttpURLConnection> apply(String strUrl, ILogger logger) {
		try {
			int httpStatus = 0;
			int redirectCount = 0;

			URL url = new URL(strUrl);

			do {
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

				connection.setConnectTimeout(3000);
				connection.setRequestMethod("GET");
				connection.connect();

				httpStatus = connection.getResponseCode();

				if(redirectCount < redirectMax && ofContinue[httpStatus])
				{
					redirectCount++;
					String redirectUrl = connection.getHeaderField("Location");

					if(redirectUrl == null) return Optional.of(connection);
					else url = new URL(redirectUrl);
				}
				else
				{
					return Optional.of(connection);
				}
			} while(ofContinue[httpStatus]);
			return Optional.empty();
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
