package com.github.bordertech.wcomponents.container;

import com.github.bordertech.wcomponents.DebugValidateXML;
import com.github.bordertech.wcomponents.RenderContext;
import com.github.bordertech.wcomponents.UIContextHolder;
import com.github.bordertech.wcomponents.WebUtilities;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;
import java.io.PrintWriter;
import java.io.StringWriter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>
 * An Interceptor used to report any WComponent that has generated HTML/XML that is not well formed.
 * </p>
 * <p>
 * To enable this Interceptor, both "bordertech.wcomponents.debug.enabled" and
 * "bordertech.wcomponents.debug.validateXML.enabled" must be set to true.
 * </p>
 * <p>
 * The Interceptor calls {@link DebugValidateXML} to determine if XML Validation is enabled. If it has been enabled,
 * then the interceptor uses a temporary buffer to hold the original response so that if {@link DebugValidateXML} has
 * reported errors, it can wrap the original response in a CDATA section and include any error messages in a new
 * response.
 * </p>
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class ValidateXMLInterceptor extends InterceptorComponent {

	/**
	 * The logger instance for this class.
	 */
	private static final Log LOG = LogFactory.getLog(ValidateXMLInterceptor.class);

	/**
	 * Override paint to include XML Validation.
	 *
	 * @param renderContext the renderContext to send the output to.
	 */
	@Override
	public void paint(final RenderContext renderContext) {
		// Check interceptor is enabled
		if (!DebugValidateXML.isEnabled()) {
			super.paint(renderContext);
			return;
		}

		if (!(renderContext instanceof WebXmlRenderContext)) {
			LOG.warn("Unable to validate against a " + renderContext);
			super.paint(renderContext);
			return;
		}

		LOG.debug("Validate XML Interceptor: Start");

		WebXmlRenderContext webRenderContext = (WebXmlRenderContext) renderContext;
		PrintWriter writer = webRenderContext.getWriter();

		// Generate XML
		StringWriter tempBuffer = new StringWriter();
		PrintWriter tempWriter = new PrintWriter(tempBuffer);
		WebXmlRenderContext tempContext = new WebXmlRenderContext(tempWriter, UIContextHolder.
				getCurrent().getLocale());

		super.paint(tempContext);
		String xml = tempBuffer.toString();

		// If no errors, check against the schema
		String error = DebugValidateXML.validateXMLAgainstSchema(xml);

		if (error != null) {
			// XML is NOT valid, so Report Errors and Wrap the original XML
			LOG.debug("Validate XML Interceptor: XML Has Errors");
			writer.println("<div>");

			writer.println("<div>");
			writer.println("Invalid XML");
			writer.println("<ul><li>" + WebUtilities.encode(error) + "</li></ul>");
			writer.println("<br/>");
			writer.println("</div>");

			// If a schema error detected, Wrap XML so line numbers reported in validation message are correct
			String testXML = DebugValidateXML.wrapXMLInRootElement(xml);
			paintOriginalXML(testXML, writer);

			writer.println("</div>");
		} else {
			// XML is valid
			writer.write(xml);
		}

		LOG.debug("Validate XML Interceptor: Finished");
	}

	/**
	 * Paint the original XML wrapped in a CDATA Section.
	 *
	 * @param originalXML the original XML
	 * @param writer the output writer
	 */
	private void paintOriginalXML(final String originalXML, final PrintWriter writer) {
		// Replace any CDATA Sections embedded in XML
		String xml = originalXML.replaceAll("<!\\[CDATA\\[", "CDATASTART");
		xml = xml.replaceAll("\\]\\]>", "CDATAFINISH");

		// Paint Output
		writer.println("<div>");
		writer.println("<!-- VALIDATE XML ERROR - START XML -->");
		writer.println("<![CDATA[");
		writer.println(xml);
		writer.println("]]>");
		writer.println("<!-- VALIDATE XML ERROR - END XML -->");
		writer.println("</div>");
	}
}
