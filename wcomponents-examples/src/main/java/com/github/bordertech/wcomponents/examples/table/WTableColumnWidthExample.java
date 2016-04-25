package com.github.bordertech.wcomponents.examples.table;

import com.github.bordertech.wcomponents.AdapterBasicTableModel;
import com.github.bordertech.wcomponents.AdapterBasicTableModel.BasicTableModel;
import com.github.bordertech.wcomponents.SimpleTableModel;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WTable;
import com.github.bordertech.wcomponents.WTableColumn;
import com.github.bordertech.wcomponents.WText;

/**
 * This example shows the simplest use of a {@link WTable}, with a two-dimensional array of data and a simple of of
 * column width.
 *
 * @author Mark Reeves
 * @since 2015-12-21
 */
public class WTableColumnWidthExample extends WPanel {

	/**
	 * The table used in the example.
	 */
	private final WTable table = new WTable();

	/**
	 * Create example.
	 */
	public WTableColumnWidthExample() {
		add(table);
		table.setSeparatorType(WTable.SeparatorType.VERTICAL);

		// Columns
		WTableColumn columnFName = new WTableColumn("First name (40%)", new WText());
		WTableColumn columnSName = new WTableColumn("Last name (45%)", new WText());
		WTableColumn columnDoB = new WTableColumn("DOB (15%)", new WText());

		columnFName.setWidth(40);
		columnSName.setWidth(45);
		columnDoB.setWidth(15);

		table.addColumn(columnFName);
		table.addColumn(columnSName);
		table.addColumn(columnDoB);

		// Set the "basic" data
		String[][] data = ExampleDataUtil.createBasicData();
		BasicTableModel model = new SimpleTableModel(data);

		// Set the model on the table via the adapter
		table.setTableModel(new AdapterBasicTableModel(model));
	}

}
