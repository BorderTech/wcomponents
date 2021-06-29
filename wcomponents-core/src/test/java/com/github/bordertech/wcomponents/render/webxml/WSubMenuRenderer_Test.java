package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.MenuSelectContainer.SelectionMode;
import com.github.bordertech.wcomponents.WMenu;
import com.github.bordertech.wcomponents.WMenu.SelectMode;
import com.github.bordertech.wcomponents.WMenuItem;
import com.github.bordertech.wcomponents.WSubMenu;
import java.io.IOException;
import org.junit.Assert;
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
	public void testDoPaint() throws IOException, SAXException {
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
	public void testOpen() throws IOException, SAXException {
		subMenu.setOpen(true);
		assertSchemaMatch(menu);
		// Open on first paint
		assertXpathNotExists("//ui:submenu/@open", menu);
		// Closed on second paint
		assertXpathNotExists("//ui:submenu/@open", menu);
	}

	@Test
	public void testDisabled() throws IOException, SAXException {
		subMenu.setDisabled(true);
		assertSchemaMatch(menu);
		assertXpathEvaluatesTo("true", "//ui:submenu/@disabled", menu);
	}

	@Test
	public void testHidden() throws IOException, SAXException {
		subMenu.setHidden(true);
		assertSchemaMatch(menu);
		assertXpathEvaluatesTo("true", "//ui:submenu/@hidden", menu);
	}

	@Test
	public void testUnusedSelectabled() throws IOException, SAXException {
		subMenu.setSelectable(true);
		// selectable no longer written.
		assertSchemaMatch(menu);
		assertXpathNotExists("//ui:submenu/@selectable", menu);
	}

	@Test
	public void testMode() throws IOException, SAXException {
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
	public void testUnusedSelected() throws IOException, SAXException {
		menu.setSelectedItem(subMenu);
		// selectable and selected no longer written.
		assertSchemaMatch(menu);
		assertXpathNotExists("//ui:submenu/@selected", menu);
	}

	@Test
	public void testSelectionMode() throws IOException, SAXException {
		// @selectMode no longer written
		subMenu.setSelectionMode(SelectionMode.SINGLE);
		assertSchemaMatch(menu);
		assertXpathNotExists("//ui:submenu/@selectMode", menu);
		subMenu.setSelectionMode(SelectionMode.MULTIPLE);
		assertSchemaMatch(menu);
		assertXpathNotExists("//ui:submenu/@selectMode", menu);
	}

	@Test
	public void testUnusedSelectMode() throws IOException, SAXException {
		// SelectMode no longer written.
		subMenu.setSelectMode(SelectMode.SINGLE);
		assertSchemaMatch(menu);
		assertXpathNotExists("//ui:submenu/@selectMode", menu);
		subMenu.setSelectMode(SelectMode.MULTIPLE);
		assertSchemaMatch(menu);
		assertXpathNotExists("//ui:submenu/@selectMode", menu);
	}

	@Test
	public void testOpenTree() throws IOException, SAXException {
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
	public void testAccessKey() throws IOException, SAXException {
		subMenu.setAccessKey('A');
		assertSchemaMatch(menu);
		assertXpathEvaluatesTo("A", "//ui:submenu/@accessKey", menu);
	}

	@Test
	public void testAccessKeyNotAtSubLevel() throws IOException, SAXException {
		WSubMenu nestedSubmenu = new WSubMenu("nested");
		subMenu.add(nestedSubmenu);
		nestedSubmenu.setAccessKey('A');
		assertSchemaMatch(menu);
		assertXpathNotExists("//ui:submenu/@accessKey", menu);
	}

	@Test
	public void testXssEscaping() throws IOException, SAXException {
		assertSafeContent(menu);

		subMenu.setToolTip(getMaliciousAttribute("ui:submenu"));
		assertSafeContent(menu);

		subMenu.setAccessibleText(getMaliciousAttribute("ui:submenu"));
		assertSafeContent(menu);
	}
}
