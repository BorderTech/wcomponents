package com.github.bordertech.wcomponents.autocomplete;

/**
 *
 * @author Mark Reeves
 */
public interface AutocompleteableMultiline extends Autocompleteable {

	/**
	 * Set the autocomplete attribute to indicate auto-fill of a multi-line address of a given address type in a given auto-fill section.
	 * @param addressType the type of address to autocomplete - represents shipping or billing
	 * @param sectionName the part of {@code section-*} represented by the asterisk
	 */
	void setAutocomplete(final AutocompleteUtil.AddressAutocompleteType addressType, final String sectionName);

	/**
	 * Set the autocomplete attribute to indicate auto-fill of a multi-line address of a given address type.
	 * @param addressType the type of address to autocomplete - represents shipping or billing
	 */
	default void setAutocomplete(final AutocompleteUtil.AddressAutocompleteType addressType) {
		setAutocomplete(addressType, null);
	}

	/**
	 * Set a multi-line address autocomplete hint for a given autocomplete section.
	 * @param sectionName the part of {@code section-*} represented by the asterisk
	 */
	default void setStreetAddressAutocomplete(final String sectionName) {
		setAutocomplete(null, sectionName);
	}

	/**
	 * Set a multi-line address autocomplete hint.
	 */
	default void setStreetAddressAutocomplete() {
		setAutocomplete(null, null);
	}
}
