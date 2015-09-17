package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.MockContentAccess;
import com.github.bordertech.wcomponents.WContent;
import com.github.bordertech.wcomponents.util.mock.MockRequest;
import java.io.IOException;
import junit.framework.Assert;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * Junit test case for {@link WContentRenderer}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WContentRenderer_Test extends AbstractWebXmlRendererTestCase {

	@Test
	public void testRendererCorrectlyConfigured() {
		WContent component = new WContent();
		Assert.assertTrue("Incorrect renderer supplied",
				getWebXmlRenderer(component) instanceof WContentRenderer);
	}

	@Test
	public void testDoPaint() throws IOException, SAXException, XpathException {
		MockRequest request = new MockRequest();

		MockContentAccess content = new MockContentAccess();
		content.setBytes(new byte[]{1, 2, 3});

		// Shouldn't display have any content by default
		WContent wContent = new WContent();
		setActiveContext(createUIContext());
		wContent.setContentAccess(content);
		wContent.setCacheKey("abcd"); // this test needs the URLs to be consistent
		wContent.handleRequest(request);

		String xml = render(wContent);
		Assert.assertEquals("Should not have painted any content", "", xml);

		// Should produce mark-up when displayed
		wContent.handleRequest(request);
		wContent.display();

		assertSchemaMatch(wContent);
		// Display again (as reset after paint)
		wContent.display();
		assertXpathEvaluatesTo(wContent.getUrl(), "//ui:popup/@url", wContent);
		// Display again (as reset after paint)
		wContent.display();
		assertXpathNotExists("ui:redirect", wContent);

		// Test all options
		String width = "111";
		String height = "222";
		wContent.setWidth(width);
		wContent.setHeight(height);
		wContent.setResizable(true);
		wContent.setDisplayMode(WContent.DisplayMode.PROMPT_TO_SAVE);

		assertSchemaMatch(wContent);

		// Display again (as reset after paint)
		wContent.display();
		assertXpathNotExists("//ui:popup", wContent);

		// Display again (as reset after paint)
		wContent.display();
		assertXpathEvaluatesTo(wContent.getUrl(), "//ui:redirect/@url", wContent);

		// Test null content
		wContent.setContentAccess(null);
		xml = render(wContent);
		Assert.assertEquals("Should not have painted any content", "", xml);
	}
}
