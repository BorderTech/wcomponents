package com.github.bordertech.wcomponents.subordinate;

import com.github.bordertech.wcomponents.AbstractWComponent;
import com.github.bordertech.wcomponents.AbstractWComponentTestCase;
import com.github.bordertech.wcomponents.SubordinateTarget;
import com.github.bordertech.wcomponents.WComponentGroup;
import com.github.bordertech.wcomponents.WLabel;
import com.github.bordertech.wcomponents.WTextField;
import junit.framework.Assert;
import org.junit.Test;

/**
 * Test cases for {@link HideInGroup}.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class HideInGroup_Test extends AbstractWComponentTestCase {

	@Test
	public void testConstructor() {
		SubordinateTarget target = new MyTarget();
		WComponentGroup<SubordinateTarget> group = new WComponentGroup<>();
		HideInGroup action = new HideInGroup(target, group);

		Assert.assertEquals("Value for HideIn should be true", Boolean.TRUE, action.getValue());
		Assert.assertEquals("Target for HideIn should be the group", group, action.getTarget());
		Assert.assertEquals("TargetInGroup for HideIn should be the target", target, action.
				getTargetInGroup());
	}

	@Test
	public void testHideInWComponentGroup() {
		SubordinateTarget target1 = new MyTarget();
		SubordinateTarget target2 = new MyTarget();
		SubordinateTarget target3 = new MyTarget();
		WComponentGroup<SubordinateTarget> group = new WComponentGroup<>();
		group.addToGroup(target1);
		group.addToGroup(target2);
		group.addToGroup(target3);
		HideInGroup action = new HideInGroup(target2, group);

		// Check all visible and not hidden
		AssertTargetUtil.assertTargetsNotHidden(target1, target2, target3);
		AssertTargetUtil.assertTargetsVisible(target1, target2, target3);

		// Execute Action
		setActiveContext(createUIContext());
		action.execute();

		// Check only target2 is hidden
		Assert.assertFalse("Target1 should not be hidden", target1.isHidden());
		Assert.assertTrue("Target2 should be hidden", target2.isHidden());
		Assert.assertFalse("Target3 should not be hidden", target3.isHidden());
		// All Visible
		AssertTargetUtil.assertTargetsVisible(target1, target2, target3);
	}

	@Test
	public void testActionType() {
		SubordinateTarget target = new WTextField();
		WComponentGroup<SubordinateTarget> group = new WComponentGroup<>();
		group.addToGroup(target);
		HideInGroup action = new HideInGroup(target, group);
		Assert.assertEquals("Incorrect Action Type", action.getActionType(),
				AbstractAction.ActionType.HIDEIN);
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

		HideInGroup action = new HideInGroup(target2, group);
		Assert.assertEquals("Incorrect toString for action",
				"hide MyTarget in WComponentGroup([MyTarget, MyTarget, MyTarget])", action.
				toString());

		new WLabel("test label", target2);
		Assert.assertEquals("Incorrect toString for action with a label",
				"hide test label in WComponentGroup([MyTarget, MyTarget, MyTarget])", action.
				toString());
	}

	/**
	 * Test component that implements the SubordinateTarget interface.
	 */
	private static class MyTarget extends AbstractWComponent implements SubordinateTarget {
	}
}
