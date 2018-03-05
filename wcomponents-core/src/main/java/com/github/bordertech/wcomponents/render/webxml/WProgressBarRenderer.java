package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.Renderer;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WProgressBar;
import com.github.bordertech.wcomponents.XmlStringBuilder;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;

/**
 * {@link Renderer} for the {@link WProgressBar} component.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
final class WProgressBarRenderer extends AbstractWebXmlRenderer {

	/**
	 * Paints the given WProgressBar.
	 *
	 * @param component the WProgressBar to paint.
	 * @param renderContext the RenderContext to paint to.
	 */
	@Override
	public void doRender(final WComponent component, final WebXmlRenderContext renderContext) {
		WProgressBar progressBar = (WProgressBar) component;
		XmlStringBuilder xml = renderContext.getWriter();

		xml.appendTagOpen("html:progress");
		xml.appendAttribute("id", component.getId());
		xml.appendAttribute("class", getHtmlClass(progressBar));
		xml.appendOptionalAttribute("hidden", progressBar.isHidden(), "hidden");
		xml.appendOptionalAttribute("title", progressBar.getToolTip());
		xml.appendOptionalAttribute("aria-label", progressBar.getAccessibleText());
		xml.appendAttribute("value", progressBar.getValue());
		xml.appendOptionalAttribute("max", progressBar.getMax() > 0, progressBar.getMax());
		xml.appendClose();
		xml.appendEndTag("html:progress");
	}

	/**
	 * @param progressBar the WProgressBar being rendered
	 * @return the value of the HTML class attribute for the progress element being rendered
	 */
	private String getHtmlClass(final WProgressBar progressBar) {
		StringBuilder builder = new StringBuilder("wc-progressbar");
		if (!WProgressBar.DEFAULT_TYPE.equals(progressBar.getProgressBarType())) {
			builder.append(" wc-progressbar-type-small");
		}
		return builder.toString();
	}
}
