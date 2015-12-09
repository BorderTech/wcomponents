package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WInternalLink;
import com.github.bordertech.wcomponents.XmlStringBuilder;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;
import com.github.bordertech.wcomponents.util.Util;

/**
 * The Renderer for {@link WInternalLink}.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
final class WInternalLinkRenderer extends AbstractWebXmlRenderer {

	/**
	 * Paints the given {@link WInternalLink}.
	 *
	 * @param component the WInternalLink to paint.
	 * @param renderContext the RenderContext to paint to.
	 */
	@Override
	public void doRender(final WComponent component, final WebXmlRenderContext renderContext) {
		WInternalLink link = (WInternalLink) component;
		XmlStringBuilder xml = renderContext.getWriter();

		if (Util.empty(link.getText())) {
			return;
		}

		xml.appendTagOpen("ui:link");
		xml.appendAttribute("id", component.getId());
		xml.appendOptionalAttribute("class", component.getHtmlClass());
		xml.appendOptionalAttribute("track", component.isTracking(), "true");
		xml.appendOptionalAttribute("toolTip", link.getToolTip());
		xml.appendOptionalAttribute("accessibleText", link.getAccessibleText());
		xml.appendAttribute("url", "#" + link.getReference().getId());
		xml.appendClose();
		xml.appendEscaped(link.getText());
		xml.appendEndTag("ui:link");

	}
}
