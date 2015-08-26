package com.github.openborders.wcomponents.examples.theme;

import com.github.openborders.wcomponents.WCheckBox;
import com.github.openborders.wcomponents.WContainer;
import com.github.openborders.wcomponents.WFieldLayout;
/**
 * Shows the various properties of WCheckBox.
 *
 * @author Mark Reeves
 * @since 1.0.0
 */
public class WCheckBoxExample extends WContainer
{

    public WCheckBoxExample()
    {
        WCheckBox cb;
        
        WFieldLayout layout = new WFieldLayout(WFieldLayout.LAYOUT_STACKED);
        add(layout);
        layout.addField("Normal Check box", new WCheckBox());
        layout.addField("Checked Check box", new WCheckBox(true));
        
        cb = new WCheckBox();
        cb.setDisabled(true);
        layout.addField("Disabled check box", cb);
        
        cb = new WCheckBox(true);
        cb.setDisabled(true);
        layout.addField("Disabled checked check box", cb);

        cb = new WCheckBox();
        cb.setMandatory(true);
        layout.addField("Mandatory check box", cb);

        cb = new WCheckBox();
        cb.setReadOnly(true);
        layout.addField("Read only unchecked check box", cb);

        cb = new WCheckBox(true);
        cb.setReadOnly(true);
        layout.addField("Read only checked check box", cb);
        
    }

}
