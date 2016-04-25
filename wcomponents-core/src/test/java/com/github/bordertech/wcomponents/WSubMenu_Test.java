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
	public void testConstructor1() {
		String text = "A";
		WSubMenu subMenu = new WSubMenu(text);
		Assert.assertEquals("Incorrect text set by constructor", text, subMenu.getText());
	}

	@Test
	public void testConstructor2() {
		WDecoratedLabel label = new WDecoratedLabel();
		WSubMenu subMenu = new WSubMenu(label);
		Assert.assertEquals("Incorrect label set by constructor", label, subMenu.getDecoratedLabel());
	}

	@Test
	public void testConstructor3() {
		String text = "A";
		char key = 'S';
		WSubMenu subMenu = new WSubMenu(text, key);
		Assert.assertEquals("Incorrect text set by constructor", text, subMenu.getText());
		Assert.assertEquals("Incorrect accessKey set by constructor", key, subMenu.getAccessKey());
	}

	@Test
	public void testModeAccessors() {
		assertAccessorsCorrect(new WSubMenu(TEST_TEXT), "mode", WSubMenu.MenuMode.CLIENT, WSubMenu.MenuMode.DYNAMIC, WSubMenu.MenuMode.EAGER);
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
	public void testAddSeparator1() {
		WSubMenu subMenu = new WSubMenu("");
		subMenu.addSeparator();
		Assert.assertTrue("Sub menu should contain a seperator", subMenu.getMenuItems().get(0) instanceof WSeparator);
	}

	@Test
	public void testAddSeparator2() {
		WSubMenu subMenu = new WSubMenu("");
		WSeparator separator = new WSeparator();
		subMenu.add(separator);
		Assert.assertTrue("Separator should have been added to sub menu", subMenu.getMenuItems().contains(separator));
	}

	@Test
	public void testAddMenuItem() {
		WSubMenu subMenu = new WSubMenu("");
		WMenuItem menuItem = new WMenuItem("item");
		subMenu.add(menuItem);
		Assert.assertTrue("Menu item should have been added to sub menu", subMenu.getMenuItems().contains(menuItem));
	}

	@Test
	public void testAddMenuItemGroup1() {
		WSubMenu subMenu = new WSubMenu("");
		WMenuItemGroup group = new WMenuItemGroup("");
		subMenu.addMenuItemGroup(group);
		Assert.assertTrue("Group item should have been added to sub menu", subMenu.getMenuItems().contains(group));
	}

	@Test
	public void testAddMenuItemGroup2() {
		WSubMenu subMenu = new WSubMenu("");
		WMenuItemGroup group = new WMenuItemGroup("");
		subMenu.add(group);
		Assert.assertTrue("Group item should have been added to sub menu", subMenu.getMenuItems().contains(group));
	}

	@Test
	public void testAddSubMenu() {
		WSubMenu subMenu = new WSubMenu("");
		WSubMenu subSubMenu = new WSubMenu("submenu");
		subMenu.add(subSubMenu);
		Assert.assertTrue("Sub-menu should have been added to sub menu", subMenu.getMenuItems().contains(subSubMenu));
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

		WSubMenu subMenu = new WSubMenu("");
		Assert.assertTrue("Menu item list should be empty by default", subMenu.getMenuItems().isEmpty());

		// Add items
		WMenuItem item1 = new WMenuItem("item1");
		WMenuItem item2 = new WMenuItem("item2");
		subMenu.add(item1);
		subMenu.add(item2);
		Assert.assertTrue("Menu item1 should have been added to menu bar", subMenu.getMenuItems().contains(item1));
		Assert.assertTrue("Menu item2 should have been added to menu bar", subMenu.getMenuItems().contains(item2));

		// Remove item
		subMenu.removeMenuItem(item1);
		Assert.assertFalse("Menu item1 should not been in items list after being removed", subMenu.getMenuItems().contains(item1));
		Assert.assertTrue("Menu item2 should be in the items list", subMenu.getMenuItems().contains(item2));
	}

	@Test
	public void testRemoveAllItems() {

		WSubMenu subMenu = new WSubMenu("");
		Assert.assertTrue("Menu item list should be empty by default", subMenu.getMenuItems().isEmpty());

		// Add items
		WMenuItem item1 = new WMenuItem("item1");
		WMenuItem item2 = new WMenuItem("item2");
		subMenu.add(item1);
		subMenu.add(item2);
		Assert.assertEquals("Menu items list should have 2 items", 2, subMenu.getMenuItems().size());

		// Remove item
		subMenu.removeAllMenuItems();
		Assert.assertTrue("Menu items should be empty", subMenu.getMenuItems().isEmpty());
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

	@Test
	public void testDisabledAccessors() {
		assertAccessorsCorrect(new WSubMenu(TEST_TEXT), "disabled", false, true, false);
	}

	@Test
	public void testAccessKeyAccessors() {
		assertAccessorsCorrect(new WSubMenu(TEST_TEXT), "accessKey", '\0', 'a', 'b');
	}

	@Test
	public void testGetAccessKeyAsString() {
		WSubMenu subMenu = new WSubMenu("");
		Assert.assertNull("Incorrect acesskey as string", subMenu.getAccessKeyAsString());

		subMenu.setAccessKey('C');
		Assert.assertEquals("Incorrect acesskey as string", "C", subMenu.getAccessKeyAsString());

		subMenu.setAccessKey('\0');
		Assert.assertNull("Incorrect acesskey as string", subMenu.getAccessKeyAsString());
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
	public void testMultipleSelectionAccessors() {
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
	public void testOpenAccessors() {
		assertAccessorsCorrect(new WSubMenu(TEST_TEXT), "open", false, true, false);
	}

	@Test
	public void testSelectableAccessors() {
		WSubMenu item = new WSubMenu("");
		Assert.assertNull("Selectable should be null by default", item.isSelectable());

		item.setSelectable(Boolean.FALSE);
		item.setLocked(true);
		setActiveContext(createUIContext());
		item.setSelectable(Boolean.TRUE);

		Assert.assertTrue("Should be selectable in session", item.isSelectable());

		resetContext();
		Assert.assertFalse("Default should not be selectable", item.isSelectable());
	}

	@Test
	public void testIsSelected() {
		WMenu menu = new WMenu();
		WSubMenu subMenu = new WSubMenu(TEST_TEXT);
		menu.add(subMenu);

		// Not selected
		Assert.assertFalse("Sub menu should not be selected by default", subMenu.isSelected());

		// Set as selected
		menu.setSelectedItem(subMenu);
		Assert.assertTrue("Sub menu should be selected by default", subMenu.isSelected());

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

}
