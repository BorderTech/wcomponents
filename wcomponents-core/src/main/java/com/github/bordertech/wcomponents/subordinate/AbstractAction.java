package com.github.bordertech.wcomponents.subordinate;

import com.github.bordertech.wcomponents.SubordinateTarget;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WComponentGroup;

/**
 * A class of "action functions" that apply a value to a target, where the target could be an individual component or a
 * group of components.
 *
 * @author Martin Shevchenko
 * @author Jonathan Austin
 * @since 1.0.0
 */
public abstract class AbstractAction implements Action {

	/**
	 * The target of the action.
	 */
	private final SubordinateTarget target;
	/**
	 * The value to be used in the action.
	 */
	private final Object value;
	/**
	 * The single component within a group that will be act upon.
	 */
	private SubordinateTarget targetInGroup;

	/**
	 * Creates an action with the given target.
	 *
	 * @param target the component to disable.
	 * @param value the value to be used in the action
	 */
	public AbstractAction(final SubordinateTarget target, final Object value) {
		if (target == null) {
			throw new IllegalArgumentException("Target cannot be null");
		}
		this.target = target;
		this.value = value;
	}

	/**
	 * @return the target for this action.
	 */
	@Override
	public SubordinateTarget getTarget() {
		return target;
	}

	/**
	 * @return value the value to be used in the action.
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * @param targetInGroup the target WComponent in a group.
	 */
	public void setTargetInGroup(final SubordinateTarget targetInGroup) {
		this.targetInGroup = targetInGroup;
	}

	/**
	 * Get the target component in a group.
	 *
	 * @return the target in a group.
	 */
	public SubordinateTarget getTargetInGroup() {
		return this.targetInGroup;
	}

	/**
	 * Execute the action.
	 */
	@Override
	public void execute() {
		if (target instanceof WComponentGroup<?>) {
			for (WComponent component : ((WComponentGroup<WComponent>) target).getComponents()) {
				if (component instanceof SubordinateTarget) {
					applyAction((SubordinateTarget) component, value);
				}
			}
		} else {
			// Leaf.
			applyAction(target, value);
		}

	}

	/**
	 * Apply the action against the target.
	 *
	 * @param aTarget the target of the action
	 * @param aValue is the evaluated value
	 */
	protected abstract void applyAction(SubordinateTarget aTarget, Object aValue);
}
