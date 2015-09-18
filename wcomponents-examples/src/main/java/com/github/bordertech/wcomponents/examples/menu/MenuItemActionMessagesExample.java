package com.github.bordertech.wcomponents.examples.menu;

import com.github.bordertech.wcomponents.Action;
import com.github.bordertech.wcomponents.ActionEvent;
import com.github.bordertech.wcomponents.Margin;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WFieldLayout;
import com.github.bordertech.wcomponents.WMenu;
import com.github.bordertech.wcomponents.WMenuItem;
import com.github.bordertech.wcomponents.WMessages;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WSubMenu;
import com.github.bordertech.wcomponents.WTextField;

/**
 * Demonstrate {@link WMenuItem} action messages to confirm submit, cancel or navigate to a new URL.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class MenuItemActionMessagesExample extends WContainer {

	/**
	 * Construct example.
	 */
	public MenuItemActionMessagesExample() {

		final WMessages messages = new WMessages();
		add(messages);

		WMenu menu = new WMenu();
		add(menu);

		WSubMenu sub = new WSubMenu("Menu items with an Action");
		menu.add(sub);

		// Submit - Confirm
		WMenuItem item = new WMenuItem("Confirm");
		sub.add(item);
		item.setToolTip("Confirm Action");
		item.setMessage("Do you want to continue?");
		item.setAction(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				messages.success("Confirm menu item clicked.");
			}
		});

		// Submit - Cancel
		item = new WMenuItem("Cancel");
		sub.add(item);
		item.setToolTip("Cancel message if form changed");
		item.setMessage("Do you want to cancel?");
		item.setCancel(true);
		item.setAction(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				messages.success("Cancel menu item clicked.");
			}
		});

		// Submit - No Message
		item = new WMenuItem("No message");
		sub.add(item);
		item.setToolTip("Submit with no message");
		item.setAction(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				messages.success("Submit no message menu item clicked.");
			}
		});

		sub = new WSubMenu("Navigate new window");
		menu.add(sub);

		// Navigate new window - Confirm
		item = new WMenuItem("Confirm");
		sub.add(item);
		item.setToolTip("Url in new window with message");
		item.setMessage("Do you want to open new window?");
		item.setUrl("http://www.ubuntu.com/");
		item.setTargetWindow("NewWindow");

		// Navigate new window - Cancel
		item = new WMenuItem("Cancel");
		sub.add(item);
		item.setToolTip("Url in new window with cancel message");
		item.setMessage("Do you want to open new window?");
		item.setCancel(true);
		item.setUrl("http://www.ubuntu.com/");
		item.setTargetWindow("NewWindow");

		// Navigate new window - No Message
		item = new WMenuItem("No message");
		sub.add(item);
		item.setToolTip("Url in new window with no message");
		item.setUrl("http://www.ubuntu.com/");
		item.setTargetWindow("NewWindow");

		sub = new WSubMenu("Navigate same window");
		menu.add(sub);

		// Navigate same window - Confirm
		item = new WMenuItem("Confirm");
		sub.add(item);
		item.setToolTip("Same window with message");
		item.setMessage("Do you want to leave?");
		item.setUrl("http://www.ubuntu.com/");

		// Navigate same window - Cancel
		item = new WMenuItem("Cancel");
		sub.add(item);
		item.setToolTip("Same window with cancel message");
		item.setMessage("Do you want to leave?");
		item.setCancel(true);
		item.setUrl("http://www.ubuntu.com/");

		// Navigate same window - No message
		item = new WMenuItem("No message");
		sub.add(item);
		item.setToolTip("Same window with no message");
		item.setUrl("http://www.ubuntu.com/");

		WPanel panel = new WPanel();
		panel.setMargin(new Margin(12, 0, 0, 0));
		add(panel);

		WFieldLayout layout = new WFieldLayout();
		layout.setLabelWidth(30);
		panel.add(layout);

		final WTextField textField = new WTextField();
		textField.setMandatory(true);
		layout.addField("Text Field", textField);

	}

}
