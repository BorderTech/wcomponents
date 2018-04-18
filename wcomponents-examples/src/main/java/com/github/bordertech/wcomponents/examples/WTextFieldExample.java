package com.github.bordertech.wcomponents.examples;

import com.github.bordertech.wcomponents.HeadingLevel;
import com.github.bordertech.wcomponents.Margin;
import com.github.bordertech.wcomponents.Size;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WDateField;
import com.github.bordertech.wcomponents.WDropdown;
import com.github.bordertech.wcomponents.WEmailField;
import com.github.bordertech.wcomponents.WFieldLayout;
import com.github.bordertech.wcomponents.WFieldSet;
import com.github.bordertech.wcomponents.WHeading;
import com.github.bordertech.wcomponents.WLabel;
import com.github.bordertech.wcomponents.WPhoneNumberField;
import com.github.bordertech.wcomponents.WTextField;
import com.github.bordertech.wcomponents.autocomplete.segment.AddressPart;
import com.github.bordertech.wcomponents.autocomplete.segment.AddressType;
import com.github.bordertech.wcomponents.autocomplete.segment.Person;
import com.github.bordertech.wcomponents.autocomplete.segment.PhoneFormat;
import com.github.bordertech.wcomponents.autocomplete.segment.PhonePart;
import com.github.bordertech.wcomponents.autocomplete.type.DateType;
import com.github.bordertech.wcomponents.autocomplete.type.Email;
import com.github.bordertech.wcomponents.autocomplete.type.Telephone;
import com.github.bordertech.wcomponents.examples.common.ExplanatoryText;
import com.github.bordertech.wcomponents.subordinate.Equal;
import com.github.bordertech.wcomponents.subordinate.Mandatory;
import com.github.bordertech.wcomponents.subordinate.Optional;
import com.github.bordertech.wcomponents.subordinate.Rule;
import com.github.bordertech.wcomponents.subordinate.WSubordinateControl;
import java.util.Date;

/**
 * Shows the various properties of WTextField.
 *
 * @author Mark Reeves
 * @since 1.1.3
 */
public final class WTextFieldExample extends WContainer {

	/**
	 * Some array data for the drop downs.
	 */
	private static final String[] STATES_AND_TERRITORIES = {"ACT", "NSW", "NT", "Qld", "SA", "Tas", "Vic", "WA", "Aus Ant T", "Jervis Bay", "CKI"};

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
		textfield = new WTextField();
		textfield.setPlaceholder("type here");
		layout.addField("placeholder", textfield);

		add(new WHeading(HeadingLevel.H2, "Autocomplete"));
		add(new ExplanatoryText("Shows some usages of autocomplete for text fields."));

		WFieldSet autocompleteSet = new WFieldSet("Australian style shipping address");
		autocompleteSet.setMargin(new Margin(Size.LARGE, null, Size.LARGE, null));
		add(autocompleteSet);
		layout = new WFieldLayout(WFieldLayout.LAYOUT_FLAT);
		layout.setLabelWidth(33);
		autocompleteSet.add(layout);
		textfield = new WTextField();
		textfield.setAddressAutocomplete(AddressType.SHIPPING, AddressPart.LINE_1);
		layout.addField("Street address", textfield);

		textfield = new WTextField();
		textfield.setAddressAutocomplete(AddressType.SHIPPING, AddressPart.LINE_2);
		textfield.setPlaceholder("optional");
		layout.addField("Street address line 2", textfield).getLabel().setHidden(true);

		textfield = new WTextField();
		textfield.setAddressAutocomplete(AddressType.SHIPPING, AddressPart.LINE_3);
		textfield.setPlaceholder("optional");
		layout.addField("Street address line 3", textfield).getLabel().setHidden(true);

		textfield = new WTextField();
		textfield.setAddressAutocomplete(AddressType.SHIPPING, AddressPart.LEVEL_2);
		layout.addField("Town", textfield);

		WDropdown state = new WDropdown(STATES_AND_TERRITORIES);
		state.setAddressAutocomplete(AddressType.SHIPPING, AddressPart.LEVEL_1);
		layout.addField("State/Territory", state);

		textfield = new WTextField();
		textfield.setAddressAutocomplete(AddressType.SHIPPING, AddressPart.POSTAL_CODE);
		textfield.setMaxLength(4);
		textfield.setColumns(4);
		textfield.setPattern("[0-9]{4}");
		textfield.setToolTip("four numeric digits");
		layout.addField("Postcode", textfield);

		autocompleteSet = new WFieldSet("Home phone number - several parts");
		autocompleteSet.setMargin(new Margin(Size.LARGE, null, Size.LARGE, null));
		add(autocompleteSet);
		layout = new WFieldLayout(WFieldLayout.LAYOUT_FLAT);
		layout.setLabelWidth(33);
		autocompleteSet.add(layout);

