package net.will_co21.application.googleAPIImageSearch;

public class SavedImageInfo {
	public final String url;
	public final String originaImagelPath;
	public final String resizedImagePath;
	public final String thumbnailPath;
	public final int width;
	public final int height;

	public SavedImageInfo(String url, String originaImagelPath, String resizedImagePath, String thumbnailPath,
			int width, int height)
	{
		this.url = url;
		this.originaImagelPath = originaImagelPath;
		this.resizedImagePath = resizedImagePath;
		this.thumbnailPath = thumbnailPath;
		this.width = width;
		this.height = height;
	}
}
