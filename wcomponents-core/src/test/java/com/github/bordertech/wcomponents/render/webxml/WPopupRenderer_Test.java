package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.WPopup;
import java.io.IOException;
import junit.framework.Assert;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * Junit test case for {@link WPopupRenderer}.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class WPopupRenderer_Test extends AbstractWebXmlRendererTestCase {

	@Test
	public void testRendererCorrectlyConfigured() {
		WPopup popup = new WPopup();
		Assert.assertTrue("Incorrect renderer supplied",
				getWebXmlRenderer(popup) instanceof WPopupRenderer);
	}

	@Test
	public void testDoPaint() throws IOException, SAXException, XpathException {
		final String testUrl = "www.testurl.invalid";
		final String testUrl2 = "www.testurl2.invalid";
		final String testWindow = "window";

		final int width = 100;
		final int height = 90;

		// Popup with only URL and default settings
		WPopup popup = new WPopup(testUrl) {
			@Override
			public boolean isVisible() {
				return true;
			}
		};

		assertSchemaMatch(popup);
		assertXpathEvaluatesTo(testUrl, "//ui:popup/@url", popup);
		assertXpathEvaluatesTo("", "//ui:popup/@width", popup);
		assertXpathEvaluatesTo("", "//ui:popup/@height", popup);
		assertXpathEvaluatesTo("true", "//ui:popup/@resizable", popup);
		assertXpathEvaluatesTo("", "//ui:popup/@showScrollbars", popup);
		assertXpathEvaluatesTo("", "//ui:popup/@targetWindow", popup);

		// All options
		popup.setUrl(testUrl2);
		popup.setWidth(width);
		popup.setHeight(height);
		popup.setResizable(false);
		popup.setScrollable(true);
		popup.setTargetWindow(testWindow);

		assertSchemaMatch(popup);
		assertXpathEvaluatesTo(testUrl2, "//ui:popup/@url", popup);
		assertXpathEvaluatesTo(Integer.toString(width), "//ui:popup/@width", popup);
		assertXpathEvaluatesTo(Integer.toString(height), "//ui:popup/@height", popup);
		assertXpathEvaluatesTo("", "//ui:popup/@resizable", popup);
		assertXpathEvaluatesTo("true", "//ui:popup/@showScrollbars", popup);
		assertXpathEvaluatesTo(testWindow, "//ui:popup/@targetWindow", popup);
	}

	@Test
	public void testXssEscaping() throws IOException, SAXException, XpathException {
		// Popup with only URL and default settings
		WPopup popup = new WPopup("www.invalid") {
			@Override
			public boolean isVisible() {
				return true;
			}
		};

		popup.setUrl("http://www.invalid/cgi?a=" + getMaliciousAttribute());
		assertSafeContent(popup);
	}
}
