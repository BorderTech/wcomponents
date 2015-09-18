package com.github.bordertech.wcomponents.subordinate.builder;

import com.github.bordertech.wcomponents.SubordinateTrigger;
import com.github.bordertech.wcomponents.subordinate.AbstractCompare.CompareType;
import com.github.bordertech.wcomponents.subordinate.builder.GroupExpression.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import org.apache.commons.logging.LogFactory;

/**
 * The expression builder provides a convenient and more readable API for building conditions than using the various
 * Expression/Operand classes.
 *
 * @author Yiannis Paschalidis
 * @author Jonathan Austin
 * @since 1.0.0
 */
public final class ExpressionBuilder {

	/**
	 * The stack of expressions so far. This is used to provide correct operator precedence.
	 */
	private final Stack<GroupExpression> stack = new Stack<>();

	/**
	 * The rule which the expressions will be added to.
	 */
	private final MutableExpression expression;

	/**
	 * The current "left-hand-side" operand in the expression (if applicable).
	 */
	private BooleanExpression lhsExpression;

	/**
	 * Creates an ExpressionBuilder.
	 */
	public ExpressionBuilder() {
		this(new MutableExpression());
	}

	/**
	 * Creates an ExpressionBuilder. This method is package protected as developers should be obtaining an
	 * ExpressionBuilder editor using {@link SubordinateBuilder#condition()}.
	 *
	 * @param expression the expression to modify
	 */
	ExpressionBuilder(final MutableExpression expression) {
		this.expression = expression;
	}

	// ----------------------------------
	// Compare Expressions
	//
	/**
	 * Appends an equals test to the condition.
	 *
	 * @param trigger the trigger field.
	 * @param compare the value to use in the compare.
	 * @return this ExpressionBuilder.
	 */
	public ExpressionBuilder equals(final SubordinateTrigger trigger, final Object compare) {
		BooleanExpression exp = new CompareExpression(CompareType.EQUAL, trigger, compare);
		appendExpression(exp);

		return this;
	}

	/**
	 * Appends a not equals test to the condition.
	 *
	 * @param trigger the trigger field.
	 * @param compare the value to use in the compare.
	 * @return this ExpressionBuilder.
	 */
	public ExpressionBuilder notEquals(final SubordinateTrigger trigger, final Object compare) {
		BooleanExpression exp = new CompareExpression(CompareType.NOT_EQUAL, trigger, compare);
		appendExpression(exp);

		return this;
	}

	/**
	 * Appends a less than test to the condition.
	 *
	 * @param trigger the trigger field.
	 * @param compare the value to use in the compare.
	 * @return this ExpressionBuilder.
	 */
	public ExpressionBuilder lessThan(final SubordinateTrigger trigger, final Object compare) {
		BooleanExpression exp = new CompareExpression(CompareType.LESS_THAN, trigger, compare);
		appendExpression(exp);

		return this;
	}

	/**
	 * Appends a less than or equals test to the condition.
	 *
	 * @param trigger the trigger field.
	 * @param compare the value to use in the compare.
	 * @return this ExpressionBuilder.
	 */
	public ExpressionBuilder lessThanOrEquals(final SubordinateTrigger trigger, final Object compare) {
		BooleanExpression exp = new CompareExpression(CompareType.LESS_THAN_OR_EQUAL, trigger,
				compare);
		appendExpression(exp);

		return this;
	}

	/**
	 * Appends a greater than test to the condition.
	 *
	 * @param trigger the trigger field.
	 * @param compare the value to use in the compare.
	 * @return this ExpressionBuilder.
	 */
	public ExpressionBuilder greaterThan(final SubordinateTrigger trigger, final Object compare) {
		BooleanExpression exp = new CompareExpression(CompareType.GREATER_THAN, trigger, compare);
		appendExpression(exp);

		return this;
	}

	/**
	 * Appends a greater than or equals test to the condition.
	 *
	 * @param trigger the trigger field.
	 * @param compare the value to use in the compare.
	 * @return this ExpressionBuilder.
	 */
	public ExpressionBuilder greaterThanOrEquals(final SubordinateTrigger trigger,
			final Object compare) {
		BooleanExpression exp = new CompareExpression(CompareType.GREATER_THAN_OR_EQUAL, trigger,
				compare);
		appendExpression(exp);

		return this;
	}

	/**
	 * Appends a matches test to the condition.
	 *
	 * @param trigger the trigger field.
	 * @param compare the value to use in the compare.
	 * @return this ExpressionBuilder.
	 */
	public ExpressionBuilder matches(final SubordinateTrigger trigger, final String compare) {
		BooleanExpression exp = new CompareExpression(CompareType.MATCH, trigger, compare);
		appendExpression(exp);

		return this;
	}

	// ----------------------------------
	// Group Expressions - OR, AND, NOT
	//
	/**
	 * Appends an OR expression to the RHS of the expression. The current RHS of the expression must be an Operand.
	 *
	 * @return this ExpressionBuilder.
	 */
	public ExpressionBuilder or() {
		GroupExpression lastGroupExpression = stack.isEmpty() ? null : stack.peek();

		if (lhsExpression == null) {
			throw new SyntaxException("Syntax exception: OR missing LHS operand");
		} else if (lastGroupExpression == null) {
			GroupExpression or = new GroupExpression(GroupExpression.Type.OR);
			or.add(lhsExpression);
			stack.push(or);
			expression.setExpression(or);
			lhsExpression = null;
		} else if (lastGroupExpression.getType().equals(GroupExpression.Type.OR)) {
			// Keep using the existing OR
			lhsExpression = null;
			return this;
		} else if (lastGroupExpression.getType().equals(GroupExpression.Type.AND)) {
			// AND takes precedence over OR, so we wrap the AND
			// by removing it from any parent expressions and inserting the OR in its place

			GroupExpression and = stack.pop();
			GroupExpression or = new GroupExpression(GroupExpression.Type.OR);

			if (stack.isEmpty()) {
				expression.setExpression(or);
				or.add(and);
			} else {
				// need to get at the parent
				GroupExpression parent = stack.pop();
				parent.remove(and);
				parent.add(or);
				or.add(and);
			}

			stack.push(and);
			stack.push(or);
			lhsExpression = null;
		}

		return this;
	}

