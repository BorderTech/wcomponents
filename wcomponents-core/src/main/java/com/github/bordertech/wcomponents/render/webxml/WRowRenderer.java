package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.Renderer;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WRow;
import com.github.bordertech.wcomponents.XmlStringBuilder;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;

/**
 * {@link Renderer} for the {@link WRow} component.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
final class WRowRenderer extends AbstractWebXmlRenderer {

	/**
	 * Paints the given WButton.
	 *
	 * @param component the WRow to paint.
	 * @param renderContext the RenderContext to paint to.
	 */
	@Override
	public void doRender(final WComponent component, final WebXmlRenderContext renderContext) {
		WRow row = (WRow) component;
		XmlStringBuilder xml = renderContext.getWriter();
		int cols = row.getChildCount();
		int hgap = row.getHgap();

		if (cols > 0) {
			xml.appendTagOpen("ui:row");
			xml.appendAttribute("id", component.getId());
			xml.appendOptionalAttribute("class", component.getHtmlClass());
			xml.appendOptionalAttribute("track", component.isTracking(), "true");
			xml.appendOptionalAttribute("hgap", hgap > 0, hgap);
			xml.appendClose();

			// Render margin
			MarginRendererUtil.renderMargin(row, renderContext);

			paintChildren(row, renderContext);

			xml.appendEndTag("ui:row");
		}
	}
}
