package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WTextField;
import com.github.bordertech.wcomponents.validation.Diagnostic;
import com.github.bordertech.wcomponents.validation.DiagnosticImpl;
import com.github.bordertech.wcomponents.validation.WFieldErrorIndicator;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import junit.framework.Assert;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * Junit test case for {@link WFieldErrorIndicatorRenderer}.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class WFieldErrorIndicatorRenderer_Test extends AbstractWebXmlRendererTestCase {

	/**
	 * Test the Layout is correctly configured.
	 */
	@Test
	public void testRendererCorrectlyConfigured() {
		WFieldErrorIndicator indicator = new WFieldErrorIndicator(new WTextField());
		Assert.assertTrue("Incorrect renderer supplied",
				getWebXmlRenderer(indicator) instanceof WFieldErrorIndicatorRenderer);
	}

	@Test
	public void testDoPaint() throws IOException, SAXException, XpathException {
		WContainer root = new WContainer();
		WTextField text = new WTextField();
		WFieldErrorIndicator indicator = new WFieldErrorIndicator(text);

		root.add(indicator);
		root.add(text);

		// Simulate Error Message
		setActiveContext(createUIContext());
		List<Diagnostic> diags = new ArrayList<>();
		diags.add(new DiagnosticImpl(Diagnostic.ERROR, text, "Test Error"));
		root.showErrorIndicators(diags);

		// Validate Schema
		assertSchemaMatch(root);
		// Check Attributes
		assertXpathEvaluatesTo(indicator.getId(), "//ui:fieldindicator/@id", root);
		assertXpathEvaluatesTo("error", "//ui:fieldindicator/@type", root);
		assertXpathEvaluatesTo(text.getId(), "//ui:fieldindicator/@for", root);
		// Check Message
		assertXpathEvaluatesTo("Test Error", "//ui:fieldindicator/ui:message", root);
	}

	@Test
	public void testXssEscaping() throws IOException, SAXException, XpathException {
		WContainer root = new WContainer();
		WTextField text = new WTextField();
		WFieldErrorIndicator indicator = new WFieldErrorIndicator(text);

		root.add(indicator);
		root.add(text);

		List<Diagnostic> diags = new ArrayList<>();
		diags.add(new DiagnosticImpl(Diagnostic.ERROR, text, getMaliciousContent()));
		root.showErrorIndicators(diags);

		assertSafeContent(root);
	}
}
