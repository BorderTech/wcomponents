package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.ComponentModel;
import com.github.bordertech.wcomponents.Margin;
import com.github.bordertech.wcomponents.WFieldLayout;
import com.github.bordertech.wcomponents.WTextField;
import java.io.IOException;
import junit.framework.Assert;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * Junit test case for {@link WFieldLayoutRenderer}.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class WFieldLayoutRenderer_Test extends AbstractWebXmlRendererTestCase {

	/**
	 * Test the Layout is correctly configured.
	 */
	@Test
	public void testRendererCorrectlyConfigured() {
		WFieldLayout layout = new WFieldLayout();
		Assert.assertTrue("Incorrect renderer supplied",
				getWebXmlRenderer(layout) instanceof WFieldLayoutRenderer);
	}

	@Test
	public void testDoPaintBasic() throws IOException, SAXException, XpathException {
		WFieldLayout layout = new WFieldLayout();

		// Validate Schema
		assertSchemaMatch(layout);
		// Check Attributes
		assertXpathEvaluatesTo(layout.getId(), "//ui:fieldlayout/@id", layout);
		assertXpathEvaluatesTo("flat", "//ui:fieldlayout/@layout", layout);
		assertXpathEvaluatesTo("", "//ui:fieldlayout/@hidden", layout);
		assertXpathEvaluatesTo("", "//ui:fieldlayout/@labelWidth", layout);
		assertXpathEvaluatesTo("", "//ui:fieldlayout/@title", layout);
		assertXpathEvaluatesTo("", "//ui:fieldlayout/@ordered", layout);
		// Check No Fields
		assertXpathNotExists("//ui:fieldlayout/ui:field", layout);
	}

	@Test
	public void testDoPaintAllOptions() throws IOException, SAXException, XpathException {
		WFieldLayout layout = new WFieldLayout(WFieldLayout.LAYOUT_STACKED);
		setFlag(layout, ComponentModel.HIDE_FLAG, true);
		layout.setLabelWidth(10);
		layout.setTitle("title1");

		// Validate Schema
		assertSchemaMatch(layout);
		// Check Attributes
		assertXpathEvaluatesTo(layout.getId(), "//ui:fieldlayout/@id", layout);
		assertXpathEvaluatesTo("stacked", "//ui:fieldlayout/@layout", layout);
		assertXpathEvaluatesTo("true", "//ui:fieldlayout/@hidden", layout);
		assertXpathEvaluatesTo("10", "//ui:fieldlayout/@labelWidth", layout);
		assertXpathEvaluatesTo("title1", "//ui:fieldlayout/@title", layout);
		// Check No Fields
		assertXpathNotExists("//ui:fieldlayout/ui:field", layout);

		// Set Label Width - 0
		layout.setLabelWidth(0);
		assertXpathEvaluatesTo("", "//ui:fieldlayout/@labelWidth", layout);

		// Set Label Width - 1
		layout.setLabelWidth(1);
		assertXpathEvaluatesTo("1", "//ui:fieldlayout/@labelWidth", layout);

		// Set Label Width - 100
		layout.setLabelWidth(100);
		assertXpathEvaluatesTo("100", "//ui:fieldlayout/@labelWidth", layout);

		// Set ordered
		layout.setOrdered(true);
		assertSchemaMatch(layout);
		assertXpathEvaluatesTo("1", "//ui:fieldlayout/@ordered", layout);

		// Set offset
		layout.setOrderedOffset(20);
		assertXpathEvaluatesTo("20", "//ui:fieldlayout/@ordered", layout);
	}

	@Test
	public void testDoPaintWithFields() throws IOException, SAXException, XpathException {
		WFieldLayout layout = new WFieldLayout();
		layout.addField("Test1", new WTextField());
		layout.addField("Test2", new WTextField());

		// Validate Schema
		assertSchemaMatch(layout);
		// Check Fields
		assertXpathEvaluatesTo("2", "count(//ui:fieldlayout/ui:field)", layout);
	}

	@Test
	public void testXssEscaping() throws IOException, SAXException, XpathException {
		WFieldLayout layout = new WFieldLayout();
		layout.setTitle(getMaliciousAttribute("ui:fieldlayout"));
		assertSafeContent(layout);
	}

	@Test
	public void testRenderedWithMargins() throws IOException, SAXException, XpathException {
		WFieldLayout layout = new WFieldLayout();
		assertXpathNotExists("//ui:fieldlayout/ui:margin", layout);

		Margin margin = new Margin(0);
		layout.setMargin(margin);
		assertXpathNotExists("//ui:fieldlayout/ui:margin", layout);

		margin = new Margin(1);
		layout.setMargin(margin);
		assertSchemaMatch(layout);
		assertXpathEvaluatesTo("1", "//ui:fieldlayout/ui:margin/@all", layout);
		assertXpathEvaluatesTo("", "//ui:fieldlayout/ui:margin/@north", layout);
		assertXpathEvaluatesTo("", "//ui:fieldlayout/ui:margin/@east", layout);
		assertXpathEvaluatesTo("", "//ui:fieldlayout/ui:margin/@south", layout);
		assertXpathEvaluatesTo("", "//ui:fieldlayout/ui:margin/@west", layout);

		margin = new Margin(1, 2, 3, 4);
		layout.setMargin(margin);
		assertSchemaMatch(layout);
		assertXpathEvaluatesTo("", "//ui:fieldlayout/ui:margin/@all", layout);
		assertXpathEvaluatesTo("1", "//ui:fieldlayout/ui:margin/@north", layout);
		assertXpathEvaluatesTo("2", "//ui:fieldlayout/ui:margin/@east", layout);
		assertXpathEvaluatesTo("3", "//ui:fieldlayout/ui:margin/@south", layout);
		assertXpathEvaluatesTo("4", "//ui:fieldlayout/ui:margin/@west", layout);
	}

}
