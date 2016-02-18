package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WMenuItemGroup;
import com.github.bordertech.wcomponents.XmlStringBuilder;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;

/**
 * The Renderer for {@link WMenuItemGroup}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
final class WMenuItemGroupRenderer extends AbstractWebXmlRenderer {

	/**
	 * Paints the given WMenuItemGroup.
	 *
	 * @param component the WMenuItemGroup to paint.
	 * @param renderContext the RenderContext to paint to.
	 */
	@Override
	public void doRender(final WComponent component, final WebXmlRenderContext renderContext) {
		WMenuItemGroup group = (WMenuItemGroup) component;
		XmlStringBuilder xml = renderContext.getWriter();

		xml.appendTagOpen("ui:menugroup");
		xml.appendAttribute("id", component.getId());
		xml.appendOptionalAttribute("class", component.getHtmlClass());
		xml.appendOptionalAttribute("track", component.isTracking(), "true");
		xml.appendClose();

		paintChildren(group, renderContext);

		xml.appendEndTag("ui:menugroup");
	}
}
