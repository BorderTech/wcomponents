package com.github.openborders.examples.theme;

import com.github.openborders.WContainer;
import com.github.openborders.WHeading;
import com.github.openborders.WMenu;

import com.github.openborders.examples.menu.ColumnMenuExample;
import com.github.openborders.examples.menu.MenuBarExample;
import com.github.openborders.examples.menu.TreeMenuExample;

/**
 * This component demonstrates the usage of the {@link WMenu} component.
 * 
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WMenuExample extends WContainer
{
    /**
     * Creates a WMenu example.
     */
    public WMenuExample()
    {
        add(new WHeading(WHeading.MAJOR, "Menu bar"));
        add(new MenuBarExample());

        add(new WHeading(WHeading.MAJOR, "Tree menu"));
        add(new TreeMenuExample());
        
        add(new WHeading(WHeading.MAJOR, "Column  menu"));
        add(new ColumnMenuExample());
    }
}
