package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WText;
import com.github.bordertech.wcomponents.layout.GridLayout;
import java.io.IOException;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * Junit test case for {@link GridLayoutRenderer}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class GridLayoutRenderer_Test extends AbstractWebXmlRendererTestCase {

	@Test
	public void testDoRenderWhenEmpty() throws IOException, SAXException, XpathException {
		WPanel panel = new WPanel();
		panel.setLayout(new GridLayout(0, 1));
		assertSchemaMatch(panel);

		assertXpathExists("//ui:panel/ui:gridlayout", panel);
		assertXpathNotExists("//ui:panel/ui:gridlayout/ui:cell", panel);
		assertXpathEvaluatesTo("0", "//ui:panel/ui:gridlayout/@rows", panel);
		assertXpathEvaluatesTo("1", "//ui:panel/ui:gridlayout/@cols", panel);
		assertXpathNotExists("//ui:panel/ui:gridlayout/@hgap", panel);
		assertXpathNotExists("//ui:panel/ui:gridlayout/@vgap", panel);
	}

	@Test
	public void testDoRender() throws IOException, SAXException, XpathException {
		final String text1 = "GridRenderer_Test.testPaint.text1";
		final String text2 = "GridRenderer_Test.testPaint.text2";

		WPanel panel = new WPanel();
		panel.setLayout(new GridLayout(1, 2, 3, 4));
		assertSchemaMatch(panel);

		assertXpathEvaluatesTo("1", "//ui:panel/ui:gridlayout/@rows", panel);
		assertXpathEvaluatesTo("2", "//ui:panel/ui:gridlayout/@cols", panel);
		assertXpathEvaluatesTo("3", "//ui:panel/ui:gridlayout/@hgap", panel);
		assertXpathEvaluatesTo("4", "//ui:panel/ui:gridlayout/@vgap", panel);
		assertXpathNotExists("//ui:panel/ui:gridlayout/ui:cell", panel);

		panel.add(new WText(text1));
		panel.add(new WText(text2));
		assertXpathEvaluatesTo("2", "count(//ui:panel/ui:gridlayout/ui:cell)", panel);
		assertXpathEvaluatesTo(text1, "normalize-space(//ui:panel/ui:gridlayout/ui:cell[1])", panel);
		assertXpathEvaluatesTo(text2, "normalize-space(//ui:panel/ui:gridlayout/ui:cell[2])", panel);
	}
}
