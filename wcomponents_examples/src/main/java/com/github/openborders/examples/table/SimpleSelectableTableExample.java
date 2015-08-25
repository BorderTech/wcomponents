package com.github.openborders.examples.table;

import com.github.openborders.Action;
import com.github.openborders.ActionEvent;
import com.github.openborders.Request;
import com.github.openborders.SimpleBeanBoundTableModel;
import com.github.openborders.WButton;
import com.github.openborders.WDateField;
import com.github.openborders.WPanel;
import com.github.openborders.WStyledText;
import com.github.openborders.WTable;
import com.github.openborders.WTableColumn;
import com.github.openborders.WText;
import com.github.openborders.WTable.ActionConstraint;
import com.github.openborders.WTable.SelectMode;

/**
 * This example demonstrates a simple {@link WTable} that is bean bound and has row selection.
 * <p>
 * Uses {@link SimpleBeanBoundTableModel} to handle the bean binding.
 * </p>
 * 
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class SimpleSelectableTableExample extends WPanel
{
    /** The table for the example. */
    private final WTable table = new WTable();

    /**
     * Create example.
     */
    public SimpleSelectableTableExample()
    {
        add(table);
        table.addColumn(new WTableColumn("First name", new WText()));
        table.addColumn(new WTableColumn("Last name", new WText()));
        table.addColumn(new WTableColumn("DOB", new WDateField()));

        // Enable multiple row selection. Use WDataTable.SelectMode.SINGLE for single selection
        table.setSelectMode(SelectMode.MULTIPLE);

        // Setup model
        SimpleBeanBoundTableModel model = new SimpleBeanBoundTableModel(new String[] { "firstName", "lastName",
                                                                                      "dateOfBirth" });
        model.setSelectable(true);
        table.setTableModel(model);

        // Add a button to the table for the user to be able to select rows
        WButton selectButton = new WButton("Select");
        table.addAction(selectButton);

        // An action constraint is used so that a row must be selected before using the "Select" button
        table.addActionConstraint(selectButton, new ActionConstraint(1, 0, true, "One or more rows must be selected"));

        final WStyledText selectionText = new WStyledText();
        selectionText.setWhitespaceMode(WStyledText.WhitespaceMode.PRESERVE);

        // The button displays the selected rows in text format.
        selectButton.setAction(new Action()
        {
            public void execute(final ActionEvent event)
            {
                StringBuffer buf = new StringBuffer("Selection:\n");

                for (Object selected : table.getSelectedRows())
                {
                    // The Model uses the "bean" as the key
                    PersonBean person = (PersonBean) selected;
                    buf.append(person.toString()).append('\n');
                }

                selectionText.setText(buf.toString());
            }
        });

        WPanel textPanel = new WPanel();
        selectButton.setAjaxTarget(textPanel);
        textPanel.add(selectionText);
        add(textPanel);
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
