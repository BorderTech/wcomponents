package com.github.dibp.wcomponents.examples.subordinate;

import com.github.dibp.wcomponents.WButton;
import com.github.dibp.wcomponents.WCheckBox;
import com.github.dibp.wcomponents.WCheckBoxSelect;
import com.github.dibp.wcomponents.WContainer;
import com.github.dibp.wcomponents.WDropdown;
import com.github.dibp.wcomponents.WFieldLayout;
import com.github.dibp.wcomponents.WLabel;
import com.github.dibp.wcomponents.WLink;
import com.github.dibp.wcomponents.WPanel;
import com.github.dibp.wcomponents.WRadioButtonSelect;
import com.github.dibp.wcomponents.WTextArea;
import com.github.dibp.wcomponents.WTextField;
import com.github.dibp.wcomponents.layout.FlowLayout;
import com.github.dibp.wcomponents.layout.FlowLayout.Alignment;
import com.github.dibp.wcomponents.subordinate.Disable;
import com.github.dibp.wcomponents.subordinate.Enable;
import com.github.dibp.wcomponents.subordinate.Equal;
import com.github.dibp.wcomponents.subordinate.Rule;
import com.github.dibp.wcomponents.subordinate.WSubordinateControl;

/**
 * A simple example of SubordinateControl being used to disable fields.
 * 
 * @author Martin Shevchenko
 * @since 1.0.0
 */
public class SubordinateControlSimpleDisableExample extends WPanel
{
    /**
     * Creates a SubordinateControlSimpleDisableExample.
     */
    public SubordinateControlSimpleDisableExample()
    {
        WFieldLayout layout = new WFieldLayout();
        layout.setLabelWidth(25);
        layout.setMargin(new com.github.dibp.wcomponents.Margin(0, 0, 12, 0));
        add(layout);
        
        // The toggle used to enable/disable the other fields.
        WCheckBox toggle = new WCheckBox();
        layout.addField("Toggle disabled", toggle);
        
        // Various input fields that will be enabled/disabled.
        WTextField textField = new WTextField();
        layout.addField("Text field", textField);
        WDropdown dropdownField = new WDropdown(new String[] { "a", "b", "c" });
        layout.addField("Drop down", dropdownField);
        WCheckBox checkboxField = new WCheckBox();
        layout.addField("Checkbox", checkboxField);
        WRadioButtonSelect rbSelect = new WRadioButtonSelect(new String[] { "a", "b", "c" });
        rbSelect.setFrameless(true);
        rbSelect.setButtonLayout(WRadioButtonSelect.LAYOUT_FLAT);
        layout.addField("Select one", rbSelect);
        WCheckBoxSelect cbSelect = new WCheckBoxSelect(new String[] { "a", "b", "c" });
        cbSelect.setFrameless(true);
        rbSelect.setButtonLayout(WRadioButtonSelect.LAYOUT_FLAT);
        layout.addField("Select any", cbSelect);
        WTextArea textArea = new WTextArea();
        textArea.setRows(3);
        layout.addField("Text Area", textArea).setInputWidth(100);
        WButton button = new WButton("Button");
        layout.addField((WLabel) null, button);
        WLink link = new WLink();
        link.setText("Link");
        link.setRenderAsButton(true);
        layout.addField((WLabel) null, link);
        
        // The subordnate control that will perform client side enabling/disabling.
        WSubordinateControl control = new WSubordinateControl();
        add(control);
        
        Rule rule = new Rule();
        rule.setCondition(new Equal(toggle, Boolean.TRUE.toString()));
        
        rule.addActionOnTrue(new Disable(textField));
        rule.addActionOnTrue(new Disable(dropdownField));
        rule.addActionOnTrue(new Disable(checkboxField));
        rule.addActionOnTrue(new Disable(rbSelect));
        rule.addActionOnTrue(new Disable(cbSelect));
        rule.addActionOnTrue(new Disable(textArea));
        rule.addActionOnTrue(new Disable(button));
        rule.addActionOnTrue(new Disable(link));
        
        rule.addActionOnFalse(new Enable(textField));
        rule.addActionOnFalse(new Enable(dropdownField));
        rule.addActionOnFalse(new Enable(checkboxField));
        rule.addActionOnFalse(new Enable(rbSelect));
        rule.addActionOnFalse(new Enable(cbSelect));
        rule.addActionOnFalse(new Enable(textArea));
        rule.addActionOnFalse(new Enable(button));
        rule.addActionOnFalse(new Enable(link));
        control.addRule(rule);
    }
}
