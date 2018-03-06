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

	@Override
	protected String geHtmlClassName(final WButton button) {
		StringBuffer htmlClassName = new StringBuffer("wc-printbutton");

		if (button.isRenderAsLink()) {
			htmlClassName.append(" wc-linkbutton");
		}
		String customButtonClassNames = button.getHtmlClass();
		if (customButtonClassNames != null) {
			htmlClassName.append(" ");
			htmlClassName.append(customButtonClassNames);
		}
		return htmlClassName.toString();
	}

	@Override
	protected String getButtonType(final WButton button) {
		return "button";
	}
}
