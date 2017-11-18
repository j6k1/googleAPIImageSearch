package net.will_co21.application.googleAPIImageSearch;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpDownloadService implements IDownloadService {
	protected volatile ExecutorService httpDownloadExecutor;
	protected volatile LinkedList<IDownloadTask> tasks;
	protected volatile HashSet<IDownloadTask> runningTasks;
	protected volatile IDownloadCounter counter;
	protected IOnSearchRequestCompleted onCompleted;
	protected IOnSearchRequestCancelled onCancelled;
	protected IImageReader imageReader;
	protected IOnImageSaveCompleted onSaveImageCompleted;
	protected ISwingLogPrinter logPrinter;
	protected ILogger logger;
	protected IEnvironment environment;
	protected ISettings settings;
	protected volatile boolean cancelled = false;;
	protected volatile Set<String> alreadyDownloads;
	protected volatile Set<String> requestedUrls;
	protected volatile boolean working = false;
	protected final ExecutorService taskExecutor;
	protected volatile int executedTaskCount;

	public HttpDownloadService(IOnSearchRequestCompleted onCompleted, IOnSearchRequestCancelled onCancelled,
			IImageReader imageReader, IOnImageSaveCompleted onSaveImageCompleted,
			ISwingLogPrinter logPrinter, ILogger logger, IEnvironment environment, ISettings settings)
	{
		this.httpDownloadExecutor = Executors.newFixedThreadPool(16);
		this.tasks = new LinkedList<IDownloadTask>();
		this.runningTasks = new HashSet<IDownloadTask>();
		this.onCompleted = (cancelled) -> {
			onCompleted.onSearchRequestCompleted(cancelled);
			if(!cancelled) this.executedTaskCount = 0;
		};
		this.counter = new HttpDownloadCounter(this.onCompleted);
		this.onCancelled = onCancelled;
		this.imageReader = imageReader;
		this.onSaveImageCompleted = onSaveImageCompleted;
		this.logPrinter = logPrinter;
		this.logger = logger;
		this.environment = environment;
		this.settings = settings;
		this.alreadyDownloads = Collections.synchronizedSet(new HashSet<String>());
		this.requestedUrls = Collections.synchronizedSet(new HashSet<String>());

		taskExecutor = Executors.newSingleThreadExecutor();

		working = true;

		taskExecutor.submit(() -> {
			while(working)
			{
				IDownloadTask task = null;

				while(working && !this.cancelled && this.counter.getCount() < 16)
				{
					synchronized(this.tasks) {
						if((task = this.tasks.pollFirst()) == null)
						{
							break;
						}
					}

					synchronized(this.runningTasks) {
						if(!this.cancelled)
						{
							this.counter.countUp();

							this.executedTaskCount++;
							this.runningTasks.add(task);
							this.requestedUrls.add(task.getUrl());

							if(this.getCounter().isCancelled()) this.getCounter().setCancelled(false);

							this.httpDownloadExecutor.submit(task);
						}
					}
				}

				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
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

	@Override
	public boolean download(String url, int depth, boolean enforce)
	{
		this.cancelled = false;
		this.getCounter().setCancelled(false);
		return download(url, depth);
	}

	@Override
	public synchronized boolean download(String url, int depth)
	{
		if(this.requestedUrls.contains(url)) return false;

		if(this.cancelled) return false;

		IDownloadTask task = new HttpDownloadTask(new HttpDownloadTaskConsumer(), this, depth, url,
														this.imageReader,
														this.onSaveImageCompleted,
														runningTask -> {
															synchronized (this.runningTasks) {
																runningTasks.remove(runningTask);
															}
														},
														this.logPrinter, this.logger,
														this.environment, this.settings);

		synchronized(this.tasks) {
			this.tasks.offerLast(task);
		}

		return true;
	}

	@Override
	public void cansel()
	{
		this.cancelled = true;

		synchronized(this.runningTasks) {
			for(IDownloadTask task: this.runningTasks)
			{
				task.cansel();
				this.requestedUrls.remove(task.getUrl());
			}
			if(this.executedTaskCount == 0) this.onCompleted.onSearchRequestCompleted(true);
		}

		this.getCounter().setCancelled(true);

		this.executedTaskCount = 0;

		synchronized(this.tasks) {
			this.tasks.clear();
		}

		onCancelled.onSearchRequestCancelled();
	}

	@Override
	public void shutdown()
	{
		if(!working) return;

		working = false;
		this.httpDownloadExecutor.shutdown();
		this.taskExecutor.shutdown();
		this.cansel();
	}

	@Override
	public void onRequestCompleted()
	{
		this.onCompleted.onSearchRequestCompleted(this.cancelled);
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
	public synchronized void restoreRequestedUrls(List<String> urls)
	{
		for(String url: urls) this.requestedUrls.add(url);
	}

	@Override
	public synchronized void resetAlreadyDownloads()
	{
		this.alreadyDownloads.clear();
	}

	@Override
	public synchronized void resetRequestedUrls()
	{
		this.requestedUrls.clear();
	}
}
