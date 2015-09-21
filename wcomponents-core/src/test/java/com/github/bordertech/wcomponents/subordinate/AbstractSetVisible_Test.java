package com.github.bordertech.wcomponents.subordinate;

import com.github.bordertech.wcomponents.AbstractWComponent;
import com.github.bordertech.wcomponents.AbstractWComponentTestCase;
import com.github.bordertech.wcomponents.ComponentModel;
import com.github.bordertech.wcomponents.SubordinateTarget;
import junit.framework.Assert;
import org.junit.Test;

/**
 * JUnit tests for {@link AbstractSetEnable}.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class AbstractSetVisible_Test extends AbstractWComponentTestCase {

	@Test
	public void testConstructor() {
		SubordinateTarget target = new MyTarget();
		Boolean value = Boolean.TRUE;

		// Constructor - 1
		AbstractSetVisible visible = new MyVisible(target, value);
		Assert.assertEquals("Incorrect target returned", target, visible.getTarget());
		Assert.assertEquals("Incorrect value returned", value, visible.getValue());
	}

	@Test
	public void testExecute() {
		//----------------------
		// Valid Target and FALSE Boolean Value
		SubordinateTarget target = new MyTarget();
		AbstractSetVisible visible = new MyVisible(target, Boolean.FALSE);

		// Execute Action
		visible.execute();

		// Should be hidden
		Assert.assertTrue("Target should be hidden", target.isHidden());
		// Should be visible
		Assert.assertTrue("Target should be visible", target.isVisible());
		// Should be not validate
		Assert.assertFalse("Target should not be validate", target.isValidate());

		//----------------------
		// Valid Target and TRUE Boolean Value
		target = new MyTarget();
		visible = new MyVisible(target, Boolean.TRUE);

		setFlag((MyTarget) target, ComponentModel.HIDE_FLAG, true);

		// Execute Action
		visible.execute();

		// Should be not hidden
		Assert.assertFalse("Target should not be hidden", target.isHidden());
		// Should be visible
		Assert.assertTrue("Target should be visible", target.isVisible());
		// Should be validate
		Assert.assertTrue("Target should not be validate", target.isValidate());
	}

	/**
	 * Test implementation class of AbstractSetVisible.
	 */
	private static final class MyVisible extends AbstractSetVisible {

		/**
		 * Test Constructor.
		 *
		 * @param aTarget a test target
		 * @param aValue a test value
		 */
		private MyVisible(final SubordinateTarget aTarget, final Boolean aValue) {
			super(aTarget, aValue);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ActionType getActionType() {
			return ActionType.SHOW;
		}
	}

	/**
	 * Test Implementation class of WInput.
	 */
	private static class MyTarget extends AbstractWComponent implements SubordinateTarget {
	}

}
