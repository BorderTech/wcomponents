package com.github.bordertech.wcomponents;

/**
 * Heading level types.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public enum HeadingLevel {
	/**
	 * Heading - Level 1.
	 */
	H1(1),
	/**
	 * Heading - Level 2.
	 */
	H2(2),
	/**
	 * Heading - Level 3.
	 */
	H3(3),
	/**
	 * Heading - Level 4.
	 */
	H4(4),
	/**
	 * Heading - Level 5.
	 */
	H5(5),
	/**
	 * Heading - Level 6.
	 */
	H6(6);

	/**
	 * The heading level.
	 */
	private final int level;

	/**
	 * @param level the heading level
	 */
	HeadingLevel(final int level) {
		this.level = level;
	}

	/**
	 * @return the heading level
	 */
	public int getLevel() {
		return level;
	}

}
