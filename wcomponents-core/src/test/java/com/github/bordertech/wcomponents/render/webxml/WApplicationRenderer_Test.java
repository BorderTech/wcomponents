package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.MockWEnvironment;
import com.github.bordertech.wcomponents.UIContext;
import com.github.bordertech.wcomponents.WApplication;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WLabel;
import com.github.bordertech.wcomponents.WText;
import com.github.bordertech.wcomponents.WTextField;
import com.github.bordertech.wcomponents.util.Config;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import junit.framework.Assert;
import org.apache.commons.configuration.Configuration;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * Junit test cases for {@link WApplicationRenderer}.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class WApplicationRenderer_Test extends AbstractWebXmlRendererTestCase {

	@Test
	public void testRendererCorrectlyConfigured() {
		WApplication application = new WApplication();
		Assert.assertTrue("Incorrect renderer supplied",
				getWebXmlRenderer(application) instanceof WApplicationRenderer);
	}

	@Test
	public void testDoPaint() throws IOException, SAXException, XpathException {
		WApplication application = new WApplication();

		MockWEnvironment environment = new MockWEnvironment();
		UIContext uic = createUIContext();
		uic.setEnvironment(environment);
		setActiveContext(uic);

		// Test with no unsavedChanges
		assertSchemaMatch(application);
		assertXpathEvaluatesTo(WComponent.DEFAULT_APPLICATION_ID, "//ui:application/@id",
				application);
		assertXpathEvaluatesTo("", "//ui:application/@unsavedChanges", application);

		// Test with unsavedChanges
		application.setUnsavedChanges(true);
		assertSchemaMatch(application);
		assertXpathEvaluatesTo(WComponent.DEFAULT_APPLICATION_ID, "//ui:application/@id",
				application);
		assertXpathEvaluatesTo("true", "//ui:application/@unsavedChanges", application);
	}

	@Test
	public void testDoPaintWithChildren() throws IOException, SAXException, XpathException {
		WApplication application = new WApplication();
		WText text = new WText("test text");
		WButton button = new WButton("button");
		application.add(text);
		application.add(button);

		MockWEnvironment environment = new MockWEnvironment();
		UIContext uic = createUIContext();
		uic.setEnvironment(environment);
		setActiveContext(uic);

		application.setUnsavedChanges(true);

		// Check Schema
		assertSchemaMatch(application);
		assertXpathEvaluatesTo(WComponent.DEFAULT_APPLICATION_ID, "//ui:application/@id",
				application);
		assertXpathEvaluatesTo("true", "//ui:application/@unsavedChanges", application);
		// Check Children
		assertXpathEvaluatesTo("test text", "normalize-space(//ui:application/text()[1])",
				application);
		assertXpathEvaluatesTo("1", "count(//ui:application/ui:button)", application);
	}

	@Test
	public void testBasicRenderedFormat() throws XpathException, IOException, SAXException {
		// Basic component (no optional fields)
		MockWEnvironment environment = new MockWEnvironment();
		WApplication application = new WApplication();
		environment.setPostPath("WApplicationRendererTest.postPath");

		UIContext uic = createUIContext();
		uic.setEnvironment(environment);
		uic.setUI(application);
		setActiveContext(uic);

		assertSchemaMatch(application);
		assertXpathEvaluatesTo(environment.getPostPath(), "//ui:application/@applicationUrl",
				application);
		assertXpathEvaluatesTo(environment.getWServletPath(), "//ui:application/@ajaxUrl",
				application);
		assertXpathEvaluatesTo(environment.getWServletPath(), "//ui:application/@dataUrl",
				application);
		assertXpathNotExists("//ui:application/@defaultFocusId", application);
	}

	@Test
	public void testRenderedFormatWithFocussedComponent() throws XpathException, IOException,
			SAXException {
		MockWEnvironment environment = new MockWEnvironment();
		WApplication application = new WApplication();
		environment.setPostPath("WApplicationRendererTest.postPath");

		WTextField focussedComponent = new WTextField();
		application.add(focussedComponent);

		UIContext uic = createUIContext();
		uic.setEnvironment(environment);
		uic.setUI(application);

		uic.setFocusRequired(true);
		uic.setFocussed(focussedComponent);
		setActiveContext(uic);

		assertSchemaMatch(application);
		assertXpathEvaluatesTo(environment.getPostPath(), "//ui:application/@applicationUrl",
				application);
		assertXpathEvaluatesTo(WComponent.DEFAULT_APPLICATION_ID, "//ui:application/@id",
				application);
		assertXpathEvaluatesTo(environment.getWServletPath(), "//ui:application/@ajaxUrl",
				application);
		assertXpathEvaluatesTo(environment.getWServletPath(), "//ui:application/@dataUrl",
				application);
		assertXpathEvaluatesTo(focussedComponent.getId(), "//ui:application/@defaultFocusId",
				application);

		uic.setFocusRequired(false);
		assertXpathNotExists("//ui:application/@defaultFocusId", application);
	}

	@Test
	public void testRenderedFormatWithHiddenFields() throws XpathException, IOException,
			SAXException {
		MockWEnvironment environment = new MockWEnvironment();
		environment.setPostPath("WApplicationRendererTest.postPath");

		WApplication application = new WApplication();

		UIContext uic = createUIContext();
		uic.setEnvironment(environment);
		uic.setUI(application);
		setActiveContext(uic);

		WLabel label = new WLabel("dummy");
		application.add(label);

		Map<String, String> hiddenParameters = new HashMap<>();
		hiddenParameters.put("keyA", "valueA");
		hiddenParameters.put("keyB", "valueB");
		environment.setHiddenParameters(hiddenParameters);

		assertSchemaMatch(application);
		assertXpathEvaluatesTo(label.getText(), "normalize-space(//ui:application/ui:label)",
				application);
		assertXpathEvaluatesTo("valueA", "//ui:application/ui:param[@name='keyA']/@value",
				application);
		assertXpathEvaluatesTo("valueB", "//ui:application/ui:param[@name='keyB']/@value",
				application);
	}

	@Test
	public void testXssEscaping() throws IOException, SAXException, XpathException {
		MockWEnvironment environment = new MockWEnvironment();
		environment.setPostPath("WApplicationRendererTest.postPath");

		WApplication application = new WApplication();

		UIContext uic = createUIContext();
		uic.setEnvironment(environment);
		uic.setUI(application);
		setActiveContext(uic);

		application.setTitle(getMaliciousAttribute("ui:application"));
		assertSafeContent(application);

		uic.getEnvironment().getHiddenParameters().put(getMaliciousAttribute("ui:param"), "dummy");
		uic.getEnvironment().getHiddenParameters().put("dummy", getMaliciousAttribute("ui:param"));
		assertSafeContent(application);
	}

	@Test
	public void testRendererTracking() throws IOException, SAXException, XpathException {
		// No tracking
		WApplication application = new WApplication();
		assertSchemaMatch(application);
		assertXpathNotExists("//ui:application/ui:analytic", application);

		// Want to test with "tracking details set"
		Configuration originalConfig = Config.getInstance();
		Configuration config = Config.copyConfiguration(originalConfig);
		config.setProperty("bordertech.wcomponents.tracking.clientid", "CID");
		config.setProperty("bordertech.wcomponents.tracking.applicationname", "APPL");
		config.setProperty("bordertech.wcomponents.tracking.cookiedomain", "CD");
		config.setProperty("bordertech.wcomponents.tracking.datacollectiondomain", "DCD");
		Config.setConfiguration(config);

		try {
			assertSchemaMatch(application);
			assertXpathEvaluatesTo("CID", "//ui:application/ui:analytic/@clientId", application);
			assertXpathEvaluatesTo("APPL", "//ui:application/ui:analytic/@name", application);
			assertXpathEvaluatesTo("CD", "//ui:application/ui:analytic/@cd", application);
			assertXpathEvaluatesTo("DCD", "//ui:application/ui:analytic/@dcd", application);
		} finally {
			// Remove overrides
			Config.setConfiguration(originalConfig);
		}
	}

	@Test
	public void testJsResources() throws IOException, SAXException, XpathException {
		// No resource
		WApplication application = new WApplication();
		assertSchemaMatch(application);
		assertXpathNotExists("//ui:application/ui:js", application);

		// Add URL resource
		application.addJsUrl("URL");
		assertSchemaMatch(application);
		assertXpathEvaluatesTo("URL", "//ui:application/ui:js/@url", application);
	}

	@Test
	public void testCssResources() throws IOException, SAXException, XpathException {
		// No resource
		WApplication application = new WApplication();
		assertSchemaMatch(application);
		assertXpathNotExists("//ui:application/ui:css", application);

		// Add URL resource
		application.addCssUrl("URL");
		assertSchemaMatch(application);
		assertXpathEvaluatesTo("URL", "//ui:application/ui:css/@url", application);
	}

}
