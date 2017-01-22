package net.will_co21.application.googleAPIImageSearch;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;

public class HttpDownloadTask implements IDownloadTask {
	protected final static HashSet<String> imageContentTypes = new HashSet<String>() {/**
		 *
		 */
		private static final long serialVersionUID = 6045351489476498780L;

	{
		add("image/jpeg");
		add("image/jpg");
		add("image/gif");
		add("image/png");
	}};

	protected final static HashMap<String, String> toExtMap = new HashMap<String, String>() {/**
		 *
		 */
		private static final long serialVersionUID = -7636084794997716879L;

	{
		put("image/jpeg", "jpg");
		put("image/jpg", "jpg");
		put("image/gif", "gif");
		put("image/png", "png");
	}};

	protected final static HashMap<String, IContentScannerCreator> notImageContentTypeScannerCreators = new HashMap<String, IContentScannerCreator>() {
	/**
		 *
		 */
		private static final long serialVersionUID = -7636084794997716879L;

	{
		put("text/html", (contentText) -> new HtmlContentScanner(contentText));
		put("text/javascript", (contentText) -> new JavaScriptContentScanner(contentText));
	}};

	protected volatile IOnHttpDownloadSuccessListener onSuccessListener;
	protected volatile IOnHttpDownloadErrorListener onErrorListener;
	protected volatile IOnReadyImagesListener onReadyImagesListener;
	protected IDownloadService downloader;
	protected HttpURLConnection connection;
	protected int depth;
	protected String url;
	protected IImageReader imageReader;
	protected ISwingLogPrinter logPrinter;
	protected ILogger logger;
	protected IEnvironment environment;
	protected ISettings settings;
	protected volatile boolean cancelled;
	protected Consumer<HttpDownloadTask> downloadDelegatee;

	public HttpDownloadTask(Consumer<HttpDownloadTask> downloadDelegatee, IDownloadService downloader, int depth,
			String url, IImageReader imageReader, ISwingLogPrinter logPrinter, ILogger logger, IEnvironment environment, ISettings settings)
	{
		this.downloadDelegatee = downloadDelegatee;
		this.downloader = downloader;
		this.depth = depth;
		this.url = url;
		this.imageReader = imageReader;
		this.logPrinter = logPrinter;
		this.logger = logger;
		this.environment = environment;
		this.settings = settings;
		this.cancelled = false;

		setUpOnSuccessListener();
		setUpOnErrorListener();
		setUpOnReadyImagesListener();
	}

	@Override
	public void cansel()
	{
		this.cancelled = true;

		setOnSuccessListener((HttpURLConnection con, String url, IImageReader imageReader,
				ISwingLogPrinter logPrinter, ILogger logger, IEnvironment environment, ISettings settings) -> {

		});

		setOnErrorListener((HttpURLConnection con, String url,
							ISwingLogPrinter logPrinter, ILogger logger, IEnvironment environment, ISettings settings) -> {

		});

		setOnReadyImagesListener((File originaImagelPath, File resizedImagePath, File thumbnailPath, int w, int h) -> {

		});
	}

	@Override
	public boolean isCancelled()
	{
		return this.cancelled;
	}

	@Override
	public void run() {
		try {
			downloadDelegatee.accept(this);
		} catch (Exception e) {
			logger.write(e);
		}
		downloader.getCounter().countDown();
	}

	public synchronized void setOnSuccessListener(IOnHttpDownloadSuccessListener listener)
	{
		onSuccessListener = listener;
	}

	public synchronized void setOnErrorListener(IOnHttpDownloadErrorListener listener)
	{
		onErrorListener = listener;
	}

	public synchronized void setOnReadyImagesListener(IOnReadyImagesListener listener)
	{
		onReadyImagesListener = listener;
	}

