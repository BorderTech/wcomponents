package com.github.bordertech.wcomponents.autocomplete.segment;

/**
 * Provides values for the HTML {@code autocomplete} for types of
 * {@link com.github.bordertech.wcomponents.autocomplete.type.Telephone telephone numbers} or
 * {@link com.github.bordertech.wcomponents.autocomplete.segment.PhonePart telephone number segments}.
 *
 * @author Mark Reeves
 * @since 1.5.3
 */
public enum PhoneFormat implements AutocompleteSegment {
	/**
	 * Indicates the field represents a number for contacting someone at their residence.
	 */
	HOME("home"),
	/**
	 * Indicates the field represents a number for contacting someone at their workplace.
	 */
	WORK("work"),
	/**
	 * Indicates the field represents a number for contacting someone regardless of location.
	 */
	MOBILE("mobile"),
	/**
	 * Indicates the field represents a fax machine's contact details.
	 */
	FAX("fax"),
	/**
	 * Indicates the field represents a pager's or beeper's contact details.
	 */
	PAGER("pager");
	/**
	 * The the {@code autocomplete} attribute value.
	 */
	private final String value;

	/**
	 * Creates each entry in the enumeration to allow for moderately type-safe auto-fill mnemonics.
	 *
	 * @param val the string to place in the {@code autocomplete} attribute
	 */
	PhoneFormat(final String val) {
		this.value = val;
	}

	@Override
	public String getValue() {
		return this.value;
	}

}
