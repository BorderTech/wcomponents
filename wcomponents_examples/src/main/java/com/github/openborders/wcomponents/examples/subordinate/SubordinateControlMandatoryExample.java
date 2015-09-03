package com.github.openborders.wcomponents.examples.subordinate;

import com.github.openborders.wcomponents.WCheckBox;
import com.github.openborders.wcomponents.WComponent;
import com.github.openborders.wcomponents.WContainer;
import com.github.openborders.wcomponents.WFieldLayout;
import com.github.openborders.wcomponents.WTextField;
import com.github.openborders.wcomponents.subordinate.Equal;
import com.github.openborders.wcomponents.subordinate.Mandatory;
import com.github.openborders.wcomponents.subordinate.Optional;
import com.github.openborders.wcomponents.subordinate.Rule;
import com.github.openborders.wcomponents.subordinate.WSubordinateControl;

import com.github.openborders.wcomponents.examples.validation.ValidationContainer;

/**
 * Demonstrate using the Mandatory and Optional actions on the Subordinate Control.
 * 
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class SubordinateControlMandatoryExample extends ValidationContainer
{
    /** Creates a SubordinateControlMandatoryExample. */
    public SubordinateControlMandatoryExample()
    {
        super(build());
    }

    /**
     * Creates the component to be added to the validation container.
     * This is doen in a static method because the component is passed into the superclass constructor. 
     * @return the component to be added to the validation container.
     */
    private static WComponent build()
    {
        WContainer root = new WContainer();

        WSubordinateControl control = new WSubordinateControl();
        root.add(control);

        WFieldLayout layout = new WFieldLayout();
        layout.setLabelWidth(25);
        layout.setMargin(new com.github.openborders.wcomponents.Margin(0, 0, 12, 0));
        WCheckBox checkBox = new WCheckBox();
        layout.addField("Set Mandatory", checkBox);

        WTextField text = new WTextField();
        layout.addField("Might need this field", text);

        WTextField mandatoryField = new WTextField();
        layout.addField("Another field always mandatory",mandatoryField);
        mandatoryField.setMandatory(true);

        root.add(layout);

        Rule rule = new Rule();
        rule.setCondition(new Equal(checkBox, Boolean.TRUE.toString()));
        rule.addActionOnTrue(new Mandatory(text));
        rule.addActionOnFalse(new Optional(text));
        control.addRule(rule);

        return root;
    }
}
