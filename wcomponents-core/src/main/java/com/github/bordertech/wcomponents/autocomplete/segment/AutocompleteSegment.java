package com.github.bordertech.wcomponents.autocomplete.segment;

/**
 * Provides a common interface for the enumerations of segments of an {@code autocomplete} attribute which is useful when a component exists in
 * several auto-fill control groups.
 *
 * @author Mark Reeves
 * @since 1.5.3
 */
public interface AutocompleteSegment {

	/**
	 * @return the {@code autocomplete} attribute value for the enum member.
	 */
	String getValue();
}
