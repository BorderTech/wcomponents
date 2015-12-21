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
 * column alignment.
 *
 * @author Mark Reeves
 * @since 2015-12-21
 */
public class WTableColumnAlignmentExample extends WPanel {

	/**
	 * The table used in the example.
	 */
	private final WTable table = new WTable();

	/**
	 * Create example.
	 */
	public WTableColumnAlignmentExample() {
		add(table);
		table.setSeparatorType(WTable.SeparatorType.VERTICAL);

		// Columns
		WTableColumn columnFName = new WTableColumn("First name (left)", new WText());
		WTableColumn columnSName = new WTableColumn("Last name (center)", new WText());
		WTableColumn columnDoB = new WTableColumn("DOB (right)", new WText());

		columnFName.setAlign(WTableColumn.Alignment.LEFT);
		columnSName.setAlign(WTableColumn.Alignment.CENTER);
		columnDoB.setAlign(WTableColumn.Alignment.RIGHT);

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
