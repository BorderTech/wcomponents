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
		assertXpathEvaluatesTo(fieldSet.getId(), "//ui:fieldset/@id", fieldSet);
		assertXpathNotExists("//ui:fieldset/@frame", fieldSet);

		// Check Input
		assertXpathEvaluatesTo(text.getText(), "//ui:fieldset/ui:content/ui:textfield", fieldSet);

		fieldSet.setFrameType(FrameType.NO_BORDER);
		assertXpathEvaluatesTo("noborder", "//ui:fieldset/@frame", fieldSet);

		fieldSet.setFrameType(FrameType.NO_TEXT);
		assertXpathEvaluatesTo("notext", "//ui:fieldset/@frame", fieldSet);

		fieldSet.setFrameType(FrameType.NONE);
		assertXpathEvaluatesTo("none", "//ui:fieldset/@frame", fieldSet);
	}

	@Test
	public void testRenderedWithMargins() throws IOException, SAXException, XpathException {
		WFieldSet fieldSet = new WFieldSet("");
		assertXpathNotExists("//ui:fieldset/ui:margin", fieldSet);

		Margin margin = new Margin(0);
		fieldSet.setMargin(margin);
		assertXpathNotExists("//ui:fieldset/ui:margin", fieldSet);

		margin = new Margin(1);
		fieldSet.setMargin(margin);
		assertSchemaMatch(fieldSet);
		assertXpathEvaluatesTo("1", "//ui:fieldset/ui:margin/@all", fieldSet);
		assertXpathEvaluatesTo("", "//ui:fieldset/ui:margin/@north", fieldSet);
		assertXpathEvaluatesTo("", "//ui:fieldset/ui:margin/@east", fieldSet);
		assertXpathEvaluatesTo("", "//ui:fieldset/ui:margin/@south", fieldSet);
		assertXpathEvaluatesTo("", "//ui:fieldset/ui:margin/@west", fieldSet);

		margin = new Margin(1, 2, 3, 4);
		fieldSet.setMargin(margin);
		assertSchemaMatch(fieldSet);
		assertXpathEvaluatesTo("", "//ui:fieldset/ui:margin/@all", fieldSet);
		assertXpathEvaluatesTo("1", "//ui:fieldset/ui:margin/@north", fieldSet);
		assertXpathEvaluatesTo("2", "//ui:fieldset/ui:margin/@east", fieldSet);
		assertXpathEvaluatesTo("3", "//ui:fieldset/ui:margin/@south", fieldSet);
		assertXpathEvaluatesTo("4", "//ui:fieldset/ui:margin/@west", fieldSet);
	}

}
