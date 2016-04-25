package com.github.bordertech.wcomponents.examples.datatable;

import com.github.bordertech.wcomponents.Action;
import com.github.bordertech.wcomponents.ActionEvent;
import com.github.bordertech.wcomponents.SimpleTableDataModel;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WDataTable;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WStyledText;
import com.github.bordertech.wcomponents.WTableColumn;
import com.github.bordertech.wcomponents.WText;
import com.github.bordertech.wcomponents.layout.FlowLayout;
import com.github.bordertech.wcomponents.layout.FlowLayout.Alignment;

/**
 * This example shows use of a {@link WDataTable}, with row selection.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class SelectableDataTableExample extends WPanel {

	/**
	 * Creates a SelectableDataTableExample.
	 */
	public SelectableDataTableExample() {
		setLayout(new FlowLayout(Alignment.VERTICAL));

		final WStyledText selectionText = new WStyledText();
		selectionText.setWhitespaceMode(WStyledText.WhitespaceMode.PRESERVE);

		final WDataTable table = createTable();

		// Enable multiple row selection. Use WDataTable.SelectMode.SINGLE for single selection
		table.setSelectMode(WDataTable.SelectMode.MULTIPLE);

		// Add a button to the table for the user to be able to select rows
		WButton selectButton = new WButton("Select");
		table.addAction(selectButton);

		// An action constraint is used so that a row must be selected before using the "Select" button
		table.addActionConstraint(selectButton, new WDataTable.ActionConstraint(1, 0, true,
				"One or more rows must be selected"));

		// The button displays the selected rows in text format.
		selectButton.setAction(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				StringBuffer buf = new StringBuffer("Selection:\n");

				for (int row : table.getSelectedRows()) {
					buf.append(table.getDataModel().getValueAt(row, 0))
							.append(' ')
							.append(table.getDataModel().getValueAt(row, 1))
							.append('\n');
				}

				selectionText.setText(buf.toString());
			}
		});

		add(table);

		WPanel textPanel = new WPanel();
		selectButton.setAjaxTarget(textPanel);
		textPanel.add(selectionText);
		add(textPanel);
	}

	/**
	 * Creates and configures the table to be used by the example. The table is configured with global rather than user
	 * data. Although this is not a realistic scenario, it will suffice for this example.
	 *
	 * @return a new configured table.
	 */
	private WDataTable createTable() {
		WDataTable table = new WDataTable();
		table.addColumn(new WTableColumn("First name", new WText()));
		table.addColumn(new WTableColumn("Last name", new WText()));
		table.addColumn(new WTableColumn("DOB", new WText()));

		String[][] data = new String[][]{
			new String[]{"Joe", "Bloggs", "01/02/1973"},
			new String[]{"Jane", "Bloggs", "04/05/1976"},
			new String[]{"Kid", "Bloggs", "31/12/1999"}
		};

		table.setDataModel(new SimpleTableDataModel(data));

		return table;
	}
}
