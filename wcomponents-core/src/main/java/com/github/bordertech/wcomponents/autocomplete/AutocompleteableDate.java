package com.github.bordertech.wcomponents.autocomplete;

/**
 * Provides implementation of the {@code autocomplete} attribute for {@link com.github.bordertech.wcomponents.WDateField}.
 *
 * @author Mark Reeves
 */
public interface AutocompleteableDate extends Autocompleteable {

	/**
	 * Set the {@code autocomplete}s attribute for a type of date input and a given auto-fill section.
	 * @param dateType the type of date to auto-fill, currently only "bday" is supported.
	 * @param sectionName the name of the auto-fill section
	 */
	void setAutocomplete(final AutocompleteUtil.DateAutocomplete dateType, final String sectionName);
	/**
	 * Set the {@code autocomplete}s attribute for a type of date input.
	 * @param dateType the type of date to auto-fill, currently only "bday" is supported.
	 */
	default void setAutocomplete(final AutocompleteUtil.DateAutocomplete dateType) {
		setAutocomplete(dateType, null);
	}

	/**
	 * Set the "birthday" autocomplete hint for a given auto-fill section.
	 * @param sectionName the auto-fill section to which the current field belongs (if any)
	 */
	default void setDateAutocomplete(final String sectionName) {
		setAutocomplete(AutocompleteUtil.DateAutocomplete.BIRTHDAY, sectionName);
	}

	/**
	 * Set the "birthday" autocomplete hint for the current field.
	 */
	default void setDateAutocomplete() {
		setAutocomplete(AutocompleteUtil.DateAutocomplete.BIRTHDAY, null);
	}
}
