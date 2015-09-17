package com.github.bordertech.wcomponents.subordinate;

import com.github.bordertech.wcomponents.Request;

/**
 * A NOT condition.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class Not extends AbstractCondition {

	/**
	 * The list of conditions.
	 */
	private final Condition condition;

	/**
	 * Creates a Not condition.
	 *
	 * @param condition the condition to evaluate.
	 */
	public Not(final Condition condition) {
		this.condition = condition;
	}

	/**
	 * Evaluates the condition.
	 *
	 * @return true if the condition is false, otherwise false
	 */
	@Override
	protected boolean execute() {
		return !condition.isTrue();
	}

	/**
	 * Evaluates the condition using values on the Request.
	 *
	 * @param request the request being processed.
	 * @return true if the condition is false, otherwise false
	 */
	@Override
	protected boolean execute(final Request request) {
		return !condition.isTrue(request);
	}

	/**
	 * @return the condition to evaluate
	 */
	public Condition getCondition() {
		return condition;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "not (" + condition + ")";
	}

}
