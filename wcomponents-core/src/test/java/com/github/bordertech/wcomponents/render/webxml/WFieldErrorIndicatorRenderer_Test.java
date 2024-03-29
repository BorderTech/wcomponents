package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WTextField;
import com.github.bordertech.wcomponents.validation.Diagnostic;
import com.github.bordertech.wcomponents.validation.DiagnosticImpl;
import com.github.bordertech.wcomponents.validation.WFieldErrorIndicator;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * Junit test case for {@link WFieldErrorIndicatorRenderer}.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class WFieldErrorIndicatorRenderer_Test extends AbstractWebXmlRendererTestCase {
	public static final String TAG = "html:span[@is='wc-fieldindicator']";

	/**
	 * Test the Layout is correctly configured.
	 */
	@Test
	public void testRendererCorrectlyConfigured() {
		WFieldErrorIndicator indicator = new WFieldErrorIndicator(new WTextField());
		Assert.assertTrue("Incorrect renderer supplied", getWebXmlRenderer(indicator) instanceof WFieldErrorIndicatorRenderer);
	}

	@Test
	public void testDoPaint() throws IOException, SAXException, XpathException {
		WContainer root = new WContainer();
		WPanel target = new WPanel();
		WFieldErrorIndicator indicator = new WFieldErrorIndicator(target);
		root.add(target);
		root.add(indicator);

		// Simulate Error Message
		setActiveContext(createUIContext());
		List<Diagnostic> diags = new ArrayList<>();
		diags.add(new DiagnosticImpl(Diagnostic.ERROR, target, "Test Error"));
		root.showErrorIndicators(diags);

		// Validate Schema
		// assertSchemaMatch(root);
		// Check Attributes
		assertXpathEvaluatesTo(indicator.getId(), String.format("//%s/@id", TAG), root);
		assertXpathEvaluatesTo("error", String.format("//%s/@data-wc-type", TAG), root);
		assertXpathEvaluatesTo(target.getId(), String.format("//%s/@data-wc-dfor", TAG), root);
		// Check Message
		assertXpathEvaluatesTo("Test Error", String.format("//%s/*[@is='wc-message']", TAG), root);
	}

	@Test
	public void testXssEscaping() throws IOException, SAXException, XpathException {
		WContainer root = new WContainer();
		WPanel target = new WPanel();
		WFieldErrorIndicator indicator = new WFieldErrorIndicator(target);
		root.add(indicator);
		root.add(target);
		List<Diagnostic> diags = new ArrayList<>();
		diags.add(new DiagnosticImpl(Diagnostic.ERROR, target, getMaliciousContent()));
		root.showErrorIndicators(diags);
		assertSafeContent(root);
	}
}
