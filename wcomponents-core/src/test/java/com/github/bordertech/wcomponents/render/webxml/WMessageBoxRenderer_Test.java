package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.WMessageBox;
import com.github.bordertech.wcomponents.WebUtilities;
import java.io.IOException;
import junit.framework.Assert;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * Junit test case for {@link WMessageBoxRenderer}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WMessageBoxRenderer_Test extends AbstractWebXmlRendererTestCase {

	@Test
	public void testRendererCorrectlyConfigured() {
		WMessageBox messageBox = new WMessageBox(WMessageBox.SUCCESS);
		Assert.assertTrue("Incorrect renderer supplied",
				getWebXmlRenderer(messageBox) instanceof WMessageBoxRenderer);
	}

	@Test
	public void testDoPaintWhenEmpty() throws IOException, SAXException, XpathException {
		WMessageBox messageBox = new WMessageBox(WMessageBox.SUCCESS);
		assertSchemaMatch(messageBox);
		assertXpathNotExists("//ui:messagebox", messageBox);
	}

	@Test
	public void testDoPaint() throws IOException, SAXException, XpathException {
		String message1 = "WMessageBox_Test.testDoPaint.message1";
		String message2 = "WMessageBox_Test.testDoPaint.message2";
		String message3 = "WMessageBox_Test.testDoPaint.message3";

		WMessageBox messageBox = new WMessageBox(WMessageBox.SUCCESS);
		messageBox.addMessage(message1);
		messageBox.addMessage(message2);
		messageBox.addMessage(message3);

		assertSchemaMatch(messageBox);
		assertXpathEvaluatesTo("success", "//ui:messagebox/@type", messageBox);
		assertXpathEvaluatesTo(message1, "normalize-space(//ui:messagebox/ui:message[position()=1])",
				messageBox);
		assertXpathEvaluatesTo(message2, "normalize-space(//ui:messagebox/ui:message[position()=2])",
				messageBox);
		assertXpathEvaluatesTo(message3, "normalize-space(//ui:messagebox/ui:message[position()=3])",
				messageBox);

		messageBox.setType(WMessageBox.INFO);
		assertSchemaMatch(messageBox);
		assertXpathEvaluatesTo("info", "//ui:messagebox/@type", messageBox);

		messageBox.setType(WMessageBox.WARN);
		assertSchemaMatch(messageBox);
		assertXpathEvaluatesTo("warn", "//ui:messagebox/@type", messageBox);

		messageBox.setType(WMessageBox.ERROR);
		assertSchemaMatch(messageBox);
		assertXpathEvaluatesTo("error", "//ui:messagebox/@type", messageBox);

		String title = "WMEssageBoxTitle";
		messageBox.setTitleText(title);
		assertSchemaMatch(messageBox);
		assertXpathEvaluatesTo(title, "//ui:messagebox/@title", messageBox);
	}

	@Test
	public void testXssEscaping() throws IOException, SAXException, XpathException {
		WMessageBox messageBox = new WMessageBox(WMessageBox.INFO);

		messageBox.addMessage(getInvalidCharSequence());
		assertSafeContent(messageBox);

		messageBox.addMessage(getMaliciousContent());
		assertSafeContent(messageBox);
	}

	@Test
	public void testEncodeText() throws IOException, SAXException, XpathException {

		String text = "T1<b>T2</b>T3";
		String encoded = WebUtilities.encode(text);

		WMessageBox messageBox = new WMessageBox(WMessageBox.INFO);
		messageBox.addMessage(text);

		// Encoded (default)
		assertSchemaMatch(messageBox);
		String xml = toXHtml(messageBox);
		Assert.assertTrue("XML should have encoded message", xml.contains(encoded));

		// Not encoded
		messageBox.reset();
		messageBox.addMessage(false, text);
		assertSchemaMatch(messageBox);
		xml = toXHtml(messageBox);
		Assert.assertTrue("XML should have not encoded message", xml.contains(text));
	}

}
