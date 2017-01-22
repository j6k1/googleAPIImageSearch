package net.will_co21.application.googleAPIImageSearch;

public interface ISettings {
	public boolean validate();
	public int downloadMaxDepth();
	public int getResizedImageWidth();
	public int getResizedImageHeight();
	public String getImageDataRootDir();
}
