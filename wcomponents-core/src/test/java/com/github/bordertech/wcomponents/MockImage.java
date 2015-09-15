package com.github.bordertech.wcomponents;

import java.awt.Dimension;

/**
 * MockImage - implementation of Image useful for unit testing.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class MockImage extends MockContentAccess implements Image {

	/**
	 * The image size, in pixels.
	 */
	private Dimension size;

	/**
	 * @return Returns the size.
	 */
	@Override
	public Dimension getSize() {
		return size;
	}

	/**
	 * @param size The size to set.
	 */
	public void setSize(final Dimension size) {
		this.size = size;
	}
}
