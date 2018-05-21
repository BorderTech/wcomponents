package com.github.bordertech.wcomponents.autocomplete.type;

/**
 * Provides values for the HTML {@code autocomplete} attribute for an input in the telephone-number ('tel') state such as
 * {@link com.github.bordertech.wcomponents.WPhoneNumberField}.
 *
 * <p>
 * This enumeration is more extensive than that allowed in the HTML specification as allowing a field in a telephone-number state to accept a
 * local number rather than requiring a full international prefix makes the field much more usable and is commonly found in current web
 * development practice.
 * </p>
 *
 * @author Mark Reeves
 * @since 1.5.3
 */
public enum Telephone {
	/**
	 * Indicates the field represents a full telephone number, including country code, in the form of ASCII digits and optional U+0020 SPACE
	 * characters, prefixed by a U+002B PLUS SIGN character (+); may be applied to a {@link com.github.bordertech.wcomponents.WPhoneNumberField}.
	 */
	FULL("tel"),
	/**
	 * Indicates the field represents a telephone number without the country code and area code components, in the form of ASCII digits but not to
	 * be used with {@link com.github.bordertech.wcomponents.WNumberField}.
	 */
	LOCAL("tel-local");
	/**
	 * The the {@code autocomplete} attribute value.
	 */
	private final String value;

	/**
	 * Creates each entry in the enumeration to allow for moderately type-safe auto-fill mnemonics.
	 *
	 * @param val the string to place in the {@code autocomplete} attribute
	 */
	Telephone(final String val) {
		this.value = val;
	}

	/**
	 * @return the {@code autocomplete} attribute value for the enum member.
	 */
	public String getValue() {
		return this.value;
	}

}
