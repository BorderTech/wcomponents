package com.github.bordertech.wcomponents.template;

import com.github.bordertech.wcomponents.UIContext;
import com.github.bordertech.wcomponents.UIContextHolder;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.util.Config;
import com.github.bordertech.wcomponents.util.SystemException;
import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.EscapingStrategy;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.MarkdownHelper;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.cache.TemplateCache;
import com.github.jknack.handlebars.context.JavaBeanValueResolver;
import com.github.jknack.handlebars.context.MapValueResolver;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import com.github.jknack.handlebars.io.TemplateLoader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Handlebars template renderer.
 * <p>
 * Has the following engine options:-
 * </p>
 * <ul>
 * <li>{@link #PRETTY_PRINT} - Include with value "true" to activate</li>
 * <li>{@link #MARKDOWN} - Include with value "true" to activate</li>
 * <li>{@link #ESCAPING_STRATEGY} - Include with {@link EscapingStrategy} as the value</li>
 * </ul>
 *
 * @author Jonathan Austin
 * @since 1.0.3
 */
public class HandlebarsRendererImpl implements TemplateRenderer {

	/**
	 * Pretty print option.
	 */
	public static final String PRETTY_PRINT = "PRETTY_PRINT";

	/**
	 * Markdown option.
	 */
	public static final String MARKDOWN = "MARKDOWN";

	/**
	 * Escaping strategy option.
	 */
	public static final String ESCAPING_STRATEGY = "ESCAPING_STRATEGY";

	/**
	 * The logger instance for this class.
	 */
	private static final Log LOG = LogFactory.getLog(HandlebarsRendererImpl.class);

	/**
	 * The handlebars cache instance.
	 */
	private static final TemplateCache CACHE = new HandlebarsCacheImpl();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void renderTemplate(final String templateName, final Map<String, Object> context, final Map<String, WComponent> taggedComponents, final Writer writer, final Map<String, Object> options) {

		LOG.debug("Rendering handlebars template " + templateName);

		try {

			// Map the tagged components to be used in the replace writer
			Map<String, WComponent> componentsByKey = TemplateUtil.mapTaggedComponents(context, taggedComponents);

			// Get Engine
			Handlebars handlebars = getHandlebarsEngine(options);

			// Load template (Handlebars loader makes the template name "absolute")
			Template template = handlebars.compile(templateName);

			// Setup handlebars context
			Context handlebarsContext = createContext(context);

			// Render
			writeTemplate(template, handlebarsContext, componentsByKey, writer);
		} catch (FileNotFoundException e) {
			throw new SystemException("Could not find handlebars template [" + templateName + "]. " + e.getMessage(), e);
		} catch (Exception e) {
			throw new SystemException("Problems with handlebars template [" + templateName + "]. " + e.getMessage(), e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void renderInline(final String templateInline, final Map<String, Object> context, final Map<String, WComponent> taggedComponents, final Writer writer, final Map<String, Object> options) {

		LOG.debug("Rendering handlebars inline template.");

		try {

			// Map the tagged components to be used in the replace writer
			Map<String, WComponent> componentsByKey = TemplateUtil.mapTaggedComponents(context, taggedComponents);

			// Get Engine
			Handlebars handlebars = getHandlebarsEngine(options);

			// Compile inline
			Template template = handlebars.compileInline(templateInline);

			// Setup handlebars context
			Context handlebarsContext = createContext(context);

			// Write template
			writeTemplate(template, handlebarsContext, componentsByKey, writer);

		} catch (Exception e) {
			throw new SystemException("Problems with handlebars inline template. " + e.getMessage(), e);
		}
	}

	/**
	 *
	 * @param options the engine options
	 * @return the handlebars engine
	 */
	protected Handlebars getHandlebarsEngine(final Map<String, Object> options) {
		// Setup handlebars
		TemplateLoader loader = new ClassPathTemplateLoader();
		// Clear the suffix so the file name does not default the file type to ".hbs"
		loader.setSuffix("");

		Handlebars handlebars = new Handlebars(loader);

		// Pretty Print
		Object value = options.get(PRETTY_PRINT);
		if (value != null) {
			handlebars.setPrettyPrint("true".equalsIgnoreCase(value.toString()));
		}

		// Escaping Strategy
		value = options.get(ESCAPING_STRATEGY);
		if (value instanceof EscapingStrategy) {
			handlebars.with((EscapingStrategy) value);
		}

		// Use markdown
		value = options.get(MARKDOWN);
		if (value != null && "true".equalsIgnoreCase(value.toString())) {
			handlebars.registerHelper("md", new MarkdownHelper());
		}

		// Caching
		if (isCaching()) {
			handlebars.with(CACHE);
		}

		return handlebars;
	}

	/**
	 *
	 * @param componentContext the component context
	 * @return the handlebars context
	 */
	protected Context createContext(final Map<String, Object> componentContext) {
		Context handlebarsContext = Context.newBuilder(componentContext)
				.resolver(MapValueResolver.INSTANCE, JavaBeanValueResolver.INSTANCE).build();
		return handlebarsContext;
	}

	/**
	 *
	 * @param template the handlebars template
	 * @param handlebarsContext the handlebars context
	 * @param componentsByKey the map of child components
	 * @param writer the writer for the output
	 * @throws IOException an IOException applying the template
	 */
	protected void writeTemplate(final Template template, final Context handlebarsContext, final Map<String, WComponent> componentsByKey, final Writer writer) throws IOException {
		// Apply template
		UIContext uic = UIContextHolder.getCurrent();
		try (TemplateWriter templateWriter = new TemplateWriter(writer, componentsByKey, uic)) {
			template.apply(handlebarsContext, templateWriter);
		}
	}

	/**
	 * @return true if use caching
	 */
	protected boolean isCaching() {
		return Config.getInstance().getBoolean("bordertech.wcomponents.handlebars.cache.enabled", Boolean.TRUE);
	}

}
