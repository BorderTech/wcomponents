package com.github.bordertech.wcomponents.autocomplete;

/**
 * Provides implementation of the {@code autocomplete} attribute for {@link com.github.bordertech.wcomponents.WDateField}.
 *
 * @author Mark Reeves
 */
public interface AutocompleteableDate extends Autocompleteable {

	/**
	 * Set the "birthday" autocomplete hint for a given auto-fill section.
	 * @param sectionName the auto-fill section to which the current field belongs (if any)
	 */
	void setDateAutocomplete(final String sectionName);

	/**
	 * Set the "birthday" autocomplete hint for the current field.
	 */
	public default void setDateAutocomplete() {
		setDateAutocomplete(null);
	}
}
