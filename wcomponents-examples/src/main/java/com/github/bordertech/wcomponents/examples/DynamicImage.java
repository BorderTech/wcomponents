package com.github.bordertech.wcomponents.examples;

import com.github.bordertech.wcomponents.Image;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * An image implementation which generates a dynamic image.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class DynamicImage implements Image {

	/**
	 * The logger instance for this class.
	 */
	private static final Log LOG = LogFactory.getLog(DynamicImage.class);

	/**
	 * The short description of the image, e.g. the file name.
	 */
	private final String text;

	/**
	 * Creates a ProductImage.
	 *
	 * @param text the text to display in the image.
	 */
	public DynamicImage(final String text) {
		this.text = text;
	}

	/**
	 * Retrieves the mime type of the image - "image/jpeg", "image/gif" etc.
	 *
	 * @return the image mime type.
	 */
	@Override
	public String getMimeType() {
		return "image/png";
	}

	/**
	 * Retrieves the natural size of the image. If only one dimension is known, a negative value will be returned for
	 * the other dimension.
	 *
	 * @return the image size, or null if unknown.
	 */
	@Override
	public Dimension getSize() {
		return new Dimension(100, 50);
	}

	/**
	 * @return the bytes that make up the document content.
	 */
	@Override
	public byte[] getBytes() {
		if (text != null) {
			try {
				// Draw the image.
				Dimension size = getSize();

				BufferedImage image = new BufferedImage(size.width, size.height,
						BufferedImage.TYPE_INT_ARGB);
				Graphics2D graphics = image.createGraphics();
				graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
						RenderingHints.VALUE_ANTIALIAS_ON);

				graphics.setColor(new Color(255, 255, 255, 0));
				graphics.fillRect(0, 0, size.width, size.height);

				graphics.setColor(Color.BLUE.darker());
				graphics.fillOval(0, 0, size.width, size.height);

				graphics.setColor(Color.WHITE);
				graphics.setFont(new Font("Arial", Font.BOLD, 24));

				Rectangle2D bounds = graphics.getFontMetrics().getStringBounds(text, graphics);
				int x = (int) (size.width - bounds.getWidth()) / 2;
				int y = (int) (size.height + bounds.getHeight() / 2) / 2;
				graphics.drawString(text, x, y);

				// Write the image to a byte array.
				Iterator<ImageWriter> writers = ImageIO.getImageWritersByMIMEType(getMimeType());
				ImageWriter writer = writers.next();
				ByteArrayOutputStream os = new ByteArrayOutputStream();
				ImageOutputStream ios = ImageIO.createImageOutputStream(os);
				writer.setOutput(ios);
				writer.write(image);

				return os.toByteArray();
			} catch (IOException ex) {
				LOG.error("Unable to generate client image.", ex);
			}
		}

		return new byte[0];
	}

	/**
	 * Retrieves some text that describes the image, for example the document filename or title.
	 *
	 * @return a short document description
	 */
	@Override
	public String getDescription() {
		return text;
	}
}
