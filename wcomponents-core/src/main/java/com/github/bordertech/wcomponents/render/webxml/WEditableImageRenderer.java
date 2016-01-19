package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.Renderer;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WEditableImage;
import com.github.bordertech.wcomponents.XmlStringBuilder;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;

/**
 * {@link Renderer} for the {@link WEditableImage} component.
 *
 * @author Rick Brown
 * @since 1.0.3
 */
final class WEditableImageRenderer extends AbstractWebXmlRenderer {

	/**
	 * Paints the given {@link WEditableImage}.
	 *
	 * @param component the WEditableImage to paint.
	 * @param renderContext the RenderContext to paint to.
	 */
	@Override
	public void doRender(final WComponent component, final WebXmlRenderContext renderContext) {
		WEditableImage editableImage = (WEditableImage) component;
		XmlStringBuilder xml = renderContext.getWriter();
		WImageRenderer.renderTagOpen(editableImage, xml);
		WComponent uploader = editableImage.getEditUploader();
		if (uploader != null) {
			xml.appendAttribute("editor", uploader.getId());
		}
		xml.appendEnd();
	}
}
