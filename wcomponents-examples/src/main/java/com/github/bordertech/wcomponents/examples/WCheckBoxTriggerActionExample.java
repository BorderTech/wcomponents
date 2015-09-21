package com.github.bordertech.wcomponents.examples;

import com.github.bordertech.wcomponents.Action;
import com.github.bordertech.wcomponents.ActionEvent;
import com.github.bordertech.wcomponents.Margin;
import com.github.bordertech.wcomponents.WAjaxControl;
import com.github.bordertech.wcomponents.WCheckBox;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WFieldLayout;
import com.github.bordertech.wcomponents.WFieldSet;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WText;

/**
 * This example demonstrates the use of {@link WCheckBox#setActionOnChange(Action)}. The action associated with the
 * checkbox gets executed whenever the checkbox selection changes.
 *
 * @author Christina Harris
 * @since 1.0.0
 * @author Mark Reeves
 * @since 1.0.0
 */
public class WCheckBoxTriggerActionExample extends WContainer {

	/**
	 * The 'breakfast' check box.
	 */
	private final WCheckBox breakfastCheckbox = new WCheckBox();

	/**
	 * The 'lunch' check box.
	 */
	private final WCheckBox lunchCheckbox = new WCheckBox();

	/**
	 * The 'dinner' check box.
	 */
	private final WCheckBox dinnerCheckbox = new WCheckBox();

	/**
	 * The panel which contains the information text display.
	 */
	private final WPanel infoPanel = new WPanel();

	/**
	 * Creates a WCheckBoxTriggerActionExample.
	 */
	public WCheckBoxTriggerActionExample() {
		WFieldSet fset = new WFieldSet("Choose one or more meal[s]");
		add(fset);
		WFieldLayout flayout = new WFieldLayout(WFieldLayout.LAYOUT_STACKED);
		fset.add(flayout);
		flayout.setLabelWidth(0);
		flayout.addField("Breakfast", breakfastCheckbox);
		flayout.addField("Lunch", lunchCheckbox);
		flayout.addField("Dinner", dinnerCheckbox);

		final WText info = new WText();
		add(infoPanel);
		infoPanel.add(info);
		infoPanel.setMargin(new Margin(12, 0, 0, 0));

		breakfastCheckbox.setActionOnChange(new Action() {

			@Override
			public void execute(final ActionEvent event) {
				if (breakfastCheckbox.isSelected()) {
					info.setText("Breakfast selected");

				} else {
					info.setText("Breakfast unselected");
				}

			}

		});

		lunchCheckbox.setActionOnChange(new Action() {

			@Override
			public void execute(final ActionEvent event) {
				if (lunchCheckbox.isSelected()) {
					info.setText("Lunch selected");

				} else {
					info.setText("Lunch unselected");
				}
			}
		});

		dinnerCheckbox.setActionOnChange(new Action() {

			@Override
			public void execute(final ActionEvent event) {
				if (dinnerCheckbox.isSelected()) {
					info.setText("Dinner selected");

				} else {
					info.setText("Dinner unselected");
				}
			}
		});

		/*
         * NOTE: you should not use WCheckBox to submit a form, so if you need to
         * trigger an action on change it should be done using AJAX.
		 */
		WAjaxControl ajaxControl = new WAjaxControl(breakfastCheckbox, infoPanel);
		add(ajaxControl);
		ajaxControl = new WAjaxControl(lunchCheckbox, infoPanel);
		add(ajaxControl);
		ajaxControl = new WAjaxControl(dinnerCheckbox, infoPanel);
		add(ajaxControl);

	}

	/**
	 * @return the 'breakfast' check box.
	 */
	public WCheckBox getBreakfastCheckBox() {
		return breakfastCheckbox;
	}

	/**
	 * @return the 'lunch' check box.
	 */
	public WCheckBox getLunchCheckBox() {
		return lunchCheckbox;
	}

	/**
	 * @return the 'dinner' check box.
	 */
	public WCheckBox getDinnerCheckBox() {
		return dinnerCheckbox;
	}

	/**
	 * @return the panel which contains the information text.
	 */
	public WPanel getInformationTextBox() {
		return infoPanel;
	}
}
