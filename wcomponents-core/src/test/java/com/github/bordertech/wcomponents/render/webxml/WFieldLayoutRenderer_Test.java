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
		assertXpathEvaluatesTo(layout.getId(), "//ui:fieldLayout/@id", layout);
		assertXpathEvaluatesTo("flat", "//ui:fieldLayout/@layout", layout);
		assertXpathEvaluatesTo("", "//ui:fieldLayout/@hidden", layout);
		assertXpathEvaluatesTo("", "//ui:fieldLayout/@labelWidth", layout);
		assertXpathEvaluatesTo("", "//ui:fieldLayout/@title", layout);
		assertXpathEvaluatesTo("", "//ui:fieldLayout/@ordered", layout);
		// Check No Fields
		assertXpathNotExists("//ui:fieldLayout/ui:field", layout);
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
		assertXpathEvaluatesTo(layout.getId(), "//ui:fieldLayout/@id", layout);
		assertXpathEvaluatesTo("stacked", "//ui:fieldLayout/@layout", layout);
		assertXpathEvaluatesTo("true", "//ui:fieldLayout/@hidden", layout);
		assertXpathEvaluatesTo("10", "//ui:fieldLayout/@labelWidth", layout);
		assertXpathEvaluatesTo("title1", "//ui:fieldLayout/@title", layout);
		// Check No Fields
		assertXpathNotExists("//ui:fieldLayout/ui:field", layout);

		// Set Label Width - 0
		layout.setLabelWidth(0);
		assertXpathEvaluatesTo("", "//ui:fieldLayout/@labelWidth", layout);

		// Set Label Width - 1
		layout.setLabelWidth(1);
		assertXpathEvaluatesTo("1", "//ui:fieldLayout/@labelWidth", layout);

		// Set Label Width - 100
		layout.setLabelWidth(100);
		assertXpathEvaluatesTo("100", "//ui:fieldLayout/@labelWidth", layout);

		// Set ordered
		layout.setOrdered(true);
		assertSchemaMatch(layout);
		assertXpathEvaluatesTo("1", "//ui:fieldLayout/@ordered", layout);

		// Set offset
		layout.setOrderedOffset(20);
		assertXpathEvaluatesTo("20", "//ui:fieldLayout/@ordered", layout);
	}

	@Test
	public void testDoPaintWithFields() throws IOException, SAXException, XpathException {
		WFieldLayout layout = new WFieldLayout();
		layout.addField("Test1", new WTextField());
		layout.addField("Test2", new WTextField());

		// Validate Schema
		assertSchemaMatch(layout);
		// Check Fields
		assertXpathEvaluatesTo("2", "count(//ui:fieldLayout/ui:field)", layout);
	}

	@Test
	public void testXssEscaping() throws IOException, SAXException, XpathException {
		WFieldLayout layout = new WFieldLayout();
		layout.setTitle(getMaliciousAttribute("ui:fieldLayout"));
		assertSafeContent(layout);
	}

	@Test
	public void testRenderedWithMargins() throws IOException, SAXException, XpathException {
		WFieldLayout layout = new WFieldLayout();
		assertXpathNotExists("//ui:fieldLayout/ui:margin", layout);

		Margin margin = new Margin(0);
		layout.setMargin(margin);
		assertXpathNotExists("//ui:fieldLayout/ui:margin", layout);

		margin = new Margin(1);
		layout.setMargin(margin);
		assertSchemaMatch(layout);
		assertXpathEvaluatesTo("1", "//ui:fieldLayout/ui:margin/@all", layout);
		assertXpathEvaluatesTo("", "//ui:fieldLayout/ui:margin/@north", layout);
		assertXpathEvaluatesTo("", "//ui:fieldLayout/ui:margin/@east", layout);
		assertXpathEvaluatesTo("", "//ui:fieldLayout/ui:margin/@south", layout);
		assertXpathEvaluatesTo("", "//ui:fieldLayout/ui:margin/@west", layout);

		margin = new Margin(1, 2, 3, 4);
		layout.setMargin(margin);
		assertSchemaMatch(layout);
		assertXpathEvaluatesTo("", "//ui:fieldLayout/ui:margin/@all", layout);
		assertXpathEvaluatesTo("1", "//ui:fieldLayout/ui:margin/@north", layout);
		assertXpathEvaluatesTo("2", "//ui:fieldLayout/ui:margin/@east", layout);
		assertXpathEvaluatesTo("3", "//ui:fieldLayout/ui:margin/@south", layout);
		assertXpathEvaluatesTo("4", "//ui:fieldLayout/ui:margin/@west", layout);
	}

}
