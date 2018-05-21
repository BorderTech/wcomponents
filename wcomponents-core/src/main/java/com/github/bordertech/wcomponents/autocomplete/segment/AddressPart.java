package com.github.bordertech.wcomponents.autocomplete.segment;

/**
 * Parts of an address that may be used to create address {@code autocomplete} attribute values which are applicable to inputs which accept
 * text-form auto-fill hints. May be used in combination with an address type to specify separate addresses.
 *
 * @author Mark Reeves
 * @since 1.5.3
 */
public enum AddressPart implements AutocompleteSegment {
	/**
	 * Indicates the field represents the first line of a street address when using single-line inputs, see
	 * <a href="https://html.spec.whatwg.org/multipage/form-control-infrastructure.html#attr-fe-autocomplete-address-line1" target="_blank">
	 * https://html.spec.whatwg.org/multipage/form-control-infrastructure.html#attr-fe-autocomplete-address-line1</a>.
	 */
	LINE_1("address-line1"),
	/**
	 * Indicates the field represents she second line of a street address using single-line inputs, see
	 * <a href="https://html.spec.whatwg.org/multipage/form-control-infrastructure.html#attr-fe-autocomplete-address-line2" target="_blank">
	 * https://html.spec.whatwg.org/multipage/form-control-infrastructure.html#attr-fe-autocomplete-address-line2</a>.
	 */
	LINE_2("address-line2"),
	/**
	 * Indicates the field represents the third line of a street address using single-line inputs, see
	 * <a href="https://html.spec.whatwg.org/multipage/form-control-infrastructure.html#attr-fe-autocomplete-address-line3" target="_blank">
	 * https://html.spec.whatwg.org/multipage/form-control-infrastructure.html#attr-fe-autocomplete-address-line3</a>.
	 */
	LINE_3("address-line3"),
	/**
	 * Indicates the field represents the most fine-grained administrative level, in addresses with four administrative levels. See
	 * <a href="https://html.spec.whatwg.org/multipage/form-control-infrastructure.html#more-on-address-levels" target="_blank">more on address
	 * levels</a>.
	 */
	LEVEL_4("address-level4"),
	/**
	 * Indicates the field represents the third administrative level, in addresses with three or more administrative levels. See
	 * <a href="https://html.spec.whatwg.org/multipage/form-control-infrastructure.html#more-on-address-levels" target="_blank">more on address
	 * levels</a>.
	 */
	LEVEL_3("address-level3"),
	/**
	 * Indicates the field represents the second administrative level, in addresses with two or more administrative levels; in the countries with
	 * two administrative levels, this would typically be the city, town, village, or other locality within which the relevant street address is
	 * found. See <a href="https://html.spec.whatwg.org/multipage/form-control-infrastructure.html#more-on-address-levels" target="_blank">more on
	 * address levels</a>.
	 */
	LEVEL_2("address-level2"),
	/**
	 * Indicates the field represents the broadest administrative level in the address, ie the province within which the locality is found; for
	 * example, in the US, this would be the state; in Switzerland it would be the canton; in the UK, the post town. See
	 * <a href="https://html.spec.whatwg.org/multipage/form-control-infrastructure.html#more-on-address-levels" target="_blank">more on address
	 * levels</a>.
	 */
	LEVEL_1("address-level1"),
	/**
	 * Indicates the field represents a country code represented by a valid
	 * <a href="https://www.iso.org/iso-3166-country-codes.html" target="_blank">ISO 3166-1-alpha-2 country code</a>.
	 */
	COUNTRY_CODE("country"),
	/**
	 * Indicates the field represents a free-form text value representing the name of a country.
	 */
	COUNTRY_NAME("country-name"),
	/**
	 * Indicates the field represents a postal code, post code, ZIP code, CEDEX code (if CEDEX, append "CEDEX", and the arrondissement, if
	 * relevant, to the address-level2 field).
	 */
	POSTAL_CODE("postal-code");
	/**
	 * The the {@code autocomplete} attribute value.
	 */
	private final String value;

	/**
	 * Creates each entry in the enumeration to allow for moderately type-safe auto-fill mnemonics.
	 *
	 * @param val the string to place in the {@code autocomplete} attribute
	 */
	AddressPart(final String val) {
		this.value = val;
	}

	@Override
	public String getValue() {
		return this.value;
	}
}
