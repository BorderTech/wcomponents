package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.ComponentModel;
import com.github.bordertech.wcomponents.MenuSelectContainer;
import com.github.bordertech.wcomponents.TestAction;
import com.github.bordertech.wcomponents.WMenu;
import com.github.bordertech.wcomponents.WMenuItem;
import com.github.bordertech.wcomponents.WSubMenu;
import java.io.IOException;
import junit.framework.Assert;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * Junit test case for {@link WMenuItemRenderer}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WMenuItemRenderer_Test extends AbstractWebXmlRendererTestCase {

	/**
	 * Text for WMenuItems created in these tests.
	 */
	private final String itemText = "WMenuItemRenderer_Test.testDoPaint.itemText !@#$%^&*()";

	/**
	 * URL for WMenuItems created in these tests.
	 */
	private final String url = "http://localhost/WMenuItemRenderer_Test.testDoPaint.url?a=b&c=d";


	@Test
	public void testRendererCorrectlyConfigured() {
		WMenuItem menuItem = new WMenuItem("");
		Assert.assertTrue("Incorrect renderer supplied",
				getWebXmlRenderer(menuItem) instanceof WMenuItemRenderer);
	}

	@Test
	public void testDoPaintDefaults() throws IOException, SAXException, XpathException {

		WMenuItem item = new WMenuItem(itemText);
		WMenu wrapped = wrapMenuItem(item);

		setActiveContext(createUIContext());

		assertSchemaMatch(wrapped);
		assertXpathExists("//ui:menuitem", wrapped);
		assertXpathEvaluatesTo(itemText, "normalize-space(//ui:menuitem/ui:decoratedlabel)", item);
		assertXpathEvaluatesTo(item.getId(), "//ui:menuitem/@id", item);
		assertXpathNotExists("//ui:menuitem/@url", item);
		assertXpathNotExists("//ui:menuitem/@submit", item);
		assertXpathNotExists("//ui:menuitem/@disabled", item);
		assertXpathNotExists("//ui:menuitem/@accessKey", item);
		assertXpathNotExists("//ui:menuitem/@targetWindow", item);
		assertXpathNotExists("//ui:menuitem/@selectable", item);
		assertXpathNotExists("//ui:menuitem/@role", item);
	}

	@Test
	public void testUrl() throws IOException, SAXException, XpathException {
		// Test with URL
		WMenuItem item = new WMenuItem(itemText, url);
		WMenu wrapped = wrapMenuItem(item);
		assertSchemaMatch(wrapped);
		assertXpathEvaluatesTo(itemText, "normalize-space(//ui:menuitem/ui:decoratedlabel)", item);
		assertXpathUrlEvaluatesTo(url, "//ui:menuitem/@url", item);
		assertXpathNotExists("//ui:menuitem/@submit", item);
	}

	@Test
	public void testAction() throws IOException, SAXException, XpathException {
		// Test with action
		WMenuItem item = new WMenuItem(itemText, new TestAction());
		WMenu wrapped = wrapMenuItem(item);
		assertSchemaMatch(wrapped);
		assertXpathEvaluatesTo(itemText, "normalize-space(//ui:menuitem/ui:decoratedlabel)", item);
		assertXpathNotExists("//ui:menuitem/@url", item);
		assertXpathEvaluatesTo("true", "//ui:menuitem/@submit", item);
	}

	@Test
	public void testTargetWindow() throws IOException, SAXException, XpathException {
		final String targetWindow = "WMenuItemLayout-targetWindow";
		// Test with target window
		WMenuItem item = new WMenuItem(itemText, url);
		item.setTargetWindow(targetWindow);
		WMenu wrapped = wrapMenuItem(item);
		assertSchemaMatch(wrapped);
		assertXpathEvaluatesTo(itemText, "normalize-space(//ui:menuitem/ui:decoratedlabel)", item);
		assertXpathEvaluatesTo(targetWindow, "//ui:menuitem/@targetWindow", item);
	}

	@Test
	public void testDisabled() throws IOException, SAXException, XpathException {
		// Disabled
		WMenuItem item = new WMenuItem(itemText, url);
		WMenu wrapped = wrapMenuItem(item);
		item.setDisabled(true);
		assertSchemaMatch(wrapped);
		assertXpathEvaluatesTo("true", "//ui:menuitem/@disabled", item);
		// disabled menu should provide disabled menu item
		item = new WMenuItem(itemText, url);
		wrapped = wrapMenuItem(item);
		wrapped.setDisabled(true);
		assertSchemaMatch(wrapped);
		assertXpathEvaluatesTo("true", "//ui:menuitem/@disabled", item);
	}

	@Test
	public void testAccessKey() throws IOException, SAXException, XpathException {
		// AccessKey
		WMenuItem item = new WMenuItem(itemText, url);
		WMenu wrapped = wrapMenuItem(item);
		item.setAccessKey('A');
		assertSchemaMatch(wrapped);
		assertXpathEvaluatesTo("A", "//ui:menuitem/@accessKey", item);
		// no access key if nested
		WSubMenu sub = new WSubMenu("sub");
		wrapped.add(sub);
		item = new WMenuItem(itemText, url);
		sub.add(item);
		item.setAccessKey('A');
		assertSchemaMatch(wrapped);
		assertXpathNotExists("//ui:menuitem/@accessKey", item);
	}

	@Test
	public void testHidden() throws IOException, SAXException, XpathException {
		WMenuItem item = new WMenuItem(itemText, url);
		WMenu wrapped = wrapMenuItem(item);
		setFlag(item, ComponentModel.HIDE_FLAG, true);
		assertSchemaMatch(wrapped);
		assertXpathEvaluatesTo("true", "//ui:menuitem/@hidden", item);
	}

	@Test
	public void testSelection() throws IOException, SAXException, XpathException {
		// Test selection
		WMenuItem item = new WMenuItem(itemText);
		WMenu wrapped = wrapMenuItem(item);
		wrapped.setSelectionMode(MenuSelectContainer.SelectionMode.SINGLE);
		assertXpathNotExists("//ui:menuitem/@selected", item);
		wrapped.setSelectedItem(item);
		assertSchemaMatch(wrapped);
		assertXpathEvaluatesTo("true", "//ui:menuitem/@selected", item);
	}

	@Test
	public void testSelectability() throws IOException, SAXException, XpathException {
		// Selectability - output as @role
		WMenuItem item = new WMenuItem(itemText);
		WMenu wrapped = wrapMenuItem(item);
		wrapped.setSelectionMode(MenuSelectContainer.SelectionMode.SINGLE);
		// default selectability is selectable
		assertXpathEvaluatesTo("menuitemradio", "//ui:menuitem/@role", item);
		item = new WMenuItem(itemText);
		wrapped.add(item);
		item.setSelectability(Boolean.FALSE);
		assertSchemaMatch(wrapped);
		assertXpathNotExists("//ui:menuitem/@role", item);
		item = new WMenuItem(itemText);
		wrapped.add(item);
		item.setSelectability(Boolean.TRUE);
		assertSchemaMatch(wrapped);
		assertXpathEvaluatesTo("menuitemradio", "//ui:menuitem/@role", item);

		item = new WMenuItem(itemText);
		wrapped = wrapMenuItem(item);
		wrapped.setSelectionMode(MenuSelectContainer.SelectionMode.MULTIPLE);
		//default selectability is selectable and so role is menuitemcheckbox
		assertXpathEvaluatesTo("menuitemcheckbox", "//ui:menuitem/@role", item);
		item = new WMenuItem(itemText);
		wrapped.add(item);
		item.setSelectability(Boolean.FALSE);
		assertXpathNotExists("//ui:menuitem/@role", item);
		item = new WMenuItem(itemText);
		wrapped.add(item);
		item.setSelectability(Boolean.TRUE);
		assertXpathEvaluatesTo("menuitemcheckbox", "//ui:menuitem/@role", item);
	}

	@Test
	public void testRoleWhenSelectedNotSelectable() throws IOException, SAXException, XpathException {
		/*
		 * A WMenuItem may be set as selected even if it is not in a selection container. This is a flaw in an
		 * old part of the WComponents API.
		 */
		WMenuItem item = new WMenuItem(itemText);
		WMenu wrapped = wrapMenuItem(item);
		wrapped.setSelectionMode(MenuSelectContainer.SelectionMode.SINGLE);
		assertXpathNotExists("//ui:menuitem/@selected", item);
		wrapped.setSelectedItem(item);
		assertSchemaMatch(wrapped);
		assertXpathEvaluatesTo("true", "//ui:menuitem/@selected", item);
	}

	@Test
	public void testXssEscaping() throws IOException, SAXException, XpathException {
		WMenu menu = new WMenu();
		WMenuItem item = new WMenuItem(getMaliciousAttribute());
		menu.add(item);

		assertSafeContent(menu);

		item.setToolTip(getMaliciousAttribute());
		assertSafeContent(menu);

		item.setAccessibleText(getMaliciousAttribute());
		assertSafeContent(menu);

		item.setUrl(getMaliciousAttribute());
		assertSafeContent(menu);
	}

	/**
	 * Menu items can not be used stand-alone, so we must test them through a WMenu.
	 *
	 * @param item the menu item
	 * @return the menu item wrapped
	 */
	private WMenu wrapMenuItem(final WMenuItem item) {
		WMenu menu = new WMenu();
		menu.add(item);
		return menu;
	}

}
