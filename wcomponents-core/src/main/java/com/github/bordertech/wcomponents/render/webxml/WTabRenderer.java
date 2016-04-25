package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.AjaxHelper;
import com.github.bordertech.wcomponents.Renderer;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WTab;
import com.github.bordertech.wcomponents.WTabSet.TabMode;
import com.github.bordertech.wcomponents.XmlStringBuilder;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;
import com.github.bordertech.wcomponents.util.SystemException;

/**
 * {@link Renderer} for the {@link WTab} component.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
final class WTabRenderer extends AbstractWebXmlRenderer {

	/**
	 * Paints the given WTab.
	 *
	 * @param component the WTab to paint.
	 * @param renderContext the RenderContext to paint to.
	 */
	@Override
	public void doRender(final WComponent component, final WebXmlRenderContext renderContext) {
		WTab tab = (WTab) component;
		XmlStringBuilder xml = renderContext.getWriter();

		xml.appendTagOpen("ui:tab");
		xml.appendAttribute("id", component.getId());
		xml.appendOptionalAttribute("class", component.getHtmlClass());
		xml.appendOptionalAttribute("track", component.isTracking(), "true");
		xml.appendOptionalAttribute("open", tab.isOpen(), "true");
		xml.appendOptionalAttribute("disabled", tab.isDisabled(), "true");
		xml.appendOptionalAttribute("hidden", tab.isHidden(), "true");
		xml.appendOptionalAttribute("toolTip", tab.getToolTip());

		switch (tab.getMode()) {
			case CLIENT:
				xml.appendAttribute("mode", "client");
				break;
			case LAZY:
				xml.appendAttribute("mode", "lazy");
				break;
			case EAGER:
				xml.appendAttribute("mode", "eager");
				break;
			case DYNAMIC:
				xml.appendAttribute("mode", "dynamic");
				break;
			case SERVER:
				xml.appendAttribute("mode", "server");
				break;
			default:
				throw new SystemException("Unknown tab mode: " + tab.getMode());
		}

		if (tab.getAccessKey() != 0) {
			xml.appendAttribute("accessKey", String.valueOf(Character.
					toUpperCase(tab.getAccessKey())));
		}

		xml.appendClose();

		// Paint label
		tab.getTabLabel().paint(renderContext);

		// Paint content
		WComponent content = tab.getContent();

		xml.appendTagOpen("ui:tabcontent");
		xml.appendAttribute("id", tab.getId() + "-content");
		xml.appendClose();

		// Render content if not EAGER Mode or is EAGER and is the current AJAX trigger
		if (content != null && (TabMode.EAGER != tab.getMode() || AjaxHelper.isCurrentAjaxTrigger(
				tab))) {
			// Visibility of content set in prepare paint
			content.paint(renderContext);
		}

		xml.appendEndTag("ui:tabcontent");

		xml.appendEndTag("ui:tab");
	}

}
