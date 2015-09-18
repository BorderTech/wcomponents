package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.ComponentModel;
import com.github.bordertech.wcomponents.MockImage;
import com.github.bordertech.wcomponents.WImage;
import com.github.bordertech.wcomponents.WebUtilities;
import java.awt.Dimension;
import java.io.IOException;
import junit.framework.Assert;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * Junit test case for {@link WImageRenderer}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WImageRenderer_Test extends AbstractWebXmlRendererTestCase {

	@Test
	public void testRendererCorrectlyConfigured() {

		WImage image = new WImage();
		Assert.assertTrue("Incorrect renderer supplied",
				getWebXmlRenderer(image) instanceof WImageRenderer);
	}

	@Test
	public void testDoPaint() throws IOException, SAXException, XpathException {

		MockImage content = new MockImage();
		WImage image = new WImage();
		assertSchemaMatch(image);

		image = new WImage();
		setActiveContext(createUIContext());
		image.setImage(content);

		assertSchemaMatch(image);
		assertXpathEvaluatesTo(image.getId(), "//ui:image/@id", image);
		assertSrcMatch(image);
		assertXpathEvaluatesTo("", "//ui:image/@alt", image);
		assertXpathNotExists("//ui:image/@hidden", image);
		assertXpathNotExists("//ui:image/@width", image);
		assertXpathNotExists("//ui:image/@height", image);

		content.setDescription("WImage_Test.testRenderedFormat.description");
		content.setSize(new Dimension(-123, -456));
		setFlag(image, ComponentModel.HIDE_FLAG, true);
		assertXpathEvaluatesTo(content.getDescription(), "//ui:image/@alt", image);
		assertXpathEvaluatesTo("true", "//ui:image/@hidden", image);
		assertXpathNotExists("//ui:image/@width", image);
		assertXpathNotExists("//ui:image/@height", image);
		assertSrcMatch(image);

		content.setSize(new Dimension(123, 456));
		assertSchemaMatch(image);
		assertXpathEvaluatesTo("123", "//ui:image/@width", image);
		assertXpathEvaluatesTo("456", "//ui:image/@height", image);

		content.setSize(new Dimension(0, 0));
		assertSchemaMatch(image);
		assertXpathEvaluatesTo("0", "//ui:image/@width", image);
		assertXpathEvaluatesTo("0", "//ui:image/@height", image);
	}

	/**
	 * We need to match urls which include a random no-cache value. e.g.
	 * <pre>unknown?no-cache=1994285646-4&target_id=L&s=0</pre>
	 *
	 * @param image the image to test.
	 *
	 * @throws IOException an IO exception
	 * @throws SAXException a SAX exception
	 * @throws XpathException an xpath exception
	 */
	private void assertSrcMatch(final WImage image) throws IOException, SAXException, XpathException {

		final String noCacheRegexp = "no-cache=[^&]*";

		String src = WebUtilities.decode(image.getTargetUrl());
		String expectedSrc = src.replaceFirst(noCacheRegexp, "");
		String actualSrc = evaluateXPath(image, "//ui:image/@src").replaceFirst(noCacheRegexp, "");
		Assert.assertEquals("Incorrect source url", expectedSrc, actualSrc);
	}

	@Test
	public void testXssEscaping() throws IOException, SAXException, XpathException {

		WImage image = new WImage();
		MockImage content = new MockImage();
		content.setDescription(getMaliciousAttribute("ui:image"));

		setActiveContext(createUIContext());
		image.setImage(content);
		assertSafeContent(image);
	}
}
