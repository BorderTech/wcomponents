package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WLabel;
import com.github.bordertech.wcomponents.WTextArea;
import java.io.IOException;
import junit.framework.Assert;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * Junit test case for {@link WLabelRenderer}.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class WLabelRenderer_Test extends AbstractWebXmlRendererTestCase {

	/**
	 * Test the Layout is correctly configured.
	 */
	@Test
	public void testRendererCorrectlyConfigured() {
		WLabel label = new WLabel();
		Assert.assertTrue("Incorrect renderer supplied",
				getWebXmlRenderer(label) instanceof WLabelRenderer);
	}

	@Test
	public void testDoPaintBasic() throws IOException, SAXException, XpathException {
		WLabel label = new WLabel();

		// Validate Schema
		assertSchemaMatch(label);
		// Check Attributes
		assertXpathEvaluatesTo(label.getId(), "//ui:label/@id", label);
		assertXpathEvaluatesTo("", "//ui:label/@for", label);
		assertXpathEvaluatesTo("", "//ui:label/@hint", label);
		assertXpathEvaluatesTo("", "//ui:label/@required", label);
		assertXpathEvaluatesTo("", "//ui:label/@accessKey", label);
		// Check Label
		assertXpathEvaluatesTo("", "//ui:label", label);
	}

	@Test
	public void testDoPaintAllOptions() throws IOException, SAXException, XpathException {
		WTextArea text = new WTextArea();
		text.setText("text1");
		WLabel label = new WLabel();
		label.setForComponent(text);
		label.setHint("hint1");
		label.setAccessKey('A');
		label.setText("label1");

		WContainer root = new WContainer();
		root.add(label);
		root.add(text);

		// Validate Schema
		assertSchemaMatch(root);
		// Check Attributes
		assertXpathEvaluatesTo(label.getId(), "//ui:label/@id", label);
		assertXpathEvaluatesTo(text.getId(), "//ui:label/@for", label);
		assertXpathEvaluatesTo("hint1", "//ui:label/@hint", label);
		assertXpathEvaluatesTo("A", "//ui:label/@accessKey", label);
		// Check Label
		assertXpathEvaluatesTo("label1", "//ui:label", label);

		// Add Children to Label
		WTextArea text2 = new WTextArea();
		text2.setText("text2");
		label.add(text2);
		assertSchemaMatch(root);
		assertXpathEvaluatesTo("text2", "//ui:label/ui:textarea", label);
	}

	@Test
	public void testXssEscaping() throws IOException, SAXException, XpathException {
		WLabel label = new WLabel(getMaliciousContent());

		assertSafeContent(label);

		label.setHint(getMaliciousAttribute("ui:label"));
		assertSafeContent(label);

		label.setToolTip(getMaliciousAttribute("ui:label"));
		assertSafeContent(label);

		label.setAccessibleText(getMaliciousAttribute("ui:label"));
		assertSafeContent(label);
	}
}
