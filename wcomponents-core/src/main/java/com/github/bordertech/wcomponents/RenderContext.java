package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.layout.UIManager;

/**
 * <p>
 * The RenderContext is used to render a component. It is passed into the {@link WComponent#paint(RenderContext)} method
 * as an argument and also the specific {@link Renderer} instance used to render the component. Components themselves
 * must not rely on knowledge of the context.</p>
 *
 * <p>
 * The appropriate {@link Renderer} for each component is obtained from the {@link UIManager}, which calls the
 * {@link #getRenderPackage()} method to obtain the package name which contains all of the renderers for a given type of
 * context.</p>
 *
 * <p>
 * RenderContext subclasses should contain additional data &amp; methods which are necessary to render the component in
 * the context. For example, a Web/HTTP renderer may include a reference to the ServletResponse.</p>
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public interface RenderContext {

	/**
	 * Return the java package name which contains the renderers for this renderer. This package must contain a public
	 * class named "RendererFactory", which implements the {@link RendererFactory} interface.
	 *
	 * @return the renderer package name.
	 */
	String getRenderPackage();
}
