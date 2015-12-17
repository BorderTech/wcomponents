package com.github.bordertech.wcomponents;

import org.junit.Assert;
import org.junit.Test;

/**
 * WMenuItemGroup_Test - Unit tests for {@link WMenuItemGroup}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WMenuItemGroup_Test extends AbstractWComponentTestCase {

	@Test
	public void testConstructor() {
		final String heading = "WMenuItemGroup_Test.testConstructor.heading";
		WMenuItemGroup group = new WMenuItemGroup(heading);
		Assert.assertEquals("Incorrect group heading", heading, group.getHeadingText());
	}

	@Test
	public void testHeadingTextAccessors() {
		final String heading1 = "WMenuItemGroup_Test.testGetHeadingText.heading1";
		final String heading2 = "WMenuItemGroup_Test.testGetHeadingText.heading2";
		final String heading3 = "WMenuItemGroup_Test.testGetHeadingText.heading3";

		WMenuItemGroup group = new WMenuItemGroup(heading1);
		assertAccessorsCorrect(group, "headingText", heading1, heading2, heading3);
	}

	@Test
	public void testDisabledAccessors() {
		assertAccessorsCorrect(new WMenuItemGroup("dummy"), "disabled", false, true, false);
	}

	@Test
	public void testAddSeparator() {
		WMenuItemGroup group = new WMenuItemGroup("dummy");
		group.setLocked(true);

		setActiveContext(createUIContext());
		group.addSeparator();
		Assert.assertEquals("Should have added separator child", 1, group.getMenuItems().size());
		Assert.assertTrue("Should have separator child", group.getMenuItems().get(0) instanceof WSeparator);

		resetContext();
		Assert.assertEquals("Should only have label as child after reset", 0, group.getMenuItems().size());
	}

	@Test
	public void testAddMenuItem() {
		WMenuItemGroup group = new WMenuItemGroup("dummy");
		group.setLocked(true);

		setActiveContext(createUIContext());
		WMenuItem menuItem = new WMenuItem("dummyItem");
		group.addMenuItem(menuItem);
		Assert.assertEquals("Should have added menu item", 1, group.getMenuItems().size());
		Assert.assertSame("Incorrect menu item added", menuItem, group.getMenuItems().get(0));

		resetContext();
		Assert.assertEquals("Should only have label as child after reset", 0, group.getMenuItems().size());
	}

	@Test
	public void testAddSubMenu() {
		WMenuItemGroup group = new WMenuItemGroup("dummy");
		group.setLocked(true);

		setActiveContext(createUIContext());
		WSubMenu subMenu = new WSubMenu("dummyMenu");
		group.add(subMenu);
		Assert.assertEquals("Should have added sub-menu", 1, group.getMenuItems().size());
		Assert.assertSame("Incorrect sub-menu added", subMenu, group.getMenuItems().get(0));

		resetContext();
		Assert.assertEquals("Should only have label as child after reset", 0, group.getMenuItems().size());
	}
}
