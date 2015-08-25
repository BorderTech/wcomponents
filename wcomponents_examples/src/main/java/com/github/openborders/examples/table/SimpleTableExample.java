package com.github.openborders.examples.table;

import com.github.openborders.Request;
import com.github.openborders.SimpleBeanBoundTableModel;
import com.github.openborders.WDateField;
import com.github.openborders.WPanel;
import com.github.openborders.WTable;
import com.github.openborders.WTableColumn;
import com.github.openborders.WText;

/**
 * This example demonstrates a simple {@link WTable} that is bean bound.
 * <p>
 * Uses {@link SimpleBeanBoundTableModel} to handle the bean binding.
 * </p>
 * 
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class SimpleTableExample extends WPanel
{
    /** The table used in the example. */
    private final WTable table = new WTable();

    /**
     * Create example.
     */
    public SimpleTableExample()
    {
        add(table);

        // Columns
        table.addColumn(new WTableColumn("First name", new WText()));
        table.addColumn(new WTableColumn("Last name", new WText()));
        table.addColumn(new WTableColumn("DOB", new WDateField()));

        // Setup model
        SimpleBeanBoundTableModel model = new SimpleBeanBoundTableModel(new String[] { "firstName", "lastName",
                                                                                      "dateOfBirth" });
        table.setTableModel(model);
    }

    /**
     * Override preparePaintComponent in order to set up the example data the first time that the example is accessed by
     * each user.
     * 
     * @param request the request being responded to.
     */
    @Override
    protected void preparePaintComponent(final Request request)
    {
        super.preparePaintComponent(request);
        if (!isInitialised())
        {
            // Set the data as the bean on the table
            table.setBean(ExampleDataUtil.createExampleData());
            setInitialised(true);
        }
    }

}
