package com.github.openborders.examples;

import com.github.openborders.Action;
import com.github.openborders.ActionEvent;
import com.github.openborders.WButton;
import com.github.openborders.WContainer;
import com.github.openborders.WDropdown;
import com.github.openborders.WFieldLayout;
import com.github.openborders.WLabel;
import com.github.openborders.WTextField;

/**
 * An example used to verify that WDropdown handles special characters correctly.
 * 
 * @author Martin Shevchnko
 * @since 1.0.0
 * @author Mark Reeves
 * @since 1.0.0
 */
public class WDropdownSpecialCharHandlingExample extends WContainer
{
    /** The dropdown used in this example. */
    private final WDropdown drop = new WDropdown();
    
    /** The text field used to display the selected option from the dropdown. */
    private final WTextField text;
    
    /**
     * Creates a WDropdownSpecialCharHandlingExample.
     */
    public WDropdownSpecialCharHandlingExample()
    {
        drop.setOptions(new String[] {null, ">", "<", "&", "\"", "normal"});

        WFieldLayout flay = new WFieldLayout();
        flay.setLabelWidth(25);
        add(flay);
        flay.addField("Select an option", drop);
        text = new WTextField();
        text.setDisabled(true);
        flay.addField("Selected option output", text);
        
        WButton submit = new WButton("Submit");
        flay.addField((WLabel) null, submit);
        
        submit.setAction(new Action() 
        {
            public void execute(final ActionEvent event)
            {
                String selected = (String) drop.getSelected();
                text.setText(selected);
            }
        });
    }
}
