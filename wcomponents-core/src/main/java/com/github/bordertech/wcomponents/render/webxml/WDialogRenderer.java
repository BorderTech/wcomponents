package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.Renderer;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WDialog;
import com.github.bordertech.wcomponents.XmlStringBuilder;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;

/**
 * The {@link Renderer} for {@link WDialog}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
final class WDialogRenderer extends AbstractWebXmlRenderer {

	/**
	 * Paints the given WDialog.
	 *
	 * @param component the WDialog to paint.
	 * @param renderContext the RenderContext to paint to.
	 */
	@Override
	public void doRender(final WComponent component, final WebXmlRenderContext renderContext) {
		WDialog dialog = (WDialog) component;
		int state = dialog.getState();

		if (state == WDialog.ACTIVE_STATE || dialog.getTrigger() != null) {
			String title = dialog.getTitle();
			XmlStringBuilder xml = renderContext.getWriter();

			xml.appendTagOpen("ui:dialog");
			xml.appendAttribute("id", component.getId());
			xml.appendOptionalAttribute("class", component.getHtmlClass());
			xml.appendOptionalAttribute("track", component.isTracking(), "true");
			xml.appendOptionalAttribute("width", dialog.getWidth());
			xml.appendOptionalAttribute("height", dialog.getHeight());
			xml.appendOptionalAttribute("resizable", dialog.isResizable(), "true");
			xml.appendOptionalAttribute("modal", dialog.getMode() == WDialog.MODAL, "true");
			xml.appendOptionalAttribute("open", dialog.getState() == WDialog.ACTIVE_STATE, "true");
			xml.appendOptionalAttribute("title", title);

			if (dialog.getTrigger() == null) {
				xml.appendEnd();
			} else {
				xml.appendClose();
				dialog.getTrigger().paint(renderContext);
				xml.appendEndTag("ui:dialog");
			}
		}
	}
}
