package com.github.bordertech.wcomponents.util;

import com.github.bordertech.wcomponents.UIContext;
import com.github.bordertech.wcomponents.WebUtilities;

/**
 * Utility class for rendering file element.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public final class XMLUtil {

	/**
	 * Theme URI.
	 */
	public static final String THEME_URI = "https://github.com/bordertech/wcomponents/namespace/ui/v1.0";

	/**
	 * XHtml URI.
	 */
	public static final String XHTML_URI = "http://www.w3.org/1999/xhtml";

	/**
	 * XML Declaration.
	 */
	public static final String XML_DECLERATION = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";

	/**
	 * Doc Type to support nbsp.
	 */
	public static final String DOC_TYPE = "<!DOCTYPE validate [<!ENTITY nbsp \"&#160;\">]>";

	/**
	 * XML Declaration combined with Doc Type.
	 */
	public static final String XML_DECLERATION_WITH_DOC_TYPE = XML_DECLERATION + DOC_TYPE;

	/**
	 * UI Namespace attribute.
	 */
	public static final String UI_NAMESPACE = " xmlns:ui=\"" + THEME_URI + "\"";

	/**
	 * Standard Namespace attributes.
	 */
	public static final String STANDARD_NAMESPACES = " xmlns=\"" + XHTML_URI + "\" xmlns:html=\"" + XHTML_URI + "\""
			+ UI_NAMESPACE;

	/**
	 * Prevent instantiation of utility class.
	 */
	private XMLUtil() {
		// Do nothing
	}

	/**
	 * @param uic the current user context
	 * @return the xml declaration with the theme processing instruction
	 */
	public static String getXMLDeclarationWithThemeXslt(final UIContext uic) {
		String theme = WebUtilities.encode(ThemeUtil.getThemeXslt(uic));
		String dec = XML_DECLERATION + "\n<?xml-stylesheet type=\"text/xsl\" href=\"" + theme + "\"?>";
		return dec;
	}
}
