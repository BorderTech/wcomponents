package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WSpan;
import com.github.bordertech.wcomponents.XmlStringBuilder;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;

/**
 * The Renderer for {@link WSpan}.
 */
class WSpanRenderer extends WTextRenderer {

	@Override
	public void doRender(final WComponent component, final WebXmlRenderContext renderContext) {

		WSpan span = (WSpan) component;
		XmlStringBuilder xml = renderContext.getWriter();

		xml.appendTagOpen("span");
		xml.appendAttribute("id", span.getId());
		xml.appendOptionalAttribute("class", span.getHtmlClass());
		xml.appendOptionalAttribute("hidden", span.isHidden(), "hidden");
		xml.appendOptionalAttribute("aria-hidden", span.isAriaHidden(), "true");
		xml.appendOptionalAttribute("aria-label", span.getAccessibleText());
		xml.appendOptionalAttribute("role", span.isImageRole(), "img");
		xml.appendClose();

		super.doRender(component, renderContext);

		xml.appendEndTag("span");
	}

}
