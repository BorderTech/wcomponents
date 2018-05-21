package com.github.bordertech.wcomponents.autocomplete.segment;

/**
 * Types of address that may be used with address segments to create address {@code autocomplete} attribute values. Used in conjunction with
 * {@link com.github.bordertech.wcomponents.autocomplete.type.Multiline full addresses} or
 * {@link com.github.bordertech.wcomponents.autocomplete.segment.AddressPart address segments}.
 *
 * @author Mark Reeves
 * @since 1.5.3
 */
public enum AddressType implements AutocompleteSegment {
	/**
	 * Indicates the field represents an address (or address segment) that is part of a billing address.
	 */
	BILLING("billing"),
	/**
	 * Indicates the field represents am address (or address segment) that is part of a shipping address.
	 */
	SHIPPING("shipping");
	/**
	 * The the {@code autocomplete} attribute value.
	 */
	private final String value;

	/**
	 * Creates each entry in the enumeration to allow for moderately type-safe auto-fill mnemonics.
	 *
	 * @param val the string to place in the {@code autocomplete} attribute
	 */
	AddressType(final String val) {
		this.value = val;
	}

	@Override
	public String getValue() {
		return this.value;
	}

}
