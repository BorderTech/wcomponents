package com.github.bordertech.wcomponents.autocomplete;

import com.github.bordertech.wcomponents.autocomplete.segment.PhoneFormat;
import com.github.bordertech.wcomponents.autocomplete.type.Telephone;

/**
 * Specific {@code autocomplete} attribute values for controls in the
 * <a href="https://html.spec.whatwg.org/multipage/form-control-infrastructure.html#control-group-tel" target="_blank">tel control
 * group</a> such as {@link com.github.bordertech.wcomponents.WPhoneNumberField}.
 *
 * @author Mark Reeves
 * @since 1.5.3
 */
public interface AutocompleteablePhone extends Autocompleteable {

	/**
	 * Set the {@code autocomplete} attribute to an appropriate value for a particular type of phone number, in either of a full or local format.
	 *
	 * @param phone the telephone auto-fill variant, being full (including international prefix) or local (without international prefix)
	 * @param phoneType the type of phone to which the number belongs, for example "mobile" or "fax"
	 */
	void setAutocomplete(final Telephone phone, final PhoneFormat phoneType);

	/**
	 * Set the {@code autocomplete} attribute to an appropriate value for a full phone number of a given type of phone.
	 *
	 * @param phoneType the type of phone number
	 */
	default void setFullPhoneAutocomplete(final PhoneFormat phoneType) {
		setAutocomplete(Telephone.FULL, phoneType);
	};

	/**
	 * Set the {@code autocomplete} attribute to an appropriate value for a full phone number phone number.
	 *
	 */
	default void setFullPhoneAutocomplete() {
		setFullPhoneAutocomplete(null);
	}

	/**
	 * Set the {@code autocomplete} attribute to an appropriate value for a local phone number of a given type of phone.
	 *
	 * @param phoneType the type of phone number
	 */
	default void setLocalPhoneAutocomplete(final PhoneFormat phoneType) {
		setAutocomplete(Telephone.LOCAL, phoneType);
	};

	/**
	 * Set the {@code autocomplete} attribute to an appropriate value for a local phone number phone number.
	 */
	default void setLocalPhoneAutocomplete() {
		setLocalPhoneAutocomplete(null);
	}
}
