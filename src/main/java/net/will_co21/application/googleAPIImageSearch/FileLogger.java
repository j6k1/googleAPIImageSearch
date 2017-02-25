package net.will_co21.application.googleAPIImageSearch;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FileLogger implements IClosableWriter {
	protected FileOutputStream ostream;
	protected OutputStreamWriter swriter;
	protected BufferedWriter writer;
	protected DateTimeFormatter dff;

	public FileLogger(File savePath, boolean append) throws FileNotFoundException, UnsupportedEncodingException
	{
		dff = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss.SSS");
		ostream = new FileOutputStream(savePath, append);
		swriter = new OutputStreamWriter(ostream, "UTF-8");
		writer = new BufferedWriter(swriter);
	}

	@Override
	public void write(String str)
	{
		try {
			writer.write(dff.format(LocalDateTime.now()) + ": " + str + "\n");
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
