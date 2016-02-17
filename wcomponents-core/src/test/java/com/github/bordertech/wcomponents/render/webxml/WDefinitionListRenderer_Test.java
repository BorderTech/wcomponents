package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.Margin;
import com.github.bordertech.wcomponents.WDefinitionList;
import com.github.bordertech.wcomponents.WText;
import java.io.IOException;
import junit.framework.Assert;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * Junit test case for {@link WDefinitionListRenderer}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WDefinitionListRenderer_Test extends AbstractWebXmlRendererTestCase {

	@Test
	public void testRendererCorrectlyConfigured() {
		WDefinitionList list = new WDefinitionList();
		Assert.assertTrue("Incorrect renderer supplied",
				getWebXmlRenderer(list) instanceof WDefinitionListRenderer);
	}

	@Test
	public void testRenderedFormat() throws IOException, SAXException, XpathException {
		final String term1 = "WDefinitionListRenderer_Test.testRenderedFormat.term1";
		final String term2 = "WDefinitionListRenderer_Test.testRenderedFormat.term2";
		final String term3 = "WDefinitionListRenderer_Test.testRenderedFormat.term3";

		final String text1 = "WDefinitionListRenderer_Test.testRenderedFormat.text1";
		final String text2 = "WDefinitionListRenderer_Test.testRenderedFormat.text2";
		final String text3 = "WDefinitionListRenderer_Test.testRenderedFormat.text3";
		final String text4 = "WDefinitionListRenderer_Test.testRenderedFormat.text3";

		WDefinitionList list = new WDefinitionList();
		assertSchemaMatch(list);
		assertXpathNotExists("//ui:definitionlist/ui:term", list);
		assertXpathNotExists("//ui:definitionlist/@type", list);

		list.setType(WDefinitionList.Type.STACKED);
		list.addTerm(term1, new WText(text1));
		assertSchemaMatch(list);
		assertXpathEvaluatesTo("stacked", "//ui:definitionlist/@type", list);
		assertXpathEvaluatesTo("1", "count(//ui:definitionlist/ui:term)", list);
		assertXpathEvaluatesTo(term1, "//ui:definitionlist/ui:term/@text", list);
		assertXpathEvaluatesTo("1", "count(//ui:definitionlist/ui:term/ui:data)", list);
		assertXpathEvaluatesTo(text1, "normalize-space(//ui:definitionlist/ui:term/ui:data)", list);

		list.addTerm(term1, new WText(text2));
		list.setType(WDefinitionList.Type.COLUMN);
		assertSchemaMatch(list);
		assertXpathEvaluatesTo("column", "//ui:definitionlist/@type", list);
		assertXpathEvaluatesTo("1", "count(//ui:definitionlist/ui:term)", list);
		assertXpathEvaluatesTo(term1, "//ui:definitionlist/ui:term/@text", list);
		assertXpathEvaluatesTo("2", "count(//ui:definitionlist/ui:term/ui:data)", list);
		assertXpathEvaluatesTo(text1, "normalize-space(//ui:definitionlist/ui:term/ui:data[1])",
				list);
		assertXpathEvaluatesTo(text2, "normalize-space(//ui:definitionlist/ui:term/ui:data[2])",
				list);

		list.addTerm(term2, new WText(text3));
		list.setType(WDefinitionList.Type.FLAT);
		assertSchemaMatch(list);
		assertXpathEvaluatesTo("flat", "//ui:definitionlist/@type", list);
		assertXpathEvaluatesTo("2", "count(//ui:definitionlist/ui:term)", list);
		assertXpathEvaluatesTo(term1, "//ui:definitionlist/ui:term[1]/@text", list);
		assertXpathEvaluatesTo("2", "count(//ui:definitionlist/ui:term[1]/ui:data)", list);
		assertXpathEvaluatesTo(text1, "normalize-space(//ui:definitionlist/ui:term[1]/ui:data[1])",
				list);
		assertXpathEvaluatesTo(text2, "normalize-space(//ui:definitionlist/ui:term[1]/ui:data[2])",
				list);
		assertXpathEvaluatesTo(term2, "//ui:definitionlist/ui:term[2]/@text", list);
		assertXpathEvaluatesTo("1", "count(//ui:definitionlist/ui:term[2]/ui:data)", list);
		assertXpathEvaluatesTo(text3, "normalize-space(//ui:definitionlist/ui:term[2]/ui:data[1])",
				list);

		list.addTerm(term3, new WText(text4));
		assertSchemaMatch(list);
		assertXpathEvaluatesTo("3", "count(//ui:definitionlist/ui:term)", list);
		assertXpathEvaluatesTo(term1, "//ui:definitionlist/ui:term[1]/@text", list);
		assertXpathEvaluatesTo("2", "count(//ui:definitionlist/ui:term[1]/ui:data)", list);
		assertXpathEvaluatesTo(text1, "normalize-space(//ui:definitionlist/ui:term[1]/ui:data[1])",
				list);
		assertXpathEvaluatesTo(text2, "normalize-space(//ui:definitionlist/ui:term[1]/ui:data[2])",
				list);
		assertXpathEvaluatesTo(term2, "//ui:definitionlist/ui:term[2]/@text", list);
		assertXpathEvaluatesTo("1", "count(//ui:definitionlist/ui:term[2]/ui:data)", list);
		assertXpathEvaluatesTo(text3, "normalize-space(//ui:definitionlist/ui:term[2]/ui:data[1])",
				list);
		assertXpathEvaluatesTo("1", "count(//ui:definitionlist/ui:term[3]/ui:data)", list);
		assertXpathEvaluatesTo(text4, "normalize-space(//ui:definitionlist/ui:term[3]/ui:data[1])",
				list);
	}

	@Test
	public void testRenderedWithMargins() throws IOException, SAXException, XpathException {
		WDefinitionList section = new WDefinitionList();
		assertXpathNotExists("//ui:definitionlist/ui:margin", section);

		Margin margin = new Margin(0);
		section.setMargin(margin);
		assertXpathNotExists("//ui:definitionlist/ui:margin", section);

		margin = new Margin(1);
		section.setMargin(margin);
		assertSchemaMatch(section);
		assertXpathEvaluatesTo("1", "//ui:definitionlist/ui:margin/@all", section);
		assertXpathEvaluatesTo("", "//ui:definitionlist/ui:margin/@north", section);
		assertXpathEvaluatesTo("", "//ui:definitionlist/ui:margin/@east", section);
		assertXpathEvaluatesTo("", "//ui:definitionlist/ui:margin/@south", section);
		assertXpathEvaluatesTo("", "//ui:definitionlist/ui:margin/@west", section);

		margin = new Margin(1, 2, 3, 4);
		section.setMargin(margin);
		assertSchemaMatch(section);
		assertXpathEvaluatesTo("", "//ui:definitionlist/ui:margin/@all", section);
		assertXpathEvaluatesTo("1", "//ui:definitionlist/ui:margin/@north", section);
		assertXpathEvaluatesTo("2", "//ui:definitionlist/ui:margin/@east", section);
		assertXpathEvaluatesTo("3", "//ui:definitionlist/ui:margin/@south", section);
		assertXpathEvaluatesTo("4", "//ui:definitionlist/ui:margin/@west", section);
	}

	@Test
	public void testXssEscaping() throws IOException, SAXException, XpathException {
		WDefinitionList list = new WDefinitionList();

		list.addTerm(getMaliciousAttribute("ui:term"), new WText("dummy"));
		assertSafeContent(list);
	}
}
