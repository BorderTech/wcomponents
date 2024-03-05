package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WComponentGroup;
import com.github.bordertech.wcomponents.XmlStringBuilder;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;
import java.util.List;

/**
 * The Renderer for {@link WComponentGroup}.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
final class WComponentGroupRenderer extends AbstractWebXmlRenderer {
	public static final String TAG_GROUP = "wc-componentgroup";
	public static final String TAG_COMPONENT = "wc-component";

	/**
	 * Paints the given WComponentGroup.
	 *
	 * @param component the WComponentGroup to paint.
	 * @param renderContext the RenderContext to paint to.
	 */
	@Override
	public void doRender(final WComponent component, final WebXmlRenderContext renderContext) {
		WComponentGroup group = (WComponentGroup) component;
		XmlStringBuilder xml = renderContext.getWriter();
		List<WComponent> components = group.getComponents();

		if (components != null && !components.isEmpty()) {
			xml.appendTagOpen(TAG_GROUP);
			xml.appendAttribute("id", component.getId());
			xml.appendOptionalAttribute("track", component.isTracking(), "true");
			xml.appendClose();

			for (WComponent comp : components) {
				xml.appendTagOpen(TAG_COMPONENT);
				xml.appendAttribute("refid", comp.getId());
				xml.appendClose();
				xml.appendEndTag(TAG_COMPONENT);
			}

			xml.appendEndTag(TAG_GROUP);
		}
	}
}
