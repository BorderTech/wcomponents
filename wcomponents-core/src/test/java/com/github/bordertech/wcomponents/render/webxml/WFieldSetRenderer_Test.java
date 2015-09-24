package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.Margin;
import com.github.bordertech.wcomponents.WFieldSet;
import com.github.bordertech.wcomponents.WFieldSet.FrameType;
import com.github.bordertech.wcomponents.WTextField;
import java.io.IOException;
import junit.framework.Assert;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * Junit test case for {@link WFieldSetRenderer}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WFieldSetRenderer_Test extends AbstractWebXmlRendererTestCase {

	/**
	 * Test the Layout is correctly configured.
	 */
	@Test
	public void testRendererCorrectlyConfigured() {
		WFieldSet fieldSet = new WFieldSet("dummy");
		Assert.assertTrue("Incorrect renderer supplied",
				getWebXmlRenderer(fieldSet) instanceof WFieldSetRenderer);
	}

	@Test
	public void testDoPaint() throws IOException, SAXException, XpathException {
		final String title = "WFieldSetRenderer_Test.testDoPaint.title";

		WTextField text = new WTextField();
		WFieldSet fieldSet = new WFieldSet(title);
		fieldSet.add(text);

		text.setText("text1");

		assertSchemaMatch(fieldSet);

		// Check Attributes
		assertXpathEvaluatesTo(fieldSet.getId(), "//ui:fieldSet/@id", fieldSet);
		assertXpathNotExists("//ui:fieldSet/@frame", fieldSet);

		// Check Input
		assertXpathEvaluatesTo(text.getText(), "//ui:fieldSet/ui:content/ui:textField", fieldSet);

		fieldSet.setFrameType(FrameType.NO_BORDER);
		assertXpathEvaluatesTo("noborder", "//ui:fieldSet/@frame", fieldSet);

		fieldSet.setFrameType(FrameType.NO_TEXT);
		assertXpathEvaluatesTo("notext", "//ui:fieldSet/@frame", fieldSet);

		fieldSet.setFrameType(FrameType.NONE);
		assertXpathEvaluatesTo("none", "//ui:fieldSet/@frame", fieldSet);
	}

	@Test
	public void testRenderedWithMargins() throws IOException, SAXException, XpathException {
		WFieldSet fieldSet = new WFieldSet("");
		assertXpathNotExists("//ui:fieldSet/ui:margin", fieldSet);

		Margin margin = new Margin(0);
		fieldSet.setMargin(margin);
		assertXpathNotExists("//ui:fieldSet/ui:margin", fieldSet);

		margin = new Margin(1);
		fieldSet.setMargin(margin);
		assertSchemaMatch(fieldSet);
		assertXpathEvaluatesTo("1", "//ui:fieldSet/ui:margin/@all", fieldSet);
		assertXpathEvaluatesTo("", "//ui:fieldSet/ui:margin/@north", fieldSet);
		assertXpathEvaluatesTo("", "//ui:fieldSet/ui:margin/@east", fieldSet);
		assertXpathEvaluatesTo("", "//ui:fieldSet/ui:margin/@south", fieldSet);
		assertXpathEvaluatesTo("", "//ui:fieldSet/ui:margin/@west", fieldSet);

		margin = new Margin(1, 2, 3, 4);
		fieldSet.setMargin(margin);
		assertSchemaMatch(fieldSet);
		assertXpathEvaluatesTo("", "//ui:fieldSet/ui:margin/@all", fieldSet);
		assertXpathEvaluatesTo("1", "//ui:fieldSet/ui:margin/@north", fieldSet);
		assertXpathEvaluatesTo("2", "//ui:fieldSet/ui:margin/@east", fieldSet);
		assertXpathEvaluatesTo("3", "//ui:fieldSet/ui:margin/@south", fieldSet);
		assertXpathEvaluatesTo("4", "//ui:fieldSet/ui:margin/@west", fieldSet);
	}

}
