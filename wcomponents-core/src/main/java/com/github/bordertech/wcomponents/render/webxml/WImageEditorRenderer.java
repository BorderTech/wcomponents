package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.Renderer;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WImageEditor;
import com.github.bordertech.wcomponents.XmlStringBuilder;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;
import java.awt.Dimension;

/**
 * {@link Renderer} for the {@link WImageEditor} component.
 *
 * @author Rick Brown
 * @since 1.0.3
 */
final class WImageEditorRenderer extends AbstractWebXmlRenderer {

	/**
	 * Paints the given {@link WImageEditor}.
	 *
	 * @param component the WImageEditor to paint.
	 * @param renderContext the RenderContext to paint to.
	 */
	@Override
	public void doRender(final WComponent component, final WebXmlRenderContext renderContext) {
		WImageEditor editorComponent = (WImageEditor) component;
		XmlStringBuilder xml = renderContext.getWriter();

		xml.appendTagOpen("wc-imageedit");
		xml.appendAttribute("id", editorComponent.getId());
		xml.appendOptionalAttribute("class", editorComponent.getHtmlClass());
		xml.appendOptionalAttribute("overlay", editorComponent.getOverlayUrl());
		xml.appendOptionalAttribute("camera", editorComponent.getUseCamera());

		// Check for size information on the image
		Dimension size = editorComponent.getSize();
		if (size != null) {
			if (size.getHeight() >= 0) {
				xml.appendAttribute("height", size.height);
			}

			if (size.getWidth() >= 0) {
				xml.appendAttribute("width", size.width);
			}
		}

		xml.appendEnd();
	}
}
