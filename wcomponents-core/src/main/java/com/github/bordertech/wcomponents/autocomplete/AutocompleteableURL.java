package com.github.bordertech.wcomponents.autocomplete;

/**
 * Provides implementation of the {@code autocomplete} attribute for URL inputs.
 *
 * @author Mark Reeves
 * @since 1.5.3
 */
public interface AutocompleteableURL extends Autocompleteable {

	/**
	 * Set the {@code autocomplete} attribute to a specific URL auto-fill type for a named auto-fill section.
	 * @param value the auto-fill hint value
	 * @param sectionName the auto-fill section name after the prefix {@code section-}
	 */
	void setAutocomplete(final AutocompleteUtil.UrlAutocomplete value, final String sectionName);

	/**
	 * Set the {@code autocomplete} attribute to a specific URL auto-fill type: "url" or "impp".
	 * @param value the auto-fill hint value
	 */
	default void setAutocomplete(final AutocompleteUtil.UrlAutocomplete value) {
		setAutocomplete(value, null);
	}

	/**
	 * Set the "url" auto-fill hint for the current field within a given auto-fill section.
	 * @param sectionName the auto-fill section to which the current field belongs (if any)
	 */
	default void setUrlAutocomplete(final String sectionName) {
		setAutocomplete(AutocompleteUtil.UrlAutocomplete.URL, sectionName);
	}

	/**
	 * Set the "url" auto-fill hint for the current field.
	 */
	default void setUrlAutocomplete() {
		setAutocomplete(AutocompleteUtil.UrlAutocomplete.URL, null);
	}
}
