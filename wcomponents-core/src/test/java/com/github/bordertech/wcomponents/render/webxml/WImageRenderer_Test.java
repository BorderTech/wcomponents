package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.ComponentModel;
import com.github.bordertech.wcomponents.MockImage;
import com.github.bordertech.wcomponents.WImage;
import com.github.bordertech.wcomponents.WebUtilities;
import java.awt.Dimension;
import java.io.IOException;
import org.junit.Assert;
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

		image = new WImage();
		setActiveContext(createUIContext());
		image.setImage(content);
		assertXpathEvaluatesTo(image.getId(), "//html:img/@id", image);
		assertSrcMatch(image);
		assertXpathEvaluatesTo("", "//html:img/@alt", image);
		assertXpathNotExists("//html:img/@title", image);
		assertXpathNotExists("//html:img/@hidden", image);
		assertXpathNotExists("//html:img/@width", image);
		assertXpathNotExists("//html:img/@height", image);

		assertSrcMatch(image);
	}

	@Test
	public void testAlt() throws IOException, SAXException, XpathException {
		MockImage content = new MockImage();
		WImage image = new WImage();
		setActiveContext(createUIContext());
		image.setImage(content);
		assertXpathEvaluatesTo("", "//html:img/@alt", image);
		// positive test: dimensions are rational
		String expected = "alt text";
		image.setAlternativeText(expected);
		assertXpathEvaluatesTo(expected, "//html:img/@alt", image);
	}

	@Test
	public void testAltOnContent() throws IOException, SAXException, XpathException {
		MockImage content = new MockImage();
		WImage image = new WImage();
		setActiveContext(createUIContext());
		image.setImage(content);
		assertXpathEvaluatesTo("", "//html:img/@alt", image);
		// positive test: dimensions are rational
		content.setDescription("WImage_Test.testRenderedFormat.description");
		assertXpathEvaluatesTo(content.getDescription(), "//html:img/@alt", image);
	}

	@Test
	public void testDimensions() throws IOException, SAXException, XpathException {
		MockImage content = new MockImage();
		WImage image = new WImage();
		setActiveContext(createUIContext());
		image.setImage(content);
		assertXpathNotExists("//html:img/@width", image);
		assertXpathNotExists("//html:img/@height", image);
		// positive test: dimensions are rational
		image.setSize(new Dimension(123, 456));
		assertXpathEvaluatesTo("123", "//html:img/@width", image);
		assertXpathEvaluatesTo("456", "//html:img/@height", image);
	}

	@Test
	public void testBadDimensions() throws IOException, SAXException, XpathException {
		MockImage content = new MockImage();
		WImage image = new WImage();
		setActiveContext(createUIContext());
		image.setImage(content);
		assertXpathNotExists("//html:img/@width", image);
		assertXpathNotExists("//html:img/@height", image);
		// negative test: dimensions are silly
		image.setSize(new Dimension(-123, -456));
		assertXpathNotExists("//html:img/@width", image);
		assertXpathNotExists("//html:img/@height", image);
	}

	@Test
	public void testZeroDimensions() throws IOException, SAXException, XpathException {
		MockImage content = new MockImage();
		WImage image = new WImage();
		setActiveContext(createUIContext());
		image.setImage(content);
		assertXpathNotExists("//html:img/@width", image);
		assertXpathNotExists("//html:img/@height", image);
		// positive test: dimensions are rational
		image.setSize(new Dimension(0, 0));
		assertXpathEvaluatesTo("0", "//html:img/@width", image);
		assertXpathEvaluatesTo("0", "//html:img/@height", image);
	}

	@Test
	public void testDimensionsOnContent() throws IOException, SAXException, XpathException {
		MockImage content = new MockImage();
		WImage image = new WImage();
		setActiveContext(createUIContext());
		image.setImage(content);
		assertXpathNotExists("//html:img/@width", image);
		assertXpathNotExists("//html:img/@height", image);
		// positive test: dimensions are rational
		content.setSize(new Dimension(123, 456));
		assertXpathEvaluatesTo("123", "//html:img/@width", image);
		assertXpathEvaluatesTo("456", "//html:img/@height", image);
	}

	@Test
	public void testBadDimensionsOnContent() throws IOException, SAXException, XpathException {
		MockImage content = new MockImage();
		WImage image = new WImage();
		setActiveContext(createUIContext());
		image.setImage(content);
		assertXpathNotExists("//html:img/@width", image);
		assertXpathNotExists("//html:img/@height", image);
		// negative test: dimensions are silly
		content.setSize(new Dimension(-123, -456));
		assertXpathNotExists("//html:img/@width", image);
		assertXpathNotExists("//html:img/@height", image);
	}

	@Test
	public void testZeroDimensionsOnContent() throws IOException, SAXException, XpathException {
		MockImage content = new MockImage();
		WImage image = new WImage();
		setActiveContext(createUIContext());
		image.setImage(content);
		assertXpathNotExists("//html:img/@width", image);
		assertXpathNotExists("//html:img/@height", image);
		// positive test: dimensions are rational
		content.setSize(new Dimension(0, 0));
		assertXpathEvaluatesTo("0", "//html:img/@width", image);
		assertXpathEvaluatesTo("0", "//html:img/@height", image);
	}

	@Test
	public void testWithToolTip() throws IOException, SAXException, XpathException {
		MockImage content = new MockImage();
		WImage image = new WImage();
		setActiveContext(createUIContext());
		image.setImage(content);
		assertXpathNotExists("//html:img/@title", image);
		final String expected = "Tooltip";
		image.setToolTip(expected);
		assertXpathEvaluatesTo(expected, "//html:img/@title", image);
	}

	@Test
	public void testWithAltAndToolTip() throws IOException, SAXException, XpathException {
		MockImage content = new MockImage();
		WImage image = new WImage();
		setActiveContext(createUIContext());
		image.setImage(content);
		assertXpathNotExists("//html:img/@title", image);
		assertXpathEvaluatesTo("", "//html:img/@alt", image);
		final String expectedTitle = "Tooltip";
		final String expectedAlt = "alt text";
		image.setAlternativeText(expectedAlt);
		image.setToolTip(expectedTitle);
		assertXpathEvaluatesTo(expectedTitle, "//html:img/@title", image);
		assertXpathEvaluatesTo(expectedAlt, "//html:img/@alt", image);
	}

	@Test
	public void testWithAltAndToolTipSame() throws IOException, SAXException, XpathException {
		MockImage content = new MockImage();
		WImage image = new WImage();
		setActiveContext(createUIContext());
		image.setImage(content);
		assertXpathNotExists("//html:img/@title", image);
		assertXpathEvaluatesTo("", "//html:img/@alt", image);
		final String expected = "alt text";
		image.setAlternativeText(expected);
		image.setToolTip(expected);
		assertXpathNotExists("//html:img/@title", image);
		assertXpathEvaluatesTo(expected, "//html:img/@alt", image);
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
		String actualSrc = evaluateXPath(image, "//html:img/@src").replaceFirst(noCacheRegexp, "");
		Assert.assertEquals("Incorrect source url", expectedSrc, actualSrc);
	}

	@Test
	public void testXssEscaping() throws IOException, SAXException, XpathException {

		WImage image = new WImage();
		MockImage content = new MockImage();
		content.setDescription(getMaliciousAttribute("html:img"));

		setActiveContext(createUIContext());
		image.setImage(content);
		assertSafeContent(image);

		image.setImageUrl(getMaliciousAttribute());
		assertSafeContent(image);

	}

	@Test
	public void testHidden() throws IOException, SAXException, XpathException {
		MockImage content = new MockImage();
		WImage image = new WImage();
		setActiveContext(createUIContext());
		image.setImage(content);
		assertXpathNotExists("//html:img/@hidden", image);
		// setFlag(image, ComponentModel.HIDE_FLAG, true);
		image.setHidden(true);
		assertXpathEvaluatesTo("hidden", "//html:img/@hidden", image);
		image.setHidden(false);
		assertXpathNotExists("//html:img/@hidden", image);
	}
}
