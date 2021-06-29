package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.AbstractWComponentTestCase;
import com.github.bordertech.wcomponents.Renderer;
import com.github.bordertech.wcomponents.UIContext;
import com.github.bordertech.wcomponents.UIContextHolder;
import com.github.bordertech.wcomponents.UIContextImpl;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WebComponent;
import com.github.bordertech.wcomponents.layout.UIManager;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;
import com.github.bordertech.wcomponents.util.NullWriter;
import com.github.bordertech.wcomponents.util.XMLUtil;
import com.github.bordertech.wcomponents.util.mock.MockRequest;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsNot;
import org.junit.Assert;
import org.xml.sax.SAXException;
import org.xmlunit.matchers.HasXPathMatcher;
import org.xmlunit.assertj3.XmlAssert;
import org.xmlunit.builder.Input;
import org.xmlunit.validation.Languages;
import org.xmlunit.validation.ValidationResult;
import org.xmlunit.validation.Validator;
import org.xmlunit.xpath.JAXPXPathEngine;
import org.xmlunit.xpath.XPathEngine;

/**
 * <p>
 * This extension of junit TestCase includes assertions and other features useful for the testing WComponent XML
 * layouts.</p>
 *
 * <p>
 * This differs from WComponentTestCase in that developers do not need to specify the schema for each component; it is
 * derived from the theme in use.
 * </p>
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public abstract class AbstractWebXmlRendererTestCase extends AbstractWComponentTestCase {

	/**
	 * The logger instance for this class.
	 */
	private static final Log LOG = LogFactory.getLog(AbstractWebXmlRendererTestCase.class);

	/**
	 * Return the path to the schema to test against. This can be overridden if client applications wish to validate
	 * their own components.
	 *
	 * @return the schema path for the current theme in use.
	 */
	protected static String getSchemaPath() {
		return "/schema/ui/v1/schema.xsd";
	}

	/**
	 * Return the schema name space URI. This can be overridden if client applications wish to validate their own
	 * components.
	 *
	 * @return the schema URI for the current theme in use.
	 */
	protected static String getThemeURI() {
		return XMLUtil.THEME_URI;
	}

	/**
	 * Asserts that the given xhtml matches the schema.
	 *
	 * @param xhtml the xhtml to validate.
	 *
	 * @throws IOException if there is an I/O error
	 * @throws SAXException if there is a parsing error
	 */
	public void assertSchemaMatch(final String xhtml) throws IOException, SAXException {
		Assert.assertTrue("xhtml should validate", validateXhtml(xhtml).isValid());
	}

	/**
	 * Asserts that the given xhtml does not match the schema.
	 *
	 * @param xhtml the xhtml to validate.
	 *
	 * @throws IOException if there is an I/O error
	 * @throws SAXException if there is a parsing error
	 */
	public void assertSchemaMismatch(final String xhtml) throws IOException, SAXException {
		Assert.assertFalse("xhtml should not validate", validateXhtml(xhtml).isValid());
	}

	public ValidationResult validateXhtml(final String xhtml) {
		return getSchemaValidator().validateInstance(Input.fromString(wrapXHtml(xhtml)).build());
	}

	/**
	 * Obtains an XMLUnit schema validator.
	 *
	 * @return the validator to use.
	 */
	protected Validator getSchemaValidator() {
		// Some web components generate a fragment of xhtml markup that does not
		// have a single root element, so we add a "body" root element to the
		// markup fragment and the schema fragment.

		try {
			// Load the schema.
			final Source schemaSource = new StreamSource(AbstractWebXmlRendererTestCase.class.getResource(getSchemaPath()).toString());

			// Validate the xhtml.
			final Validator validator = Validator.forLanguage(Languages.W3C_XML_SCHEMA_NS_URI);
			validator.setSchemaSource(schemaSource);

			return validator;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Wraps an XML fragment rendered by a WComponent so that it can be validated against the schema.
	 *
	 * @param xml the XML fragment to wrap
	 * @return a wrapped copy of the given fragment, suitable for validation against the schema.
	 */
	protected String wrapXHtml(final String xml) {
		return XMLUtil.XML_DECLARATION
				+ "<ui:root " + XMLUtil.STANDARD_NAMESPACES + ">"
				+ xml
				+ "</ui:root>";
	}

	/**
	 * Renders the <code>component</code> to xhtml and asserts that the <code>xpathExpression</code> evaluates to the
	 * <code>expectedValue</code> for a URL.
	 *
	 * @param expectedUrlValue the expected value
	 * @param xpathExpression the xpath expression
	 * @param component the component to validate
	 * @throws IOException if there is an I/O error
	 * @throws SAXException if there is a parsing error
	 */
	public void assertXpathUrlEvaluatesTo(final String expectedUrlValue, final String xpathExpression,
			final WebComponent component) throws SAXException, IOException {
		String xhtml = toWrappedXHtml(component);
		assertXpathUrlEvaluatesTo(expectedUrlValue, xpathExpression, xhtml);
	}

	/**
	 * Asserts that the <code>xpathExpression</code> evaluates to the <code>expectedValue</code> for a URL.
	 *
	 * @param expectedUrlValue the expected URL value
	 * @param xpathExpression the xpath expression
	 * @param xml the xml to validate
	 * @throws IOException if there is an I/O error
	 * @throws SAXException if there is a parsing error
	 */
	public void assertXpathUrlEvaluatesTo(final String expectedUrlValue, final String xpathExpression,
			final String xml) throws SAXException, IOException {
		assertXpathEvaluatesTo(expectedUrlValue, xpathExpression, xml);
	}

	/**
	 * Renders the <code>component</code> to xhtml and asserts that the <code>xpathExpression</code> evaluates to the
	 * <code>expectedValue</code>.
	 *
	 * @param expectedValue the expected value
	 * @param xpathExpression the xpath expression
	 * @param component the component to validate
	 * @throws IOException if there is an I/O error
	 * @throws SAXException if there is a parsing error
	 */
	public void assertXpathEvaluatesTo(final String expectedValue, final String xpathExpression,
			final WebComponent component) throws SAXException, IOException {
		String xhtml = toWrappedXHtml(component);
		assertXpathEvaluatesTo(expectedValue, xpathExpression, xhtml);
	}

	/**
	 * Asserts that the <code>xpathExpression</code> evaluates to the <code>expectedValue</code>.
	 *
	 * @param expectedValue the expected value
	 * @param xpathExpression the xpath expression
	 * @param xml the xml to validate
	 * @throws IOException if there is an I/O error
	 * @throws SAXException if there is a parsing error
	 */
	public void assertXpathEvaluatesTo(final String expectedValue, final String xpathExpression,
			final String xml) throws SAXException, IOException {
		XmlAssert
			.assertThat(xml).withNamespaceContext(XMLUtil.NAMESPACE_CONTEXT)
			.valueByXPath(xpathExpression).asString()
			.isEqualTo(expectedValue);
	}

	/**
	 * Renders the <code>component</code> to xhtml and then asserts that the <code>xpathExpression</code> exists in that
	 * xhtml.
	 *
	 * @param xpathExpression the xpath expression to process
	 * @param component the component to validate
	 * @throws IOException if there is an I/O error
	 * @throws SAXException if there is a parsing error
	 */
	public void assertXpathExists(final String xpathExpression, final WebComponent component)
		throws IOException, SAXException {
		String xhtml = toWrappedXHtml(component);
		assertXpathExists(xpathExpression, xhtml);
	}

	/**
	 * Renders the <code>component</code> to xhtml and then asserts that the <code>xpathExpression</code> exists in that
	 * xhtml.
	 *
	 * @param xpathExpression the xpath expression to process
	 * @param xhtml the xhtml to validate
	 * @throws IOException if there is an I/O error
	 * @throws SAXException if there is a parsing error
	 * @throws XpathException if there is a xpath error
	 */
	public void assertXpathExists(final String xpathExpression, final String xhtml)
		throws IOException, SAXException {
		MatcherAssert.assertThat(xhtml, HasXPathMatcher.hasXPath(xpathExpression).withNamespaceContext(XMLUtil.NAMESPACE_CONTEXT));
	}

	/**
	 * Renders the <code>component</code> to xhtml and then asserts that the <code>xpathExpression</code> does not exist
	 * in that xhtml.
	 *
	 * @param xpathExpression the xpath expression to process
	 * @param component the component to validate
	 * @throws IOException if there is an I/O error
	 * @throws SAXException if there is a parsing error
	 */
	public void assertXpathNotExists(final String xpathExpression, final WebComponent component)
			throws IOException, SAXException {
		String xhtml = toWrappedXHtml(component);
		assertXpathNotExists(xpathExpression, xhtml);
	}

	/**
	 * Renders the <code>component</code> to xhtml and then asserts that the <code>xpathExpression</code> does not exist
	 * in that xhtml.
	 *
	 * @param xpathExpression the xpath expression to process
	 * @param xhtml the xhtml to validate
	 * @throws IOException if there is an I/O error
	 * @throws SAXException if there is a parsing error
	 * @throws XpathException if there is a xpath error
	 */
	public void assertXpathNotExists(final String xpathExpression, final String xhtml)
		throws IOException, SAXException {
		MatcherAssert.assertThat(xhtml, IsNot.not(HasXPathMatcher.hasXPath(xpathExpression).withNamespaceContext(XMLUtil.NAMESPACE_CONTEXT)));
	}

	/**
	 * Asserts that rendered output for the component matches the schema.
	 *
	 * @param component the component to validate.
	 *
	 * @throws IOException if there is an I/O error
	 * @throws SAXException if there is a parsing error
	 */
	public void assertSchemaMatch(final WebComponent component) throws IOException,
			SAXException {
		String xhtml = toXHtml(component);
		assertSchemaMatch(xhtml);
	}

	/**
	 * Renders the given component to xml.
	 *
	 * @param component the component to render.
	 * @return the rendered component.
	 */
	public String toXHtml(final WebComponent component) {
		String xhtml = render(component);
		LOG.debug(xhtml);
		return xhtml;
	}

	/**
	 * Some webcomponents generate a fragment of xhtml markup that does not have a single root element, so we add a
	 * "body" root element to the markup fragment and the schema fragment.
	 *
	 * @param component the component to wrap
	 * @return the component wrapped as xhtml
	 */
	protected String toWrappedXHtml(final WebComponent component) {
		String xhtmlFrag = toXHtml(component);
		return wrapXHtml(xhtmlFrag);
	}

	/**
	 * Obtains a UI Context.
	 *
	 * This is called by many of the other methods where a UIContext is not explicitly passed. Subclasses can therefore
	 * e.g. override this to ensure that the same context is always used.
	 *
	 * @return a new UIContext.
	 */
	@Override
	protected UIContext createUIContext() {
		return new UIContextImpl();
	}

	/**
	 * Renders the given component to xml.
	 *
	 * @param component the component to render.
	 * @return the rendered format of the component.
	 */
	public String render(final WebComponent component) {
		boolean needsContext = UIContextHolder.getCurrent() == null;

		if (needsContext) {
			setActiveContext(createUIContext());
		}

		try {
			StringWriter buffer = new StringWriter();

			component.preparePaint(new MockRequest());
			PrintWriter writer = new PrintWriter(buffer);
			component.paint(new WebXmlRenderContext(writer));
			writer.close();

			return buffer.toString();
		} finally {
			if (needsContext) {
				UIContextHolder.popContext();
			}
		}
	}

	/**
	 * Evaluates the given XPath expression against the rendered output of the given component.
	 *
	 * @param component the component to render.
	 * @param xpathExpression the XPath expression to evaluate.
	 * @return the result of the XPath expression on the component's rendered format.
	 *
	 * @throws IOException if there is an I/O error
	 * @throws SAXException if there is a parsing error
	 */
	protected String evaluateXPath(final WebComponent component, final String xpathExpression)
			throws IOException, SAXException {
		final String xml = toWrappedXHtml(component);
		final Source source = Input.fromString(xml).build();
		final XPathEngine engine = new JAXPXPathEngine();
		engine.setNamespaceContext(XMLUtil.NAMESPACE_CONTEXT);
		return engine.evaluate(xpathExpression, source);
	}

	/**
	 * Generates un-escaped malicious content which would be placed as a text node within the XML document.
	 *
	 * @return some malicious content.
	 */
	protected String getMaliciousContent() {
		return "<script language='javascript'>alert('test');</script>{{bad}}";
	}

	/**
	 * Generates un-escaped malicious content which would be placed as an attribute of a self-closing element within the
	 * XML document.
	 *
	 * @return some malicious content.
	 */
	protected String getMaliciousAttribute() {
		return "\"/><script language='javascript'>alert('test');</script>{{bad}}<a name=\"";
	}

	/**
	 * Generates un-escaped malicious content which would be placed as an attribute of an element within the XML
	 * document.
	 *
	 * @param tagName the element tag name.
	 * @return some malicious content.
	 */
	protected String getMaliciousAttribute(final String tagName) {
		return "\"></"
				+ tagName
				+ "><script language='javascript'>alert('test');</script>{{bad}}<"
				+ tagName
				+ " dummy=\"";
	}

	/**
	 * Generates invalid content.
	 *
	 * @return some invalid content.
	 */
	protected String getInvalidCharSequence() {
		return "invalid&character";
	}

	/**
	 * <p>
	 * Asserts that any unsafe content has been escaped correctly. Checks whether the XML is syntactically valid,
	 * matches the component schema and doesn't contain any content returned by
	 * {@link #getMaliciousAttribute()}, {@link #getMaliciousAttribute(String)} or {@link #getMaliciousContent()}.</p>
	 *
	 * <p>
	 * Note: This method assumes that no components are emitting script tags themselves.</p>
	 *
	 * @param component the component to check.
	 * @throws IOException if there is an I/O error
	 * @throws SAXException if there is a parsing error
	 */
	public void assertSafeContent(final WebComponent component) throws IOException,
			SAXException {
		// Render the webcomponent to xhtml.
		String xhtml = toXHtml(component);

		Assert.assertTrue("Unsafe content should have been escaped", xhtml.indexOf("<script") == -1);
		Assert.assertTrue("Unsafe handlebars open bracket should have been escaped", xhtml.indexOf("{{") == -1);
		Assert.assertTrue("Unsafe handlebars close bracket should have been escaped", xhtml.indexOf("}}") == -1);
	}

	/**
	 * Retrieves the webxml renderer for the given component.
	 *
	 * @param component the component to retrieve the renderer for.
	 *
	 * @return the webxml renderer for the component
	 */
	public Renderer getWebXmlRenderer(final WComponent component) {
		return UIManager.getRenderer(component, new WebXmlRenderContext(new PrintWriter(
				new NullWriter())));
	}
}
