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
import com.github.bordertech.wcomponents.velocity.VelocityEngineFactory;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;

/**
 *
 * @author jonathan
 */
public class VelocityRendererImpl implements TemplateRenderer {

	/**
	 * The logger instance for this class.
	 */
	private static final Log LOG = LogFactory.getLog(VelocityRendererImpl.class);

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

			// Load template
			Template template = VelocityEngineFactory.getVelocityEngine().getTemplate(templateName);

			// Setup context
			VelocityContext velocityContext = new VelocityContext();
			for (Map.Entry<String, Object> entry : componentContext.entrySet()) {
				velocityContext.put(entry.getKey(), entry.getValue());
			}

			// Apply template
			UIContext uic = UIContextHolder.getCurrent();
			try (TemplateWriter velocityWriter = new TemplateWriter(writer, componentsByKey, uic)) {
				template.merge(velocityContext, velocityWriter);
			}

		} catch (Exception e) {
			throw new SystemException("Problems with velocity [" + templateName + "]. " + e.getMessage(), e);
		}
	}

}
