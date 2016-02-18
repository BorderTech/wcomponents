package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.Renderer;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.XmlStringBuilder;
import com.github.bordertech.wcomponents.layout.ListLayout;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;

/**
 * This {@link Renderer} renders the children of a {@link WPanel} which have been arranged using a {@link ListLayout}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
final class ListLayoutRenderer extends AbstractWebXmlRenderer {

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
		ListLayout layout = (ListLayout) panel.getLayout();
		int childCount = panel.getChildCount();
		int hgap = layout.getHgap();
		int vgap = layout.getVgap();

		xml.appendTagOpen("ui:listlayout");

		switch (layout.getType()) {
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
				throw new IllegalArgumentException("Invalid type: " + layout.getType());
		}

		switch (layout.getAlignment()) {
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
				throw new IllegalArgumentException("Invalid alignment: " + layout.getAlignment());
		}

		switch (layout.getSeparator()) {
			case NONE:
				// none is assumed if omitted
				break;

			case BAR:
				xml.appendAttribute("separator", "bar");
				break;

			case DOT:
				xml.appendAttribute("separator", "dot");
				break;

			default:
				throw new IllegalArgumentException("Invalid separator: " + layout.getSeparator());
		}

		xml.appendOptionalAttribute("ordered", layout.isOrdered(), "true");

		xml.appendOptionalAttribute("hgap", hgap > 0, hgap);
		xml.appendOptionalAttribute("vgap", vgap > 0, vgap);

		xml.appendClose();

		// Paint children
		for (int i = 0; i < childCount; i++) {
			xml.appendTag("ui:cell");
			panel.getChildAt(i).paint(renderContext);
			xml.appendEndTag("ui:cell");
		}

		xml.appendEndTag("ui:listlayout");
	}
}
