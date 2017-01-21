package net.will_co21.application.googleAPIImageSearch;

import java.util.function.Consumer;

public class ConsoleLogger implements Consumer<String> {

	@Override
	public void accept(String str) {
		System.out.println(str);
	}
}
