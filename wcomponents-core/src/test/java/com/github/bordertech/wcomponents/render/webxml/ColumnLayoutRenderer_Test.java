package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WText;
import com.github.bordertech.wcomponents.layout.ColumnLayout;
import com.github.bordertech.wcomponents.layout.ColumnLayout.Alignment;
import java.io.IOException;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * Junit test case for {@link ColumnLayoutRenderer}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class ColumnLayoutRenderer_Test extends AbstractWebXmlRendererTestCase {

	@Test
	public void testDoRenderWhenEmpty() throws IOException, SAXException, XpathException {
		WPanel container = new WPanel();
		container.setLayout(new ColumnLayout(new int[]{50, 50}));
		assertSchemaMatch(container);

		assertXpathNotExists("//ui:panel/ui:columnlayout/ui:cell", container);
	}

	@Test
	public void testDoRender() throws IOException, SAXException, XpathException {
		final int[] cols = new int[]{1, 100};
		final Alignment[] aligns = new Alignment[]{Alignment.RIGHT, Alignment.CENTER};

		final String text1 = "ColumnRenderer_Test.testPaint.text1";
		final String text2 = "ColumnRenderer_Test.testPaint.text2";

		WPanel container = new WPanel();
		container.setLayout(new ColumnLayout(cols));

		// One element -> 1 row, 2 cols (one empty)
		container.add(new WText(text1));
		assertSchemaMatch(container);
		assertXpathEvaluatesTo("1", "count(//ui:panel/ui:columnlayout)", container);
		assertXpathEvaluatesTo(String.valueOf(cols.length),
				"count(//ui:panel/ui:columnlayout/ui:column)", container);
		assertXpathEvaluatesTo(text1, "normalize-space(//ui:panel/ui:columnlayout/ui:cell[1])",
				container);
		assertXpathEvaluatesTo("", "normalize-space(//ui:panel/ui:columnlayout/ui:cell[2])",
				container);

		// Two elements -> 1 row, 2 cols
		container.add(new WText(text2));
		assertSchemaMatch(container);
		assertXpathEvaluatesTo("1", "count(//ui:panel/ui:columnlayout)", container);
		assertXpathEvaluatesTo(String.valueOf(cols.length),
				"count(//ui:panel/ui:columnlayout/ui:column)", container);
		assertXpathEvaluatesTo(String.valueOf(cols[0]),
				"//ui:panel/ui:columnlayout/ui:column[1]/@width", container);
		assertXpathEvaluatesTo(String.valueOf(cols[1]),
				"//ui:panel/ui:columnlayout/ui:column[2]/@width", container);
		assertXpathEvaluatesTo(text1, "normalize-space(//ui:panel/ui:columnlayout/ui:cell[1])",
				container);
		assertXpathEvaluatesTo(text2, "normalize-space(//ui:panel/ui:columnlayout/ui:cell[2])",
				container);
		assertXpathEvaluatesTo("", "//ui:panel/ui:columnlayout/@hgap", container);
		assertXpathEvaluatesTo("", "//ui:panel/ui:columnlayout/@vgap", container);

		// Test hgap, vgap
		container.setLayout(new ColumnLayout(cols, 1, 2));
		assertSchemaMatch(container);
		assertXpathEvaluatesTo("1", "//ui:panel/ui:columnlayout/@hgap", container);
		assertXpathEvaluatesTo("2", "//ui:panel/ui:columnlayout/@vgap", container);

		// Test Alignment
		container.setLayout(new ColumnLayout(cols, aligns));
		assertSchemaMatch(container);
		assertXpathEvaluatesTo("", "//ui:panel/ui:columnlayout/@hgap", container);
		assertXpathEvaluatesTo("", "//ui:panel/ui:columnlayout/@vgap", container);
		assertXpathEvaluatesTo("right", "//ui:panel/ui:columnlayout/ui:column[1]/@align", container);
		assertXpathEvaluatesTo("center", "//ui:panel/ui:columnlayout/ui:column[2]/@align", container);

		// Test Alignment, hgap, vgap
		container.setLayout(new ColumnLayout(cols, aligns, 1, 2));
		assertSchemaMatch(container);
		assertXpathEvaluatesTo("1", "//ui:panel/ui:columnlayout/@hgap", container);
		assertXpathEvaluatesTo("2", "//ui:panel/ui:columnlayout/@vgap", container);
		assertXpathEvaluatesTo("right", "//ui:panel/ui:columnlayout/ui:column[1]/@align", container);
		assertXpathEvaluatesTo("center", "//ui:panel/ui:columnlayout/ui:column[2]/@align", container);
	}
}
