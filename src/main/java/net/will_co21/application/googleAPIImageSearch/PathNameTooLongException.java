package net.will_co21.application.googleAPIImageSearch;

import java.io.File;

public class PathNameTooLongException extends RuntimeException {
	/**
	 *
	 */
	private static final long serialVersionUID = -4783551668116753225L;

	public PathNameTooLongException(File f)
	{
		super(String.format("絶対パス名が長すぎます。 (%s)", f.getAbsolutePath()));
	}
}
