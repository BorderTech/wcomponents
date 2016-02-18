package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WSelectToggle;
import com.github.bordertech.wcomponents.WSelectToggle.State;
import com.github.bordertech.wcomponents.XmlStringBuilder;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;

/**
 * The Renderer for {@link WSelectToggle}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
final class WSelectToggleRenderer extends AbstractWebXmlRenderer {

	/**
	 * Paints the given WSelectToggle.
	 *
	 * @param component the WSelectToggle to paint.
	 * @param renderContext the RenderContext to paint to.
	 */
	@Override
	public void doRender(final WComponent component, final WebXmlRenderContext renderContext) {
		WSelectToggle toggle = (WSelectToggle) component;
		XmlStringBuilder xml = renderContext.getWriter();

		xml.appendTagOpen("ui:selecttoggle");
		xml.appendAttribute("id", component.getId());
		xml.appendOptionalAttribute("class", component.getHtmlClass());
		xml.appendOptionalAttribute("track", component.isTracking(), "true");

		State state = toggle.getState();

		if (State.ALL.equals(state)) {
			xml.appendAttribute("selected", "all");
		} else if (State.NONE.equals(state)) {
			xml.appendAttribute("selected", "none");
		} else {
			xml.appendAttribute("selected", "some");
		}

		xml.appendOptionalAttribute("disabled", toggle.isDisabled(), "true");
		xml.appendAttribute("target", toggle.getTarget().getId());
		xml.appendAttribute("renderAs", toggle.isRenderAsText() ? "text" : "control");
		xml.appendOptionalAttribute("roundTrip", !toggle.isClientSide(), "true");
		xml.appendEnd();
	}
}
