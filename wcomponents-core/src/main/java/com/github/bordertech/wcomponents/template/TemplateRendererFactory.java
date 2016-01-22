package com.github.bordertech.wcomponents.template;

import com.github.bordertech.wcomponents.util.Config;

/**
 *
 * @author jonathan
 */
public class TemplateRendererFactory {

	public static final String VELOCITY = "com.github.bordertech.wcomponents.template.VelocityRendererImpl";

	public static final String HANDLEBARS = "com.github.bordertech.wcomponents.template.HandlebarsRendererImpl";

	public static final String DEFAULT_FACTORY = Config.getInstance().getString("bordertech.wcomponents.template.factory", VELOCITY);

	public static TemplateRenderer newInstance() {
		return newInstance(DEFAULT_FACTORY);
	}

	public static TemplateRenderer newInstance(final String rendererClassName) {

		try {
			Class<TemplateRenderer> clazz = (Class<TemplateRenderer>) Class.forName(rendererClassName);
			TemplateRenderer renderer = clazz.newInstance();
			return renderer;
		} catch (Exception e) {
			throw new IllegalArgumentException("Could not instantiate class [" + rendererClassName + "]. " + e.getMessage(), e);
		}

	}

}
