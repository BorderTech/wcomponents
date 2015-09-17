package com.github.bordertech.wcomponents.subordinate;

import com.github.bordertech.wcomponents.Request;

/**
 * A "Condition" function that evaluate to either true or false.
 *
 * @author Martin Shevchenko
 * @author Jonathan Austin
 * @since 1.0.0
 */
public abstract class AbstractCondition implements Condition {

	/**
	 * Indicates whether this condition evaluates to 'true' in the given context.
	 *
	 * @return the boolean value of this condition.
	 */
	@Override
	public boolean isTrue() {
		return execute();
	}

	/**
	 * Indicates whether this condition evaluates to 'false' in the given context.
	 *
	 * @return the negation of the boolean value of this condition.
	 */
	@Override
	public boolean isFalse() {
		return !isTrue();
	}

	/**
	 * Indicates whether this condition evaluates to 'true' in the given context.
	 *
	 * @param request the request being processed.
	 * @return the boolean value of this condition.
	 */
	@Override
	public boolean isTrue(final Request request) {
		return execute(request);
	}

	/**
	 * Indicates whether this condition evaluates to 'false' in the given context.
	 *
	 * @param request the request being processed.
	 * @return the negation of the boolean value of this condition.
	 */
	@Override
	public boolean isFalse(final Request request) {
		return !isTrue(request);
	}

	/**
	 * Execute this condition.
	 *
	 * @return true if the condition evaluates to true, otherwise false
	 */
	protected abstract boolean execute();

	/**
	 * Execute this condition using the value on the Request.
	 *
	 * @param request the request being processed.
	 * @return true if the condition evaluates to true, otherwise false
	 */
	protected abstract boolean execute(final Request request);

}
