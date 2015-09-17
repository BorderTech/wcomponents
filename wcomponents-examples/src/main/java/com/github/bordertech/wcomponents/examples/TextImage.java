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
 * A simple text to image class, used by some of the examples.
 *
 * @author Yiannis Pachalidis
 * @since 1.0.0
 */
public class TextImage implements Image {

	/**
	 * The logger instance for this class.
	 */
	private static final Log LOG = LogFactory.getLog(TextImage.class);

	/**
	 * The width of the image, in pixels.
	 */
	private static final int DEFAULT_WIDTH = 150;

	/**
	 * The height of the image, in pixels.
	 */
	private static final int DEFAULT_HEIGHT = 50;

	/**
	 * The background colour; white, with 0 alpha for transparency.
	 */
	private static final Color BACKGROUND_COLOR = new Color(255, 255, 255, 0);

	/**
	 * The binary image data.
	 */
	private byte[] imageBytes;

	/**
	 * The image mime-type, e.g. image/gif.
	 */
	private static final String MIME_TYPE = "image/png";

	/**
	 * The short description of the image, e.g. the file name.
	 */
	private String description;

	/**
	 * The natural size of the image, if known.
	 */
	private final Dimension size;

	/**
	 * Creates a TextImage with the default size.
	 *
	 * @param text the text to display on the image.
	 */
	public TextImage(final String text) {
		this(text, null);
	}

	/**
	 * Creates a TextImage.
	 *
	 * @param text the text to display on the image.
	 * @param size the size of the image to generate.
	 */
	public TextImage(final String text, final Dimension size) {
		this.size = size == null ? new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT) : size;

		if (text != null) {
			description = text;

			try {
				// Draw the image.
				BufferedImage image = new BufferedImage(this.size.width, this.size.height,
						BufferedImage.TYPE_INT_ARGB);
				Graphics2D graphics = image.createGraphics();
				graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
						RenderingHints.VALUE_ANTIALIAS_ON);

				graphics.setColor(BACKGROUND_COLOR);
				graphics.fillRect(0, 0, this.size.width, this.size.height);

				graphics.setColor(Color.BLUE.darker());
				graphics.fillOval(0, 0, this.size.width, this.size.height);

				graphics.setColor(Color.WHITE);
				graphics.setFont(new Font("Arial", Font.BOLD, 24));

				Rectangle2D bounds = graphics.getFontMetrics().
						getStringBounds(description, graphics);
				int x = (int) (this.size.width - bounds.getWidth()) / 2;
				int y = (int) (this.size.height + bounds.getHeight() / 2) / 2;
				graphics.drawString(description, x, y);

				// Write the image to a byte  array.
				Iterator<ImageWriter> writers = ImageIO.getImageWritersByMIMEType(MIME_TYPE);
				ImageWriter writer = writers.next();
				ByteArrayOutputStream os = new ByteArrayOutputStream();
				ImageOutputStream ios = ImageIO.createImageOutputStream(os);
				writer.setOutput(ios);
				writer.write(image);

				imageBytes = os.toByteArray();
			} catch (IOException ex) {
				LOG.error("Unable to generate client image.", ex);
			}
		}
	}

	/**
	 * Retrieves the mime type of the image - "image/jpeg", "image/gif" etc.
	 *
	 * @return the image mime type.
	 */
	@Override
	public String getMimeType() {
		return MIME_TYPE;
	}

	/**
	 * Retrieves the natural size of the image. If only one dimension is known, a negative value will be returned for
	 * the other dimension.
	 *
	 * @return the image size, or null if unknown.
	 */
	@Override
	public Dimension getSize() {
		return size;
	}

	/**
	 * Sets the natural size of the image. If only one dimension is known, use a negative value for the other dimension.
	 * If the image size is unknown, set the size to null.
	 *
	 * @param size the image size.
	 */
	public void setSize(final Dimension size) {
		this.size.setSize(size);
	}

	/**
	 * @return the bytes that make up the document content.
	 */
	@Override
	public byte[] getBytes() {
		return imageBytes;
	}

	/**
	 * Retrieves some text that describes the image, for example the document filename or title.
	 *
	 * @return a short document description
	 */
	@Override
	public String getDescription() {
		return description;
	}

	/**
	 * Sets some text that describes the image, for example the document filename or title.
	 *
	 * @param description the short document description.
	 */
	public void setDescription(final String description) {
		this.description = description;
	}
}
