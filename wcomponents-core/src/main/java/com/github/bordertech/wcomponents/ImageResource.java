package com.github.bordertech.wcomponents;

import java.awt.Dimension;

/**
 * Provides a bridge to static image resources which are present in the class path, but not in the web application
 * itself.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class ImageResource extends InternalResource implements Image {

	/**
	 * The image size, if known.
	 */
	private final Dimension size;

	/**
	 * Creates an imageResource.
	 *
	 * @param imageResource the resource name.
	 */
	public ImageResource(final String imageResource) {
		this(imageResource, "unknown image", null);
	}

	/**
	 * Creates an imageResource.
	 *
	 * @param imageResource the resource name.
	 * @param description the description of the image.
	 */
	public ImageResource(final String imageResource, final String description) {
		this(imageResource, description, null);
	}

	/**
	 * Creates an imageResource.
	 *
	 * @param imageResource the resource name.
	 * @param description the description of the image.
	 * @param size the image dimensions, or null if unknown.
	 */
	public ImageResource(final String imageResource, final String description, final Dimension size) {
		super(imageResource, description);
		this.size = size;
	}

	/**
	 * @return the image size, or null if unknown.
	 */
	@Override
	public Dimension getSize() {
		return size;
	}
}
