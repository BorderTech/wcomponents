package com.github.openborders.examples; 

import com.github.openborders.Action;
import com.github.openborders.ActionEvent;
import com.github.openborders.WButton;
import com.github.openborders.WPanel;
import com.github.openborders.WPopup;
import com.github.openborders.layout.FlowLayout;
import com.github.openborders.layout.FlowLayout.Alignment;

/** 
 * An example showing {@link WPopup} usage. 
 * 
 * @author Yiannis Paschalidis 
 * @since 1.0.0
 */
public class WPopupExample extends WPanel
{
    /** Creates a WPopupExample. */
    public WPopupExample()
    {
        final WPopup popup = new WPopup("http://www.ubuntu.com/");
        popup.setResizable(true);
        popup.setScrollable(true);

        WButton button1 = new WButton("Popup Immi website");
        button1.setAction(new Action()
        {
            public void execute(final ActionEvent event)
            {
                popup.setVisible(true);
            }
        });
        
        WButton button2 = new WButton("Refresh page");
        
        setLayout(new FlowLayout(Alignment.VERTICAL));
        add(button1);
        add(button2);
        add(popup);
    }
}
