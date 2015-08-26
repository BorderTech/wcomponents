package com.github.openborders.wcomponents.subordinate;

import junit.framework.Assert;

import org.junit.Test;

import com.github.openborders.wcomponents.AbstractWComponent;
import com.github.openborders.wcomponents.AbstractWComponentTestCase;
import com.github.openborders.wcomponents.ComponentModel;
import com.github.openborders.wcomponents.Disableable;
import com.github.openborders.wcomponents.SubordinateTarget;
import com.github.openborders.wcomponents.WComponentGroup;
import com.github.openborders.wcomponents.WLabel;
import com.github.openborders.wcomponents.WTextField;

/**
 * Test cases for {@link EnableInGroup}.
 * 
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class EnableInGroup_Test extends AbstractWComponentTestCase
{
    @Test
    public void testConstructor()
    {
        SubordinateTarget target = new MyTarget();
        WComponentGroup<SubordinateTarget> group = new WComponentGroup<SubordinateTarget>();
        EnableInGroup action = new EnableInGroup(target, group);

        Assert.assertEquals("Value for EnableIn should be false", Boolean.FALSE, action.getValue());
        Assert.assertEquals("Target for EnableIn should be the group", group, action.getTarget());
        Assert.assertEquals("TargetInGroup for EnableIn should be the target", target, action.getTargetInGroup());
    }

    @Test
    public void testEnableInWComponentGroup()
    {
        MyTarget target1 = new MyTarget();
        MyTarget target2 = new MyTarget();
        MyTarget target3 = new MyTarget();
        WComponentGroup<SubordinateTarget> group = new WComponentGroup<SubordinateTarget>();
        group.addToGroup(target1);
        group.addToGroup(target2);
        group.addToGroup(target3);
        EnableInGroup action = new EnableInGroup(target2, group);

        // Check all enabled
        AssertTargetUtil.assertTargetsEnabled(target1, target2, target3);

        // Execute Action
        setActiveContext(createUIContext());
        action.execute();

        // Check only target2 is enabled
        Assert.assertTrue("Target1 should be disabled", target1.isDisabled());
        Assert.assertFalse("Target2 should be enabled", target2.isDisabled());
        Assert.assertTrue("Target3 should be disabled", target3.isDisabled());
    }

    @Test
    public void testActionType()
    {
        SubordinateTarget target = new WTextField();
        WComponentGroup<SubordinateTarget> group = new WComponentGroup<SubordinateTarget>();
        group.addToGroup(target);
        EnableInGroup action = new EnableInGroup(target, group);
        Assert.assertEquals("Incorrect Action Type", action.getActionType(), AbstractAction.ActionType.ENABLEIN);
    }

    @Test
    public void testToString()
    {
        SubordinateTarget target1 = new MyTarget();
        SubordinateTarget target2 = new MyTarget();
        SubordinateTarget target3 = new MyTarget();

        WComponentGroup<SubordinateTarget> group = new WComponentGroup<SubordinateTarget>();
        group.addToGroup(target1);
        group.addToGroup(target2);
        group.addToGroup(target3);

        EnableInGroup action = new EnableInGroup(target2, group);
        Assert.assertEquals("Incorrect toString for action",
                            "enable MyTarget in WComponentGroup([MyTarget, MyTarget, MyTarget])", action.toString());

        new WLabel("test label", target2);
        Assert.assertEquals("Incorrect toString for action with a label",
                            "enable test label in WComponentGroup([MyTarget, MyTarget, MyTarget])", action.toString());
    }

    /**
     * Test component that implements the SubordinateTarget interface.
     */
    private static class MyTarget extends AbstractWComponent implements SubordinateTarget, Disableable
    {
        /**
         * {@inheritDoc}
         */
        public boolean isDisabled()
        {
            return isFlagSet(ComponentModel.DISABLED_FLAG);
        }

        /**
         * {@inheritDoc}
         */
        public void setDisabled(final boolean disabled)
        {
            setFlag(ComponentModel.DISABLED_FLAG, disabled);
        }
    }
}
