package com.github.bordertech.wcomponents.examples.table;

import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.SimpleBeanBoundTableModel;
import com.github.bordertech.wcomponents.WDateField;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WTable;
import com.github.bordertech.wcomponents.WTable.ExpandMode;
import com.github.bordertech.wcomponents.WTableColumn;
import com.github.bordertech.wcomponents.WText;

/**
 * This example demonstrates a simple {@link WTable} that is bean bound and has expandable rows. The sample data is
 * heirarchic (ie the expanded rows use the same columns).
 * <p>
 * Uses {@link SimpleBeanBoundTableModel} to handle the bean binding and define the expandable levels.
 * </p>
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class SimpleExpandableTableExample extends WPanel {

	/**
	 * The table used in the example.
	 */
	private final WTable table = new WTable();

	/**
	 * Create example.
	 */
	public SimpleExpandableTableExample() {
		add(table);

		// Columns
		table.addColumn(new WTableColumn("First name", new WText()));
		table.addColumn(new WTableColumn("Last name", new WText()));
		table.addColumn(new WTableColumn("DOB", new WDateField()));

		// Expand mode
		table.setExpandMode(ExpandMode.DYNAMIC);

		// Use expand all controls
		table.setExpandAll(true);

		// Heirarchic data (so displays correctly)
		table.setType(WTable.Type.HIERARCHIC);

		// Setup model - Define bean properties for the columns and which property (ie "more") determines the next level
		SimpleBeanBoundTableModel model = new SimpleBeanBoundTableModel(
				new String[]{"firstName", "lastName",
					"dateOfBirth"}, "more");

		// Iterate on the row if the bean has "more" details.
		model.setIterateFirstLevel(true);

		table.setTableModel(model);
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
