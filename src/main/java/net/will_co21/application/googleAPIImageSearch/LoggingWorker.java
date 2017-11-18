package net.will_co21.application.googleAPIImageSearch;

import java.util.LinkedList;

public class LoggingWorker implements Runnable {
	protected IClosableWriter writer;
	protected volatile boolean working;
	protected volatile LinkedList<String> messages;

	public LoggingWorker(IClosableWriter writer)
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
				try {
					this.writer.write(message);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		this.writer.close();
	}
}
