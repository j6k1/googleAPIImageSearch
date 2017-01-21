package net.will_co21.application.googleAPIImageSearch;

import java.util.Arrays;

public class FileNameNormalizer {
	protected String originalName;

	protected static final char[] map = new char[128];

	static {
		Arrays.fill(map, '\0');
		map['\\'] = '￥';
		map['/'] = '／';
		map[':'] = '：';
		map['*'] = '＊';
		map['?'] = '？';
		map['\''] = '’';
		map['"'] = '”';
		map['<']= '＜';
		map['>'] = '＞';
	}

	public FileNameNormalizer(String name)
	{
		originalName = name;
	}

	public String normalizedName()
	{
		char[] chars = originalName.toCharArray();

		StringBuilder sb = new StringBuilder();

		for(char c: chars)
		{
			if(map[c] != '\0') sb.append(map[c]);
			else sb.append(c);
		}

		return sb.toString();
	}
}
