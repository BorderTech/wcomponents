package com.github.openborders.examples.subordinate;

import com.github.openborders.Margin;
import com.github.openborders.WContainer;
import com.github.openborders.WDropdown;
import com.github.openborders.WField;
import com.github.openborders.WFieldLayout;
import com.github.openborders.WTextField;
import com.github.openborders.subordinate.Equal;
import com.github.openborders.subordinate.Hide;
import com.github.openborders.subordinate.Rule;
import com.github.openborders.subordinate.Show;
import com.github.openborders.subordinate.WSubordinateControl;

import com.github.openborders.examples.common.ExampleLookupTable.TableWithNullOption;

/**
 * <p>An example showing using a {@link WSubordinateControl} to hide/show a field 
 * depending on the selected value in a {@link WDropdown}.</p>
 * 
 * <p>Three rules have been created:
 * <ol>
 *   <li>Hide the "extra" field when no Gender is selected</li>
 *   <li>Show the "extra" field when "M" is selected</li>
 *   <li>Hide the "extra" field when "F" is selected</li>
 * </ol>
 * </p>
 *   
 * 
 * @author Martin Shevchenko
 * @since 1.0.0
 */
public class SubordinateControlCrtWDropdownExample extends WContainer
{
    /** Creates a SubordinateControlCrtWDropdownExample. */
    public SubordinateControlCrtWDropdownExample()
    {
        WFieldLayout layout = new WFieldLayout();
        add(layout);
        layout.setLabelWidth(25);
        layout.setMargin(new Margin(0, 0, 12, 0));
        
        WDropdown genderDropdown = new WDropdown(new TableWithNullOption("sex"));
        
        WField field = layout.addField("Gender", genderDropdown);
        field.getLabel().setHint("The 'Male' option requires more information");

        WTextField extraField = new WTextField();
        WField xtraWField = layout.addField("Extra information", extraField);
        
        WSubordinateControl control = new WSubordinateControl();
        add(control);
        
        Rule rule = new Rule();
        rule.setCondition(new Equal(genderDropdown, "M"));
        rule.addActionOnTrue(new Show(xtraWField));
        rule.addActionOnFalse(new Hide(xtraWField));
        control.addRule(rule);
    }
}
