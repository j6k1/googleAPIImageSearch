package net.will_co21.application.googleAPIImageSearch;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.function.Consumer;

import net.will_co21.format.json.JsonFormatErrorException;
import net.will_co21.format.json.JsonObject;
import net.will_co21.format.json.JsonParser;
import net.will_co21.format.json.JsonProperty;
import net.will_co21.format.json.KeyNotFoundException;
import net.will_co21.format.json.NotSupportedMethodException;

public class GoogleAPIImageSearchSettings implements ISettings {
	protected File jsonPath;
	protected int downloadMaxDepth;
	protected boolean enableSafeSearch;
	protected int resizedImageWidth;
	protected int resizedImageHeight;
	protected String imageDataRootDir;
	protected String apiKeysPath;
	protected String apiKey;
	protected String engineId;

	@Override
	public void save() throws IOException
	{
		try(BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(jsonPath),"UTF-8"))) {

			writer.write((new JsonObject(new JsonProperty[] {
					JsonProperty.create("search", new JsonObject(new JsonProperty[] {
							JsonProperty.create("maxdepth", downloadMaxDepth),
							JsonProperty.create("safesearch", enableSafeSearch)
					})),
					JsonProperty.create("images", new JsonObject(new JsonProperty[] {
							JsonProperty.create("resize", new JsonObject(new JsonProperty[] {
									JsonProperty.create("width", resizedImageWidth),
									JsonProperty.create("height", resizedImageHeight)
							}))
					})),
					JsonProperty.create("imageDir", imageDataRootDir),
					JsonProperty.create("apikeys", new JsonObject(new JsonProperty[] {
							JsonProperty.create("path", apiKeysPath)
					}))
			})).toPrettyJson());

			writer.flush();
		}
	}

	public GoogleAPIImageSearchSettings(File jsonPath, Consumer<String> messageWriter) throws UnsupportedEncodingException, FileNotFoundException, IOException
	{
		this.jsonPath = jsonPath;

		if(!jsonPath.exists())
		{
			downloadMaxDepth = 5;
			enableSafeSearch = false;
			resizedImageWidth = 1000;
			resizedImageHeight = 800;
			imageDataRootDir = "images";
			apiKeysPath = "apikeys.json";

			save();

			messageWriter.accept(String.format("設定ファイルが%sに存在しません。デフォルト設定で作成しました。", jsonPath.getAbsolutePath()));
		}
		else if(jsonPath.isDirectory()) throw new InvalidSettingException(String.format("設定ファイル%sと同名のディレクトリが既に存在します", jsonPath.getAbsolutePath()));
		else if(!jsonPath.isFile()) throw new InvalidSettingException(String.format("設定ファイルと同名のパス%sにファイルでもディレクトリでもない何かが既に存在します。", jsonPath.getAbsolutePath()));

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
		} catch (JsonFormatErrorException e) {
			throw new InvalidSettingException("jsonファイルの形式が不正です。");
		}

		try {
			downloadMaxDepth = jobj.get("search").get("maxdepth").getInt();
			enableSafeSearch = jobj.get("search").get("safesearch").getBoolean();
			resizedImageWidth = jobj.get("images").get("resize").get("width").getInt();
			resizedImageHeight = jobj.get("images").get("resize").get("height").getInt();
			imageDataRootDir = jobj.get("imageDir").getString();
			apiKeysPath = jobj.get("apikeys").get("path").getString();
		} catch (KeyNotFoundException e) {
			throw new InvalidSettingException("jsonファイルの形式が不正です。");
		} catch (NotSupportedMethodException e) {
			throw new InvalidSettingException("jsonファイルの形式が不正です。");
		}

		File apikeysPath = new File(this.getAPIKeysPath());
		if(!apikeysPath.exists()) throw new InvalidSettingException("APIキーを格納したファイルが設定されているパスに存在しません。");
		else if(apikeysPath.isDirectory()) throw new InvalidSettingException("APIキーを格納したファイルのパスとして設定されているパスに同名のディレクトリが既に存在します。");
		else if(!apikeysPath.isFile()) throw new InvalidSettingException("APIキーを格納したファイルのパスとして設定されているパスにファイルでもディレクトリでもない何かが既に存在します。");

		lines = new LinkedList<String>();

		try(BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(apikeysPath),"UTF-8"))) {

			String line;

			while((line = reader.readLine()) != null)
			{
				lines.add(line);
			}
		}

		json = String.join("\n", lines);
		jobj = null;
		try {
			jobj = (JsonObject)JsonParser.parse(json);
		} catch (JsonFormatErrorException e) {
			throw new InvalidSettingException("jsonファイルの形式が不正です。");
		}

		try {
			apiKey = jobj.get("apikey").getString();
			engineId = jobj.get("engineId").getString();
		} catch (KeyNotFoundException e) {
			throw new InvalidSettingException("jsonファイルの形式が不正です。");
		} catch (NotSupportedMethodException e) {
			throw new InvalidSettingException("jsonファイルの形式が不正です。");
		}
	}

	@Override
	public boolean validate()
	{
		File f = new File(this.getImageDataRootDir());
		if(f.isFile()) throw new InvalidSettingException("画像の保存先として指定されたディレクトリと同名のファイル" + f.getAbsolutePath() + "が既に存在します。");
		else if(f.isDirectory()) {}
		else if(f.exists())  throw new InvalidSettingException("画像の保存先として指定されたパスにファイルでもディレクトリでもない何かが存在します。");
		else throw new InvalidSettingException("画像の保存先として指定されたディレクトリが存在しません。");

		return true;
	}

	@Override
	public int getDownloadMaxDepth() {
		return downloadMaxDepth;
	}

	@Override
	public int getResizedImageWidth()
	{
		return resizedImageWidth;
	}

	@Override
	public int getResizedImageHeight()
	{
		return resizedImageHeight;
	}

	@Override
	public String getImageDataRootDir()
	{
		return imageDataRootDir;
	}

	@Override
	public String getAPIKeysPath()
	{
		return apiKeysPath;
	}

	@Override
	public boolean getEnableSafeSearch()
	{
		return enableSafeSearch;
	}

	@Override
	public void setEnableSafeSearch(boolean b)
	{
		enableSafeSearch = b;
	}

	@Override
	public String getAPIKey()
	{
		return apiKey;
	}

	@Override
	public String getEngineId()
	{
		return engineId;
	}
}
