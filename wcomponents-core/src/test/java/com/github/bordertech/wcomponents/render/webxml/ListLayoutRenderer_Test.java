package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WText;
import com.github.bordertech.wcomponents.layout.ListLayout;
import java.io.IOException;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * Junit test case for {@link ListLayoutRenderer}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class ListLayoutRenderer_Test extends AbstractWebXmlRendererTestCase {

	@Test
	public void testDoRenderWhenEmpty() throws IOException, SAXException, XpathException {
		WPanel panel = new WPanel();
		panel.setLayout(new ListLayout(ListLayout.Type.FLAT, ListLayout.Alignment.LEFT,
				ListLayout.Separator.NONE, false));
		assertSchemaMatch(panel);

		assertXpathExists("//ui:panel/ui:listlayout", panel);
		assertXpathNotExists("//ui:panel/ui:listlayout/ui:cell", panel);
		assertXpathNotExists("//ui:panel/ui:listlayout/@hgap", panel);
		assertXpathNotExists("//ui:panel/ui:listlayout/@vgap", panel);
		assertXpathNotExists("//ui:panel/ui:listlayout/@align", panel);
		assertXpathNotExists("//ui:panel/ui:listlayout/@separator", panel);
		assertXpathNotExists("//ui:panel/ui:listlayout/@ordered", panel);
		assertXpathEvaluatesTo("flat", "//ui:panel/ui:listlayout/@type", panel);

		panel.setLayout(new ListLayout(ListLayout.Type.STRIPED, ListLayout.Alignment.CENTER,
				ListLayout.Separator.NONE, false));
		assertXpathEvaluatesTo("striped", "//ui:panel/ui:listlayout/@type", panel);
		assertXpathEvaluatesTo("center", "//ui:panel/ui:listlayout/@align", panel);
		assertXpathNotExists("//ui:panel/ui:listlayout/ui:cell", panel);
		assertXpathNotExists("//ui:panel/ui:listlayout/@hgap", panel);
		assertXpathNotExists("//ui:panel/ui:listlayout/@vgap", panel);
		assertXpathNotExists("//ui:panel/ui:listlayout/@separator", panel);
		assertXpathNotExists("//ui:panel/ui:listlayout/@ordered", panel);

		panel.setLayout(new ListLayout(ListLayout.Type.STACKED, ListLayout.Alignment.RIGHT,
				ListLayout.Separator.NONE, false, 1, 2));
		assertXpathEvaluatesTo("stacked", "//ui:panel/ui:listlayout/@type", panel);
		assertXpathEvaluatesTo("right", "//ui:panel/ui:listlayout/@align", panel);
		assertXpathEvaluatesTo("1", "//ui:panel/ui:listlayout/@hgap", panel);
		assertXpathEvaluatesTo("2", "//ui:panel/ui:listlayout/@vgap", panel);
		assertXpathNotExists("//ui:panel/ui:listlayout/ui:cell", panel);
		assertXpathNotExists("//ui:panel/ui:listlayout/@separator", panel);
		assertXpathNotExists("//ui:panel/ui:listlayout/@ordered", panel);
	}

	@Test
	public void testDoRender() throws IOException, SAXException, XpathException {
		final String text1 = "FlowRenderer_Test.testPaint.text1";
		final String text2 = "FlowRenderer_Test.testPaint.text2";

		WPanel panel = new WPanel();
		panel.setLayout(new ListLayout(ListLayout.Type.STRIPED, ListLayout.Alignment.LEFT,
				ListLayout.Separator.DOT, true, 3, 4));
		assertSchemaMatch(panel);

		assertXpathEvaluatesTo("striped", "//ui:panel/ui:listlayout/@type", panel);
		assertXpathNotExists("//ui:panel/ui:listlayout/@align", panel);
		assertXpathEvaluatesTo("dot", "//ui:panel/ui:listlayout/@separator", panel);
		assertXpathEvaluatesTo("3", "//ui:panel/ui:listlayout/@hgap", panel);
		assertXpathEvaluatesTo("4", "//ui:panel/ui:listlayout/@vgap", panel);
		assertXpathEvaluatesTo("true", "//ui:panel/ui:listlayout/@ordered", panel);
		assertXpathNotExists("//ui:panel/ui:listlayout/ui:cell", panel);

		panel.add(new WText(text1));
		panel.add(new WText(text2));
		assertXpathEvaluatesTo("2", "count(//ui:panel/ui:listlayout/ui:cell)", panel);
		assertXpathEvaluatesTo(text1, "normalize-space(//ui:panel/ui:listlayout/ui:cell[1])", panel);
		assertXpathEvaluatesTo(text2, "normalize-space(//ui:panel/ui:listlayout/ui:cell[2])", panel);
	}
}
