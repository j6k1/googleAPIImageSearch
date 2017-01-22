package net.will_co21.application.googleAPIImageSearch;

import java.io.File;

public class GoogleAPIImageSearchSettings implements ISettings {
	public GoogleAPIImageSearchSettings(File jsonPath)
	{

	}

	@Override
	public boolean validate()
	{
		File f = new File(this.getImageDataRootDir());
		if(f.isFile()) throw new InvalidSettingException("画像の保存先として指定されたディレクトリと同名のファイル" + f.getAbsolutePath() + "が既に存在します。");
		else if(f.isDirectory()) return true;
		else if(f.exists())  throw new InvalidSettingException("画像の保存先として指定されたパスにファイルでもディレクトリでもない何かが存在します。");
		else  throw new InvalidSettingException("画像の保存先として指定されたディレクトリが存在しません。");
	}

	@Override
	public int downloadMaxDepth() {
		return 5;
	}

	@Override
	public int getResizedImageWidth()
	{
		return 1000;
	}

	@Override
	public int getResizedImageHeight()
	{
		return 800;
	}

	@Override
	public String getImageDataRootDir()
	{
		return "F:\\画像\\googleAPIImageSearch";
	}
}
