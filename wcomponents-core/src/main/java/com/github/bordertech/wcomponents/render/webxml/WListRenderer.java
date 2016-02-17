package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.UIContext;
import com.github.bordertech.wcomponents.UIContextHolder;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WList;
import com.github.bordertech.wcomponents.XmlStringBuilder;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;
import com.github.bordertech.wcomponents.util.SystemException;
import java.util.List;

/**
 * The Renderer for the {@link WList} component.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
final class WListRenderer extends AbstractWebXmlRenderer {

	/**
	 * Paints the given WList.
	 *
	 * @param component the WList to paint.
	 * @param renderContext the RenderContext to paint to.
	 */
	@Override
	public void doRender(final WComponent component, final WebXmlRenderContext renderContext) {
		WList list = (WList) component;
		XmlStringBuilder xml = renderContext.getWriter();
		WList.Type type = list.getType();
		WList.Separator separator = list.getSeparator();
		int hgap = list.getHgap();
		int vgap = list.getVgap();

		xml.appendTagOpen("ui:panel");
		xml.appendAttribute("id", component.getId());
		xml.appendOptionalAttribute("class", component.getHtmlClass());
		xml.appendOptionalAttribute("track", component.isTracking(), "true");
		xml.appendOptionalAttribute("type", list.isRenderBorder(), "box");
		xml.appendClose();

		// Render margin
		MarginRendererUtil.renderMargin(list, renderContext);

		xml.appendTagOpen("ui:listlayout");
		xml.appendOptionalAttribute("hgap", hgap > 0, hgap);
		xml.appendOptionalAttribute("vgap", vgap > 0, vgap);

		if (type != null) {
			switch (type) {
				case FLAT:
					xml.appendAttribute("type", "flat");
					break;

				case STACKED:
					xml.appendAttribute("type", "stacked");
					break;

				case STRIPED:
					xml.appendAttribute("type", "striped");
					break;

				default:
					throw new SystemException("Unknown list type: " + type);
			}
		}

		if (separator != null) {
			switch (separator) {
				case BAR:
					xml.appendAttribute("separator", "bar");
					break;

				case DOT:
					xml.appendAttribute("separator", "dot");
					break;

				case NONE:
					break;

				default:
					throw new SystemException("Unknown list type: " + type);
			}
		}

		xml.appendClose();

		paintRows(list, renderContext);

		xml.appendEndTag("ui:listlayout");
		xml.appendEndTag("ui:panel");
	}

	/**
	 * Paints the rows.
	 *
	 * @param list the WList to paint the rows for.
	 * @param renderContext the RenderContext to paint to.
	 */
	protected void paintRows(final WList list, final WebXmlRenderContext renderContext) {
		List<?> beanList = list.getBeanList();
		WComponent row = list.getRepeatedComponent();
		XmlStringBuilder xml = renderContext.getWriter();

		for (int i = 0; i < beanList.size(); i++) {
			Object rowData = beanList.get(i);

			// Each row has its own context. This is why we can reuse the same
			// WComponent instance for each row.
			UIContext rowContext = list.getRowContext(rowData, i);

			UIContextHolder.pushContext(rowContext);

			try {
				xml.appendTag("ui:cell");
				row.paint(renderContext);
				xml.appendEndTag("ui:cell");
			} finally {
				UIContextHolder.popContext();
			}
		}
	}
}
