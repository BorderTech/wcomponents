package com.github.bordertech.wcomponents.template;

import com.github.bordertech.wcomponents.UIContext;
import com.github.bordertech.wcomponents.UIContextHolder;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.util.ConfigurationProperties;
import com.github.bordertech.wcomponents.util.SystemException;
import java.io.Writer;
import java.util.Map;
import java.util.Properties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

/**
 * Velocity template renderer.
 * <p>
 * Has no engine options.
 * </p>
 *
 * @author Jonathan Austin
 * @since 1.0.3
 */
public class VelocityRendererImpl implements TemplateRenderer {

	/**
	 * The logger instance for this class.
	 */
	private static final Log LOG = LogFactory.getLog(VelocityRendererImpl.class);

	/**
	 * The singleton VelocityEngine instance associated with this factory.
	 */
	private static VelocityEngine engine;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void renderTemplate(final String templateName, final Map<String, Object> context, final Map<String, WComponent> taggedComponents,
			final Writer writer, final Map<String, Object> options) {

		LOG.debug("Rendering velocity template [" + templateName + "].");

		// Velocity uses a ClassLoader so dont use an absolute path.
		String name = templateName.startsWith("/") ? templateName.substring(1) : templateName;

		try {

			// Load template
			Template template = getVelocityEngine().getTemplate(name);

			// Map the tagged components to be used in the replace writer
			Map<String, WComponent> componentsByKey = TemplateUtil.mapTaggedComponents(context, taggedComponents);

			// Setup context
			VelocityContext velocityContext = new VelocityContext();
			for (Map.Entry<String, Object> entry : context.entrySet()) {
				velocityContext.put(entry.getKey(), entry.getValue());
			}

			// Write template
			UIContext uic = UIContextHolder.getCurrent();
			try (TemplateWriter velocityWriter = new TemplateWriter(writer, componentsByKey, uic)) {
				template.merge(velocityContext, velocityWriter);
			}
		} catch (ResourceNotFoundException e) {
			throw new SystemException("Could not find velocity template [" + templateName + "]. " + e.getMessage(), e);
		} catch (Exception e) {
			throw new SystemException("Problems with velocity template [" + templateName + "]. " + e.getMessage(), e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void renderInline(final String templateInline, final Map<String, Object> context, final Map<String, WComponent> taggedComponents,
			final Writer writer, final Map<String, Object> options) {

		LOG.debug("Rendering inline velocity template.");

		try {

			// Map the tagged components to be used in the replace writer
			Map<String, WComponent> componentsByKey = TemplateUtil.mapTaggedComponents(context, taggedComponents);

			// Setup context
			VelocityContext velocityContext = new VelocityContext();
			for (Map.Entry<String, Object> entry : context.entrySet()) {
				velocityContext.put(entry.getKey(), entry.getValue());
			}

			// Write inline template
			UIContext uic = UIContextHolder.getCurrent();
			try (TemplateWriter velocityWriter = new TemplateWriter(writer, componentsByKey, uic)) {
				getVelocityEngine().evaluate(velocityContext, velocityWriter, templateInline, templateInline);
			}

		} catch (Exception e) {
			throw new SystemException("Problems with inline velocity template." + e.getMessage(), e);
		}
	}

	/**
	 * <p>
	 * Returns the VelocityEngine associated with this factory. If this is the first time we are using the engine,
	 * create it and initialise it.</p>
	 *
	 * <p>
	 * Note that velocity engines are hugely resource intensive, so we don't want too many of them. For the time being
	 * we have a single instance stored as a static variable. This would only be a problem if the VelocityLayout class
	 * ever wanted to use different engine configurations (unlikely).</p>
	 *
	 * @return the VelocityEngine associated with this factory.
	 */
	public static synchronized VelocityEngine getVelocityEngine() {
		if (engine == null) {
			Properties props = new Properties();

			// Class Loader
			props.setProperty("resource.loader", "class");
			props.setProperty("class.resource.loader.class", ClasspathResourceLoader.class.getName());

			// Caching
			if (isCaching()) {
				props.setProperty("class.resource.loader.cache", "true");
				props.setProperty(RuntimeConstants.RESOURCE_MANAGER_CACHE_CLASS, VelocityCacheImpl.class.getName());
			} else {
				props.setProperty("class.resource.loader.cache", "false");
			}

			// Backwards compatability properties (if enabled)
			if (ConfigurationProperties.isVelocityBackwardCompatability17Enabled()) {
				props.putAll(ConfigurationProperties.getVelocityBackwardCompatability17Properties());
			}
			// Project custom properties (if set)
			props.putAll(ConfigurationProperties.getVelocityCustomProperties());

			try {
				engine = new VelocityEngine(props);
			} catch (Exception ex) {
				throw new SystemException("Failed to configure VelocityEngine", ex);
			}
		}

		return engine;
	}

	/**
	 * @return true if use caching
	 */
	public static boolean isCaching() {
		return ConfigurationProperties.getVelocityCache();
	}

}
