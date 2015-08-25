package com.github.openborders.examples; 

import com.github.openborders.Request;
import com.github.openborders.WCheckBox;
import com.github.openborders.WContainer;
import com.github.openborders.WLabel;
import com.github.openborders.WPanel;
import com.github.openborders.layout.FlowLayout;
import com.github.openborders.layout.FlowLayout.Alignment;

/**
 * An example of the {@link WCheckBox} component.
 * 
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class CheckBoxExample extends WPanel
{
    private final WCheckBox rb1;
    private final WCheckBox rb2;
    private final WCheckBox rb3;

    /**
     * Creates a CheckBoxExample.
     */
    public CheckBoxExample()
    {
        this.setLayout(new FlowLayout(Alignment.VERTICAL));

        WContainer panel = new WContainer();
        rb1 = new WCheckBox();
        panel.add(new WLabel("Default", rb1));
        panel.add(rb1);
        this.add(panel);
        
        panel = new WContainer();
        rb2 = new WCheckBox();
        panel.add(new WLabel("Initially selected", rb2));
        panel.add(rb2);
        this.add(panel);

        panel = new WContainer();
        rb3 = new WCheckBox();
        rb3.setDisabled(true);
        panel.add(new WLabel("Disabled", rb3));
        panel.add(rb3);
        this.add(panel);
    }

    /**
     * Override preparePaintComponent to test that dynamic attributes are handled correctly.
     * 
     * @param request the request that triggered the paint. 
     */
    @Override
    protected void preparePaintComponent(final Request request)
    {
        super.preparePaintComponent(request);
        
        if (!isInitialised())
        {
            rb2.setSelected(true);
            rb3.setToolTip("This is disabled.");
            
            setInitialised(true);
        }
    }
}
