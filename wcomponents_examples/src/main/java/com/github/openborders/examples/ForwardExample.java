package com.github.openborders.examples; 

import com.github.openborders.Action;
import com.github.openborders.ActionEvent;
import com.github.openborders.WButton;
import com.github.openborders.WContainer;
import com.github.openborders.WTextField;

/** 
 * This example demonstrates the use of the "forward" feature.
 * Enter a URL in the entry field and then press the Forward button to go to that url.
 * 
 * @author Martin Shevchenko
 * @since 14/02/2008
 */
public class ForwardExample extends WContainer
{
    /** Creates a ForwardExample. */
    public ForwardExample()
    {
        final WTextField urlField = new WTextField();
        urlField.setText("http://www.google.com.au/");
        urlField.setColumns(60);
        add(urlField, "urlField");
        
        WButton forwardBtn = new WButton("Forward");
        forwardBtn.setAction(new Action() 
        {
            public void execute(final ActionEvent event)
            {
                forward(urlField.getText());
            }
        });
        
        add(forwardBtn, "forwardBtn");
    }
}
