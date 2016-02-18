package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.WStyledText;
import java.io.IOException;
import junit.framework.Assert;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * JUnit test case for {@link WStyledTextRenderer}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WStyledTextRenderer_Test extends AbstractWebXmlRendererTestCase {

	@Test
	public void testRendererCorrectlyConfigured() {
		WStyledText component = new WStyledText();
		Assert.assertTrue("Incorrect renderer supplied",
				getWebXmlRenderer(component) instanceof WStyledTextRenderer);
	}

	@Test
	public void testPaint() throws IOException, SAXException, XpathException {
		String text = "WStyledText_Test.testRenderedFormat.text";

		WStyledText styledText = new WStyledText(text);
		assertSchemaMatch(styledText);
		assertXpathEvaluatesTo(text, "//ui:text[@type='plain']", styledText);

		styledText = new WStyledText(text, WStyledText.Type.PLAIN);
		assertSchemaMatch(styledText);
		assertXpathEvaluatesTo(text, "//ui:text[@type='plain']", styledText);

		styledText = new WStyledText(text, WStyledText.Type.EMPHASISED);
		assertSchemaMatch(styledText);
		assertXpathEvaluatesTo(text, "//ui:text[@type='emphasised']", styledText);

		styledText = new WStyledText(text, WStyledText.Type.HIGH_PRIORITY);
		assertSchemaMatch(styledText);
		assertXpathEvaluatesTo(text, "//ui:text[@type='highPriority']", styledText);

		styledText = new WStyledText(text, WStyledText.Type.LOW_PRIORITY);
		assertSchemaMatch(styledText);
		assertXpathEvaluatesTo(text, "//ui:text[@type='lowPriority']", styledText);

		styledText = new WStyledText(text, WStyledText.Type.MEDIUM_PRIORITY);
		assertSchemaMatch(styledText);
		assertXpathEvaluatesTo(text, "//ui:text[@type='mediumPriority']", styledText);

		styledText = new WStyledText(text, WStyledText.Type.ACTIVE_INDICATOR);
		assertSchemaMatch(styledText);
		assertXpathEvaluatesTo(text, "//ui:text[@type='activeIndicator']", styledText);

		styledText = new WStyledText(text, WStyledText.Type.MATCH_INDICATOR);
		assertSchemaMatch(styledText);
		assertXpathEvaluatesTo(text, "//ui:text[@type='matchIndicator']", styledText);

		styledText = new WStyledText(text, WStyledText.Type.INSERT);
		assertSchemaMatch(styledText);
		assertXpathEvaluatesTo(text, "//ui:text[@type='insert']", styledText);

		styledText = new WStyledText(text, WStyledText.Type.DELETE);
		assertSchemaMatch(styledText);
		assertXpathEvaluatesTo(text, "//ui:text[@type='delete']", styledText);

		styledText = new WStyledText(text, WStyledText.Type.MANDATORY_INDICATOR);
		assertSchemaMatch(styledText);
		assertXpathEvaluatesTo(text, "//ui:text[@type='mandatoryIndicator']", styledText);

		// Check null / empty strings
		styledText = new WStyledText(null, WStyledText.Type.MATCH_INDICATOR);
		assertXpathNotExists("//ui:text", styledText);

		styledText = new WStyledText("", WStyledText.Type.MATCH_INDICATOR);
		assertXpathNotExists("//ui:text", styledText);
	}

	@Test
	public void testParagraphText() throws IOException, SAXException, XpathException {
		WStyledText styledText = new WStyledText("abc def");
		styledText.setWhitespaceMode(WStyledText.WhitespaceMode.PARAGRAPHS);
		assertSchemaMatch(styledText);
		assertXpathEvaluatesTo("abc def", "//ui:text[@type='plain']", styledText);
		assertXpathEvaluatesTo("paragraphs", "//ui:text/@space", styledText);

		styledText.setText("abc\ndef");
		assertSchemaMatch(styledText);
		assertInnerTextEquals("abc<ui:nl/>def", styledText);

		styledText.setText("abc\rdef");
		assertSchemaMatch(styledText);
		assertInnerTextEquals("abc<ui:nl/>def", styledText);

		styledText.setText("abc\r\ndef");
		assertSchemaMatch(styledText);
		assertInnerTextEquals("abc<ui:nl/>def", styledText);

		styledText.setText("abc\n\rdef");
		assertSchemaMatch(styledText);
		assertInnerTextEquals("abc<ui:nl/>def", styledText);

		styledText.setText("abc\n\ndef");
		assertSchemaMatch(styledText);
		assertInnerTextEquals("abc<ui:nl/>def", styledText);

		styledText.setText("abc\r\rdef");
		assertSchemaMatch(styledText);
		assertInnerTextEquals("abc<ui:nl/>def", styledText);

		styledText.setText("abc\r\n\r\ndef");
		assertSchemaMatch(styledText);
		assertInnerTextEquals("abc<ui:nl/>def", styledText);

		styledText.setText("abc\n\r\r\ndef");
		assertSchemaMatch(styledText);
		assertInnerTextEquals("abc<ui:nl/>def", styledText);

		styledText.setText("\nabc\ndef\rghi\n");
		assertSchemaMatch(styledText);
		assertInnerTextEquals("abc<ui:nl/>def<ui:nl/>ghi", styledText);
	}

	@Test
	public void testXssEscaping() throws IOException, SAXException, XpathException {
		WStyledText styledText = new WStyledText(getMaliciousContent());

		assertSafeContent(styledText);

		styledText.setToolTip(getMaliciousAttribute("ui:textfield"));
		assertSafeContent(styledText);

		styledText.setAccessibleText(getMaliciousAttribute("ui:textfield"));
		assertSafeContent(styledText);

		styledText.setWhitespaceMode(WStyledText.WhitespaceMode.PRESERVE);
		assertSafeContent(styledText);

		styledText.setWhitespaceMode(WStyledText.WhitespaceMode.PARAGRAPHS);
		assertSafeContent(styledText);
	}

	/**
	 * This function tests the rendered content of the ui:text. This is necessary, as there doesn't seem to be a nice
	 * way to obtain the nested content using XPath.
	 *
	 * @param expected the expected text
	 * @param text the text component
	 */
	private void assertInnerTextEquals(final String expected, final WStyledText text) {
		String xml = toXHtml(text).trim();
		String innerText = xml.substring(xml.indexOf('>') + 1, xml.lastIndexOf('<'));
		Assert.assertEquals("Inccorect inner text", expected, innerText);
	}
}
