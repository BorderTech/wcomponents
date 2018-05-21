package com.github.bordertech.wcomponents.autocomplete.segment;

/**
 * Provides values for the HTML {@code autocomplete} attribute for a parts of a telephone number which are applicable to inputs which accept
 * text-form auto-fill hints.
 *
 * @author Mark Reeves
 * @since 1.5.3
 */
public enum PhonePart implements AutocompleteSegment {
	/**
	 * Indicates the field represents an area code component of the telephone number, with a country-internal prefix applied if applicable, in the
	 * form of ASCII digits but not to be used with {@link com.github.bordertech.wcomponents.WNumberField}.
	 */
	AREA_CODE("tel-area-code"),
	/**
	 * Indicates the field represents a country code component of the telephone number consisting of ASCII digits preceded by a U+002B PLUS SIGN
	 * character (+); must not be used in conjunction with a {@link com.github.bordertech.wcomponents.WPhoneNumberField}.
	 */
	COUNTRY_CODE("tel-country-code"),
	/**
	 * Indicates the field represents a telephone number internal extension code, in the form of ASCII digits but not to be used with
	 * {@link com.github.bordertech.wcomponents.WNumberField}.
	 */
	EXTENSION("tel-extension"),
	/**
	 * Indicates the field represents a telephone number without the country code and area code components, in the form of ASCII digits but not to
	 * be used with {@link com.github.bordertech.wcomponents.WNumberField}.
	 */
	LOCAL("tel-local"),
	// NOTE: this does appear in the PhonePart enum too.
	/**
	 * Indicates the field represents the first part of the component of the telephone number that follows the area code, when that component is
	 * split into two components, in the form of ASCII digits but not to be used with {@link com.github.bordertech.wcomponents.WNumberField}.
	 */
	LOCAL_PREFIX("tel-local-prefix"),
	/**
	 * Indicates the field represents the second part of the component of the telephone number that follows the area code, when that component is
	 * split into two components, in the form of ASCII digits but not to be used with {@link com.github.bordertech.wcomponents.WNumberField}.
	 */
	LOCAL_SUFFIX("tel-local-suffix"),
	/**
	 * Indicates the field represents a telephone number without the county code component, with a country-internal prefix applied if applicable
	 * in the form of ASCII digits with optional U+0020 SPACE characters; must not be used in conjunction with a
	 * {@link com.github.bordertech.wcomponents.WPhoneNumberField}.
	 */
	NATIONAL("tel-national");
	/**
	 * The the {@code autocomplete} attribute value.
	 */
	private final String value;

	/**
	 * Creates each entry in the enumeration to allow for moderately type-safe auto-fill mnemonics.
	 *
	 * @param val the string to place in the {@code autocomplete} attribute
	 */
	PhonePart(final String val) {
		this.value = val;
	}

	@Override
	public String getValue() {
		return this.value;
	}

}