	public void setUpOnSuccessListener()
	{
		setOnSuccessListener((HttpURLConnection con, String url, IImageReader imageReader,
				ISwingLogPrinter logPrinter, ILogger logger, IEnvironment environment, ISettings settings) -> {
			try {
				String contentType = con.getContentType();

				if(contentType == null) return;

				int separatorPosition = contentType.indexOf(';');
				if(separatorPosition != -1) contentType = contentType.substring(0, separatorPosition);
				contentType = contentType.trim();

				if(!imageContentTypes.contains(contentType) && !notImageContentTypeScannerCreators.containsKey(contentType))
				{
					return;
				}

				InputStream in = con.getInputStream();

				try(ByteArrayOutputStream out = new ByteArrayOutputStream()) {

					byte[] buffer = new byte[256];

					int size = -1;

					while((size = in.read(buffer)) != -1)
					{
						if(this.cancelled)
						{
							out.close();
							return;
						}

						out.write(buffer, 0, size);
					}


					if(!this.cancelled && imageContentTypes.contains(contentType))
					{
						onImageSuccess(out.toByteArray(), url, contentType, imageReader, logPrinter, logger, environment);
					}
					else if(!this.cancelled && depth <= settings.downloadMaxDepth() && notImageContentTypeScannerCreators.containsKey(contentType))
					{
						String charset = EncodingDetector.getEncoding(con.getContentType(), out.toByteArray());

						BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(out.toByteArray()), charset));

						LinkedList<String> lines = new LinkedList<String>();

						String line;

						while((line = reader.readLine()) != null)
						{
							if(this.cancelled)
							{
								out.close();
								return;
							}
							lines.add(line);
						}

						IContentScanner scanner = notImageContentTypeScannerCreators.get(contentType).create(String.join("\n", lines));
						onContentSuccess(scanner, url, logPrinter, logger, environment, settings);
					}
				}
			} catch (SocketTimeoutException e) {
				throw e;
			} catch (Exception e) {
				logger.write(e);
			}
		});
	}

	protected void setUpOnErrorListener()
	{
		setOnErrorListener((HttpURLConnection con, String url,
							ISwingLogPrinter logPrinter, ILogger logger, IEnvironment environment, ISettings settings) -> {
			try {
				logPrinter.print(String.format("通信時にエラー発生: url = %s, HTTP Status = %d",
												url, con.getResponseCode()));
			} catch (IOException e) {
				logger.write(e);
			}
		});
	}

	protected void setUpOnReadyImagesListener()
	{
		setOnReadyImagesListener((File originaImagelPath, File resizedImagePath, File thumbnailPath, int w, int h) -> {
			imageReader.readImages(originaImagelPath, resizedImagePath, thumbnailPath, w, h);
		});
	}

	@Override
	public void onSuccess(HttpURLConnection con, String url,
							IImageReader imageReader, ISwingLogPrinter logPrinter,
							ILogger logger, IEnvironment environment, ISettings settings) throws SocketTimeoutException {
		onSuccessListener.onSuccess(con, url, imageReader, logPrinter, logger, environment, settings);
	}

	@Override
	public void onError(HttpURLConnection con, String url, ISwingLogPrinter logPrinter, ILogger logger, IEnvironment environment, ISettings settings) {
		onErrorListener.onError(con, url, logPrinter, logger, environment, settings);
	}

	protected void onImageSuccess(byte[] imageData, String url, String mimetype,
			IImageReader imageReader, ISwingLogPrinter logPrinter, ILogger logger, IEnvironment environment) throws
			UnsupportedEncodingException, MalformedURLException, IOException
	{
		String hostname = (new URL(url)).getHost();

		int extIndex = url.lastIndexOf('.');

		String filename;

		if(extIndex == -1 || extIndex == url.length() - 1 || !url.substring(extIndex + 1).equals(toExtMap.get(mimetype)))
		{
			filename = (new FileNameNormalizer(url + "." + toExtMap.get(mimetype))).normalizedName();
		}
		else
		{
			filename = (new FileNameNormalizer(url)).normalizedName();
		}

		File resizedImagePath = environment.getImagePath(String.join(File.separator, new String[] { hostname, "Resized"}), filename);

		(new DirectoryCreator(DirectoryCreator.getParentPath(resizedImagePath.getAbsolutePath()), 4)).create();

		Optional<Pair<Integer, Integer>> resizedImageSize = ResizedImageWriter.writeImage(imageData, mimetype,
				settings.getResizedImageWidth(), settings.getResizedImageHeight(), false,
				resizedImagePath, logger, new CancelStateReader(this));

		if(!resizedImageSize.isPresent()) return;

		File thumbnailImagePath = environment.getImagePath(String.join(File.separator, new String[] { hostname, "thumbnail"}), filename);

		(new DirectoryCreator(DirectoryCreator.getParentPath(thumbnailImagePath.getAbsolutePath()), 4)).create();

		Optional<Pair<Integer, Integer>> thumbnailImageSize = ResizedImageWriter.writeImage(imageData, mimetype,
				ThumbnailSize.width, ThumbnailSize.height, true,
				thumbnailImagePath, logger, new CancelStateReader(this));

		if(!thumbnailImageSize.isPresent()) return;

		Optional<File> rawImagePath = saveRawImage(imageData, environment.getImagePath(String.join(File.separator, new String[] { hostname }), filename), logger);

		resizedImageSize.ifPresent(pair -> rawImagePath.ifPresent((path) -> imageReader.readImages(path, resizedImagePath, thumbnailImagePath, pair.fst, pair.snd)));
	}

	protected void onContentSuccess(IContentScanner scanner, String url,
									ISwingLogPrinter logPrinter, ILogger logger, IEnvironment environment, ISettings settings)
	{
		List<String> urls = scanner.getURLList(new URLNormalizer(url));

		for(String foundUrl: urls)
		{
			if(this.cancelled) break;

			try {
				Thread.sleep(1);
				downloader.download(foundUrl, depth + 1);
			} catch (InterruptedException e) {
				logger.write(e);
			}
		}
	}

	protected Optional<File> saveRawImage(byte[] data, File path, ILogger logger)
	{
		FileOutputStream out = null;
		boolean error = false;
		try {
			out = new FileOutputStream(path);
			out.write(data);
		} catch (IOException e) {
			error = true;
			logger.write(e);
		} finally {
			try {
				if(out != null) out.close();
			} catch (IOException e) {
				error = true;
				logger.write(e);
			}
		}

		if(error) return Optional.empty();
		else return Optional.of(path);
	}
}
