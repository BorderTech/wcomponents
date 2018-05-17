package com.github.bordertech.wcomponents.examples.theme;

import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WFieldLayout;
import com.github.bordertech.wcomponents.WPhoneNumberField;
import com.github.bordertech.wcomponents.autocomplete.segment.PhoneFormat;
import com.github.bordertech.wcomponents.autocomplete.type.Telephone;
import com.github.bordertech.wcomponents.examples.common.ExplanatoryText;

/**
 * Example using WPhoneNumberField.
 */
public class WPhoneNumberFieldExample extends WContainer {

	private WPhoneNumberField field;

	/**
	 * Construct example.
	 */
	public WPhoneNumberFieldExample() {
		WFieldLayout layout = new WFieldLayout();
		layout.setLabelWidth(25);
		add(layout);

		layout.addField("Plain phone number", new WPhoneNumberField());

		field = new WPhoneNumberField();
		field.setDisabled(true);
		layout.addField("Disabled phone number field", field);

		field = new WPhoneNumberField();
		field.setReadOnly(true);
		layout.addField("Read-only phone number field", field);

		field = new WPhoneNumberField();
		field.setText("+61.99999999");
		layout.addField("Phone number field with data", field);

		field = new WPhoneNumberField();
		field.setText("+61.99999999");
		field.setDisabled(true);
		layout.addField("Disabled phone number field with data", field);

		field = new WPhoneNumberField();
		field.setText("+61.99999999");
		field.setReadOnly(true);
		layout.addField("Read-only phone number field with data", field);

		field = new WPhoneNumberField();
		field.setPlaceholder("ring ring");
		layout.addField("With placeholder", field);

		field = new WPhoneNumberField();
		field.setFullPhoneAutocomplete();
		layout.addField("With default autocomplete", field);

		field = new WPhoneNumberField();
		field.setAutocompleteOff();
		layout.addField("With autocomplete off", field);

		field = new WPhoneNumberField();
		field.setFullPhoneAutocomplete(PhoneFormat.MOBILE);
		layout.addField("With mobile autocomplete", field);

		field = new WPhoneNumberField();
		field.setFullPhoneAutocomplete((PhoneFormat)null);
		field.addAutocompleteSection("foo");
		layout.addField("With autocomplete for section foo", field);

		field = new WPhoneNumberField();
		field.setFullPhoneAutocomplete(PhoneFormat.MOBILE);
		field.addAutocompleteSection("foo");
		layout.addField("With mobile autocomplete for section foo", field);

		field = new WPhoneNumberField();
		field.setAutocomplete(Telephone.LOCAL, PhoneFormat.MOBILE);
		field.addAutocompleteSection("foo");
		layout.addField("With (local) mobile autocomplete for section foo", field);

		field = new WPhoneNumberField();
		field.setLocalPhoneAutocomplete();
		layout.addField("With (local) autocomplete", field);

		field = new WPhoneNumberField();
		field.setLocalPhoneAutocomplete(PhoneFormat.MOBILE);
		layout.addField("With (local) mobile phone autocomplete", field);

		add(new ExplanatoryText(
				"You will notice that a WPhoneNumberField when read only outputs a link with a protocol of tel."
				+ " This will signal to the browser to launch a soft phone system if available."));
	}

}
