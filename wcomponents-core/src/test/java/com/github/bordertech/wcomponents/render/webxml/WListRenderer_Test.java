package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.Margin;
import com.github.bordertech.wcomponents.Size;
import com.github.bordertech.wcomponents.WList;
import com.github.bordertech.wcomponents.WText;
import com.github.bordertech.wcomponents.util.SpaceUtil;
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
 * @author Mark Reeves
 * @since 1.0.0
 */
public class WListRenderer_Test extends AbstractWebXmlRendererTestCase {

	/**
	 * A reusable gap value.
	 */
	private static final Size GAP = Size.SMALL;

	/**
	 * A different reusable gap value. This is used to differentiate the (now deprecated) hgap and vgap properties.
	 */
	private static final Size BIG_GAP = Size.LARGE;

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

		for (WList.Type t : WList.Type.values()) {
			list.setType(t);
			assertSchemaMatch(list);
			assertXpathEvaluatesTo(t.toString().toLowerCase(), "//ui:listlayout/@type", list);
			assertXpathNotExists("//ui:listlayout/@separator", list);
			assertXpathNotExists("//ui:panel/@type", list);
		}

		for (WList.Separator s : WList.Separator.values()) {
			list.setSeparator(s);
			assertSchemaMatch(list);
			if (s == WList.Separator.NONE) {
				assertXpathNotExists("//ui:listlayout/@separator", list);
			} else {
				assertXpathEvaluatesTo(s.toString().toLowerCase(), "//ui:listlayout/@separator", list);
			}
		}
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
	public void testRenderedFormaGap() throws IOException, SAXException, XpathException {
		// No hgap, vgap
		WList list = new WList(WList.Type.STRIPED);
		list.setRepeatedComponent(new WText());
		list.setData(Arrays.asList(new String[]{"row1", "row2", "row3"}));
		assertSchemaMatch(list);
		assertXpathNotExists("//ui:panel/ui:listlayout/@gap", list);

		// With gap
		list = new WList(WList.Type.STRIPED, GAP);
		list.setRepeatedComponent(new WText());
		list.setData(Arrays.asList(new String[]{"row1", "row2", "row3"}));
		assertSchemaMatch(list);
		assertXpathEvaluatesTo(String.valueOf(GAP), "//ui:panel/ui:listlayout/@gap", list);
	}


	@Test
	public void testRenderedWithMargins() throws IOException, SAXException, XpathException {
		WList list = new WList(WList.Type.STRIPED);
		list.setRepeatedComponent(new WText());
		assertXpathNotExists("//ui:panel/ui:margin", list);

		Margin margin = new Margin(0);
		list.setMargin(margin);
		assertXpathNotExists("//ui:panel/ui:margin", list);

		margin = new Margin(Size.SMALL);
		list.setMargin(margin);
		assertSchemaMatch(list);
		assertXpathEvaluatesTo("sm", "//ui:panel/ui:margin/@all", list);
		assertXpathEvaluatesTo("", "//ui:panel/ui:margin/@north", list);
		assertXpathEvaluatesTo("", "//ui:panel/ui:margin/@east", list);
		assertXpathEvaluatesTo("", "//ui:panel/ui:margin/@south", list);
		assertXpathEvaluatesTo("", "//ui:panel/ui:margin/@west", list);

		margin = new Margin(Size.SMALL, Size.MEDIUM, Size.LARGE, Size.XL);
		list.setMargin(margin);
		assertSchemaMatch(list);
		assertXpathEvaluatesTo("", "//ui:panel/ui:margin/@all", list);
		assertXpathEvaluatesTo("sm", "//ui:panel/ui:margin/@north", list);
		assertXpathEvaluatesTo("med", "//ui:panel/ui:margin/@east", list);
		assertXpathEvaluatesTo("lg", "//ui:panel/ui:margin/@south", list);
		assertXpathEvaluatesTo("xl", "//ui:panel/ui:margin/@west", list);
	}



	// deprecated hgap vgap constructor render
	@Test
	public void testRenderedFormatHgapVgap() throws IOException, SAXException, XpathException {
		WList list;

		for (WList.Type t : WList.Type.values()) {
			list = new WList(t, SpaceUtil.sizeToInt(GAP), SpaceUtil.sizeToInt(BIG_GAP));
			list.setRepeatedComponent(new WText());
			list.setData(Arrays.asList(new String[]{"row1", "row2", "row3"}));
			assertSchemaMatch(list);

			if (t == WList.Type.FLAT) {
				assertXpathEvaluatesTo(GAP.toString(), "//ui:panel/ui:listlayout/@gap", list);
			} else {
				assertXpathEvaluatesTo(BIG_GAP.toString(), "//ui:panel/ui:listlayout/@gap", list);
			}
		}
	}

}
