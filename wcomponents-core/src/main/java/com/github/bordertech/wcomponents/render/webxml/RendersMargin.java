package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.Margin;
import com.github.bordertech.wcomponents.Marginable;
import com.github.bordertech.wcomponents.Size;
import com.github.bordertech.wcomponents.XmlStringBuilder;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;

/**
 * Renders margins for instances of Marginable.
 * 
 * @author John McGuinness
 */
public interface RendersMargin {

	/**
	 * @param component the marginable component to paint a margin
	 * @param renderContext the RenderContext to paint to.
	 */
	public default void renderMargin(final Marginable component,
			final WebXmlRenderContext renderContext) {
		Margin margin = component.getMargin();
		if (margin == null) {
			return;
		}

		XmlStringBuilder xml = renderContext.getWriter();

		if (margin.getMargin() != null) {
			xml.appendTagOpen("ui:margin");
			xml.appendAttribute("all", margin.getMargin().toString());
			xml.appendEnd();
		} else if (margin.getTop() != null || margin.getRight() != null || margin.getBottom() != null || margin.getLeft() != null) {
			xml.appendTagOpen("ui:margin");
			Size size = margin.getTop();
			if (size != null) {
				xml.appendAttribute("north", size.toString());
			}
			size = margin.getRight();
			if (size != null) {
				xml.appendAttribute("east", size.toString());
			}
			size = margin.getBottom();
			if (size != null) {
				xml.appendAttribute("south", size.toString());
			}
			size = margin.getLeft();
			if (size != null) {
				xml.appendAttribute("west", size.toString());
			}
			xml.appendEnd();
		}
	}
}
