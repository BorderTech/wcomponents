package com.github.bordertech.wcomponents.autocomplete;

import com.github.bordertech.wcomponents.autocomplete.segment.AddressPart;
import com.github.bordertech.wcomponents.autocomplete.segment.AddressType;
import com.github.bordertech.wcomponents.autocomplete.segment.AutocompleteSegment;
import com.github.bordertech.wcomponents.autocomplete.segment.PhoneFormat;
import com.github.bordertech.wcomponents.autocomplete.segment.PhonePart;


/**
 * Specific {@code autocomplete} attribute values for controls which are in the
 * <a href="https://html.spec.whatwg.org/multipage/form-control-infrastructure.html#control-group-tetextl" target="_blank">text control
 * group</a> such as {@code text} control group such as {@link com.github.bordertech.wcomponents.WTextField}.
 *
 * @author Mark Reeves
 * @since 1.5.3
 */
public interface AutocompleteableText extends AutocompleteableDate, AutocompleteableEmail, AutocompleteableNumeric, AutocompleteablePassword,
		AutocompleteablePhone, AutocompleteableURL {

	/**
	 * Set {@code autocomplete} attribute value for a field based on a segment name.
	 * @param value the auto-fill mnemonic
	 */
	void setAutocomplete(final AutocompleteSegment value);

	/**
	 * Set values for the {@code autocomplete} attribute applicable to an address or part thereof.
	 * @param addressType the type of address being auto-filled
	 * @param addressPart the address segment for the field
	 */
	void setAddressAutocomplete(final AddressType addressType, final AddressPart addressPart);

	/**
	 * Set the {@code autocomplete} attribute value relevant for a specified telephone number type and segment.
	 * @param phoneType the type of phone number
	 * @param phoneSegment the phone number segment
	 */
	void setPhoneSegmentAutocomplete(final PhoneFormat phoneType, final PhonePart phoneSegment);

}
