package com.github.bordertech.wcomponents.autocomplete.type;

/**
 * Provides values for the {@code autocomplete} attribute which are applicable to date inputs such as
 * {@link com.github.bordertech.wcomponents.WDateField}.
 *
 * @author Mark Reeves
 * @since 1.5.3
 */
public enum DateType {
	/**
	 * Indicates the field represents a birthday as a <a href="https://html.spec.whatwg.org/multipage/common-microsyntaxes.html#valid-date-string"
	 * target="_blank">valid date String</a>; used in conjunction with {@link com.github.bordertech.wcomponents.WDateField}.
	 */
	BIRTHDAY("bday");
	/**
	 * The the {@code autocomplete} attribute value.
	 */
	private final String value;

	/**
	 * Creates each entry in the enumeration to allow for moderately type-safe auto-fill mnemonics.
	 *
	 * @param val the string to place in the {@code autocomplete} attribute
	 */
	DateType(final String val) {
		this.value = val;
	}

	/**
	 * @return the {@code autocomplete} attribute value for the enum member.
	 */
	public String getValue() {
		return value;
	}

}
