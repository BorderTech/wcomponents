package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.WColumn;
import com.github.bordertech.wcomponents.WLabel;
import com.github.bordertech.wcomponents.WRow;
import java.io.IOException;
import junit.framework.Assert;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * Junit test case for {@link WColumnRenderer}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WColumnRenderer_Test extends AbstractWebXmlRendererTestCase {

	@Test
	public void testRendererCorrectlyConfigured() {
		WColumn component = new WColumn();
		Assert.assertTrue("Incorrect renderer supplied",
				getWebXmlRenderer(component) instanceof WColumnRenderer);
	}

	@Test
	public void testPaintWhenEmpty() throws IOException, SAXException {
		WColumn column = new WColumn(100);

		// A Column is not valid by itself, so we must add it to a row.
		WRow row = new WRow();
		row.add(column);
		assertSchemaMatch(row);
	}

	@Test
	public void testPaint() throws IOException, SAXException, XpathException {
		WRow row = new WRow();
		WColumn column = new WColumn(100);
		row.add(column);

		column.add(new WLabel("dummy"));
		assertSchemaMatch(row);
		assertXpathEvaluatesTo(column.getId(), "//ui:column/@id", row);
		assertXpathEvaluatesTo("100", "//ui:column/@width", row);
		assertXpathNotExists("//ui:column/@align", row);

		column.setAlignment(WColumn.Alignment.LEFT);
		assertSchemaMatch(row);
		assertXpathNotExists("//ui:column/@align", row);

		column.setAlignment(WColumn.Alignment.CENTER);
		assertSchemaMatch(row);
		assertXpathEvaluatesTo("center", "//ui:column/@align", row);

		column.setAlignment(WColumn.Alignment.RIGHT);
		assertSchemaMatch(row);
		assertXpathEvaluatesTo("right", "//ui:column/@align", row);
	}
}
