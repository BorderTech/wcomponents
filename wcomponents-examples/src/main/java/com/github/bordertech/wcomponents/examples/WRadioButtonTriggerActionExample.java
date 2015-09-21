package com.github.bordertech.wcomponents.examples;

import com.github.bordertech.wcomponents.Action;
import com.github.bordertech.wcomponents.ActionEvent;
import com.github.bordertech.wcomponents.RadioButtonGroup;
import com.github.bordertech.wcomponents.WAjaxControl;
import com.github.bordertech.wcomponents.WFieldLayout;
import com.github.bordertech.wcomponents.WFieldSet;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WRadioButton;
import com.github.bordertech.wcomponents.WText;
import com.github.bordertech.wcomponents.layout.FlowLayout;
import com.github.bordertech.wcomponents.layout.FlowLayout.Alignment;
import java.util.Date;

/**
 *
 * This example demonstrates how an {@link Action} can be associated with a {@link RadioButtonGroup}. The action is
 * executed when a radio button selection within the group changes. Use
 * {@link RadioButtonGroup#setActionOnChange(Action)} to add the action to the group.
 *
 * @author Christina Harris
 * @since 1/04/2008
 */
public class WRadioButtonTriggerActionExample extends WPanel {

	/**
	 * The container used to hold the textual information display.
	 */
	private final WPanel textBox = new WPanel();

	/**
	 * Creates a WRadioButtonTriggerActionExample.
	 */
	public WRadioButtonTriggerActionExample() {
		setLayout(new FlowLayout(Alignment.VERTICAL));

		RadioButtonGroup mealSelections = new RadioButtonGroup();
		add(mealSelections);

		final WRadioButton rb1 = mealSelections.addRadioButton("Breakfast");
		final WRadioButton rb2 = mealSelections.addRadioButton("Lunch");
		final WRadioButton rb3 = mealSelections.addRadioButton("Dinner");

		rb2.setSelected(true);

		WFieldSet fset = new WFieldSet("Select a meal");
		add(fset);
		fset.setMargin(new com.github.bordertech.wcomponents.Margin(0, 0, 12, 0));

		WFieldLayout flay = new WFieldLayout(WFieldLayout.LAYOUT_STACKED);
		fset.add(flay);
		flay.setLabelWidth(0);
		flay.addField("Breakfast", rb1);
		flay.addField("Lunch", rb2);
		flay.addField("Dinner", rb3);

		final WText text1 = new WText();
		/*
         * NOTE: you should never use submitOnChange with a WRadioButton
		 */
		fset.add(new WAjaxControl(mealSelections, textBox));

		mealSelections.setActionOnChange(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				String selection = null;

				if (rb1.isSelected()) {
					selection = "Breakfast selected";
				}

				if (rb2.isSelected()) {
					selection = "Lunch selected";
				}

				if (rb3.isSelected()) {
					selection = "Dinner selected";
				}

				text1.setText(selection + " : " + (new Date()).toString());
			}
		});

		textBox.add(text1);
		add(textBox);
		textBox.setMargin(new com.github.bordertech.wcomponents.Margin(0, 0, 12, 0));
	}

	/**
	 * @return the container for the information text.
	 */
	public WPanel getInformationTextBox() {
		return textBox;
	}
}
