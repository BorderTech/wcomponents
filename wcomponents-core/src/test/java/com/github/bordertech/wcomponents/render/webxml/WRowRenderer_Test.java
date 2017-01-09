package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.Margin;
import com.github.bordertech.wcomponents.WColumn;
import com.github.bordertech.wcomponents.WRow;
import com.github.bordertech.wcomponents.util.GapSizeUtil;
import java.io.IOException;
import junit.framework.Assert;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * Junit test case for {@link WRowRenderer}.
 *
 * @author Yiannis Paschalidis
 * @author Mark Reeves
 * @since 1.0.0
 */
public class WRowRenderer_Test extends AbstractWebXmlRendererTestCase {
	/**
	 * A small gap.
	 */
	private static final GapSizeUtil.Size GAP = GapSizeUtil.Size.SMALL;
	/**
	 * A big gap.
	 */
	private static final GapSizeUtil.Size BIG_GAP = GapSizeUtil.Size.LARGE;

	@Test
	public void testRendererCorrectlyConfigured() {
		WRow component = new WRow();
		Assert.assertTrue("Incorrect renderer supplied", getWebXmlRenderer(component) instanceof WRowRenderer);
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
		assertXpathEvaluatesTo("", "//ui:row/@gap", row);
	}

	@Test
	public void testRenderedWithSmallGap() throws IOException, SAXException, XpathException {
		WRow row = new WRow(GAP);
		row.add(new WColumn(100));
		assertSchemaMatch(row);
		assertXpathEvaluatesTo(GAP.toString(), "//ui:row/@gap", row);
	}

	@Test
	public void testRenderedWithMargins() throws IOException, SAXException, XpathException {
		WRow row = new WRow();
		row.add(new WColumn(100));

		assertXpathNotExists("//ui:row/ui:margin", row);

		Margin margin = new Margin(0);
		row.setMargin(margin);
		assertXpathNotExists("//ui:row/ui:margin", row);

		margin = new Margin(GAP);
		row.setMargin(margin);
		assertSchemaMatch(row);
		assertXpathEvaluatesTo(GAP.toString(), "//ui:row/ui:margin/@all", row);
		assertXpathEvaluatesTo("", "//ui:row/ui:margin/@north", row);
		assertXpathEvaluatesTo("", "//ui:row/ui:margin/@east", row);
		assertXpathEvaluatesTo("", "//ui:row/ui:margin/@south", row);
		assertXpathEvaluatesTo("", "//ui:row/ui:margin/@west", row);

		margin = new Margin(GapSizeUtil.Size.SMALL, GapSizeUtil.Size.MEDIUM, GapSizeUtil.Size.LARGE, GapSizeUtil.Size.XL);
		row.setMargin(margin);
		assertSchemaMatch(row);
		assertXpathEvaluatesTo("", "//ui:row/ui:margin/@all", row);
		assertXpathEvaluatesTo(GapSizeUtil.Size.SMALL.toString(), "//ui:row/ui:margin/@north", row);
		assertXpathEvaluatesTo(GapSizeUtil.Size.MEDIUM.toString(), "//ui:row/ui:margin/@east", row);
		assertXpathEvaluatesTo(GapSizeUtil.Size.LARGE.toString(), "//ui:row/ui:margin/@south", row);
		assertXpathEvaluatesTo(GapSizeUtil.Size.XL.toString(), "//ui:row/ui:margin/@west", row);
	}

}
