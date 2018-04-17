package com.github.bordertech.wcomponents.autocomplete;

/**
 * Provide valid {@code autocomplete} attribute values suitable for {@link com.github.bordertech.wcomponents.WNumberField}.
 * @author Mark Reeves
 */
public interface AutocompleteableNumeric extends Autocompleteable {

	/**
	 * Set the autocomplete attribute for a number field for a given auto-fill value and auto-fill section.
	 * @param value the type of number for auto-fill
	 * @param sectionName the part of {@code section-*} represented by the asterisk
	 */
	void setAutocomplete(final AutocompleteUtil.NumericAutocomplete value, final String sectionName);

	/**
	 * Set the autocomplete attribute for a number field for a given auto-fill value with no auto-fill section.
	 * @param value the type of number for auto-fill
	 */
	default void setAutocomplete(final AutocompleteUtil.NumericAutocomplete value) {
		setAutocomplete(value, null);
	}

}
