package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WMessageBox;
import com.github.bordertech.wcomponents.XmlStringBuilder;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;

/**
 * The Renderer for the {@link WMessageBox} component.
 *
 * @author Yiannis Paschalidis
 * @author Jonathan Austin
 * @author Mark Reeves
 * @since 1.0.0
 */
final class WMessageBoxRenderer extends AbstractWebXmlRenderer {

	/**
	 * Paints the given WMessageBox.
	 *
	 * @param component the WMessageBox to paint.
	 * @param renderContext the RenderContext to paint to.
	 */
	@Override
	public void doRender(final WComponent component, final WebXmlRenderContext renderContext) {
		WMessageBox messageBox = (WMessageBox) component;
		XmlStringBuilder xml = renderContext.getWriter();

		if (messageBox.hasMessages()) {
			xml.appendTagOpen("ui:messageBox");
			xml.appendAttribute("id", component.getId());
			xml.appendOptionalAttribute("class", component.getHtmlClass());
			xml.appendOptionalAttribute("track", component.isTracking(), "true");

			switch (messageBox.getType()) {
				case SUCCESS:
					xml.appendOptionalAttribute("type", "success");
					break;

				case INFO:
					xml.appendOptionalAttribute("type", "info");
					break;

				case WARN:
					xml.appendOptionalAttribute("type", "warn");
					break;

				case ERROR:
				default:
					xml.appendOptionalAttribute("type", "error");
					break;
			}

			xml.appendOptionalAttribute("title", messageBox.getTitleText());

			xml.appendClose();

			for (String message : messageBox.getMessages()) {
				xml.appendTag("ui:message");
				xml.print(message);
				xml.appendEndTag("ui:message");
			}

			xml.appendEndTag("ui:messageBox");
		}
	}
}
