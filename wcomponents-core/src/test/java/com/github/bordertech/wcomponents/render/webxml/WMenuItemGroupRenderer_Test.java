package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WMenu;
import com.github.bordertech.wcomponents.WMenuItemGroup;
import java.io.IOException;
import junit.framework.Assert;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * Junit test case for {@link WMenuItemGroupRenderer}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WMenuItemGroupRenderer_Test extends AbstractWebXmlRendererTestCase {

	@Test
	public void testRendererCorrectlyConfigured() {
		WMenuItemGroup menuItemGroup = new WMenuItemGroup("");
		Assert.assertTrue("Incorrect renderer supplied",
				getWebXmlRenderer(menuItemGroup) instanceof WMenuItemGroupRenderer);
	}

	@Test
	public void testDoPaint() throws IOException, SAXException, XpathException {
		String groupName = "WMenuItemGroupRenderer_Test.testDoPaint.groupName";

		WMenuItemGroup menuGroup = new WMenuItemGroup(groupName);
		WComponent wrapped = wrapMenuGroup(menuGroup);

		setActiveContext(createUIContext());

		assertXpathExists("//ui:menuGroup", wrapped);
		assertXpathEvaluatesTo(groupName, "normalize-space(//ui:menuGroup/ui:decoratedLabel)",
				wrapped);
		assertXpathEvaluatesTo(menuGroup.getId(), "//ui:menuGroup/@id", wrapped);
		assertXpathNotExists("//ui:menuGroup/ui:submenu", wrapped);
		assertXpathNotExists("//ui:menuGroup/ui:menuItem", wrapped);
		assertXpathNotExists("//ui:menuGroup/ui:separator", wrapped);

		menuGroup.addSeparator();
		assertXpathExists("//ui:menuGroup/ui:separator", wrapped);
	}

	/**
	 * Menu group can not be used stand-alone, so we must test them through a WMenu.
	 *
	 * @param menuGroup the menu group to wrap
	 *
	 * @return the wrapped menu group
	 */
	private WComponent wrapMenuGroup(final WMenuItemGroup menuGroup) {
		WMenu menu = new WMenu();
		menu.add(menuGroup);
		return menu;
	}
}
