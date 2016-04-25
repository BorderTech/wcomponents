package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.UIContext;
import com.github.bordertech.wcomponents.UIContextHolder;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WRepeater;
import com.github.bordertech.wcomponents.XmlStringBuilder;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;
import java.util.List;

/**
 * The Renderer for WRepeater.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
final class WRepeaterRenderer extends AbstractWebXmlRenderer {

	/**
	 * Paints the given WText.
	 *
	 * @param component the WText to paint.
	 * @param renderContext the RenderContext to paint to.
	 */
	@Override
	public void doRender(final WComponent component, final WebXmlRenderContext renderContext) {
		WRepeater repeater = (WRepeater) component;
		XmlStringBuilder xml = renderContext.getWriter();

		xml.appendTagOpen("ui:panel");
		xml.appendAttribute("id", component.getId());
		xml.appendOptionalAttribute("class", component.getHtmlClass());
		xml.appendOptionalAttribute("track", component.isTracking(), "true");
		xml.appendClose();

		xml.appendTag("ui:content");
		paintRows(repeater, renderContext);
		xml.appendEndTag("ui:content");

		xml.appendEndTag("ui:panel");
	}

	/**
	 * Paints the rows.
	 *
	 * @param repeater the repeater to paint the rows for.
	 * @param renderContext the RenderContext to paint to.
	 */
	protected void paintRows(final WRepeater repeater, final WebXmlRenderContext renderContext) {
		List<?> beanList = repeater.getBeanList();
		WComponent row = repeater.getRepeatedComponent();

		for (int i = 0; i < beanList.size(); i++) {
			Object rowData = beanList.get(i);

			// Each row has its own context. This is why we can reuse the same
			// WComponent instance for each row.
			UIContext rowContext = repeater.getRowContext(rowData, i);
			UIContextHolder.pushContext(rowContext);

			try {
				row.paint(renderContext);
			} finally {
				UIContextHolder.popContext();
			}
		}
	}
}
