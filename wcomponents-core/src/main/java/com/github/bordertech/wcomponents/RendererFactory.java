package com.github.bordertech.wcomponents;

/**
 * Describes a renderer factory for a given package.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public interface RendererFactory {

	/**
	 * Obtains a renderer for the given class.
	 *
	 * @param clazz the WComponent class to retrieve the renderer for.
	 * @return the renderer for the given class, or null if there isn't a renderer in this package.
	 */
	Renderer getRenderer(Class<?> clazz);

	/**
	 * Obtains a renderer for the given template.
	 *
	 * @return a template renderer for the given template, or null if template rendering is not supported.
	 * @deprecated Use {@link WTemplate} instead.
	 */
	@Deprecated
	Renderer getTemplateRenderer();
}
