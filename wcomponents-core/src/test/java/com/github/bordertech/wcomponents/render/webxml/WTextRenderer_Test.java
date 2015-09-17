package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.WText;
import com.github.bordertech.wcomponents.WebUtilities;
import java.io.IOException;
import junit.framework.Assert;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * Junit test case for {@link WText}.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class WTextRenderer_Test extends AbstractWebXmlRendererTestCase {

	@Test
	public void testRendererCorrectlyConfigured() {
		WText text = new WText();
		Assert.assertTrue("Incorrect renderer supplied",
				getWebXmlRenderer(text) instanceof WTextRenderer);
	}

	@Test
	public void testDoPaintWhenEmpty() throws IOException, SAXException, XpathException {
		WText text = new WText();
		String xml = toXHtml(text);
		Assert.assertEquals("Text output should be empty by default", "", xml);
	}

	@Test
	public void testXssEscaping() throws IOException, SAXException, XpathException {
		WText text = new WText();

		text.setText(getInvalidCharSequence());
		assertSafeContent(text);

		text.setText(getMaliciousContent());
		assertSafeContent(text);
	}

	@Test
	public void testEncodeText() throws IOException, SAXException, XpathException {
		String value = "T1<b>T2</b>T3";
		String encoded = WebUtilities.encode(value);

		WText text = new WText();
		text.setText(value);

		// Encoded (default)
		String xml = toXHtml(text);
		Assert.assertTrue("XML should have encoded text", xml.contains(encoded));

		// Not encoded
		text.setEncodeText(false);
		xml = toXHtml(text);
		Assert.assertTrue("XML should have not encoded text", xml.contains(value));
	}

}
