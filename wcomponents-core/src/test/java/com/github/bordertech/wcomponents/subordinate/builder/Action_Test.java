package com.github.bordertech.wcomponents.subordinate.builder;

import com.github.bordertech.wcomponents.AbstractWComponent;
import com.github.bordertech.wcomponents.SubordinateTarget;
import com.github.bordertech.wcomponents.WComponentGroup;
import com.github.bordertech.wcomponents.WLabel;
import junit.framework.Assert;
import org.junit.Test;

/**
 * JUnit tests for the {@link Action} class.
 *
 * @author Yiannis Paschalidis
 * @author Jonathan Austin
 * @since 1.0.0
 */
public final class Action_Test {

	@Test
	public void testConstructor1() {
		SubordinateTarget target = new MyTarget();

		Action action = new Action(
				com.github.bordertech.wcomponents.subordinate.Action.ActionType.DISABLE, target);

		Assert.assertEquals("Incorrect type returned",
				com.github.bordertech.wcomponents.subordinate.Action.ActionType.DISABLE,
				action.getType());
		Assert.assertEquals("Incorrect target returned", target, action.getTarget());
		Assert.assertNull("Group should be null", action.getGroup());
	}

	@Test
	public void testConstructor2() {
		SubordinateTarget target = new MyTarget();
		WComponentGroup<SubordinateTarget> group = new WComponentGroup<>();

		Action action = new Action(
				com.github.bordertech.wcomponents.subordinate.Action.ActionType.DISABLE, target,
				group);

		Assert.assertEquals("Incorrect type returned",
				com.github.bordertech.wcomponents.subordinate.Action.ActionType.DISABLE,
				action.getType());
		Assert.assertEquals("Incorrect target returned", target, action.getTarget());
		Assert.assertEquals("Incorrect group returned", group, action.getGroup());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructorWithNullType() {
		new Action(null, new MyTarget());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructorWithNullTarget() {
		new Action(com.github.bordertech.wcomponents.subordinate.Action.ActionType.DISABLE, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructorWithNullGroupShowIn() {
		new Action(com.github.bordertech.wcomponents.subordinate.Action.ActionType.SHOWIN,
				new MyTarget(), null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructorWithNullGroupHideIn() {
		new Action(com.github.bordertech.wcomponents.subordinate.Action.ActionType.HIDEIN,
				new MyTarget(), null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructorWithNullGroupEnableIn() {
		new Action(com.github.bordertech.wcomponents.subordinate.Action.ActionType.ENABLEIN,
				new MyTarget(), null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructorWithNullGroupDisableIn() {
		new Action(com.github.bordertech.wcomponents.subordinate.Action.ActionType.DISABLEIN,
				new MyTarget(), null);
	}

	/**
	 * Since the string representation is relied upon by other tests, it's worth testing specifically.
	 */
	@Test
	public void testToString() {
		SubordinateTarget target = new MyTarget();

		Action action = new Action(
				com.github.bordertech.wcomponents.subordinate.Action.ActionType.DISABLE, target);
		Assert.assertEquals("Incorrect toString for disable action", "disable MyTarget", action.
				toString());

		action = new Action(com.github.bordertech.wcomponents.subordinate.Action.ActionType.ENABLE,
				target);
		Assert.assertEquals("Incorrect toString for enable action", "enable MyTarget", action.
				toString());

		action = new Action(com.github.bordertech.wcomponents.subordinate.Action.ActionType.HIDE,
				target);
		Assert.
				assertEquals("Incorrect toString for hide action", "hide MyTarget", action.
						toString());

		action = new Action(
				com.github.bordertech.wcomponents.subordinate.Action.ActionType.MANDATORY, target);
		Assert.assertEquals("Incorrect toString for set mandatory action", "set MyTarget mandatory",
				action.toString());

		action = new Action(com.github.bordertech.wcomponents.subordinate.Action.ActionType.OPTIONAL,
				target);
		Assert.assertEquals("Incorrect toString for set optional action", "set MyTarget optional",
				action.toString());

		action = new Action(com.github.bordertech.wcomponents.subordinate.Action.ActionType.SHOW,
				target);
		Assert.
				assertEquals("Incorrect toString for show action", "show MyTarget", action.
						toString());

		action = new Action(com.github.bordertech.wcomponents.subordinate.Action.ActionType.SHOWIN,
				target,
				new WComponentGroup<SubordinateTarget>());
		Assert.assertEquals("Incorrect toString for showIn action",
				"show MyTarget in WComponentGroup([])",
				action.toString());

		action = new Action(com.github.bordertech.wcomponents.subordinate.Action.ActionType.HIDEIN,
				target,
				new WComponentGroup<SubordinateTarget>());
		Assert.assertEquals("Incorrect toString for hideIn action",
				"hide MyTarget in WComponentGroup([])",
				action.toString());

		action = new Action(com.github.bordertech.wcomponents.subordinate.Action.ActionType.ENABLEIN,
				target,
				new WComponentGroup<SubordinateTarget>());
		Assert.assertEquals("Incorrect toString for enableIn action",
				"enable MyTarget in WComponentGroup([])",
				action.toString());

		action = new Action(
				com.github.bordertech.wcomponents.subordinate.Action.ActionType.DISABLEIN, target,
				new WComponentGroup<SubordinateTarget>());
		Assert.assertEquals("Incorrect toString for disableIn action",
				"disable MyTarget in WComponentGroup([])",
				action.toString());

		// Test when a label is associated with the field
		action = new Action(com.github.bordertech.wcomponents.subordinate.Action.ActionType.SHOW,
				target);
		new WLabel("My test field", target);
		Assert.assertEquals("Incorrect toString for show action with label", "show My test field",
				action.toString());
	}

	/**
	 * Test component that implements the SubordinateTarget interface.
	 */
	private static class MyTarget extends AbstractWComponent implements SubordinateTarget {
	}
}
