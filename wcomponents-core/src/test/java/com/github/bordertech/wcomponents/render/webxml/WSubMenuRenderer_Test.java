package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.WMenu;
import com.github.bordertech.wcomponents.WMenu.SelectMode;
import com.github.bordertech.wcomponents.WMenuItem;
import com.github.bordertech.wcomponents.WSubMenu;
import java.io.IOException;
import junit.framework.Assert;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * Junit test case for {@link WSubMenuRenderer}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WSubMenuRenderer_Test extends AbstractWebXmlRendererTestCase {

	/**
	 * A WMenu used in the examples.
	 */
	private WMenu menu;
	/**
	 * A WSubMenu used in the examples.
	 */
	private WSubMenu subMenu;
	/**
	 * Title for the WSubMenu.
	 */
	private static final String SUB_MENU_TEXT = "WSubMenuRenderer_Test.testDoPaint.subMenuText";

	@Before
	public void before() {
		menu = new WMenu();
		subMenu = new WSubMenu(SUB_MENU_TEXT);
		menu.add(subMenu);
		subMenu.add(new WMenuItem("Item"));
	}

	@Test
	public void testRendererCorrectlyConfigured() {
		Assert.assertTrue("Incorrect renderer supplied", getWebXmlRenderer(subMenu) instanceof WSubMenuRenderer);
	}

	@Test
	public void testDoPaint() throws IOException, SAXException, XpathException {
		assertSchemaMatch(menu);
		assertXpathExists("//ui:submenu", menu);
		assertXpathEvaluatesTo(subMenu.getId(), "//ui:submenu/@id", menu);
		assertXpathNotExists("//ui:submenu/@open", menu);
		assertXpathNotExists("//ui:submenu/@disabled", menu);
		assertXpathNotExists("//ui:submenu/@hidden", menu);
		assertXpathExists("//ui:submenu/ui:content/ui:menuitem", menu);
		assertXpathNotExists("//ui:submenu/ui:content/ui:separator", menu);
		assertXpathNotExists("//ui:submenu/@accessKey", menu);
		assertXpathEvaluatesTo("client", "//ui:submenu/@mode", menu);

		subMenu.addSeparator();
		assertSchemaMatch(menu);
		assertXpathExists("//ui:submenu/ui:content/ui:separator", menu);
	}

	@Test
	public void testOpen() throws IOException, SAXException, XpathException {
		subMenu.setOpen(true);
		assertSchemaMatch(menu);
		// Open on first paint
		assertXpathNotExists("//ui:submenu/@open", menu);
		// Closed on second paint
		assertXpathNotExists("//ui:submenu/@open", menu);
	}

	@Test
	public void testDisabled() throws IOException, SAXException, XpathException {
		subMenu.setDisabled(true);
		assertSchemaMatch(menu);
		assertXpathEvaluatesTo("true", "//ui:submenu/@disabled", menu);
	}

	@Test
	public void testHidden() throws IOException, SAXException, XpathException {
		subMenu.setHidden(true);
		assertSchemaMatch(menu);
		assertXpathEvaluatesTo("true", "//ui:submenu/@hidden", menu);
	}

	@Test
	public void testUnusedSelectabled() throws IOException, SAXException, XpathException {
		subMenu.setSelectable(true);
		// selectable no longer written.
		assertSchemaMatch(menu);
		assertXpathNotExists("//ui:submenu/@selectable", menu);
	}

	@Test
	public void testMode() throws IOException, SAXException, XpathException {
		subMenu.setMode(WSubMenu.MenuMode.LAZY);
		assertSchemaMatch(menu);
		assertXpathEvaluatesTo("lazy", "//ui:submenu/@mode", menu);

		subMenu.setMode(WSubMenu.MenuMode.EAGER);
		assertSchemaMatch(menu);
		assertXpathEvaluatesTo("eager", "//ui:submenu/@mode", menu);

		subMenu.setMode(WSubMenu.MenuMode.DYNAMIC);
		assertSchemaMatch(menu);
		assertXpathEvaluatesTo("dynamic", "//ui:submenu/@mode", menu);

		// mode server mapped to mode dynamic as per https://github.com/BorderTech/wcomponents/issues/687
		subMenu.setMode(WSubMenu.MenuMode.SERVER);
		assertSchemaMatch(menu);
		assertXpathEvaluatesTo("dynamic", "//ui:submenu/@mode", menu);
	}

	@Test
	public void testUnusedSelected() throws IOException, SAXException, XpathException {
		menu.setSelectedItem(subMenu);
		// selectable and selected no longer written.
		assertSchemaMatch(menu);
		assertXpathNotExists("//ui:submenu/@selected", menu);
	}

	@Test
	public void testUnusedSelectMode() throws IOException, SAXException, XpathException {
		// SelectMode no longer written.
		subMenu.setSelectMode(SelectMode.SINGLE);
		assertSchemaMatch(menu);
		assertXpathNotExists("//ui:submenu/@selectMode", menu);
		subMenu.setSelectMode(SelectMode.MULTIPLE);
		assertSchemaMatch(menu);
		assertXpathNotExists("//ui:submenu/@selectMode", menu);
	}

	@Test
	public void testOpenTree() throws IOException, SAXException, XpathException {
		menu = new WMenu(WMenu.MenuType.TREE);
		subMenu = new WSubMenu("SubMenu");
		menu.add(subMenu);
		subMenu.add(new WMenuItem("Item"));
		subMenu.setOpen(true);
		assertSchemaMatch(menu);
		assertXpathEvaluatesTo("true", "//ui:submenu/@open", menu);
		assertXpathEvaluatesTo("true", "//ui:submenu/@open", menu);
	}

	@Test
	public void testAccessKey() throws IOException, SAXException, XpathException {
		subMenu.setAccessKey('A');
		assertSchemaMatch(menu);
		assertXpathEvaluatesTo("A", "//ui:submenu/@accessKey", menu);
	}

	@Test
	public void testAccessKeyNotAtSubLevel() throws IOException, SAXException, XpathException {
		WSubMenu nestedSubmenu = new WSubMenu("nested");
		subMenu.add(nestedSubmenu);
		nestedSubmenu.setAccessKey('A');
		assertSchemaMatch(menu);
		assertXpathNotExists("//ui:submenu/@accessKey", menu);
	}

	@Test
	public void testXssEscaping() throws IOException, SAXException, XpathException {
		assertSafeContent(menu);

		subMenu.setToolTip(getMaliciousAttribute("ui:submenu"));
		assertSafeContent(menu);

		subMenu.setAccessibleText(getMaliciousAttribute("ui:submenu"));
		assertSafeContent(menu);
	}
}
