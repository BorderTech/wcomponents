package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.util.InternalMessages;

/**
 * This button opens a client side browser print window. It can be rendered as a button or as a link.
 *
 * @author Martin Shevchenko
 * @since 1.0.0
 *
 * @deprecated: does not provide useful functionality.
 */
public class WPrintButton extends WButton {

	/**
	 * Creates a WPrintButton with the default button text.
	 *
	 * @deprecated provides no useful functionality
	 *
	 */
	public WPrintButton() {
		this(InternalMessages.DEFAULT_PRINT_BUTTON_TEXT);
	}

	/**
	 * Creates a WPrintButton with the given button text.
	 *
	 * @param text the text to display on the button.
	 * @deprecated provides no useful functionality
	 */
	public WPrintButton(final String text) {
		super(text);
	}
}
