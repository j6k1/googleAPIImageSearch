package net.will_co21.application.googleAPIImageSearch;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpDownloadService implements IDownloadService {
	protected ExecutorService httpDownloadExecutor;
	protected LinkedList<IDownloadTask> tasks;
	protected IDownloadCounter counter;
	protected IImageReader imageReader;
	protected ISwingLogPrinter logPrinter;
	protected ILogger logger;
	protected IEnvironment environment;
	protected ISettings settings;

	public HttpDownloadService(Runnable onCompleted, IImageReader imageReader, ISwingLogPrinter logPrinter, ILogger logger,
			IEnvironment environment, ISettings settings)
	{
		this.httpDownloadExecutor = Executors.newCachedThreadPool();
		this.tasks = new LinkedList<IDownloadTask>();
		this.counter = new HttpDownloadCounter(onCompleted);
		this.imageReader = imageReader;
		this.logPrinter = logPrinter;
		this.logger = logger;
		this.environment = environment;
		this.settings = settings;
	}

	@Override
	public IDownloadCounter getCounter()
	{
		return this.counter;
	}

	@Override
	public void download(String url, int depth) {
		IDownloadTask task = new HttpDownloadTask(this, depth, url, this.imageReader,
														this.logPrinter, this.logger,
														this.environment, this.settings);

		this.tasks.offerLast(task);

		this.counter.countUp();

		this.httpDownloadExecutor.submit(task);
	}

	@Override
	public void cancel()
	{
		IDownloadTask task;

		while((task = this.tasks.pollFirst()) != null)
		{
			task.cansel();
		}
	}
}
