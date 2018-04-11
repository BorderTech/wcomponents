package com.github.bordertech.wcomponents.autocomplete;

import com.github.bordertech.wcomponents.util.Util;

/**
 * Provides values for the {@code autocomplete} attribute found on some HTML controls. See
 * <a href="https://html.spec.whatwg.org/multipage/form-control-infrastructure.html#autofilling-form-controls:-the-autocomplete-attribute"
 * target="_blank"> the HTML spec for autocomplete</a>.
 *
 * <p>
 * Note that there are some circumstances where the {@code autocomplete} attribute may be ignored or inferred. see <a
 * href="https://html.spec.whatwg.org/multipage/form-control-infrastructure.html#autofill-processing-model:attr-fe-autocomplete-off-6">the spec</a>.
 * </p>
 *
 * @author Mark Reeves
 */
public final class AutocompleteUtil {

	/**
	 * The separator used when an {@code autocomplete} attribute has multiple values.
	 */
	private static final String SEPARATOR = " ";

	/**
	 * The prefix which must be attached to a name to create a valid auto-fill section.
	 */
	private static final String SECTION_PREFIX = "section-";

	/**
	 * The value used to turn auto-fill off: must not be used with any other value.
	 */
	public static final String OFF = "off";

	/**
	 * Turn auto-fill explicitly "on". This is usually irrelevant as this is the default. Should not be used with any other value.
	 */
	public static final String ON = "on";

	public static enum DATE_AUTOCOMPLETE {
		/**
		 * Indicates the field represents a birthday as a <a href="https://html.spec.whatwg.org/multipage/common-microsyntaxes.html#valid-date-string"
		 * target="_blank">valid date String</a>; used in conjunction with {@link com.github.bordertech.wcomponents.WDateField}.
		 */
		BIRTHDAY("bday");

		/**
		 * The the {@code autocomplete} attribute value.
		 */
		private String value;

		private DATE_AUTOCOMPLETE(String value) {
			this.value = value;
		}

		/**
		 * @return the {@code autocomplete} attribute value for the enum member.
		 */
		public String getValue() {
			return value;
		}
	}


	public static enum EMAIL_AUTOCOMPLETE {
		/**
		 * Indicates the field represents an e-mail address.
		 */
		EMAIL("email");

		/**
		 * The the {@code autocomplete} attribute value.
		 */
		private String value;

		private EMAIL_AUTOCOMPLETE(String value) {
			this.value = value;
		}

		/**
		 * @return the {@code autocomplete} attribute value for the enum member.
		 */
		public String getValue() {
			return value;
		}
	}

	/**
	 * Provides values for the {@code autocomplete} attribute which are applicable to numeric inputs such as
	 * {@link com.github.bordertech.wcomponents.WNumberField}.
	 */
	public static enum NUMERIC_AUTOCOMPLETE {
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

		private NUMERIC_AUTOCOMPLETE(final String val) {
			value = val;
		}

		/**
		 * @return the {@code autocomplete} attribute value for the enum member.
		 */
		public String getValue() {
			return value;
		}
	}

	/**
	 * Provides values for the {@code autocomplete} attribute which are applicable to password inputs such as
	 * {@link com.github.bordertech.wcomponents.WPasswordField}.
	 */
	public static enum PASSWORD_AUTOCOMPLETE {
		/**
		 * Indicates the field represents the current password for the account identified by the {@link #USERNAME username} field (for example when
		 * logging in) and may only be used in conjunction with {@link com.github.bordertech.wcomponents.WPasswordField}.
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

		private PASSWORD_AUTOCOMPLETE(String value) {
			this.value = value;
		}

		/**
		 * @return the {@code autocomplete} attribute value for the enum member.
		 */
		public String getValue() {
			return value;
		}
	}

	/**
	 * Provides values for the HTML {@code autocomplete} attribute for an input in the telephone-number ('tel') state.
	 */
	public static enum TELEPHONE_AUTOCOMPLETE {
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

		private TELEPHONE_AUTOCOMPLETE(String value) {
			this.value = value;
		}

		/**
		 * @return the {@code autocomplete} attribute value for the enum member.
		 */
		public String getValue() {
			return this.value;
		}
	}

	/**
	 * Provides values for the HTML {@code autocomplete} for types of {@link TELEPHONE_AUTOCOMPLETE telephone numbers} or
	 * {@link TELEPHONE_SEGMENT telephone number segments}.
	 */
	public static enum TELEPHONE_TYPE {
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

		private TELEPHONE_TYPE(final String val) {
			this.value = val;
		}

