package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.Container;
import com.github.bordertech.wcomponents.RenderContext;
import com.github.bordertech.wcomponents.Renderer;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;
import com.github.bordertech.wcomponents.util.SystemException;

/**
 * An abstract web xml renderer.
 *
 * All web xml renderers should extend this class, and must be thread-safe, as only a single instance of the renderer is
 * created by the UIManager. It is recommended that renderers do not keep any state information, to avoid the
 * performance penality of having to synchronize methods.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public abstract class AbstractWebXmlRenderer implements Renderer {

	/**
	 * Renders the component.
	 *
	 * @param component the component to paint
	 * @param renderContext the context for rendering.
	 */
	@Override
	public void render(final WComponent component, final RenderContext renderContext) {
		if (renderContext instanceof WebXmlRenderContext) {
			doRender(component, (WebXmlRenderContext) renderContext);
		} else {
			throw new SystemException("Unable to render web xml output to " + renderContext);
		}
	}

	/**
	 * Paints the children of the given component.
	 *
	 * @param container the component whose children will be painted.
	 * @param renderContext the context for rendering.
	 */
	protected final void paintChildren(final Container container,
			final WebXmlRenderContext renderContext) {
		final int size = container.getChildCount();

		for (int i = 0; i < size; i++) {
			WComponent child = container.getChildAt(i);
			child.paint(renderContext);
		}
	}

	/**
	 * Paints the component and its children using the manager's layout algorithm.
	 *
	 * @param component the component to paint
	 * @param renderContext the context for rendering.
	 */
	public abstract void doRender(WComponent component, WebXmlRenderContext renderContext);
}
