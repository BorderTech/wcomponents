package com.github.bordertech.wcomponents.subordinate;

import com.github.bordertech.wcomponents.AbstractWComponentTestCase;
import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.util.mock.MockRequest;
import java.util.Arrays;
import junit.framework.Assert;
import org.junit.Test;

/**
 * Test cases for {@link Or}.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class Or_Test extends AbstractWComponentTestCase {

	@Test
	public void testConstructors() {
		Condition condTrue1 = new TrueCondition();
		Condition condTrue2 = new TrueCondition();
		Condition condFalse1 = new FalseCondition();
		Condition condFalse2 = new FalseCondition();
		Condition condFalse3 = new FalseCondition();
		Condition condFalse4 = new FalseCondition();

		// Constructor - Two Conditions
		// Test True
		Or orTest = new Or(condFalse1, condTrue1);
		Assert.assertTrue("Constructor with two conditions: Or condition isTrue should be true",
				orTest.isTrue());
		orTest = new Or(condTrue1, condTrue2);
		Assert.assertTrue("Constructor with two conditions: Or condition isTrue should be true",
				orTest.isTrue());
		// Test False
		orTest = new Or(condFalse1, condFalse2);
		Assert.assertFalse("Constructor with two conditions: Or condition isTrue should be false",
				orTest.isTrue());

		// Constructor - Multiple Conditions
		// Test True
		orTest = new Or(condFalse1, condFalse2, condTrue1);
		Assert.
				assertTrue(
						"Constructor with multiple conditions: Or condition isTrue should be true",
						orTest.isTrue());
		orTest = new Or(condFalse1, condFalse2, condFalse3, condTrue1);
		Assert.
				assertTrue(
						"Constructor with multiple conditions: Or condition isTrue should be true",
						orTest.isTrue());

		// Test False
		orTest = new Or(condFalse1, condFalse2, condFalse3);
		Assert
				.assertFalse(
						"Constructor with multiple conditions: Or condition isTrue should be false",
						orTest.isTrue());
		orTest = new Or(condFalse1, condFalse2, condFalse3, condFalse4);
		Assert
				.assertFalse(
						"Constructor with multiple conditions: Or condition isTrue should be false",
						orTest.isTrue());
	}

	@Test
	public void testNestedConditions() {
		Condition condTrue1 = new TrueCondition();
		Condition condFalse1 = new FalseCondition();
		Condition condFalse2 = new FalseCondition();

		Or trueOr1 = new Or(condTrue1, condFalse1);
		Or trueOr2 = new Or(condTrue1, condFalse1);
		Or falseOr1 = new Or(condFalse1, condFalse2);
		Or falseOr2 = new Or(condFalse1, condFalse2);

		// Test True
		Or orTest = new Or(condFalse1, trueOr1);
		Assert.assertTrue("Nested Or condition isTrue should be true", orTest.isTrue());
		orTest = new Or(trueOr1, trueOr2);
		Assert.assertTrue("Nested Or condition isTrue should be true", orTest.isTrue());

		// Test False
		orTest = new Or(condFalse1, falseOr1);
		Assert.assertFalse("Nested Or condition isTrue should be false", orTest.isTrue());
		orTest = new Or(falseOr1, falseOr2);
		Assert.assertFalse("Nested Or condition isTrue should be false", orTest.isTrue());
	}

	@Test
	public void testIsFalse() {
		Condition condTrue1 = new TrueCondition();
		Condition condFalse1 = new FalseCondition();
		Condition condFalse2 = new FalseCondition();

		// Test True
		Or orTest = new Or(condTrue1, condFalse1);
		Assert.assertTrue("Or condition isTrue should be true", orTest.isTrue());
		Assert.assertFalse("Or condition isFalse should be false", orTest.isFalse());

		// Test False
		orTest = new Or(condFalse1, condFalse2);
		Assert.assertFalse("Or condition isTrue should be false", orTest.isTrue());
		Assert.assertTrue("Or condition isFalse should be true", orTest.isFalse());
	}

	@Test
	public void testRequestCondition() {
		Condition condTrue1 = new TrueCondition();
		Condition condFalse1 = new FalseCondition();
		Condition condFalse2 = new FalseCondition();

		// Mock Request
		setActiveContext(createUIContext());
		MockRequest request = new MockRequest();

		// Test True
		Or orTest = new Or(condTrue1, condFalse1);
		Assert.assertTrue("Or condition isTrue should be true with request", orTest.isTrue(request));
		Assert.assertFalse("Or condition isFalse should be false with request", orTest.isFalse(
				request));

		// Test False
		orTest = new Or(condFalse1, condFalse2);
		Assert.assertFalse("Or condition isTrue should be false with request", orTest.
				isTrue(request));
		Assert.assertTrue("Or condition isFalse should be true with request", orTest.
				isFalse(request));
	}

	@Test
	public void testGetConditions() {
		Condition condTrue1 = new TrueCondition();
		Condition condTrue2 = new TrueCondition();

		Or orTest = new Or(condTrue1, condTrue2);

		Assert.assertEquals("Invalid conditions returned", Arrays.asList(condTrue1, condTrue2),
				orTest.getConditions());
	}

	@Test
	public void testToString() {
		Or or = new Or(new TrueCondition(), new FalseCondition(), new TrueCondition());
		Assert.assertEquals("Incorrect toString for OR", "(true or false or true)", or.toString());
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
