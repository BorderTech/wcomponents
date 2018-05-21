package com.github.bordertech.wcomponents.autocomplete.segment;

/**
 * Provides 'payment' (for example credit card) values for the {@code autocomplete} attribute which are applicable to inputs which accept
 * text-form auto-fill hints.
 *
 * @author Mark Reeves
 * @since 1.5.3
 */
public enum Payment implements AutocompleteSegment {
	/**
	 * Indicates the field represents the full name as given on the payment instrument.
	 */
	FULL_NAME("cc-name"),
	/**
	 * Indicates the field represents the given name as given on the payment instrument (in some Western cultures, also known as the first name).
	 */
	GIVEN_NAME("cc-given-name"),
	/**
	 * Indicates the field represents additional names given on the payment instrument (in some Western cultures, also known as middle names,
	 * forenames other than the first name).
	 */
	ADDITIONAL_NAME("cc-additional-name"),
	/**
	 * Indicates the field represents the family name given on the payment instrument (in some Western cultures, also known as the last name or
	 * surname).
	 */
	FAMILY_NAME("cc-family-name"),
	/**
	 * Indicates the field represents the code identifying the payment instrument (for example the credit card number).
	 */
	NUMBER("cc-number"),
	/**
	 * Indicates the field represents the expiration date of the payment instrument and must accept data in the form of a
	 * <A href="https://html.spec.whatwg.org/multipage/common-microsyntaxes.html#valid-month-string" target="_blank">valid month string</a>.
	 */
	EXPIRY("cc-exp"),
	/**
	 * Indicates the field represents a security code for the payment instrument (also known as the card security code (CSC), card validation
	 * code (CVC), card verification value (CVV), signature panel code (SPC), credit card ID (CCID), etc). Note that this value is limited to
	 * ASCII digits but is <strong>not</strong> a numeric input and so should use {@link com.github.bordertech.wcomponents.WTextField} and should
	 * not be applied to {@link com.github.bordertech.wcomponents.WNumberField}.
	 */
	CSC("cc-csc"),
	/**
	 * Indicates the field represents the type of payment instrument.
	 */
	TYPE("cc-type"),
	/**
	 * Indicates the field represents the currency that the user would prefer the transaction to use and is in the form of an
	 * <a href="https://html.spec.whatwg.org/multipage/references.html#refsISO4217" target="_blank">ISO 4217</a> currency code.
	 */
	CURRENCY("transaction-currency");
	/**
	 * The the {@code autocomplete} attribute value.
	 */
	private final String value;

	/**
	 * Creates each entry in the enumeration to allow for moderately type-safe auto-fill mnemonics.
	 *
	 * @param val the string to place in the {@code autocomplete} attribute
	 */
	Payment(final String val) {
		this.value = val;
	}

	@Override
	public String getValue() {
		return value;
	}

}
