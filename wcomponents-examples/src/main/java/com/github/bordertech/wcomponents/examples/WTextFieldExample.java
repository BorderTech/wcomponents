package com.github.bordertech.wcomponents.examples;

import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WFieldLayout;
import com.github.bordertech.wcomponents.WTextField;

/**
 * Shows the various properties of WTextField.
 *
 * @author Mark Reeves
 * @since 1.1.3
 */
public class WTextFieldExample extends WContainer {

	/**
	 * Construct example.
	 */
	public WTextFieldExample() {
		WTextField textfield;

		WFieldLayout layout = new WFieldLayout(WFieldLayout.LAYOUT_STACKED);
		add(layout);
		layout.addField("Normal input", new WTextField());


		textfield = new WTextField();
		textfield.setText("Initial value");
		layout.addField("Normal input with value", textfield);

		textfield = new WTextField();
		textfield.setDisabled(true);
		layout.addField("Disabled input", textfield);

		textfield = new WTextField();
		textfield.setText("Initial value");
		textfield.setDisabled(true);
		layout.addField("Disabled input with value", textfield);

		textfield = new WTextField();
		textfield.setMandatory(true);
		layout.addField("Mandatory input", textfield);

		textfield = new WTextField();
		textfield.setReadOnly(true);
		layout.addField("Read only input", textfield);

		textfield = new WTextField();
		textfield.setText("Initial value");
		textfield.setReadOnly(true);
		layout.addField("Read only input with value", textfield);

		// constraints
		textfield = new WTextField();
		textfield.setMaxLength(20);
		layout.addField("Max length 20", textfield);
		textfield = new WTextField();
		textfield.setMinLength(20);
		layout.addField("Min length 20", textfield);
		textfield = new WTextField();
		textfield.setPattern("\\d+");
		layout.addField("Pattern only numbers", textfield);

	}

}