		/**
		 * @return the {@code autocomplete} attribute value for the enum member.
		 */
		public String getValue() {
			return this.value;
		}
	}

	/**
	 * Provides values for the HTML {@code autocomplete} attribute for a parts of a telephone number.
	 */
	public static enum TELEPHONE_SEGMENT {
		/**
		 * Indicates the field represents an area code component of the telephone number, with a country-internal prefix applied if applicable, in
		 * the form of ASCII digits but not to be used with {@link com.github.bordertech.wcomponents.WNumberField}.
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
		LOCAL("tel-local"), // NOTE: this does appear in the TELEPHONE_AUTOCOMPLETE enum too.
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

		private TELEPHONE_SEGMENT(final String val) {
			this.value = val;
		}

		/**
		 * @return the {@code autocomplete} attribute value for the enum member.
		 */
		public String getValue() {
			return this.value;
		}
	}

	/**
	 * Provides values for the {@code autocomplete} attribute which are applicable to url inputs.
	 */
	public static enum URL_AUTOCOMPLETE {
		/**
		 * Indicates the field represents a home page or other Web page corresponding to the company, person, address, or contact information in the
		 * other fields associated with this field.
		 */
		URL("url"),
		/**
		 * Indicates the field represents a URL representing an instant messaging protocol endpoint (for example, "aim:goim?screenname=example" or
		 * "xmpp:user@example.net").
		 */
		IMPP("impp");

		/**
		 * The the {@code autocomplete} attribute value.
		 */
		private final String value;

		private URL_AUTOCOMPLETE(final String val) {
			value = val;
		}

		/**
		 * @return the {@code autocomplete} attribute value for the enum member.
		 */
		public String getValue() {
			return value;
		}
	}

	/**
	 * Types of address that may be used with address segments to create address {@code autocomplete} attribute values.
	 */
	public static enum ADDRESS_TYPE {
		/**
		 * Indicates the field represents an address (or address segment) that is part of a billing address.
		 */
		BILLING("billing"),
		/**
		 * Indicates the field represents am address (or address segment) that is part of a shipping address.
		 */
		SHIPPING("shipping");

		/**
		 * The the {@code autocomplete} attribute value.
		 */
		private final String value;

		private ADDRESS_TYPE(final String val) {
			this.value = val;
		}

		/**
		 * @return the {@code autocomplete} attribute value for the enum member.
		 */
		String getValue() {
			return this.value;
		}
	}

	/**
	 * Parts of an address that may be used to create address {@code autocomplete} attribute values. May be used in combination with an
	 * {@link #ADDRESS_TYPE} to specify separate addresses.
	 */
	public static enum ADDRESS_SEGMENT {
		/**
		 * Indicates the field represents a street address, for use with a multiple-line input such as
		 * {@link com.github.bordertech.wcomponents.WTextArea}.
		 * See <a href="https://html.spec.whatwg.org/multipage/form-control-infrastructure.html#attr-fe-autocomplete-street-address" target="_blank">
		 * https://html.spec.whatwg.org/multipage/form-control-infrastructure.html#attr-fe-autocomplete-street-address</a>.
		 *
		 * <p>
		 * To have auto-fill for a street address using single line inputs such as {@link com.github.bordertech.wcomponents.WTextField} use
		 * {@link #LINE_1}, {@link #LINE_2} and {@link #LINE_3}.
		 * </p>
		 */
		FULL_MULTILINE("street-address"),
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
		 * relevant, to the {@link #LEVEL_2 address-level2} field).
		 */
		POSTAL_CODE("postal-code");

		/**
		 * The the {@code autocomplete} attribute value.
		 */
		private final String value;

		private ADDRESS_SEGMENT(final String val) {
			this.value = val;
		}

		/**
		 * @return the {@code autocomplete} attribute value for the enum member.
		 */
		String getValue() {
			return this.value;
		}
	}

	// NAMES ##################################################################
	/**
	 * Full name, free form text, no new lines.
	 */
	public static final String NAME = "name";

	/**
	 * Indicates the field represents a pre-honorific, prefix or title, for example  "Mr.", "Ms.", "Dr.", "Mlle".
	 */
	public static final String PRE_HONORIFIC = "honorific-prefix";

	/**
	 * Indicates the field represents a given name (in some Western cultures, also known as the first name).
	 */
	public static final String GIVEN_NAME = "given-name";

	/**
	 * Indicates the field represents additional names (in some Western cultures, also known as middle names, forenames other than the first name).
	 */
	public static final String ADDITIONAL_NAME = "additional-name";

