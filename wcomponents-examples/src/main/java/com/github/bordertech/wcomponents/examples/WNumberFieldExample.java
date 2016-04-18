package com.github.bordertech.wcomponents.examples;

import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WFieldLayout;
import com.github.bordertech.wcomponents.WNumberField;

/**
 * Shows the various properties of WNumberField.
 *
 * @author Mark Reeves
 * @since 1.1.3
 */
public class WNumberFieldExample extends WContainer {

	/**
	 * Construct example.
	 */
	public WNumberFieldExample() {
		WNumberField numberField;

		WFieldLayout layout = new WFieldLayout(WFieldLayout.LAYOUT_STACKED);
		add(layout);
		layout.addField("Normal input", new WNumberField());


		numberField = new WNumberField();
		numberField.setNumber(3);
		layout.addField("Normal input with value", numberField);

		numberField = new WNumberField();
		numberField.setDisabled(true);
		layout.addField("Disabled input", numberField);

		numberField = new WNumberField();
		numberField.setNumber(3);
		numberField.setDisabled(true);
		layout.addField("Disabled input with value", numberField);

		numberField = new WNumberField();
		numberField.setMandatory(true);
		layout.addField("Mandatory input", numberField);

		numberField = new WNumberField();
		numberField.setReadOnly(true);
		layout.addField("Read only input", numberField);

		numberField = new WNumberField();
		numberField.setNumber(3);
		numberField.setReadOnly(true);
		layout.addField("Read only input with value", numberField);

		// constraints
		numberField = new WNumberField();
		numberField.setMaxValue(200);
		layout.addField("Max 200", numberField);
		numberField = new WNumberField();
		numberField.setMinValue(0);
		layout.addField("Min 0", numberField);
		numberField = new WNumberField();
		numberField.setMinValue(0);
		numberField.setStep(5);
		layout.addField("Min 0, step 5", numberField);
	}

}
