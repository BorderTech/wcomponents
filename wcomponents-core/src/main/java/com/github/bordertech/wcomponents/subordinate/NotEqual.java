package com.github.bordertech.wcomponents.subordinate;

import com.github.bordertech.wcomponents.SubordinateTrigger;
import com.github.bordertech.wcomponents.WLabel;
import com.github.bordertech.wcomponents.util.Util;

/**
 * A logical condition that tests if the trigger is not equal to the compare value.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class NotEqual extends AbstractCompare {

	/**
	 * Create a NotEqual condition with a SubordinateTrigger and Compare value.
	 *
	 * @param trigger the trigger input field.
	 * @param compare the value to use in the compare.
	 */
	public NotEqual(final SubordinateTrigger trigger, final Object compare) {
		super(trigger, compare);
	}

	/**
	 * @return the compare type of NotEqual.
	 */
	@Override
	public CompareType getCompareType() {
		return CompareType.NOT_EQUAL;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean doCompare(final Object aVal, final Object bVal) {
		return !Util.equals(aVal, bVal);
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

		return triggerName + "!=\"" + getValue() + "\"";
	}

}
