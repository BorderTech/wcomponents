package com.github.bordertech.wcomponents.subordinate.builder;

import com.github.bordertech.wcomponents.subordinate.And;
import com.github.bordertech.wcomponents.subordinate.Condition;
import com.github.bordertech.wcomponents.subordinate.Not;
import com.github.bordertech.wcomponents.subordinate.Or;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 * This class describes a boolean expression. Since an expression evaluates to a boolean result, it may also be treated
 * as a BooleanExpression.
 * </p>
 *
 * @author Yiannis Paschalidis
 * @author Jonathan Austin
 * @since 1.0.0
 */
public final class GroupExpression implements BooleanExpression {

	/**
	 * Default serialisation identifier.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The list of operands in this expression.
	 */
	private final List<BooleanExpression> operands = new ArrayList<>();

	/**
	 * The type of expression.
	 */
	private final GroupExpression.Type type;

	/**
	 * The expression type.
	 */
	public enum Type {
		/**
		 * An expression where the result is the logical OR of at least two boolean operands.
		 */
		OR,
		/**
		 * An expression where the result is the logical AND of at least two boolean operands.
		 */
		AND,
		/**
		 * An expression where the result is the logical NOT of a boolean operand.
		 */
		NOT
	}

	/**
	 * Creates a BooleanExpression.
	 *
	 * @param type the type of expression.
	 */
	public GroupExpression(final Type type) {
		if (type == null) {
			throw new IllegalArgumentException("Group type can not be null");
		}
		this.type = type;
	}

	/**
	 * @return the expression type.
	 */
	public GroupExpression.Type getType() {
		return type;
	}

	/**
	 * Adds an operand to the expression.
	 *
	 * @param operand the operand to add.
	 */
	public void add(final BooleanExpression operand) {
		operands.add(operand);
	}

	/**
	 * Removes an operand from the expression.
	 *
	 * @param operand the operand to remove.
	 */
	public void remove(final BooleanExpression operand) {
		operands.remove(operand);
	}

	/**
	 * @return an immutable list of the current operands.
	 */
	public List<BooleanExpression> getOperands() {
		return Collections.unmodifiableList(operands);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean evaluate() {
		switch (getType()) {
			case OR: {
				if (operands.size() < 2) {
					throw new IllegalArgumentException(
							"An OR expression must have at least 2 operands");
				}

				for (BooleanExpression operand : operands) {
					if (operand.evaluate()) {
						return true;
					}
				}

				return false;
			}

			case AND: {
				if (operands.size() < 2) {
					throw new IllegalArgumentException(
							"An AND expression must have at least 2 operands");
				}

				for (BooleanExpression operand : operands) {
					if (!operand.evaluate()) {
						return false;
					}
				}

				return true;
			}

			case NOT: {
				if (operands.size() != 1) {
					throw new IllegalArgumentException("A NOT expression must have 1 operand");
				}

				return !operands.get(0).evaluate();
			}

			default:
				throw new IllegalArgumentException("Unknown type: " + type);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Condition build() {
		switch (getType()) {
			case OR: {
				List<Condition> conditions = new ArrayList<>(operands.size());

				for (BooleanExpression operand : operands) {
					conditions.add(operand.build());
				}

				// The subordinate API is a bit nasty, we have to split the list after the first two conditions.
				if (conditions.size() < 2) {
					throw new IllegalArgumentException(
							"An OR expression must include at least two operands.");
				}

				List<Condition> extraConditionsList = conditions.subList(2, conditions.size());
				Condition[] extraConditions = extraConditionsList.toArray(
						new Condition[extraConditionsList.size()]);
				return new Or(conditions.get(0), conditions.get(1), extraConditions);
			}
			case AND: {
				List<Condition> conditions = new ArrayList<>(operands.size());

				for (BooleanExpression operand : operands) {
					conditions.add(operand.build());
				}

				// The subordinate API is a bit nasty, we have to split the list after the first two conditions.
				if (conditions.size() < 2) {
					throw new IllegalArgumentException(
							"An AND expression must include at least two operands.");
				}

				List<Condition> extraConditionsList = conditions.subList(2, conditions.size());
				Condition[] extraConditions = extraConditionsList.toArray(
						new Condition[extraConditionsList.size()]);
				return new And(conditions.get(0), conditions.get(1), extraConditions);
			}

			case NOT:
				if (operands.size() != 1) {
					throw new IllegalArgumentException("A NOT expression must have 1 operand");
				}
				BooleanExpression operand = operands.get(0);
				return new Not(operand.build());

			default:
				throw new IllegalArgumentException("Unknown action type: " + getType());
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		switch (getType()) {
			case OR:
			case AND: {
				StringBuffer buf = new StringBuffer();
				buf.append('(');

				for (int i = 0; i < operands.size(); i++) {
					if (i > 0) {
						buf.append(getType() == Type.AND ? " and " : " or ");
					}

					buf.append(operands.get(i));
				}

				buf.append(')');
				return buf.toString();
			}

			case NOT: {
				StringBuffer buf = new StringBuffer();
				buf.append("NOT (");
				if (operands.size() > 0) {
					buf.append(operands.get(0));
				}
				buf.append(')');
				return buf.toString();
			}

			default:
				throw new IllegalArgumentException("Unknown type: " + getType());
		}
	}
}
