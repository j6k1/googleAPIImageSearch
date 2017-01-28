package net.will_co21.application.googleAPIImageSearch;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiFunction;

import net.will_co21.format.json.JsonArray;
import net.will_co21.format.json.JsonObject;
import net.will_co21.format.json.JsonParser;

public class HttpsGoogleAPIRequester implements IGoogleAPIRequester {
	public final String apiUrl = "https://www.googleapis.com/customsearch/v1";

	protected volatile boolean cancelled;
	protected final BiFunction<String, ILogger, Optional<HttpURLConnection>> delegate = new HttpDownloadDelegate();
	protected final ExecutorService requestExecutor;
	protected final ISettings settings;
	protected final ISwingLogPrinter logPrinter;
	protected final ILogger logger;
	protected String keyword = "";
	protected int currentPage;

	public HttpsGoogleAPIRequester(ISettings settings, ISwingLogPrinter logPrinter, ILogger logger) throws UnsupportedEncodingException, FileNotFoundException, IOException
	{
		this.cancelled = false;
		this.settings = settings;
		this.logPrinter = logPrinter;
		this.logger = logger;
		this.currentPage = 1;
		this.requestExecutor = Executors.newSingleThreadExecutor();
	}

	@Override
	public void request(IDownloadService downloader) throws Exception {
		cancelled = false;
		String url = this.apiUrl + "?key=" + settings.getAPIKey() +
				"&cx=" + settings.getEngineId() +
				"&q=" + URLEncoder.encode(keyword, "UTF-8") +
				"&safe=" + (settings.getEnableSafeSearch() ? "medium" : "off") +
				"&start=" + this.currentPage;

		requestExecutor.submit(() -> {
			Optional<HttpURLConnection> connection = delegate.apply(url, new HttpConnectLogger(url, this.logger, this.logPrinter));
			if(cancelled) return;

			connection.ifPresent(con -> {
				try {
					int status = con.getResponseCode();
					if(status >= 200 && status < 300) this.onSuccess(downloader, con);
					else this.onError(url, con, logPrinter, logger);
				} catch (SocketTimeoutException e) {
					this.logger.write(String.format("通信タイムアウト発生: url = %s", url));
					this.logPrinter.print(String.format("通信タイムアウト発生: url = %s", url));
				} catch (Exception e) {
					this.logger.write(e);
				} catch (Error e) {
					this.logger.write(String.format("致命的な例外: %s, message = %s", e.getClass().getName(), e.getMessage()));
					this.logger.write(e);
				} catch (Throwable t) {
					this.logger.write(String.format("ExceptionでもErrorでもない例外がスローされました。 %s, message = %s", t.getClass().getName(), t.getMessage()));
					this.logger.write(t);
				}
			});
		});
	}

	@Override
	public void reset() {
		this.currentPage = 1;
	}

	@Override
	public void cancel() {
		this.cancelled = true;
	}

	@Override
	public void shutdown()
	{
		this.currentPage = 1;
		this.requestExecutor.shutdown();
	}

	@Override
	public void onSearchRequestCompleted()
	{
		this.currentPage++;
	}

	@Override
	public void setKeyword(String keyword)
	{
		this.keyword = keyword;
	}

	protected void onSuccess(IDownloadService downloader, HttpURLConnection con) throws UnsupportedEncodingException, IOException
	{
		BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));

		LinkedList<String> lines = new LinkedList<String>();

		String line;

		while((line = reader.readLine()) != null)
		{
			if(this.cancelled)
			{
				return;
			}
			lines.add(line);
		}

		String json = String.join("\n", lines);
		JsonObject jobj = null;
		try {
			jobj = (JsonObject)JsonParser.parse(json);
		} catch (Exception e) {
			System.out.println(json);
		}

		List<String> urls = ((JsonArray)jobj.get("items")).map((item) -> {
			return item.value.get("link").getString();
		});

		for(String url: urls)
		{
			if(cancelled) break;
			downloader.download(url, 1, true);
		}
	}

	protected void onError(String url, HttpURLConnection con, ISwingLogPrinter logPrinter, ILogger logger)
	{
		try {
			logPrinter.print(String.format("通信時にエラー発生: url = %s, HTTP Status = %d",
											url, con.getResponseCode()));
		} catch (IOException e) {
			logger.write(e);
		}
	}
}
