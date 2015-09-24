package com.github.bordertech.wcomponents.subordinate;

import com.github.bordertech.wcomponents.SubordinateTarget;
import com.github.bordertech.wcomponents.WComponentGroup;
import com.github.bordertech.wcomponents.WLabel;

/**
 * An action that shows only one target component within a group of components.
 *
 * @author Martin Shevchenko
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class ShowInGroup extends AbstractSetVisible {

	/**
	 * Creates a ShowIn action with the given target.
	 *
	 * @param target the component to show in the group.
	 * @param group the group containing the target.
	 */
	public ShowInGroup(final SubordinateTarget target,
			final WComponentGroup<? extends SubordinateTarget> group) {
		// Hide everything in the group.
		super(group, Boolean.FALSE);
		setTargetInGroup(target);
	}

	/**
	 * Executes the action. Shows the target component and hides everything else in the group.
	 */
	@Override
	public void execute() {
		// Hide everything in the group.
		super.execute();

		// Now show the target
		applyAction(getTargetInGroup(), Boolean.TRUE);
	}

	/**
	 * @return the action type of showIn.
	 */
	@Override
	public ActionType getActionType() {
		return ActionType.SHOWIN;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		String targetInName = getTargetInGroup().getClass().getSimpleName();

		WLabel label = getTargetInGroup().getLabel();
		if (label != null) {
			targetInName = label.getText();
		}

		return "show " + targetInName + " in " + getTarget();
	}

}
