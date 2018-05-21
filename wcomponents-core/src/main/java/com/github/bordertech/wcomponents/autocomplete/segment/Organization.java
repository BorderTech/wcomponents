package com.github.bordertech.wcomponents.autocomplete.segment;

/**
 * Provides organization details values for the {@code autocomplete} attribute which are applicable to inputs which accept text-form auto-fill
 * hints. Often used in conjunction with values from either {@link com.github.bordertech.wcomponents.autocomplete.segment.Person person details} or
 * {@link com.github.bordertech.wcomponents.autocomplete.segment.AddressPart addresses}.
 *
 * @author Mark Reeves
 * @since 1.5.3
 */
public enum Organization implements AutocompleteSegment {
	/**
	 * Company name corresponding to the person, address, or contact information in the other fields associated with this field.
	 */
	ORGANIZATION("organization");
	/**
	 * The the {@code autocomplete} attribute value.
	 */
	private final String value;

	/**
	 * Creates each entry in the enumeration to allow for moderately type-safe auto-fill mnemonics.
	 *
	 * @param val the string to place in the {@code autocomplete} attribute
	 */
	Organization(final String val) {
		this.value = val;
	}

	@Override
	public String getValue() {
		return value;
	}

}
