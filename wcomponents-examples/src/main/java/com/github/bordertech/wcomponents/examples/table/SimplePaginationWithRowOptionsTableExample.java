package com.github.bordertech.wcomponents.examples.table;

import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.SimpleBeanBoundTableModel;
import com.github.bordertech.wcomponents.WDateField;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WTable;
import com.github.bordertech.wcomponents.WTable.PaginationMode;
import com.github.bordertech.wcomponents.WTableColumn;
import com.github.bordertech.wcomponents.WText;
import java.util.Arrays;

/**
 * This example demonstrates a simple {@link WTable} that is bean bound, has pagination and the rows per page options
 * set.
 * <p>
 * Uses {@link SimpleBeanBoundTableModel} to handle the bean binding.
 * </p>
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class SimplePaginationWithRowOptionsTableExample extends WPanel {

	/**
	 * The table used in the example.
	 */
	private final WTable table = new WTable();

	/**
	 * Create example.
	 */
	public SimplePaginationWithRowOptionsTableExample() {
		add(table);

		// Columns
		table.addColumn(new WTableColumn("First name", new WText()));
		table.addColumn(new WTableColumn("Last name", new WText()));
		table.addColumn(new WTableColumn("DOB", new WDateField()));

		// Pagination Mode
		table.setPaginationMode(PaginationMode.DYNAMIC);
		table.setRowsPerPage(2);

		// Setup model
		SimpleBeanBoundTableModel model = new SimpleBeanBoundTableModel(
				new String[]{"firstName", "lastName",
					"dateOfBirth"});
		table.setTableModel(model);

		// Set the rows per page options
		table.setRowsPerPageOptions(Arrays.asList(0, 2, 4));
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
			// Set the data as the bean on the table
			table.setBean(ExampleDataUtil.createExampleData());
			setInitialised(true);
		}
	}

}
