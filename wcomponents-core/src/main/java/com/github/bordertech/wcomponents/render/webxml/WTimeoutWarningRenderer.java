package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WTimeoutWarning;
import com.github.bordertech.wcomponents.XmlStringBuilder;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;

/**
 * The XML renderer for WTimeoutWarning.
 *
 * @author Mark Reeves
 * @since 1.0.0
 */
final class WTimeoutWarningRenderer extends AbstractWebXmlRenderer {

	/**
	 * Paints the given WTimeoutWarning if the component's timeout period is greater than 0.
	 *
	 * @param component the WTimeoutWarning to paint.
	 * @param renderContext the RenderContext to paint to.
	 */
	@Override
	public void doRender(final WComponent component, final WebXmlRenderContext renderContext) {
		WTimeoutWarning warning = (WTimeoutWarning) component;

		XmlStringBuilder xml = renderContext.getWriter();
		final int timoutPeriod = warning.getTimeoutPeriod();
		if (timoutPeriod > 0) {
			xml.appendTagOpen("ui:session");
			xml.appendAttribute("timeout", String.valueOf(timoutPeriod));
			int warningPeriod = warning.getWarningPeriod();
			xml.appendOptionalAttribute("warn", warningPeriod > 0, warningPeriod);
			xml.appendEnd();
		}

	}

}
