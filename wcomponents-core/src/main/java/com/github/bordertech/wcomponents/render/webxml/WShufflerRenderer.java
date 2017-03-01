package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.Renderer;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WShuffler;
import com.github.bordertech.wcomponents.XmlStringBuilder;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;
import java.util.List;

/**
 * The {@link Renderer} for {@link WShuffler}.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
final class WShufflerRenderer extends AbstractWebXmlRenderer {

	/**
	 * Paints the given WShuffler.
	 *
	 * @param component the WShuffler to paint.
	 * @param renderContext the RenderContext to paint to.
	 */
	@Override
	public void doRender(final WComponent component, final WebXmlRenderContext renderContext) {
		WShuffler shuffler = (WShuffler) component;
		XmlStringBuilder xml = renderContext.getWriter();
		boolean readOnly = shuffler.isReadOnly();

		// Start tag
		xml.appendTagOpen("ui:shuffler");
		xml.appendAttribute("id", component.getId());
		xml.appendOptionalAttribute("class", component.getHtmlClass());
		xml.appendOptionalAttribute("track", component.isTracking(), "true");
		xml.appendOptionalAttribute("hidden", shuffler.isHidden(), "true");
		if (readOnly) {
			xml.appendAttribute("readOnly", "true");
		} else {
			xml.appendOptionalAttribute("disabled", shuffler.isDisabled(), "true");
			xml.appendOptionalAttribute("tabindex", component.hasTabIndex(), component.getTabIndex());
			xml.appendOptionalAttribute("toolTip", shuffler.getToolTip());
			xml.appendOptionalAttribute("accessibleText", shuffler.getAccessibleText());
			int rows = shuffler.getRows();
			xml.appendOptionalAttribute("rows", rows > 0, rows);
		}
		xml.appendClose();

		// Options
		List<?> options = shuffler.getOptions();

		if (options != null && !options.isEmpty()) {
			for (int i = 0; i < options.size(); i++) {
				String stringOption = String.valueOf(options.get(i));
				xml.appendTagOpen("ui:option");
				xml.appendAttribute("value", stringOption);
				xml.appendClose();
				xml.appendEscaped(stringOption);
				xml.appendEndTag("ui:option");
			}
		}

		// End tag
		xml.appendEndTag("ui:shuffler");
	}
}
