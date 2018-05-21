package com.github.bordertech.wcomponents.autocomplete;

import com.github.bordertech.wcomponents.autocomplete.type.Password;

/**
 * Specific {@code autocomplete} attribute values for controls in the
 * <a href="https://html.spec.whatwg.org/multipage/form-control-infrastructure.html#control-group-password" target="_blank">password control
 * group</a> such as {@link com.github.bordertech.wcomponents.WPasswordField}.
 *
 * @author Mark Reeves
 * @since 1.5.3
 */
public interface AutocompleteablePassword extends Autocompleteable {

	/**
	 * Set the {@code autocomplete} attribute of a password field to a given password auto-fill hint type.
	 * @param value the type of password field to auto-fill
	 */
	void setAutocomplete(final Password value);
}
