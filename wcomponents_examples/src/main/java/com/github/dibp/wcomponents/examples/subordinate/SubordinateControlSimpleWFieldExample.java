package com.github.dibp.wcomponents.examples.subordinate;

import com.github.dibp.wcomponents.Margin;
import com.github.dibp.wcomponents.RadioButtonGroup;
import com.github.dibp.wcomponents.WContainer;
import com.github.dibp.wcomponents.WField;
import com.github.dibp.wcomponents.WFieldLayout;
import com.github.dibp.wcomponents.WRadioButtonSelect;
import com.github.dibp.wcomponents.WTextField;
import com.github.dibp.wcomponents.subordinate.Equal;
import com.github.dibp.wcomponents.subordinate.Hide;
import com.github.dibp.wcomponents.subordinate.Rule;
import com.github.dibp.wcomponents.subordinate.Show;
import com.github.dibp.wcomponents.subordinate.WSubordinateControl;

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
