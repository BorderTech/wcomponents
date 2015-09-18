package com.github.bordertech.wcomponents.subordinate;

import com.github.bordertech.wcomponents.SubordinateTrigger;
import com.github.bordertech.wcomponents.WLabel;

/**
 * A logical condition that tests if the trigger is less than the compare value.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class LessThan extends AbstractCompare {

	/**
	 * Create a LessThan condition with a SubordinateTrigger and Compare value.
	 *
	 * @param trigger the trigger input field.
	 * @param compare the value to use in the compare.
	 */
	public LessThan(final SubordinateTrigger trigger, final Object compare) {
		super(trigger, compare);
	}

	/**
	 * @return the compare type of LessThan.
	 */
	@Override
	public CompareType getCompareType() {
		return CompareType.LESS_THAN;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean doCompare(final Object aVal, final Object bVal) {
		// Cannot compare if either value is null (This matches client side logic)
		if (aVal == null || bVal == null) {
			return false;
		}

		return ((Comparable<Object>) aVal).compareTo(bVal) < 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		String triggerName = getTrigger().getClass().getSimpleName();

		WLabel label = getTrigger().getLabel();
		if (label != null) {
			triggerName = label.getText();
		}

		return triggerName + "<\"" + getValue() + "\"";
	}

}
