package com.github.bordertech.wcomponents.subordinate;

import com.github.bordertech.wcomponents.SubordinateTarget;
import com.github.bordertech.wcomponents.WComponentGroup;
import com.github.bordertech.wcomponents.WLabel;

/**
 * An action that disables only one target component within a group of components.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class DisableInGroup extends AbstractSetEnable {

	/**
	 * Creates a DisableIn action with the given target.
	 *
	 * @param target the component to disable in the group.
	 * @param group the group containing the target.
	 */
	public DisableInGroup(final SubordinateTarget target,
			final WComponentGroup<? extends SubordinateTarget> group) {
		// Enable everything in the group.
		super(group, Boolean.TRUE);
		setTargetInGroup(target);
	}

	/**
	 * Executes the action. Disables the target component and makes everything else enabled in the group.
	 */
	@Override
	public void execute() {
		// Enable everything in the group.
		super.execute();

		// Now make the target disabled
		applyAction(getTargetInGroup(), Boolean.FALSE);
	}

	/**
	 * @return the action type of DisableIn.
	 */
	@Override
	public ActionType getActionType() {
		return ActionType.DISABLEIN;
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

		return "disable " + targetInName + " in " + getTarget();
	}

}
