package com.github.bordertech.wcomponents.subordinate;

import com.github.bordertech.wcomponents.AbstractWComponent;
import com.github.bordertech.wcomponents.ComponentModel;
import com.github.bordertech.wcomponents.Input;
import com.github.bordertech.wcomponents.Mandatable;
import com.github.bordertech.wcomponents.SubordinateTarget;
import com.github.bordertech.wcomponents.WField;
import com.github.bordertech.wcomponents.WFieldLayout;
import com.github.bordertech.wcomponents.WFieldSet;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WTextArea;
import junit.framework.Assert;
import org.junit.Test;

/**
 * JUnit tests for {@link AbstractSetEnable}.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class AbstractSetMandatory_Test {

	@Test
	public void testConstructor() {
		SubordinateTarget target = new MyTarget();
		Boolean value = Boolean.TRUE;

		// Constructor - 1
		AbstractSetMandatory mandatory = new MyMandatory(target, value);
		Assert.assertEquals("Incorrect target returned", target, mandatory.getTarget());
		Assert.assertEquals("Incorrect value returned", value, mandatory.getValue());
	}

	@Test
	public void testExecute() {
		// ---------------------
		// Valid Target (WInput) and TRUE Boolean Value
		SubordinateTarget target1 = new MyTarget();
		AbstractSetMandatory mandatory = new MyMandatory(target1, Boolean.TRUE);

		// Should be mandatory
		mandatory.execute();
		Assert.assertTrue("Target (Mandatable) should be mandatory", ((Mandatable) target1).
				isMandatory());

		// FALSE Boolean Value
		mandatory = new MyMandatory(target1, Boolean.FALSE);

		// Should not be mandatory
		mandatory.execute();
		Assert.assertFalse("Target (Mandatable) should not be mandatory", ((Mandatable) target1).
				isMandatory());

		// ---------------------
		// Valid Target (WField) and TRUE Boolean Value
		Input textArea = new WTextArea();
		WField target2 = new WFieldLayout().addField("test", textArea);
		mandatory = new MyMandatory(target2, Boolean.TRUE);

		// Should be mandatory
		mandatory.execute();
		Assert.assertTrue("Target (WField) should be mandatory", textArea.isMandatory());

		// FALSE Boolean Value
		mandatory = new MyMandatory(target2, Boolean.FALSE);

		// Should not be mandatory
		mandatory.execute();
		Assert.assertFalse("Target (WField) should not be mandatory", textArea.isMandatory());

		// ---------------------
		// Valid Target (WFieldSet) and TRUE Boolean Value
		WFieldSet target3 = new WFieldSet("Test");
		mandatory = new MyMandatory(target3, Boolean.TRUE);

		// Should be mandatory
		mandatory.execute();
		Assert.assertTrue("Target (WFieldSet) should be mandatory", target3.isMandatory());

		// FALSE Boolean Value
		mandatory = new MyMandatory(target3, Boolean.FALSE);

		// Should not be mandatory
		mandatory.execute();
		Assert.assertFalse("Target (WFieldSet) should not be mandatory", target3.isMandatory());

		// ---------------------
		// Invalid Target (Cannot be set Mandatory) and Boolean Value
		MyInvalidTarget target4 = new MyInvalidTarget();
		mandatory = new MyMandatory(target4, Boolean.TRUE);

		// Should do nothing
		mandatory.execute();

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

		AbstractSetMandatory enable = new MyMandatory(panel, Boolean.TRUE);

		// Targets should be optional by default
		Assert.assertFalse("WPanel - Target1 should be optional", target1.isMandatory());
		Assert.assertFalse("WPanel - Target2 should be optional", target2.isMandatory());
		Assert.assertFalse("WPanel - Target3 should be optional", target3.isMandatory());

		enable.execute();

		// Targets should be mandatory
		Assert.assertTrue("WPanel - Target1 should be mandatory", target1.isMandatory());
		Assert.assertTrue("WPanel - Target2 should be mandatory", target2.isMandatory());
		Assert.assertTrue("WPanel - Target3 should be mandatory", target3.isMandatory());
	}

	/**
	 * Test Implementation class of AbstractSetEnable.
	 */
	private static final class MyMandatory extends AbstractSetMandatory {

		/**
		 * Test Constructor.
		 *
		 * @param aTarget a test target
		 * @param aValue a test value
		 */
		private MyMandatory(final SubordinateTarget aTarget, final Boolean aValue) {
			super(aTarget, aValue);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ActionType getActionType() {
			return ActionType.MANDATORY;
		}

	}

	/**
	 * Test Implementation class of SubordinateTarget and Mandatable.
	 */
	private static class MyTarget extends AbstractWComponent implements SubordinateTarget,
			Mandatable {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean isMandatory() {
			return isFlagSet(ComponentModel.MANDATORY_FLAG);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void setMandatory(final boolean mandatory) {
			setFlag(ComponentModel.MANDATORY_FLAG, mandatory);
		}
	}

	/**
	 * Test Implementation class that cannot be made Mandatory.
	 */
	private static class MyInvalidTarget extends AbstractWComponent implements SubordinateTarget {
	}

}
