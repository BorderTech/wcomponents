package com.github.bordertech.wcomponents.render.nil;

import com.github.bordertech.wcomponents.RenderContext;
import com.github.bordertech.wcomponents.Renderer;
import com.github.bordertech.wcomponents.RendererFactory;
import com.github.bordertech.wcomponents.WComponent;

/**
 * The layout factory for the nil renderer package. This factory produces a renderer which doesn't do anything, and is
 * useful for testing.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public final class RendererFactoryImpl implements RendererFactory {

	/**
	 * The singleton NullRenderer instance.
	 */
	private static final Renderer NULL_RENDERER = new NullRenderer();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Renderer getRenderer(final Class<?> clazz) {
		return NULL_RENDERER;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @deprecated Use {@link WTemplate} instead
	 */
	@Deprecated
	@Override
	public Renderer getTemplateRenderer() {
		return NULL_RENDERER;
	}

	/**
	 * A no-op renderer.
	 */
	private static final class NullRenderer implements Renderer {

		/**
		 * Doesn't do anything.
		 *
		 * @param component ignored.
		 * @param renderContext ignored.
		 */
		@Override
		public void render(final WComponent component, final RenderContext renderContext) {
			// NO-OP
		}
	};
}
