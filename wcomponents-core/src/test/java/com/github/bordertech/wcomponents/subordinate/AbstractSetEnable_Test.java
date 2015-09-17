package com.github.bordertech.wcomponents.subordinate;

import com.github.bordertech.wcomponents.AbstractWComponent;
import com.github.bordertech.wcomponents.ComponentModel;
import com.github.bordertech.wcomponents.Disableable;
import com.github.bordertech.wcomponents.SubordinateTarget;
import com.github.bordertech.wcomponents.WFieldSet;
import com.github.bordertech.wcomponents.WPanel;
import junit.framework.Assert;
import org.junit.Test;

/**
 * JUnit tests for {@link AbstractSetEnable}.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class AbstractSetEnable_Test {

	@Test
	public void testConstructor() {
		SubordinateTarget target = new MyTarget();
		Boolean value = Boolean.TRUE;

		// Constructor - 1
		AbstractSetEnable enable = new MyEnable(target, value);
		Assert.assertEquals("Incorrect target returned", target, enable.getTarget());
		Assert.assertEquals("Incorrect value returned", value, enable.getValue());
	}

	@Test
	public void testExecute() {
		// Valid Disableable Target and FALSE Boolean Value
		SubordinateTarget target = new MyTarget();
		AbstractSetEnable enable = new MyEnable(target, Boolean.FALSE);

		// Should be disabled
		enable.execute();
		Assert.assertTrue("Target should be disabled", ((Disableable) target).isDisabled());
		// Should be not validate
		Assert.assertFalse("Target should not be validate", target.isValidate());

		// Valid Disableable Target and TRUE Boolean Value
		target = new MyTarget();
		enable = new MyEnable(target, Boolean.TRUE);

		((Disableable) target).setDisabled(true);
		// Should be enabled
		enable.execute();
		Assert.assertFalse("Target should be enabled", ((Disableable) target).isDisabled());
		// Should be validate
		Assert.assertTrue("Target should be validate", target.isValidate());

		// Invalid Target (Not Disableable)
		target = new MyInvalidTarget();
		enable = new MyEnable(target, Boolean.FALSE);
		// Nothing happen to target
		enable.execute();
	}

	@Test
	public void testExecuteWithWPanel() {
		// Setup targets in a Panel
		WPanel panel = new WPanel();
		MyTarget target1 = new MyTarget();
		MyTarget target2 = new MyTarget();
		MyTarget target3 = new MyTarget();
		panel.add(target1);
		panel.add(target2);
		panel.add(target3);

		AbstractSetEnable enable = new MyEnable(panel, Boolean.FALSE);

		// Targets should be enabled by default
		Assert.assertFalse("WPanel - Target1 should be enabled", target1.isDisabled());
		Assert.assertFalse("WPanel - Target2 should be enabled", target2.isDisabled());
		Assert.assertFalse("WPanel - Target3 should be enabled", target3.isDisabled());

		enable.execute();

		// Targets should be disabled
		Assert.assertTrue("WPanel - Target1 should be disabled", target1.isDisabled());
		Assert.assertTrue("WPanel - Target2 should be disabled", target2.isDisabled());
		Assert.assertTrue("WPanel - Target3 should be disabled", target3.isDisabled());
	}

	@Test
	public void testExecuteWithWFieldSet() {
		// Setup targets in a FieldSet
		WFieldSet fieldSet = new WFieldSet("test");
		MyTarget target1 = new MyTarget();
		MyTarget target2 = new MyTarget();
		MyTarget target3 = new MyTarget();
		fieldSet.add(target1);
		fieldSet.add(target2);
		fieldSet.add(target3);

		AbstractSetEnable enable = new MyEnable(fieldSet, Boolean.FALSE);

		// Targets should be enabled by default
		Assert.assertFalse("WFieldSet - Target1 should be enabled", target1.isDisabled());
		Assert.assertFalse("WFieldSet - Target2 should be enabled", target2.isDisabled());
		Assert.assertFalse("WFieldSet - Target3 should be enabled", target3.isDisabled());

		enable.execute();

		// Targets should be disabled
		Assert.assertTrue("WFieldSet - Target1 should be disabled", target1.isDisabled());
		Assert.assertTrue("WFieldSet - Target2 should be disabled", target2.isDisabled());
		Assert.assertTrue("WFieldSet - Target3 should be disabled", target3.isDisabled());
	}

	/**
	 * Test Implementation class of AbstractSetEnable.
	 */
	private static final class MyEnable extends AbstractSetEnable {

		/**
		 * Test Constructor.
		 *
		 * @param aTarget a test target
		 * @param aValue a test value
		 */
		private MyEnable(final SubordinateTarget aTarget, final Boolean aValue) {
			super(aTarget, aValue);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ActionType getActionType() {
			return ActionType.ENABLE;
		}

	}

	/**
	 * Test Implementation class of SubordinateTarget and Disableable.
	 */
	private static class MyTarget extends AbstractWComponent implements SubordinateTarget,
			Disableable {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean isDisabled() {
			return isFlagSet(ComponentModel.DISABLED_FLAG);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void setDisabled(final boolean disabled) {
			setFlag(ComponentModel.DISABLED_FLAG, disabled);
		}
	}

	/**
	 * Test Implementation class of WInput (that is not Disableable).
	 */
	private static class MyInvalidTarget extends AbstractWComponent implements SubordinateTarget {
	}

}
