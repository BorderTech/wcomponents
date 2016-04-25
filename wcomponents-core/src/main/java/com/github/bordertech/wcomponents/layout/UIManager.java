package com.github.bordertech.wcomponents.layout;

import com.github.bordertech.wcomponents.AbstractWComponent;
import com.github.bordertech.wcomponents.RenderContext;
import com.github.bordertech.wcomponents.Renderer;
import com.github.bordertech.wcomponents.RendererFactory;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WTemplate;
import com.github.bordertech.wcomponents.render.webxml.VelocityRenderer;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;
import com.github.bordertech.wcomponents.util.Config;
import com.github.bordertech.wcomponents.util.Duplet;
import com.github.bordertech.wcomponents.util.NullWriter;
import com.github.bordertech.wcomponents.util.SystemException;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The UIManager provides a mechanism for client applications to use different Renderers, without having to specify a
 * renderer on a per-component basis.
 *
 * Renderers are assigned in the following order of preference:
 * <ol>
 * <li>
 * An application-specific renderer for the component. This is configured using application parameters, e.g. for a WText
 * component a Renderer would be specified as:
 * <code>bordertech.wcomponents.UIManager.renderer.com.github.bordertech.wcomponents.WText=com.github.myApp.render.MyTextRenderer</code>
 * </li>
 * <li>
 * A package implementation for the component, for the current RenderContext in use. This is controlled by the
 * {@link RenderContext#getRenderPackage()} method, which specifies the package name. The UIManager expects that each
 * renderer package includes a {@link RendererFactory} implementation which knows how to create a specific renderer for
 * a component. The RendererFactory class for each package must be named "RendererFactory".
 * </li>
 * <li>
 * As for the first two options, but for the class's parent class (then grand-parent etc., repeated until the
 * {@link WComponent} class is reached).
 * </li>
 * </ol>
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public final class UIManager implements PropertyChangeListener {

	/**
	 * {@link Config Configuration} parameters key prefix for retrieving Renderer overrides.
	 */
	private static final String PARAM_KEY_OVERRIDE_PREFIX = "bordertech.wcomponents.UIManager.renderer.";

	/**
	 * The logger instance for this class.
	 */
	private static final Log LOG = LogFactory.getLog(UIManager.class);

	/**
	 * Singleton instance.
	 */
	private static final UIManager INSTANCE = new UIManager();

	/**
	 * Marker Layout instance for when no layout was found.
	 */
	private static final Renderer NULL_RENDERER = new Renderer() {
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void render(final WComponent component, final RenderContext renderContext) {
			// NO-OP
		}
	};

	/**
	 * A cache of component Renderers keyed by WComponent classes. This cache must be flushed if the
	 * {@link Config configuration} is changed.
	 */
	private final Map<Duplet<String, Class<?>>, Renderer> renderers = new HashMap<>();

	/**
	 * A cache of template Renderers keyed by WComponent classes. This cache must be flushed if the
	 * {@link Config configuration} is changed.
	 */
	private final Map<String, Renderer> templateRenderers = new HashMap<>();

	/**
	 * A cache of LayoutManagers keyed by WComponent classes. This cache must be flushed if the
	 * {@link Config configuration} is changed.
	 */
	private final Map<String, RendererFactory> factoriesByPackage = new HashMap<>();

	/**
	 * Prevent instantiation of UIManager.
	 */
	private UIManager() {
		// Listen for configuration changes
		Config.addPropertyChangeListener(this);
	}

	/**
	 * We must invalidate the cached manager lookup when the {@link Config configuration} is reloaded, as the mappings
	 * may have changed.
	 *
	 * @param evt ignored.
	 */
	@Override
	public void propertyChange(final PropertyChangeEvent evt) {
		LOG.info("Parameters reloaded, flushing UIManager cache.");
		INSTANCE.clearCache();
	}

	/**
	 * Retrieves a renderer which can renderer the given component to the context.
	 *
	 * @param component the component to retrieve the renderer for.
	 * @param context the render context.
	 * @return an appropriate renderer for the component and context, or null if a suitable renderer could not be found.
	 */
	public static Renderer getRenderer(final WComponent component, final RenderContext context) {
		Class<? extends WComponent> clazz = component.getClass();

		Duplet<String, Class<?>> key = new Duplet<String, Class<?>>(context.getRenderPackage(),
				clazz);
		Renderer renderer = INSTANCE.renderers.get(key);

		if (renderer == null) {
			renderer = INSTANCE.findRenderer(component, key);
		} else if (renderer == NULL_RENDERER) {
			return null;
		}

		return renderer;
	}

	/**
	 * Retrieves a renderer which can renderer templates for the given context.
	 *
	 * @param context the render context.
	 * @return an appropriate renderer for the component and context, or null if a suitable renderer could not be found.
	 * @deprecated Use {@link WTemplate} instead.
	 */
	@Deprecated
	public static Renderer getTemplateRenderer(final RenderContext context) {
		String packageName = context.getRenderPackage();
		Renderer renderer = INSTANCE.templateRenderers.get(packageName);

		if (renderer == null) {
			renderer = INSTANCE.findTemplateRenderer(packageName);
		} else if (renderer == NULL_RENDERER) {
			return null;
		}

		return renderer;
	}

	/**
	 * Retrieves the template renderer for the given package.
	 *
	 * @param packageName the package to retrieve the template renderer for.
	 * @return the template renderer for the given package, or null if the package does not contain a template renderer.
	 * @deprecated Use {@link WTemplate} instead.
	 */
	@Deprecated
	private synchronized Renderer findTemplateRenderer(final String packageName) {
		RendererFactory factory = INSTANCE.findRendererFactory(packageName);
		Renderer renderer = factory.getTemplateRenderer();

		if (renderer == null) {
			templateRenderers.put(packageName, NULL_RENDERER);
		} else {
			templateRenderers.put(packageName, renderer);
		}

		return renderer;
	}

	/**
	 * Retrieves the default LayoutManager for the given component. This method must no longer be used, as it will only
	 * ever return a web-xml renderer.
	 *
	 * @param component the component.
	 * @return the LayoutManager for the given component.
	 *
	 * @deprecated use {@link #getRenderer(WComponent, RenderContext)}.
	 */
	@Deprecated
	public static Renderer getDefaultRenderer(final WComponent component) {
		LOG.warn("The getDefaultRenderer() method is deprecated. Do not obtain renderers directly.");
		return getRenderer(component, new WebXmlRenderContext(new PrintWriter(new NullWriter())));
	}

	/**
	 * Clears the lookup caches.
	 */
	private synchronized void clearCache() {
		factoriesByPackage.clear();
		renderers.clear();
	}

	/**
	 * Finds the layout for the given theme and component.
	 *
	 * @param component the WComponent class to find a manager for.
	 * @param key the component key to use for caching the renderer.
	 * @return the LayoutManager for the component.
	 */
	private synchronized Renderer findRenderer(final WComponent component,
			final Duplet<String, Class<?>> key) {
		LOG.info("Looking for layout for " + key.getSecond().getName() + " in " + key.getFirst());

		Renderer renderer = findConfiguredRenderer(component, key.getFirst());

		if (renderer == null) {
			renderers.put(key, NULL_RENDERER);
		} else {
			renderers.put(key, renderer);
		}

		return renderer;
	}

	/**
	 * Finds the renderer factory for the given package.
	 *
	 * @param packageName the package name to find the renderer factory for.
	 * @return the RendererFactory for the given package, or null if not found.
	 */
	private synchronized RendererFactory findRendererFactory(final String packageName) {
		RendererFactory factory = factoriesByPackage.get(packageName);

		if (factory == null) {
			try {
				factory = (RendererFactory) Class.forName(packageName + ".RendererFactoryImpl").
						newInstance();
				factoriesByPackage.put(packageName, factory);
			} catch (Exception e) {
				throw new SystemException(
						"Failed to create layout manager factory for " + packageName, e);
			}
		}

		return factory;
	}

	/**
	 * Attempts to find the configured renderer for the given output format and component.
	 *
	 * @param component the component to find a manager for.
	 * @param rendererPackage the package containing the renderers.
	 * @return the Renderer for the component, or null if there is no renderer defined.
	 */
	private Renderer findConfiguredRenderer(final WComponent component, final String rendererPackage) {
		Renderer renderer = null;

		// We loop for each WComponent in the class hierarchy, as the
		// Renderer may have been specified at a higher level.
		for (Class<?> c = component.getClass(); renderer == null && c != null && !AbstractWComponent.class.
				equals(c); c = c.getSuperclass()) {
			String qualifiedClassName = c.getName();

			// Is there an override for this class?
			String rendererName = Config.getInstance().getString(
					PARAM_KEY_OVERRIDE_PREFIX + qualifiedClassName, null);

			if (rendererName != null) {
				renderer = createRenderer(rendererName);

				if (renderer == null) {
					LOG.warn(
							"Layout Manager \"" + rendererName + "\" specified for " + qualifiedClassName + " was not found");
				} else {
					return renderer;
				}
			}

			renderer = findRendererFactory(rendererPackage).getRenderer(c);
		}

		return renderer;
	}

	/**
	 * Attempts to create a Renderer with the given name.
	 *
	 * @param rendererName the name of the Renderer
	 * @return a Renderer of the given type, or null if the class was not found.
	 */
	private static Renderer createRenderer(final String rendererName) {
		if (rendererName.endsWith(".vm")) {
			// This is a velocity template, so use a VelocityLayout
			return new VelocityRenderer(rendererName);
		}

		try {
			Class<?> managerClass = Class.forName(rendererName);
			Object manager = managerClass.newInstance();

			if (!(manager instanceof Renderer)) {
				throw new SystemException(rendererName + " is not a Renderer");
			}

			return (Renderer) manager;
		} catch (ClassNotFoundException e) {
			// Legal - there might not a manager implementation in a given theme
			return null;
		} catch (InstantiationException e) {
			throw new SystemException("Failed to instantiate " + rendererName, e);
		} catch (IllegalAccessException e) {
			throw new SystemException("Failed to access " + rendererName, e);
		}
	}
}
