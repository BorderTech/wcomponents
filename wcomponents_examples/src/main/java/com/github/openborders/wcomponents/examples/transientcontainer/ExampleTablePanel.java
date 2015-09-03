package com.github.openborders.wcomponents.examples.transientcontainer; 

import java.util.List;

import com.github.openborders.wcomponents.SimpleBeanListTableDataModel;
import com.github.openborders.wcomponents.WContainer;
import com.github.openborders.wcomponents.WDataTable;
import com.github.openborders.wcomponents.WTableColumn;
import com.github.openborders.wcomponents.WText;

/**
 * An example table for the {@link TransientDataContainerExample}.
 * The data must be supplied to the table using the setData(List) method.   
 * 
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class ExampleTablePanel extends WContainer
{
    /** The WDataTable used to present the data. */
    private final WDataTable simpleTable = new WDataTable();

    /**
     * Creates an ExampleTablePanel.
     */
    public ExampleTablePanel()
    {
        simpleTable.setCaption("Example table usage with no data stored in the UIContext long-term.");
        simpleTable.setSummary("Example table usage with no data stored in the UIContext long-term.");
        simpleTable.setNoDataMessage("No Data!");
        
        simpleTable.addColumn(new WTableColumn("Colour", WText.class));
        simpleTable.addColumn(new WTableColumn("Shape", WText.class));
        simpleTable.addColumn(new WTableColumn("Animal", WText.class));

        add(simpleTable);
    }

    /**
     * Sets the table data.
     * 
     * @param data a list of {@link ExampleDataBean}s.
     */
    public void setData(final List data)
    {
        // Bean properties to render
        String[] properties = new String[]{ "colour", "shape", "animal" };
        
        simpleTable.setDataModel(new SimpleBeanListTableDataModel(properties, data));
    }
}
