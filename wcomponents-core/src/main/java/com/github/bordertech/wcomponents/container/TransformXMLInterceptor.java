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
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This interceptor is used to perform server-side XSLT so that HTML is delivered to the client instead of XML. This
 * works by buffering the response in memory and then transforming it before sending the response to the client. This
 * will use more memory and CPU on the server. If this becomes a problem it may be better to perform the transform on an
 * appliance (or the client).
 *
 * <p>
 * It is enabled by setting the "bordertech.wcomponents.xslt.enabled" to true.
 * </p>
 * <p>
 * However, projects may not always have the theme resources in the classpath and want to load them from a static
 * resource (eg from a different web server or resources defined in their war file). To load resources from a static
 * resource, projects set a theme content path via the "bordertech.wcomponents.theme.content.path=theme/myTheme"
 * parameter.
 * </p>
 * <p>
 * So if a project sets the theme content path then server side transforms become meaningless and will not be performed.
 * This will allow the client agent to do the transform using the theme loaded from the theme content path.
 * </p>
 *
 * @author Rick Brown
 * @since 1.0.0
 */
public class TransformXMLInterceptor extends InterceptorComponent {

	private static final String NO_XSLT_FLAG = "wcnoxslt";

	/**
	 * Cache compiled XSLT stylesheets.
	 */
	private static final Map<String, Templates> CACHE = new HashMap();

	/**
	 * The logger instance for this class.
	 */
	private static final Log LOG = LogFactory.getLog(TransformXMLInterceptor.class);

	/**
	 * If true then server side XSLT will be ignored regardless of the configuration property.
	 * This is to account for user agents that cannot handle HTML, yes such a thing exists.
	 */
	private boolean doTransform = false;

	/**
	 * Override preparePaint in order to perform processing specific to this interceptor.
	 *
	 * @param request the request being responded to.
	 */
	@Override
	public void preparePaint(final Request request) {
		doTransform = isPerformTransform();
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

		// Perform the transformation and write the result.
		String xml = xmlBuffer.toString();
		transform(xml, uic, writer);

		LOG.debug("Transform XML Interceptor: Finished");
	}

	/**
	 * @return true if transform flag is set true and a theme content path has not been set.
	 */
	private boolean isPerformTransform() {
		boolean transform = ConfigurationProperties.getXsltServerSide();
		if (transform) {
			// Check a theme content path has not been set.
			String themePath = ConfigurationProperties.getThemeContentPath();
			return Util.empty(themePath);
		}
		return false;
	}

	/**
	 * Transform the UI XML to HTML using the correct XSLT from the classpath.
	 *
	 * @param xml The XML to transform.
	 * @param uic The UIContext used to determine variables such as locale.
	 * @param writer The result of the transformation will be written to this writer.
	 */
	private void transform(final String xml, final UIContext uic, final PrintWriter writer) {
		String xsltName = ThemeUtil.getThemeXsltName(uic);
		String resourceName = ThemeUtil.getThemeBase() + "xslt/" + xsltName;
		Transformer transformer = newTransformer(resourceName);
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
	 * Creates a new Transformer instance using cached XSLT stylesheets. There will be one cached stylesheet per locale,
	 * so this is unlikely to ever use much memory but will certainly use less CPU not having to compile the complex
	 * XSLT each time.
	 *
	 * Transformer instances are not thread-safe and cannot be reused (they can after the transformation is complete).
	 *
	 * @param resourceName The name of the XSLT file to load from the classpath.
	 * @return A new Transformer instance.
	 */
	private static synchronized Transformer newTransformer(final String resourceName) {
		Templates templates = CACHE.get(resourceName);
		try {
			if (templates == null) {
				URL xsltURL = ThemeUtil.class.getResource(resourceName);
				if (xsltURL != null) {
					Source xsltSource = new StreamSource(xsltURL.openStream(), xsltURL.toExternalForm());
					TransformerFactory factory = TransformerFactory.newInstance();
					templates = factory.newTemplates(xsltSource);
					CACHE.put(resourceName, templates);
					LOG.debug("Cached xslt: " + resourceName);
				} else {
					// Perhaps we should disable this interceptor if we end up here and fall back to serving raw XML?
					throw new IllegalStateException(ConfigurationProperties.XSLT_SERVER_SIDE + " true but " + resourceName + " not on classpath");
				}
			}
			return templates.newTransformer();
		} catch (IOException | TransformerConfigurationException ex) {
			throw new SystemException("Could not create transformer for " + resourceName, ex);
		}
	}
}
