package com.github.bordertech.wcomponents.examples.menu;

import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WDecoratedLabel;
import com.github.bordertech.wcomponents.WImage;
import com.github.bordertech.wcomponents.WMenu;
import com.github.bordertech.wcomponents.WMenu.SelectMode;
import com.github.bordertech.wcomponents.WMenuItem;
import com.github.bordertech.wcomponents.WMenuItemGroup;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WStyledText;
import com.github.bordertech.wcomponents.WSubMenu;
import com.github.bordertech.wcomponents.WText;
import com.github.bordertech.wcomponents.layout.FlowLayout;
import com.github.bordertech.wcomponents.layout.FlowLayout.Alignment;
import com.github.bordertech.wcomponents.util.TreeNode;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * This component demonstrates the usage of a {@link WMenu.MenuType#TREE Tree} {@link WMenu}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class TreeMenuExample extends WPanel {

	/**
	 * Creates a TreeMenuExample.
	 */
	public TreeMenuExample() {
		setLayout(new FlowLayout(Alignment.VERTICAL, 0, 24));
		add(new WText("Example java object hierarchy"));

		WPanel content = new WPanel(WPanel.Type.BLOCK);
		WText selectedMenuText = new WText();
		content.add(new WStyledText("Selected item: ", WStyledText.Type.EMPHASISED));
		content.add(selectedMenuText);
		add(content);
		add(buildTreeMenu(selectedMenuText));
		//simple tree menu showing WDecoratedLabel
		add(buildTreeMenuWithDecoratedLabel());
	}

	/**
	 * Builds up a tree menu for inclusion in the example.
	 *
	 * @param selectedMenuText the WText to display the selected menu item.
	 * @return a tree menu for the example.
	 */
	private WMenu buildTreeMenu(final WText selectedMenuText) {
		WMenu menu = new WMenu(WMenu.MenuType.TREE);
		menu.setSelectMode(SelectMode.SINGLE);

		mapTreeHierarchy(menu, createExampleHierarchy(), selectedMenuText);

		return menu;
	}

	/**
	 * Recursively maps a tree hierarchy to a hierarchical menu.
	 *
	 * @param currentComponent the current component in the menu.
	 * @param currentNode the current node in the tree.
	 * @param selectedMenuText the WText to display the selected menu item.
	 */
	private void mapTreeHierarchy(final WComponent currentComponent,
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

			if (currentComponent instanceof WMenu) {
				((WMenu) currentComponent).add(subMenu);
			} else {
				((WSubMenu) currentComponent).add(subMenu);
			}

			// Expand the first couple of levels in the tree by default.
			if (currentNode.getLevel() < 2) {
				subMenu.setOpen(true);
			}

			for (Iterator<TreeNode> i = currentNode.children(); i.hasNext();) {
				mapTreeHierarchy(subMenu, (StringTreeNode) i.next(), selectedMenuText);
			}
		}
	}

	/**
	 * Creates an example hierarchy, showing the WMenu API.
	 *
	 * @return the root of the tree.
	 */
	private static StringTreeNode createExampleHierarchy() {
		StringTreeNode root = new StringTreeNode(Object.class.getName());
		Map<String, StringTreeNode> nodeMap = new HashMap<>();
		nodeMap.put(root.getData(), root);

		// The classes to show in the hierarchy
		Class<?>[] classes = new Class[]{
			WMenu.class,
			WMenuItem.class,
			WSubMenu.class,
			WMenuItemGroup.class,
			WText.class,
			WText.class
		};

		for (Class<?> clazz : classes) {
			StringTreeNode childNode = new StringTreeNode(clazz.getName());
			nodeMap.put(childNode.getData(), childNode);

			for (Class<?> parentClass = clazz.getSuperclass(); parentClass != null; parentClass = parentClass.
					getSuperclass()) {
				StringTreeNode parentNode = nodeMap.get(parentClass.getName());

				if (parentNode == null) {
					parentNode = new StringTreeNode(parentClass.getName());
					nodeMap.put(parentNode.getData(), parentNode);
					parentNode.add(childNode);
					childNode = parentNode;
				} else {
					parentNode.add(childNode); // already have this node hierarchy
					break;
				}
			}
		}

		return root;
	}

	/**
	 * Tree menu containing image in the items. This example demonstrates creating {@link WSubMenu} and
	 * {@link WMenuItem} components with {@link WDecoratedLabel}.
	 *
	 * @return menu with a decorated label
	 */
	private WMenu buildTreeMenuWithDecoratedLabel() {
		WMenu menu = new WMenu(WMenu.MenuType.TREE);

		WDecoratedLabel dLabel = new WDecoratedLabel(null, new WText("Settings Menu"), new WImage(
				"/image/settings.png", "settings"));
		WSubMenu settings = new WSubMenu(dLabel);
		settings.setMode(WSubMenu.MenuMode.LAZY);
		menu.add(settings);
		settings.add(new WMenuItem(new WDecoratedLabel(null, new WText("Account Settings"),
				new WImage("/image/user-properties.png", "user properties"))));
		settings.add(new WMenuItem(new WDecoratedLabel(null, new WText("Personal Details"),
				new WImage("/image/user.png", "user"))));
		WSubMenu addressSub = new WSubMenu(new WDecoratedLabel(null, new WText("Address Details"),
				new WImage("/image/address-book-open.png", "address book")));
		addressSub.setMode(WSubMenu.MenuMode.LAZY);
		settings.add(addressSub);
		addressSub.add(new WMenuItem(new WDecoratedLabel(null, new WText("Home Address"),
				new WImage("/image/home.png", "home"))));
		addressSub.add(new WMenuItem(new WDecoratedLabel(null, new WText("Work Address"),
				new WImage("/image/wrench.png", "work"))));
		addressSub.add(new WMenuItem(new WDecoratedLabel(null, new WText("Postal Address"),
				new WImage("/image/mail-post.png", "postal"))));
		return menu;
	}
}
