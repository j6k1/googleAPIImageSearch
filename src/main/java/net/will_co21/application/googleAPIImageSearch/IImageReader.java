package net.will_co21.application.googleAPIImageSearch;

import java.io.File;

public interface IImageReader {
	public void readImages(String url, File originaImagelPath, File resizedImagePath, File thumbnailPath, int w, int h);
}
