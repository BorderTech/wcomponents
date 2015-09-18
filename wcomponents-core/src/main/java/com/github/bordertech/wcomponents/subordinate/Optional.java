package com.github.bordertech.wcomponents.subordinate;

import com.github.bordertech.wcomponents.SubordinateTarget;
import com.github.bordertech.wcomponents.WLabel;

/**
 * An action that makes a given target optional.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class Optional extends AbstractSetMandatory {

	/**
	 * Creates an optional action with the given target.
	 *
	 * @param target the component to make optional.
	 */
	public Optional(final SubordinateTarget target) {
		super(target, Boolean.FALSE);
	}

	/**
	 * @return an action type of optional.
	 */
	@Override
	public ActionType getActionType() {
		return ActionType.OPTIONAL;
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

		return "set " + targetName + " optional";
	}

}
