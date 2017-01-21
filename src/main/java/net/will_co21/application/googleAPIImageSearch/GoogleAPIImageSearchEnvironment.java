package net.will_co21.application.googleAPIImageSearch;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.zip.CRC32;

public class GoogleAPIImageSearchEnvironment implements IEnvironment {
	protected boolean safeModeEnable = false;
	protected SearchMode searchMode = SearchMode.apiSearch;
	@Override
	public File getImagePath(String path, String filename) throws UnsupportedEncodingException {
		File imagePath = new File(String.join(File.separator, new String[] {"data", "images", path, filename}));

		if(imagePath.getAbsolutePath().getBytes("Shift_JIS").length > 256)
		{
			CRC32 crc32 = new CRC32();

			crc32.update(filename.getBytes("Shift_JIS"));

			int extStart = filename.lastIndexOf('.');
			String ext = filename.substring(extStart + 1);
			String crc32FileName = String.format("%08x.%s", crc32.getValue(), ext);

			imagePath = new File(String.join(File.separator, new String[] {"data", "images", path, crc32FileName}));

			if(imagePath.getAbsolutePath().getBytes("Shift_JIS").length > 256)
			{
				throw new PathNameTooLongException(imagePath);
			}
		}

		return imagePath;
	}

	@Override
	public void setSafeMode(boolean mode)
	{
		safeModeEnable = mode;
	}

	@Override
	public boolean getSafeMode()
	{
		return safeModeEnable;
	}

	@Override
	public void setSearchMode(SearchMode mode)
	{
		searchMode = mode;
	}

	@Override
	public SearchMode getSearchMode()
	{
		return searchMode;
	}
}
