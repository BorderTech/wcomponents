package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.Margin;
import com.github.bordertech.wcomponents.WDecoratedLabel;
import com.github.bordertech.wcomponents.WHeading;
import com.github.bordertech.wcomponents.WText;
import java.io.IOException;
import junit.framework.Assert;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * Junit test case for {@link WHeadingRenderer}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WHeadingRenderer_Test extends AbstractWebXmlRendererTestCase {

	@Test
	public void testRendererCorrectlyConfigured() {
		WHeading component = new WHeading(WHeading.TITLE, "");
		Assert.assertTrue("Incorrect renderer supplied",
				getWebXmlRenderer(component) instanceof WHeadingRenderer);
	}

	@Test
	public void testPaint() throws IOException, SAXException, XpathException {
		final String text = "WHeading_Test.testPaint.heading";

		WHeading heading = new WHeading(WHeading.TITLE, text);
		assertSchemaMatch(heading);
		assertXpathEvaluatesTo(text, "//ui:heading[@level=1]", heading);

		heading = new WHeading(WHeading.MAJOR, text);
		assertSchemaMatch(heading);
		assertXpathEvaluatesTo(text, "//ui:heading[@level=2]", heading);

		heading = new WHeading(WHeading.SECTION, text);
		assertSchemaMatch(heading);
		assertXpathEvaluatesTo(text, "//ui:heading[@level=3]", heading);

		heading = new WHeading(WHeading.MINOR, text);
		assertSchemaMatch(heading);
		assertXpathEvaluatesTo(text, "//ui:heading[@level=4]", heading);

		heading = new WHeading(WHeading.SUB_HEADING, text);
		assertSchemaMatch(heading);
		assertXpathEvaluatesTo(text, "//ui:heading[@level=5]", heading);

		heading = new WHeading(WHeading.SUB_SUB_HEADING, text);
		assertSchemaMatch(heading);
		assertXpathEvaluatesTo(text, "//ui:heading[@level=6]", heading);

		try {
			heading = new WHeading(123456, text);
			Assert.fail("Should have thrown a SystemException");
		} catch (IllegalArgumentException expected) {
			Assert.assertNotNull("Thrown exception should contain a message", expected.getMessage());
		}
	}

	@Test
	public void testPaintWithDecoratedLabel() throws IOException, SAXException, XpathException {
		final String text1 = "WHeading_Test.testPaintWithDecoratedLabel.text1";
		final String text2 = "WHeading_Test.testPaintWithDecoratedLabel.text2";

		WHeading heading = new WHeading(WHeading.TITLE, new WDecoratedLabel(new WText(text1)));
		assertSchemaMatch(heading);

		assertXpathEvaluatesTo(text1, "//ui:heading[@level=1]/ui:decoratedlabel/ui:labelbody/text()",
				heading);

		// Test WHeading's WText implementation
		heading.setText(text2);
		assertXpathEvaluatesTo(text2 + text1,
				"//ui:heading[@level=1]/ui:decoratedlabel/ui:labelbody/text()", heading);
	}

	@Test
	public void testRenderedWithMargins() throws IOException, SAXException, XpathException {
		WHeading heading = new WHeading(WHeading.TITLE, "test");
		assertXpathNotExists("//ui:heading/ui:margin", heading);

		Margin margin = new Margin(0);
		heading.setMargin(margin);
		assertXpathNotExists("//ui:heading/ui:margin", heading);

		margin = new Margin(1);
		heading.setMargin(margin);
		assertSchemaMatch(heading);
		assertXpathEvaluatesTo("1", "//ui:heading/ui:margin/@all", heading);
		assertXpathEvaluatesTo("", "//ui:heading/ui:margin/@north", heading);
		assertXpathEvaluatesTo("", "//ui:heading/ui:margin/@east", heading);
		assertXpathEvaluatesTo("", "//ui:heading/ui:margin/@south", heading);
		assertXpathEvaluatesTo("", "//ui:heading/ui:margin/@west", heading);

		margin = new Margin(1, 2, 3, 4);
		heading.setMargin(margin);
		assertSchemaMatch(heading);
		assertXpathEvaluatesTo("", "//ui:heading/ui:margin/@all", heading);
		assertXpathEvaluatesTo("1", "//ui:heading/ui:margin/@north", heading);
		assertXpathEvaluatesTo("2", "//ui:heading/ui:margin/@east", heading);
		assertXpathEvaluatesTo("3", "//ui:heading/ui:margin/@south", heading);
		assertXpathEvaluatesTo("4", "//ui:heading/ui:margin/@west", heading);
	}

	@Test
	public void testXssEscaping() throws IOException, SAXException, XpathException {
		WHeading heading = new WHeading(WHeading.TITLE, new WDecoratedLabel(new WText("dummy")));

		assertSafeContent(heading);

		heading.setAccessibleText(getMaliciousAttribute("ui:heading"));
		assertSafeContent(heading);
	}
}
