package net.will_co21.application.googleAPIImageSearch;

import java.util.LinkedList;
import java.util.concurrent.ThreadFactory;
import java.util.function.Consumer;

public class LoggingWorker implements Runnable {
	protected Consumer<String> writer;
	protected volatile boolean working;
	protected volatile LinkedList<String> messages;

	public LoggingWorker(Consumer<String> writer)
	{
		this.writer = writer;
		this.working = true;
		this.messages = new LinkedList<String>();
	}

	public synchronized void post(String message)
	{
		this.messages.offerLast(message);
	}

	public synchronized void shutdown()
	{
		this.working = false;
	}

	@Override
	public void run() {
		while(this.working)
		{
			String message;

			while((message = messages.pollFirst()) != null)
			{
				this.writer.accept(message);
			}

			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
