package com.github.bordertech.wcomponents.autocomplete;

import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.util.SystemException;
import com.github.bordertech.wcomponents.util.Util;

/**
 * Marks a component as being able to implement the {@code autocomplete} attribute.

 * <p>
 * See the <a href="https://html.spec.whatwg.org/multipage/form-control-infrastructure.html#attr-fe-autocomplete" target="_blank">HTML spec</a>
 * </p>
 *
 * @author Mark Reeves
 * @since 1.5.3
 */
public interface Autocompleteable extends WComponent {

	/**
	 * @return the value of the {@code autocomplete} attribute applied to the current field.
	 */
	String getAutocomplete();

	/**
	 * Turn {@code autocomplete} off for the current field.
	 */
	void setAutocompleteOff();

	/**
	 * Pre-pend an {@code autocomplete} section to the value of an {@code autocomplete} attribute for the current field.
	 *
	 * @param sectionName the name of the section being the part which would replace the asterisk in the form {@code section-*}
	 *
	 * @throws IllegalArgumentException if sectionName arg is empty
	 * @throws SystemException if a section is applied to a field with {@code autocomplete}  "off"
	 */
	void addAutocompleteSection(final String sectionName);

	/**
	 * Clear the {@code autocomplete} attribute.
	 */
	void clearAutocomplete();

	/**
	 * @return {@code true} if the current {@code autocomplete} setting is "off"
	 */
	default boolean isAutocompleteOff() {
		String autocomplete = getAutocomplete();
		if (Util.empty(autocomplete)) {
			return false;
		}
		return AutocompleteUtil.getOff().equalsIgnoreCase(autocomplete);
	}
}
