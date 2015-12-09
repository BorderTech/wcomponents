package com.github.bordertech.wcomponents.subordinate.builder;

import com.github.bordertech.wcomponents.WDropdown;
import com.github.bordertech.wcomponents.WMultiSelect;
import com.github.bordertech.wcomponents.WTextArea;
import com.github.bordertech.wcomponents.WTextField;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * <p>
 * JUnit tests for the {@link ExpressionBuilder} class.
 * </p>
 * <p>
 * The tests use the <code>toString()</code> representation of the conditions as they are easier to write and are easier
 * to diagnose when the tests fail.
 * </p>
 *
 * @author Yiannis Paschalidis
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class ExpressionBuilder_Test {

	/**
	 * The builder being tested.
	 */
	private ExpressionBuilder builder;

	@Before
	public void createBuilder() {
		builder = new ExpressionBuilder();
	}

	@Test
	public void testValidate() {
		Assert.assertFalse("An empty expression should not be valid", builder.validate());

		builder.equals(new WTextField(), "1");
		Assert.assertTrue("A valid expression should be valid", builder.validate());
	}

	@Test
	public void testInvalidBuildMissingOperand() {
		builder.equals(new WTextField(), "1").and();
		Assert.assertNull("Invalid expression should not build", builder.build());
	}

	@Test
	public void testInvalidBuildExpressionNesting() {
		builder.equals(new WTextField(), "1").and(builder);
		Assert.assertNull("Invalid expression should not build", builder.build());
	}

	@Test
	public void testSimpleOneArgCondition() {
		builder.equals(new WTextField(), "1");
		Assert.assertEquals("Incorrect condition", "WTextField=\"1\"", builder.build().toString());
	}

	@Test
	public void testTwoArgOrCondition() {
		builder.equals(new WTextField(), "1").or().equals(new WTextArea(), "2");
		Assert.assertEquals("Incorrect condition", "(WTextField=\"1\" or WTextArea=\"2\")", builder.
				build().toString());
	}

	@Test
	public void testTwoArgAndCondition() {
		builder.equals(new WTextField(), "1").and().equals(new WTextArea(), "2");
		Assert
				.assertEquals("Incorrect condition", "(WTextField=\"1\" and WTextArea=\"2\")",
						builder.build().toString());
	}

	@Test
	public void testThreeArgOrCondition() {
		builder.equals(new WTextField(), "1").or().equals(new WTextArea(), "2").or().equals(
				new WDropdown(), "3");

		Assert.assertEquals("Incorrect condition",
				"(WTextField=\"1\" or WTextArea=\"2\" or WDropdown=\"3\")", builder
				.build().toString());
	}

	@Test
	public void testThreeArgAndCondition() {
		builder.equals(new WTextField(), "1").and().equals(new WTextArea(), "2").and().equals(
				new WDropdown(), "3");

		Assert.assertEquals("Incorrect condition",
				"(WTextField=\"1\" and WTextArea=\"2\" and WDropdown=\"3\")",
				builder.build().toString());
	}

	/**
	 * Tests for correct operator precedence when there is an AND on the right-hand side of the OR: a || b
	 * {@literal &}{@literal &} c.
	 */
	@Test
	public void testAndOperatorPrecedenceRHS() {
		builder.equals(new WTextField(), "1").or().equals(new WTextArea(), "2").and().equals(
				new WDropdown(), "3");

		Assert.assertEquals("Incorrect condition",
				"(WTextField=\"1\" or (WTextArea=\"2\" and WDropdown=\"3\"))",
				builder.build().toString());
	}

	/**
	 * Tests for correct operator precedence when there is an AND on the left-hand side of the OR: a
	 * {@literal &}{@literal &} b || c.
	 */
	@Test
	public void testAndOperatorPrecedenceLHS() {
		builder.equals(new WTextField(), "1").and().equals(new WTextArea(), "2").or().equals(
				new WDropdown(), "3");

		Assert.assertEquals("Incorrect condition",
				"((WTextField=\"1\" and WTextArea=\"2\") or WDropdown=\"3\")",
				builder.build().toString());
	}

	/**
	 * Tests for correct operator precedence when there are ANDs on both sides of the OR: a {@literal &}{@literal &} b
	 * || c {@literal &}{@literal &} d.
	 */
	@Test
	public void testAndOperatorPrecedenceBoth() {
		builder.equals(new WTextField(), "1").and().equals(new WTextArea(), "2").or().equals(
				new WDropdown(), "3")
				.and().equals(new WMultiSelect(), "4");

		Assert.assertEquals("Incorrect condition",
				"((WTextField=\"1\" and WTextArea=\"2\") or (WDropdown=\"3\" and WMultiSelect=\"4\"))",
				builder.build().toString());
	}

	/**
	 * Tests for correct operator precedence when there are ANDs on both sides of the OR: a || b
	 * {@literal &}{@literal &} c || d.
	 */
	@Test
	public void testOrOperatorPrecedenceBoth() {
		builder.equals(new WTextField(), "1").or().equals(new WTextArea(), "2").and().equals(
				new WDropdown(), "3").or()
				.equals(new WMultiSelect(), "4");

		Assert.assertEquals("Incorrect condition",
				"(WTextField=\"1\" or ((WTextArea=\"2\" and WDropdown=\"3\") or WMultiSelect=\"4\"))",
				builder.build().toString());
	}

	/**
	 * Test Not condition.
	 */
	@Test
	public void testNotCondition() {
		builder.not(new ExpressionBuilder().equals(new WTextArea(), "1"));

		Assert.assertEquals("Incorrect condition", "NOT (WTextArea=\"1\")", builder.build().
				toString());
	}

	/**
	 * Test Not with And condition.
	 */
	@Test
	public void testNotWithAndCondition() {

		builder.equals(new WTextField(), "1").and().not(new ExpressionBuilder().equals(
				new WTextArea(), "2")).or()
				.not(new ExpressionBuilder().equals(new WTextArea(), "2")).and().equals(
				new WDropdown(), "3");

		Assert
				.assertEquals("Incorrect condition",
						"((WTextField=\"1\" and NOT (WTextArea=\"2\")) or (NOT (WTextArea=\"2\") and WDropdown=\"3\"))",
						builder.build().toString());
	}

	/**
	 * Tests nesting of expression builders to change the order of operations: (a {@literal &}{@literal &} (b || c)
	 * {@literal &}{@literal &} d).
	 */
	@Test
	public void testNesting() {
		builder.equals(new WTextField(), "1")
				.and(new ExpressionBuilder().equals(new WTextArea(), "2").or().equals(
						new WDropdown(), "3")).and()
				.equals(new WMultiSelect(), "4");

		Assert.assertEquals("Incorrect condition",
				"(WTextField=\"1\" and (WTextArea=\"2\" or WDropdown=\"3\") and WMultiSelect=\"4\")",
				builder.build().toString());

	}

	/**
	 * Tests nesting of expression builders to change the order of operations: (a {@literal &}{@literal &} b) || (c
	 * {@literal &}{@literal &} d).
	 */
	@Test
	public void testAndandOrWithExpressions() {
		builder.equals(new WTextField(), "1").and(new ExpressionBuilder().equals(new WTextArea(),
				"2"))
				.or(new ExpressionBuilder().equals(new WDropdown(), "3")).and().equals(
				new WMultiSelect(), "4");

		Assert.assertEquals("Incorrect condition",
				"((WTextField=\"1\" and WTextArea=\"2\") or (WDropdown=\"3\" and WMultiSelect=\"4\"))",
				builder.build().toString());

	}

	@Test(expected = SyntaxException.class)
	public void testInvalidSyntaxConsecutiveEquals() {
		builder.equals(new WTextField(), "1").equals(new WTextArea(), "2");
	}

	@Test(expected = SyntaxException.class)
	public void testInvalidSyntaxAndWithoutOperand() {
		builder.and();
	}

	@Test(expected = SyntaxException.class)
	public void testInvalidSyntaxOrWithoutOperand() {
		builder.or();
	}

	@Test(expected = SyntaxException.class)
	public void testInvalidSyntaxConsecutiveAnds() {
		builder.equals(new WTextField(), "1").and().and();
	}

	@Test(expected = SyntaxException.class)
	public void testInvalidSyntaxConsecutiveOrs() {
		builder.equals(new WTextField(), "1").or().or();
	}

	@Test
	public void testBuildEquals() {
		builder.equals(new WTextField(), "1");
		Assert.assertEquals("Incorrect condition", "WTextField=\"1\"", builder.build().toString());
	}

	@Test
	public void testBuildNotEquals() {
		builder.notEquals(new WTextField(), "1");
		Assert.assertEquals("Incorrect condition", "WTextField!=\"1\"", builder.build().toString());
	}

	@Test
	public void testBuildLessThan() {
		builder.lessThan(new WTextField(), "1");
		Assert.assertEquals("Incorrect condition", "WTextField<\"1\"", builder.build().toString());
	}

	@Test
	public void testBuildLessThanOrEquals() {
		builder.lessThanOrEquals(new WTextField(), "1");
		Assert.assertEquals("Incorrect condition", "WTextField<=\"1\"", builder.build().toString());
	}

	@Test
	public void testBuildGreaterThan() {
		builder.greaterThan(new WTextField(), "1");
		Assert.assertEquals("Incorrect condition", "WTextField>\"1\"", builder.build().toString());
	}

	@Test
	public void testBuildGreaterThanOrEquals() {
		builder.greaterThanOrEquals(new WTextField(), "1");
		Assert.assertEquals("Incorrect condition", "WTextField>=\"1\"", builder.build().toString());
	}

	@Test
	public void testBuildMatches() {
		builder.matches(new WTextField(), "1");
		Assert.assertEquals("Incorrect condition", "WTextField matches \"1\"", builder.build().
				toString());
	}

}
