package net.will_co21.application.googleAPIImageSearch;

public class LoggingMode {
	public static final LoggingMode conole = new LoggingMode();
	public static final LoggingMode file = new LoggingMode();

	private LoggingMode()
	{

	}

	public static LoggingMode parse(String str)
	{
		if(str.equals("file")) return LoggingMode.file;
		else return LoggingMode.conole;
	}
}
