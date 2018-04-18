package com.github.bordertech.wcomponents.autocomplete.type;

/**
 * Provides values for the {@code autocomplete} attribute which are applicable to numeric inputs such as
 * {@link com.github.bordertech.wcomponents.WNumberField}.
 *
 * @author Mark Reeves
 * @since 1.5.3
 */
public enum Numeric {
	/**
	 * Indicates the field represents the day component of birthday as a valid integer from 1 to 31; used in conjunction with
	 * {@link com.github.bordertech.wcomponents.WNumberField} or an appropriately configured selection tool such as
	 * {@link com.github.bordertech.wcomponents.WDropdown}.
	 */
	BIRTHDAY_DAY("bday-day"),
	/**
	 * Indicates the field represents the month component of birthday as a valid integer from 1 to 12; used in conjunction with
	 * {@link com.github.bordertech.wcomponents.WNumberField} or an appropriately configured selection tool such as
	 * {@link com.github.bordertech.wcomponents.WDropdown}.
	 */
	BIRTHDAY_MONTH("bday-month"),
	/**
	 * Indicates the field represents the year component of birthday as a valid positive integer; used in conjunction with
	 * {@link com.github.bordertech.wcomponents.WNumberField}.
	 */
	BIRTHDAY_YEAR("bday-year"),
	/**
	 * Indicates the field represents the month component of the expiration date of the payment instrument as an integer from 1 to 12 and should
	 * be used in conjunction with {@link com.github.bordertech.wcomponents.WNumberField}.
	 */
	CREDIT_CARD_EXPIRY_MONTH("cc-exp-month"),
	/**
	 * Indicates the field represents the year component of the expiration date of the payment instrument as a positive integer and should be used
	 * in conjunction with {@link com.github.bordertech.wcomponents.WNumberField}.
	 */
	CREDIT_CARD_EXPIRY_YEAR("cc-exp-year"),
	/**
	 * Indicates the field represents the amount that the user would like for the transaction (for example when entering a bid or sale price) as a
	 * floating point number; used in conjunction with {@link com.github.bordertech.wcomponents.WNumberField}.
	 */
	TRANSACTION_AMOUNT("transaction-amount");
	/**
	 * The the {@code autocomplete} attribute value.
	 */
	private final String value;

	/**
	 * Creates each entry in the enumeration to allow for moderately type-safe auto-fill mnemonics.
	 *
	 * @param val the string to place in the {@code autocomplete} attribute
	 */
	Numeric(final String val) {
		value = val;
	}

	/**
	 * @return the {@code autocomplete} attribute value for the enum member.
	 */
	public String getValue() {
		return value;
	}

}
