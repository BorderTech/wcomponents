package com.github.bordertech.wcomponents.subordinate;

import com.github.bordertech.wcomponents.InternalSubordinateUtil;
import com.github.bordertech.wcomponents.SubordinateTarget;

/**
 * An "action function" that sets the visibility on a WComponent or group of WComponents.
 *
 * @author Martin Shevchenko
 * @author Jonathan Austin
 * @since 1.0.0
 */
public abstract class AbstractSetVisible extends AbstractAction {

	/**
	 * Creates a AbstractSetVisible action with the given target and value.
	 *
	 * @param target the component to make visible/invisible
	 * @param value the value to make visible/invisible
	 */
	public AbstractSetVisible(final SubordinateTarget target, final Boolean value) {
		super(target, value);
	}

	/**
	 * Apply the action against the target.
	 *
	 * @param target the target of the action
	 * @param value is the evaluated value.
	 */
	@Override
	protected void applyAction(final SubordinateTarget target, final Object value) {
		InternalSubordinateUtil.applyAction(this, target, value);
	}
}
