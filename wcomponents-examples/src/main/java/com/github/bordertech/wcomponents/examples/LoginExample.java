package com.github.bordertech.wcomponents.examples;

import com.github.bordertech.wcomponents.Margin;
import com.github.bordertech.wcomponents.Size;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WFieldLayout;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WPasswordField;
import com.github.bordertech.wcomponents.WSection;
import com.github.bordertech.wcomponents.WTextField;

/**
 * Demonstrate a basic login screen.
 */
public class LoginExample extends WSection {

	/**
	 * Construct example.
	 */
	public LoginExample() {
		super("Log in");
		setMargin(new Margin(Size.XL));
		WPanel content = getContent();
		WFieldLayout layout = new WFieldLayout(WFieldLayout.LAYOUT_STACKED);
		content.add(layout);
		layout.addField("Name", new WTextField());
		layout.addField("Password", new WPasswordField());
		layout.addField(new WButton("go"));
	}
}
