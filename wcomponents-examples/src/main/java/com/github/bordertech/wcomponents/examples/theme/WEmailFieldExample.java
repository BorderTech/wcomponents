package com.github.bordertech.wcomponents.examples.theme;

import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WEmailField;
import com.github.bordertech.wcomponents.WFieldLayout;
import com.github.bordertech.wcomponents.autocomplete.type.Email;

/**
 * Example using WEmailFiled.
 */
public class WEmailFieldExample extends WContainer {

	private WEmailField eField;

	/**
	 * Construct example.
	 */
	public WEmailFieldExample() {

		WFieldLayout layout = new WFieldLayout();
		layout.setLabelWidth(25);
		add(layout);

		layout.addField("Plain email address", new WEmailField());

		eField = new WEmailField();
		eField.setDisabled(true);
		layout.addField("Disabled email address field", eField);

		eField = new WEmailField();
		eField.setReadOnly(true);
		layout.addField("Read-only email address field", eField);

		eField = new WEmailField();
		eField.setText("user@example.com");
		layout.addField("email address field with data", eField);

		eField = new WEmailField();
		eField.setText("user@example.com");
		eField.setDisabled(true);
		layout.addField("Disabled email address field with data", eField);

		eField = new WEmailField();
		eField.setText("user@example.com");
		eField.setReadOnly(true);
		layout.addField("Read-only email address field with data", eField);



		// constraints
		eField = new WEmailField();
		eField.setMaxLength(254);
		eField.setPlaceholder("name@example.com");
		layout.addField("Max length 254", eField);

		// autocomplete
		for (Email email : Email.values()) {
			eField = new WEmailField();
			eField.setAutocomplete(email);
			layout.addField("auto-fill hint set to ".concat(email.getValue()), eField);
		}

		eField = new WEmailField();
		eField.setAutocompleteOff();
		layout.addField("Autocomplete off", eField);
	}

}