	/**
	 * Indicates the field represents a family name (in some Western cultures, also known as the last name or surname).
	 */
	public static final String FAMILY_NAME = "family-name";

	/**
	 * Indicates the field represents a post-honorific or suffix, for example "Bt.", "Jr.", "Ph.D..".
	 */
	public static final String POST_HONORIFIC = "honorific-suffix";

	/**
	 * Indicates the field represents a nickname, screen name, handle: a typically short name used instead of the full name.
	 */
	public static final String NICKNAME = "nickname";

	/**
	 * Indicates the field represents a job title, for example "Software Engineer", "Senior Vice President", "Deputy Managing Director".
	 */
	public static final String ORGANIZATION_TITLE = "organization-title";

	// AUTHORIZATION ##########################################################
	/**
	 * Indicates the field represents a username, for example for log-in purposes.
	 */
	public static final String USERNAME = "username";

	// CREDIT CARD OR PAYMENT DETAILS - ALSO SEE NUMERIC_AUTOCOMPLETE #########
	/**
	 * Indicates the field represents the full name as given on the payment instrument.
	 */
	public static final String CC_NAME = "cc-name";
	/**
	 * Indicates the field represents the given name as given on the payment instrument (in some Western cultures, also known as the first name).
	 */
	public static final String CC_GIVEN_NAME = "cc-given-name";
	/**
	 * Indicates the field represents additional names given on the payment instrument (in some Western cultures, also known as middle names,
	 * forenames other than the first name).
	 */
	public static final String CC_ADDITIONAL_NAME = "cc-additional-name";
	/**
	 * Indicates the field represents the family name given on the payment instrument (in some Western cultures, also known as the last name or
	 * surname).
	 */
	public static final String CC_FAMILY_NAME = "cc-family-name";
	/**
	 * Indicates the field represents the code identifying the payment instrument (for example the credit card number).
	 */
	public static final String CC_NUMBER = "cc-number";
	/**
	 * Indicates the field represents the expiration date of the payment instrument and must accept data in the form of a
	 * <A href="https://html.spec.whatwg.org/multipage/common-microsyntaxes.html#valid-month-string" target="_blank">valid month string</a>.
	 */
	public static final String CC_EXPIRY = "cc-exp";
	/**
	 * Indicates the field represents a security code for the payment instrument (also known as the card security code (CSC), card validation code
	 * (CVC), card verification value (CVV), signature panel code (SPC), credit card ID (CCID), etc). Note that this value is limited to ASCII
	 * digits but is <strong>not</strong> a numeric input and so should use {@link com.github.bordertech.wcomponents.WTextField} and should not be
	 * applied to {@link com.github.bordertech.wcomponents.WNumberField}.
	 */
	public static final String CC_CSC = "cc-csc";
	/**
	 * Indicates the field represents the type of payment instrument.
	 */
	public static final String CC_TYPE = "cc-type";
	/**
	 * Indicates the field represents the currency that the user would prefer the transaction to use and is in the form of an
	 * <a href="https://html.spec.whatwg.org/multipage/references.html#refsISO4217" target="_blank">ISO 4217</a> currency code.
	 */
	public static final String TRANSACTION_CURRENCY = "transaction-currency";

	// INDIVIDUALS other than name ############################################

	/**
	 * Indicates the field represents the person's preferred language as a valid
	 * <a href="https://html.spec.whatwg.org/multipage/references.html#refsBCP47" target="_blank">BCP 47</a> language tag.
	 */
	public static final String LANGUAGE = "language";
	/**
	 * Indicates the field represents a gender identity (for example Female, Fa'afafine).
	 */
	public static final String SEX = "sex";


	// ITEMS LINKED TO OTHER TYPES ############################################
	// The autocomplete values in this section may be applied to an address, a
	// name, a person or any other combination of other fields
	/**
	 * Indicates the field represents a company name corresponding to the person, address, or contact information in the other fields associated with
	 * this field.
	 */
	public static final String ORGANIZATION = "organization";
	/**
	 * Indicates the field represents a photograph, icon, or other image corresponding to the company, person, address, or contact information in the
	 * other fields associated with this field.
	 */
	public static final String PHOTO = "photo";



	/**
	 * Prevent instantiation.
	 */
	private AutocompleteUtil() {
	}

