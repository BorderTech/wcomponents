package com.github.bordertech.wcomponents;

/**
 * This is a marker interface to indicate that this component can take placeholder text.
 * @author Rick Brown
 * @since 1.4
 */
public interface Placeholderable extends WComponent {

	/**
	 * Set placeholder text which will appear in the field if it is editable and has no content.
	 * @param placeholder The text to set as the placeholder.
	 */
	void setPlaceholder(final String placeholder);

	/**
	 * Get the placeholder text, if explicitly set.
	 * @return The placeholder text, if set.
	 */
	String getPlaceholder();
}
