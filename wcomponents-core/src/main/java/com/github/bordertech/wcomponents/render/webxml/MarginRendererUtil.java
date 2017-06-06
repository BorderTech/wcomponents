package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.Margin;
import com.github.bordertech.wcomponents.Marginable;
import com.github.bordertech.wcomponents.Size;
import com.github.bordertech.wcomponents.XmlStringBuilder;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;

/**
 * Utility methods for rendering margin element.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public final class MarginRendererUtil {

	/* *
	 * The prefix for all margin HTML classes.
	private static final String CLASS_PREFIX = "wc-margin-";
	 */

	/* *
	 * Build the individual HTML className values for each part of a margin.
	 * @param size the margin size
	 * @param where a representation of the margin position
	 * @return a HTML className string
	private String buildSizeClass(final Size size, final String where) {
		if (size == null) {
			return null;
		}

		StringBuffer buffer = new StringBuffer(CLASS_PREFIX);
		buffer.append(where);
		buffer.append(size.toString());
		return buffer.toString();
	} */

	/* *
	 * Convert a Margin to a WComponents HTML className string.
	 * @param margin the current margin
	 * @return the className(s) appropriate for the margin.
	private String marginToClassName(final Margin margin) {
		Size size = margin.getMargin();
		boolean appendSpace = false;
		if (size != null) {
			return buildSizeClass(size, "all");
		}

		StringBuffer buffer = new StringBuffer();
		size = margin.getTop();
		if (size != null) {
			buffer.append(buildSizeClass(size, "n"));
			appendSpace = true;
		}
		size = margin.getRight();
		if (size != null) {
			buffer.append(appendSpace ? " " : "");
			buffer.append(buildSizeClass(size, "e"));
			appendSpace = true;
		}
		size = margin.getBottom();
		if (size != null) {
			buffer.append(appendSpace ? " " : "");
			buffer.append(buildSizeClass(size, "s"));
			appendSpace = true;
		}
		size = margin.getLeft();
		if (size != null) {
			buffer.append(appendSpace ? " " : "");
			buffer.append(buildSizeClass(size, "w"));
		}
		return buffer.toString();
	}
	 */

	/**
	 * Prevent instantiation of utility class.
	 */
	private MarginRendererUtil() {
		// Do nothing
	}

	/**
	 * @param component the marginable component to paint a margin
	 * @param renderContext the RenderContext to paint to.
	 */
	public static void renderMargin(final Marginable component,
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
