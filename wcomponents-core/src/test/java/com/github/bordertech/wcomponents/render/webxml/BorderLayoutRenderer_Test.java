package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WText;
import com.github.bordertech.wcomponents.layout.BorderLayout;
import java.io.IOException;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * Junit test case for {@link BorderLayoutRenderer}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class BorderLayoutRenderer_Test extends AbstractWebXmlRendererTestCase {

	@Test
	public void testDoRenderWhenEmpty() throws IOException, SAXException, XpathException {
		WPanel container = new WPanel();
		container.setLayout(new BorderLayout());
		assertSchemaMatch(container);

		assertXpathExists("//ui:panel/ui:borderlayout", container);
		assertXpathNotExists("//ui:panel/ui:borderlayout/@hgap", container);
		assertXpathNotExists("//ui:panel/ui:borderlayout/@vgap", container);
		assertXpathNotExists("//ui:panel/ui:borderlayout/ui:north", container);
		assertXpathNotExists("//ui:panel/ui:borderlayout/ui:south", container);
		assertXpathNotExists("//ui:panel/ui:borderlayout/ui:east", container);
		assertXpathNotExists("//ui:panel/ui:borderlayout/ui:west", container);
		assertXpathNotExists("//ui:panel/ui:borderlayout/ui:center", container);
	}

	@Test
	public void testDoRender() throws IOException, SAXException, XpathException {
		final String northText = "BorderRenderer_Test.testPaint.northText";
		final String westText = "BorderRenderer_Test.testPaint.westText";
		final String southText = "BorderRenderer_Test.testPaint.southText";
		final String eastText = "BorderRenderer_Test.testPaint.eastText";
		final String centerText = "BorderRenderer_Test.testPaint.centerText";

		WPanel container = new WPanel();
		container.setLayout(new BorderLayout(3, 4));
		assertSchemaMatch(container);

		assertXpathEvaluatesTo("3", "//ui:panel/ui:borderlayout/@hgap", container);
		assertXpathEvaluatesTo("4", "//ui:panel/ui:borderlayout/@vgap", container);
		assertXpathNotExists("//ui:panel/ui:borderlayout/ui:north", container);
		assertXpathNotExists("//ui:panel/ui:borderlayout/ui:south", container);
		assertXpathNotExists("//ui:panel/ui:borderlayout/ui:east", container);
		assertXpathNotExists("//ui:panel/ui:borderlayout/ui:west", container);
		assertXpathNotExists("//ui:panel/ui:borderlayout/ui:center", container);

		container.add(new WText(northText), BorderLayout.NORTH);
		container.add(new WText(westText), BorderLayout.WEST);
		assertXpathEvaluatesTo(northText, "normalize-space(//ui:panel/ui:borderlayout/ui:north)",
				container);
		assertXpathNotExists("//ui:panel/ui:borderlayout/ui:south", container);
		assertXpathNotExists("//ui:panel/ui:borderlayout/ui:east", container);
		assertXpathEvaluatesTo(westText, "normalize-space(//ui:panel/ui:borderlayout/ui:west)",
				container);
		assertXpathNotExists("//ui:panel/ui:borderlayout/ui:center", container);

		container.add(new WText(southText), BorderLayout.SOUTH);
		container.add(new WText(eastText), BorderLayout.EAST);
		container.add(new WText(centerText));
		assertXpathEvaluatesTo(northText, "normalize-space(//ui:panel/ui:borderlayout/ui:north)",
				container);
		assertXpathEvaluatesTo(westText, "normalize-space(//ui:panel/ui:borderlayout/ui:west)",
				container);
		assertXpathEvaluatesTo(southText, "normalize-space(//ui:panel/ui:borderlayout/ui:south)",
				container);
		assertXpathEvaluatesTo(eastText, "normalize-space(//ui:panel/ui:borderlayout/ui:east)",
				container);
		assertXpathEvaluatesTo(centerText, "normalize-space(//ui:panel/ui:borderlayout/ui:center)",
				container);
	}
}
