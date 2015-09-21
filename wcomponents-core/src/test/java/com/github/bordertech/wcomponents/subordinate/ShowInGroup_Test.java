package com.github.bordertech.wcomponents.subordinate;

import com.github.bordertech.wcomponents.AbstractWComponent;
import com.github.bordertech.wcomponents.AbstractWComponentTestCase;
import com.github.bordertech.wcomponents.ComponentModel;
import com.github.bordertech.wcomponents.SubordinateTarget;
import com.github.bordertech.wcomponents.WComponentGroup;
import com.github.bordertech.wcomponents.WLabel;
import com.github.bordertech.wcomponents.WTextField;
import junit.framework.Assert;
import org.junit.Test;

/**
 * Test cases for {@link ShowInGroup}.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class ShowInGroup_Test extends AbstractWComponentTestCase {

	@Test
	public void testConstructor() {
		SubordinateTarget target = new MyTarget();
		WComponentGroup<SubordinateTarget> group = new WComponentGroup<>();
		ShowInGroup action = new ShowInGroup(target, group);

		Assert.assertEquals("Value for ShowIn should be false", Boolean.FALSE, action.getValue());
		Assert.assertEquals("Target for ShowIn should be the group", group, action.getTarget());
		Assert.assertEquals("TargetInGroup for ShowIn should be the target", target, action.
				getTargetInGroup());
	}

	@Test
	public void testShowInWComponentGroup() {
		MyTarget target1 = new MyTarget();
		MyTarget target2 = new MyTarget();
		MyTarget target3 = new MyTarget();
		WComponentGroup<SubordinateTarget> group = new WComponentGroup<>();
		group.addToGroup(target1);
		group.addToGroup(target2);
		group.addToGroup(target3);
		ShowInGroup action = new ShowInGroup(target2, group);

		setFlag(target1, ComponentModel.HIDE_FLAG, true);
		setFlag(target2, ComponentModel.HIDE_FLAG, true);
		setFlag(target3, ComponentModel.HIDE_FLAG, true);

		// Check all hidden
		AssertTargetUtil.assertTargetsHidden(target1, target2, target3);
		AssertTargetUtil.assertTargetsVisible(target1, target2, target3);

		// Execute Action
		setActiveContext(createUIContext());
		action.execute();

		// Check only target2 is not hidden
		Assert.assertTrue("Target1 should be hidden", target1.isHidden());
		Assert.assertFalse("Target2 should not be hidden", target2.isHidden());
		Assert.assertTrue("Target3 should be hidden", target3.isHidden());
		AssertTargetUtil.assertTargetsVisible(target1, target2, target3);
	}

	@Test
	public void testActionType() {
		SubordinateTarget target = new WTextField();
		WComponentGroup<SubordinateTarget> group = new WComponentGroup<>();
		group.addToGroup(target);
		ShowInGroup action = new ShowInGroup(target, group);
		Assert.assertEquals("Incorrect Action Type", action.getActionType(),
				AbstractAction.ActionType.SHOWIN);
	}

	@Test
	public void testToString() {
		SubordinateTarget target1 = new MyTarget();
		SubordinateTarget target2 = new MyTarget();
		SubordinateTarget target3 = new MyTarget();

		WComponentGroup<SubordinateTarget> group = new WComponentGroup<>();
		group.addToGroup(target1);
		group.addToGroup(target2);
		group.addToGroup(target3);

		ShowInGroup action = new ShowInGroup(target2, group);
		Assert.assertEquals("Incorrect toString for action",
				"show MyTarget in WComponentGroup([MyTarget, MyTarget, MyTarget])", action.
				toString());

		new WLabel("test label", target2);
		Assert.assertEquals("Incorrect toString for action with a label",
				"show test label in WComponentGroup([MyTarget, MyTarget, MyTarget])", action.
				toString());
	}

	/**
	 * Test component that implements the SubordinateTarget interface.
	 */
	private static class MyTarget extends AbstractWComponent implements SubordinateTarget {
	}
}
