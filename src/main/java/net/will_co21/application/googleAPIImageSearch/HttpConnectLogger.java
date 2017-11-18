package net.will_co21.application.googleAPIImageSearch;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

public class HttpConnectLogger implements ILogger {
	protected final String url;
	protected final ILogger logger;
	protected final ISwingLogPrinter logPrinter;

	public HttpConnectLogger(String url, ILogger logger, ISwingLogPrinter logPrinter)
	{
		this.url = url;
		this.logger = logger;
		this.logPrinter = logPrinter;
	}

	public void write(String str)
	{
		logger.write(str);
	}

	public void write(Exception e)
	{
		if(e instanceof UnknownHostException)
		{
			logger.write(String.format("ホストが見つかりません。: url = %s", url));
			logPrinter.print(String.format("ホストが見つかりません。: url = %s", url));
		}
		else if(e instanceof SocketTimeoutException)
		{
			logger.write(String.format("通信タイムアウト発生: url = %s", url));
			logPrinter.print(String.format("通信タイムアウト発生: url = %s", url));
		}
		else if((e instanceof ConnectException) && (e.getMessage().equals("Connection timed out: connect")))
		{
			logger.write(String.format("通信タイムアウト発生: url = %s", url));
			logPrinter.print(String.format("通信タイムアウト発生: url = %s", url));
		}
		else
		{
			logger.write(String.format("通信時に例外発生: url = %s, 例外クラス名 = %s, message = %s",
					url, e.getClass().getName(), (e.getMessage() == null ? "null" : e.getMessage())));
		}
	}

	public void write(Error e)
	{
		logger.write(e);
	}

	public void write(Throwable t)
	{
		logger.write(t);
	}
}
