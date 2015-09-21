package com.github.bordertech.wcomponents.examples.validation.fields;

import com.github.bordertech.wcomponents.Action;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WCancelButton;
import com.github.bordertech.wcomponents.WMessageBox;
import com.github.bordertech.wcomponents.WMultiSelect;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.layout.FlowLayout;
import com.github.bordertech.wcomponents.layout.FlowLayout.Alignment;

/**
 * Test component used to select an option from a list and provide the result to another component.
 *
 * @author Adam Millard
 */
public class SelectorPanel extends WPanel {

	/**
	 * The list of options to select from.
	 */
	private final WMultiSelect options;
	/**
	 * The ok button.
	 */
	private final WButton okBtn;
	/**
	 * The cancel button.
	 */
	private final WButton cancelBtn;

	/**
	 * Creates a SelectorPanel.
	 */
	public SelectorPanel() {
		setLayout(new FlowLayout(Alignment.VERTICAL));

		add(new WMessageBox(WMessageBox.INFO, "Selector button clicked. Select something."));

		options = new WMultiSelect(new String[]{"Circle", "Oval", "Rectangle", "Square", "Triangle"});
		add(options);

		okBtn = new WButton("OK", 'O');
		add(okBtn);
		cancelBtn = new WCancelButton();
		add(cancelBtn);
	}

	/**
	 * Sets the 'ok' button's action.
	 *
	 * @param action the ok action.
	 */
	public void setOKAction(final Action action) {
		okBtn.setAction(action);
	}

	/**
	 * Sets the 'cancel' button's action.
	 *
	 * @param action the cancel action.
	 */
	public void setCancelAction(final Action action) {
		cancelBtn.setAction(action);
	}

	/**
	 * Retrieves the selected option.
	 *
	 * @return the selected option.
	 */
	public String getSelectedOption() {
		return options.getValueAsString();
	}
}
