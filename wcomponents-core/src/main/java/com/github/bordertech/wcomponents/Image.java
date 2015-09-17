package com.github.bordertech.wcomponents;

import java.awt.Dimension;

/**
 * Represents an image. Implementations can choose whether to explicitly store the byte[] or fetch it from some sort of
 * streaming resource as needed to service the {@link #getBytes} method as needed.
 *
 * @author James Gifford, Martin Shevchenko
 * @since 1.0.0
 */
public interface Image extends ContentAccess {

	/**
	 * @return the natural size of the image, or null if no natural size is known. If only one dimension is known, use a
	 * negative value (eg, -1) for the other dimension.
	 */
	Dimension getSize();
}
