package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WLabel;
import com.github.bordertech.wcomponents.XmlStringBuilder;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;
import com.github.bordertech.wcomponents.util.Util;

/**
 * The Renderer for {@link WLabel}.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
final class WLabelRenderer extends AbstractWebXmlRenderer {

	/**
	 * Paints the given {@link WLabel}.
	 *
	 * @param component the WLabel to paint.
	 * @param renderContext the RenderContext to paint to.
	 */
	@Override
	public void doRender(final WComponent component, final WebXmlRenderContext renderContext) {
		WLabel label = (WLabel) component;
		XmlStringBuilder xml = renderContext.getWriter();

		xml.appendTagOpen("ui:label");
		xml.appendAttribute("id", component.getId());
		xml.appendOptionalAttribute("class", component.getHtmlClass());
		xml.appendOptionalAttribute("track", component.isTracking(), "true");
		xml.appendOptionalAttribute("for", label.getLabelFor());
		xml.appendOptionalAttribute("hint", label.getHint());
		xml.appendOptionalAttribute("accessKey", Util.upperCase(label.getAccessKeyAsString()));
		xml.appendOptionalAttribute("hidden", label.isHidden(), "true");
		xml.appendOptionalAttribute("toolTip", label.getToolTip());
		xml.appendOptionalAttribute("accessibleText", label.getAccessibleText());
		xml.appendClose();

		xml.append(label.getText(), label.isEncodeText());

		paintChildren(label, renderContext);

		xml.appendEndTag("ui:label");
	}

}
