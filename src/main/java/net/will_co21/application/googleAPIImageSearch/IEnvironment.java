package net.will_co21.application.googleAPIImageSearch;

import java.io.File;
import java.io.UnsupportedEncodingException;

public interface IEnvironment {
	public File getImagePath(String path, String filename) throws UnsupportedEncodingException;
	public void setSafeMode(boolean mode);
	public boolean getSafeMode();
}
