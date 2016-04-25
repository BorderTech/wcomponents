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
		assertXpathExists("//ui:button", button);

		setActiveContext(createUIContext());
		button.setUnsavedChanges(true);
		assertXpathEvaluatesTo("true", "//ui:button/@unsavedChanges", button);
		assertXpathEvaluatesTo("true", "//ui:button/@cancel", button);
	}

	@Test
	public void testXssEscaping() throws IOException, SAXException, XpathException {
		WCancelButton button = new WCancelButton(getMaliciousContent());

		setActiveContext(createUIContext());
		assertSafeContent(button);

		button.setToolTip(getMaliciousAttribute("ui:button"));
		assertSafeContent(button);

		button.setAccessibleText(getMaliciousAttribute("ui:button"));
		assertSafeContent(button);
	}
}
