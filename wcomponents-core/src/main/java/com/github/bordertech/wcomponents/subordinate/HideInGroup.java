package com.github.bordertech.wcomponents.subordinate;

import com.github.bordertech.wcomponents.SubordinateTarget;
import com.github.bordertech.wcomponents.WComponentGroup;
import com.github.bordertech.wcomponents.WLabel;

/**
 * An action that hides only one target component within a group of components.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class HideInGroup extends AbstractSetVisible {

	/**
	 * Creates a HideIn action with the given target.
	 *
	 * @param target the component to show in the group.
	 * @param group the group containing the target.
	 */
	public HideInGroup(final SubordinateTarget target,
			final WComponentGroup<? extends SubordinateTarget> group) {
		// Show everything in the group.
		super(group, Boolean.TRUE);
		setTargetInGroup(target);
	}

	/**
	 * Executes the action. Hides the target component and makes everything else visible in the group.
	 */
	@Override
	public void execute() {
		// Show everything in the group.
		super.execute();

		// Now make the target hidden
		applyAction(getTargetInGroup(), Boolean.FALSE);
	}

	/**
	 * @return the action type of hideIn.
	 */
	@Override
	public ActionType getActionType() {
		return ActionType.HIDEIN;
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

		return "hide " + targetInName + " in " + getTarget();
	}

}
