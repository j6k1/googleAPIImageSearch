package net.will_co21.application.googleAPIImageSearch;

import java.io.File;

public class GoogleAPIImageSearchSettings implements ISettings {
	public GoogleAPIImageSearchSettings(File jsonPath)
	{

	}

	@Override
	public int downloadMaxDepth() {
		return 5;
	}
	public int getResizedImageWidth()
	{
		return 1000;
	}

	public int getResizedImageHeight()
	{
		return 800;
	}
}
