package com.github.bordertech.wcomponents.examples.menu;

import com.github.bordertech.wcomponents.MenuItem;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WMenu;
import com.github.bordertech.wcomponents.WMenu.SelectMode;
import com.github.bordertech.wcomponents.WMenuItem;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WStyledText;
import com.github.bordertech.wcomponents.WSubMenu;
import com.github.bordertech.wcomponents.WSubMenu.MenuMode;
import com.github.bordertech.wcomponents.WText;
import com.github.bordertech.wcomponents.layout.ColumnLayout;
import com.github.bordertech.wcomponents.util.TreeNode;
import java.util.Iterator;

/**
 * This component demonstrates the usage of a {@link WMenu.MenuType#COLUMN Tree} {@link WMenu}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class ColumnMenuExample extends WPanel {

	/**
	 * Creates a ColumnMenuExample.
	 */
	public ColumnMenuExample() {
		WPanel content = new WPanel(WPanel.Type.BLOCK);
		WText selectedMenuText = new WText();
		content.add(new WStyledText("Selected item: ", WStyledText.Type.EMPHASISED));
		content.add(selectedMenuText);
		add(content);

		WPanel menuAndPlaceholderPanel = new WPanel();
		add(menuAndPlaceholderPanel);
		menuAndPlaceholderPanel.setLayout(new ColumnLayout(new int[]{20, 80}));
		menuAndPlaceholderPanel.add(buildColumnMenu(selectedMenuText));
		menuAndPlaceholderPanel.add(new WText("placeholder"));
		add(new WButton("Submit"));
	}

	/**
	 * Builds up a column menu for inclusion in the example.
	 *
	 * @param selectedMenuText the WText to display the selected menu.
	 * @return a column menu for the example.
	 */
	private WMenu buildColumnMenu(final WText selectedMenuText) {
		WMenu menu = new WMenu(WMenu.MenuType.COLUMN);
		menu.setSelectMode(SelectMode.SINGLE);
		menu.setRows(8);

		StringTreeNode root = getOrgHierarchyTree();
		mapColumnHierarchy(menu, root, selectedMenuText);

		// Demonstrate different menu modes
		getSubMenuByText("NSW", menu).setMode(MenuMode.CLIENT);
		getSubMenuByText("Branch 1", menu).setMode(MenuMode.DYNAMIC);
		getSubMenuByText("VIC", menu).setMode(MenuMode.LAZY);

		return menu;
	}

	/**
	 * Recursively maps a tree hierarchy to a column menu.
	 *
	 * @param currentComponent the current component in the menu.
	 * @param currentNode the current node in the tree.
	 * @param selectedMenuText the WText to display the selected menu item.
	 */
	private void mapColumnHierarchy(final WComponent currentComponent,
			final StringTreeNode currentNode, final WText selectedMenuText) {
		if (currentNode.isLeaf()) {
			WMenuItem menuItem = new WMenuItem(currentNode.getData(), new ExampleMenuAction(
					selectedMenuText));
			menuItem.setActionObject(currentNode.getData());
			if (currentComponent instanceof WMenu) {
				((WMenu) currentComponent).add(menuItem);
			} else {
				((WSubMenu) currentComponent).add(menuItem);
			}

		} else {
			WSubMenu subMenu = new WSubMenu(currentNode.getData());
			subMenu.setSelectMode(SelectMode.SINGLE);
			subMenu.setSelectable(false);

			subMenu.setAction(new ExampleMenuAction(selectedMenuText));
			subMenu.setActionObject(currentNode.getData());

			if (currentComponent instanceof WMenu) {
				((WMenu) currentComponent).add(subMenu);
			} else {
				((WSubMenu) currentComponent).add(subMenu);
			}

			// Expand the first level in the tree by default.
			if (currentNode.getLevel() == 0) {
				subMenu.setOpen(true);
			}

			for (Iterator<TreeNode> i = currentNode.children(); i.hasNext();) {
				mapColumnHierarchy(subMenu, (StringTreeNode) i.next(), selectedMenuText);
			}
		}
	}

	/**
	 * Builds an organisation hierarchy tree for the column menu example.
	 *
	 * @return an organisation hierarchy tree.
	 */
	private StringTreeNode getOrgHierarchyTree() {
		// Hierarchical data in a flat format.
		// If an Object array contains 1 String element, it is a leaf node.
		// Else an Object array contains 1 String element + object arrays and is a branch node.
		Object[] data = new Object[]{
			"Australia",
			new Object[]{"ACT"},
			new Object[]{
				"NSW",
				new Object[]{"Paramatta"},
				new Object[]{
					"Sydney",
					new Object[]{
						"Branch 1",
						new Object[]{"Processing Team 1"},
						new Object[]{
							"Processing Team 2",
							new Object[]{"Robert Rogriguez"},
							new Object[]{"Phillip Sedgwick"},
							new Object[]{"Donald Sullivan"},
							new Object[]{"All"}
						},
						new Object[]{
							"Processing Team 3",
							new Object[]{"Jim McCarthy"},
							new Object[]{"Peter Dunne"},
							new Object[]{"Nicole Brown"},
							new Object[]{"All"}
						},
						new Object[]{"Processing Team 4"},
						new Object[]{"Processing Team 5"},
						new Object[]{"All"}
					},
					new Object[]{"Branch 2"},
					new Object[]{"Branch 3"}
				},
				new Object[]{"Broken Hill"},
				new Object[]{"Tamworth"},
				new Object[]{"Griffith"},
				new Object[]{"Wollongong"},
				new Object[]{"Port Macquarie"},
				new Object[]{"Moree"},
				new Object[]{"Orange"},
				new Object[]{"Richmond"},
				new Object[]{"Bathurst"},
				new Object[]{"Newcastle"},
				new Object[]{"Nowra"},
				new Object[]{"Woy Woy"},
				new Object[]{"Maitland"},
				new Object[]{"Hay"},
				new Object[]{"Bourke"},
				new Object[]{"Lightning Ridge"},
				new Object[]{"Coffs Harbour"},
				new Object[]{"All"}
			},
			new Object[]{
				"VIC",
				new Object[]{"Melbourne"},
				new Object[]{"Wangaratta"},
				new Object[]{"Broken Hill"},
				new Object[]{"Albury"},
				new Object[]{"Ballarat"},
				new Object[]{"Bendigo"},
				new Object[]{"Horsham"},
				new Object[]{"Portland"},
				new Object[]{"Geelong"},
				new Object[]{"Shepparton"},
				new Object[]{"Hamilton"},
				new Object[]{"Morewell"}
			},
			new Object[]{"SA"},
			new Object[]{"NT"},
			new Object[]{"QLD"},
			new Object[]{"WA"},
			new Object[]{"TAS"}
		};

		return buildOrgHierarchyTree(data);
	}

	/**
	 * Builds one level of the org hierarchy tree.
	 *
	 * The data parameter should either contain a single String, or a String plus data arrays for child nodes.
	 *
	 * @param data the node data.
	 * @return the tree node created from the data.
	 */
	private StringTreeNode buildOrgHierarchyTree(final Object[] data) {
		StringTreeNode childNode = new StringTreeNode((String) data[0]);

		if (data.length > 1) {
			for (int i = 1; i < data.length; i++) {
				childNode.add(buildOrgHierarchyTree((Object[]) data[i]));
			}
		}

		return childNode;
	}

	/**
	 * Retrieves a sub menu by its text.
	 *
	 * @param text the text to search for.
	 * @param node the current node in the WComponent tree.
	 * @return the sub menu with the given text, or null if not found.
	 */
	private WSubMenu getSubMenuByText(final String text, final WComponent node) {

		if (node instanceof WSubMenu) {
			WSubMenu subMenu = (WSubMenu) node;
			if (text.equals(subMenu.getText())) {
				return subMenu;
			}
			for (MenuItem item : subMenu.getMenuItems()) {
				WSubMenu result = getSubMenuByText(text, item);
				if (result != null) {
					return result;
				}
			}
		} else if (node instanceof WMenu) {
			WMenu menu = (WMenu) node;
			for (MenuItem item : menu.getMenuItems()) {
				WSubMenu result = getSubMenuByText(text, item);
				if (result != null) {
					return result;
				}
			}
		}

		return null;
	}
}
