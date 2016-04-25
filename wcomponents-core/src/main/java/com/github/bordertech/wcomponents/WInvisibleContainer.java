package com.github.bordertech.wcomponents;

/**
 * <p>
 * This container is used as a marker for some special cases, where we want to skip normal WComponent life-cycle
 * processes.</p>
 *
 * <p>
 * It is always invisible.</p>
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WInvisibleContainer extends AbstractMutableContainer {

	/**
	 * Override isVisible so that the container does not take part in normal event handling and painting.
	 *
	 * @return false - this component is always invisible.
	 */
	@Override
	public boolean isVisible() {
		return false;
	}
}
