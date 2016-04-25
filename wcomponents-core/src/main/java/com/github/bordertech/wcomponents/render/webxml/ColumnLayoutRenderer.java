package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.Renderer;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.XmlStringBuilder;
import com.github.bordertech.wcomponents.layout.ColumnLayout;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;

/**
 * This {@link Renderer} renders the children of a {@link WPanel} which have been arranged using a {@link ColumnLayout}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
final class ColumnLayoutRenderer extends AbstractWebXmlRenderer {

	/**
	 * Paints the given WPanel's children.
	 *
	 * @param component the container to paint.
	 * @param renderContext the RenderContext to paint to.
	 */
	@Override
	public void doRender(final WComponent component, final WebXmlRenderContext renderContext) {
		WPanel panel = (WPanel) component;
		XmlStringBuilder xml = renderContext.getWriter();
		ColumnLayout layout = (ColumnLayout) panel.getLayout();
		int childCount = panel.getChildCount();
		int hgap = layout.getHgap();
		int vgap = layout.getVgap();
		int cols = layout.getColumnCount();

		xml.appendTagOpen("ui:columnlayout");
		xml.appendOptionalAttribute("hgap", hgap > 0, hgap);
		xml.appendOptionalAttribute("vgap", vgap > 0, vgap);
		xml.appendClose();

		// Column Definitions
		for (int col = 0; col < cols; col++) {
			xml.appendTagOpen("ui:column");
			int width = layout.getColumnWidth(col);
			xml.appendOptionalAttribute("width", width > 0, width);

			switch (layout.getColumnAlignment(col)) {
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
					throw new IllegalArgumentException("Invalid alignment: " + layout.
							getColumnAlignment(col));
			}

			xml.appendEnd();
		}

		for (int i = 0; i < childCount; i++) {
			xml.appendTag("ui:cell");
			WComponent child = panel.getChildAt(i);
			child.paint(renderContext);
			xml.appendEndTag("ui:cell");
		}

		xml.appendEndTag("ui:columnlayout");
	}
}
