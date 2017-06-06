package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.Renderer;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WTabGroup;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;

/**
 * {@link Renderer} for the {@link WTabGroup} component.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 * @deprecated see {@link WTabGroup}
 */
final class WTabGroupRenderer extends AbstractWebXmlRenderer {

	/**
	 * Paints the given WTabGroup.
	 *
	 * @param component the WTabGroup to paint.
	 * @param renderContext the RenderContext to paint to.
	 */
	@Override
	public void doRender(final WComponent component, final WebXmlRenderContext renderContext) {
		WTabGroup group = (WTabGroup) component;
		paintChildren(group, renderContext);
	}
}
