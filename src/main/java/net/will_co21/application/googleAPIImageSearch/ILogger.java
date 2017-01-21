package net.will_co21.application.googleAPIImageSearch;

public interface ILogger {
	public void write(String str);
	public void write(Exception e);
	public void write(Error e);
}
