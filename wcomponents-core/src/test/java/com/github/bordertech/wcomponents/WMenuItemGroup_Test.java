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
	public void testConstructor1() {
		final String heading = "WMenuItemGroup_Test.testConstructor.heading";
		WMenuItemGroup group = new WMenuItemGroup(heading);
		Assert.assertEquals("Incorrect group heading", heading, group.getHeadingText());
	}

	@Test
	public void testConstructor2() {
		final WDecoratedLabel label = new WDecoratedLabel("test");
		WMenuItemGroup group = new WMenuItemGroup(label);
		Assert.assertEquals("Incorrect label returned", label, group.getDecoratedLabel());
	}

	@Test
	public void testHeadingTextAccessors() {
		assertAccessorsCorrect(new WMenuItemGroup("A"), "headingText", "A", "B", "C");
	}

	@Test
	public void testDisabledAccessors() {
		assertAccessorsCorrect(new WMenuItemGroup("dummy"), "disabled", false, true, false);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddGroupItemException() {
		new WMenuItemGroup("dummy").addMenuItem(new WMenuItemGroup("nested"));
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

	@Test
	public void testAddItem() {
		WMenuItemGroup group = new WMenuItemGroup("dummy");
		WMenuItem menuItem = new WMenuItem("item");
		group.addMenuItem(menuItem);
		Assert.assertTrue("Menu item should have been added to menu bar", group.getMenuItems().contains(menuItem));
	}

	@Test
	public void testRemoveItem() {

		WMenuItemGroup group = new WMenuItemGroup("dummy");
		WMenuItem menuItem = new WMenuItem("item");

		junit.framework.Assert.assertTrue("Menu item list should be empty by default", group.getMenuItems().isEmpty());

		// Add item
		group.add(menuItem);
		junit.framework.Assert.assertTrue("Menu item should have been added to menu bar", group.getMenuItems().contains(menuItem));

		// Remove item
		group.removeMenuItem(menuItem);
		junit.framework.Assert.assertTrue("Menu item list should be empty after remove item", group.getMenuItems().isEmpty());
	}

	@Test
	public void testRemoveAllItems() {

		WMenuItemGroup group = new WMenuItemGroup("dummy");
		Assert.assertTrue("Menu item list should be empty by default", group.getMenuItems().isEmpty());

		// Add item
		WMenuItem menuItem = new WMenuItem("item");
		group.add(menuItem);
		junit.framework.Assert.assertTrue("Menu item should have been added to menu bar", group.getMenuItems().contains(menuItem));

		// Remove all items
		group.removeAllMenuItems();
		Assert.assertTrue("Menu item list should be empty after remove item", group.getMenuItems().isEmpty());
	}

	@Test
	public void testGetItems() {
		WMenuItemGroup group = new WMenuItemGroup("dummy");
		WMenuItem menuItem = new WMenuItem("item");
		group.add(menuItem);

		// Add submenu with a child item
		WSubMenu subMenu = new WSubMenu("sub");
		group.add(subMenu);
		WMenuItem subItem = new WMenuItem("subItem");
		subMenu.add(subItem);

		Assert.assertEquals("Items list should have two items", 2, group.getMenuItems().size());
		Assert.assertTrue("Items list should contain the menu item", group.getMenuItems().contains(menuItem));
		Assert.assertTrue("Items list should contain the sub menu item", group.getMenuItems().contains(menuItem));
	}

}
