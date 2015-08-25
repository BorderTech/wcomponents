package com.github.openborders.examples.subordinate;

import com.github.openborders.SubordinateTarget;
import com.github.openborders.WButton;
import com.github.openborders.WComponentGroup;
import com.github.openborders.WDropdown;
import com.github.openborders.WField;
import com.github.openborders.WFieldLayout;
import com.github.openborders.WHorizontalRule;
import com.github.openborders.WPanel;
import com.github.openborders.WTextField;
import com.github.openborders.layout.FlowLayout;
import com.github.openborders.layout.FlowLayout.Alignment;
import com.github.openborders.subordinate.Equal;
import com.github.openborders.subordinate.Hide;
import com.github.openborders.subordinate.Rule;
import com.github.openborders.subordinate.Show;
import com.github.openborders.subordinate.ShowInGroup;
import com.github.openborders.subordinate.WSubordinateControl;

/**
 * This example demonstrates a SubordinateControl that combines
 * WDropdown, Groups, and FieldLayout with functions Show, Hide, and ShowInGroup.
 * 
 * @author Martin Shevchenko
 * @since 1.0.0
 */
public class SubordinateControlGroupExample extends WPanel
{
    private static final String SHOW_FIRST = "Show First";
    private static final String SHOW_ALL = "Show All";
    private static final String HIDE_ALL = "Hide All";
    private static final String HIDE_LAYOUT = "Hide L1 and L2";
    
    /**
     * Creates a SubordinateControlGroupExample.
     */
    public SubordinateControlGroupExample()
    {
        WFieldLayout layout = new WFieldLayout();
        add(layout);
        layout.setLabelWidth(25);
        layout.setMargin(new com.github.openborders.Margin(0, 0, 12, 0));
        
        WDropdown select = new WDropdown();
        select.setOptions(new String[] {SHOW_FIRST, SHOW_ALL, HIDE_ALL, HIDE_LAYOUT});
        layout.addField("Select the fields to show", select);
        
        WField field1 = layout.addField("Simple text field", new WTextField());
        WField field2 = layout.addField("A different text field", new WTextField());
        
        WField fieldL1 = layout.addField("L1", new WTextField());
        WField fieldL2 = layout.addField("L2", new WTextField());
        
        WSubordinateControl control = new WSubordinateControl();
        add(control);

        WComponentGroup<SubordinateTarget> group = new WComponentGroup<SubordinateTarget>();
        group.addToGroup(field1);
        group.addToGroup(field2);
        add(group);

        WComponentGroup<SubordinateTarget> group2 = new WComponentGroup<SubordinateTarget>();
        group2.addToGroup(fieldL1);
        group2.addToGroup(fieldL2);
        add(group2);
        
        Rule rule = new Rule();
        rule.setCondition(new Equal(select, SHOW_FIRST));
        rule.addActionOnTrue(new ShowInGroup(field1, group));
        rule.addActionOnFalse(new Show(field1));
        rule.addActionOnTrue(new Hide(group2));
        control.addRule(rule);
        
        rule = new Rule();
        rule.setCondition(new Equal(select, SHOW_ALL));
        rule.addActionOnTrue(new Show(group));
        rule.addActionOnTrue(new Show(group2));
        control.addRule(rule);
        
        rule = new Rule();
        rule.setCondition(new Equal(select, HIDE_ALL));
        rule.addActionOnTrue(new Hide(group));
        rule.addActionOnTrue(new Hide(group2));
        control.addRule(rule);
        
        rule = new Rule();
        rule.setCondition(new Equal(select, HIDE_LAYOUT));
        rule.addActionOnTrue(new Show(group));
        rule.addActionOnTrue(new Hide(group2));
        control.addRule(rule);
    }
}
