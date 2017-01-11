package com.github.bordertech.wcomponents.container;

import com.github.bordertech.wcomponents.RenderContext;
import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.Response;
import com.github.bordertech.wcomponents.UIContext;
import com.github.bordertech.wcomponents.UIContextHolder;
import com.github.bordertech.wcomponents.WebUtilities;
import com.github.bordertech.wcomponents.servlet.ServletRequest;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;
import com.github.bordertech.wcomponents.util.ConfigurationProperties;
import com.github.bordertech.wcomponents.util.SystemException;
import com.github.bordertech.wcomponents.util.ThemeUtil;
import com.github.bordertech.wcomponents.util.Util;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URL;
import javax.servlet.http.HttpServletRequest;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.apache.commons.lang3.text.translate.AggregateTranslator;
import org.apache.commons.lang3.text.translate.CharSequenceTranslator;
import org.apache.commons.lang3.text.translate.CodePointTranslator;
import org.apache.commons.lang3.text.translate.LookupTranslator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This interceptor is used to perform server-side XSLT so that HTML is
 * delivered to the client instead of XML. This works by buffering the response
 * in memory and then transforming it before sending the response to the client.
 * This will use more memory and CPU on the server. If this becomes a problem it
 * may be better to perform the transform on an appliance (or the client).
 *
 * @author Rick Brown
 * @since 1.0.0
 */
public class TransformXMLInterceptor extends InterceptorComponent {

	/**
	 * Support for no-xslt agents.
	 */
	private static final String NO_XSLT_FLAG = "wcnoxslt";

	/**
	 * The logger instance for this class.
	 */
	private static final Log LOG = LogFactory.getLog(TransformXMLInterceptor.class);

	/**
	 * The theme XSLT resource name.
	 */
	private static final String RESOURCE_NAME = ThemeUtil.getThemeBase() + "xslt/" + ThemeUtil.getThemeXsltName();

	/**
	 * The XSLT cached templates.
	 */
	private static final Templates TEMPLATES = initTemplates();

	/**
	 * If true then server side XSLT will be ignored regardless of the
	 * configuration property. This is to account for user agents that
	 * cannot handle HTML, yes such a thing exists.
	 */
	private boolean doTransform = true;

	/**
	 * Override preparePaint in order to perform processing specific to this
	 * interceptor.
	 *
	 * @param request the request being responded to.
	 */
	@Override
	public void preparePaint(final Request request) {
		if (doTransform && request instanceof ServletRequest) {
			HttpServletRequest httpServletRequest = ((ServletRequest) request).getBackingRequest();
			String userAgentString = httpServletRequest.getHeader("User-Agent");
			/* It is possible to opt out on a case by case basis by setting a flag on the ua string.
			 * This helps custom user agents that do not support HTML as well as facilitating debugging.
			 */
			if (userAgentString != null && userAgentString.contains(NO_XSLT_FLAG)) {
				doTransform = false;
			}
		}
		super.preparePaint(request);
	}

	/**
	 * Override paint to perform XML to HTML transformation.
	 *
	 * @param renderContext the renderContext to send the output to.
	 */
	@Override
	public void paint(final RenderContext renderContext) {

		if (!doTransform) {
			super.paint(renderContext);
			return;
		}

		if (!(renderContext instanceof WebXmlRenderContext)) {
			LOG.warn("Unable to transform a " + renderContext);
			super.paint(renderContext);
			return;
		}
		LOG.debug("Transform XML Interceptor: Start");

		UIContext uic = UIContextHolder.getCurrent();

		// Set up a render context to buffer the XML payload.
		StringWriter xmlBuffer = new StringWriter();
		PrintWriter xmlWriter = new PrintWriter(xmlBuffer);

		WebXmlRenderContext xmlContext = new WebXmlRenderContext(xmlWriter, uic.getLocale());

		super.paint(xmlContext);  // write the XML to the buffer

		// Get a handle to the true PrintWriter.
		WebXmlRenderContext webRenderContext = (WebXmlRenderContext) renderContext;
		PrintWriter writer = webRenderContext.getWriter();

		/*
		 * Switch the response content-type to HTML.
		 * In theory the transformation could be to ANYTHING (not just HTML) so perhaps it would make more sense to
		 *    write a new interceptor "ContentTypeInterceptor" which attempts to sniff payloads and choose the correct
		 *    content-type. This is exactly the kind of thing IE6 loved to do, so perhaps it's a bad idea.
		 */
		Response response = getResponse();
		response.setContentType(WebUtilities.CONTENT_TYPE_HTML);

		String xml = xmlBuffer.toString();
		if (isAllowCorruptCharacters() && !Util.empty(xml)) {

			// Remove illegal HTML characters from the content before transforming it.
			xml = removeCorruptCharacters(xml);
		}

		// Perform the transformation and write the result.
		transform(xml, uic, writer);

		LOG.debug("Transform XML Interceptor: Finished");
	}

	/**
	 * Transform the UI XML to HTML using the correct XSLT from the
	 * classpath.
	 *
	 * @param xml The XML to transform.
	 * @param uic The UIContext used to determine variables such as locale.
	 * @param writer The result of the transformation will be written to
	 * this writer.
	 */
	private void transform(final String xml, final UIContext uic, final PrintWriter writer) {

		Transformer transformer = newTransformer();
		Source inputXml;
		try {
			inputXml = new StreamSource(new ByteArrayInputStream(xml.getBytes("utf-8")));
			StreamResult result = new StreamResult(writer);
			transformer.transform(inputXml, result);
		} catch (UnsupportedEncodingException | TransformerException ex) {
			throw new SystemException("Could not transform xml", ex);
		}
	}

