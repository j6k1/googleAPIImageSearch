package net.will_co21.application.googleAPIImageSearch;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.LinkedList;

import org.junit.BeforeClass;
import org.junit.Test;

public class JavaScriptContentScannerTest {
	private static String content;

	@BeforeClass
	public static void initialize() throws UnsupportedCharsetException, UnsupportedEncodingException, FileNotFoundException, IOException
	{
		String currentDir = System.getProperty("user.dir");

		String path = String.join(File.separator, new String[] { currentDir, "testdata", "parsejstest.js" });

		LinkedList<String> lines = new LinkedList<String>();

		try(BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(path),"UTF-8"))) {

			String line;

			while((line = reader.readLine()) != null)
			{
				lines.add(line);
			}
		}

		content = String.join("\r\n", lines);
	}

	@Test
	public void testJavaScriptContentScanner() {
		URLNormalizer nomalizer = new URLNormalizer("http://will-co21.net");
		JavaScriptContentScanner scanner = new JavaScriptContentScanner(content);

		assertThat(scanner.getURLList(nomalizer), is(new ArrayList<String>() {
		/**
			 *
			 */
			private static final long serialVersionUID = 8570787426513671346L;

		{
			add("http://will-co21.net/resget.php");
			add("http://will-co21.net");
			add("http://will-co21.net/index.html");
			add("http://will-co21.net/games.html");
			add("http://will-co21.net/software.html");
			add("http://will-co21.net/software/noisybbs.html?0");
			add("http://will-co21.net/software/noisybbs.html?9");
			add("http://will-co21.net/software/noisybbs.html?0.1119");
			add("http://will-co21.net/software/noisybbs.html?1.1119");
		}}));
	}

	@Test
	public void testJavaScriptContentScannerCaseInvalidNumber() {
		URLNormalizer nomalizer = new URLNormalizer("http://will-co21.net");
		JavaScriptContentScanner scanner = new JavaScriptContentScanner(
			String.join("\r\n", new String[] {
				"\"http://will-co21.net/index.html\"",
				"\"http://will-co21.net\" + 0.1.1;",
				"\"http://will-co21.net\" + a;",
				"\"http://will-co21.net\" + 00.19;",
				"\"http://will-co21.net/\" + 0.19 +",
				"\"/links.html\""
			})
		);

		assertThat(scanner.getURLList(nomalizer), is(new ArrayList<String>() {
		/**
			 *
			 */
			private static final long serialVersionUID = 8570787426513671346L;

		{
			add("http://will-co21.net/index.html");
			add("http://will-co21.net/0.19/links.html");
		}}));
	}
}
