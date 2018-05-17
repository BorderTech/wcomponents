package com.github.bordertech.wcomponents.autocomplete.type;

/**
 * Provides values for the {@code autocomplete} attribute which are applicable to url inputs.
 *
 * @author Mark Reeves
 * @since 1.5.3
 */
public enum Url {
	/**
	 * Indicates the field represents a home page or other Web page corresponding to the company, person, address, or contact information in the
	 * other fields associated with this field.
	 */
	URL("url"),
	/**
	 * Indicates the field represents a URL representing an instant messaging protocol endpoint (for example, "aim:goim?screenname=example" or
	 * "xmpp:user@example.net").
	 */
	IMPP("impp"),
	/**
	 * Indicates the field represents a photograph, icon, or other image corresponding to the company, person, address, or contact information in
	 * the other fields associated with this field.
	 */
	PHOTO("photo");
	/**
	 * The the {@code autocomplete} attribute value.
	 */
	private final String value;

	/**
	 * Creates each entry in the enumeration to allow for moderately type-safe auto-fill mnemonics.
	 *
	 * @param val the string to place in the {@code autocomplete} attribute
	 */
	Url(final String val) {
		value = val;
	}

	/**
	 * @return the {@code autocomplete} attribute value for the enum member.
	 */
	public String getValue() {
		return value;
	}

}
