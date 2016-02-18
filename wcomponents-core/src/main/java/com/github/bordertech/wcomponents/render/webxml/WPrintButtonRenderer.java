package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.Renderer;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WPrintButton;

/**
 * The {@link Renderer} for {@link WPrintButton}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
final class WPrintButtonRenderer extends WButtonRenderer {

	/**
	 * Override to change the main tag.
	 *
	 * @param button the WPrintButtonLayout being painted.
	 * @return the main tag name
	 */
	@Override
	protected String getTagName(final WButton button) {
		return "ui:printbutton";
	}
}
