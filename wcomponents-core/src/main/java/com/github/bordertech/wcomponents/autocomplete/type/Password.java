package com.github.bordertech.wcomponents.autocomplete.type;

/**
 * Provides values for the {@code autocomplete} attribute which are applicable to password inputs such as
 * {@link com.github.bordertech.wcomponents.WPasswordField}.
 *
 * @author Mark Reeves
 * @since 1.5.3
 */
public enum Password {
	/**
	 * Indicates the field represents the current password for the account identified by the username field (for example when logging in) and may
	 * only be used in conjunction with {@link com.github.bordertech.wcomponents.WPasswordField}.
	 */
	CURRENT("current-password"),
	/**
	 * Indicates the field represents a new password (for example when creating an account or changing a password) and may only be used in
	 * conjunction with {@link com.github.bordertech.wcomponents.WPasswordField}.
	 */
	NEW("new-password");
	/**
	 * The the {@code autocomplete} attribute value.
	 */
	private final String value;

	/**
	 * Creates each entry in the enumeration to allow for moderately type-safe auto-fill mnemonics.
	 *
	 * @param val the string to place in the {@code autocomplete} attribute
	 */
	Password(final String val) {
		this.value = val;
	}

	/**
	 * @return the {@code autocomplete} attribute value for the enum member.
	 */
	public String getValue() {
		return value;
	}

}
