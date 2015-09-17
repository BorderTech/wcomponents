package com.github.bordertech.wcomponents.examples.datatable;

import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.SimpleTableDataModel;
import com.github.bordertech.wcomponents.TableDataModel;
import com.github.bordertech.wcomponents.WDataTable;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WTableColumn;
import com.github.bordertech.wcomponents.WText;

/**
 * This example shows the simplest use of a {@link WDataTable}, with a two-dimensional array of data. The data will be
 * held in the user's session.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WDataTableExample extends WPanel {

	/**
	 * The table used in the example.
	 */
	private final WDataTable table;

	/**
	 * Creates a DataTableExample.
	 */
	public WDataTableExample() {
		table = new WDataTable();
		table.addColumn(new WTableColumn("First name", new WText()));
		table.addColumn(new WTableColumn("Last name", new WText()));
		table.addColumn(new WTableColumn("DOB", new WText()));

		add(table);
	}

	/**
	 * Override preparePaintComponent in order to set up the example data the first time that the example is accessed by
	 * each user.
	 *
	 * @param request the request being responded to.
	 */
	@Override
	protected void preparePaintComponent(final Request request) {
		if (!isInitialised()) {
			table.setDataModel(createTableModel());
			setInitialised(true);
		}
	}

	/**
	 * Creates a simple table data model containing some dummy data.
	 *
	 * @return a new data model.
	 */
	private TableDataModel createTableModel() {
		String[][] data = new String[][]{
			new String[]{"Joe", "Bloggs", "01/02/1973"},
			new String[]{"Jane", "Bloggs", "04/05/1976"},
			new String[]{"Kid", "Bloggs", "31/12/1999"}
		};

		return new SimpleTableDataModel(data);
	}
}
