package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.WAbbrText;
import java.io.IOException;
import junit.framework.Assert;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * Junit test case for {@link WAbbrTextRenderer}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WAbbrTextRenderer_Test extends AbstractWebXmlRendererTestCase {

	@Test
	public void testRendererCorrectlyConfigured() {
		WAbbrText component = new WAbbrText();
		Assert.assertTrue("Incorrect renderer supplied",
				getWebXmlRenderer(component) instanceof WAbbrTextRenderer);
	}

	@Test
	public void testDoPaint() throws IOException, SAXException, XpathException {
		String textString = "abbreviated string";
		String abbrString = "The really long non-abbreviated string";

		WAbbrText text = new WAbbrText();
		text.setText(textString);
		text.setAbbrText(abbrString);

		// Test with no abbreviation
		assertSchemaMatch(text);

		assertXpathEvaluatesTo(textString, "//ui:abbr", text);
		assertXpathEvaluatesTo(abbrString, "//ui:abbr/@description", text);
	}

	@Test
	public void testXssEscaping() throws IOException, SAXException, XpathException {
		WAbbrText text = new WAbbrText();

		text.setText(getMaliciousContent());
		assertSafeContent(text);

		text.setAbbrText(getMaliciousAttribute("ui:abbr"));
		assertSafeContent(text);
	}
}
