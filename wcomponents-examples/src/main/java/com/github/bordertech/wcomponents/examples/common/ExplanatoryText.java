package com.github.bordertech.wcomponents.examples.common;

import com.github.bordertech.wcomponents.WStyledText;

/**
 * Utility class to create a paragraph of text using {@link WStyledText}.
 *
 * @author Mark Reeves
 * @since 1.0.0
 */
public class ExplanatoryText extends WStyledText {

	/**
	 * Simple utility to create a paragraph of text. This is used rather a lot to add human readable text explanations
	 * to the WComponent example output.
	 */
	public ExplanatoryText() {
		this("", Type.PLAIN);
	}

	/**
	 * @param text the content of the paragraph
	 */
	public ExplanatoryText(final String text) {
		this(text, Type.PLAIN);
	}

	/**
	 * @param text The content of the paragraph
	 * @param type The render Type for the style.
	 */
	public ExplanatoryText(final String text, final Type type) {
		super(text, type);
		this.setWhitespaceMode(WhitespaceMode.PARAGRAPHS);
	}

}
