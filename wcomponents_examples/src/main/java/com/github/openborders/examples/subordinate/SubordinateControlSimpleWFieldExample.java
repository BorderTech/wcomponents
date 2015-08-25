package com.github.openborders.examples.subordinate;

import com.github.openborders.Margin;
import com.github.openborders.RadioButtonGroup;
import com.github.openborders.WContainer;
import com.github.openborders.WField;
import com.github.openborders.WFieldLayout;
import com.github.openborders.WRadioButtonSelect;
import com.github.openborders.WTextField;
import com.github.openborders.subordinate.Equal;
import com.github.openborders.subordinate.Hide;
import com.github.openborders.subordinate.Rule;
import com.github.openborders.subordinate.Show;
import com.github.openborders.subordinate.WSubordinateControl;

/**
 * This example demonstrates showing/hiding an extra field depending on
 * the selection in a {@link RadioButtonGroup}.
 *  
 * @author Martin Shevchenko 
 * @since 1.0.0
 */
public class SubordinateControlSimpleWFieldExample extends WContainer
{
    private static final String YES_OPTION = "yes";
    private static final String NO_OPTION = "no";
    
    /**
     * Creates a SubordinateControlSimpleWFieldExample.
     */
    public SubordinateControlSimpleWFieldExample()
    {
        WRadioButtonSelect extraGroup = new WRadioButtonSelect(new String[]{YES_OPTION, NO_OPTION});
        extraGroup.setFrameless(true);
        extraGroup.setButtonLayout(WRadioButtonSelect.LAYOUT_FLAT);
        WFieldLayout layout = new WFieldLayout();
        layout.setLabelWidth(25);
        layout.setMargin(new Margin(0, 0, 12, 0));
        add(layout);
        layout.addField("Extra info required", extraGroup);
        WField extraField = layout.addField("Extra Text", new WTextField()); 
        
        // Subordinate
        WSubordinateControl control = new WSubordinateControl();
        add(control);
        
        Rule rule = new Rule();
        rule.setCondition(new Equal(extraGroup, YES_OPTION));
        rule.addActionOnTrue(new Show(extraField));
        rule.addActionOnFalse(new Hide(extraField));
        control.addRule(rule);
    }
}
