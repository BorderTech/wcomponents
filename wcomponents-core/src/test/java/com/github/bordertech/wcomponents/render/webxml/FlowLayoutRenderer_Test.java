package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.Size;
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
 * @author Mark Reeves
 * @since 1.0.0
 */
public class FlowLayoutRenderer_Test extends AbstractWebXmlRendererTestCase {

	/**
	 * A space between components.
	 */
	private static final Size GAP = Size.SMALL;

	/**
	 * A different space used to differentiate the (now deprecated) hgap and vgap in the two-gap constructors.
	 */
	private static final Size BIG_GAP = Size.LARGE;

	// The expected render of the default constructor.
	@Test
	public void testDoRenderWhenEmpty() throws IOException, SAXException, XpathException {
		WPanel panel = new WPanel();
		panel.setLayout(new FlowLayout());
		assertSchemaMatch(panel);

		assertXpathExists("//ui:panel/ui:flowlayout", panel);
		assertXpathEvaluatesTo("center", "//ui:panel/ui:flowlayout/@align", panel);
		assertXpathNotExists("//ui:panel/ui:flowlayout/ui:cell", panel);
		assertXpathNotExists("//ui:panel/ui:flowlayout/@gap", panel);
		assertXpathNotExists("//ui:panel/ui:flowlayout/@valign", panel);
	}

	@Test
	public void testDoRenderWithContent() throws IOException, SAXException, XpathException {
		final String text1 = "FlowRenderer_Test.testPaint.text1";
		final String text2 = "FlowRenderer_Test.testPaint.text2";

		WPanel panel = new WPanel();
		panel.setLayout(new FlowLayout());
		assertSchemaMatch(panel);

		panel.add(new WText(text1));
		panel.add(new WText(text2));
		assertSchemaMatch(panel);
		assertXpathEvaluatesTo("2", "count(//ui:panel/ui:flowlayout/ui:cell)", panel);
		assertXpathEvaluatesTo(text1, "normalize-space(//ui:panel/ui:flowlayout/ui:cell[1])", panel);
		assertXpathEvaluatesTo(text2, "normalize-space(//ui:panel/ui:flowlayout/ui:cell[2])", panel);
	}

	@Test
	public void testRenderAlignment() throws IOException, SAXException, XpathException {
		WPanel panel = new WPanel();
		String expected;

		for (FlowLayout.Alignment a : FlowLayout.Alignment.values()) {
			panel.setLayout(new FlowLayout(a));
			assertSchemaMatch(panel);
			assertXpathExists("//ui:panel/ui:flowlayout", panel);
			expected = a.toString().toLowerCase();
			assertXpathEvaluatesTo(expected, "//ui:panel/ui:flowlayout/@align", panel);
		}
	}

	@Test
	public void testRenderContentAlignment() throws IOException, SAXException, XpathException {
		WPanel panel = new WPanel();
		String expected;

		for (FlowLayout.Alignment a : FlowLayout.Alignment.values()) {
			for (FlowLayout.ContentAlignment c : FlowLayout.ContentAlignment.values()) {
				panel.setLayout(new FlowLayout(a, c));
				assertSchemaMatch(panel);
				assertXpathExists("//ui:panel/ui:flowlayout", panel);
				expected = a.toString().toLowerCase();
				assertXpathEvaluatesTo(expected, "//ui:panel/ui:flowlayout/@align", panel);

				if (a == FlowLayout.VERTICAL) {
					assertXpathNotExists("//ui:panel/ui:flowlayout/@valign", panel);
				} else {
					expected = c.toString().toLowerCase();
					assertXpathEvaluatesTo(expected, "//ui:panel/ui:flowlayout/@valign", panel);
				}
			}
		}
	}

	@Test
	public void testRenderGap() throws IOException, SAXException, XpathException {
		WPanel panel = new WPanel();

		for (FlowLayout.Alignment a : FlowLayout.Alignment.values()) {
			panel.setLayout(new FlowLayout(a, GAP));
			assertSchemaMatch(panel);
			assertXpathEvaluatesTo(GAP.toString(), "//ui:panel/ui:flowlayout/@gap", panel);
		}
	}

	// Test that use of deprecated two-gap constructors output the expected gap
	@Test
	public void testHVGaps() throws IOException, SAXException, XpathException {
		WPanel panel = new WPanel();
		String expected;

		for (FlowLayout.Alignment a : FlowLayout.Alignment.values()) {
			panel.setLayout(new FlowLayout(a, 3, 16));
			assertSchemaMatch(panel);
			expected = a == FlowLayout.VERTICAL ? BIG_GAP.toString() : GAP.toString();
			assertXpathEvaluatesTo(expected, "//ui:panel/ui:flowlayout/@gap", panel);
		}
	}

	@Test
	public void testHVGapsWithContentAlign() throws IOException, SAXException, XpathException {
		WPanel panel = new WPanel();
		String expected;

		for (FlowLayout.Alignment a : FlowLayout.Alignment.values()) {
			for (FlowLayout.ContentAlignment c : FlowLayout.ContentAlignment.values()) {
				panel.setLayout(new FlowLayout(a, 3, 16, c));
				assertSchemaMatch(panel);
				expected = a == FlowLayout.VERTICAL ? BIG_GAP.toString() : GAP.toString();
				assertXpathEvaluatesTo(expected, "//ui:panel/ui:flowlayout/@gap", panel);
			}
		}
	}

}
