package com.github.bordertech.wcomponents.autocomplete.type;

/**
 * Provides values for the {@code autocomplete} attribute which are applicable to email inputs such as
 * {@link com.github.bordertech.wcomponents.WEmailField}.
 *
 * @author Mark Reeves
 * @since 1.5.3
 */
public enum Email {
	/**
	 * Indicates the field represents an e-mail address.
	 */
	EMAIL("email");
	/**
	 * The the {@code autocomplete} attribute value.
	 */
	private final String value;

	/**
	 * Creates each entry in the enumeration to allow for moderately type-safe auto-fill mnemonics.
	 *
	 * @param val the string to place in the {@code autocomplete} attribute
	 */
	Email(final String val) {
		this.value = val;
	}

	/**
	 * @return the {@code autocomplete} attribute value for the enum member.
	 */
	public String getValue() {
		return value;
	}

}