	/**
	 * Creates a new Transformer instance using cached XSLT Templates. There
	 * will be one cached template. Transformer instances are not
	 * thread-safe and cannot be reused (they can after the transformation
	 * is complete).
	 *
	 * @return A new Transformer instance.
	 */
	private static Transformer newTransformer() {

		if (TEMPLATES == null) {
			throw new IllegalStateException("TransformXMLInterceptor not initialized.");
		}

		try {
			return TEMPLATES.newTransformer();
		} catch (TransformerConfigurationException ex) {
			throw new SystemException("Could not create transformer for " + RESOURCE_NAME, ex);
		}
	}

	/**
	 * Statically initialize the XSLT templates that are cached for all
	 * future transforms.
	 *
	 * @return the XSLT Templates.
	 */
	private static Templates initTemplates() {
		try {
			URL xsltURL = ThemeUtil.class.getResource(RESOURCE_NAME);
			if (xsltURL != null) {
				Source xsltSource = new StreamSource(xsltURL.openStream(), xsltURL.toExternalForm());
				TransformerFactory factory = new net.sf.saxon.TransformerFactoryImpl();
				Templates templates = factory.newTemplates(xsltSource);
				LOG.debug("Generated XSLT templates for: " + RESOURCE_NAME);
				return templates;
			} else {
				// Server-side XSLT enabled but theme resource not on classpath.
				throw new IllegalStateException(RESOURCE_NAME + " not on classpath");
			}
		} catch (IOException | TransformerConfigurationException ex) {
			throw new SystemException("Could not create transformer for " + RESOURCE_NAME, ex);
		}
	}

	/**
	 * @return true if allow corrupt characters in XSLT processing.
	 */
	private static boolean isAllowCorruptCharacters() {
		return ConfigurationProperties.getXsltAllowCorruptCharacters();
	}

	/**
	 * Remove bad characters in XML.
	 *
	 * @param input The String to escape.
	 * @return the clean string
	 */
	private static String removeCorruptCharacters(final String input) {
		if (Util.empty(input)) {
			return input;
		}
		return ESCAPE_BAD_XML10.translate(input);
	}

	/**
	 * Translator object for escaping XML 1.0.
	 *
	 * While {@link #escapeXml10(String)} is the expected method of use,
	 * this object allows the XML escaping functionality to be used as the
	 * foundation for a custom translator.
	 */
	private static final CharSequenceTranslator ESCAPE_BAD_XML10
		= new AggregateTranslator(
			new LookupTranslator(
				new String[][]{
					{"\u000b", ""},
					{"\u000c", ""},
					{"\ufffe", ""},
					{"\uffff", ""}
				}),
			NumericEntityIgnorer.between(0x00, 0x08),
			NumericEntityIgnorer.between(0x0e, 0x1f),
			NumericEntityIgnorer.between(0x7f, 0x9f)
		);

	/**
	 * <p>
	 * Implementation of the CodePointTranslator to throw away the matching
	 * characters. This is copied from
	 * org.apache.commons.lang3.text.translate.NumericEntityEscaper, but has
	 * been changed to discard the characters rather than attempting to
	 * encode them.<p>
	 * <p>
	 * Discarding the characters is necessary because certain invalid
	 * characters (e.g. decimal 129) cannot be encoded for HTML. An existing
	 * library was not available for this function because no HTML page
	 * should ever contain these characters.</p>
	 */
	private static final class NumericEntityIgnorer extends CodePointTranslator {

		private final int below;
		private final int above;
		private final boolean between;

		/**
		 * <p>
		 * Constructs a <code>NumericEntityEscaper</code> for the
		 * specified range. This is the underlying method for the other
		 * constructors/builders. The <code>below</code> and
		 * <code>above</code> boundaries are inclusive when
		 * <code>between</code> is <code>true</code> and exclusive when
		 * it is <code>false</code>. </p>
		 *
		 * @param below int value representing the lowest codepoint
		 * boundary
		 * @param above int value representing the highest codepoint
		 * boundary
		 * @param between whether to escape between the boundaries or
		 * outside them
		 */
		private NumericEntityIgnorer(final int below, final int above, final boolean between) {
			this.below = below;
			this.above = above;
			this.between = between;
		}

		/**
		 * <p>
		 * Constructs a <code>NumericEntityEscaper</code> between the
		 * specified values (inclusive). </p>
		 *
		 * @param codepointLow above which to escape
		 * @param codepointHigh below which to escape
		 * @return the newly created {@code NumericEntityEscaper}
		 * instance
		 */
		public static NumericEntityIgnorer between(final int codepointLow, final int codepointHigh) {
			return new NumericEntityIgnorer(codepointLow, codepointHigh, true);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean translate(final int codepoint, final Writer out) throws IOException {
			if (between) {
				if (codepoint < below || codepoint > above) {
					return false;
				}
			} else if (codepoint >= below && codepoint <= above) {
				return false;
			}
// Commented out from org.apache.commons.lang3.text.translate.NumericEntityEscaper
// these characters cannot be handled in any way - write no output.

//			out.write("&#");
//			out.write(Integer.toString(codepoint, 10));
//			out.write(';');
			if (LOG.isWarnEnabled()) {
				LOG.warn("Illegal HTML character stripped from XML. codepoint=" + codepoint);
			}

			return true;
		}
	}

}
