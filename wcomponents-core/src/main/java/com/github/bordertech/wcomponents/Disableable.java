package com.github.bordertech.wcomponents;

/**
 * Expresses the notion that a WComponent can handle the idea of being "enabled" or "disabled".
 *
 * @author James Gifford
 * @since 1.0.0
 */
public interface Disableable extends WComponent {

	/**
	 * Indicates whether the Disableable is disabled.
	 *
	 * @return true if the Disableable is disabled, otherwise false.
	 */
	boolean isDisabled();

	/**
	 * Sets whether the Disableable is disabled.
	 *
	 * @param disabled if true, the Disableable is disabled. If false, it is enabled.
	 */
	void setDisabled(boolean disabled);
}
