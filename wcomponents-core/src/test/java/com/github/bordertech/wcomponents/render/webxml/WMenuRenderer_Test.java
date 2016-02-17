package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.ComponentModel;
import com.github.bordertech.wcomponents.Margin;
import com.github.bordertech.wcomponents.WMenu;
import com.github.bordertech.wcomponents.WMenu.SelectMode;
import com.github.bordertech.wcomponents.WMenuItem;
import java.io.IOException;
import junit.framework.Assert;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * Junit test case for {@link WMenuRenderer}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WMenuRenderer_Test extends AbstractWebXmlRendererTestCase {

	@Test
	public void testRendererCorrectlyConfigured() {
		WMenu menu = new WMenu();
		Assert.assertTrue("Incorrect renderer supplied",
				getWebXmlRenderer(menu) instanceof WMenuRenderer);
	}

	@Test
	public void testDoPaint() throws IOException, SAXException, XpathException {
		WMenu menu = new WMenu();
		menu.add(new WMenuItem(""));

		assertSchemaMatch(menu);
		assertXpathExists("//ui:menu/@id", menu);
		assertXpathEvaluatesTo(menu.getId(), "//ui:menu/@id", menu);
		assertXpathEvaluatesTo("bar", "//ui:menu/@type", menu);
		assertXpathNotExists("//ui:menu/@disabled", menu);
		assertXpathNotExists("//ui:menu/@selectMode", menu);
		assertXpathExists("//ui:menu/ui:menuitem", menu);
		assertXpathNotExists("//ui:menu/ui:separator", menu);

		menu.addSeparator();
		assertSchemaMatch(menu);
		assertXpathExists("//ui:menu/ui:separator", menu);

		menu.setDisabled(true);
		setFlag(menu, ComponentModel.HIDE_FLAG, true);
		assertSchemaMatch(menu);
		assertXpathEvaluatesTo("true", "//ui:menu/@disabled", menu);
		assertXpathEvaluatesTo("true", "//ui:menu/@hidden", menu);

		menu.setSelectMode(SelectMode.SINGLE);
		assertSchemaMatch(menu);
		assertXpathEvaluatesTo("single", "//ui:menu/@selectMode", menu);

		menu.setSelectMode(SelectMode.MULTIPLE);
		assertSchemaMatch(menu);
		assertXpathEvaluatesTo("multiple", "//ui:menu/@selectMode", menu);

		menu.setRows(1);
		assertSchemaMatch(menu);
		assertXpathEvaluatesTo("1", "//ui:menu/@rows", menu);

		menu = new WMenu(WMenu.MenuType.FLYOUT);
		menu.add(new WMenuItem(""));
		assertSchemaMatch(menu);
		assertXpathEvaluatesTo("flyout", "//ui:menu/@type", menu);

		menu = new WMenu(WMenu.MenuType.TREE);
		menu.add(new WMenuItem(""));
		assertSchemaMatch(menu);
		assertXpathEvaluatesTo("tree", "//ui:menu/@type", menu);

		menu = new WMenu(WMenu.MenuType.COLUMN);
		menu.add(new WMenuItem(""));
		assertSchemaMatch(menu);
		assertXpathEvaluatesTo("column", "//ui:menu/@type", menu);
	}

	@Test
	public void testRenderedWithMargins() throws IOException, SAXException, XpathException {
		WMenu menu = new WMenu();
		menu.add(new WMenuItem(""));

		assertXpathNotExists("//ui:menu/ui:margin", menu);

		Margin margin = new Margin(0);
		menu.setMargin(margin);
		assertXpathNotExists("//ui:menu/ui:margin", menu);

		margin = new Margin(1);
		menu.setMargin(margin);
		assertSchemaMatch(menu);
		assertXpathEvaluatesTo("1", "//ui:menu/ui:margin/@all", menu);
		assertXpathEvaluatesTo("", "//ui:menu/ui:margin/@north", menu);
		assertXpathEvaluatesTo("", "//ui:menu/ui:margin/@east", menu);
		assertXpathEvaluatesTo("", "//ui:menu/ui:margin/@south", menu);
		assertXpathEvaluatesTo("", "//ui:menu/ui:margin/@west", menu);

		margin = new Margin(1, 2, 3, 4);
		menu.setMargin(margin);
		assertSchemaMatch(menu);
		assertXpathEvaluatesTo("", "//ui:menu/ui:margin/@all", menu);
		assertXpathEvaluatesTo("1", "//ui:menu/ui:margin/@north", menu);
		assertXpathEvaluatesTo("2", "//ui:menu/ui:margin/@east", menu);
		assertXpathEvaluatesTo("3", "//ui:menu/ui:margin/@south", menu);
		assertXpathEvaluatesTo("4", "//ui:menu/ui:margin/@west", menu);
	}

}
