package net.will_co21.application.googleAPIImageSearch;

import java.io.File;

public class DirectoryCreator {
	public final String path;
	public final int maxdepth;

	public DirectoryCreator(String path, int maxdepth)
	{
		if(maxdepth < 1) throw new RuntimeException("maxdepthの指定が不正です。");

		this.path = path;
		this.maxdepth = maxdepth;
	}

	public static String getParentPath(String path)
	{
		int separatorIndex;

		if((separatorIndex = path.lastIndexOf(File.separator)) == -1)
		{
			throw new InvalidPathNameException("渡されたパス名からディレクトリ名を取得できません。パス名には絶対パスを渡してください。");
		}

		return path.substring(0, separatorIndex);
	}

	public boolean create()
	{
		return create(this.path, 1);
	}

	public boolean create(String path, int depth)
	{
		File f = new File(path);

		if(f.isDirectory()) return true;
		else if(f.isFile()) throw new DirectoryCreateFailedException("ディレクトリ名と同名のファイル" + f.getAbsolutePath() + "が既に存在します。");
		else if(f.exists()) throw new DirectoryCreateFailedException("指定されたパス上にディレクトリでもファイルでもない何かが存在します。 指定されたパス: " + f.getAbsolutePath());
		else if(maxdepth < depth)
		{
			throw new DirectoryCreateFailedException("ディレクトリ" + f.getAbsolutePath() + "が存在しません。");
		}
		else
		{
			int separatorIndex = f.getAbsolutePath().lastIndexOf(File.separator);

			if(separatorIndex == -1) throw new DirectoryCreateFailedException("ディレクトリ" + f.getAbsolutePath() + "が存在しません。");

			create(f.getAbsolutePath().substring(0, separatorIndex), depth + 1);
			if(!f.mkdir()) throw new DirectoryCreateFailedException("ディレクトリ" + f.getAbsolutePath() + "の作成に失敗しました。");
			else return true;
		}
	}
}