	/**
	 * Appends an AND expression to the RHS of the expression. The current RHS of the expression must be an Operand.
	 *
	 * @return this ExpressionBuilder.
	 */
	public ExpressionBuilder and() {
		GroupExpression lastGroupExpression = stack.isEmpty() ? null : stack.peek();

		if (lhsExpression == null) {
			throw new SyntaxException("Syntax exception: AND missing LHS operand");
		} else if (lastGroupExpression == null) {
			GroupExpression and = new GroupExpression(GroupExpression.Type.AND);
			and.add(lhsExpression);
			stack.push(and);
			expression.setExpression(and);
			lhsExpression = null;
		} else if (lastGroupExpression.getType().equals(GroupExpression.Type.AND)) {
			// Keep using the existing AND
			lhsExpression = null;
			return this;
		} else if (lastGroupExpression.getType().equals(GroupExpression.Type.OR)) {
			// AND takes precedence over OR, so we steal the OR's RHS
			GroupExpression or = lastGroupExpression;
			GroupExpression and = new GroupExpression(GroupExpression.Type.AND);

			or.remove(lhsExpression);
			and.add(lhsExpression);
			or.add(and);
			stack.push(and);
			lhsExpression = null;
		}

		return this;
	}

	/**
	 * Appends an AND expression to this expression.
	 *
	 * @param exp the builder containing the expression to AND with the current expression.
	 * @return this ExpressionBuilder.
	 */
	public ExpressionBuilder and(final ExpressionBuilder exp) {
		and().appendExpression(exp.expression.getExpression());
		return this;
	}

	/**
	 * Appends an OR expression to the RHS of the expression.
	 *
	 * @param exp the builder containing the expression to OR with the current expression.
	 * @return this ExpressionBuilder.
	 */
	public ExpressionBuilder or(final ExpressionBuilder exp) {
		or().appendExpression(exp.expression.getExpression());
		return this;
	}

	/**
	 * Appends a NOT expression to this expression.
	 *
	 * @param exp the builder containing the expression to NOT with the current expression.
	 * @return this ExpressionBuilder.
	 */
	public ExpressionBuilder not(final ExpressionBuilder exp) {
		// Note - NOT Expressions are not added to the stack (like the other group expressions AND and OR) and are
		// treated the same as a "compare" expression.
		// It is expected a NOT Expression is used by itself or with an AND or NOT.
		GroupExpression not = new GroupExpression(Type.NOT);
		not.add(exp.expression.getExpression());

		appendExpression(not);

		return this;
	}

	/**
	 * Appends the given expression to this expression.
	 *
	 * @param newExpression the expression to append.
	 */
	private void appendExpression(final BooleanExpression newExpression) {
		if (lhsExpression != null) {
			throw new SyntaxException("Syntax exception: use AND or OR to join expressions");
		}

		lhsExpression = newExpression;

		GroupExpression currentExpression = stack.isEmpty() ? null : stack.peek();

		if (currentExpression == null) {
			this.expression.setExpression(newExpression);
		} else {
			currentExpression.add(newExpression);
		}
	}

	/**
	 * Returns the built expression if it passes basic validation.
	 *
	 * @return the built expression, or null if the expression is invalid.
	 */
	protected BooleanExpression build() {
		if (validate()) {
			return expression.getExpression();
		}

		return null;
	}

	/**
	 * Determines whether the current expression is syntactically correct.
	 *
	 * @return true if the current expression is valid, false if not.
	 */
	public boolean validate() {
		try {
			BooleanExpression built = expression.getExpression();

			if (built == null) {
				// nothing to evaluate.
				return false;
			}

			// First check the nesting (an expression must not contain itself)
			checkNesting(built, new ArrayList<BooleanExpression>());

			// If the expression evaluates correctly, the syntax is correct.
			built.evaluate();
		} catch (Exception e) {
			LogFactory.getLog(getClass()).warn("Invalid expression: " + e.getMessage());
			return false;
		}

		return true;
	}

	/**
	 * Checks nesting of expressions to ensure we don't end up in an infinite recursive loop during evaluation.
	 *
	 * @param expression the expression to check
	 * @param visitedExpressions a list of expressions which have been visited so far.
	 */
	private static void checkNesting(final BooleanExpression expression,
			final List<BooleanExpression> visitedExpressions) {
		if (visitedExpressions.contains(expression)) {
			// Unfortunately, we can't give much more information - even calling toString() will overflow the stack.
			throw new SyntaxException("An expression can not contain itself.");
		}

		visitedExpressions.add(expression);

		if (expression instanceof GroupExpression) {
			GroupExpression group = (GroupExpression) expression;
			for (BooleanExpression operand : group.getOperands()) {
				checkNesting(operand, visitedExpressions);
			}
		}
	}
}
