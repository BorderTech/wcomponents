package com.github.bordertech.wcomponents.examples.petstore;

import com.github.bordertech.wcomponents.Image;
import com.github.bordertech.wcomponents.examples.petstore.model.ProductBean;
import com.github.bordertech.wcomponents.util.StreamUtil;
import java.awt.Dimension;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Encapsulation of a product image.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class ProductImage implements Image {

	/**
	 * The location of the PetStore images.
	 */
	private static final String IMAGE_PATH = "com/github/bordertech/wcomponents/examples/petstore/resources/images/";

	/**
	 * The logger instance for this class.
	 */
	private static final Log LOG = LogFactory.getLog(ProductImage.class);

	/**
	 * The binary image data.
	 */
	private byte[] imageBytes;
	/**
	 * The image mime-type, e.g. image/gif.
	 */
	private String mimeType;
	/**
	 * The short description of the image, e.g. the file name.
	 */
	private String description;
	/**
	 * The natural size of the image, if known.
	 */
	private Dimension size;

	/**
	 * Creates a ProductImage.
	 *
	 * @param bean the bean to retrieve the image for.
	 */
	public ProductImage(final ProductBean bean) {
		if (bean != null) {
			description = bean.getShortTitle();
			String name = bean.getImage();

			if (name != null) {
				InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(
						IMAGE_PATH + name);

				if (in != null) {
					try {
						imageBytes = StreamUtil.getBytes(in);
						in.close();
					} catch (IOException ex) {
						LOG.error("Cannot load product image.", ex);
					}

					String type = name.substring(name.lastIndexOf('.') + 1).toLowerCase();

					if ("jpg".equals(type)) {
						mimeType = "image/jpeg";
					} else {
						mimeType = "image/" + type;
					}
				}
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
		return mimeType;
	}

	/**
	 * Sets the mime type of the image.
	 *
	 * @param mimeType the image mime type - "image/jpeg", "image/gif" etc.
	 */
	public void setMimeType(final String mimeType) {
		this.mimeType = mimeType;
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
		this.size = size;
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
