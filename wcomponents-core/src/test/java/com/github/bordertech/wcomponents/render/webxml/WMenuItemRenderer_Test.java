package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.ComponentModel;
import com.github.bordertech.wcomponents.TestAction;
import com.github.bordertech.wcomponents.WMenu;
import com.github.bordertech.wcomponents.WMenuItem;
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

	@Test
	public void testRendererCorrectlyConfigured() {
		WMenuItem menuItem = new WMenuItem("");
		Assert.assertTrue("Incorrect renderer supplied",
				getWebXmlRenderer(menuItem) instanceof WMenuItemRenderer);
	}

	@Test
	public void testDoPaint() throws IOException, SAXException, XpathException {
		final String itemText = "WMenuItemRenderer_Test.testDoPaint.itemText !@#$%^&*()";
		final String url = "http://localhost/WMenuItemRenderer_Test.testDoPaint.url?a=b&c=d";
		final String targetWindow = "WMenuItemLayout-targetWindow";

		WMenuItem item = new WMenuItem(itemText);
		WMenu wrapped = wrapMenuItem(item);

		setActiveContext(createUIContext());

		assertSchemaMatch(wrapped);
		assertXpathExists("//ui:menuItem", wrapped);
		assertXpathEvaluatesTo(itemText, "normalize-space(//ui:menuItem/ui:decoratedLabel)", item);
		assertXpathEvaluatesTo(item.getId(), "//ui:menuItem/@id", item);
		assertXpathNotExists("//ui:menuItem/@url", item);
		assertXpathNotExists("//ui:menuItem/@submit", item);
		assertXpathNotExists("//ui:menuItem/@disabled", item);
		assertXpathNotExists("//ui:menuItem/@accessKey", item);
		assertXpathNotExists("//ui:menuItem/@targetWindow", item);
		assertXpathNotExists("//ui:menuItem/@selectable", item);

		item.setDisabled(true);
		assertXpathEvaluatesTo("true", "//ui:menuItem/@disabled", item);

		// Test with URL
		item = new WMenuItem(itemText, url);
		wrapped = wrapMenuItem(item);
		assertSchemaMatch(wrapped);
		assertXpathEvaluatesTo(itemText, "normalize-space(//ui:menuItem/ui:decoratedLabel)", item);
		assertXpathEvaluatesTo(url, "//ui:menuItem/@url", item);
		assertXpathNotExists("//ui:menuItem/@submit", item);

		// Test with action
		item = new WMenuItem(itemText, new TestAction());

		wrapped = wrapMenuItem(item);
		assertSchemaMatch(wrapped);
		assertXpathEvaluatesTo(itemText, "normalize-space(//ui:menuItem/ui:decoratedLabel)", item);
		assertXpathNotExists("//ui:menuItem/@url", item);
		assertXpathEvaluatesTo("true", "//ui:menuItem/@submit", item);

		// Test with target window
		item = new WMenuItem(itemText, url);
		item.setTargetWindow(targetWindow);
		wrapped = wrapMenuItem(item);
		assertSchemaMatch(wrapped);
		assertXpathEvaluatesTo(itemText, "normalize-space(//ui:menuItem/ui:decoratedLabel)", item);
		assertXpathEvaluatesTo(targetWindow, "//ui:menuItem/@targetWindow", item);

		// Test selection
		item = new WMenuItem(itemText, url);
		wrapped = wrapMenuItem(item);
		assertXpathNotExists("//ui:menuItem/@selected", item);
		assertXpathNotExists("//ui:menuItem/@selected", item);

		wrapped.setSelectedItem(item);
		assertSchemaMatch(wrapped);
		assertXpathEvaluatesTo("true", "//ui:menuItem/@selected", item);

		item.setAccessKey('A');
		item.setDisabled(true);
		setFlag(item, ComponentModel.HIDE_FLAG, true);
		item.setSelectable(true);
		assertSchemaMatch(wrapped);
		assertXpathEvaluatesTo("A", "//ui:menuItem/@accessKey", item);
		assertXpathEvaluatesTo("true", "//ui:menuItem/@disabled", item);
		assertXpathEvaluatesTo("true", "//ui:menuItem/@hidden", item);
		assertXpathEvaluatesTo("true", "//ui:menuItem/@selectable", item);

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
