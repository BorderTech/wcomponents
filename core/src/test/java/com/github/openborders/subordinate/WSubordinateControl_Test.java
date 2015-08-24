package com.github.openborders.subordinate;

import junit.framework.Assert;

import org.junit.Test;

import com.github.openborders.AbstractWComponentTestCase;
import com.github.openborders.ComponentModel;
import com.github.openborders.SubordinateTarget;
import com.github.openborders.WCheckBox;
import com.github.openborders.WContainer;
import com.github.openborders.WTextField;
import com.github.openborders.subordinate.Equal;
import com.github.openborders.subordinate.Hide;
import com.github.openborders.subordinate.Rule;
import com.github.openborders.subordinate.Show;
import com.github.openborders.subordinate.WSubordinateControl;
import com.github.openborders.util.mock.MockRequest;

/**
 * Test cases for {@link WSubordinateControl}.
 * 
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class WSubordinateControl_Test extends AbstractWComponentTestCase
{
    @Test
    public void testRulesAccessors()
    {
        Rule rule1 = new Rule();

        WSubordinateControl control = new WSubordinateControl();

        // Controls
        Assert.assertTrue("Controls should be empty", control.getRules().isEmpty());
        control.addRule(rule1);
        Assert.assertEquals("Controls list should have 1 item", 1, control.getRules().size());
        Assert.assertEquals("Item 1 in Controls list is incorrect", rule1, control.getRules().get(0));
        try
        {
            control.addRule(null);
            Assert.fail("Should not be able to add a null control.");
        }
        catch (IllegalArgumentException e)
        {
            Assert.assertNotNull("Invalid exception message", e.getMessage());
        }

    }

    @Test
    public void testApplyControls()
    {
        WCheckBox box = new WCheckBox();
        SubordinateTarget target = new WTextField();

        // Create Rule
        Rule rule = new Rule();
        rule.setCondition(new Equal(box, Boolean.TRUE));
        rule.addActionOnTrue(new Hide(target));
        rule.addActionOnFalse(new Show(target));

        WSubordinateControl control = new WSubordinateControl();
        control.addRule(rule);

        WContainer root = new WContainer();
        root.add(control);
        root.add(box);
        root.add(target);

        setActiveContext(createUIContext());
        // Setup true condition - Selected
        box.setSelected(true);
        // Apply the controls
        setFlag(box, ComponentModel.HIDE_FLAG, false);
        control.applyTheControls();
        Assert.assertTrue("After applyControls target should be hidden", target.isHidden());

        setFlag(box, ComponentModel.HIDE_FLAG, false);
        // Setup true condition - CheckBox selected in Request
        MockRequest request = new MockRequest();
        setupCheckBoxRequest(box, request, true);
        // ApplyControls with Request
        control.applyTheControls(request);
        Assert.assertTrue("Request - After applyControls target should be hidden", target.isHidden());
    }

    @Test
    public void testToString()
    {
        WCheckBox box = new WCheckBox();
        SubordinateTarget target = new WTextField();

        Rule rule = new Rule();
        rule.setCondition(new Equal(box, Boolean.TRUE));
        rule.addActionOnTrue(new Hide(target));
        rule.addActionOnFalse(new Show(target));

        WSubordinateControl control = new WSubordinateControl();
        control.addRule(rule);

        Assert.assertEquals("Incorrect toString for control",
                            "RULE: if (WCheckBox=\"true\")\nthen\n   [hide WTextField]\nelse\n   [show WTextField]\n",
                            control.toString());

    }

    private void setupCheckBoxRequest(final WCheckBox target, final MockRequest request, final boolean condition)
    {
        if (condition)
        {
            request.setParameter(target.getId(), "true");
        }
    }
}
