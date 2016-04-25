package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.Renderer;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WDecoratedLabel;
import com.github.bordertech.wcomponents.XmlStringBuilder;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;

/**
 * {@link Renderer} for the {@link WDecoratedLabel} component.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
final class WDecoratedLabelRenderer extends AbstractWebXmlRenderer {

	/**
	 * Paints the given WDecoratedLabel.
	 *
	 * @param component the WDecoratedLabel to paint.
	 * @param renderContext the RenderContext to paint to.
	 */
	@Override
	public void doRender(final WComponent component, final WebXmlRenderContext renderContext) {
		WDecoratedLabel label = (WDecoratedLabel) component;
		XmlStringBuilder xml = renderContext.getWriter();
		WComponent head = label.getHead();
		WComponent body = label.getBody();
		WComponent tail = label.getTail();

		xml.appendTagOpen("ui:decoratedlabel");
		xml.appendAttribute("id", component.getId());
		xml.appendOptionalAttribute("class", component.getHtmlClass());
		xml.appendOptionalAttribute("track", component.isTracking(), "true");
		xml.appendOptionalAttribute("hidden", label.isHidden(), "true");
		xml.appendClose();

		if (head != null && head.isVisible()) {
			xml.appendTagOpen("ui:labelhead");
			xml.appendAttribute("id", label.getId() + "-head");
			xml.appendClose();
			head.paint(renderContext);
			xml.appendEndTag("ui:labelhead");
		}

		xml.appendTagOpen("ui:labelbody");
		xml.appendAttribute("id", label.getId() + "-body");
		xml.appendClose();
		body.paint(renderContext);
		xml.appendEndTag("ui:labelbody");

		if (tail != null && tail.isVisible()) {
			xml.appendTagOpen("ui:labeltail");
			xml.appendAttribute("id", label.getId() + "-tail");
			xml.appendClose();
			tail.paint(renderContext);
			xml.appendEndTag("ui:labeltail");
		}

		xml.appendEndTag("ui:decoratedlabel");
	}

}
