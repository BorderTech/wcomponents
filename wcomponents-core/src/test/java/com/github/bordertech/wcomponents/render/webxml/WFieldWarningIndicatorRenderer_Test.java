package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WTextField;
import com.github.bordertech.wcomponents.validation.Diagnostic;
import com.github.bordertech.wcomponents.validation.DiagnosticImpl;
import com.github.bordertech.wcomponents.validation.WFieldWarningIndicator;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import junit.framework.Assert;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * Junit test case for {@link WFieldWarningIndicatorRenderer}.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class WFieldWarningIndicatorRenderer_Test extends AbstractWebXmlRendererTestCase {

	/**
	 * Test the Layout is correctly configured.
	 */
	@Test
	public void testRendererCorrectlyConfigured() {
		WFieldWarningIndicator indicator = new WFieldWarningIndicator(new WTextField());
		Assert.assertTrue("Incorrect renderer supplied",
				getWebXmlRenderer(indicator) instanceof WFieldWarningIndicatorRenderer);
	}

	@Test
	public void testDoPaint() throws IOException, SAXException, XpathException {
		WContainer root = new WContainer();
		WTextField text = new WTextField();
		text.setMandatory(true);
		WFieldWarningIndicator indicator = new WFieldWarningIndicator(text);

		root.add(indicator);
		root.add(text);

		// Simulate Warning Message
		setActiveContext(createUIContext());
		List<Diagnostic> diags = new ArrayList<>();
		diags.add(new DiagnosticImpl(Diagnostic.WARNING, text, "Test Warning"));
		root.showWarningIndicators(diags);

		// Validate Schema
		assertSchemaMatch(root);
		// Check Attributes
		assertXpathEvaluatesTo(indicator.getId(), "//ui:fieldindicator/@id", root);
		assertXpathEvaluatesTo("warn", "//ui:fieldindicator/@type", root);
		assertXpathEvaluatesTo(text.getId(), "//ui:fieldindicator/@for", root);
		// Check Message
		assertXpathEvaluatesTo("Test Warning", "//ui:fieldindicator/ui:message", root);
	}

	@Test
	public void testXssEscaping() throws IOException, SAXException, XpathException {
		WContainer root = new WContainer();
		WTextField text = new WTextField();
		WFieldWarningIndicator indicator = new WFieldWarningIndicator(text);

		root.add(indicator);
		root.add(text);

		setActiveContext(createUIContext());
		List<Diagnostic> diags = new ArrayList<>();
		diags.add(new DiagnosticImpl(Diagnostic.WARNING, text, getMaliciousContent()));
		root.showWarningIndicators(diags);

		assertSafeContent(root);
	}
}
