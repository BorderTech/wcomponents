package com.github.openborders.examples.theme;

import com.github.openborders.Action;
import com.github.openborders.ActionEvent;
import com.github.openborders.WMenu;
import com.github.openborders.WMenuItem;
import com.github.openborders.WPanel;
import com.github.openborders.WSubMenu;
import com.github.openborders.WText;
import com.github.openborders.layout.FlowLayout;
import com.github.openborders.layout.FlowLayout.Alignment;

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
