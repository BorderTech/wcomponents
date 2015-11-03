package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.WConfirmationButton;
import java.io.IOException;
import junit.framework.Assert;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * Junit test case for {@link WButtonRenderer}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WConfirmationButtonRenderer_Test extends AbstractWebXmlRendererTestCase {

	@Test
	public void testRendererCorrectlyConfigured() {
		WConfirmationButton component = new WConfirmationButton("dummy");
		Assert.assertTrue("Incorrect renderer supplied",
				getWebXmlRenderer(component) instanceof WButtonRenderer);
	}

	@Test
	public void testDoPaint() throws IOException, SAXException, XpathException {
		WConfirmationButton button = new WConfirmationButton("dummy");
		assertSchemaMatch(button);
		assertXpathExists("//ui:button", button);

		String message = "WConfirmationButton_Test.testRenderedFormat.message";
		button.setMessage(message);
		assertXpathEvaluatesTo(message, "//ui:button/@msg", button);
	}

	@Test
	public void testXssEscaping() throws IOException, SAXException, XpathException {
		WConfirmationButton button = new WConfirmationButton(getMaliciousContent());

		assertSafeContent(button);

		button.setToolTip(getMaliciousAttribute("ui:button"));
		assertSafeContent(button);

		button.setAccessibleText(getMaliciousAttribute("ui:button"));
		assertSafeContent(button);

		button.setMessage(getMaliciousAttribute("ui:button"));
		assertSafeContent(button);
	}
}
