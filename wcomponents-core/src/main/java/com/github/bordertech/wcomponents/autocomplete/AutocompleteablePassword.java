package com.github.bordertech.wcomponents.autocomplete;

/**
 * Specific {@code autocomplete} attribute values for {@link com.github.bordertech.wcomponents.WPasswordField}. For allowed types see
 * <a href="https://html.spec.whatwg.org/multipage/form-control-infrastructure.html#control-group-password" target="_blank">the HTML spec</a>.
 *
 * @author Mark Reeves
 * @since 1.5.3
 */
public interface AutocompleteablePassword extends Autocompleteable {

	/**
	 * Set the {@code autocomplete} attribute of a password field to a given password auto-fill hint type within a named auto-fill section.
	 * @param passwordType the type of password field to auto-fill
	 * @param sectionName the name of the auto-fill section.
	 */
	void setAutocomplete(final AutocompleteUtil.PASSWORD_AUTOCOMPLETE passwordType, final String sectionName);

	/**
	 * Set the {@code autocomplete} attribute of a password field to a given password auto-fill hint type.
	 * @param passwordType the type of password field to auto-fill
	 */
	public default void setAutocomplete(final AutocompleteUtil.PASSWORD_AUTOCOMPLETE passwordType) {
		setAutocomplete(passwordType, null);
	}
}
