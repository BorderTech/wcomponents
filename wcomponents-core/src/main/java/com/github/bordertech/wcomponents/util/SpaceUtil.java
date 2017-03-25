package com.github.bordertech.wcomponents.util;

import com.github.bordertech.wcomponents.Size;

/**
 * Size utility class used to ensure consistent spacing of intra-component gaps and Margins.
 * @author Mark Reeves
 * @since 1.4.0
 */
public final class SpaceUtil {
//	/**
//	 * Available space values.
//	 */
//	public enum Size {
//		/**
//		 * Explicit zero space. Used to remove innate margins (for example).
//		 */
//		ZERO ("z"),
//		/**
//		 * Small space.
//		 */
//		SMALL ("sm"),
//		/**
//		 * Medium space.
//		 */
//		MEDIUM ("med"),
//		/**
//		 * Large space.
//		 */
//		LARGE ("lg"),
//		/**
//		 * Very large space.
//		 */
//		XL ("xl");
//
//		/**
//		 * A usable representation of the Size.
//		 */
//		private final String size;
//
//		/**
//		 *
//		 * @param sz the size representation passed to the UI
//		 */
//		Size(final String sz) {
//			size = sz;
//		}
//
//		/**
//		 *
//		 * @return a string representation of the size
//		 */
//		@Override
//		public String toString() {
//			return size;
//		}
//	}

	/**
	 * prevent instantiation.
	 */
	private SpaceUtil() {
	}

	/**
	 * The <strong>largest</strong> integer space considered small. For backwards compatibility during conversion of int spaces to Size spaces.
	 */
	@Deprecated
	private static final int MAX_SMALL = 4;

	/**
	 * The <strong>largest</strong> integer space considered medium. For backwards compatibility during conversion of int spaces to Size spaces.
	 */
	@Deprecated
	private static final int MAX_MED = 8;

	/**
	 * The <strong>largest</strong> integer space considered small. For backwards compatibility during conversion of int spaces to Size spaces.
	 */
	@Deprecated
	private static final int MAX_LARGE = 16;

	/**
	 * The <strong>representative</strong> integer space considered extra-large. For backwards compatibility during conversion of int spaces to Size
	 * spaces.
	 */
	@Deprecated
	private static final int COMMON_XL = 24;

	/**
	 * Convert an int space to a Size. For backwards compatibility during conversion of int spaces to Size spaces.
	 * @param convert the int size to convert
	 * @return a Size appropriate to the int
	 */
	@Deprecated
	public static Size intToSize(final int convert) {
		// NOTE: no zero size margin in the old versions.
		if (convert <= 0) {
			return null;
		}
		if (convert <= MAX_SMALL) {
			return Size.SMALL;
		}
		if (convert <= MAX_MED) {
			return Size.MEDIUM;
		}
		if (convert <= MAX_LARGE) {
			return Size.LARGE;
		}
		return Size.XL;
	}

	/**
	 * Convert a size back to a representative int. For testing only during conversion of int spaces to Size spaces.
	 * @param size the Size to convert
	 * @return an int representative of the Size
	 */
	@Deprecated
	public static int sizeToInt(final Size size) {
		if (size == null) {
			return -1;
		}
		switch (size) {
			case ZERO:
				return 0;
			case SMALL:
				return MAX_SMALL;
			case MEDIUM:
				return MAX_MED;
			case LARGE:
				return MAX_LARGE;
			default:
				return COMMON_XL;
		}
	}
}
