package com.github.bordertech.wcomponents.subordinate;

import com.github.bordertech.wcomponents.SubordinateTarget;
import com.github.bordertech.wcomponents.WLabel;

/**
 * An action that disables a given target component.
 *
 * @author Martin Shevchenko
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class Disable extends AbstractSetEnable {

	/**
	 * Creates a disable action with the given target.
	 *
	 * @param target the component to disable.
	 */
	public Disable(final SubordinateTarget target) {
		super(target, Boolean.FALSE);
	}

	/**
	 * @return an action type of disable.
	 */
	@Override
	public ActionType getActionType() {
		return ActionType.DISABLE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		String targetName = getTarget().getClass().getSimpleName();

		WLabel label = getTarget().getLabel();
		if (label != null) {
			targetName = label.getText();
		}

		return "disable " + targetName;
	}
}
