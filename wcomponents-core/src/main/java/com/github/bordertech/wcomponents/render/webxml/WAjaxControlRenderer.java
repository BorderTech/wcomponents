package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.AjaxTarget;
import com.github.bordertech.wcomponents.Renderer;
import com.github.bordertech.wcomponents.WAjaxControl;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.XmlStringBuilder;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;

/**
 * The {@link Renderer} for {@link WAjaxControl}.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
final class WAjaxControlRenderer extends AbstractWebXmlRenderer {

	/**
	 * Paints the given AjaxControl.
	 *
	 * @param component the AjaxControl to paint.
	 * @param renderContext the RenderContext to paint to.
	 */
	@Override
	public void doRender(final WComponent component, final WebXmlRenderContext renderContext) {
		WAjaxControl ajaxControl = (WAjaxControl) component;
		XmlStringBuilder xml = renderContext.getWriter();
		WComponent trigger = ajaxControl.getTrigger() == null ? ajaxControl : ajaxControl.
				getTrigger();
		int loadCount = ajaxControl.getLoadCount();
		int delay = ajaxControl.getDelay();

		if (ajaxControl.getTargets() == null || ajaxControl.getTargets().isEmpty()) {
			return;
		}

		// Start tag
		xml.appendTagOpen("ui:ajaxtrigger");
		xml.appendAttribute("triggerId", trigger.getId());
		xml.appendOptionalAttribute("allowedUses", loadCount > 0, loadCount);
		xml.appendOptionalAttribute("delay", delay > 0, delay);
		xml.appendClose();

		// Targets
		for (AjaxTarget target : ajaxControl.getTargets()) {
			xml.appendTagOpen("ui:ajaxtargetid");
			xml.appendAttribute("targetId", target.getId());
			xml.appendEnd();
		}

		// End tag
		xml.appendEndTag("ui:ajaxtrigger");
	}

}
