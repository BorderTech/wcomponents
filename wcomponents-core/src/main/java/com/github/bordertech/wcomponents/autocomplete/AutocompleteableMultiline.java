package com.github.bordertech.wcomponents.autocomplete;

import com.github.bordertech.wcomponents.autocomplete.segment.AddressType;
import com.github.bordertech.wcomponents.autocomplete.type.Multiline;

/**
 * Provides implementation of the {@code autocomplete} attribute for controls in the
 * <a href="https://html.spec.whatwg.org/multipage/form-control-infrastructure.html#control-group-multiline" target="_blank">Multiline control
 * group</a> such as {@link com.github.bordertech.wcomponents.WTextArea}.
 *
 * @author Mark Reeves
 * @since 1.5.3
 */
public interface AutocompleteableMultiline extends Autocompleteable {

	/**
	 * Set the autocomplete attribute to indicate auto-fill of a multi-line field.
	 * @param value the type of multiline field being autofilled: currently only one value - "street-address"
	 */
	void setAutocomplete(final Multiline value);

	/**
	 * Set the autocomplete attribute to indicate auto-fill of a multi-line street address of a given address type (shipping or billing).
	 * @param value the type of multiline field being autofilled: currently only one value - "street-address"
	 */
	void setFullStreetAddressAutocomplete(final AddressType value);

	/**
	 * Set the autocomplete attribute to indicate auto-fill of a multi-line street address.
	 */
	default void setFullStreetAddressAutocomplete() {
		setAutocomplete(Multiline.STREET_ADDRESS);
	}
}
