package com.github.bordertech.wcomponents;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This server side browser detection code is required as the client side browser detection code doesn't work for
 * WebSeal's URL re-writing. The client side browser detection uses Microsoft specific code that is embedded in
 * comments. The comments are ignored by all browsers except Microsoft browsers which secretly parses the comments for
 * special keywords. WebSeal behaves as any other normal browser and only URL re-writes html code (and ignores comments)
 * so the screen.css file cannot be located correctly as it's URL doesn't get re-written.
 *
 * @author Martin Shevchenko
 * @since 1.0.0
 */
public class UserAgentInfo implements Serializable {

	/**
	 * Indicates an unknown browser.
	 */
	private static final byte BROWSER_UNKNOWN = 0;
	/**
	 * Indicates an unspecified version of IE.
	 */
	private static final byte BROWSER_IE = 1;
	/**
	 * Indicates IE 6.
	 */
	private static final byte BROWSER_IE6 = 2;

	/**
	 * The browser version.
	 */
	private byte browserVersion = BROWSER_UNKNOWN;

	/**
	 * Creates a UserAgentInfo with an unknown browser.
	 */
	public UserAgentInfo() {
	}

	/**
	 * Creates a UserAgentInfo from the given browser header string.
	 *
	 * @param headerStr the browser header String.
	 */
	public UserAgentInfo(final String headerStr) {
		if (headerStr == null || headerStr.length() == 0) {
			return;
		}

		// Try to find Microsoft browsers by looking for MSIE.
		Pattern pattern = Pattern.compile("MSIE.(\\d+)");
		Matcher matcher = pattern.matcher(headerStr);

		if (matcher.find()) {
			browserVersion = BROWSER_IE;
			String ieMajorVersion = matcher.group(1);

			if ("6".equals(ieMajorVersion)) {
				browserVersion = BROWSER_IE6;
			}
		}
	}

	/**
	 * @return true if the browser is some version of IE.
	 */
	public boolean isIE() {
		return browserVersion == BROWSER_IE || browserVersion == BROWSER_IE6;
	}

	/**
	 * @return true if the browser is IE 6.
	 */
	public boolean isIE6() {
		return browserVersion == BROWSER_IE6;
	}

	/**
	 * @return true if the browser is unknown.
	 */
	public boolean isUnknown() {
		return browserVersion == BROWSER_UNKNOWN;
	}
}
