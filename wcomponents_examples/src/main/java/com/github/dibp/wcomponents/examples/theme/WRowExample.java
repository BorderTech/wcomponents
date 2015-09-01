package com.github.dibp.wcomponents.examples.theme; 

import com.github.dibp.wcomponents.WColumn;
import com.github.dibp.wcomponents.WHeading;
import com.github.dibp.wcomponents.WPanel;
import com.github.dibp.wcomponents.WRow;
import com.github.dibp.wcomponents.WText;
import com.github.dibp.wcomponents.layout.FlowLayout;
import com.github.dibp.wcomponents.layout.FlowLayout.Alignment;

/**
 * Example showing how to use the {@link WRow} component. 
 * 
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WRowExample extends WPanel
{
    /**
     * Creates a WRowExample.
     */
    public WRowExample()
    {
        setLayout(new FlowLayout(Alignment.VERTICAL, 0, 5));
        
        add(new WHeading(WHeading.SECTION, "WRow / WCol"));

        add(createRow(0, new int[] { 10, 90 }));
        add(createRow(0, new int[] { 20, 80 }));
        add(createRow(0, new int[] { 30, 70 }));
        add(createRow(0, new int[] { 40, 60 }));
        add(createRow(0, new int[] { 50, 50 }));
        add(createRow(0, new int[] { 60, 40 }));
        add(createRow(0, new int[] { 70, 30 }));
        add(createRow(0, new int[] { 80, 20 }));
        add(createRow(0, new int[] { 90, 10 }));
        add(createRow(0, new int[] { 33, 33, 33 }));
        add(createRow(0, new int[] { 25, 25, 25, 25 }));
        add(createRow(0, new int[] { 20, 20, 20, 20, 20 }));
        
        add(new WHeading(WHeading.SECTION, "WRow / WCol with hgap=5"));
        add(createRow(5, new int[] { 33, 33, 33 }));
        add(createRow(5, new int[] { 25, 25, 25, 25 }));
        add(createRow(5, new int[] { 20, 20, 20, 20, 20 }));
    }

    /**
     * Creates a row containing columns with the given widths.
     * 
     * @param hgap the horizontal gap between columns, in pixels.
     * @param colWidths the percentage widths for each column.
     * @return a WRow containing columns with the given widths.
     */
    private WRow createRow(final int hgap, final int[] colWidths)
    {
        WRow row = new WRow(hgap);
        
        for (int i = 0; i < colWidths.length; i++)
        {
            WColumn col = new WColumn(colWidths[i]);
            WPanel box = new WPanel(WPanel.Type.BOX);
            box.add(new WText(colWidths[i] + "%"));
            col.add(box);
            row.add(col);
        }
        
        return row;
    }
}
