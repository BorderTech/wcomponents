package com.github.bordertech.wcomponents.subordinate;

import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.util.mock.MockRequest;
import junit.framework.Assert;
import org.junit.Test;

/**
 * Test cases for {@link Not}.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class Not_Test {

	@Test
	public void testConstructor() {
		Condition cond = new TrueCondition();

		// Constructor - 1
		Not not = new Not(cond);
		Assert.assertEquals("Incorrect condition returned", cond, not.getCondition());
	}

	@Test
	public void testExecute() {
		// True Condition
		Not not = new Not(new TrueCondition());
		Assert.assertFalse("A Not with a true condition should return false", not.execute());
		Assert.assertFalse("A Not with a true condition should return false with a Request",
				not.execute(new MockRequest()));

		// False Condition
		not = new Not(new FalseCondition());
		Assert.assertTrue("A Not with a false condition should return true", not.execute());
		Assert.assertTrue("A Not with a false condition should return true with a Request",
				not.execute(new MockRequest()));
	}

	@Test
	public void testToString() {
		Not not = new Not(new TrueCondition());
		Assert.assertEquals("Incorrect toString for NOT", "not (true)", not.toString());
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
