package com.github.bordertech.wcomponents.autocomplete;

/**
 * Provides implementation of the {@code autocomplete} attribute for {@link com.github.bordertech.wcomponents.WEmailField}.
 *
 * @author Mark Reeves
 * @since 1.5.3
 */
public interface AutocompleteableEmail extends Autocompleteable {

	/**
	 * Set the {@code autocomplete} attribute to a specific "email" value for a named auto-fill section. Currently only value "email" is supported.
	 * @param value the auto-fill hint value
	 * @param sectionName the auto-fill section name after the prefix {@code section-}
	 */
	void setAutocomplete(final AutocompleteUtil.EmailAutocomplete value, final String sectionName);

	/**
	 * Set the {@code autocomplete} attribute to a specific "email" value. Currently only value "email" is supported.
	 * @param value the auto-fill hint value
	 */
	default void setAutocomplete(final AutocompleteUtil.EmailAutocomplete value) {
		setAutocomplete(value, null);
	}

	/**
	 * Set the "email" auto-fill hint for the current field within a given auto-fill section.
	 * @param sectionName the auto-fill section to which the current field belongs (if any)
	 */
	default void setEmailAutocomplete(final String sectionName) {
		setAutocomplete(AutocompleteUtil.EmailAutocomplete.EMAIL, sectionName);
	}

	/**
	 * Set the "email" auto-fill hint for the current field.
	 */
	default void setEmailAutocomplete() {
		setAutocomplete(AutocompleteUtil.EmailAutocomplete.EMAIL, null);
	}
}
