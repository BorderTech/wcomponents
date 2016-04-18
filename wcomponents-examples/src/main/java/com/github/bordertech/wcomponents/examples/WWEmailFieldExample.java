package com.github.bordertech.wcomponents.examples;

import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WEmailField;
import com.github.bordertech.wcomponents.WFieldLayout;

/**
 * Shows the various properties of WEmailField.
 *
 * @author Mark Reeves
 * @since 1.1.3
 */
public class WWEmailFieldExample extends WContainer {

	/**
	 * Construct example.
	 */
	public WWEmailFieldExample() {
		WEmailField emailField;

		WFieldLayout layout = new WFieldLayout(WFieldLayout.LAYOUT_STACKED);
		add(layout);
		layout.addField("Normal input", new WEmailField());


		emailField = new WEmailField();
		emailField.setText("user@example.com");
		layout.addField("Normal input with value", emailField);

		emailField = new WEmailField();
		emailField.setDisabled(true);
		layout.addField("Disabled input", emailField);

		emailField = new WEmailField();
		emailField.setText("user@example.com");
		emailField.setDisabled(true);
		layout.addField("Disabled input with value", emailField);

		emailField = new WEmailField();
		emailField.setMandatory(true);
		layout.addField("Mandatory input", emailField);

		emailField = new WEmailField();
		emailField.setReadOnly(true);
		layout.addField("Read only input", emailField);

		emailField = new WEmailField();
		emailField.setText("user@example.com");
		emailField.setReadOnly(true);
		layout.addField("Read only input with value", emailField);

		// constraints
		emailField = new WEmailField();
		emailField.setMaxLength(254);
		layout.addField("Max length 254", emailField);
	}

}
