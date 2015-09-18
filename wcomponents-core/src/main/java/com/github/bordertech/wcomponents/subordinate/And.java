package com.github.bordertech.wcomponents.subordinate;

import com.github.bordertech.wcomponents.Request;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A logical AND of two or more conditions.
 *
 * @author Martin Shevchenko
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class And extends AbstractCondition {

	/**
	 * The list of conditions.
	 */
	private final List<Condition> conditions = new ArrayList<>(2);

	/**
	 * Creates an And condition.
	 *
	 * @param condition1 the first condition.
	 * @param condition2 the second condition.
	 */
	public And(final Condition condition1, final Condition condition2) {
		conditions.add(condition1);
		conditions.add(condition2);
	}

	/**
	 * Create an And condition with 3 or more conditions.
	 *
	 * @param condition1 the first condition
	 * @param condition2 the second condition
	 * @param conditions3 the nth conditions
	 */
	public And(final Condition condition1, final Condition condition2,
			final Condition... conditions3) {
		conditions.add(condition1);
		conditions.add(condition2);

		for (Condition condition : conditions3) {
			conditions.add(condition);
		}
	}

	/**
	 * Evaluates the condition. Note that this uses the short-circuit and operator, so condition 'b' will not
	 * necessarily be evaluated.
	 *
	 * @return true if all conditions are true, otherwise false
	 */
	@Override
	protected boolean execute() {
		for (Condition condition : conditions) {
			if (!condition.isTrue()) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Evaluates the condition using values on the Request. Note that this uses the short-circuit and operator, so
	 * condition 'b' will not necessarily be evaluated.
	 *
	 * @param request the request being processed.
	 * @return true if all conditions are true, otherwise false
	 */
	@Override
	protected boolean execute(final Request request) {
		for (Condition condition : conditions) {
			if (!condition.isTrue(request)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * @return the conditions which will be combined with a logical AND.
	 */
	public List<Condition> getConditions() {
		return Collections.unmodifiableList(conditions);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append('(');

		boolean firstCond = true;
		for (Condition cond : getConditions()) {
			if (!firstCond) {
				buf.append(" and ");
			}
			buf.append(cond);
			firstCond = false;
		}

		buf.append(')');
		return buf.toString();
	}

}
