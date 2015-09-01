package com.github.dibp.wcomponents.subordinate;

import junit.framework.Assert;

import org.junit.Test;

import com.github.dibp.wcomponents.AbstractWComponent;
import com.github.dibp.wcomponents.AbstractWComponentTestCase;
import com.github.dibp.wcomponents.SubordinateTarget;
import com.github.dibp.wcomponents.WLabel;

/**
 * Test cases for {@link Show}.
 * 
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class Show_Test extends AbstractWComponentTestCase
{
    @Test
    public void testConstructor()
    {
        SubordinateTarget target = new MyTarget();
        Show action = new Show(target);

        Assert.assertEquals("Value for Show should be true", Boolean.TRUE, action.getValue());
        Assert.assertEquals("Target for Show should be the target", target, action.getTarget());
    }

    @Test
    public void testActionType()
    {
        Show action = new Show(new MyTarget());
        Assert.assertEquals("Incorrect Action Type", AbstractAction.ActionType.SHOW, action.getActionType());
    }

    @Test
    public void testToString()
    {
        MyTarget target = new MyTarget();

        Show action = new Show(target);
        Assert.assertEquals("Incorrect toString for action", "show MyTarget", action.toString());

        WLabel label = new WLabel("test label", target);
        Assert.assertEquals("Incorrect toString for action with a label", "show " + label.getText(), action.toString());
    }

    /**
     * Test component that implements the SubordinateTarget interface.
     */
    private static class MyTarget extends AbstractWComponent implements SubordinateTarget
    {
    }
}
