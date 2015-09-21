package com.github.bordertech.wcomponents.subordinate;

import com.github.bordertech.wcomponents.Container;
import com.github.bordertech.wcomponents.Mandatable;
import com.github.bordertech.wcomponents.SubordinateTarget;
import com.github.bordertech.wcomponents.WComponent;

/**
 * An "action function" that sets the mandatory attribute on a wcomponent or group of wcomponents.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public abstract class AbstractSetMandatory extends AbstractAction {

	/**
	 * Creates a SetMandatory action function with the given target and value.
	 *
	 * @param target the component to make mandatory/optional
	 * @param value the value to make mandatory/optional
	 */
	public AbstractSetMandatory(final SubordinateTarget target, final Boolean value) {
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
			applyMandatoryAction(target, ((Boolean) value));
		}
	}

	/**
	 * Apply the mandatory action against the target and its children.
	 *
	 * @param target the target of this action
	 * @param mandatory is the evaluated value
	 */
	private void applyMandatoryAction(final WComponent target, final boolean mandatory) {
		if (target instanceof Mandatable) {
			((Mandatable) target).setMandatory(mandatory);
		} else if (target instanceof Container) { // Apply to the Mandatable children
			Container cont = (Container) target;
			final int size = cont.getChildCount();

			for (int i = 0; i < size; i++) {
				WComponent child = cont.getChildAt(i);
				applyMandatoryAction(child, mandatory);
			}
		}
	}
}
