package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.Renderer;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;
import com.github.bordertech.wcomponents.validation.WFieldErrorIndicator;

/**
 * {@link Renderer} for the {@link WFieldErrorIndicator} component.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 * @deprecated see {@link WFieldErrorIndicator}
 */
@Deprecated
final class WFieldErrorIndicatorRenderer extends AbstractWFieldIndicatorRenderer {

	@Override
	public void doRender(final WComponent component, final WebXmlRenderContext renderContext) {
		// NO OP - used to enure nothing is output.
	}

}
