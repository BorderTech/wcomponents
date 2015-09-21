package com.github.bordertech.wcomponents;

/**
 * Expresses the notion that a WComponent can handle the idea of being "mandatory" or "optional".
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public interface Mandatable extends WComponent {

	/**
	 * Set whether or not this component is mandatory.
	 *
	 * @param mandatory true for mandatory, false for optional.
	 */
	void setMandatory(final boolean mandatory);

	/**
	 * Indicates whether or not this component is mandatory.
	 *
	 * @return true if the input is mandatory, false otherwise.
	 */
	boolean isMandatory();
}
