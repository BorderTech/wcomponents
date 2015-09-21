package com.github.bordertech.wcomponents.subordinate;

import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.util.mock.MockRequest;
import java.util.Arrays;
import junit.framework.Assert;
import org.junit.Test;

/**
 * Test cases for {@link And}.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class And_Test {

	@Test
	public void testConstructors() {
		Condition condTrue1 = new TrueCondition();
		Condition condTrue2 = new TrueCondition();
		Condition condTrue3 = new TrueCondition();
		Condition condTrue4 = new TrueCondition();
		Condition condFalse = new FalseCondition();

		// Constructor - Two Conditions
		// Test True
		And and = new And(condTrue1, condTrue2);
		Assert.assertTrue("Constructor with two conditions: And condition isTrue should be true",
				and.isTrue());

		// Test False
		and = new And(condTrue1, condFalse);
		Assert.assertFalse("Constructor with two conditions: And condition isTrue should be false",
				and.isTrue());

		// Constructor - Multiple Conditions
		// Test True
		and = new And(condTrue1, condTrue2, condTrue3);
		Assert.assertTrue(
				"Constructor with multiple conditions: And condition isTrue should be true", and.
				isTrue());
		and = new And(condTrue1, condTrue2, condTrue3, condTrue4);
		Assert.assertTrue(
				"Constructor with multiple conditions: And condition isTrue should be true", and.
				isTrue());

		// Test False
		and = new And(condTrue1, condTrue2, condFalse);
		Assert.assertFalse(
				"Constructor with multiple conditions: And condition isTrue should be false", and.
				isTrue());
		and = new And(condTrue1, condTrue2, condTrue3, condFalse);
		Assert.assertFalse(
				"Constructor with multiple conditions: And condition isTrue should be false", and.
				isTrue());
	}

	@Test
	public void testNestedConditions() {
		Condition condTrue1 = new TrueCondition();
		Condition condTrue2 = new TrueCondition();
		Condition condFalse = new FalseCondition();

		// Create Nested Conditions
		And trueAnd1 = new And(condTrue1, condTrue2);
		And trueAnd2 = new And(condTrue1, condTrue2);
		And falseAnd = new And(condTrue1, condFalse);

		// Test True
		And and = new And(condTrue1, trueAnd1);
		Assert.assertTrue("And with nested conditions isTrue should be true", and.isTrue());
		and = new And(trueAnd1, trueAnd2);
		Assert.assertTrue("And with nested conditions isTrue should be true", and.isTrue());

		// Test False
		and = new And(condTrue1, falseAnd);
		Assert.assertFalse("And with nested conditions isTrue should be false", and.isTrue());
		and = new And(trueAnd1, falseAnd);
		Assert.assertFalse("And with nested conditions isTrue should be false", and.isTrue());
	}

	@Test
	public void testIsFalse() {
		Condition condTrue1 = new TrueCondition();
		Condition condTrue2 = new TrueCondition();
		Condition condFalse = new FalseCondition();

		// Test True
		And and = new And(condTrue1, condTrue2);
		Assert.assertTrue("And condition isTrue should be true", and.isTrue());
		Assert.assertFalse("And condition isFalse should be false", and.isFalse());

		// Test False
		and = new And(condTrue1, condFalse);
		Assert.assertFalse("And condition isTrue should be false", and.isTrue());
		Assert.assertTrue("And condition isFalse should be true", and.isFalse());
	}

	@Test
	public void testRequestConditions() {
		Condition condTrue1 = new TrueCondition();
		Condition condTrue2 = new TrueCondition();
		Condition condFalse = new FalseCondition();

		// Mock Request
		MockRequest request = new MockRequest();

		// Test True
		And and = new And(condTrue1, condTrue2);
		Assert.assertTrue("Request - And condition isTrue should be true", and.isTrue(request));
		Assert.assertFalse("Request - And condition isFalse should be false", and.isFalse(request));

		// Test False
		and = new And(condTrue1, condFalse);
		Assert.assertFalse("Request - And condition isTrue should be false", and.isTrue(request));
		Assert.assertTrue("Request - And condition isFalse should be true", and.isFalse(request));
	}

	@Test
	public void testGetConditions() {
		Condition condTrue1 = new TrueCondition();
		Condition condTrue2 = new TrueCondition();

		And and = new And(condTrue1, condTrue2);

		Assert.assertEquals("Invalid conditions returned", Arrays.asList(condTrue1, condTrue2), and.
				getConditions());
	}

	@Test
	public void testToString() {
		And and = new And(new TrueCondition(), new FalseCondition(), new TrueCondition());
		Assert.assertEquals("Incorrect toString for AND", "(true and false and true)", and.
				toString());
	}

	/**
	 * True Test Condition.
	 */
	private static class TrueCondition extends AbstractCondition {

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected boolean execute() {
			return true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected boolean execute(final Request request) {
			return true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return "true";
		}
	}

	/**
	 * False Test Condition.
	 */
	private static class FalseCondition extends AbstractCondition {

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected boolean execute() {
			return false;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected boolean execute(final Request request) {
			return false;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return "false";
		}
	}

}
