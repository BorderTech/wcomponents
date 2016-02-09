package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.AjaxHelper;
import com.github.bordertech.wcomponents.Renderer;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WSuggestions;
import com.github.bordertech.wcomponents.XmlStringBuilder;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;

/**
 * The {@link Renderer} for {@link WSuggestions}.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
final class WSuggestionsRenderer extends AbstractWebXmlRenderer {

	/**
	 * Paints the given WSuggestions.
	 *
	 * @param component the WSuggestions to paint.
	 * @param renderContext the RenderContext to paint to.
	 */
	@Override
	public void doRender(final WComponent component, final WebXmlRenderContext renderContext) {
		WSuggestions suggestions = (WSuggestions) component;
		XmlStringBuilder xml = renderContext.getWriter();

		// Cache key for a lookup table
		String dataKey = suggestions.getListCacheKey();
		// Use AJAX if not using a cached list and have a refresh action
		boolean useAjax = dataKey == null && suggestions.getRefreshAction() != null;

		// Start tag
		xml.appendTagOpen("ui:suggestions");
		xml.appendAttribute("id", component.getId());
		xml.appendOptionalAttribute("class", component.getHtmlClass());
		xml.appendOptionalAttribute("track", component.isTracking(), "true");
		xml.appendOptionalAttribute("min", suggestions.getMinRefresh() > 0, suggestions.
				getMinRefresh());
		xml.appendOptionalAttribute("ajax", useAjax, "true");
		xml.appendOptionalAttribute("data", dataKey);
		WSuggestions.Autocomplete autocomplete = suggestions.getAutocomplete();
		if (autocomplete == WSuggestions.Autocomplete.LIST) {
			xml.appendOptionalAttribute("autocomplete", "list");
		}
		xml.appendClose();

		// Check if this is the current AJAX trigger
		boolean isTrigger = useAjax && AjaxHelper.isCurrentAjaxTrigger(suggestions);

		// Render suggestions
		if (isTrigger || (dataKey == null && !useAjax)) {
			for (String suggestion : suggestions.getSuggestions()) {
				xml.appendTagOpen("ui:suggestion");
				xml.appendAttribute("value", suggestion);
				xml.appendEnd();
			}
		}

		// End tag
		xml.appendEndTag("ui:suggestions");
	}
}
