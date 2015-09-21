package com.github.bordertech.wcomponents.container;

import com.github.bordertech.wcomponents.AbstractWComponentTestCase;
import com.github.bordertech.wcomponents.MockLabel;
import com.github.bordertech.wcomponents.RenderContext;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.render.nil.NullRenderContext;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;
import com.github.bordertech.wcomponents.util.Config;
import com.github.bordertech.wcomponents.util.XMLUtil;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import junit.framework.Assert;
import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.MapConfiguration;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * Test Cases for the {@link ValidateXMLInterceptor} class.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class ValidateXMLInterceptor_Test extends AbstractWComponentTestCase {

	private static Configuration originalConfig;

	/**
	 * Invalid output message.
	 */
	private static final String INCORRECT_OUTPUT_MSG = "Incorrect Output generated.";
	/**
	 * Error not detected message.
	 */
	private static final String DID_NOT_DETECT_ERROR_MSG = "XML is invalid but Interceptor did not detect a problem.";
	/**
	 * Wrapped XML not well formed message.
	 */
	private static final String WRAPPED_XML_INVALID_MSG = "Wrapped XML from Interceptor is not well formed.";
	/**
	 * Output does not have original XML message.
	 */
	private static final String MISSING_ORIGINAL_XML_MSG = "Output does not have the original XML.";

	@BeforeClass
	public static void setUp() {
		originalConfig = Config.getInstance();
		CompositeConfiguration config = new CompositeConfiguration(originalConfig);

		MapConfiguration overrides = new MapConfiguration(new HashMap<String, Object>());
		overrides.setProperty("bordertech.wcomponents.debug.enabled", "true");
		overrides.setProperty("bordertech.wcomponents.debug.validateXML.enabled", "true");
		config.addConfiguration(overrides);

		Config.setConfiguration(config);
	}

	@AfterClass
	public static void tearDown() {
		// Remove overrides
		Config.setConfiguration(originalConfig);
	}

	@Test
	public void testWellFormedXML() {
		String testXML = "<div>this is good</div>";
		MyComponent testUI = new MyComponent(testXML);
		String xml = generateOutput(testUI);
		Assert.assertEquals(INCORRECT_OUTPUT_MSG, testXML, xml);
	}

	@Test
	public void testWellFormedXMLWithNamespace() {
		String testXML = "<ui:comment>this is good with namespace</ui:comment>";
		MyComponent testUI = new MyComponent(testXML);
		String xml = generateOutput(testUI);
		Assert.assertEquals(INCORRECT_OUTPUT_MSG, testXML, xml);
	}

	@Test
	public void testBadlyFormedXMLWithNamespace() {
		String testXML = "<ui:bad>this is bad with namespace";
		MyComponent testUI = new MyComponent(testXML);
		String xml = generateOutput(testUI);
		Assert.assertTrue(DID_NOT_DETECT_ERROR_MSG, hasInterceptorMessage(xml));
		Assert.assertTrue(WRAPPED_XML_INVALID_MSG, checkXMLWellFormed(xml));
		Assert.assertTrue(MISSING_ORIGINAL_XML_MSG, hasOriginalXML(xml, testXML));
	}

	@Test
	public void testWellFormedXMLWithDeclaration() {
		String testXML = wrapXML("<div>this is good with declaration</div>");
		MyComponent testUI = new MyComponent(testXML);
		String xml = generateOutput(testUI);
		Assert.assertEquals(INCORRECT_OUTPUT_MSG, testXML, xml);
	}

	@Test
	public void testBadlyFormedXMLWithDeclaration() {
		String testXML = wrapXML("<bad>this is bad with declaration");
		MyComponent testUI = new MyComponent(testXML);
		String xml = generateOutput(testUI);
		Assert.assertTrue(DID_NOT_DETECT_ERROR_MSG, hasInterceptorMessage(xml));
		Assert.assertTrue(WRAPPED_XML_INVALID_MSG, checkXMLWellFormed(xml));
		Assert.assertTrue(MISSING_ORIGINAL_XML_MSG, hasOriginalXML(xml, testXML));
	}

	@Test
	public void testWellFormedXMLWithDoctype() {
		String testXML = XMLUtil.DOC_TYPE + "<ui:root " + XMLUtil.STANDARD_NAMESPACES + ">" + "<div>this is good xml with doctype</div></ui:root>";
		MyComponent testUI = new MyComponent(testXML);
		String xml = generateOutput(testUI);
		Assert.assertEquals(INCORRECT_OUTPUT_MSG, testXML, xml);
	}

	@Test
	public void testBadlyFormedXMLWithDoctype() {
		String testXML = XMLUtil.DOC_TYPE + "<bad>this is bad xml with doctype";
		MyComponent testUI = new MyComponent(testXML);
		String xml = generateOutput(testUI);
		Assert.assertTrue(DID_NOT_DETECT_ERROR_MSG, hasInterceptorMessage(xml));
		Assert.assertTrue(WRAPPED_XML_INVALID_MSG, checkXMLWellFormed(xml));
		Assert.assertTrue(MISSING_ORIGINAL_XML_MSG, hasOriginalXML(xml, testXML));
	}

	@Test
	public void testWellFormedXMLWithChild() {
		String testXML = "<div>this is good with child</div>";
		String childXML = "<child>I am a child</child>";
		MyComponent testUI = new MyComponent(testXML);
		MyComponent child1 = new MyComponent(childXML);
		testUI.add(child1);
		String xml = generateOutput(testUI);
		Assert.assertEquals(INCORRECT_OUTPUT_MSG, testXML + childXML, xml);
	}

	@Test
	public void testWellFormedXMLWithBadInvisibleChild() {
		String testXML = "<div>this is good with invisible child</div>";
		String childXML1 = "<child>I am a child</child>";
		String childXML2 = "<badchild>";
		MyComponent testUI = new MyComponent(testXML);
		MyComponent child1 = new MyComponent(childXML1);
		MyComponent child2 = new MyComponent(childXML2);
		child2.setVisible(false);
		testUI.add(child1);
		testUI.add(child2);
		String xml = generateOutput(testUI);
		Assert.assertEquals(INCORRECT_OUTPUT_MSG, testXML + childXML1, xml);
	}

	@Test
	public void testWellFormedXMLWithNBSP() {
		String testXML = "<div>this is good &nbsp;</div>";
		MyComponent testUI = new MyComponent(testXML);
		String xml = generateOutput(testUI);
		Assert.assertEquals(INCORRECT_OUTPUT_MSG, testXML, xml);
	}

	@Test
	public void testWellFormedXMLWithEntities() {
		String testXML = "<div>&lt; &gt; &apos; &quot; &amp;</div>";
		MyComponent testUI = new MyComponent(testXML);
		String xml = generateOutput(testUI);
		Assert.assertEquals(INCORRECT_OUTPUT_MSG, testXML, xml);
	}

	@Test
	public void testBadlyFormedXML() {
		String testXML = "<badxml>";
		MyComponent testUI = new MyComponent(testXML);
		String xml = generateOutput(testUI);
		Assert.assertTrue(DID_NOT_DETECT_ERROR_MSG, hasInterceptorMessage(xml));
		Assert.assertTrue(WRAPPED_XML_INVALID_MSG, checkXMLWellFormed(xml));
		Assert.assertTrue(MISSING_ORIGINAL_XML_MSG, hasOriginalXML(xml, testXML));
	}

	@Test
	public void testBadlyFormedXMLWithChild() {
		String testXML = "<div>this is good with bad child</div>";
		String childXML = "<badxml>";
		MyComponent testUI = new MyComponent(testXML);
		MyComponent child1 = new MyComponent(childXML);
		testUI.add(child1);
		String xml = generateOutput(testUI);
		Assert.assertTrue(DID_NOT_DETECT_ERROR_MSG, hasInterceptorMessage(xml));
		Assert.assertTrue(WRAPPED_XML_INVALID_MSG, checkXMLWellFormed(xml));
		Assert.assertTrue(MISSING_ORIGINAL_XML_MSG, hasOriginalXML(xml, testXML + childXML));
	}

	@Test
	public void testBadlyFormedXMLWithCDATA() {
		String testXML = "<badxml><![CDATA[ in side the section ]]>";
		MyComponent testUI = new MyComponent(testXML);
		String xml = generateOutput(testUI);
		Assert.assertTrue(DID_NOT_DETECT_ERROR_MSG, hasInterceptorMessage(xml));
		Assert.assertTrue("CDATA Start not formatted correctly", xml.indexOf("CDATASTART") != -1);
		Assert.assertTrue("CDATA Finish not formatted correctly", xml.indexOf("CDATAFINISH") != -1);
		Assert.assertTrue(WRAPPED_XML_INVALID_MSG, checkXMLWellFormed(xml));
	}

	@Test
	public void testUnknownRenderContext() {
		MockLabel label = new MockLabel("testUnknownRenderContext");
		InterceptorComponent interceptor = new ValidateXMLInterceptor();
		interceptor.setBackingComponent(label);

		interceptor.paint(new NullRenderContext());

		Assert.assertEquals("Label should have been painted", label.getPaintCount(), 1);
	}

	/**
	 * Check if the XML contains the correct inteceptor message.
	 *
	 * @param xml the xml to check
	 * @return true if xml string contains the inteceptor message
	 */
	private boolean hasInterceptorMessage(final String xml) {
		return (xml.indexOf("Invalid XML") != -1);
	}

	/**
	 * Check if the wrapped XML contains the original XML.
	 *
	 * @param xml the xml to check
	 * @param originalXML the original xml
	 * @return true if xml string contains the original xml
	 */
	private boolean hasOriginalXML(final String xml, final String originalXML) {
		return (xml.indexOf(originalXML) != -1);
	}

	/**
	 * Check the XML is well formed.
	 *
	 * @param xml is the xml to be parsed
	 * @return true if the xml is well formed
	 */
	private boolean checkXMLWellFormed(final String xml) {
		try {
			XMLReader reader = XMLReaderFactory.createXMLReader();
			reader.parse(new InputSource(new StringReader(xml)));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Extend WComponent to setup well/badly formed XML.
	 */
	private static final class MyComponent extends WContainer {

		/**
		 * Content to be painted by the test component.
		 */
		private final String content;

		/**
		 * @param content the test content
		 */
		private MyComponent(final String content) {
			this.content = content;
		}

		@Override
		protected void paintComponent(final RenderContext renderContext) {
			// We can safely cast here, as this test only uses a WebXmlRenderer
			((WebXmlRenderContext) renderContext).getWriter().print(content);
			super.paintComponent(renderContext);
		}
	}

	/**
	 * Execute the intercepter and generate the XML Output.
	 *
	 * @param testUI the test component
	 * @return the response
	 */
	private String generateOutput(final MyComponent testUI) {
		InterceptorComponent interceptor = new ValidateXMLInterceptor();
		interceptor.setBackingComponent(testUI);
		StringWriter writer = new StringWriter();
		setActiveContext(createUIContext());

		try {
			interceptor.paint(new WebXmlRenderContext(new PrintWriter(writer)));
		} finally {
			resetContext();
		}

		return writer.toString();
	}

	/**
	 * Wrap XML with definitions.
	 *
	 * @param xml the xml string to wrap
	 * @return the wrapped xml.
	 */
	private String wrapXML(final String xml) {
		return XMLUtil.XML_DECLERATION + "<ui:root" + XMLUtil.STANDARD_NAMESPACES + ">" + xml + "</ui:root>";
	}
}
