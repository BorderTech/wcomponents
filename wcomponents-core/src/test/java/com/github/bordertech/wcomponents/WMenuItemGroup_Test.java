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
		assertAccessorsCorrect(new WMenuItemGroup(""), "disabled", false, true, false);
	}

	@Test
	public void testAddSeparator1() {
		WMenuItemGroup group = new WMenuItemGroup("");
		group.addSeparator();
		Assert.assertTrue("Items should contain a seperator", group.getMenuItems().get(0) instanceof WSeparator);
	}

	@Test
	public void testAddSeparator2() {
		WMenuItemGroup group = new WMenuItemGroup("");
		WSeparator separator = new WSeparator();
		group.add(separator);
		Assert.assertTrue("Separator should have been added to group", group.getMenuItems().contains(separator));
	}

	@Test
	public void testAddMenuItem() {
		WMenuItemGroup group = new WMenuItemGroup("");
		WMenuItem menuItem = new WMenuItem("item");
		group.add(menuItem);
		Assert.assertTrue("Menu item should have been added to group", group.getMenuItems().contains(menuItem));
	}

	@Test
	public void testAddSubMenu() {
		WMenuItemGroup group = new WMenuItemGroup("");
		WSubMenu subMenu = new WSubMenu("submenu");
		group.add(subMenu);
		Assert.assertTrue("Sub-menu should have been added to group", group.getMenuItems().contains(subMenu));
	}

	@Test
	public void testAddItem() {
		WMenuItemGroup group = new WMenuItemGroup("");
		WMenuItem menuItem = new WMenuItem("item");
		group.addMenuItem(menuItem);
		Assert.assertTrue("Menu item should have been added to group", group.getMenuItems().contains(menuItem));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddGroupItemException() {
		new WMenuItemGroup("").addMenuItem(new WMenuItemGroup("nested"));
	}

	@Test
	public void testRemoveItem() {

		WMenuItemGroup group = new WMenuItemGroup("");
		Assert.assertTrue("Menu item list should be empty by default", group.getMenuItems().isEmpty());

		// Add items
		WMenuItem item1 = new WMenuItem("item1");
		WMenuItem item2 = new WMenuItem("item2");
		group.add(item1);
		group.add(item2);
		Assert.assertTrue("Menu item1 should have been added to group", group.getMenuItems().contains(item1));
		Assert.assertTrue("Menu item2 should have been added to group", group.getMenuItems().contains(item2));

		// Remove item
		group.removeMenuItem(item1);
		Assert.assertFalse("Menu item1 should not been in items list after being removed", group.getMenuItems().contains(item1));
		Assert.assertTrue("Menu item2 should be in the items list", group.getMenuItems().contains(item2));
	}

	@Test
	public void testRemoveAllItems() {

		WMenuItemGroup group = new WMenuItemGroup("");
		Assert.assertTrue("Menu item list should be empty by default", group.getMenuItems().isEmpty());

		// Add items
		WMenuItem item1 = new WMenuItem("item1");
		WMenuItem item2 = new WMenuItem("item2");
		group.add(item1);
		group.add(item2);
		Assert.assertEquals("Menu items list should have 2 items", 2, group.getMenuItems().size());

		// Remove item
		group.removeAllMenuItems();
		Assert.assertTrue("Menu items should be empty", group.getMenuItems().isEmpty());
	}

	@Test
	public void testGetItems() {
		WMenuItemGroup group = new WMenuItemGroup("");
		WMenuItem menuItem = new WMenuItem("item");
		group.add(menuItem);

		// Add submenu with a child item
		WSubMenu subMenu = new WSubMenu("sub");
		group.add(subMenu);
		WMenuItem subItem = new WMenuItem("subItem");
		subMenu.add(subItem);

		Assert.assertEquals("Items list should have two items", 2, group.getMenuItems().size());
		Assert.assertTrue("Items list should contain the menu item", group.getMenuItems().contains(menuItem));
		Assert.assertTrue("Items list should contain the sub menu item", group.getMenuItems().contains(subMenu));
	}

}
