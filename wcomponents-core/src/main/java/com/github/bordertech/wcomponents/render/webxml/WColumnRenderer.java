package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.Renderer;
import com.github.bordertech.wcomponents.WColumn;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.XmlStringBuilder;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;

/**
 * {@link Renderer} for the {@link WColumn} component.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
final class WColumnRenderer extends AbstractWebXmlRenderer {

	/**
	 * Paints the given WButton.
	 *
	 * @param component the WColumn to paint.
	 * @param renderContext the RenderContext to paint to.
	 */
	@Override
	public void doRender(final WComponent component, final WebXmlRenderContext renderContext) {
		WColumn col = (WColumn) component;
		XmlStringBuilder xml = renderContext.getWriter();

		xml.appendTagOpen("ui:column");
		xml.appendAttribute("id", component.getId());
		xml.appendOptionalAttribute("class", component.getHtmlClass());
		xml.appendOptionalAttribute("track", component.isTracking(), "true");
		int width = col.getWidth();
		xml.appendOptionalAttribute("width", width > 0, width);

		switch (col.getAlignment()) {
			case LEFT:
				// left is assumed if omitted
				break;

			case RIGHT:
				xml.appendAttribute("align", "right");
				break;

			case CENTER:
				xml.appendAttribute("align", "center");
				break;

			default:
				throw new IllegalArgumentException("Invalid alignment: " + col.getAlignment());
		}

		xml.appendClose();

		// Paint column contents
		paintChildren(col, renderContext);

		xml.appendEndTag("ui:column");
	}
}
