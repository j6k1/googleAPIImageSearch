package net.will_co21.application.googleAPIImageSearch;

public interface IOnImageSaveCompleted {
	public void saveCompleted(String url, String originaImagelPath, String resizedImagePath, String thumbnailPath, int w, int h);
}
