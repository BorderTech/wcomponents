package com.github.bordertech.wcomponents.examples.datatable;

import com.github.bordertech.wcomponents.AbstractTableDataModel;
import com.github.bordertech.wcomponents.ScrollableTableDataModel;
import com.github.bordertech.wcomponents.TableDataModel;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WDataTable;
import com.github.bordertech.wcomponents.WDataTable.PaginationMode;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WTableColumn;
import com.github.bordertech.wcomponents.WText;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.LogFactory;

/**
 * This example shows the use of a {@link WDataTable}, with a scrollable data model.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class ScrollableDataTableModelExample extends WPanel {

	/**
	 * Creates a ScrollableDataTableModelExample.
	 */
	public ScrollableDataTableModelExample() {
		WDataTable table = createTable();

		add(table);
		add(new WButton("Submit"));
	}

	/**
	 * Creates and configures the table to be used by the example. The table is configured with global rather than user
	 * data. Although this is not a realistic scenario, it will suffice for this example.
	 *
	 * @return a new configured table.
	 */
	private WDataTable createTable() {
		WDataTable table = new WDataTable();
		table.addColumn(new WTableColumn("First name", WText.class));
		table.addColumn(new WTableColumn("Last name", WText.class));
		table.addColumn(new WTableColumn("DOB", WText.class));
		table.setPaginationMode(PaginationMode.DYNAMIC);
		table.setRowsPerPage(1);

		table.setDataModel(createTableModel());

		return table;
	}

	/**
	 * Creates a simple table data model containing some dummy data.
	 *
	 * @return a new data model.
	 */
	private TableDataModel createTableModel() {
		MyTableDataModel model = new MyTableDataModel();
		return model;
	}

	/**
	 * A basic implementation of the ScrollableTableDataModel.
	 */
	public static final class MyTableDataModel extends AbstractTableDataModel implements
			ScrollableTableDataModel {

		/**
		 * The current row offset for the first row, used to translate the table row index to the list index.
		 */
		private int startRow = 0;

		/**
		 * The current total available row count. For this example, this is fixed.
		 */
		private int rowCount = 0;

		/**
		 * The current list of data being displayed.
		 */
		private final List<String[]> data = new ArrayList<>();

		/**
		 * <p>
		 * This method will be called by the table to notify the TableDataModel of which rows are likely to be used in
		 * the near future.</p>
		 *
		 * <p>
		 * This example implementation asks the service to return a sub-set of data, for the current page being
		 * displayed.</p>
		 *
		 * @param start the starting row index.
		 * @param end the ending row index.
		 */
		@Override
		public void setCurrentRows(final int start, final int end) {
			startRow = start;
			data.clear();
			rowCount = DummyService.read(start, end, data);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int getRowCount() {
			return rowCount;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Object getValueAt(final int row, final int col) {
			// Since we only store a sub-set of data, we need to adjust the row index.
			return data.get(row - startRow)[col];
		}
	}

	/**
	 * A dummy service implementation which just has hard-coded data.
	 */
	private static final class DummyService {

		/**
		 * The hard-coded data for this "service".
		 */
		private static final String[][] DATA = new String[][]{
			new String[]{"Joe", "Bloggs", "01/02/1973"},
			new String[]{"Jane", "Bloggs", "04/05/1976"},
			new String[]{"Kid", "Bloggs", "31/12/1999"}
		};

		/**
		 * Reads a sub-set of data into the "data" list, and returns the total number of rows.
		 *
		 * @param start the start index of the data to read.
		 * @param end the end index of the data to read.
		 * @param data the list to place the data into.
		 * @return the total number of rows contained in this service.
		 */
		public static int read(final int start, final int end, final List<String[]> data) {
			LogFactory.getLog(DummyService.class).info(
					"Calling DummyService to read results " + start + " to " + end);

			for (int i = start; i <= Math.min(end, DATA.length - 1); i++) {
				data.add(DATA[i]);
			}

			return DATA.length;
		}
	}
}
