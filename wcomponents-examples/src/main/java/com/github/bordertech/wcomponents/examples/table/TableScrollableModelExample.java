package com.github.bordertech.wcomponents.examples.table;

import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.SimpleBeanBoundTableModel;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WDateField;
import com.github.bordertech.wcomponents.WTable;
import com.github.bordertech.wcomponents.WTable.ExpandMode;
import com.github.bordertech.wcomponents.WTable.PaginationMode;
import com.github.bordertech.wcomponents.WTable.ScrollableTableModel;
import com.github.bordertech.wcomponents.WTableColumn;
import com.github.bordertech.wcomponents.WText;
import java.util.List;

/**
 * This example shows use of a {@link WTable}, with a {@link ScrollableTableModel}.
 * <p>
 * It extends {@link SimpleBeanBoundTableModel} to be scrollable. As this model is bean bound, the rows for the current
 * page are set as the bean for the table.
 * </p>
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class TableScrollableModelExample extends WContainer {

	/**
	 * The table.
	 */
	private final WTable table = new WTable();

	/**
	 * Construct example.
	 */
	public TableScrollableModelExample() {
		add(table);
		table.setType(WTable.Type.HIERARCHIC);
		table.addColumn(new WTableColumn("First name", WText.class));
		table.addColumn(new WTableColumn("Last name", WText.class));
		table.addColumn(new WTableColumn("Date of birth", WDateField.class));

		table.setRowsPerPage(3);
		table.setExpandMode(ExpandMode.DYNAMIC);
		table.setPaginationMode(PaginationMode.DYNAMIC);
		table.setExpandAll(true);

		add(new WButton("Refresh"));
	}

	/**
	 * Override preparePaintComponent in order to set up the example data the first time that the example is accessed by
	 * each user.
	 *
	 * @param request the request being responded to.
	 */
	@Override
	protected void preparePaintComponent(final Request request) {
		super.preparePaintComponent(request);
		if (!isInitialised()) {
			// Needs to be set per user as the model holds the current page index and total row per user.
			ExampleScrollableModel model = new ExampleScrollableModel(table,
					new String[]{"firstName", "lastName",
						"dateOfBirth"}, "more");
			model.setIterateFirstLevel(true);
			table.setTableModel(model);
			setInitialised(true);
		}
	}

	/**
	 * Example of a scrollable table model. Needs to be set per user as it holds the current page index and total row.
	 */
	public static class ExampleScrollableModel extends SimpleBeanBoundTableModel implements
			ScrollableTableModel {

		/**
		 * The table instance.
		 */
		private final WTable table;
		/**
		 * Total rows.
		 */
		private int totalRows = -1;
		/**
		 * Current start index.
		 */
		private int currentStartIndex = 0;

		/**
		 * @param table the table instance
		 * @param columnBeanProperties the column bean properties
		 * @param levelBeanProperty the level property
		 */
		public ExampleScrollableModel(final WTable table, final String[] columnBeanProperties,
				final String levelBeanProperty) {
			super(columnBeanProperties, levelBeanProperty);
			this.table = table;
		}

		/**
		 * Call a service to determine the total number of rows.
		 * <p>
		 * Hold the answer so the service is not called multiple times.
		 * </p>
		 *
		 * @return the total row count
		 */
		@Override
		public int getRowCount() {
			if (totalRows == -1) {
				totalRows = FakeService.fakeServiceTotalRows();
			}
			return totalRows;
		}

		/**
		 * Call a service to retrieve the rows for this page and set the rows as the bean on the table.
		 *
		 * @param start the start index of the current page
		 * @param end the end index of the current page
		 */
		@Override
		public void setCurrentRows(final int start, final int end) {
			currentStartIndex = start;
			// Save the current page rows as the bean on the table.
			table.setBean(FakeService.fakeServiceRetrievePage(start, end));
		}

		/**
		 * Override to allow for page offset.
		 *
		 * @param row the row index
		 * @return the bean for the top level
		 */
		@Override
		protected Object getTopRowBean(final List<Integer> row) {
			// Get current page rows
			List<?> lvl = getBeanList();
			if (lvl == null || lvl.isEmpty()) {
				return null;
			}

			// Offset row index
			int rowIdx = row.get(0) - currentStartIndex;
			Object rowData = lvl.get(rowIdx);
			return rowData;
		}

	}

	/**
	 * A fake service to mimic service calls to retrieve rows for a specific page.
	 */
	private static final class FakeService {

		/**
		 * Hold data.
		 */
		private static final List<PersonBean> DATA = ExampleDataUtil.createExampleData();

		/**
		 * Utility class.
		 */
		private FakeService() {
			// Do nothing
		}

		/**
		 * @return the total number of rows.
		 */
		public static int fakeServiceTotalRows() {
			return DATA.size();
		}

		/**
		 * @param startIndex the start index for the current page
		 * @param endIndex the end index for the current page
		 * @return the rows for the index range
		 */
		public static List<PersonBean> fakeServiceRetrievePage(final int startIndex,
				final int endIndex) {
			int max = Math.min(DATA.size() - 1, endIndex);
			return DATA.subList(startIndex, max + 1);
		}
	}
}
