package com.github.bordertech.wcomponents.subordinate;

import com.github.bordertech.wcomponents.SubordinateTarget;
import com.github.bordertech.wcomponents.WComponentGroup;
import com.github.bordertech.wcomponents.WLabel;

/**
 * An action that enables only one target component within a group of components.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class EnableInGroup extends AbstractSetEnable {

	/**
	 * Creates a EnableIn action with the given target.
	 *
	 * @param target the component to enable in the group.
	 * @param group the group containing the target.
	 */
	public EnableInGroup(final SubordinateTarget target,
			final WComponentGroup<? extends SubordinateTarget> group) {
		// Disable everything in the group.
		super(group, Boolean.FALSE);
		setTargetInGroup(target);
	}

	/**
	 * Executes the action. Enables the target component and makes everything else disabled in the group.
	 */
	@Override
	public void execute() {
		// Disable everything in the group.
		super.execute();

		// Now make the target enabled
		applyAction(getTargetInGroup(), Boolean.TRUE);
	}

	/**
	 * @return the action type of enableIn.
	 */
	@Override
	public ActionType getActionType() {
		return ActionType.ENABLEIN;
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

		return "enable " + targetInName + " in " + getTarget();
	}

}
