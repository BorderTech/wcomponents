package com.github.bordertech.wcomponents.autocomplete;

import com.github.bordertech.wcomponents.autocomplete.type.Numeric;

/**
 * Provide valid {@code autocomplete} attribute values suitable for controls in the
 * <a href="hhttps://html.spec.whatwg.org/multipage/form-control-infrastructure.html#control-group-numeric" target="_blank">numeric control group</a>
 * such as {@link com.github.bordertech.wcomponents.WNumberField}.
 *
 * @author Mark Reeves
 * @since 1.5.3
 */
public interface AutocompleteableNumeric extends Autocompleteable {

	/**
	 * Set the autocomplete attribute for a number field for a given numeric auto-fill value.
	 * @param value the type of number for auto-fill
	 */
	void setAutocomplete(final Numeric value);

}
