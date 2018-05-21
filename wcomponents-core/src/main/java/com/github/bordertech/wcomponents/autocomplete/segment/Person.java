package com.github.bordertech.wcomponents.autocomplete.segment;

/**
 * Provides 'name' and other personal details values for the {@code autocomplete} attribute which are applicable to inputs which accept text-form
 * auto-fill hints.
 *
 * @author Mark Reeves
 * @since 1.5.3
 */
public enum Person implements AutocompleteSegment {
	/**
	 * Full name, free form text, no new lines.
	 */
	FULL("name"),
	/**
	 * Indicates the field represents a pre-honorific, prefix or title, for example "Mr.", "Ms.", "Dr.", "Mlle".
	 */
	PRE_HONORIFIC("honorific-prefix"),
	/**
	 * Indicates the field represents a given name (in some Western cultures, also known as the first name).
	 */
	GIVEN("given-name"),
	/**
	 * Indicates the field represents additional names (in some Western cultures, also known as middle names, forenames other than the first
	 * name).
	 */
	ADDITIONAL("additional-name"),
	/**
	 * Indicates the field represents a family name (in some Western cultures, also known as the last name or surname).
	 */
	FAMILY("family-name"),
	/**
	 * Indicates the field represents a post-honorific or suffix, for example "Bt.", "Jr.", "Ph.D..".
	 */
	POST_HONORIFIC("honorific-suffix"),
	/**
	 * Indicates the field represents a nickname, screen name, handle: a typically short name used instead of the full name.
	 */
	NICKNAME("nickname"),
	/**
	 * Indicates the field represents a job title, for example "Software Engineer", "Senior Vice President", "Deputy Managing Director".
	 */
	ORGANIZATION_TITLE("organization-title"),
	/**
	 * Indicates the field represents a username, for example for log-in purposes.
	 */
	USERNAME("username"),
	/**
	 * Indicates the field represents the person's preferred language as a valid
	 * <a href="https://html.spec.whatwg.org/multipage/references.html#refsBCP47" target="_blank">BCP 47</a> language tag.
	 */
	LANGUAGE("language"),
	/**
	 * Indicates the field represents a gender identity (for example Female, Fa'afafine).
	 */
	SEX("sex");
	/**
	 * The the {@code autocomplete} attribute value.
	 */
	private final String value;

	/**
	 * Creates each entry in the enumeration to allow for moderately type-safe auto-fill mnemonics.
	 *
	 * @param val the string to place in the {@code autocomplete} attribute
	 */
	Person(final String val) {
		value = val;
	}

	@Override
	public String getValue() {
		return value;
	}

}
