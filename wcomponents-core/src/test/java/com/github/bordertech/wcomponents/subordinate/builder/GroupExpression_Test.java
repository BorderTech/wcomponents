package com.github.bordertech.wcomponents.subordinate.builder;

import com.github.bordertech.wcomponents.WTextField;
import com.github.bordertech.wcomponents.subordinate.AbstractCompare.CompareType;
import com.github.bordertech.wcomponents.subordinate.And;
import com.github.bordertech.wcomponents.subordinate.Condition;
import com.github.bordertech.wcomponents.subordinate.Not;
import com.github.bordertech.wcomponents.subordinate.Or;
import java.util.Arrays;
import java.util.List;
import junit.framework.Assert;
import org.junit.Test;

/**
 * JUnit tests for the {@link GroupExpression} class.
 *
 * @author Yiannis Paschalidis
 * @author Jonathan Austin
 * @since 1.0.0
 */
public final class GroupExpression_Test {

	@Test
	public void testConstructor() {
		GroupExpression expr = new GroupExpression(GroupExpression.Type.AND);
		Assert.assertEquals("Incorrect type", GroupExpression.Type.AND, expr.getType());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructorWithNullType() {
		new GroupExpression(null);
	}

	@Test
	public void testAddOperand() {
		GroupExpression expr = new GroupExpression(GroupExpression.Type.AND);
		Assert.assertEquals("Should have no operands by default", 0, expr.getOperands().size());

		BooleanExpression operand1 = new BooleanLiteral(true);
		expr.add(operand1);
		Assert.assertEquals("Incorrect number of operands", 1, expr.getOperands().size());
		Assert.assertSame("Incorrect operand", operand1, expr.getOperands().get(0));

		BooleanExpression operand2 = new BooleanLiteral(false);
		expr.add(operand2);
		Assert.assertEquals("Incorrect number of operands", 2, expr.getOperands().size());
		Assert.assertSame("Incorrect operand 1", operand1, expr.getOperands().get(0));
		Assert.assertSame("Incorrect operand 2", operand2, expr.getOperands().get(1));
	}

	@Test
	public void testRemoveOperand() {
		GroupExpression expr = new GroupExpression(GroupExpression.Type.AND);

		BooleanExpression operand1 = new BooleanLiteral(true);
		BooleanExpression operand2 = new BooleanLiteral(false);
		expr.add(operand1);
		expr.add(operand2);

		expr.remove(operand2);
		Assert.assertEquals("Incorrect number of operands", 1, expr.getOperands().size());
		Assert.assertSame("Incorrect operand after remove of last", operand1, expr.getOperands().
				get(0));

		expr.add(operand2);
		expr.remove(operand1);
		Assert.assertEquals("Incorrect number of operands", 1, expr.getOperands().size());
		Assert.assertSame("Incorrect operand after remove of first", operand2, expr.getOperands().
				get(0));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testEvaluateInvalidAndNoOperands() {
		GroupExpression expr = new GroupExpression(GroupExpression.Type.AND);
		expr.evaluate();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testEvaluateInvalidAndOneOperand() {
		GroupExpression expr = new GroupExpression(GroupExpression.Type.AND);
		expr.add(new BooleanLiteral(true));
		expr.evaluate();
	}

	@Test
	public void testEvaluateAnd() {
		TruthTableRow[] testData = {
			// Two operands
			new TruthTableRow(false, false, false),
			new TruthTableRow(false, false, true),
			new TruthTableRow(false, true, false),
			new TruthTableRow(true, true, true),
			// Three operands
			new TruthTableRow(false, false, false, false),
			new TruthTableRow(false, false, false, true),
			new TruthTableRow(false, false, true, false),
			new TruthTableRow(false, false, true, true),
			new TruthTableRow(false, true, false, false),
			new TruthTableRow(false, true, false, true),
			new TruthTableRow(false, true, true, false),
			new TruthTableRow(true, true, true, true)};

		for (TruthTableRow row : testData) {
			GroupExpression expr = new GroupExpression(GroupExpression.Type.AND);

			for (boolean literal : row.operands) {
				expr.add(new BooleanLiteral(literal));
			}

			List<Boolean> operands = Arrays.asList(row.operands);
			Assert.assertEquals("Invalid AND of " + operands, row.expected, expr.evaluate().
					booleanValue());
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void testEvaluateInvalidOrNoOperands() {
		GroupExpression expr = new GroupExpression(GroupExpression.Type.OR);
		expr.evaluate();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testEvaluateInvalidOrOneOperand() {
		GroupExpression expr = new GroupExpression(GroupExpression.Type.OR);
		expr.add(new BooleanLiteral(true));
		expr.evaluate();
	}

	@Test
	public void testEvaluateOr() {
		TruthTableRow[] testData = {
			// Two operands
			new TruthTableRow(false, false, false),
			new TruthTableRow(true, false, true),
			new TruthTableRow(true, true, false),
			new TruthTableRow(true, true, true),
			// Three operands
			new TruthTableRow(false, false, false, false),
			new TruthTableRow(true, false, false, true),
			new TruthTableRow(true, false, true, false),
			new TruthTableRow(true, false, true, true),
			new TruthTableRow(true, true, false, false),
			new TruthTableRow(true, true, false, true),
			new TruthTableRow(true, true, true, false),
			new TruthTableRow(true, true, true, true)};

		for (TruthTableRow row : testData) {
			GroupExpression expr = new GroupExpression(GroupExpression.Type.OR);

			for (boolean literal : row.operands) {
				expr.add(new BooleanLiteral(literal));
			}

			List<Boolean> operands = Arrays.asList(row.operands);
			Assert.assertEquals("Invalid OR of " + operands, row.expected, expr.evaluate().
					booleanValue());
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void testEvaluateInvalidNotNoOperands() {
		GroupExpression expr = new GroupExpression(GroupExpression.Type.NOT);
		expr.evaluate();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testEvaluateInvalidNotTwoOperand() {
		GroupExpression expr = new GroupExpression(GroupExpression.Type.NOT);
		expr.add(new BooleanLiteral(true));
		expr.add(new BooleanLiteral(true));
		expr.evaluate();
	}

	@Test
	public void testEvaluateNot() {
		// True Literal
		GroupExpression expr = new GroupExpression(GroupExpression.Type.NOT);
		expr.add(new BooleanLiteral(true));

		Assert.assertFalse("NOT for true literal should be false", expr.evaluate());

		// False Literal
		expr = new GroupExpression(GroupExpression.Type.NOT);
		expr.add(new BooleanLiteral(false));

		Assert.assertTrue("NOT for false literal should be true", expr.evaluate());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testBuildInvalidAndNoOperands() {
		GroupExpression expr = new GroupExpression(GroupExpression.Type.AND);
		expr.build();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testBuildInvalidAndOneOperand() {
		GroupExpression expr = new GroupExpression(GroupExpression.Type.AND);
		expr.add(new BooleanLiteral(true));
		expr.build();
	}

	@Test
	public void testBuildAnd() {
		GroupExpression expr = new GroupExpression(GroupExpression.Type.AND);
		BooleanExpression operand1 = new CompareExpression(CompareType.EQUAL, new WTextField(), "1");
		BooleanExpression operand2 = new CompareExpression(CompareType.EQUAL, new WTextField(), "2");
		BooleanExpression operand3 = new CompareExpression(CompareType.EQUAL, new WTextField(), "3");

		expr.add(operand1);
		expr.add(operand2);
		expr.add(operand3);

		And condition = (And) expr.build();

		Assert.assertEquals("Incorrect number of conditions for AND", 3, condition.getConditions().
				size());
		Assert.assertEquals("Incorrect 1st operand for AND", operand1.build().toString(), condition.
				getConditions()
				.get(0).toString());
		Assert.assertEquals("Incorrect 2nd operand for AND", operand2.build().toString(), condition.
				getConditions()
				.get(1).toString());
		Assert.assertEquals("Incorrect 3rd operand for AND", operand3.build().toString(), condition.
				getConditions()
				.get(2).toString());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testBuildInvalidOrNoOperands() {
		GroupExpression expr = new GroupExpression(GroupExpression.Type.OR);
		expr.build();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testBuildInvalidOrOneOperand() {
		GroupExpression expr = new GroupExpression(GroupExpression.Type.OR);
		expr.add(new BooleanLiteral(true));
		expr.build();
	}

	@Test
	public void testBuildOr() {
		GroupExpression expr = new GroupExpression(GroupExpression.Type.OR);
		BooleanExpression operand1 = new CompareExpression(CompareType.EQUAL, new WTextField(), "1");
		BooleanExpression operand2 = new CompareExpression(CompareType.EQUAL, new WTextField(), "2");
		BooleanExpression operand3 = new CompareExpression(CompareType.EQUAL, new WTextField(), "3");

		expr.add(operand1);
		expr.add(operand2);
		expr.add(operand3);

		Or condition = (Or) expr.build();

		Assert.assertEquals("Incorrect 1st operand for OR", operand1.build().toString(),
				condition.getConditions().get(0).toString());
		Assert.assertEquals("Incorrect 2nd operand for OR", operand2.build().toString(),
				condition.getConditions().get(1).toString());
		Assert.assertEquals("Incorrect 3rd operand for OR", operand3.build().toString(),
				condition.getConditions().get(2).toString());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testBuildInvalidNotNoOperands() {
		GroupExpression expr = new GroupExpression(GroupExpression.Type.NOT);
		expr.build();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testBuildInvalidNotTwoOperands() {
		GroupExpression expr = new GroupExpression(GroupExpression.Type.NOT);
		expr.add(new BooleanLiteral(true));
		expr.add(new BooleanLiteral(true));
		expr.build();
	}

	@Test
	public void testBuildNot() {
		GroupExpression expr = new GroupExpression(GroupExpression.Type.NOT);
		BooleanExpression operand1 = new CompareExpression(CompareType.EQUAL, new WTextField(), "1");

		expr.add(operand1);

		Not condition = (Not) expr.build();

		Assert.assertEquals("Incorrect 1st operand for NOT", operand1.build().toString(), condition.
				getCondition()
				.toString());
	}

	@Test
	public void testToStringAnd() {
		// No Operands
		GroupExpression expr = new GroupExpression(GroupExpression.Type.AND);
		Assert.assertEquals("Invalid string for AND with no operands", "()", expr.toString());

		// One Operand
		expr.add(new BooleanLiteral(true));
		Assert.assertEquals("Invalid string for AND with one operand", "(true)", expr.toString());

		// Two Operand
		expr.add(new BooleanLiteral(false));
		Assert.assertEquals("Invalid string for AND with two operand", "(true and false)", expr.
				toString());

		// Three Operand
		expr.add(new BooleanLiteral(true));
		Assert.
				assertEquals("Invalid string for AND with three operand",
						"(true and false and true)", expr.toString());
	}

	@Test
	public void testToStringOr() {
		// No Operands
		GroupExpression expr = new GroupExpression(GroupExpression.Type.OR);
		Assert.assertEquals("Invalid string for OR with no operands", "()", expr.toString());

		// One Operand
		expr.add(new BooleanLiteral(true));
		Assert.assertEquals("Invalid string for OR with one operand", "(true)", expr.toString());

		// Two Operand
		expr.add(new BooleanLiteral(false));
		Assert.assertEquals("Invalid string for OR with two operand", "(true or false)", expr.
				toString());

		// Three Operand
		expr.add(new BooleanLiteral(true));
		Assert.assertEquals("Invalid string for OR with three operand", "(true or false or true)",
				expr.toString());
	}

	@Test
	public void testToStringNot() {
		// No Operands
		GroupExpression expr = new GroupExpression(GroupExpression.Type.NOT);
		Assert.assertEquals("Invalid string for NOT with no operands", "NOT ()", expr.toString());

		// One Operand
		expr.add(new BooleanLiteral(true));
		Assert.
				assertEquals("Invalid string for NOT with one operand", "NOT (true)", expr.
						toString());

		// Two Operand
		expr.add(new BooleanLiteral(false));
		Assert.
				assertEquals("Invalid string for NOT with two operand", "NOT (true)", expr.
						toString());
	}

	/**
	 * An entry in a truth table. Used to have data.
	 *
	 * @author Yiannis Paschalidis
	 */
	private static final class TruthTableRow {

		/**
		 * The expected value.
		 */
		private final boolean expected;

		/**
		 * The boolean operands.
		 */
		private final Boolean[] operands;

		/**
		 * Creates a TruthTableRow.
		 *
		 * @param expected the expected outcome.
		 * @param operands the expression operands.
		 */
		private TruthTableRow(final boolean expected, final Boolean... operands) {
			this.expected = expected;
			this.operands = operands;
		}
	}

	/**
	 * Test class for boolean literal.
	 */
	private static final class BooleanLiteral implements BooleanExpression {

		/**
		 * Value for the boolean literal.
		 */
		private final boolean value;

		/**
		 * @param value the value for the boolean literal
		 */
		private BooleanLiteral(final boolean value) {
			this.value = value;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Condition build() {
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Boolean evaluate() {
			return value;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return String.valueOf(value);
		}
	}
}
