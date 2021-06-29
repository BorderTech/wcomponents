package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.HeadingLevel;
import com.github.bordertech.wcomponents.Margin;
import com.github.bordertech.wcomponents.Size;
import com.github.bordertech.wcomponents.WDecoratedLabel;
import com.github.bordertech.wcomponents.WHeading;
import com.github.bordertech.wcomponents.WText;
import java.io.IOException;
import org.junit.Assert;
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
		WHeading component = new WHeading(HeadingLevel.H1, "");
		Assert.assertTrue("Incorrect renderer supplied",
				getWebXmlRenderer(component) instanceof WHeadingRenderer);
	}

	@Test
	public void testPaint() throws IOException, SAXException {
		final String text = "WHeading_Test.testPaint.heading";

		WHeading heading = new WHeading(HeadingLevel.H1, text);
		assertSchemaMatch(heading);
		assertXpathEvaluatesTo(text, "//ui:heading[@level=1]", heading);

		heading = new WHeading(HeadingLevel.H2, text);
		assertSchemaMatch(heading);
		assertXpathEvaluatesTo(text, "//ui:heading[@level=2]", heading);

		heading = new WHeading(HeadingLevel.H3, text);
		assertSchemaMatch(heading);
		assertXpathEvaluatesTo(text, "//ui:heading[@level=3]", heading);

		heading = new WHeading(HeadingLevel.H4, text);
		assertSchemaMatch(heading);
		assertXpathEvaluatesTo(text, "//ui:heading[@level=4]", heading);

		heading = new WHeading(HeadingLevel.H5, text);
		assertSchemaMatch(heading);
		assertXpathEvaluatesTo(text, "//ui:heading[@level=5]", heading);

		heading = new WHeading(HeadingLevel.H6, text);
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
	public void testPaintWithDecoratedLabel() throws IOException, SAXException {
		final String text1 = "WHeading_Test.testPaintWithDecoratedLabel.text1";
		final String text2 = "WHeading_Test.testPaintWithDecoratedLabel.text2";

		WHeading heading = new WHeading(HeadingLevel.H1, new WDecoratedLabel(new WText(text1)));
		assertSchemaMatch(heading);

		assertXpathEvaluatesTo(text1, "//ui:heading[@level=1]/ui:decoratedlabel/ui:labelbody/text()",
				heading);

		// Test WHeading's WText implementation
		heading.setText(text2);
		assertXpathEvaluatesTo(text2 + text1,
				"//ui:heading[@level=1]/ui:decoratedlabel/ui:labelbody/text()", heading);
	}

	@Test
	public void testRenderedWithMargins() throws IOException, SAXException {
		WHeading heading = new WHeading(HeadingLevel.H1, "test");
		assertXpathNotExists("//ui:heading/ui:margin", heading);

		Margin margin = new Margin(null);
		heading.setMargin(margin);
		assertXpathNotExists("//ui:heading/ui:margin", heading);

		margin = new Margin(Size.SMALL);
		heading.setMargin(margin);
		assertSchemaMatch(heading);
		assertXpathEvaluatesTo("sm", "//ui:heading/ui:margin/@all", heading);
		assertXpathEvaluatesTo("", "//ui:heading/ui:margin/@north", heading);
		assertXpathEvaluatesTo("", "//ui:heading/ui:margin/@east", heading);
		assertXpathEvaluatesTo("", "//ui:heading/ui:margin/@south", heading);
		assertXpathEvaluatesTo("", "//ui:heading/ui:margin/@west", heading);

		margin = new Margin(Size.SMALL, Size.MEDIUM, Size.LARGE, Size.XL);
		heading.setMargin(margin);
		assertSchemaMatch(heading);
		assertXpathEvaluatesTo("", "//ui:heading/ui:margin/@all", heading);
		assertXpathEvaluatesTo("sm", "//ui:heading/ui:margin/@north", heading);
		assertXpathEvaluatesTo("med", "//ui:heading/ui:margin/@east", heading);
		assertXpathEvaluatesTo("lg", "//ui:heading/ui:margin/@south", heading);
		assertXpathEvaluatesTo("xl", "//ui:heading/ui:margin/@west", heading);
	}

	@Test
	public void testXssEscaping() throws IOException, SAXException {
		WHeading heading = new WHeading(HeadingLevel.H1, new WDecoratedLabel(new WText("dummy")));

		assertSafeContent(heading);

		heading.setAccessibleText(getMaliciousAttribute("ui:heading"));
		assertSafeContent(heading);
	}
}
