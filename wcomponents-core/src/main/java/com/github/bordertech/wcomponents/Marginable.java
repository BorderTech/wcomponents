package com.github.bordertech.wcomponents;

/**
 * WComponents that can have a margin.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public interface Marginable extends WComponent {

	/**
	 * Set the margin for the component, or null for no margin.
	 *
	 * @param margin the margin for the component
	 */
	void setMargin(final Margin margin);

	/**
	 * Get the margin for the component, or null if not set.
	 *
	 * @return the margin for the component, or null if not set
	 */
	Margin getMargin();
}
