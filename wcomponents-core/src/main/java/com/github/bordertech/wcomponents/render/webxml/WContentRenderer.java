package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.Renderer;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WContent;
import com.github.bordertech.wcomponents.XmlStringBuilder;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The {@link Renderer} for {@link WContent}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
final class WContentRenderer extends AbstractWebXmlRenderer {

	/**
	 * The logger instance for this class.
	 */
	private static final Log LOG = LogFactory.getLog(WContentRenderer.class);

	/**
	 * <p>
	 * Paints the given WContent.</p>
	 *
	 * <p>
	 * This paint method outputs a popup that opens browser window in which the content document will be displayed. The
	 * component is only rendered for requests in which the display() method of the content component has just been
	 * called.</p>
	 *
	 * <p>
	 * WContent's handleRequest() method will return the actual PDF document content via the use of an Escape.</p>
	 *
	 * @param component the WContent to paint.
	 * @param renderContext the RenderContext to paint to.
	 */
	@Override
	public void doRender(final WComponent component, final WebXmlRenderContext renderContext) {
		WContent content = (WContent) component;
		XmlStringBuilder xml = renderContext.getWriter();

		if (!content.isDisplayRequested()) {
			// This is the normal situation.
			return;
		}

		Object contentAccess = content.getContentAccess();

		if (contentAccess == null) {
			LOG.warn("No content specified");
			return;
		}

		// Ok, the content is available and should be shown
		switch (content.getDisplayMode()) {
			case DISPLAY_INLINE:
			case PROMPT_TO_SAVE:
				xml.appendTagOpen("ui:redirect");
				xml.appendAttribute("url", content.getUrl());
				xml.appendEnd();
				break;

			case OPEN_NEW_WINDOW:
				xml.appendTagOpen("ui:popup");
				xml.appendAttribute("url", content.getUrl());
				xml.appendAttribute("width", content.getWidth().replaceAll("px", ""));
				xml.appendAttribute("height", content.getHeight().replaceAll("px", ""));
				xml.appendOptionalAttribute("resizable", content.isResizable(), "true");
				xml.appendEnd();
				break;

			default:
				throw new IllegalStateException("Invalid display mode: " + content.getDisplayMode());
		}
	}
}
