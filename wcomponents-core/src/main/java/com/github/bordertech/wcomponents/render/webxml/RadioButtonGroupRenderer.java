package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;

/**
 * The Renderer for RadioButtonGroup.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
final class RadioButtonGroupRenderer extends AbstractWebXmlRenderer {

	/**
	 * Paints the given RadioButtonGroup.
	 *
	 * @param component the RadioButtonGroup to paint.
	 * @param renderContext the RenderContext to paint to.
	 */
	@Override
	public void doRender(final WComponent component, final WebXmlRenderContext renderContext) {
		// We don't actually want to render anything in the HTML
	}
}
