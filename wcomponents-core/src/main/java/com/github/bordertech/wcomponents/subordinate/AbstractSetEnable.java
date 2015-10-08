package com.github.bordertech.wcomponents.subordinate;

import com.github.bordertech.wcomponents.Container;
import com.github.bordertech.wcomponents.Disableable;
import com.github.bordertech.wcomponents.SubordinateTarget;
import com.github.bordertech.wcomponents.WComponent;

/**
 * An "action function" that sets the enable/disable attribute on a wcomponent or group of wcomponents.
 *
 * @author Martin Shevchenko
 * @author Jonathan Austin
 * @since 1.0.0
 */
public abstract class AbstractSetEnable extends AbstractAction {

	/**
	 * Creates an action with the given target.
	 *
	 * @param target the component to enable/disable.
	 * @param value the value to enable/disable.
	 */
	public AbstractSetEnable(final SubordinateTarget target, final Boolean value) {
		super(target, value);
	}

	/**
	 * Apply the action against the target.
	 *
	 * @param target the target of this action
	 * @param value is the evaluated value
	 */
	@Override
	protected void applyAction(final SubordinateTarget target, final Object value) {
		// Should always be Boolean, but check anyway
		if (value instanceof Boolean) {
			applyEnableAction(target, ((Boolean) value));
		}
	}

	/**
	 * Apply the enable action against the target and its children.
	 *
	 * @param target the target of this action
	 * @param enabled is the evaluated value
	 */
	private void applyEnableAction(final WComponent target, final boolean enabled) {
		// Found Disableable component
		if (target instanceof Disableable) {
			target.setValidate(enabled);
			((Disableable) target).setDisabled(!enabled);
		} else if (target instanceof Container) {  // Apply to any Disableable children
			Container cont = (Container) target;
			final int size = cont.getChildCount();

			for (int i = 0; i < size; i++) {
				WComponent child = cont.getChildAt(i);
				applyEnableAction(child, enabled);
			}
		}
	}

}
