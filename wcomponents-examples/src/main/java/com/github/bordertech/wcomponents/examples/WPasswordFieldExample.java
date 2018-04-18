package com.github.bordertech.wcomponents.examples;

import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WFieldLayout;
import com.github.bordertech.wcomponents.WPasswordField;
import com.github.bordertech.wcomponents.autocomplete.type.Password;

/**
 * Shows the various properties of WPasswordField.
 *
 * @author Mark Reeves
 * @since 1.3.0
 */
public class WPasswordFieldExample extends WContainer {

	/**
	 * Construct example.
	 */
	public WPasswordFieldExample() {
		WPasswordField pwfield;

		WFieldLayout layout = new WFieldLayout(WFieldLayout.LAYOUT_STACKED);
		add(layout);
		layout.addField("Normal input", new WPasswordField());


		pwfield = new WPasswordField();
		pwfield.setText("Initial value");
		layout.addField("Normal input with value should not echo value", pwfield);

		pwfield = new WPasswordField();
		pwfield.setDisabled(true);
		layout.addField("Disabled input", pwfield);

		pwfield = new WPasswordField();
		pwfield.setText("Initial value");
		pwfield.setDisabled(true);
		layout.addField("Disabled input with value should not echo value", pwfield);

		pwfield = new WPasswordField();
		pwfield.setMandatory(true);
		layout.addField("Mandatory input", pwfield);

		pwfield = new WPasswordField();
		pwfield.setReadOnly(true);
		layout.addField("Read only input", pwfield);

		pwfield = new WPasswordField();
		pwfield.setText("Initial value");
		pwfield.setReadOnly(true);
		layout.addField("Read only input with value should not echo value", pwfield);

		// constraints
		pwfield = new WPasswordField();
		pwfield.setMaxLength(20);
		layout.addField("Max length 20", pwfield);
		pwfield = new WPasswordField();
		pwfield.setMinLength(20);
		layout.addField("Min length 20", pwfield);
		pwfield = new WPasswordField();
		pwfield.setPlaceholder("type here");
		layout.addField("placeholder", pwfield);

		// autocomplete
		pwfield = new WPasswordField();
		pwfield.setAutocomplete(Password.CURRENT);
		layout.addField("with autocomplete current", pwfield);
		pwfield = new WPasswordField();
		pwfield.setAutocomplete(Password.NEW);
		layout.addField("with autocomplete new", pwfield);
		pwfield = new WPasswordField();
		pwfield.setAutocomplete(Password.CURRENT);
		pwfield.addAutocompleteSection("anothersection");
		layout.addField("with autocomplete current in a section", pwfield);
		pwfield = new WPasswordField();
		pwfield.setAutocomplete(Password.NEW);
		pwfield.addAutocompleteSection("anothersection");
		layout.addField("with autocomplete new in a section", pwfield);
		pwfield = new WPasswordField();
		pwfield.setAutocompleteOff();
		layout.addField("with autocomplete off", pwfield);


	}

}
