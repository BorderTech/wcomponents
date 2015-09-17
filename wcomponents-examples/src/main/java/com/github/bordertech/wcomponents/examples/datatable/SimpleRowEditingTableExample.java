package com.github.bordertech.wcomponents.examples.datatable;

import com.github.bordertech.wcomponents.Action;
import com.github.bordertech.wcomponents.ActionEvent;
import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.SimpleTableDataModel;
import com.github.bordertech.wcomponents.TableDataModel;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WDataTable;
import com.github.bordertech.wcomponents.WDataTable.ActionConstraint;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WStyledText;
import com.github.bordertech.wcomponents.WTableColumn;
import com.github.bordertech.wcomponents.WTextField;
import java.util.List;

/**
 * This example demonstrates a simple editable table where only one row is allowed to be edited at a time.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class SimpleRowEditingTableExample extends WPanel {

	/**
	 * The table used in the example.
	 */
	private final WDataTable table;

	/**
	 * Creates a DataTableExample.
	 */
	public SimpleRowEditingTableExample() {
		// Create the example table. The data is created on first access by the user.
		table = new WDataTable();
		table.addColumn(new WTableColumn("First name", new WTextField()));
		table.addColumn(new WTableColumn("Last name", new WTextField()));
		table.addColumn(new WTableColumn("DOB", new WTextField()));
		table.setSelectMode(WDataTable.SelectMode.SINGLE);

		add(table);

		// Create a button which allows the user to select a row to edit.
		// An action constraint is also added to ensure that the user has selected a row to edit.
		WButton editRowButton = new WButton("Edit selected row");
		editRowButton.setAction(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				List<Integer> selectedRows = table.getSelectedRows();

				if (!selectedRows.isEmpty()) {
					MyTableDataModel model = (MyTableDataModel) table.getDataModel();
					model.setEditRow(selectedRows.get(0));
				}
			}
		});

		table.addAction(editRowButton);
		table.addActionConstraint(editRowButton, new ActionConstraint(1, 0, true,
				"At least one row must be selected to use this function."));

		// Create a component to display the table data in text format
		final WStyledText dataOutput = new WStyledText() {
			@Override
			public String getText() {
				MyTableDataModel model = (MyTableDataModel) table.getDataModel();
				String[][] data = model.getData();
				StringBuffer buf = new StringBuffer("Saved data:\n");

				for (String[] row : data) {
					for (String col : row) {
						buf.append(col);
						buf.append('\t');
					}

					buf.append('\n');
				}

				return buf.toString();
			}
		};

		dataOutput.setWhitespaceMode(WStyledText.WhitespaceMode.PRESERVE);

		WButton displayButton = new WButton("Save data");
		displayButton.setAction(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				table.updateBeanValue();
				table.getRepeater().reset();
			}
		});

		add(displayButton);
		add(dataOutput);
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

		return new MyTableDataModel(data);
	}

	/**
	 * An extension of SimpleTableDataModel which allows editing of a single row, and also provides an accessor method
	 * to retrieve the entire data.
	 */
	private static final class MyTableDataModel extends SimpleTableDataModel {

		/**
		 * The table data.
		 */
		private final String[][] data;

		/**
		 * The row which is being edited.
		 */
		private int editRow = -1;

		/**
		 * Creates a MyTableDataModel.
		 *
		 * @param data the table data.
		 */
		private MyTableDataModel(final String[][] data) {
			super(data);
			this.data = data;
			setEditable(true);
		}

		/**
		 * @return Returns the data.
		 */
		public String[][] getData() {
			return data;
		}

		/**
		 * Override the isCellEditable to control editability for individual rows.
		 *
		 * @param row the row index of the cell to check.
		 * @param col the column index of the cell to check.
		 *
		 * @return true if the cell is editable, false if not.
		 */
		@Override
		public boolean isCellEditable(final int row, final int col) {
			return super.isCellEditable(row, col)
					&& row == editRow;
		}

		/**
		 * Sets which row is being edited.
		 *
		 * @param editRow The row index of the editable row.
		 */
		public void setEditRow(final int editRow) {
			this.editRow = editRow;
		}
	}
}
