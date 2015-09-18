package com.github.bordertech.wcomponents.subordinate;

import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.util.mock.MockRequest;
import junit.framework.Assert;
import org.junit.Test;

/**
 * JUnit tests for {@link AbstractCondition}.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class AbstractCondition_Test {

	@Test
	public void testTrueFalseAccessors() {
		MyCondition condition = new MyCondition();

		// True Condition
		condition.setTestCondition(true);
		Assert.assertTrue("Condition isTrue should be true", condition.isTrue());
		Assert.assertFalse("Condition isFalse should be false", condition.isFalse());

		// False Condition
		condition.setTestCondition(false);
		Assert.assertFalse("Condition isTrue should be false", condition.isTrue());
		Assert.assertTrue("Condition isFalse should be true", condition.isFalse());

		MockRequest request = new MockRequest();
		// True Condition
		request.setParameter("condition", "true");
		Assert.assertTrue("Condition isTrue should be true with Request", condition.isTrue(request));
		Assert.assertFalse("Condition isFalse should be false with Request", condition.isFalse(
				request));

		// False Condition
		request.setParameter("condition", "false");
		Assert.assertFalse("Condition isTrue should be false with Request", condition.
				isTrue(request));
		Assert.assertTrue("Condition isFalse should be true with Request", condition.
				isFalse(request));

	}

	/**
	 * Test Implementation class of AbstractCondition.
	 */
	private static class MyCondition extends AbstractCondition {

		/**
		 * Test condition value.
		 */
		private boolean testCondition;

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected boolean execute() {
			return testCondition;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected boolean execute(final Request request) {
			String param = request.getParameter("condition");
			return "true".equals(param);
		}

		/**
		 * @param testCondition the value to be returned by the condition.
		 */
		private void setTestCondition(final boolean testCondition) {
			this.testCondition = testCondition;
		}

	}

}
