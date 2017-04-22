package net.will_co21.application.googleAPIImageSearch;

import java.io.File;
import java.io.IOException;

public interface ISettings {
	public void save() throws IOException;
	public boolean validate();
	public int getDownloadMaxDepth();
	public int getResizedImageWidth();
	public int getResizedImageHeight();
	public String getImageDataRootDir();
	public String getAPIKeysPath();
	public String getIgnoreHostsPath();
	public boolean getEnableSafeSearch();
	public void setEnableSafeSearch(boolean b);
	public String getAPIKey();
	public String getEngineId();
	public LoggingMode getLoggingMode();
	public String getLogFilePath();
	public boolean getIsAppendLogging();
}
