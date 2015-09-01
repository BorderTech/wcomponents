package com.github.dibp.wcomponents.examples.theme;

import com.github.dibp.wcomponents.WContainer;
import com.github.dibp.wcomponents.WEmailField;
import com.github.dibp.wcomponents.WFieldLayout;

public class WEmailFieldExample extends WContainer
{

    private WEmailField eField;
    
    public WEmailFieldExample()
    {

        WFieldLayout layout = new WFieldLayout();
        layout.setLabelWidth(25);
        add(layout);
        

        layout.addField("Plain email address", new WEmailField());
        
        eField = new WEmailField();
        eField.setDisabled(true);
        layout.addField("Disabled email address field", eField);
        
        eField = new WEmailField();
        eField.setReadOnly(true);
        layout.addField("Read-only email address field", eField);
        
        eField = new WEmailField();
        eField.setText("user@example.com");
        layout.addField("email address field with data", eField);
        
        eField = new WEmailField();
        eField.setText("user@example.com");
        eField.setDisabled(true);
        layout.addField("Disabled email address field with data", eField);
        
        eField = new WEmailField();
        eField.setText("user@example.com");
        eField.setReadOnly(true);
        layout.addField("Read-only email address field with data", eField);
    }

}
