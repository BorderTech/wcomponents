package com.github.bordertech.wcomponents.autocomplete;

/**
 * Specific {@code autocomplete} attribute support for {@link com.github.bordertech.wcomponents.WPhoneNumberField}.
 *
 * @author Mark Reeves
 * @since 1.5.3
 */
public interface AutocompleteablePhone extends Autocompleteable {

	/**
	 * Set the {@code autocomplete} attribute to an appropriate value for a particular type of phone number, in either of a full or local format in a
	 * given auto-fill section.
	 *
	 * @param phoneType the type of phone number
	 * @param phone the telephone auto-fill variant, being full (including international prefix) or local (without international prefix)
	 * @param sectionName an auto-fill section name
	 */
	void setAutocomplete(final AutocompleteUtil.TelephoneAutocompleteType phoneType, final AutocompleteUtil.TelephoneAutocomplete phone,
			final String sectionName);

	/**
	 * Set the {@code autocomplete} attribute to an appropriate value for a particular type of phone number, in either of a full or local format.
	 *
	 * @param phoneType the type of phone number
	 * @param phone the telephone auto-fill variant, being full (including international prefix) or local (without international prefix)
	 */
	default void setAutocomplete(final AutocompleteUtil.TelephoneAutocompleteType phoneType, final AutocompleteUtil.TelephoneAutocomplete phone) {
		setAutocomplete(phoneType, phone, null);
	}

	/**
	 * Set the {@code autocomplete} attribute to an appropriate value for a particular type of full phone number in a given auto-fill section.
	 *
	 * @param phoneType the type of phone number
	 * @param sectionName an auto-fill section name
	 */
	default void setAutocomplete(final AutocompleteUtil.TelephoneAutocompleteType phoneType, final String sectionName) {
		setAutocomplete(phoneType, AutocompleteUtil.TelephoneAutocomplete.FULL, sectionName);
	}

	/**
	 * Set the {@code autocomplete} attribute to an appropriate value for a full phone number of a given type.
	 *
	 * @param phoneType the type of phone number
	 */
	default void setAutocomplete(final AutocompleteUtil.TelephoneAutocompleteType phoneType) {
		setAutocomplete(phoneType, AutocompleteUtil.TelephoneAutocomplete.FULL, null);
	};

	/**
	 * Set the {@code autocomplete} attribute to an appropriate value for a full or local phone number without type.
	 *
	 * @param phone the auto-fill role of the phone number - FULL ('tel') or LOCAL ('tel-local')
	 */
	default void setAutocomplete(final AutocompleteUtil.TelephoneAutocomplete phone) {
		setAutocomplete(null, phone, null);
	};

	/**
	 * Set the {@code autocomplete} attribute to an appropriate value for a full phone number phone number without any type.
	 *
	 */
	default void setFullPhoneAutocomplete() {
		setAutocomplete(null, AutocompleteUtil.TelephoneAutocomplete.FULL, null);
	}

	/**
	 * Set the {@code autocomplete} attribute to an appropriate value for a local phone number of a given type.
	 *
	 * @param phoneType the type of phone number
	 */
	default void setLocalPhoneAutocomplete(final AutocompleteUtil.TelephoneAutocompleteType phoneType) {
		setAutocomplete(phoneType, AutocompleteUtil.TelephoneAutocomplete.LOCAL, null);
	};

	/**
	 * Set the {@code autocomplete} attribute to an appropriate value for a local phone number phone number without any type.
	 */
	default void setLocalPhoneAutocomplete() {
		setAutocomplete(null, AutocompleteUtil.TelephoneAutocomplete.LOCAL, null);
	}


}
