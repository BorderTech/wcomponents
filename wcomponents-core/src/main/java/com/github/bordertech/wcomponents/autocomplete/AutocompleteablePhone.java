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
	void setAutocomplete(final AutocompleteUtil.TELEPHONE_TYPE phoneType, final AutocompleteUtil.TELEPHONE_AUTOCOMPLETE phone,
			final String sectionName);

	/**
	 * Set the {@code autocomplete} attribute to an appropriate value for a particular type of full phone number in a given auto-fill section.
	 *
	 * @param phoneType the type of phone number
	 * @param sectionName an auto-fill section name
	 */
	public default void setAutocomplete(final AutocompleteUtil.TELEPHONE_TYPE phoneType, final String sectionName) {
		setAutocomplete(phoneType, AutocompleteUtil.TELEPHONE_AUTOCOMPLETE.FULL, sectionName);
	}

	/**
	 * Set the {@code autocomplete} attribute to an appropriate value for a full phone number of a given type.
	 *
	 * @param phoneType the type of phone number
	 */
	public default void setAutocomplete(final AutocompleteUtil.TELEPHONE_TYPE phoneType) {
		setAutocomplete(phoneType, AutocompleteUtil.TELEPHONE_AUTOCOMPLETE.FULL, null);
	};

	/**
	 * Set the {@code autocomplete} attribute to an appropriate value for a full phone number phone number without any type.
	 *
	 */
	public default void setFullPhoneAutocomplete() {
		setAutocomplete(null, AutocompleteUtil.TELEPHONE_AUTOCOMPLETE.FULL, null);
	}

	/**
	 * Set the {@code autocomplete} attribute to an appropriate value for a local phone number of a given type.
	 *
	 * @param phoneType the type of phone number
	 */
	public default void setLocalPhoneAutocomplete(final AutocompleteUtil.TELEPHONE_TYPE phoneType) {
		setAutocomplete(phoneType, AutocompleteUtil.TELEPHONE_AUTOCOMPLETE.LOCAL, null);
	};

	/**
	 * Set the {@code autocomplete} attribute to an appropriate value for a local phone number phone number without any type.
	 */
	public default void setLocalPhoneAutocomplete() {
		setAutocomplete(null, AutocompleteUtil.TELEPHONE_AUTOCOMPLETE.LOCAL, null);
	}
}
