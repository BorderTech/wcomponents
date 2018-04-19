package com.github.bordertech.wcomponents.examples;

import com.github.bordertech.wcomponents.Action;
import com.github.bordertech.wcomponents.ActionEvent;
import com.github.bordertech.wcomponents.HeadingLevel;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WDropdown;
import com.github.bordertech.wcomponents.WFieldLayout;
import com.github.bordertech.wcomponents.WHeading;
import com.github.bordertech.wcomponents.WMessageBox;
import com.github.bordertech.wcomponents.WText;
import com.github.bordertech.wcomponents.examples.common.ExplanatoryText;

/**
 * This example is to see how {@link WDropdown} cope with options that contain spaces.
 *
 * @author Martin Shevchenko
 * @since 1.0.0
 * @author Mark Reeves
 * @since 1.0.0
 */
public class WDropdownSpaceHandlingExample extends WContainer {

	private static final String NO_SPACE = "NoSpace";
	private static final String LEADING_SPACE = " LeadingSpace";
	private static final String TRAILING_SPACE = "TrailingSpace ";
	private static final String DOUBLE_SPACE = "Double  Space";

	private final WText text;
	private final WDropdown drop;
	private final WButton submit;

	/**
	 * Creates a WDropdownSpaceHandlingExample.
	 */
	public WDropdownSpaceHandlingExample() {
		drop = new WDropdown();
		drop.setOptions(new String[]{null, NO_SPACE, LEADING_SPACE, TRAILING_SPACE, DOUBLE_SPACE});

		text = new WText();

		submit = new WButton("Submit");
		submit.setAction(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				String selected = (String) drop.getSelected();
				if (selected != null) {
					selected = selected.replaceAll(" ", "%20");
				}
				text.setText(selected);
			}
		});

		setupUI();
	}

	/**
	 * Add controls to the UI.
	 */
	private void setupUI() {
		add(new WMessageBox(WMessageBox.WARN,
				"This example is for framework testing ONLY and must not be used as an example of how to set up any UI controls"));
		WFieldLayout layout = new WFieldLayout();
		add(layout);

		layout.addField("Select an option with spaces", drop);
		layout.addField(submit);
		add(new ExplanatoryText("In the result output space characters are replaced with '%20'."));
		add(new WHeading(HeadingLevel.H2, "Result Text"));
		add(text);
	}
}
