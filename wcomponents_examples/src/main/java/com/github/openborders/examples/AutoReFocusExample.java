package com.github.openborders.examples;

import com.github.openborders.WPanel;
import com.github.openborders.layout.FlowLayout;
import com.github.openborders.layout.FlowLayout.Alignment;

/**
 * This example demonstrates that focus will be returned to the control 
 * which triggered the form submit after a round trip to the server completes.
 * 
 * @author Martin Shevchenko
 * @since 1.0.0
 */
public class AutoReFocusExample extends WPanel
{
    /**
     * Creates an AutoReFocusExample.
     */
    public AutoReFocusExample()
    {
        setLayout(new FlowLayout(Alignment.VERTICAL));
        
        add(new TextDuplicator());
        add(new WRadioButtonTriggerActionExample());
        add(new WDropdownSubmitOnChangeExample());
        add(new WDropdownTriggerActionExample());
    }
}
