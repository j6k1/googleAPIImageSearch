package net.will_co21.application.googleAPIImageSearch;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.sun.org.apache.xalan.internal.xsltc.compiler.sym;

public class HttpDownloadService implements IDownloadService {
	protected volatile ExecutorService httpDownloadExecutor;
	protected volatile LinkedList<IDownloadTask> tasks;
	protected volatile IDownloadCounter counter;
	IOnSearchRequestCancelled onCancelled;
	protected IImageReader imageReader;
	protected ISwingLogPrinter logPrinter;
	protected ILogger logger;
	protected IEnvironment environment;
	protected ISettings settings;
	protected volatile boolean cancelled = false;;
	protected volatile HashSet<String> alreadyDownloads;
	protected volatile boolean working = false;
	protected final ExecutorService taskExecutor;

	public HttpDownloadService(IOnSearchRequestCompleted onCompleted, IOnSearchRequestCancelled onCancelled,
			IImageReader imageReader, ISwingLogPrinter logPrinter, ILogger logger,
			IEnvironment environment, ISettings settings)
	{
		this.httpDownloadExecutor = Executors.newFixedThreadPool(16);
		this.tasks = new LinkedList<IDownloadTask>();
		this.counter = new HttpDownloadCounter(onCompleted);
		this.onCancelled = onCancelled;
		this.imageReader = imageReader;
		this.logPrinter = logPrinter;
		this.logger = logger;
		this.environment = environment;
		this.settings = settings;
		this.alreadyDownloads = new HashSet<String>();

		taskExecutor = Executors.newSingleThreadExecutor();

		working = true;

		taskExecutor.submit(() -> {
			while(working)
			{
				IDownloadTask task = null;

				while(working && this.counter.getCount() <= 16 && (task = this.tasks.pollFirst()) != null)
				{
					this.counter.countUp();

					this.httpDownloadExecutor.submit(task);
				}
			}
		});
	}

	@Override
	public synchronized IDownloadCounter getCounter()
	{
		return this.counter;
	}

	public boolean isCancelled()
	{
		return this.cancelled;
	}

	public synchronized void synchronaizedExecute(Runnable r)
	{
		r.run();
	}

	@Override
	public void download(String url, int depth, boolean enforce)
	{
		this.cancelled = false;
		download(url, depth);
	}

	@Override
	public void download(String url, int depth) {
		synchronaizedExecute(() -> {
			if(this.cancelled) return;

			IDownloadTask task = new HttpDownloadTask(new HttpDownloadTaskConsumer(), this, depth, url, this.imageReader,
															this.logPrinter, this.logger,
															this.environment, this.settings);

			this.tasks.offerLast(task);
		});
	}

	@Override
	public void cansel()
	{
		synchronaizedExecute(() -> {
			this.cancelled = true;

			IDownloadTask task;

			while((task = this.tasks.pollLast()) != null)
			{
				task.cansel();
			}

			onCancelled.onSearchRequestCancelled();
		});
	}

	@Override
	public void shutdown()
	{
		working = false;
		this.httpDownloadExecutor.shutdown();
		this.taskExecutor.shutdown();
	}

	@Override
	public synchronized void addAlreadyDownloads(String filename)
	{
		this.alreadyDownloads.add(filename);
	}

	@Override
	public boolean alreadyDownload(String filename)
	{
		return this.alreadyDownloads.contains(filename);
	}

	@Override
	public synchronized void resetAlreadyDownloads()
	{
		this.alreadyDownloads.clear();
	}
}
