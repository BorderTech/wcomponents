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

	public static final String WC_AJAXTRIGGER = "wc-ajaxtrigger";
	public static final String WC_AJAXTARGETID = "wc-ajaxtargetid";

	/**
	 * Paints the given AjaxControl.
	 *
	 * @param component the AjaxControl to paint
	 * @param renderContext the RenderContext to paint to
	 */
	@Override
	public void doRender(final WComponent component, final WebXmlRenderContext renderContext) {
		WAjaxControl ajaxControl = (WAjaxControl) component;
		XmlStringBuilder xml = renderContext.getWriter();
		WComponent trigger = ajaxControl.getTrigger() == null ? ajaxControl : ajaxControl.
				getTrigger();
		int delay = ajaxControl.getDelay();

		if (ajaxControl.getTargets() == null || ajaxControl.getTargets().isEmpty()) {
			return;
		}

		// Start tag
		xml.appendTagOpen(WC_AJAXTRIGGER);
		xml.appendAttribute("triggerId", trigger.getId());
		xml.appendOptionalAttribute("loadOnce", ajaxControl.isLoadOnce(), "true");
		xml.appendOptionalAttribute("delay", delay > 0, delay);
		xml.appendClose();

		// Targets
		for (AjaxTarget target : ajaxControl.getTargets()) {
			xml.appendTagOpen(WC_AJAXTARGETID);
			xml.appendAttribute("targetId", target.getId());
			xml.appendClose();
			xml.appendEndTag(WC_AJAXTARGETID);
		}

		// End tag
		xml.appendEndTag(WC_AJAXTRIGGER);
	}

}
