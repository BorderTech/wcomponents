package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.Renderer;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WFieldLayout;
import com.github.bordertech.wcomponents.XmlStringBuilder;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;

/**
 * {@link Renderer} for the {@link WFieldLayout} component.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
final class WFieldLayoutRenderer extends AbstractWebXmlRenderer {

	/**
	 * Paints the given WFieldLayout.
	 *
	 * @param component the WFieldLayout to paint.
	 * @param renderContext the RenderContext to paint to.
	 */
	@Override
	public void doRender(final WComponent component, final WebXmlRenderContext renderContext) {
		WFieldLayout fieldLayout = (WFieldLayout) component;
		XmlStringBuilder xml = renderContext.getWriter();
		int labelWidth = fieldLayout.getLabelWidth();
		String title = fieldLayout.getTitle();

		xml.appendTagOpen("ui:fieldlayout");
		xml.appendAttribute("id", component.getId());
		xml.appendOptionalAttribute("class", component.getHtmlClass());
		xml.appendOptionalAttribute("track", component.isTracking(), "true");
		xml.appendOptionalAttribute("hidden", fieldLayout.isHidden(), "true");
		xml.appendOptionalAttribute("labelWidth", labelWidth > 0, labelWidth);
		xml.appendAttribute("layout", fieldLayout.getLayoutType());
		xml.appendOptionalAttribute("title", title);
		// Ordered layout
		if (fieldLayout.isOrdered()) {
			xml.appendAttribute("ordered", fieldLayout.getOrderedOffset());
		}
		xml.appendClose();

		// Render margin
		MarginRendererUtil.renderMargin(fieldLayout, renderContext);

		// Paint Fields
		paintChildren(fieldLayout, renderContext);

		xml.appendEndTag("ui:fieldlayout");
	}
}
