package net.will_co21.application.googleAPIImageSearch;

public class ConsoleLogger implements IClosableWriter {

	@Override
	public void write(String str) {
		System.out.println(str);
	}

	@Override
	public void close()
	{

	}
}
