package com.github.bordertech.wcomponents.subordinate;

import com.github.bordertech.wcomponents.SubordinateTarget;
import com.github.bordertech.wcomponents.WLabel;

/**
 * An action that hides (makes invisible) a given target component(s).
 *
 * @author Martin Shevchenko
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class Hide extends AbstractSetVisible {

	/**
	 * Creates a hide action with the given target.
	 *
	 * @param target the component to hide.
	 */
	public Hide(final SubordinateTarget target) {
		super(target, Boolean.FALSE);
	}

	/**
	 * @return the action type of hide.
	 */
	@Override
	public ActionType getActionType() {
		return ActionType.HIDE;
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

		return "hide " + targetName;
	}

}
