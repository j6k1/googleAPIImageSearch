package net.will_co21.application.googleAPIImageSearch;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Optional;
import java.util.function.BooleanSupplier;

import javax.imageio.IIOException;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;

public class ResizedImageWriter {

	public static Optional<Pair<Integer, Integer>> writeImage(byte[] data, String mimetype, int w, int h, boolean fixedSize, File path,
																ILogger logger, BooleanSupplier canselStateReader) throws IOException
	{
		ImageReader reader = null;
		ImageWriter writer = null;
		int imageWidth = -1;
		int imageHeight = -1;

		boolean error = false;

		try {
			Iterator<ImageReader> rit = ImageIO.getImageReadersByMIMEType(mimetype);

			reader = rit.next();

			reader.setInput(ImageIO.createImageInputStream(new ByteArrayInputStream(data)));

			Iterator<ImageWriter> wit = ImageIO.getImageWritersByMIMEType(mimetype);

			writer = wit.next();

			try(FileImageOutputStream ostream = new FileImageOutputStream(path)) {
				writer.setOutput(ostream);
				writer.prepareWriteSequence(null);

				int dw = 0, dh = 0;

				int i = 0;

				try {
					while(true)
					{
						if(canselStateReader.getAsBoolean())
						{
							if(path.isFile())
							{
								writer.endWriteSequence();
								writer.dispose();
								if(!path.delete()) logger.write(String.format("ファイル%sを削除できませんでした。", path.getAbsolutePath()));
							}
							reader.dispose();
							writer.dispose();
							return Optional.empty();
						}

						BufferedImage sourceImage = reader.read(i);

						if(sourceImage.getHeight() <= h && sourceImage.getWidth() <= w)
						{
							dw = sourceImage.getWidth();
							dh = sourceImage.getHeight();
						}
						else if((int)((double)sourceImage.getHeight() / (double)(sourceImage.getWidth() / (double)w)) > h)
						{
							dh = h;
							dw = (int)((double)sourceImage.getWidth() / ((double)sourceImage.getHeight() / (double)h));
						}
						else
						{
							dw = w;
							dh = (int)((double)sourceImage.getHeight() / ((double)sourceImage.getWidth() / (double)w));
						}

						imageWidth = Math.max(imageWidth, dw);
						imageHeight = Math.max(imageHeight, dh);

						int x,y;

						if(fixedSize)
						{
							x = (w - dw) / 2;
							y = (h - dh) / 2;
						}
						else
						{
							x = 0;
							y = 0;
							w = dw;
							h = dh;
						}

						BufferedImage thumbnail = new BufferedImage(w, h, sourceImage.getType());
						thumbnail
							.getGraphics()
							.drawImage(sourceImage.getScaledInstance(dw, dh, sourceImage.getType()),
																	x, y, null);
						writer.writeToSequence(new IIOImage(thumbnail, null, null), null);
						++i;
					}
				} catch (IndexOutOfBoundsException e) {
				}
				writer.endWriteSequence();

				if(i == 0)
				{
					error = true;
				}
				else
				{
					return Optional.of(new Pair<Integer, Integer>(imageWidth, imageHeight));
				}
			}
		} catch (UnsupportedOperationException e) {
			error = true;
		} catch (IIOException e) {
			error = true;
		} catch (IOException e) {
			logger.write(e);
			writer.dispose();
			if(path.isFile())
			{
				if(!path.delete()) logger.write(String.format("ファイル%sを削除できませんでした。", path.getAbsolutePath()));
			}
			throw e;
		} catch (Exception e) {
			logger.write(e);
			writer.dispose();
			if(path.isFile())
			{
				if(!path.delete()) logger.write(String.format("ファイル%sを削除できませんでした。", path.getAbsolutePath()));
			}
			throw e;
		} finally {
			if(reader != null) reader.dispose();
			if(writer != null)
			{
				writer.dispose();
			}
			if(error) if(path.isFile()) if(!path.delete()) logger.write(String.format("ファイル%sを削除できませんでした。", path.getAbsolutePath()));
		}
		return Optional.empty();
	}
}
