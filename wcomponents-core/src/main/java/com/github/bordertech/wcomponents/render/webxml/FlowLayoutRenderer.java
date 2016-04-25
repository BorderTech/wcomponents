package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.Renderer;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.XmlStringBuilder;
import com.github.bordertech.wcomponents.layout.FlowLayout;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;

/**
 * This {@link Renderer} renders the children of a {@link WPanel} which have been arranged using a {@link FlowLayout}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
final class FlowLayoutRenderer extends AbstractWebXmlRenderer {

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
		FlowLayout layout = (FlowLayout) panel.getLayout();
		int hgap = layout.getHgap();
		int vgap = layout.getVgap();

		xml.appendTagOpen("ui:flowlayout");

		xml.appendOptionalAttribute("hgap", hgap > 0, hgap);
		xml.appendOptionalAttribute("vgap", vgap > 0, vgap);

		switch (layout.getAlignment()) {
			case RIGHT: {
				xml.appendAttribute("align", "right");
				break;
			}
			case CENTER: {
				xml.appendAttribute("align", "center");
				break;
			}
			case LEFT: {
				xml.appendAttribute("align", "left");
				break;
			}

			case VERTICAL:
				xml.appendAttribute("align", "vertical");
				break;

			default:
				throw new IllegalStateException("Unknown layout type: " + layout.getAlignment());
		}

		if (layout.getContentAlignment() != null) {
			switch (layout.getContentAlignment()) {
				case TOP: {
					xml.appendAttribute("valign", "top");
					break;
				}
				case MIDDLE: {
					xml.appendAttribute("valign", "middle");
					break;
				}
				case BASELINE: {
					xml.appendAttribute("valign", "baseline");
					break;
				}

				case BOTTOM:
					xml.appendAttribute("valign", "bottom");
					break;

				default:
					throw new IllegalStateException("Unknown content alignment type: " + layout.
							getContentAlignment());
			}
		}

		xml.appendClose();

		int size = panel.getChildCount();

		for (int i = 0; i < size; i++) {
			xml.appendTag("ui:cell");
			WComponent child = panel.getChildAt(i);
			child.paint(renderContext);
			xml.appendEndTag("ui:cell");
		}

		xml.appendEndTag("ui:flowlayout");
	}
}
