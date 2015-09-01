package com.github.dibp.wcomponents.examples.theme;

import com.github.dibp.wcomponents.WContainer;
import com.github.dibp.wcomponents.WFieldLayout;
import com.github.dibp.wcomponents.WPhoneNumberField;

import com.github.dibp.wcomponents.examples.common.ExplanatoryText;

public class WPhoneNumberFieldExample extends WContainer
{

    private WPhoneNumberField field;
    
    public WPhoneNumberFieldExample()
    {
        WFieldLayout layout = new WFieldLayout();
        layout.setLabelWidth(25);
        add(layout);
        
        layout.addField("Plain phone number", new WPhoneNumberField());
        
        field = new WPhoneNumberField();
        field.setDisabled(true);
        layout.addField("Disabled phone number field", field);
        
        field = new WPhoneNumberField();
        field.setReadOnly(true);
        layout.addField("Read-only phone number field", field);
        
        field = new WPhoneNumberField();
        field.setText("+61.99999999");
        layout.addField("Phone number field with data", field);
        
        field = new WPhoneNumberField();
        field.setText("+61.99999999");
        field.setDisabled(true);
        layout.addField("Disabled phone number field with data", field);
        
        field = new WPhoneNumberField();
        field.setText("+61.99999999");
        field.setReadOnly(true);
        layout.addField("Read-only phone number field with data", field);
        
        add (new ExplanatoryText("You will notice that a WPhoneNumberFIeld when read only outputs a link with a protocol of tel." + 
        " This will signal to the browser to launch a soft phone system if available."));
    }

}
