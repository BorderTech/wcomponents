package com.github.bordertech.wcomponents.subordinate;

import com.github.bordertech.wcomponents.SubordinateTrigger;
import com.github.bordertech.wcomponents.WLabel;
import java.util.regex.PatternSyntaxException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A logical condition that tests if the trigger matches the compare value as a regular expression.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class Match extends AbstractCompare {

	/**
	 * The logger instance for this class.
	 */
	private static final Log LOG = LogFactory.getLog(Match.class);

	/**
	 * Create a Match condition with a SubordinateTrigger and Compare value.
	 *
	 * @param trigger the trigger input field.
	 * @param compare the regular expression to be matched.
	 */
	public Match(final SubordinateTrigger trigger, final String compare) {
		super(trigger, compare);
	}

	/**
	 * @return the compare type of Match.
	 */
	@Override
	public CompareType getCompareType() {
		return CompareType.MATCH;
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

		final String aStr = aVal.toString();
		final String bStr = bVal.toString();

		try {
			return aStr.matches(bStr);
		} catch (PatternSyntaxException e) {
			LOG.warn("Invalid pattern (" + bStr + "). Will be ignored and condition will be false.");
			return false;
		}
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

		return triggerName + " matches \"" + getValue() + "\"";
	}

}
