package com.github.bordertech.wcomponents.examples.layout;

import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WText;

/**
 * <p>
 * A simple WComponent extension that draws a box around its content. This is to provide a quick visual guide to how the
 * various layout cells are sized.</p>
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
final class BoxComponent extends WPanel {

	/**
	 * Creates an empty BoxComponent.
	 */
	BoxComponent() {
		super(WPanel.Type.BOX);
	}

	/**
	 * Creates a BoxComponent with the specified text.
	 *
	 * @param text the text to display in the box.
	 */
	BoxComponent(final String text) {
		super(WPanel.Type.BOX);
		add(new WText(text));
	}
}
