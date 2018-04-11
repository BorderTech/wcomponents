package com.github.bordertech.wcomponents.autocomplete;

/**
 * Provide valid {@code autocomplete} attribute values suitable for {@link com.github.bordertech.wcomponents.WNumberField}.
 * @author Mark Reeves
 */
public interface AutocompleteableNumeric extends Autocompleteable {

	void setAutocomplete(final AutocompleteUtil.NUMERIC_AUTOCOMPLETE value, final String sectionName);

	public default void setAutocomplete(final AutocompleteUtil.NUMERIC_AUTOCOMPLETE value) {
		setAutocomplete(value, null);
	}

}
