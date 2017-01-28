package net.will_co21.application.googleAPIImageSearch;

import java.io.PrintWriter;
import java.io.StringWriter;

public class StackTraceString {
	protected final Throwable t;

	public StackTraceString(Throwable t)
	{
		this.t = t;
	}

	public static String toString(Throwable t)
	{
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		t.printStackTrace(pw);
		pw.flush();
		return sw.toString();
	}

	@Override
	public String toString()
	{
		return toString(t);
	}
}
