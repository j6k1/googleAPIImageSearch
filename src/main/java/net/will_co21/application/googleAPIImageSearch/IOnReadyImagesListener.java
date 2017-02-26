package net.will_co21.application.googleAPIImageSearch;

import java.io.File;

public interface IOnReadyImagesListener {
	public void readImages(String url, File originaImagelPath, File resizedImagePath, File thumbnailPath, int w, int h);
}
