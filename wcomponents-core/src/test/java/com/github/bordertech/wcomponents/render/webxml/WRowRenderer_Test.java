package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.Margin;
import com.github.bordertech.wcomponents.WColumn;
import com.github.bordertech.wcomponents.WRow;
import java.io.IOException;
import junit.framework.Assert;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * Junit test case for {@link WRowRenderer}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WRowRenderer_Test extends AbstractWebXmlRendererTestCase {

	@Test
	public void testRendererCorrectlyConfigured() {
		WRow component = new WRow();
		Assert.assertTrue("Incorrect renderer supplied",
				getWebXmlRenderer(component) instanceof WRowRenderer);
	}

	@Test
	public void testRenderedFormatWhenEmpty() throws IOException, SAXException, XpathException {
		WRow row = new WRow();
		assertSchemaMatch(row);
		assertXpathNotExists("//ui:row", row);
	}

	@Test
	public void testRenderedFormatWithColumn() throws IOException, SAXException, XpathException {
		WRow row = new WRow();
		row.add(new WColumn(100));

		assertSchemaMatch(row);
		assertXpathExists("//ui:row/ui:column", row);
		assertXpathEvaluatesTo(row.getId(), "//ui:row/@id", row);
		assertXpathEvaluatesTo("", "//ui:row/@hgap", row);
	}

	@Test
	public void testRenderedWithHgap() throws IOException, SAXException, XpathException {
		WRow row = new WRow(10);
		row.add(new WColumn(100));
		assertSchemaMatch(row);
		assertXpathEvaluatesTo(row.getId(), "//ui:row/@id", row);
		assertXpathEvaluatesTo("10", "//ui:row/@hgap", row);
	}

	@Test
	public void testRenderedWithMargins() throws IOException, SAXException, XpathException {
		WRow row = new WRow();
		row.add(new WColumn(100));

		assertXpathNotExists("//ui:row/ui:margin", row);

		Margin margin = new Margin(0);
		row.setMargin(margin);
		assertXpathNotExists("//ui:row/ui:margin", row);

		margin = new Margin(1);
		row.setMargin(margin);
		assertSchemaMatch(row);
		assertXpathEvaluatesTo("1", "//ui:row/ui:margin/@all", row);
		assertXpathEvaluatesTo("", "//ui:row/ui:margin/@north", row);
		assertXpathEvaluatesTo("", "//ui:row/ui:margin/@east", row);
		assertXpathEvaluatesTo("", "//ui:row/ui:margin/@south", row);
		assertXpathEvaluatesTo("", "//ui:row/ui:margin/@west", row);

		margin = new Margin(1, 2, 3, 4);
		row.setMargin(margin);
		assertSchemaMatch(row);
		assertXpathEvaluatesTo("", "//ui:row/ui:margin/@all", row);
		assertXpathEvaluatesTo("1", "//ui:row/ui:margin/@north", row);
		assertXpathEvaluatesTo("2", "//ui:row/ui:margin/@east", row);
		assertXpathEvaluatesTo("3", "//ui:row/ui:margin/@south", row);
		assertXpathEvaluatesTo("4", "//ui:row/ui:margin/@west", row);
	}

}
