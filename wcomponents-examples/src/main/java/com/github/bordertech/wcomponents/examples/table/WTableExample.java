package com.github.bordertech.wcomponents.examples.table;

import com.github.bordertech.wcomponents.AdapterBasicTableModel;
import com.github.bordertech.wcomponents.AdapterBasicTableModel.BasicTableModel;
import com.github.bordertech.wcomponents.SimpleTableModel;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WTable;
import com.github.bordertech.wcomponents.WTableColumn;
import com.github.bordertech.wcomponents.WText;

/**
 * This example shows the simplest use of a {@link WTable}, with a two-dimensional array of data.
 * <p>
 * Shows how the {@link BasicTableModel} can be used with the {@link AdapterBasicTableModel}.
 * </p>
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class WTableExample extends WPanel {

	/**
	 * The table used in the example.
	 */
	private final WTable table = new WTable();

	/**
	 * Create example.
	 */
	public WTableExample() {
		add(table);

		// Columns
		table.addColumn(new WTableColumn("First name", new WText()));
		table.addColumn(new WTableColumn("Last name", new WText()));
		table.addColumn(new WTableColumn("DOB", new WText()));

		// Set the "basic" data
		String[][] data = ExampleDataUtil.createBasicData();
		BasicTableModel model = new SimpleTableModel(data);

		// Set the model on the table via the adapter
		table.setTableModel(new AdapterBasicTableModel(model));
	}

}
