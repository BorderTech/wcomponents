package com.github.bordertech.wcomponents.autocomplete;

import com.github.bordertech.wcomponents.autocomplete.type.Url;

/**
 * Specific {@code autocomplete} attribute values for controls in the
 * <a href="https://html.spec.whatwg.org/multipage/form-control-infrastructure.html#control-group-url" target="_blank">url control group</a>.
 *
 * @author Mark Reeves
 * @since 1.5.3
 */
public interface AutocompleteableURL extends Autocompleteable {

	/**
	 * Set the {@code autocomplete} attribute to a specific URL auto-fill type: "url", "impp" or "photo".
	 * @param value the auto-fill hint value
	 */
	void setAutocomplete(final Url value);

	/**
	 * Set the "url" auto-fill hint for the current field.
	 */
	default void setUrlAutocomplete() {
		setAutocomplete(Url.URL);
	}
}
