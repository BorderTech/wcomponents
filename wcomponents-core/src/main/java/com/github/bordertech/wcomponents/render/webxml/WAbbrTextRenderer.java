package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.Renderer;
import com.github.bordertech.wcomponents.WAbbrText;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.XmlStringBuilder;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;

/**
 * {@link Renderer} for the {@link WAbbrText} component.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
final class WAbbrTextRenderer extends AbstractWebXmlRenderer {

	/**
	 * Paints the given WAbbrText.
	 *
	 * @param component the WAbbrText to paint.
	 * @param renderContext the RenderContext to paint to.
	 */
	@Override
	public void doRender(final WComponent component, final WebXmlRenderContext renderContext) {
		WAbbrText abbrText = (WAbbrText) component;
		XmlStringBuilder xml = renderContext.getWriter();

		xml.appendTagOpen("ui:abbr");
		xml.appendOptionalAttribute("toolTip", abbrText.getToolTip());
		xml.appendOptionalAttribute("class", abbrText.getHtmlClass());
		xml.appendClose();

		xml.appendEscaped(abbrText.getText());

		xml.appendEndTag("ui:abbr");
	}
}
