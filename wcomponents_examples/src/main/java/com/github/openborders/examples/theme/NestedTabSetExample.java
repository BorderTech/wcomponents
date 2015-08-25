package com.github.openborders.examples.theme;

import com.github.openborders.WLabel;
import com.github.openborders.WPanel;
import com.github.openborders.WTabSet;
import com.github.openborders.WText;
import com.github.openborders.WTabSet.TabMode;
import com.github.openborders.layout.FlowLayout;
import com.github.openborders.layout.FlowLayout.Alignment;

import com.github.openborders.examples.TextDuplicator;
import com.github.openborders.examples.menu.ColumnMenuExample;
import com.github.openborders.examples.menu.MenuBarExample;
import com.github.openborders.examples.menu.TreeMenuExample;

/**
 * A demonstration of various components. 
 * @author Martin Shevcheno
 */
public class NestedTabSetExample extends WPanel
{
    /**
     * Creates a NestedTabSetExample.
     */
    public NestedTabSetExample()
    {
        WTabSet tests = new WTabSet();
        this.setLayout(new FlowLayout(Alignment.VERTICAL));
        add(tests);
        tests.addTab(new ClientSideTabTests(), "Client Side", WTabSet.TAB_MODE_SERVER);
        tests.addTab(new WLabel("Another tab"), "Another", WTabSet.TAB_MODE_SERVER);
        tests.addTab(new ColumnMenuExample(), "Column Menu", TabMode.DYNAMIC);
        tests.addTab(new MenuBarExample(), "Menu Bar", TabMode.DYNAMIC);
        tests.addTab(new TreeMenuExample(), "Tree Menu", TabMode.DYNAMIC);
    }

    /**
     * A demonstration of client-side tabset functionality. 
     * @author Martin Shevchenko 
     */
    static class ClientSideTabTests extends WPanel
    {
        /**
         * Creates a ClientSideTabTests example.
         */
        public ClientSideTabTests()
        {
            this.setLayout(new FlowLayout(Alignment.VERTICAL));

            add(new WText("Tabs on top:"));
            WTabSet tabTop = new WTabSet();
            
            tabTop.addTab(new WText("CS Page One..."), "One", WTabSet.TAB_MODE_CLIENT);
            tabTop.addTab(new WText("CS Page Two..."), "Two", WTabSet.TAB_MODE_CLIENT);
            tabTop.addTab(new TextDuplicator("Dup"), "Duplicator", WTabSet.TAB_MODE_CLIENT);
            tabTop.addTab(new WText("CS Page Three..."), "Three", WTabSet.TAB_MODE_CLIENT);
            add(tabTop);

            add(new WText("Tabs at bottom:"));
            WTabSet tabBottom = new WTabSet(WTabSet.TabSetType.LEFT);
            tabBottom.addTab(new WText("One..."), "One", WTabSet.TAB_MODE_CLIENT);
            tabBottom.addTab(new WText("Two..."), "Two", WTabSet.TAB_MODE_CLIENT);
            add(tabBottom);
        }
    }
}
