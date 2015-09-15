package com.github.bordertech.wcomponents.render.nil;

import com.github.bordertech.wcomponents.RenderContext;

/**
 * The NullRenderContext produces no output and is useful for testing.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class NullRenderContext implements RenderContext {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getRenderPackage() {
		return "com.github.bordertech.wcomponents.render.nil";
	}
}
