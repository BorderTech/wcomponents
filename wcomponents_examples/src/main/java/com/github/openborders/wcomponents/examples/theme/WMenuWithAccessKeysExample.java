package com.github.openborders.wcomponents.examples.theme;

import com.github.openborders.wcomponents.Action;
import com.github.openborders.wcomponents.ActionEvent;
import com.github.openborders.wcomponents.WMenu;
import com.github.openborders.wcomponents.WMenuItem;
import com.github.openborders.wcomponents.WPanel;
import com.github.openborders.wcomponents.WSubMenu;
import com.github.openborders.wcomponents.WText;
import com.github.openborders.wcomponents.layout.FlowLayout;
import com.github.openborders.wcomponents.layout.FlowLayout.Alignment;

/**
 * Example of a menu bar containing entries with access keys.
 * 
 * @author Martin Shevchenko
 * @since 1.0.0
 */
public class WMenuWithAccessKeysExample extends WPanel
{
    /**
     * Creates a WMenuWithAccessKeysExample.
     */
    public WMenuWithAccessKeysExample()
    {
        // Wire up actions so we can identify when the menu items are triggered.
        final WText console = new WText("No menu item has been activated.");
        
        Action reportAction = new Action() 
        {
            public void execute(final ActionEvent event)
            {
                String command = event.getActionCommand();
                console.setText("Activated: " + command);
            }
        };
        
        // Create the menu with various links
        WMenu bar = new WMenu();
        
        WSubMenu subMenu = new WSubMenu("Menu containing access keys");
        bar.add(subMenu);
        
        WMenuItem menuItem = new WMenuItem("My MenuItem", 'I', reportAction);
        menuItem.setActionCommand(menuItem.getText());
        subMenu.add(menuItem);
        
        menuItem = new WMenuItem("My Button", 'B', reportAction);
        menuItem.setActionCommand(menuItem.getText());
        subMenu.add(menuItem);
        
        menuItem = new WMenuItem("No access key item", reportAction);
        menuItem.setActionCommand(menuItem.getText());
        subMenu.add(menuItem);
        
        WMenuItem externalLink = new WMenuItem("My Google Link", "http://www.google.com.au");
        externalLink.setAccessKey('G');
        subMenu.add(externalLink);

        // Layout the UI 
        setLayout(new FlowLayout(Alignment.VERTICAL, 0, 5));
        add(bar);
        add(console);
    }
}