		textfield = new WTextField();
		textfield.setPhoneSegmentAutocomplete(PhoneFormat.HOME, PhonePart.COUNTRY_CODE);
		textfield.setPattern("\\+[1-9][0-9]{0,3}");
		textfield.setToolTip("plus sign (+) followed by 1 to 4 digits");
		textfield.setMaxLength(6);
		textfield.setColumns(6);
		textfield.setPlaceholder("+61");
		layout.addField("Country code", textfield);

		textfield = new WTextField();
		textfield.setPhoneSegmentAutocomplete(PhoneFormat.HOME, PhonePart.AREA_CODE);
		textfield.setPattern("0[0-9]{1,5}");
		textfield.setToolTip("numeric zero (0) followed by 1 to 5 digits");
		textfield.setMaxLength(6);
		textfield.setColumns(6);
		textfield.setPlaceholder("0400");
		layout.addField("Area code", textfield);

		textfield = new WTextField();
		textfield.setAutocomplete(Telephone.LOCAL, PhoneFormat.HOME);
		textfield.setPattern("[0-9]{1,12}");
		textfield.setToolTip("up to 12 digits without spaces");
		textfield.setPlaceholder("12349876");
		textfield.setMaxLength(12);
		textfield.setColumns(12);
		layout.addField("Local number", textfield).getLabel().setHint("numbers only, no spaces");

		textfield = new WTextField();
		textfield.setPhoneSegmentAutocomplete(PhoneFormat.HOME, PhonePart.EXTENSION);
		textfield.setPattern("[0-9]{1,6}");
		textfield.setToolTip("up to 6 digits without spaces");
		textfield.setPlaceholder("optional");
		textfield.setMaxLength(12);
		textfield.setColumns(12);
		layout.addField("Extension", textfield).getLabel().setHint("optional as numbers only without spaces");


		autocompleteSet = new WFieldSet("Phone number choice");
		autocompleteSet.setMargin(new Margin(Size.LARGE, null, Size.LARGE, null));
		autocompleteSet.setMandatory(true);
		add(autocompleteSet);
		autocompleteSet.add(new ExplanatoryText("Complete at least one field."));
		layout = new WFieldLayout(WFieldLayout.LAYOUT_FLAT);
		layout.setLabelWidth(33);
		autocompleteSet.add(layout);
		// NOTE: these would be better as WPhoneNumberField.
		textfield = new WTextField();
		textfield.setAutocomplete(Telephone.LOCAL, PhoneFormat.HOME);
		textfield.setPattern("[0-9\\ ]{5,15}");
		textfield.setToolTip("from 5 to 15 digits with optional spaces");
		layout.addField("Home", textfield);
		textfield = new WTextField();
		textfield.setAutocomplete(Telephone.LOCAL, PhoneFormat.WORK);
		layout.addField("Work", textfield);
		textfield.setPattern("[0-9\\ ]{5,15}");
		textfield.setToolTip("from 5 to 15 digits with optional spaces");
		textfield = new WTextField();
		textfield.setAutocomplete(Telephone.LOCAL, PhoneFormat.MOBILE);
		layout.addField("Mobile", textfield);
		textfield.setPattern("[0-9\\ ]{5,15}");
		textfield.setToolTip("from 5 to 15 digits with optional spaces");


		autocompleteSet = new WFieldSet("Your details");
		autocompleteSet.setMargin(new Margin(Size.LARGE, null, Size.LARGE, null));
		add(autocompleteSet);
		layout = new WFieldLayout(WFieldLayout.LAYOUT_FLAT);
		layout.setLabelWidth(33);
		autocompleteSet.add(layout);

		textfield = new WTextField();
		textfield.setAutocomplete(Person.GIVEN);
		textfield.setMandatory(true);
		layout.addField("Given name", textfield);

		textfield = new WTextField();
		textfield.setAutocomplete(Person.FAMILY);
		layout.addField("Family name", textfield);

		WDateField dob = new WDateField();
		dob.setMaxDate(new Date());
		dob.setAutocomplete(DateType.BIRTHDAY);
		layout.addField("Date of Birth", dob);

		final WPhoneNumberField phoneField = new WPhoneNumberField();
		phoneField.setFullPhoneAutocomplete();
		layout.addField("Phone", phoneField);

		final WEmailField email = new WEmailField();
		email.setAutocomplete(Email.EMAIL);
		layout.addField("Email", email);
		WSubordinateControl control = new WSubordinateControl();
		autocompleteSet.add(control);
		control.addRule(new Rule(new Equal(phoneField, ""), new Mandatory(email), new Optional(email)));
		control.addRule(new Rule(new Equal(email, ""), new Mandatory(phoneField), new Optional(phoneField)));

		add(new WHeading(HeadingLevel.H2, "Anti-pattern: inadequately labelled"));
		layout = new WFieldLayout();
		add(layout);
		layout.addField((WLabel) null, new WTextField());
		layout.addField("", new WTextField());
		layout.addField(new WLabel(), new WTextField());
		layout.addField(new WLabel(""), new WTextField());
		layout.addField(new WLabel(" "), new WTextField());
	}

}
