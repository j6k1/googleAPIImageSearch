package net.will_co21.application.googleAPIImageSearch;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

public class FileLogger implements IClosableWriter {
	protected FileOutputStream ostream;
	protected OutputStreamWriter swriter;
	protected BufferedWriter writer;

	public FileLogger(File savePath) throws FileNotFoundException, UnsupportedEncodingException
	{
		ostream = new FileOutputStream(savePath);
		swriter = new OutputStreamWriter(ostream, "UTF-8");
		writer = new BufferedWriter(swriter);
	}

	@Override
	public void write(String str)
	{
		try {
			writer.write(str + "\n");
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void close()
	{
		try {
			writer.close();
			swriter.close();
			ostream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
