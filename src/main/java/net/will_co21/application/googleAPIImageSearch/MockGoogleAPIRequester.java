package net.will_co21.application.googleAPIImageSearch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

import net.will_co21.format.json.JsonArray;
import net.will_co21.format.json.JsonObject;
import net.will_co21.format.json.JsonParser;

public class MockGoogleAPIRequester implements IGoogleAPIRequester {
	protected final File jsonPath;
	protected volatile boolean cancelled;

	public MockGoogleAPIRequester(File jsonPath) throws UnsupportedEncodingException, FileNotFoundException, IOException
	{
		this.cancelled = false;
		this.jsonPath = jsonPath;
	}

	@Override
	public void request(IDownloadService downloader, boolean changeKeyword) throws Exception {
		cancelled = false;
		LinkedList<String> lines = new LinkedList<String>();

		try(BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(jsonPath),"UTF-8"))) {

			String line;

			while((line = reader.readLine()) != null)
			{
				lines.add(line);
			}
		}

		String json = String.join("\n", lines);
		JsonObject jobj = null;
		try {
			jobj = (JsonObject)JsonParser.parse(json);
		} catch (Exception e) {
			System.out.println(json);
		}

		List<String> urls = ((JsonArray)jobj.get("items")).map((item) -> {
			return item.value.get("link").getString();
		});

		for(String url: urls)
		{
			if(cancelled) break;
			downloader.download(url, 1, true);
		}
	}

	@Override
	public void reset() {
	}

	@Override
	public void cancel() {
		this.cancelled = true;
	}

	@Override
	public void shutdown()
	{

	}

	@Override
	public void onSearchRequestCompleted()
	{

	}

	@Override
	public void setKeyword(String keyword)
	{

	}

	@Override
	public synchronized void addCacheImage(SavedImageInfo info)
	{

	}
}
