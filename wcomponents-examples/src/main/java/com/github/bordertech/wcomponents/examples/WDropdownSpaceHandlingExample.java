package com.github.bordertech.wcomponents.examples;

import com.github.bordertech.wcomponents.Action;
import com.github.bordertech.wcomponents.ActionEvent;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WCheckBoxSelect;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WDropdown;
import com.github.bordertech.wcomponents.WHeading;
import com.github.bordertech.wcomponents.WHorizontalRule;
import com.github.bordertech.wcomponents.WMessageBox;
import com.github.bordertech.wcomponents.WStyledText;
import com.github.bordertech.wcomponents.WText;

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

	/**
	 * Creates a WDropdownSpaceHandlingExample.
	 */
	public WDropdownSpaceHandlingExample() {
		add(new WMessageBox(WMessageBox.WARN,
				"This example is for framework testing ONLY and must not be used as an example of how to set up any UI controls"));
		final WText text = new WText();

		final WDropdown drop = new WDropdown();
		drop.setToolTip("Select an option with spaces");
		drop.setOptions(new String[]{null, NO_SPACE, LEADING_SPACE, TRAILING_SPACE, DOUBLE_SPACE});
		add(drop);

		WButton submit = new WButton("Submit");
		add(submit);

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

		WStyledText explanation = new WStyledText(
				"In the result output space characters are replaced with '%20'.");
		explanation.setWhitespaceMode(WStyledText.WhitespaceMode.PARAGRAPHS);
		add(explanation);
		add(new WHeading(WHeading.MAJOR, "Result Text"));
		add(text);
		add(new WHorizontalRule());

		add(new WHeading(WHeading.MAJOR, "Test of spaces in options of WCheckBoxSelect"));
		explanation = new WStyledText(
				"This control is here only for the purposes of unit testing and serves no function with respect to the output of the example above.");
		explanation.setWhitespaceMode(WStyledText.WhitespaceMode.PARAGRAPHS);
		add(explanation);
		WCheckBoxSelect multiSelectGroup = new WCheckBoxSelect();
		multiSelectGroup.setFrameless(true);
		multiSelectGroup.setToolTip("Select one or more options with spaces");
		multiSelectGroup.setOptions(
				new String[]{NO_SPACE, LEADING_SPACE, TRAILING_SPACE, DOUBLE_SPACE});
		add(multiSelectGroup);
	}
}
