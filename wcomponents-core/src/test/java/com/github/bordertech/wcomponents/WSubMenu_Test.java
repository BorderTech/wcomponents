package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.util.mock.MockRequest;
import junit.framework.Assert;
import org.junit.Test;

/**
 * WSubMenu_Test - Unit tests for {@link WSubMenu}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WSubMenu_Test extends AbstractWComponentTestCase {

	/**
	 * Test text.
	 */
	private static final String TEST_TEXT = "WSubMenu_Test.testText";

	@Test
	public void testAddSubMenu() {
		WSubMenu subMenu = new WSubMenu("");
		WSubMenu subSubMenu = new WSubMenu("submenu");
		subMenu.add(subSubMenu);

		Assert.assertSame("Sub-menu should be ancestor of sub-sub-menu", subMenu, WebUtilities.
				getTop(subSubMenu));
	}

	@Test
	public void testAddMenuItem() {
		WSubMenu subMenu = new WSubMenu("");
		WMenuItem menuItem = new WMenuItem("item");
		subMenu.add(menuItem);

		Assert.assertSame("Sub-menu should be ancestor of menu item", subMenu, WebUtilities.getTop(
				menuItem));
	}

	@Test
	public void testIsTopLevelMenu() {
		WMenu menu = new WMenu();
		WSubMenu subMenu = new WSubMenu("a");
		WSubMenu subSubMenu = new WSubMenu("b");

		Assert.assertFalse("Should not be a top-level menu when there is no parent", subMenu.
				isTopLevelMenu());

		menu.add(subMenu);
		subMenu.add(subSubMenu);

		Assert.assertTrue("isTopLevel should be true for top-level sub-menu", subMenu.
				isTopLevelMenu());
		Assert.assertFalse("isTopLevel should be false for second-level sub-menu", subSubMenu.
				isTopLevelMenu());
	}

	@Test
	public void testDisabledAccessors() {
		assertAccessorsCorrect(new WSubMenu(TEST_TEXT), "disabled", false, true, false);
	}

	@Test
	public void testAccessKeyAccessors() {
		assertAccessorsCorrect(new WSubMenu(TEST_TEXT), "accessKey", '\0', 'a', 'b');
	}

	@Test
	public void testTextAccessors() throws Exception {
		assertAccessorsCorrect(new WSubMenu(TEST_TEXT), "text", TEST_TEXT, "A", "B");
	}

	@Test
	public void testSelectabilityAccessors() {
		assertAccessorsCorrect(new WSubMenu(TEST_TEXT), "selectability", null, true, false);
	}

	@Test
	public void testSetMultipleSelection() {
		assertAccessorsCorrect(new WSubMenu(TEST_TEXT), "multipleSelection", false, true, false);
	}

	@Test
	public void testSelectModeAccessors() {
		assertAccessorsCorrect(new WSubMenu(TEST_TEXT), "selectMode", WMenu.SelectMode.NONE, WMenu.SelectMode.MULTIPLE,
				WMenu.SelectMode.SINGLE);
	}

	@Test
	public void testSelectionModeAccessors() {
		assertAccessorsCorrect(new WSubMenu(TEST_TEXT), "selectionMode", MenuSelectContainer.SelectionMode.NONE, MenuSelectContainer.SelectionMode.MULTIPLE,
				MenuSelectContainer.SelectionMode.SINGLE);
	}

	@Test
	public void testActionCommandAccessors() {
		assertAccessorsCorrect(new WSubMenu(TEST_TEXT), "actionCommand", null, "A", "B");
	}

	@Test
	public void testActionObjectAccessors() {
		assertAccessorsCorrect(new WSubMenu(TEST_TEXT), "actionObject", null, "A", "B");
	}

	@Test
	public void testHandleRequest() {
		TestAction action = new TestAction();
		WMenu menu = new WMenu();
		WSubMenu subMenu = new WSubMenu(TEST_TEXT);
		subMenu.setAction(action);
		menu.add(subMenu);

		setActiveContext(createUIContext());

		// Menu not in Request
		MockRequest request = new MockRequest();
		menu.serviceRequest(request);
		Assert.assertFalse("Action should not have been called when sub-menu was not selected",
				action.wasTriggered());

		// Menu in Request but submenu not current AJAX Trigger
		request = new MockRequest();
		request.setParameter(menu.getId() + "-h", "x");
		menu.serviceRequest(request);
		Assert.assertFalse("Action should not have been called when sub-menu was not selected",
				action.wasTriggered());

		// Menu in Request and submenu is the current ajax triiger
		request = new MockRequest();
		request.setParameter(menu.getId() + "-h", "x");
		request.setParameter(subMenu.getId(), "x");

		try {
			// Setup AJAX Operation trigger by the submenu
			AjaxHelper.setCurrentOperationDetails(new AjaxOperation(subMenu.getId(), subMenu.getId()),
					null);

			menu.serviceRequest(request);
		} finally {
			AjaxHelper.clearCurrentOperationDetails();
		}
		Assert.assertTrue("Action should have been called when sub-menu is selected", action.
				wasTriggered());
	}

	@Test
	public void testHandleRequestSubMenuOpen() {
		WMenu menu = new WMenu();
		WSubMenu subMenu = new WSubMenu(TEST_TEXT);
		menu.add(subMenu);

		setActiveContext(createUIContext());

		// Menu not in Request
		MockRequest request = new MockRequest();
		menu.serviceRequest(request);
		Assert.assertFalse("Submenu should not be open", subMenu.isOpen());

		// Menu in Request but submenu not open
		request = new MockRequest();
		request.setParameter(menu.getId() + "-h", "x");
		menu.serviceRequest(request);
		Assert.assertFalse("Submenu should not be open", subMenu.isOpen());

		// Menu in Request and submenu open
		request = new MockRequest();
		request.setParameter(menu.getId() + "-h", "x");
		request.setParameter(subMenu.getId(), "x");
		request.setParameter(subMenu.getId() + ".open", "true");
		menu.serviceRequest(request);
		Assert.assertTrue("Submenu should be open", subMenu.isOpen());
	}

	@Test
	public void testHandleRequestWhenDisabled() {
		TestAction action = new TestAction();
		WMenu menu = new WMenu();
		WSubMenu subMenu = new WSubMenu(TEST_TEXT);
		subMenu.setAction(action);
		menu.add(subMenu);

		setActiveContext(createUIContext());
		subMenu.setDisabled(true);

		MockRequest request = new MockRequest();
		request.setParameter(menu.getId() + "-h", "x");

		menu.serviceRequest(request);
		Assert.assertFalse("Action should not have been called when sub-menu was not selected",
				action.wasTriggered());

		request.setParameter(subMenu.getId(), "x");
		menu.serviceRequest(request);
		Assert.assertFalse("Action should not have been called on a disabled sub-menu", action.
				wasTriggered());
	}

	@Test
	public void testAddItem() {
		WSubMenu subMenu = new WSubMenu(TEST_TEXT);
		WMenuItem menuItem = new WMenuItem("item");
		subMenu.addMenuItem(menuItem);
		Assert.assertTrue("Menu item should have been added to menu bar", subMenu.getMenuItems().contains(menuItem));
	}

	@Test
	public void testRemoveItem() {
		WSubMenu subMenu = new WSubMenu(TEST_TEXT);
		WMenuItem menuItem = new WMenuItem("item");

		junit.framework.Assert.assertTrue("Menu item list should be empty by default", subMenu.getMenuItems().isEmpty());

		// Add item
		subMenu.add(menuItem);
		junit.framework.Assert.assertTrue("Menu item should have been added to menu bar", subMenu.getMenuItems().contains(menuItem));

		// Remove item
		subMenu.removeMenuItem(menuItem);
		junit.framework.Assert.assertTrue("Menu item list should be empty after remove item", subMenu.getMenuItems().isEmpty());
	}

	@Test
	public void testRemoveAllItems() {
		WSubMenu subMenu = new WSubMenu(TEST_TEXT);
		Assert.assertTrue("Menu item list should be empty by default", subMenu.getMenuItems().isEmpty());

		// Add item
		WMenuItem menuItem = new WMenuItem("item");
		subMenu.add(menuItem);
		junit.framework.Assert.assertTrue("Menu item should have been added to menu bar", subMenu.getMenuItems().contains(menuItem));

		// Remove all items
		subMenu.removeAllMenuItems();
		Assert.assertTrue("Menu item list should be empty after remove item", subMenu.getMenuItems().isEmpty());
	}

	@Test
	public void testGetItems() {
		WSubMenu subMenu = new WSubMenu(TEST_TEXT);
		WMenuItem menuItem = new WMenuItem("item");
		subMenu.add(menuItem);

		// Add submenu with a child item
		WSubMenu subMenu2 = new WSubMenu("sub");
		subMenu.add(subMenu2);
		WMenuItem subItem = new WMenuItem("subItem");
		subMenu2.add(subItem);

		Assert.assertEquals("Items list should have two items", 2, subMenu.getMenuItems().size());
		Assert.assertTrue("Items list should contain the menu item", subMenu.getMenuItems().contains(menuItem));
		Assert.assertTrue("Items list should contain the sub menu item", subMenu.getMenuItems().contains(menuItem));
	}

}
