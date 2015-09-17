package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.WMenu.MenuType;
import com.github.bordertech.wcomponents.WMenu.SelectMode;
import com.github.bordertech.wcomponents.util.mock.MockRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import junit.framework.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link WMenu}.
 *
 * @author Anthony O'Connor
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
	public void testSelectModeAccessors() {
		assertAccessorsCorrect(new WMenu(), "selectMode", SelectMode.NONE, SelectMode.MULTIPLE, SelectMode.SINGLE);
	}

	@Test
	public void testDisabledAccessors() {
		assertAccessorsCorrect(new WMenu(), "disabled", false, true, false);
	}

	@Test
	public void testMarginAccessors() {
		assertAccessorsCorrect(new WMenu(), "margin", null, new Margin(1), new Margin(2));
	}

	/**
	 * Test setSelectedItems.
	 */
	@Test
	public void testSetSelectedItems() {
		WMenu menu = new WMenu();
		WMenuItem item1 = new WMenuItem(new WDecoratedLabel("label1"));
		WMenuItem item2 = new WMenuItem(new WDecoratedLabel("label2"));
		WSubMenu subMenu1 = new WSubMenu("label3");
		WMenuItem item3 = new WMenuItem(new WDecoratedLabel("label4"));
		menu.add(item1);
		menu.add(item2);
		menu.add(subMenu1);
		subMenu1.add(item3);
		List<WComponent> selectedItems = Arrays.asList(new WComponent[]{item1, item3});

		menu.setLocked(true);
		setActiveContext(createUIContext());
		menu.setSelectedItems(selectedItems);

		List<WComponent> resultItems = menu.getSelectedItems();
		Assert.assertNotNull("results should not be null", resultItems);
		Assert.assertEquals("results should be same size as inputs", selectedItems.size(), resultItems.size());
		Assert.assertTrue("results should contain the inputs", resultItems.containsAll(selectedItems));

		resetContext();
		Assert.assertTrue("Should not have a selection by default", menu.getSelectedItems().isEmpty());
	}

	/**
	 * Test getSelectedItems - when no selections set.
	 */
	@Test
	public void testGetSelectedItemWhenNoSelections() {
		WMenu menu = new WMenu();
		WMenuItem item1 = new WMenuItem(new WDecoratedLabel("label1"));
		WMenuItem item2 = new WMenuItem(new WDecoratedLabel("label2"));
		WSubMenu subMenu1 = new WSubMenu("label3");
		WMenuItem item3 = new WMenuItem(new WDecoratedLabel("label4"));
		menu.add(item1);
		menu.add(item2);
		menu.add(subMenu1);
		subMenu1.add(item3);

		Assert.assertNull("should return first child", menu.getSelectedItem());
	}

	/**
	 * Test getSelectedItem - when selections set.
	 */
	@Test
	public void testGetSelectedItem() {
		WMenu menu = new WMenu();
		WMenuItem item1 = new WMenuItem(new WDecoratedLabel("label1"));
		WMenuItem item2 = new WMenuItem(new WDecoratedLabel("label2"));
		WSubMenu subMenu1 = new WSubMenu("label3");
		WMenuItem item3 = new WMenuItem(new WDecoratedLabel("label4"));
		menu.add(item1);
		menu.add(item2);
		menu.add(subMenu1);
		subMenu1.add(item3);
		List<WComponent> selectedItems = Arrays.asList(new WComponent[]{item3, subMenu1});

		menu.setLocked(true);
		setActiveContext(createUIContext());
		menu.setSelectedItems(selectedItems);

		Assert.assertEquals("should return first selected item", item3, menu.getSelectedItem());

		resetContext();
		Assert.assertNull("Should not have a selection by default", menu.getSelectedItem());
	}

	/**
	 * Test setSelectedItem - when selections already set. creates new selectionList containing the
	 * single new item.
	 */
	@Test
	public void testSetSelectedItem() {
		WMenu menu = new WMenu();
		WMenuItem item1 = new WMenuItem(new WDecoratedLabel("label1"));
		WMenuItem item2 = new WMenuItem(new WDecoratedLabel("label2"));
		WSubMenu subMenu1 = new WSubMenu("label3");
		WMenuItem item3 = new WMenuItem(new WDecoratedLabel("label4"));
		menu.add(item1);
		menu.add(item2);
		menu.add(subMenu1);
		subMenu1.add(item3);
		List<WComponent> selectedItems = Arrays.asList(new WComponent[]{item3, subMenu1});

		menu.setLocked(true);
		setActiveContext(createUIContext());
		menu.setSelectedItems(selectedItems);
		menu.setSelectedItem(item2);

		List<WComponent> expectedItems = Arrays.asList(new WComponent[]{item2});
		List<WComponent> resultItems = menu.getSelectedItems();

		Assert.assertNotNull("results should not be null", resultItems);
		Assert.assertEquals("results size should equal expected size", expectedItems.size(), resultItems.size());
		Assert.assertTrue("results should contain all items in expected", resultItems.containsAll(expectedItems));

		resetContext();
		Assert.assertNull("Should not have a selection by default", menu.getSelectedItem());
	}

	/**
	 * Test setSelectedItem - when no selection set. creates new selectionList containing the single
	 * new item.
	 */
	@Test
	public void testSetSelectedItemNoSelections() {
		WMenu menu = new WMenu();
		WMenuItem item1 = new WMenuItem(new WDecoratedLabel("label1"));
		WMenuItem item2 = new WMenuItem(new WDecoratedLabel("label2"));
		WSubMenu subMenu1 = new WSubMenu("label3");
		WMenuItem item3 = new WMenuItem(new WDecoratedLabel("label4"));
		menu.add(item1);
		menu.add(item2);
		menu.add(subMenu1);
		subMenu1.add(item3);

		menu.setLocked(true);
		setActiveContext(createUIContext());
		menu.setSelectedItem(item2);

		List<WComponent> expectedItems = Arrays.asList(new WComponent[]{item2});
		List<WComponent> resultItems = menu.getSelectedItems();

		Assert.assertNotNull("results should not be null", resultItems);
		Assert.assertEquals("results size should equal expected size", expectedItems.size(), resultItems.size());
		Assert.assertTrue("results should contain all items in expected", resultItems.containsAll(expectedItems));

		resetContext();
		Assert.assertNull("Should not have a selection by default", menu.getSelectedItem());
	}

	/**
	 * Test handleRequest - two items selected in request - one from submenu.
	 */
	@Test
	public void testHandleRequest() {
		WMenu menu = new WMenu();
		menu.setSelectMode(SelectMode.SINGLE);

		WMenuItem item1 = new WMenuItem(new WDecoratedLabel("label1"));
		WMenuItem item2 = new WMenuItem(new WDecoratedLabel("label2"));
		WSubMenu subMenu1 = new WSubMenu("label3");
		WMenuItem item3 = new WMenuItem(new WDecoratedLabel("label4"));
		item3.setSelectable(true);
		menu.add(item1);
		menu.add(item2);
		menu.add(subMenu1);
		subMenu1.add(item3);
		List<WComponent> expectedSelectedItems = Arrays.asList(new WComponent[]{item1, item3});

		// put the selected items to be expected in the request
		menu.setLocked(true);
		setActiveContext(createUIContext());
		MockRequest request = new MockRequest();
		request.setParameter(menu.getId() + "-h", "x");
		for (WComponent item : expectedSelectedItems) {
			request.setParameter(item.getId() + ".selected", "x");
		}
		menu.handleRequest(request);

		List<WComponent> resultSelectedItems = menu.getSelectedItems();

		Assert.assertNotNull("results should not be null", resultSelectedItems);
		Assert.assertEquals("results size should equal expected size", expectedSelectedItems.size(),
				resultSelectedItems.size());
		Assert.assertTrue("results should contain all items in expected",
				resultSelectedItems.containsAll(expectedSelectedItems));

		// Test that selection is ignored when the menu is disabled.
		menu.setDisabled(true);
		menu.setSelectedItems(new ArrayList<WComponent>());

		menu.handleRequest(request);

		resultSelectedItems = menu.getSelectedItems();

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
		List<WComponent> expectedSelectedItems = Arrays.asList(new WComponent[]{item1, item3});

		menu.setLocked(true);
		setActiveContext(createUIContext());
		menu.setSelectedItems(expectedSelectedItems);

		// Menu not in request, selected items should not change
		MockRequest request = new MockRequest();
		menu.handleRequest(request);
		Assert.assertEquals("results should not have changed", expectedSelectedItems, menu.getSelectedItems());

		// Menu in request with no items, no items should be selected
		request = new MockRequest();
		request.setParameter(menu.getId() + "-h", "x");
		menu.handleRequest(request);
		Assert.assertTrue("results should be empty", menu.getSelectedItems().isEmpty());
	}

	@Test
	public void testAddSubMenu() {
		WMenu menuBar = new WMenu();
		WSubMenu subMenu = new WSubMenu("submenu");
		menuBar.add(subMenu);

		Assert.assertTrue("Sub-menu should have been added to menu bar", menuBar.getIndexOfChild(subMenu) != -1);
		Assert.assertSame("Menu bar should be parent of sub-menu", menuBar, subMenu.getParent());
	}

	@Test
	public void testAddMenuItem() {
		WMenu menuBar = new WMenu();
		WMenuItem menuItem = new WMenuItem("item");
		menuBar.add(menuItem);

		Assert.assertTrue("Menu item should have been added to menu bar", menuBar.getIndexOfChild(menuItem) != -1);
		Assert.assertSame("Menu bar should be parent of menu item", menuBar, menuItem.getParent());
	}

}
