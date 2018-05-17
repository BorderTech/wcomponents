package com.github.bordertech.wcomponents.autocomplete;

import com.github.bordertech.wcomponents.autocomplete.type.DateType;

/**
 * Provides implementation of the {@code autocomplete} attribute for controls in the
 * <a href="https://html.spec.whatwg.org/multipage/form-control-infrastructure.html#control-group-date" target="_blank">date control group</a>
 * such as {@link com.github.bordertech.wcomponents.WDateField}.
 *
 * @author Mark Reeves
 * @since 1.5.3
 */
public interface AutocompleteableDate extends Autocompleteable {

	/**
	 * Set the {@code autocomplete}s attribute for a type of date input.
	 * @param value the type of date to auto-fill, currently only "bday" is supported.
	 */
	void setAutocomplete(final DateType value);

	/**
	 * Set the "birthday" autocomplete hint for the current field.
	 */
	default void setBirthdayAutocomplete() {
		setAutocomplete(DateType.BIRTHDAY);
	}
}
