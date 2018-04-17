package com.github.bordertech.wcomponents.autocomplete;

import com.github.bordertech.wcomponents.util.Util;

/**
 *
 * @author Mark Reeves
 */
public interface AutocompleteableText extends AutocompleteableDate, AutocompleteableEmail, AutocompleteableNumeric,
		AutocompleteablePassword, AutocompleteablePhone, AutocompleteableURL {

	/**
	 * Set the value of the {@code autocomplete} attribute for the current field.
	 * @param autocompleteValue the value to set as a (optionally space delimited list of) String value(s).
	 */
	void setAutocomplete(final String autocompleteValue);

	/**
	 * Set the value of the {@code autocomplete} attribute for the current field in a given auto-fill section.
	 * @param value the value to set as a (optionally space delimited list of) String value(s).
	 * @param sectionName an optional auto-fill section name.
	 */
	default void setAutocomplete(final String value, final String sectionName) {
		String newValue = Util.empty(sectionName) ? value : AutocompleteUtil.getCombinedForSection(sectionName, value);
		setAutocomplete(newValue);
	}

	/**
	 * Set values for the {@code autocomplete} attribute applicable to an address or part thereof.
	 * @param addressType the type of address being auto-filled
	 * @param addressPart the address segment for the field
	 * @param sectionName an optional auto-fill section name.
	 */
	default void setAddressAutocomplete(final AutocompleteUtil.AddressAutocompleteType addressType,
			final AutocompleteUtil.AddressAutocompleteSegment addressPart,
			final String sectionName) {
		boolean emptySectionName = Util.empty(sectionName);

		if (emptySectionName && addressType == null && addressPart == null) {
			clearAutocomplete();
		}
		String innerAddressType = addressType == null ? null : addressType.getValue();
		String innerAddressPart = addressPart == null ? null : addressPart.getValue();


		String newValue = emptySectionName
				? AutocompleteUtil.getCombinedAutocomplete(innerAddressType, innerAddressPart)
				: AutocompleteUtil.getCombinedForSection(sectionName, innerAddressType, innerAddressPart);

		setAutocomplete(newValue);
	}

	/**
	 * Set values for the {@code autocomplete} attribute applicable to an address or part thereof.
	 * @param addressType the type of address being auto-filled
	 * @param addressPart the address segment for the field
	 */
	default void setAddressAutocomplete(final AutocompleteUtil.AddressAutocompleteType addressType,
			final AutocompleteUtil.AddressAutocompleteSegment addressPart) {
		setAddressAutocomplete(addressType, addressPart, null);
	}

	/**
	 * Set values for the {@code autocomplete} attribute applicable to an address or part thereof.
	 * @param addressPart the address segment for the field
	 */
	default void setAddressAutocomplete(final AutocompleteUtil.AddressAutocompleteSegment addressPart) {
		setAddressAutocomplete(null, addressPart, null);
	}

	/**
	 * Set the {@code autocomplete} attribute value relevant for a specified telephone number type and segment in a named auto-fill section.
	 * @param phoneType the type of phone number
	 * @param phonePart the phone number segment
	 * @param sectionName an optional auto-fill section name
	 */
	default void setPhoneAutocomplete(final AutocompleteUtil.TelephoneAutocompleteType phoneType,
			final AutocompleteUtil.TelephoneAutocompleteSegment phonePart,
			final String sectionName) {
		boolean emptySectionName = Util.empty(sectionName);
		if (emptySectionName && phoneType == null && phonePart == null) {
			clearAutocomplete();
}
		String innerPhoneType = phoneType == null ? null : phoneType.getValue();
		String innerPhonePart = phonePart == null ? null : phonePart.getValue();

		String newValue = emptySectionName
				? AutocompleteUtil.getCombinedAutocomplete(innerPhoneType, innerPhonePart)
				: AutocompleteUtil.getCombinedForSection(sectionName, innerPhoneType, innerPhonePart);
		setAutocomplete(newValue);
	}

	/**
	 * Set the {@code autocomplete} attribute value relevant for a specified telephone number type and segment.
	 * @param phoneType the type of phone number
	 * @param phonePart the phone number segment
	 */
	default void setPhoneAutocomplete(final AutocompleteUtil.TelephoneAutocompleteType phoneType,
			final AutocompleteUtil.TelephoneAutocompleteSegment phonePart) {
		setPhoneAutocomplete(phoneType, phonePart, null);
	}


	/**
	 * Set the {@code autocomplete} attribute value relevant for a specified telephone segment without specifying the phone number type.
	 * @param phonePart the phone number segment
	 */
	default void setPhoneAutocomplete(final AutocompleteUtil.TelephoneAutocompleteSegment phonePart) {
		setPhoneAutocomplete(null, phonePart, null);
	}

}