	/**
	 * Combines (preferably at least two otherwise this is pointless) Strings into a valid attribute value with appropriate separators. Note: if any
	 * argument is a case-insensitive match for "off" then only "off" will be returned.
	 *
	 * @param valueOne a value to add to the autocomplete attribute
	 * @param args any other values to add to the autocomplete attribute
	 * @return a value suitable for the HTML autocomplete attribute.
	 */
	public static String getCombinedAutocomplete(final String valueOne, final String ... args) {
		if (OFF.equalsIgnoreCase(valueOne)) {
			return OFF;
		}
		StringBuilder builder = new StringBuilder();
		if (valueOne != null) {
			builder.append(valueOne);
		}

		if (args != null) {
			for (String val : args) {
				if (!Util.empty(val)) {
					if (OFF.equalsIgnoreCase(val)) {
						return OFF;
					}
					builder.append(SEPARATOR);
					builder.append(val);
				}
			}
		}

		String built = builder.toString().trim();
		if (Util.empty(built)) {
			return null;
		}
		return built;
	}

	/*
	 * SECTION-* HELPERS
	 */

	/**
	 * Get an {@code autocomplete} attribute value for a named auto-fill section.
	 * <p>
	 * For information on sections see
	 * <a href="https://html.spec.whatwg.org/multipage/form-control-infrastructure.html#attr-fe-autocomplete-section" target="_blank">
	 * https://html.spec.whatwg.org/multipage/form-control-infrastructure.html#attr-fe-autocomplete-section</a>. Note that in this use a section is
	 * <strong>not</strong> a HTML section element as provided by {@link com.github.bordertech.wcomponents.WSection}.
	 * </p>
	 *
	 * @param sectionName the name of the autocomplete section required
	 * @return an autocomplete value for a named section
	 */
	public static String getNamedSection(final String sectionName) {
		if (Util.empty(sectionName)) {
			throw new IllegalArgumentException("argument must not be empty");
		}
		return SECTION_PREFIX.concat(sectionName);
	}

	/**
	 * Combine autocomplete values into a single String suitable to apply to a named auto-fill section.
	 * @param sectionName the name of the autocomplete section
	 * @param args any other valid autocomplete values
	 * @return a single attribute value useful to apply an autocomplete helper to a named section
	 */
	public static String getCombinedForSection(final String sectionName, final String ... args) {
		return getCombinedAutocomplete(getNamedSection(sectionName), args);
	}



	/*
	 * ADDRESS HELPERS
	 */

	/**
	 * Helper to generate an autocomplete attribute typed ("shipping" or "billing") address value with the correct value order.
	 * @param addressType the type of address {@link #ADDRESS_TYPE_SHIPPING} or {@link #ADDRESS_TYPE_BILLING}
	 * @param addressPart the address part, fir example {@link #ADDRESS_FULL_MULTILINE} or {@link #ADDRESS_LINE_1}
	 * @return the combined attribute value with the correct order of values.
	 */
	private static String makeTypedAddress(final ADDRESS_TYPE addressType, final ADDRESS_SEGMENT addressPart) {
		if (addressPart == null) {
			return getCombinedAutocomplete(addressType.getValue(), ADDRESS_SEGMENT.FULL_MULTILINE.getValue());
		}
		return getCombinedAutocomplete(addressType.getValue(), addressPart.getValue());
	}

	/**
	 * @param addressPart a part of an address, for example {@link #ADDRESS_LINE_1}
	 * @return an autocomplete attribute value for a shipping address with the correct order of values.
	 */
	public static String getShippingAddressAutocomplete(final ADDRESS_SEGMENT addressPart) {
		return makeTypedAddress(ADDRESS_TYPE.SHIPPING, addressPart);
	}

	/**
	 * @return an autocomplete attribute value for a shipping {@link #ADDRESS_FULL_MULTILINE street address} with the correct order of values.
	 */
	public static String getShippingAddressAutocomplete() {
			return getShippingAddressAutocomplete(ADDRESS_SEGMENT.FULL_MULTILINE);
	}

	/**
	 * @param addressPart a part of an address, for example {@link #ADDRESS_LINE_1}
	 * @return an autocomplete attribute value for a shipping address with the correct order of values.
	 */
	public static String getBillingAddressAutocomplete(final ADDRESS_SEGMENT addressPart) {
		return makeTypedAddress(ADDRESS_TYPE.BILLING, addressPart);
	}

	/**
	 * @return an autocomplete attribute value for a shipping {@link #ADDRESS_FULL_MULTILINE street address} with the correct order of values.
	 */
	public static String getBillingAddressAutocomplete() {
			return getBillingAddressAutocomplete(ADDRESS_SEGMENT.FULL_MULTILINE);
	}

}
