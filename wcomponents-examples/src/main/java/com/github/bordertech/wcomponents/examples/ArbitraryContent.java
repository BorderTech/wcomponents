package com.github.bordertech.wcomponents.examples;

import com.github.bordertech.wcomponents.WContainer;

/**
 * <p>
 * This component generates paragraphs of text which can be used as arbitrary content to assist with development of
 * wcomponent examples.</p>
 *
 * <p>
 * The "size" determines how many paragraphs of text are displayed.</p>
 *
 * @author Martin Shevchenko
 */
public class ArbitraryContent extends WContainer {

	/**
	 * The number of paragraphs of text to display.
	 */
	private final int size;

	/**
	 * Creates an ArbitraryContent with two paragraphs of text.
	 */
	public ArbitraryContent() {
		this(2);
	}

	/**
	 * Creates an ArbitraryContent with the given number of paragraphs of text.
	 *
	 * @param size the number of paragraphs of text to display.
	 */
	public ArbitraryContent(final int size) {
		setTemplate(ArbitraryContent.class);
		this.size = size;
	}

	/**
	 * @return Returns the size.
	 */
	public int getSize() {
		return size;
	}
}
