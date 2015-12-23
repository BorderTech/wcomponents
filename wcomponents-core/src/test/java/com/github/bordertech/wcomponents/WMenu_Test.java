package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.MenuSelectContainer.SelectionMode;
import com.github.bordertech.wcomponents.WMenu.MenuType;
import com.github.bordertech.wcomponents.WMenu.SelectMode;
import com.github.bordertech.wcomponents.util.mock.MockRequest;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import junit.framework.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link WMenu}.
 *
 * @author Anthony O'Connor
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class WMenu_Test extends AbstractWComponentTestCase {

	@Test
	public void testConstructor1() {
		WMenu menu = new WMenu();
		Assert.assertEquals("Incorrect default type", MenuType.BAR, menu.getType());
	}

	@Test
	public void testConstructor2() {
		WMenu menu = new WMenu(MenuType.COLUMN);
		Assert.assertEquals("Incorrect type", MenuType.COLUMN, menu.getType());
	}

	@Test
	public void testRowsAccessors() {
		assertAccessorsCorrect(new WMenu(), "rows", 0, 1, 2);
	}

	@Test
	public void testMarginAccessors() {
		assertAccessorsCorrect(new WMenu(), "margin", null, new Margin(1), new Margin(2));
	}

	@Test
	public void testSelectModeAccessors() {
		assertAccessorsCorrect(new WMenu(), "selectMode", SelectMode.NONE, SelectMode.MULTIPLE,
				SelectMode.SINGLE);
	}

	@Test
	public void testSelectionModeAccessors() {
		assertAccessorsCorrect(new WMenu(), "selectionMode", MenuSelectContainer.SelectionMode.NONE, SelectionMode.MULTIPLE,
				SelectionMode.SINGLE);
	}

	@Test
	public void testDisabledAccessors() {
		assertAccessorsCorrect(new WMenu(), "disabled", false, true, false);
	}

	@Test
	public void testAddSeparator1() {
		WMenu menuBar = new WMenu();
		menuBar.addSeparator();
		Assert.assertTrue("Menu should contain a seperator", menuBar.getMenuItems().get(0) instanceof WSeparator);
	}

	@Test
	public void testAddSeparator2() {
		WMenu menuBar = new WMenu();
		WSeparator separator = new WSeparator();
		menuBar.add(separator);
		Assert.assertTrue("Separator should have been added to menu bar", menuBar.getMenuItems().contains(separator));
	}

	@Test
	public void testAddMenuItem() {
		WMenu menuBar = new WMenu();
		WMenuItem menuItem = new WMenuItem("item");
		menuBar.add(menuItem);
		Assert.assertTrue("Menu item should have been added to menu bar", menuBar.getMenuItems().contains(menuItem));
	}

	@Test
	public void testAddMenuItemGroup() {
		WMenu menuBar = new WMenu();
		WMenuItemGroup group = new WMenuItemGroup("");
		menuBar.add(group);
		Assert.assertTrue("Group item should have been added to menu bar", menuBar.getMenuItems().contains(group));
	}

	@Test
	public void testAddSubMenu() {
		WMenu menuBar = new WMenu();
		WSubMenu subMenu = new WSubMenu("submenu");
		menuBar.add(subMenu);
		Assert.assertTrue("Sub-menu should have been added to menu bar", menuBar.getMenuItems().contains(subMenu));
	}

	@Test
	public void testAddItem() {
		WMenu menuBar = new WMenu();
		WMenuItem menuItem = new WMenuItem("item");
		menuBar.addMenuItem(menuItem);
		Assert.assertTrue("Menu item should have been added to menu bar", menuBar.getMenuItems().contains(menuItem));
	}

	@Test
	public void testRemoveItem() {

		WMenu menuBar = new WMenu();
		Assert.assertTrue("Menu item list should be empty by default", menuBar.getMenuItems().isEmpty());

		// Add items
		WMenuItem item1 = new WMenuItem("item1");
		WMenuItem item2 = new WMenuItem("item2");
		menuBar.add(item1);
		menuBar.add(item2);
		Assert.assertTrue("Menu item1 should have been added to menu bar", menuBar.getMenuItems().contains(item1));
		Assert.assertTrue("Menu item2 should have been added to menu bar", menuBar.getMenuItems().contains(item2));

		// Remove item
		menuBar.removeMenuItem(item1);
		Assert.assertFalse("Menu item1 should not been in items list after being removed", menuBar.getMenuItems().contains(item1));
		Assert.assertTrue("Menu item2 should be in the items list", menuBar.getMenuItems().contains(item2));
	}

	@Test
	public void testRemoveAllItems() {

		WMenu menuBar = new WMenu();
		Assert.assertTrue("Menu item list should be empty by default", menuBar.getMenuItems().isEmpty());

		// Add items
		WMenuItem item1 = new WMenuItem("item1");
		WMenuItem item2 = new WMenuItem("item2");
		menuBar.add(item1);
		menuBar.add(item2);
		Assert.assertEquals("Menu items list should have 2 items", 2, menuBar.getMenuItems().size());

		// Remove item
		menuBar.removeAllMenuItems();
		Assert.assertTrue("Menu items should be empty", menuBar.getMenuItems().isEmpty());
	}

	@Test
	public void testGetItems() {
		WMenu menuBar = new WMenu();
		WMenuItem menuItem = new WMenuItem("item");
		menuBar.add(menuItem);

		// Add submenu with child items
		WSubMenu subMenu = new WSubMenu("sub");
		menuBar.add(subMenu);
		WMenuItem subItem = new WMenuItem("subItem");
		subMenu.add(subItem);

		Assert.assertEquals("Items list should have two items", 2, menuBar.getMenuItems().size());
		Assert.assertTrue("Items list should contain the menu item", menuBar.getMenuItems().contains(menuItem));
		Assert.assertTrue("Items list should contain the sub menu item", menuBar.getMenuItems().contains(subMenu));
	}

	@Test
	public void testGetItemsRecursive() {
		WMenu menuBar = new WMenu();
		WMenuItem menuItem = new WMenuItem("item");
		menuBar.add(menuItem);

		WSubMenu subMenu = new WSubMenu("sub");
		menuBar.add(subMenu);
		WMenuItem subItem = new WMenuItem("subItem");
		subMenu.add(subItem);

		// Not recursive
		Assert.assertEquals("Items list should have two items", 2, menuBar.getMenuItems(false).size());
		Assert.assertTrue("Items list should contain the menu item", menuBar.getMenuItems(false).contains(menuItem));
		Assert.assertTrue("Items list should contain the sub menu item", menuBar.getMenuItems(false).contains(subMenu));

		// Recursive (include submenu children)
		Assert.assertEquals("Items list should have three items", 3, menuBar.getMenuItems(true).size());
		Assert.assertTrue("Items list should contain the menu item", menuBar.getMenuItems(true).contains(menuItem));
		Assert.assertTrue("Items list should contain the sub menu item", menuBar.getMenuItems(true).contains(subMenu));
		Assert.assertTrue("Items list should contain the sub item", menuBar.getMenuItems(true).contains(subItem));

	}

	@Test
	public void testSelectedItemsAccessors() {
		assertAccessorsCorrect(new WMenu(), "selectedItems", Collections.EMPTY_LIST, Arrays.asList(new WMenuItem("A")), Arrays.asList(new WMenuItem("B")));
	}

	@Test
	public void testSelectedItemAccessors1() {
		assertAccessorsCorrect(new WMenu(), "selectedItem", null, new WMenuItem("A"), new WMenuItem("B"));
	}

	@Test
	public void testSelectedItemAccessors2() {
		WMenu menu = new WMenu();
		WComponent item1 = new WMenuItem("A");
		menu.setSelectedItems(Arrays.asList(item1, new WMenuItem("B")));
		Assert.assertEquals("Should return the first selected item", item1, menu.getSelectedItem());
	}

	@Test
	public void testClearSelectedItems() {
		WMenu menu = new WMenu();

		// Check nothing selected
		Assert.assertNull("Should be nothing selected by default", menu.getSelectedItem());
		Assert.assertTrue("Should be nothing selected in the list by default", menu.getSelectedItems().isEmpty());

		// Set selected
		menu.setSelectedItem(new WMenuItem("A"));
		Assert.assertNotNull("Should have selected item", menu.getSelectedItem());
		Assert.assertFalse("Should have selected item in list", menu.getSelectedItems().isEmpty());

		// Clear selection
		menu.clearSelectedItems();
		Assert.assertNull("Should be nothing selected after clear selected", menu.getSelectedItem());
		Assert.assertTrue("Should be nothing selected in the list after clear selected", menu.getSelectedItems().isEmpty());
	}

	@Test
	public void testSelectedMenuItemsAccessors() {
		assertAccessorsCorrect(new WMenu(), "selectedMenuItems", Collections.EMPTY_LIST, Arrays.asList(new WMenuItem("")), Arrays.asList(new WMenuItem("")));
	}

	@Test
	public void testSelectedMenuItemAccessors1() {
		assertAccessorsCorrect(new WMenu(), "selectedMenuItem", null, new WMenuItem("A"), new WMenuItem("B"));
	}

	@Test
	public void testSelectedMenuItemAccessors2() {
		WMenu menu = new WMenu();
		WMenuItem item1 = new WMenuItem("A");
		menu.setSelectedMenuItems(Arrays.asList(item1, new WMenuItem("B")));
		Assert.assertEquals("Should return the first selected item", item1, menu.getSelectedMenuItem());
	}

	@Test
	public void testClearSelectedMenuItems() {
		WMenu menu = new WMenu();

		// Check nothing selected
		Assert.assertNull("Should be nothing selected by default", menu.getSelectedMenuItem());
		Assert.assertTrue("Should be nothing selected in the list by default", menu.getSelectedMenuItems().isEmpty());

		// Set selected
		menu.setSelectedMenuItem(new WMenuItem("A"));
		Assert.assertNotNull("Should have selected item", menu.getSelectedMenuItem());
		Assert.assertFalse("Should have selected item in list", menu.getSelectedMenuItems().isEmpty());

		// Clear selection
		menu.clearSelectedMenuItems();
		Assert.assertNull("Should be nothing selected after clear selected", menu.getSelectedMenuItem());
		Assert.assertTrue("Should be nothing selected in the list after clear selected", menu.getSelectedMenuItems().isEmpty());
	}

	/**
	 * Test handleRequest - two items selected in request - one from submenu.
	 */
	@Test
	public void testHandleRequest() {
		WMenu menu = new WMenu();
		menu.setSelectionMode(SelectionMode.SINGLE);

		WMenuItem item1 = new WMenuItem(new WDecoratedLabel("label1"));
		WMenuItem item2 = new WMenuItem(new WDecoratedLabel("label2"));
		WSubMenu subMenu1 = new WSubMenu("label3");
		WMenuItem item3 = new WMenuItem(new WDecoratedLabel("label4"));
		item3.setSelectability(true);
		menu.add(item1);
		menu.add(item2);
		menu.add(subMenu1);
		subMenu1.add(item3);
		List<MenuItemSelectable> expectedSelectedItems = Arrays.asList(new MenuItemSelectable[]{item1, item3});

		// put the selected items to be expected in the request
		menu.setLocked(true);
		setActiveContext(createUIContext());
		MockRequest request = new MockRequest();
		request.setParameter(menu.getId() + "-h", "x");
		for (WComponent item : expectedSelectedItems) {
			request.setParameter(item.getId() + ".selected", "x");
		}
		menu.handleRequest(request);

		List<MenuItemSelectable> resultSelectedItems = menu.getSelectedMenuItems();

		Assert.assertNotNull("results should not be null", resultSelectedItems);
		Assert.assertEquals("results size should equal expected size", expectedSelectedItems.size(),
				resultSelectedItems.size());
		Assert.assertTrue("results should contain all items in expected",
				resultSelectedItems.containsAll(expectedSelectedItems));

		// Test that selection is ignored when the menu is disabled.
		menu.setDisabled(true);
		menu.clearSelectedMenuItems();

		menu.handleRequest(request);

		resultSelectedItems = menu.getSelectedMenuItems();

		Assert.assertNotNull("Results should not be null", resultSelectedItems);
		Assert.assertTrue("Results should be empty", resultSelectedItems.isEmpty());
	}

	/**
	 * Test handleRequest - no items selected in request.
	 */
	@Test
	public void testHandleRequestNoSelections() {
		WMenu menu = new WMenu();
		WMenuItem item1 = new WMenuItem(new WDecoratedLabel("label1"));
		WMenuItem item2 = new WMenuItem(new WDecoratedLabel("label2"));
		WSubMenu subMenu1 = new WSubMenu("label3");
		WMenuItem item3 = new WMenuItem(new WDecoratedLabel("label4"));
		menu.add(item1);
		menu.add(item2);
		menu.add(subMenu1);
		subMenu1.add(item3);
		List<MenuItemSelectable> expectedSelectedItems = Arrays.asList(new MenuItemSelectable[]{item1, item3});

		menu.setLocked(true);
		setActiveContext(createUIContext());
		menu.setSelectedMenuItems(expectedSelectedItems);

		// Menu not in request, selected items should not change
		MockRequest request = new MockRequest();
		menu.handleRequest(request);
		Assert.assertEquals("results should not have changed", expectedSelectedItems, menu.
				getSelectedMenuItems());

		// Menu in request with no items, no items should be selected
		request = new MockRequest();
		request.setParameter(menu.getId() + "-h", "x");
		menu.handleRequest(request);
		Assert.assertTrue("results should be empty", menu.getSelectedMenuItems().isEmpty());
	}

}
