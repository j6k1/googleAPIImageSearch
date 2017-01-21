package net.will_co21.application.googleAPIImageSearch;

import java.io.PrintWriter;
import java.io.StringWriter;

public class LoggingExceptionMessageBuilder {
	public static String build(Exception e)
	{
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		pw.flush();
		return sw.toString();
	}

	public static String build(Error e)
	{
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		pw.flush();
		return sw.toString();
	}
}
