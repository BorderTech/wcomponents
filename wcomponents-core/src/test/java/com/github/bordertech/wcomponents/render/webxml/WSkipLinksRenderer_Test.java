package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.WSkipLinks;
import java.io.IOException;
import junit.framework.Assert;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * Junit test case for {@link WSkipLinksRenderer}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WSkipLinksRenderer_Test extends AbstractWebXmlRendererTestCase {

	@Test
	public void testRendererCorrectlyConfigured() {
		WSkipLinks skipLinks = new WSkipLinks();
		Assert.assertTrue("Incorrect renderer supplied",
				getWebXmlRenderer(skipLinks) instanceof WSkipLinksRenderer);
	}

	@Test
	public void testDoPaint() throws IOException, SAXException, XpathException {
		WSkipLinks skipLinks = new WSkipLinks();
		assertSchemaMatch(skipLinks);
		assertXpathExists("//ui:skiplinks", skipLinks);
	}
}
