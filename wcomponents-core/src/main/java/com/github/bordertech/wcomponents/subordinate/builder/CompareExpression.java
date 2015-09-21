package com.github.bordertech.wcomponents.subordinate.builder;

import com.github.bordertech.wcomponents.SubordinateTrigger;
import com.github.bordertech.wcomponents.subordinate.AbstractCompare.CompareType;
import com.github.bordertech.wcomponents.subordinate.Condition;
import com.github.bordertech.wcomponents.subordinate.Equal;
import com.github.bordertech.wcomponents.subordinate.GreaterThan;
import com.github.bordertech.wcomponents.subordinate.GreaterThanOrEqual;
import com.github.bordertech.wcomponents.subordinate.LessThan;
import com.github.bordertech.wcomponents.subordinate.LessThanOrEqual;
import com.github.bordertech.wcomponents.subordinate.Match;
import com.github.bordertech.wcomponents.subordinate.NotEqual;

/**
 * <p>
 * This class describes a boolean expression that compares a trigger and a value.
 * </p>
 *
 * @author Yiannis Paschalidis
 * @author Jonathan Austin
 * @since 1.0.0
 */
public final class CompareExpression implements BooleanExpression {

	/**
	 * Default serialisation identifier.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The compare type for the expression.
	 */
	private final CompareType type;
	/**
	 * The trigger for the expression.
	 */
	private final SubordinateTrigger trigger;
	/**
	 * The compare value for the expression.
	 */
	private final Object value;

	/**
	 * @param type the compare type for the expression
	 * @param trigger the trigger for the expression
	 * @param value the compare value for the expression
	 */
	public CompareExpression(final CompareType type, final SubordinateTrigger trigger,
			final Object value) {
		if (type == null) {
			throw new IllegalArgumentException("Compare type can not be null");
		}

		if (trigger == null) {
			throw new IllegalArgumentException("Trigger can not be null");
		}

		if (CompareType.MATCH.equals(type) && !(value instanceof String)) {
			throw new IllegalArgumentException("The value for a match compare must be a String");
		}

		this.type = type;
		this.trigger = trigger;
		this.value = value;
	}

	/**
	 * @return the compare type.
	 */
	public CompareType getType() {
		return type;
	}

	/**
	 * @return the trigger to use in the expression.
	 */
	public SubordinateTrigger getTrigger() {
		return trigger;
	}

	/**
	 * @return the value to use in the expression.
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean evaluate() {
		// Without re-implementing the subordinate logic, use the subordinate compare to evaluate the value
		return build().isTrue();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Condition build() {
		switch (type) {
			case EQUAL:
				return new Equal(trigger, value);

			case NOT_EQUAL:
				return new NotEqual(trigger, value);

			case LESS_THAN:
				return new LessThan(trigger, value);

			case LESS_THAN_OR_EQUAL:
				return new LessThanOrEqual(trigger, value);

			case GREATER_THAN:
				return new GreaterThan(trigger, value);

			case GREATER_THAN_OR_EQUAL:
				return new GreaterThanOrEqual(trigger, value);

			case MATCH:
				return new Match(trigger, (String) value);

			default:
				throw new IllegalStateException("Unknown compare type " + type);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		String triggerName = trigger.getClass().getSimpleName();

		if (trigger.getLabel() != null) {
			triggerName = trigger.getLabel().getText();
		}

		switch (type) {
			case EQUAL:
				return triggerName + "=\"" + value + "\"";

			case NOT_EQUAL:
				return triggerName + "!=\"" + value + "\"";

			case LESS_THAN:
				return triggerName + "<\"" + value + "\"";

			case LESS_THAN_OR_EQUAL:
				return triggerName + "<=\"" + value + "\"";

			case GREATER_THAN:
				return triggerName + ">\"" + value + "\"";

			case GREATER_THAN_OR_EQUAL:
				return triggerName + ">=\"" + value + "\"";

			case MATCH:
				return triggerName + " matches \"" + value + "\"";

			default:
				throw new IllegalArgumentException("Unknown type: " + type);
		}
	}

}
