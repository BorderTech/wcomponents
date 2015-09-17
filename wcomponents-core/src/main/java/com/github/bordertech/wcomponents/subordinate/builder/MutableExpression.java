package com.github.bordertech.wcomponents.subordinate.builder;

import com.github.bordertech.wcomponents.subordinate.Condition;
import java.io.Serializable;

/**
 * A mutable wrapper for a boolean expression. Used by the {@link ExpressionBuilder}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
final class MutableExpression implements BooleanExpression, Serializable {

	/**
	 * Default serialisation identifier.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The wrapped boolean operand.
	 */
	private BooleanExpression expression;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean evaluate() {
		return expression == null ? Boolean.FALSE : expression.evaluate();
	}

	/**
	 * @return Returns the expression.
	 */
	public BooleanExpression getExpression() {
		return expression;
	}

	/**
	 * Sets the expression.
	 *
	 * @param expression The expression to set.
	 */
	public void setExpression(final BooleanExpression expression) {
		this.expression = expression;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return expression == null ? "" : expression.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Condition build() {
		return expression.build();
	}
}
