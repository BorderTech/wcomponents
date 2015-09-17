package com.github.bordertech.wcomponents;

import java.io.Serializable;

/**
 * <p>
 * Handles rendering of WComponents. This interface is technology independant, but implementations will be tied to a
 * particular technology, e.g. web-based HTML/XML. Each WComponent will only use one renderer per rendering technology.
 *
 * <p>
 * If a WComponent has no renderer, it just renders each of its child components in turn.</p>
 *
 * <p>
 * All renderers must be thread-safe, as only a single instance of the renderer is created by the UIManager. It is
 * recommended that renderers do not keep any state information, to avoid the performance penality of having to
 * synchronize methods.</p>
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public interface Renderer extends Serializable {

	/**
	 * Renders the component.
	 *
	 * @param component the component to paint
	 * @param renderContext the context for rendering.
	 */
	void render(WComponent component, RenderContext renderContext);
}
