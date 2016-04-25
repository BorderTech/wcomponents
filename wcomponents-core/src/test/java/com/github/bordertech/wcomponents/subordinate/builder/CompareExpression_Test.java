package com.github.bordertech.wcomponents.subordinate.builder;

import com.github.bordertech.wcomponents.SubordinateTrigger;
import com.github.bordertech.wcomponents.WLabel;
import com.github.bordertech.wcomponents.WTextField;
import com.github.bordertech.wcomponents.subordinate.AbstractCompare.CompareType;
import com.github.bordertech.wcomponents.subordinate.Equal;
import com.github.bordertech.wcomponents.subordinate.GreaterThan;
import com.github.bordertech.wcomponents.subordinate.GreaterThanOrEqual;
import com.github.bordertech.wcomponents.subordinate.LessThan;
import com.github.bordertech.wcomponents.subordinate.LessThanOrEqual;
import com.github.bordertech.wcomponents.subordinate.Match;
import com.github.bordertech.wcomponents.subordinate.NotEqual;
import java.util.Date;
import junit.framework.Assert;
import org.junit.Test;

/**
 * JUnit tests for the {@link CompareExpression} class.
 *
 * @author Yiannis Paschalidis
 * @author Jonathan Austin
 * @since 1.0.0
 */
public final class CompareExpression_Test {

	@Test
	public void testConstructor() {
		SubordinateTrigger trigger = new WTextField();
		String value = "test";

		CompareExpression expr = new CompareExpression(CompareType.MATCH, trigger, value);
		Assert.assertEquals("Incorrect type", CompareType.MATCH, expr.getType());
		Assert.assertEquals("Incorrect trigger", trigger, expr.getTrigger());
		Assert.assertEquals("Incorrect value", value, expr.getValue());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructorWithNullType() {
		new CompareExpression(null, new WTextField(), null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructorWithNullTrigger() {
		new CompareExpression(CompareType.EQUAL, null, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructorWithMatchTypeNullValue() {
		new CompareExpression(CompareType.MATCH, new WTextField(), null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructorWithMatchTypeInvalidValue() {
		new CompareExpression(CompareType.MATCH, new WTextField(), new Date());
	}

	@Test
	public void testEvaluate() {
		// Setup a true "equals" condition
		CompareExpression expr = new CompareExpression(CompareType.EQUAL, new WTextField(), null);
		Assert.assertTrue("Evaluate for true compare should be true", expr.evaluate());

		// Setup a false "equals" condition
		expr = new CompareExpression(CompareType.EQUAL, new WTextField(), "not true");
		Assert.assertFalse("Evaluate for false compare should be false", expr.evaluate());
	}

	@Test
	public void testBuild() {
		SubordinateTrigger trigger = new WTextField();
		String value = "test";

		// Build Equal
		CompareExpression expr = new CompareExpression(CompareType.EQUAL, trigger, value);
		Equal equal = (Equal) expr.build();
		Assert.assertEquals("Equal condition returned invalid trigger", trigger, equal.getTrigger());
		Assert.assertEquals("Equal condition returned invalid value", value, equal.getValue());

		// Build NotEqual
		expr = new CompareExpression(CompareType.NOT_EQUAL, trigger, value);
		NotEqual notEqual = (NotEqual) expr.build();
		Assert.assertEquals("NotEqual condition returned invalid trigger", trigger, notEqual.
				getTrigger());
		Assert.assertEquals("NotEqual condition returned invalid value", value, notEqual.getValue());

		// Build LessThan
		expr = new CompareExpression(CompareType.LESS_THAN, trigger, value);
		LessThan lessThan = (LessThan) expr.build();
		Assert.assertEquals("LessThan condition returned invalid trigger", trigger, lessThan.
				getTrigger());
		Assert.assertEquals("LessThan condition returned invalid value", value, lessThan.getValue());

		// Build LessThanOrEqual
		expr = new CompareExpression(CompareType.LESS_THAN_OR_EQUAL, trigger, value);
		LessThanOrEqual lessThanOrEqual = (LessThanOrEqual) expr.build();
		Assert
				.assertEquals("LessThanOrEqual condition returned invalid trigger", trigger,
						lessThanOrEqual.getTrigger());
		Assert.assertEquals("LessThanOrEqual condition returned invalid value", value,
				lessThanOrEqual.getValue());

		// Build GreaterThan
		expr = new CompareExpression(CompareType.GREATER_THAN, trigger, value);
		GreaterThan greaterThan = (GreaterThan) expr.build();
		Assert.assertEquals("GreaterThan condition returned invalid trigger", trigger, greaterThan.
				getTrigger());
		Assert.assertEquals("GreaterThan condition returned invalid value", value, greaterThan.
				getValue());

		// Build GreaterThanOrEqual
		expr = new CompareExpression(CompareType.GREATER_THAN_OR_EQUAL, trigger, value);
		GreaterThanOrEqual greaterThanOrEqual = (GreaterThanOrEqual) expr.build();
		Assert.assertEquals("GreaterThanOrEqual condition returned invalid trigger", trigger,
				greaterThanOrEqual.getTrigger());
		Assert
				.assertEquals("GreaterThanOrEqual condition returned invalid value", value,
						greaterThanOrEqual.getValue());

		// Build Match
		expr = new CompareExpression(CompareType.MATCH, trigger, value);
		Match match = (Match) expr.build();
		Assert.assertEquals("Match condition returned invalid trigger", trigger, match.getTrigger());
		Assert.assertEquals("Match condition returned invalid value", value, match.getValue());
	}

	@Test
	public void testToString() {
		SubordinateTrigger trigger = new WTextField();
		String value = "test";

		// Equal
		CompareExpression expr = new CompareExpression(CompareType.EQUAL, trigger, value);
		Assert.assertEquals("Incorrect toString for equals compare", "WTextField=\"test\"", expr.
				toString());

		// NotEqual
		expr = new CompareExpression(CompareType.NOT_EQUAL, trigger, value);
		Assert.assertEquals("Incorrect toString for not equals compare", "WTextField!=\"test\"",
				expr.toString());

		// LessThan
		expr = new CompareExpression(CompareType.LESS_THAN, trigger, value);
		Assert.assertEquals("Incorrect toString for less than compare", "WTextField<\"test\"", expr.
				toString());

		// LessThanOrEqual
		expr = new CompareExpression(CompareType.LESS_THAN_OR_EQUAL, trigger, value);
		Assert.assertEquals("Incorrect toString for less than or equal compare",
				"WTextField<=\"test\"",
				expr.toString());

		// GreaterThan
		expr = new CompareExpression(CompareType.GREATER_THAN, trigger, value);
		Assert.assertEquals("Incorrect toString for greater than compare", "WTextField>\"test\"",
				expr.toString());

		// GreaterThanOrEqual
		expr = new CompareExpression(CompareType.GREATER_THAN_OR_EQUAL, trigger, value);
		Assert.assertEquals("Incorrect toString for greater thanor equal compare",
				"WTextField>=\"test\"",
				expr.toString());

		// Match
		expr = new CompareExpression(CompareType.MATCH, trigger, value);
		Assert.assertEquals("Incorrect toString for match compare", "WTextField matches \"test\"",
				expr.toString());

		// Test when a label is associated with the field
		expr = new CompareExpression(CompareType.EQUAL, trigger, value);
		new WLabel("My test field", trigger);
		Assert.assertEquals("Incorrect toString for equals compare with label",
				"My test field=\"test\"",
				expr.toString());

	}

}
