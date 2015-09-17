package com.github.bordertech.wcomponents.subordinate;

import com.github.bordertech.wcomponents.AbstractWComponent;
import com.github.bordertech.wcomponents.AbstractWComponentTestCase;
import com.github.bordertech.wcomponents.SubordinateTarget;
import com.github.bordertech.wcomponents.WComponentGroup;
import junit.framework.Assert;
import org.junit.Test;

/**
 * JUnit tests for {@link AbstractAction}.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class AbstractAction_Test extends AbstractWComponentTestCase {

	@Test
	public void testConstructor() {
		SubordinateTarget target = new MyTarget();
		Object value = new Object();
		AbstractAction action = new MyAction(target, value);
		Assert.assertEquals("Incorrect Target returned", target, action.getTarget());
		Assert.assertEquals("Incorrect Value returned", value, action.getValue());

		// Null target
		try {
			action = new MyAction(null, new MyTarget());
			Assert.fail("Null target should have thrown an exception");
		} catch (IllegalArgumentException e) {
			Assert.assertNotNull("Exception should have a message", e.getMessage());
		}
	}

	@Test
	public void testTargetInGroupAccessors() {
		AbstractAction action = new MyAction(new MyTarget(), null);
		Assert.assertNull("TargetInGroup should be null by default", action.getTargetInGroup());
		// Set Target
		SubordinateTarget target = new MyTarget();
		action.setTargetInGroup(target);
		Assert.assertEquals("Incorrect TargetInGroup returned", target, action.getTargetInGroup());
	}

	@Test
	public void testExecute() {
		SubordinateTarget target = new MyTarget();
		String value = "TEST_VALUE";
		MyAction action = new MyAction(target, value);
		// Execute Action
		action.execute();
		Assert.assertEquals("Incorrect value set on target", value, ((MyTarget) target).getValue());
	}

	@Test
	public void testRecursiveWithWComponentGroup() {
		SubordinateTarget target1 = new MyTarget();
		SubordinateTarget target2 = new MyTarget();
		SubordinateTarget target3 = new MyTarget();

		WComponentGroup<SubordinateTarget> compGroup = new WComponentGroup<>();
		compGroup.addToGroup(target1);
		compGroup.addToGroup(target2);
		compGroup.addToGroup(target3);

		String value = "TEST_COMP";

		MyAction action = new MyAction(compGroup, value);

		// Execute Action
		setActiveContext(createUIContext());
		action.execute();

		Assert.
				assertEquals("Incorrect value set on target1", value, ((MyTarget) target1).
						getValue());
		Assert.
				assertEquals("Incorrect value set on target2", value, ((MyTarget) target2).
						getValue());
		Assert.
				assertEquals("Incorrect value set on target3", value, ((MyTarget) target3).
						getValue());
	}

	/**
	 * Test Implementation class of AbstractAction.
	 */
	public static class MyAction extends AbstractAction {

		/**
		 * Creates an action with the given target.
		 *
		 * @param target the component to disable.
		 * @param value the action value
		 */
		public MyAction(final SubordinateTarget target, final Object value) {
			super(target, value);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void applyAction(final SubordinateTarget target, final Object value) {
			MyTarget test = (MyTarget) target;
			test.setValue((String) value);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ActionType getActionType() {
			return null;
		}
	}

	/**
	 * Test Implementation class of SubordinateTarget.
	 */
	private static class MyTarget extends AbstractWComponent implements SubordinateTarget {

		/**
		 * The value of the target.
		 */
		private String value;

		/**
		 * @return the value of the target.
		 */
		public String getValue() {
			return value;
		}

		/**
		 * @param value the value of the target.
		 */
		public void setValue(final String value) {
			this.value = value;
		}
	}

}
