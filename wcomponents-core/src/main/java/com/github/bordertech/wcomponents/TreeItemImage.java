package com.github.bordertech.wcomponents;

import java.io.Serializable;

/**
 * Holds the details of an image for a tree item used in {@link WTree}.
 *
 * @author Jonathan Austin
 * @since 1.1.0
 */
public class TreeItemImage implements Serializable {

	/**
	 * The URL for an image.
	 */
	private final String url;
	/**
	 * The image content.
	 */
	private final Image image;
	/**
	 * The image cache key.
	 */
	private final String imageCacheKey;

	/**
	 *
	 * @param url the URL for an image.
	 */
	public TreeItemImage(final String url) {
		this.url = url;
		this.image = null;
		this.imageCacheKey = null;
	}

	/**
	 *
	 * @param image the image for a tree item
	 */
	public TreeItemImage(final Image image) {
		this(image, null);
	}

	/**
	 *
	 * @param image the image for a tree item
	 * @param imageCacheKey the cache key for the image on a tree item
	 */
	public TreeItemImage(final Image image, final String imageCacheKey) {
		this.url = null;
		this.image = image;
		this.imageCacheKey = imageCacheKey;
	}

	/**
	 *
	 * @return the URL for an image
	 */
	public String getUrl() {
		return url;
	}

	/**
	 *
	 * @return the image for a tree item
	 */
	public Image getImage() {
		return image;
	}

	/**
	 *
	 * @return the cache key for the image on a tree item
	 */
	public String getImageCacheKey() {
		return imageCacheKey;
	}

}
