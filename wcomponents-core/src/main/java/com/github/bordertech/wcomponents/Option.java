package com.github.bordertech.wcomponents;

/**
 * Describes an option in a list-type control, e.g. {@link WDropdown}, {@link WMultiSelect} etc.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public interface Option {

	/**
	 * @return the code for this option.
	 */
	String getCode();

	/**
	 * @return the textual description of this option.
	 */
	String getDesc();
}
