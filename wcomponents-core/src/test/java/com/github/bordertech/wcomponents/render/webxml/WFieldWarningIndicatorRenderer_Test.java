package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WTextField;
import com.github.bordertech.wcomponents.validation.Diagnostic;
import com.github.bordertech.wcomponents.validation.DiagnosticImpl;
import com.github.bordertech.wcomponents.validation.WFieldWarningIndicator;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
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
		WPanel target = new WPanel();
		WFieldWarningIndicator indicator = new WFieldWarningIndicator(target);

		root.add(target);
		root.add(indicator);

		// Simulate Warning Message
		setActiveContext(createUIContext());
		List<Diagnostic> diags = new ArrayList<>();
		diags.add(new DiagnosticImpl(Diagnostic.WARNING, target, "Test Warning"));
		root.showWarningIndicators(diags);

		// Validate Schema
		// assertSchemaMatch(root);
		// Check Attributes
		assertXpathEvaluatesTo(indicator.getId(), String.format("//%s/@id", WFieldErrorIndicatorRenderer_Test.TAG), root);
		assertXpathEvaluatesTo("warn", String.format("//%s/@data-wc-type", WFieldErrorIndicatorRenderer_Test.TAG), root);
		assertXpathEvaluatesTo(target.getId(), String.format("//%s/@data-wc-dfor", WFieldErrorIndicatorRenderer_Test.TAG), root);
		// Check Message
		assertXpathEvaluatesTo("Test Warning", String.format("//%s/*[@is='wc-message']", WFieldErrorIndicatorRenderer_Test.TAG), root);
	}

	@Test
	public void testXssEscaping() throws IOException, SAXException, XpathException {
		WContainer root = new WContainer();
		WPanel target = new WPanel();
		WFieldWarningIndicator indicator = new WFieldWarningIndicator(target);

		root.add(target);
		root.add(indicator);

		setActiveContext(createUIContext());
		List<Diagnostic> diags = new ArrayList<>();
		diags.add(new DiagnosticImpl(Diagnostic.WARNING, target, getMaliciousContent()));
		root.showWarningIndicators(diags);

		assertSafeContent(root);
	}
}
