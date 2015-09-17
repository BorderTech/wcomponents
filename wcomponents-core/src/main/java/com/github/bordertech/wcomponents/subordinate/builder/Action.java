package com.github.bordertech.wcomponents.subordinate.builder;

import com.github.bordertech.wcomponents.SubordinateTarget;
import com.github.bordertech.wcomponents.WComponentGroup;
import com.github.bordertech.wcomponents.WLabel;
import com.github.bordertech.wcomponents.subordinate.Disable;
import com.github.bordertech.wcomponents.subordinate.DisableInGroup;
import com.github.bordertech.wcomponents.subordinate.Enable;
import com.github.bordertech.wcomponents.subordinate.EnableInGroup;
import com.github.bordertech.wcomponents.subordinate.Hide;
import com.github.bordertech.wcomponents.subordinate.HideInGroup;
import com.github.bordertech.wcomponents.subordinate.Mandatory;
import com.github.bordertech.wcomponents.subordinate.Optional;
import com.github.bordertech.wcomponents.subordinate.Show;
import com.github.bordertech.wcomponents.subordinate.ShowInGroup;

/**
 * Describes an action to execute. This class is used by the {@link SubordinateBuilder} class to define what needs to be
 * done when a condition is met.
 *
 * @author Yiannis Paschalidis
 * @author Jonathan Austin
 * @since 1.0.0
 */
public final class Action {

	/**
	 * The type of action to perform.
	 */
	private final com.github.bordertech.wcomponents.subordinate.Action.ActionType type;

	/**
	 * The object to perform the action on.
	 */
	private final SubordinateTarget target;

	/**
	 * The group of targets that may be used in the action.
	 */
	private final WComponentGroup<? extends SubordinateTarget> group;

	/**
	 * Creates an action.
	 *
	 * @param type the type of action to execute.
	 * @param target the target to execute the action on.
	 */
	public Action(final com.github.bordertech.wcomponents.subordinate.Action.ActionType type,
			final SubordinateTarget target) {
		this(type, target, null);
	}

	/**
	 * Creates an action.
	 *
	 * @param type the type of action to execute.
	 * @param target the target to execute the action on.
	 * @param group the group of targets to use on the action.
	 */
	public Action(final com.github.bordertech.wcomponents.subordinate.Action.ActionType type,
			final SubordinateTarget target,
			final WComponentGroup<? extends SubordinateTarget> group) {
		if (type == null) {
			throw new IllegalArgumentException("Action type can not be null");
		}

		if (target == null) {
			throw new IllegalArgumentException("Action target can not be null");
		}

		if (group == null
				&& (com.github.bordertech.wcomponents.subordinate.Action.ActionType.HIDEIN.equals(
						type)
				|| com.github.bordertech.wcomponents.subordinate.Action.ActionType.SHOWIN.equals(
						type)
				|| com.github.bordertech.wcomponents.subordinate.Action.ActionType.ENABLEIN.equals(
						type) || com.github.bordertech.wcomponents.subordinate.Action.ActionType.DISABLEIN.
				equals(type))) {
			throw new IllegalArgumentException("Group can not be null");
		}

		this.type = type;
		this.target = target;
		this.group = group;
	}

	/**
	 * @return the action type.
	 */
	public com.github.bordertech.wcomponents.subordinate.Action.ActionType getType() {
		return type;
	}

	/**
	 * @return the target.
	 */
	public SubordinateTarget getTarget() {
		return target;
	}

	/**
	 * @return the group.
	 */
	public WComponentGroup<? extends SubordinateTarget> getGroup() {
		return group;
	}

	/**
	 * @return an instance of the subordinate action for this builder action
	 */
	public com.github.bordertech.wcomponents.subordinate.Action build() {
		switch (type) {
			case DISABLE:
				return new Disable(target);

			case ENABLE:
				return new Enable(target);

			case HIDE:
				return new Hide(target);

			case SHOW:
				return new Show(target);

			case MANDATORY:
				return new Mandatory(target);

			case OPTIONAL:
				return new Optional(target);

			case SHOWIN:
				return new ShowInGroup(target, group);

			case HIDEIN:
				return new HideInGroup(target, group);

			case ENABLEIN:
				return new EnableInGroup(target, group);

			case DISABLEIN:
				return new DisableInGroup(target, group);

			default:
				throw new IllegalArgumentException("Unknown action type: " + type);
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		String targetName = target.getClass().getSimpleName();

		WLabel label = target.getLabel();
		if (label != null) {
			targetName = label.getText();
		}

		switch (type) {
			case DISABLE:
				return "disable " + targetName;

			case ENABLE:
				return "enable " + targetName;

			case HIDE:
				return "hide " + targetName;

			case MANDATORY:
				return "set " + targetName + " mandatory";

			case OPTIONAL:
				return "set " + targetName + " optional";

			case SHOW:
				return "show " + targetName;

			case SHOWIN:
				return "show " + targetName + " in " + group;

			case HIDEIN:
				return "hide " + targetName + " in " + group;

			case ENABLEIN:
				return "enable " + targetName + " in " + group;

			case DISABLEIN:
				return "disable " + targetName + " in " + group;

			default:
				throw new IllegalArgumentException("Unknown type: " + type);
		}
	}
}
