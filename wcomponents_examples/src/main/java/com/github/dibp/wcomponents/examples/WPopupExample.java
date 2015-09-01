package com.github.dibp.wcomponents.examples;

import com.github.dibp.wcomponents.Action;
import com.github.dibp.wcomponents.ActionEvent;
import com.github.dibp.wcomponents.WButton;
import com.github.dibp.wcomponents.WPanel;
import com.github.dibp.wcomponents.WPopup;
import com.github.dibp.wcomponents.layout.FlowLayout;
import com.github.dibp.wcomponents.layout.FlowLayout.Alignment;

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

        WButton button1 = new WButton("Popup Ubuntu website");
        button1.setAction(new Action()
        {
            @Override
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
