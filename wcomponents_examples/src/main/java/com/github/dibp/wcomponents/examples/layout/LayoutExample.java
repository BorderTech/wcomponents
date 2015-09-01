package com.github.dibp.wcomponents.examples.layout;

import com.github.dibp.wcomponents.WPanel;
import com.github.dibp.wcomponents.WTabSet;
import com.github.dibp.wcomponents.WTabSet.TabMode;
import com.github.dibp.wcomponents.WTabSet.TabSetType;
import com.github.dibp.wcomponents.layout.BorderLayout;
import com.github.dibp.wcomponents.layout.ColumnLayout;
import com.github.dibp.wcomponents.layout.FlowLayout;
import com.github.dibp.wcomponents.layout.GridLayout;

/**
 * This example demonstrates all of the standard layouts.
 * 
 * @see GridLayout
 * @see BorderLayout
 * @see FlowLayout
 * @see ColumnLayout
 * 
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class LayoutExample extends WPanel
{
    /** Creates a LayoutExample. */
    public LayoutExample()
    {
        WTabSet tabs = new WTabSet(TabSetType.TOP);
        add(tabs);
        
        tabs.addTab(new ColumnLayoutExample(), "Column layout", TabMode.CLIENT);
        tabs.addTab(new BorderLayoutExample(), "Border layout", TabMode.CLIENT);
        tabs.addTab(new FlowLayoutExample(), "Flow layout", TabMode.CLIENT);
        tabs.addTab(new GridLayoutExample(), "Grid layout", TabMode.CLIENT);
    }
}
