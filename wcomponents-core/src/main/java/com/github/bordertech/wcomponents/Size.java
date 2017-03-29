package com.github.bordertech.wcomponents;

/**
 * Used to ensure consistent spacing of intra-component gaps and Margins.
 * @author Mark Reeves
 * @author John McGuiness
 * @since 1.4.0
 */
public enum Size {
	/**
	 * Explicit zero space. Used to remove innate margins (for example).
	 */
	ZERO("z"),
	/**
	 * Small space.
	 */
	SMALL("sm"),
	/**
	 * Medium space.
	 */
	MEDIUM("med"),
	/**
	 * Large space.
	 */
	LARGE("lg"),
	/**
	 * Very large space.
	 */
	XL("xl");

	/**
	 * A usable representation of the Size.
	 */
	private final String size;

	/**
	 *
	 * @param sz the size representation passed to the UI
	 */
	Size(final String sz) {
		size = sz;
	}

	/**
	 *
	 * @return a string representation of the size
	 */
	@Override
	public String toString() {
		return size;
	}
}
