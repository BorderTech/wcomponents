package com.github.bordertech.wcomponents.autocomplete;

import com.github.bordertech.wcomponents.autocomplete.type.Email;

/**
 * Provides implementation of the {@code autocomplete} attribute for controls in the
 * <a href="https://html.spec.whatwg.org/multipage/form-control-infrastructure.html#control-group-e-mail" target="_blank">E-mail control group</a>
 * such as {@link com.github.bordertech.wcomponents.WEmailField}.
 *
 * @author Mark Reeves
 * @since 1.5.3
 */
public interface AutocompleteableEmail extends Autocompleteable {

	/**
	 * Set the {@code autocomplete} attribute to a specific "email" value. Currently only value "email" is supported.
	 * @param value the auto-fill hint value
	 */
	void setAutocomplete(final Email value);

	/**
	 * Set the "email" auto-fill hint for the current field.
	 */
	default void setEmailAutocomplete() {
		setAutocomplete(Email.EMAIL);
	}
}
