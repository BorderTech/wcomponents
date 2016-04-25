package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.ComponentModel;
import com.github.bordertech.wcomponents.WMenu;
import com.github.bordertech.wcomponents.WMenu.SelectMode;
import com.github.bordertech.wcomponents.WMenuItem;
import com.github.bordertech.wcomponents.WSubMenu;
import java.io.IOException;
import junit.framework.Assert;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * Junit test case for {@link WSubMenuRenderer}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WSubMenuRenderer_Test extends AbstractWebXmlRendererTestCase {

	@Test
	public void testRendererCorrectlyConfigured() {
		WSubMenu subMenu = new WSubMenu("");
		Assert.assertTrue("Incorrect renderer supplied",
				getWebXmlRenderer(subMenu) instanceof WSubMenuRenderer);
	}

	@Test
	public void testDoPaint() throws IOException, SAXException, XpathException {
		final String subMenuText = "WSubMenuRenderer_Test.testDoPaint.subMenuText";

		WMenu menu = new WMenu();
		WSubMenu subMenu = new WSubMenu(subMenuText);
		menu.add(subMenu);
		subMenu.add(new WMenuItem(""));

		assertSchemaMatch(menu);
		assertXpathExists("//ui:submenu", menu);
		assertXpathEvaluatesTo(subMenu.getId(), "//ui:submenu/@id", menu);
		assertXpathNotExists("//ui:submenu/@open", menu);
		assertXpathNotExists("//ui:submenu/@disabled", menu);
		assertXpathNotExists("//ui:submenu/@hidden", menu);
		assertXpathNotExists("//ui:submenu/@selectable", menu);
		assertXpathNotExists("//ui:submenu/@selected", menu);
		assertXpathNotExists("//ui:submenu/@accessKey", menu);
		assertXpathNotExists("//ui:submenu/@selectMode", menu);
		assertXpathExists("//ui:submenu/ui:content/ui:menuitem", menu);
		assertXpathNotExists("//ui:submenu/ui:content/ui:separator", menu);
		assertXpathEvaluatesTo("client", "//ui:submenu/@mode", menu);

		subMenu.addSeparator();
		assertSchemaMatch(menu);
		assertXpathExists("//ui:submenu/ui:content/ui:separator", menu);

		subMenu.setOpen(true);
		subMenu.setDisabled(true);
		setFlag(subMenu, ComponentModel.HIDE_FLAG, true);
		subMenu.setSelectable(true);
		menu.setSelectedItem(subMenu);
		subMenu.setAccessKey('A');
		assertSchemaMatch(menu);
		assertXpathEvaluatesTo("true", "//ui:submenu/@open", menu);
		assertXpathEvaluatesTo("true", "//ui:submenu/@disabled", menu);
		assertXpathEvaluatesTo("true", "//ui:submenu/@hidden", menu);
		assertXpathEvaluatesTo("true", "//ui:submenu/@selectable", menu);
		assertXpathEvaluatesTo("true", "//ui:submenu/@selected", menu);
		assertXpathEvaluatesTo("A", "//ui:submenu/@accessKey", menu);

		subMenu.setSelectMode(SelectMode.SINGLE);
		assertSchemaMatch(menu);
		assertXpathEvaluatesTo("single", "//ui:submenu/@selectMode", menu);

		subMenu.setSelectMode(SelectMode.MULTIPLE);
		assertSchemaMatch(menu);
		assertXpathEvaluatesTo("multiple", "//ui:submenu/@selectMode", menu);

		subMenu.setMode(WSubMenu.MenuMode.LAZY);
		assertSchemaMatch(menu);
		assertXpathEvaluatesTo("lazy", "//ui:submenu/@mode", menu);

		subMenu.setMode(WSubMenu.MenuMode.EAGER);
		assertSchemaMatch(menu);
		assertXpathEvaluatesTo("eager", "//ui:submenu/@mode", menu);

		subMenu.setMode(WSubMenu.MenuMode.DYNAMIC);
		assertSchemaMatch(menu);
		assertXpathEvaluatesTo("dynamic", "//ui:submenu/@mode", menu);

		subMenu.setMode(WSubMenu.MenuMode.SERVER);
		assertSchemaMatch(menu);
		assertXpathEvaluatesTo("server", "//ui:submenu/@mode", menu);
	}

	@Test
	public void testXssEscaping() throws IOException, SAXException, XpathException {
		WMenu menu = new WMenu();
		WSubMenu subMenu = new WSubMenu(getMaliciousContent());
		menu.add(subMenu);
		subMenu.add(new WMenuItem("dummy"));

		assertSafeContent(menu);

		subMenu.setToolTip(getMaliciousAttribute("ui:submenu"));
		assertSafeContent(menu);

		subMenu.setAccessibleText(getMaliciousAttribute("ui:submenu"));
		assertSafeContent(menu);
	}
}
