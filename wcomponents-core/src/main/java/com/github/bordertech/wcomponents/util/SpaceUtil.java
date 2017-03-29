package com.github.bordertech.wcomponents.util;

import com.github.bordertech.wcomponents.Size;

/**
 * Size utility class used to ensure consistent spacing of intra-component gaps and Margins For backwards compatibility only.
 * @author Mark Reeves
 * @author John McGuiness
 * @since 1.4.0
 */
@Deprecated
public final class SpaceUtil {
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
