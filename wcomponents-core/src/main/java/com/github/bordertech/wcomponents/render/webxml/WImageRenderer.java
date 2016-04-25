package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.Renderer;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WImage;
import com.github.bordertech.wcomponents.XmlStringBuilder;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;
import com.github.bordertech.wcomponents.util.I18nUtilities;
import java.awt.Dimension;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * {@link Renderer} for the {@link WImage} component.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
final class WImageRenderer extends AbstractWebXmlRenderer {

	/**
	 * The logger instance for this class.
	 */
	private static final Log LOG = LogFactory.getLog(WImageRenderer.class);

	/**
	 * Builds the "open tag" part of the XML, that is the tagname and attributes.
	 *
	 * E.g. &lt;ui:image src="example.png" alt="some alt txt"
	 *
	 * The caller may then append any additional attributes and then close the XML tag.
	 *
	 * @param imageComponent The WImage to render.
	 * @param xml The buffer to render the XML into.
	 */
	protected static void renderTagOpen(final WImage imageComponent, final XmlStringBuilder xml) {

		// No image set
		if (imageComponent.getImage() == null && imageComponent.getImageUrl() == null) {
			return;
		}

		// Check for alternative text on the image
		String alternativeText = imageComponent.getAlternativeText();
		if (alternativeText == null) {
			LOG.warn("Image should have a description.");
			alternativeText = "";
		} else {
			alternativeText = I18nUtilities.format(null, alternativeText);
		}

		xml.appendTagOpen("ui:image");
		xml.appendAttribute("id", imageComponent.getId());
		xml.appendOptionalAttribute("class", imageComponent.getHtmlClass());
		xml.appendOptionalAttribute("track", imageComponent.isTracking(), "true");
		xml.appendAttribute("src", imageComponent.getTargetUrl());
		xml.appendAttribute("alt", alternativeText);
		xml.appendOptionalAttribute("hidden", imageComponent.isHidden(), "true");

		// Check for size information on the image
		Dimension size = imageComponent.getSize();
		if (size != null) {
			if (size.getHeight() >= 0) {
				xml.appendAttribute("height", size.height);
			}

			if (size.getWidth() >= 0) {
				xml.appendAttribute("width", size.width);
			}
		}
	}

	/**
	 * Paints the given {@link WImage}.
	 *
	 * @param component the WImage to paint.
	 * @param renderContext the RenderContext to paint to.
	 */
	@Override
	public void doRender(final WComponent component, final WebXmlRenderContext renderContext) {
		WImage imageComponent = (WImage) component;
		XmlStringBuilder xml = renderContext.getWriter();
		renderTagOpen(imageComponent, xml);
		xml.appendEnd();
	}
}
