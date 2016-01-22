/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.bordertech.wcomponents.template;

import com.github.bordertech.wcomponents.UIContext;
import com.github.bordertech.wcomponents.UIContextHolder;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.util.SystemException;
import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.context.MapValueResolver;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import com.github.jknack.handlebars.io.TemplateLoader;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author jonathan
 */
public class HandlebarsRendererImpl implements TemplateRenderer {

	/**
	 * The logger instance for this class.
	 */
	private static final Log LOG = LogFactory.getLog(HandlebarsRendererImpl.class);

	@Override
	public void render(final String templateName, final Map<String, Object> context, final Map<String, WComponent> componentsByKey, final PrintWriter writer, final boolean debugLayout) {

		if (debugLayout) {
			writer.println("<!-- Start " + templateName + " -->");
			renderTemplate(templateName, context, componentsByKey, writer);
			writer.println("<!-- End   " + templateName + " -->");
		} else {
			renderTemplate(templateName, context, componentsByKey, writer);
		}
	}

	/**
	 * Paints the component in XML using the Velocity Template.
	 *
	 * @param component the component to paint.
	 * @param writer the writer to send the HTML output to.
	 */
	protected void renderTemplate(String templateName, Map<String, Object> componentContext, Map<String, WComponent> componentsByKey, Writer writer) {
		LOG.debug("Rendering template " + templateName);

		try {

			// Setup handlebars
			TemplateLoader loader = new ClassPathTemplateLoader();
			Handlebars handlebars = new Handlebars(loader);

			// Load template
			Template template = handlebars.compile(templateName);

			// Setup context
			Context handlebarsContext = Context.newBuilder(componentContext).resolver(MapValueResolver.INSTANCE).build();

			// Apply template
			UIContext uic = UIContextHolder.getCurrent();
			try (TemplateWriter templateWriter = new TemplateWriter(writer, componentsByKey, uic)) {
				template.apply(handlebarsContext, templateWriter);
			}

		} catch (Exception e) {
			throw new SystemException("Problems with handlebars template [" + templateName + "]. " + e.getMessage(), e);
		}
	}

}
