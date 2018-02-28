package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.WCancelButton;
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
public class WCancelButtonRenderer_Test extends AbstractWebXmlRendererTestCase {

	@Test
	public void testRendererCorrectlyConfigured() {
		WCancelButton component = new WCancelButton("dummy");
		Assert.assertTrue("Incorrect renderer supplied",
				getWebXmlRenderer(component) instanceof WButtonRenderer);
	}

	@Test
	public void testDoPaint() throws IOException, SAXException, XpathException {
		WCancelButton button = new WCancelButton("dummy");
		assertSchemaMatch(button);
		assertXpathExists("//html:button", button);

		setActiveContext(createUIContext());
		button.setUnsavedChanges(true);
		assertXpathExists("//html:button[contains(@class, 'wc_unsaved')]", button);
		assertXpathEvaluatesTo("formnovalidate", "//html:button/@formnovalidate", button);
	}

	@Test
	public void testXssEscaping() throws IOException, SAXException, XpathException {
		WCancelButton button = new WCancelButton(getMaliciousContent());

		setActiveContext(createUIContext());
		assertSafeContent(button);

		button.setToolTip(getMaliciousAttribute("html:button"));
		assertSafeContent(button);

		button.setAccessibleText(getMaliciousAttribute("html:button"));
		assertSafeContent(button);

		button.setImageUrl(getMaliciousAttribute());
		assertSafeContent(button);
	}
}
