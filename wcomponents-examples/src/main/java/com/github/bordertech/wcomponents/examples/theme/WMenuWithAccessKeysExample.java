package com.github.bordertech.wcomponents.examples.theme;

import com.github.bordertech.wcomponents.Action;
import com.github.bordertech.wcomponents.ActionEvent;
import com.github.bordertech.wcomponents.WMenu;
import com.github.bordertech.wcomponents.WMenuItem;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WSubMenu;
import com.github.bordertech.wcomponents.WText;
import com.github.bordertech.wcomponents.layout.FlowLayout;
import com.github.bordertech.wcomponents.layout.FlowLayout.Alignment;

/**
 * Example of a menu bar containing entries with access keys.
 *
 * @author Martin Shevchenko
 * @since 1.0.0
 */
public class WMenuWithAccessKeysExample extends WPanel {

	/**
	 * Creates a WMenuWithAccessKeysExample.
	 */
	public WMenuWithAccessKeysExample() {
		// Wire up actions so we can identify when the menu items are triggered.
		final WText console = new WText("No menu item has been activated.");

		Action reportAction = new Action() {
			@Override
			public void execute(final ActionEvent event) {
				String command = event.getActionCommand();
				console.setText("Activated: " + command);
			}
		};

		// Create the menu with various links
		WMenu bar = new WMenu();

		WSubMenu subMenu = new WSubMenu("Menu containing access keys");
		bar.add(subMenu);

		WMenuItem menuItem = new WMenuItem("My MenuItem", 'I', reportAction);
		menuItem.setActionCommand(menuItem.getText());
		subMenu.add(menuItem);

		menuItem = new WMenuItem("My Button", 'B', reportAction);
		menuItem.setActionCommand(menuItem.getText());
		subMenu.add(menuItem);

		menuItem = new WMenuItem("No access key item", reportAction);
		menuItem.setActionCommand(menuItem.getText());
		subMenu.add(menuItem);

		WMenuItem externalLink = new WMenuItem("My Google Link", "http://www.google.com.au");
		externalLink.setAccessKey('G');
		subMenu.add(externalLink);

		// Layout the UI
		setLayout(new FlowLayout(Alignment.VERTICAL, 0, 5));
		add(bar);
		add(console);
	}
}
