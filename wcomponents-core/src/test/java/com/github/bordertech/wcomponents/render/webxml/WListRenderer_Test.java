package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.Margin;
import com.github.bordertech.wcomponents.WList;
import com.github.bordertech.wcomponents.WText;
import java.io.IOException;
import java.util.Arrays;
import junit.framework.Assert;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * Junit test case for {@link WListRenderer}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WListRenderer_Test extends AbstractWebXmlRendererTestCase {

	@Test
	public void testLayoutCorrectlyConfigured() {
		WList component = new WList(WList.Type.STRIPED);
		Assert.assertTrue("Incorrect layout supplied",
				getWebXmlRenderer(component) instanceof WListRenderer);
	}

	@Test
	public void testRenderedFormatEmptyNoBorder() throws IOException, SAXException, XpathException {
		// empty list, no border
		WList list = new WList(WList.Type.STRIPED);
		list.setRepeatedComponent(new WText());
		assertSchemaMatch(list);
		assertXpathEvaluatesTo("striped", "//ui:listlayout/@type", list);
		assertXpathNotExists("//ui:listlayout/@separator", list);
		assertXpathNotExists("//ui:panel/@type", list);

		list.setType(WList.Type.FLAT);
		assertXpathEvaluatesTo("flat", "//ui:listlayout/@type", list);
		assertXpathNotExists("//ui:listlayout/@separator", list);
		assertXpathNotExists("//ui:panel/@type", list);

		list.setType(WList.Type.STACKED);
		assertXpathEvaluatesTo("stacked", "//ui:listlayout/@type", list);
		assertXpathNotExists("//ui:listlayout/@separator", list);
		assertXpathNotExists("//ui:panel/@type", list);

		list.setSeparator(WList.Separator.NONE);
		assertXpathEvaluatesTo("stacked", "//ui:listlayout/@type", list);
		assertXpathNotExists("//ui:listlayout/@separator", list);
		assertXpathNotExists("//ui:panel/@type", list);

		list.setSeparator(WList.Separator.BAR);
		assertXpathEvaluatesTo("stacked", "//ui:listlayout/@type", list);
		assertXpathEvaluatesTo("bar", "//ui:listlayout/@separator", list);
		assertXpathNotExists("//ui:panel/@type", list);

		list.setSeparator(WList.Separator.DOT);
		assertXpathEvaluatesTo("stacked", "//ui:listlayout/@type", list);
		assertXpathEvaluatesTo("dot", "//ui:listlayout/@separator", list);
		assertXpathNotExists("//ui:listlayout[@type='box']", list);
	}

	@Test
	public void testRenderedFormatEmptyWithBorder() throws IOException, SAXException, XpathException {
		// empty list, with border
		WList list = new WList(WList.Type.STRIPED);
		list.setRenderBorder(true);
		list.setRepeatedComponent(new WText());

		assertSchemaMatch(list);
		assertXpathEvaluatesTo("striped", "//ui:listlayout/@type", list);
		assertXpathEvaluatesTo("box", "//ui:panel/@type", list);
		assertXpathNotExists("//ui:listlayout/@separator", list);
	}

	@Test
	public void testRenderedFormatNoBorder() throws IOException, SAXException {
		// non-empty list, no border
		WList list = new WList(WList.Type.STRIPED);
		list.setRepeatedComponent(new WText());

		setActiveContext(createUIContext());
		list.setData(Arrays.asList(new String[]{"row1", "row2", "row3"}));

		assertSchemaMatch(list);
	}

	@Test
	public void testRenderedFormatWithBorder() throws IOException, SAXException {
		// non-empty list, no border
		WList list = new WList(WList.Type.STRIPED);
		list.setRenderBorder(true);
		list.setRepeatedComponent(new WText());
		list.setData(Arrays.asList(new String[]{"row1", "row2", "row3"}));

		assertSchemaMatch(list);
	}

	@Test
	public void testRenderedFormatHgapVgap() throws IOException, SAXException, XpathException {
		// No hgap, vgap
		WList list = new WList(WList.Type.STRIPED);
		list.setRepeatedComponent(new WText());
		list.setData(Arrays.asList(new String[]{"row1", "row2", "row3"}));
		assertSchemaMatch(list);
		assertXpathNotExists("//ui:panel/ui:listlayout/@hgap", list);
		assertXpathNotExists("//ui:panel/ui:listlayout/@vgap", list);

		// With hgap, vgap
		list = new WList(WList.Type.STRIPED, 1, 2);
		list.setRepeatedComponent(new WText());
		list.setData(Arrays.asList(new String[]{"row1", "row2", "row3"}));
		assertSchemaMatch(list);
		assertXpathEvaluatesTo("1", "//ui:panel/ui:listlayout/@hgap", list);
		assertXpathEvaluatesTo("2", "//ui:panel/ui:listlayout/@vgap", list);
	}

	@Test
	public void testRenderedWithMargins() throws IOException, SAXException, XpathException {
		WList list = new WList(WList.Type.STRIPED);
		list.setRepeatedComponent(new WText());
		assertXpathNotExists("//ui:panel/ui:margin", list);

		Margin margin = new Margin(0);
		list.setMargin(margin);
		assertXpathNotExists("//ui:panel/ui:margin", list);

		margin = new Margin(1);
		list.setMargin(margin);
		assertSchemaMatch(list);
		assertXpathEvaluatesTo("1", "//ui:panel/ui:margin/@all", list);
		assertXpathEvaluatesTo("", "//ui:panel/ui:margin/@north", list);
		assertXpathEvaluatesTo("", "//ui:panel/ui:margin/@east", list);
		assertXpathEvaluatesTo("", "//ui:panel/ui:margin/@south", list);
		assertXpathEvaluatesTo("", "//ui:panel/ui:margin/@west", list);

		margin = new Margin(1, 2, 3, 4);
		list.setMargin(margin);
		assertSchemaMatch(list);
		assertXpathEvaluatesTo("", "//ui:panel/ui:margin/@all", list);
		assertXpathEvaluatesTo("1", "//ui:panel/ui:margin/@north", list);
		assertXpathEvaluatesTo("2", "//ui:panel/ui:margin/@east", list);
		assertXpathEvaluatesTo("3", "//ui:panel/ui:margin/@south", list);
		assertXpathEvaluatesTo("4", "//ui:panel/ui:margin/@west", list);
	}

}
