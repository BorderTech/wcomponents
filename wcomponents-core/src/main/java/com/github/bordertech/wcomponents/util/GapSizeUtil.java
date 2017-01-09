package com.github.bordertech.wcomponents.util;

/**
 * Size utility class used to enforce consistent spacing in gaps and margins.
 * @author Mark Reeves
 * @since 1.4.0
 */
public final class GapSizeUtil {
	/**
	 * Size enumeration used to enforce consistent spacing in gaps and margins.
	 */
	public enum Size {
		/**
		 * Explicit zero gap. Used to remove innate margins (for example).
		 */
		ZERO ("z"),
		/**
		 * Small gap.
		 */
		SMALL ("sm"),
		/**
		 * Medium gap.
		 */
		MEDIUM ("med"),
		/**
		 * Large gap.
		 */
		LARGE ("lg"),
		/**
		 * Very large gap.
		 */
		XL ("xl");

		/**
		 * A usable representation of the Size.
		 */
		private final String size;

		/**
		 *
		 * @param sz the size representation
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

	/**
	 * prevent instantiation.
	 */
	private GapSizeUtil() {
	}

	/**
	 * The largest integer Margin considered small. This is for backwards compatibility
	 * whilst we convert applications from int margins to Size margins.
	 */
	private static final int MAX_SMALL = 4;

	/**
	 * The largest integer Margin considered medium. This is for backwards compatibility
	 * whilst we convert applications from int margins to Size margins.
	 */
	private static final int MAX_MED = 8;

	/**
	 * The largest integer Margin considered small. This is for backwards compatibility
	 * whilst we convert applications from int margins to Size margins.
	 */
	private static final int MAX_LARGE = 16;

	/**
	 * The representative integer Margin considered extra-large. This is for backwards compatibility
	 * whilst we convert applications from int margins to Size margins.
	 */
	private static final int COMMON_XL = 24;

	/**
	 * Convert an int space to a size. This is for backwards compatibility whilst we convert applications from int
	 * gaps to Size gaps.
	 * @param convert the int size to convert
	 * @return a Size appropriate to the int
	 * @deprecated 1.4.0 do not use.
	 */
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
	 * Convert a size back to a representative int. This is for backwards compatibility whilst we convert applications
	 * from int gaps to Size gaps.
	 * @param size the size to convert
	 * @return an int appropriate to the size
	 * @deprecated 1.4.0 do not use.
	 */
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
