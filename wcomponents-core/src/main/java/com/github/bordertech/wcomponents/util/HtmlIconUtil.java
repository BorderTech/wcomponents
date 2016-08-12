package com.github.bordertech.wcomponents.util;

/**
 * Provides helpers to attach icons to components.
 *
 * @author Mark Reeves
 * @since 1.2.1
 */
public final class HtmlIconUtil {

	/**
	 * Expose the HTML className to set an icon.
	 */
	public static final String CLASS_ICON = HtmlClassProperties.ICON.toString();

	/**
	 * Expose the HTML className to set an icon before content.
	 */
	public static final String CLASS_ICON_BEFORE_CONTENT = HtmlClassProperties.ICON_BEFORE.toString();

	/**
	 * Expose the HTML className to set an icon after content.
	 */
	public static final String CLASS_ICON_AFTER_CONTENT = HtmlClassProperties.ICON_AFTER.toString();

	/**
	 * Provides access to position icons in components.
	 */
	public enum IconPosition {
		/**
		 * Put an icon before other content.
		 */
		BEFORE,
		/**
		 * Put an icon after other content.
		 */
		AFTER,
		/**
		 * Put an icon in an undefined position for use as only content.
		 */
		UNDEFINED
	}

	/**
	 * Prevent instantiation.
	 */
	private HtmlIconUtil() {
	}

	/**
	 * Pass in a string HTML class name and get the correct WComponents icon HTML classes.
	 * @param icon the icon-specific HTML className
	 * @return a HTML className string.
	 */
	public static String getIconClasses(final String icon) {
		return getIconClasses(icon, null);
	}

	/**
	 * @param icon the HTML className representing an icon
	 * @param position where the icon should go in relation to the content of the component
	 * @return a HTML className String which will set up a component to contain an icon in the desired position.
	 */
	public static String getIconClasses(final String icon, final IconPosition position) {
		StringBuilder builder = new StringBuilder();
		if (position == null) {
			builder.append(CLASS_ICON);
		} else {
			switch (position) {
				case BEFORE:
					builder.append(CLASS_ICON_BEFORE_CONTENT);
					break;
				case AFTER:
					builder.append(CLASS_ICON_AFTER_CONTENT);
					break;
				default:
					builder.append(CLASS_ICON);
					break;
			}
		}
		if (!(icon == null || Util.empty(icon))) {
			builder.append(" ");
			builder.append(icon);
		}
		return builder.toString();
	}
}
