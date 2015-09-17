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
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import junit.framework.Assert;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.custommonkey.xmlunit.NamespaceContext;
import org.custommonkey.xmlunit.Validator;
import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;
import org.custommonkey.xmlunit.XpathEngine;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

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
	 * We need to register the "ui" prefix with XMLUnit so that we can use it in XPath expressions.
	 */
	@BeforeClass
	public static void registerUINamespace() {
		NamespaceContext context = XMLUnit.getXpathNamespaceContext();
		context = new XmlLayoutTestNamespaceContext(context);
		XMLUnit.setXpathNamespaceContext(context);
	}

	/**
	 * Restores the initial XMLUnit namespace context.
	 */
	@AfterClass
	public static void restoreNamespaces() {
		XmlLayoutTestNamespaceContext context = (XmlLayoutTestNamespaceContext) XMLUnit.
				getXpathNamespaceContext();
		XMLUnit.setXpathNamespaceContext(context.backing);
	}

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
		Validator validator = getSchemaValidator(xhtml);
		validator.assertIsValid();
	}

	/**
	 * Asserts that the given xhtml does not match the schema.
	 *
	 * @param xhtml the xhtml to validate.
	 *
	 * @throws IOException if there is an I/O error
	 * @throws SAXException if there is a parsing error
	 */
	public void assertSchemaMismatch(final String xhtml) throws IOException,
			SAXException {
		Validator validator = getSchemaValidator(xhtml);
		Assert.assertFalse("xhtml should not validate", validator.isValid());
	}

	/**
	 * Obtains an XMLUnit schema validator for validating the output.
	 *
	 * @param xhtml the html to validate
	 * @return the validator to use.
	 */
	protected Validator getSchemaValidator(final String xhtml) {
		// Some web components generate a fragment of xhtml markup that does not
		// have a single root element, so we add a "body" root element to the
		// markup fragment and the schema fragment.

		try {
			// Load the schema.
			String schemaPath = getSchemaPath();
			Object schema = AbstractWebXmlRendererTestCase.class.getResource(schemaPath).toString();

			String wrappedXhtml = wrapXHtml(xhtml);

			// Validate the xhtml.
			Validator validator;
			StringReader reader = new StringReader(wrappedXhtml);
			validator = new Validator(reader);
			validator.useXMLSchema(true);
			validator.setJAXP12SchemaSource(schema);

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
		return XMLUtil.XML_DECLERATION
				+ "<ui:root " + XMLUtil.STANDARD_NAMESPACES + ">"
				+ xml
				+ "</ui:root>";
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
	 * @throws XpathException if there is a xpath error
	 */
	public void assertXpathEvaluatesTo(final String expectedValue, final String xpathExpression,
			final WebComponent component) throws SAXException, IOException, XpathException {
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
	 * @throws XpathException if there is a xpath error
	 */
	public void assertXpathEvaluatesTo(final String expectedValue, final String xpathExpression,
			final String xml) throws SAXException, IOException, XpathException {
		XMLAssert.assertXpathEvaluatesTo(expectedValue, xpathExpression, xml);
	}

	/**
	 * Renders the <code>component</code> to xhtml and then asserts that the <code>xpathExpression</code> exists in that
	 * xhtml.
	 *
	 * @param xpathExpression the xpath expression to process
	 * @param component the component to validate
	 * @throws IOException if there is an I/O error
	 * @throws SAXException if there is a parsing error
	 * @throws XpathException if there is a xpath error
	 */
	public void assertXpathExists(final String xpathExpression, final WebComponent component) throws
			XpathException,
			IOException, SAXException {
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
	public void assertXpathExists(final String xpathExpression, final String xhtml) throws
			XpathException,
			IOException, SAXException {
		XMLAssert.assertXpathExists(xpathExpression, xhtml);
	}

	/**
	 * Renders the <code>component</code> to xhtml and then asserts that the <code>xpathExpression</code> does not exist
	 * in that xhtml.
	 *
	 * @param xpathExpression the xpath expression to process
	 * @param component the component to validate
	 * @throws IOException if there is an I/O error
	 * @throws SAXException if there is a parsing error
	 * @throws XpathException if there is a xpath error
	 */
	public void assertXpathNotExists(final String xpathExpression, final WebComponent component)
			throws XpathException,
			IOException, SAXException {
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
	public void assertXpathNotExists(final String xpathExpression, final String xhtml) throws
			XpathException,
			IOException, SAXException {
		XMLAssert.assertXpathNotExists(xpathExpression, xhtml);
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
	 * Creates an XML Unit validator for the given xml.
	 *
	 * @param xml the xml to validate
	 * @return an XML Unit validator that for validating the xml.
	 */
	public Validator setupValidator(final String xml) {
		try {
			StringReader reader = new StringReader(xml);
			Validator validator = new Validator(reader);
			validator.useXMLSchema(true);
			validator.setJAXP12SchemaSource(new File(getSchemaPath()));
			return validator;
		} catch (SAXException ex) {
			LOG.error("Unexpected error while testing", ex);
			throw new IllegalStateException("Unable to set up validator");
		}
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
	 * @throws XpathException if there is a xpath error
	 */
	protected String evaluateXPath(final WebComponent component, final String xpathExpression)
			throws IOException,
			SAXException, XpathException {
		String xml = render(component);
		Document doc = XMLUnit.buildControlDocument(wrapXHtml(xml));
		XpathEngine simpleXpathEngine = XMLUnit.newXpathEngine();
		return simpleXpathEngine.evaluate(xpathExpression, doc);
	}

	/**
	 * A namespace context which can resolve the html and ui namespaces without requiring network access. This is
	 * necessary when running tests behing a firewall.
	 */
	private static final class XmlLayoutTestNamespaceContext implements NamespaceContext {

		/**
		 * The backing namespace.
		 */
		private final NamespaceContext backing;

		/**
		 * Creates an XmlLayoutTestNamespaceContext.
		 *
		 * @param backing the backing context, used to resolve other namespaces.
		 */
		XmlLayoutTestNamespaceContext(final NamespaceContext backing) {
			this.backing = backing;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getNamespaceURI(final String prefix) {
			if ("ui".equals(prefix)) {
				return getThemeURI();
			} else if ("html".equals(prefix)) {
				return XMLUtil.XHTML_URI;
			} else if (backing != null) {
				return backing.getNamespaceURI(prefix);
			}

			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Iterator<String> getPrefixes() {
			Set<String> prefixes = new HashSet<>(2);
			prefixes.add("ui");
			prefixes.add("html");

			if (backing != null) {
				for (Iterator i = backing.getPrefixes(); i.hasNext();) {
					prefixes.add((String) i.next());
				}
			}

			return prefixes.iterator();
		}
	}

	/**
	 * Generates un-escaped malicious content which would be placed as a text node within the XML document.
	 *
	 * @return some malicious content.
	 */
	protected String getMaliciousContent() {
		return "<script language='javascript'>alert('test');</script>";
	}

	/**
	 * Generates un-escaped malicious content which would be placed as an attribute of a self-closing element within the
	 * XML document.
	 *
	 * @return some malicious content.
	 */
	protected String getMaliciousAttribute() {
		return "\"/><script language='javascript'>alert('test');</script><a name=\"";
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
				+ "><script language='javascript'>alert('test');</script><"
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

		assertSchemaMatch(xhtml);
		Assert.assertTrue("Unsafe content should have been escaped", xhtml.indexOf("<script") == -1);
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
