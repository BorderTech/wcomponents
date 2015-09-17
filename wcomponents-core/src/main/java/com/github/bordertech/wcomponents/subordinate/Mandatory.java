package com.github.bordertech.wcomponents.subordinate;

import com.github.bordertech.wcomponents.SubordinateTarget;
import com.github.bordertech.wcomponents.WLabel;

/**
 * An action that makes a given target mandatory.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class Mandatory extends AbstractSetMandatory {

	/**
	 * Creates a mandatory action with the given target.
	 *
	 * @param target the component to make mandatory.
	 */
	public Mandatory(final SubordinateTarget target) {
		super(target, Boolean.TRUE);
	}

	/**
	 * @return an action type of mandatory.
	 */
	@Override
	public ActionType getActionType() {
		return ActionType.MANDATORY;
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

		return "set " + targetName + " mandatory";
	}

}
