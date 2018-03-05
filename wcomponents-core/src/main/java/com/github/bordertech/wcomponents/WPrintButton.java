package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.util.InternalMessages;

/**
 * This button opens a client side browser print window.
 *
 * @author Martin Shevchenko
 * @since 1.0.0
 */
public class WPrintButton extends WButton {

	/**
	 * Creates a WPrintButton with the default button text.
	 */
	public WPrintButton() {
		this(InternalMessages.DEFAULT_PRINT_BUTTON_TEXT);
	}

	/**
	 * Creates a WPrintButton with the given button text.
	 *
	 * @param text the text to display on the button.
	 */
	public WPrintButton(final String text) {
		super(text);
	}
}
