package com.github.openborders.examples.repeater.link; 

import com.github.openborders.WDataRenderer;
import com.github.openborders.WFieldSet;
import com.github.openborders.WRepeater;

/** 
 * This component shows an example of nested repeaters.
 * 
 * @author Adam Millard
 */
public class NestedRepeaterTabComponent extends WDataRenderer
{
    /** The outer repeater. */
    private final WRepeater repeater = new WRepeater();    
    
    /**
     * Creates a NestedRepeaterTabComponent.
     */
    public NestedRepeaterTabComponent()
    {
        //setLayout(new FlowLayout(FlowLayout.VERTICAL));
        
        repeater.setRepeatedComponent(new RepeaterLinkTab());

        WFieldSet fieldset = new WFieldSet("Nested");
        add(fieldset);
        fieldset.add(repeater);
    }

    /** {@inheritDoc} */
    @Override
    public void setData(final Object data)
    {
        repeater.setData(data);
    }
}
