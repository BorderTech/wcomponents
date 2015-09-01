package com.github.dibp.wcomponents.examples.theme;

import com.github.dibp.wcomponents.WContainer;
import com.github.dibp.wcomponents.WHeading;
import com.github.dibp.wcomponents.WMenu;

import com.github.dibp.wcomponents.examples.menu.ColumnMenuExample;
import com.github.dibp.wcomponents.examples.menu.MenuBarExample;
import com.github.dibp.wcomponents.examples.menu.TreeMenuExample;

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
