package com.github.openborders.wcomponents.examples;

import com.github.openborders.wcomponents.WPanel;
import com.github.openborders.wcomponents.layout.FlowLayout;
import com.github.openborders.wcomponents.layout.FlowLayout.Alignment;

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
