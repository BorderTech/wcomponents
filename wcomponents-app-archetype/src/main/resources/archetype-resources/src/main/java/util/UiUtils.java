package ${package}.util;

import com.github.bordertech.wcomponents.Margin;

/**
 * Provides some common user interface components.
 */
public class UiUtils {

	/** An inter-component gap */
	public static final int MEDIUM_GAP = 12;

	/** A large gap */
	public static final int LARGE_GAP = 24;

	/**
	 * @return A large margin.
	 */
	public static final Margin getBigMargin() {
		return new Margin(LARGE_GAP);
	}

	/**
	 * @return A medium margin.
	 */
	public static final Margin getMediumMargin() {
		return new Margin(MEDIUM_GAP);
	}

	public static final int LABEL_WIDTH = 25;

	/** Do not allow instantiation */
	private UiUtils() {
	}
}

