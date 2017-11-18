package net.will_co21.application.googleAPIImageSearch;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Optional;
import java.util.function.BooleanSupplier;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;

public class ImageSizeCalculator {

	public static Optional<Pair<Integer, Integer>> calcImageSize(File path, String mimetype, ILogger logger, BooleanSupplier canselStateReader) throws IOException
	{
		ImageReader reader = null;
		int imageWidth = -1;
		int imageHeight = -1;

		try {
			Iterator<ImageReader> rit = ImageIO.getImageReadersByMIMEType(mimetype);

			reader = rit.next();

			reader.setInput(ImageIO.createImageInputStream(new FileInputStream(path)));

			int dw = 0, dh = 0;

			int i = 0;

			try {

				while(true)
				{
					if(canselStateReader.getAsBoolean())
					{
						reader.dispose();
						return Optional.empty();
					}

					BufferedImage sourceImage = reader.read(i);

					dw = sourceImage.getWidth();
					dh = sourceImage.getHeight();

					imageWidth = Math.max(imageWidth, dw);
					imageHeight = Math.max(imageHeight, dh);

					++i;
				}
			} catch (IndexOutOfBoundsException e) {
			}

			if(i == 0)
			{
				return Optional.empty();
			}
			else
			{
				return Optional.of(new Pair<Integer, Integer>(imageWidth, imageHeight));
			}
		} catch (UnsupportedOperationException e) {
			return Optional.empty();
		} catch (IIOException e) {
			return Optional.empty();
		} catch (IOException e) {
			logger.write(e);
			throw e;
		} catch (Exception e) {
			logger.write(e);
			throw e;
		} finally {
			if(reader != null) reader.dispose();
		}
	}
}
