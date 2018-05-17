package com.github.bordertech.wcomponents.autocomplete.type;

/**
 * Provides values for the {@code autocomplete} attribute which are applicable to multi-line inputs such as
 * {@link com.github.bordertech.wcomponents.WTextArea}.
 *
 * @author Mark Reeves
 * @since 1.5.3
 */
public enum Multiline {
	/**
	 * Indicates the field represents a street address, for use with a multiple-line input such as
	 * {@link com.github.bordertech.wcomponents.WTextArea}. See
	 * <a href="https://html.spec.whatwg.org/multipage/form-control-infrastructure.html#attr-fe-autocomplete-street-address" target="_blank">
	 * https://html.spec.whatwg.org/multipage/form-control-infrastructure.html#attr-fe-autocomplete-street-address</a>.
	 */
	STREET_ADDRESS("street-address");
	/**
	 * The the {@code autocomplete} attribute value.
	 */
	private final String value;

	/**
	 * Creates each entry in the enumeration to allow for moderately type-safe auto-fill mnemonics.
	 *
	 * @param val the string to place in the {@code autocomplete} attribute
	 */
	Multiline(final String val) {
		this.value = val;
	}

	/**
	 * @return the {@code autocomplete} attribute value for the enum member.
	 */
	public String getValue() {
		return this.value;
	}

}
