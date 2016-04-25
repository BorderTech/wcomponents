package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WText;
import com.github.bordertech.wcomponents.layout.FlowLayout;
import java.io.IOException;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * Junit test case for {@link FlowLayoutRenderer}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class FlowLayoutRenderer_Test extends AbstractWebXmlRendererTestCase {

	@Test
	public void testDoRenderWhenEmpty() throws IOException, SAXException, XpathException {
		WPanel panel = new WPanel();
		panel.setLayout(new FlowLayout());
		assertSchemaMatch(panel);

		assertXpathExists("//ui:panel/ui:flowlayout", panel);
		assertXpathNotExists("//ui:panel/ui:flowlayout/ui:cell", panel);
		assertXpathNotExists("//ui:panel/ui:flowlayout/@hgap", panel);
		assertXpathNotExists("//ui:panel/ui:flowlayout/@vgap", panel);
		assertXpathEvaluatesTo("center", "//ui:panel/ui:flowlayout/@align", panel);
	}

	@Test
	public void testDoRenderWithContent() throws IOException, SAXException, XpathException {
		final String text1 = "FlowRenderer_Test.testPaint.text1";
		final String text2 = "FlowRenderer_Test.testPaint.text2";

		WPanel panel = new WPanel();
		panel.setLayout(new FlowLayout(FlowLayout.Alignment.LEFT, 3, 4));
		assertSchemaMatch(panel);

		assertXpathEvaluatesTo("3", "//ui:panel/ui:flowlayout/@hgap", panel);
		assertXpathEvaluatesTo("4", "//ui:panel/ui:flowlayout/@vgap", panel);
		assertXpathEvaluatesTo("left", "//ui:panel/ui:flowlayout/@align", panel);
		assertXpathNotExists("//ui:panel/ui:flowlayout/ui:cell", panel);

		panel.add(new WText(text1));
		panel.add(new WText(text2));
		assertSchemaMatch(panel);
		assertXpathEvaluatesTo("2", "count(//ui:panel/ui:flowlayout/ui:cell)", panel);
		assertXpathEvaluatesTo(text1, "normalize-space(//ui:panel/ui:flowlayout/ui:cell[1])", panel);
		assertXpathEvaluatesTo(text2, "normalize-space(//ui:panel/ui:flowlayout/ui:cell[2])", panel);
	}

	@Test
	public void testRenderAlignmentOptions() throws IOException, SAXException, XpathException {
		WPanel panel = new WPanel();
		panel.setLayout(new FlowLayout());
		assertSchemaMatch(panel);

		assertXpathExists("//ui:panel/ui:flowlayout", panel);
		assertXpathEvaluatesTo("center", "//ui:panel/ui:flowlayout/@align", panel);

		panel.setLayout(new FlowLayout(FlowLayout.Alignment.CENTER));
		assertSchemaMatch(panel);
		assertXpathEvaluatesTo("center", "//ui:panel/ui:flowlayout/@align", panel);

		panel.setLayout(new FlowLayout(FlowLayout.Alignment.RIGHT));
		assertSchemaMatch(panel);
		assertXpathEvaluatesTo("right", "//ui:panel/ui:flowlayout/@align", panel);

		panel.setLayout(new FlowLayout(FlowLayout.Alignment.LEFT));
		assertSchemaMatch(panel);
		assertXpathEvaluatesTo("left", "//ui:panel/ui:flowlayout/@align", panel);

		panel.setLayout(new FlowLayout(FlowLayout.Alignment.VERTICAL));
		assertSchemaMatch(panel);
		assertXpathEvaluatesTo("vertical", "//ui:panel/ui:flowlayout/@align", panel);
	}

	@Test
	public void testRenderContentAlignmentOptions() throws IOException, SAXException, XpathException {
		WPanel panel = new WPanel();
		panel.setLayout(new FlowLayout());
		assertSchemaMatch(panel);

		assertXpathExists("//ui:panel/ui:flowlayout", panel);
		assertXpathEvaluatesTo("center", "//ui:panel/ui:flowlayout/@align", panel);

		panel.
				setLayout(new FlowLayout(FlowLayout.Alignment.CENTER,
						FlowLayout.ContentAlignment.TOP));
		assertSchemaMatch(panel);
		assertXpathEvaluatesTo("top", "//ui:panel/ui:flowlayout/@valign", panel);

		panel.setLayout(new FlowLayout(FlowLayout.Alignment.CENTER,
				FlowLayout.ContentAlignment.MIDDLE));
		assertSchemaMatch(panel);
		assertXpathEvaluatesTo("middle", "//ui:panel/ui:flowlayout/@valign", panel);

		panel.setLayout(new FlowLayout(FlowLayout.Alignment.CENTER,
				FlowLayout.ContentAlignment.BASELINE));
		assertSchemaMatch(panel);
		assertXpathEvaluatesTo("baseline", "//ui:panel/ui:flowlayout/@valign", panel);

		panel.setLayout(new FlowLayout(FlowLayout.Alignment.CENTER,
				FlowLayout.ContentAlignment.BOTTOM));
		assertSchemaMatch(panel);
		assertXpathEvaluatesTo("bottom", "//ui:panel/ui:flowlayout/@valign", panel);
	}

}
