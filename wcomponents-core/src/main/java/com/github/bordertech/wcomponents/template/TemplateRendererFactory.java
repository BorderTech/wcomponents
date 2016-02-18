package com.github.bordertech.wcomponents.template;

import com.github.bordertech.wcomponents.WTemplate;
import com.github.bordertech.wcomponents.util.Config;
import com.github.bordertech.wcomponents.util.SystemException;
import com.github.bordertech.wcomponents.util.Util;
import java.util.HashMap;
import java.util.Map;

/**
 * Return the {@link TemplateRenderer} implementation for a given template engine.
 *
 * <p>
 * The {@link TemplateRenderer} implementation for an engine is determined by the parameter
 * "bordertech.wcomponents.template.renderer" suffixed with the engine name. For example:-
 * <code>bordertech.wcomponents.template.renderer.myengine = my.package.MyEngineRendererImpl</code>
 * </p>
 * <p>
 * The default template engine is set via the parameter "bordertech.wcomponents.template.renderer". For example:-
 * <code>bordertech.wcomponents.template.renderer=myengine</code>
 * </p>
 *
 * @see WTemplate
 * @see TemplateRenderer
 *
 * @author Jonathan Austin
 * @since 1.0.3
 */
public final class TemplateRendererFactory {

	/**
	 * A cache of template renderers keyed by the class name.
	 */
	private static final Map<String, TemplateRenderer> CACHE = new HashMap<>();

	/**
	 * Private constructor.
	 */
	private TemplateRendererFactory() {
		// Do not allow instantiation
	}

	/**
	 * Available template engines.
	 */
	public enum TemplateEngine {
		/**
		 * Velocity engine.
		 */
		VELOCITY,
		/**
		 * Handlebars engine.
		 */
		HANDLEBARS,
		/**
		 * Plaintext engine.
		 */
		PLAINTEXT;

		/**
		 * @return the template engine name
		 */
		public String getEngineName() {
			return name().toLowerCase();
		}
	}

	/**
	 * Parameter prefix for template engines.
	 */
	public static final String ENGINE_PARAM_PREFIX = "bordertech.wcomponents.template.renderer";

	/**
	 * Default template engine name.
	 */
	public static final String DEFAULT_ENGINE_NAME = Config.getInstance().getString(ENGINE_PARAM_PREFIX);

	/**
	 *
	 * @return the default template renderer.
	 */
	public static TemplateRenderer newInstance() {
		return newInstance(DEFAULT_ENGINE_NAME);
	}

	/**
	 *
	 * @param engine the template engine
	 * @return the template renderer for this template engine
	 */
	public static TemplateRenderer newInstance(final TemplateEngine engine) {
		return newInstance(engine.getEngineName());
	}

	/**
	 *
	 * @param engineName the template engine name
	 * @return the template renderer for this template engine
	 */
	public static TemplateRenderer newInstance(final String engineName) {

		String paramKey = ENGINE_PARAM_PREFIX + "." + engineName;
		String clazzName = Config.getInstance().getString(paramKey);

		if (Util.empty(clazzName)) {
			throw new SystemException("No implementation set for template engine [" + engineName + "]. Set the parameter [" + paramKey + "].");
		}

		// Check the cache
		TemplateRenderer cached = CACHE.get(clazzName);
		if (cached != null) {
			return cached;
		}

		try {
			Class<TemplateRenderer> clazz = (Class<TemplateRenderer>) Class.forName(clazzName);
			TemplateRenderer renderer = clazz.newInstance();
			CACHE.put(clazzName, renderer);
			return renderer;
		} catch (Exception e) {
			throw new IllegalArgumentException("Could not instantiate template renderer [" + clazzName + "]. " + e.getMessage(), e);
		}

	}

}
