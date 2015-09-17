package com.github.bordertech.wcomponents.examples.datatable;

import com.github.bordertech.wcomponents.Action;
import com.github.bordertech.wcomponents.ActionEvent;
import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.SimpleTableDataModel;
import com.github.bordertech.wcomponents.TableDataModel;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WDataTable;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WStyledText;
import com.github.bordertech.wcomponents.WTableColumn;
import com.github.bordertech.wcomponents.WTextField;

/**
 * This example demonstrates an editable table.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class SimpleEditableDataTableExample extends WPanel {

	/**
	 * The table used in the example.
	 */
	private final WDataTable table;

	/**
	 * Creates a DataTableExample.
	 */
	public SimpleEditableDataTableExample() {
		// Create the example table. The data is created on first access by the user.
		table = new WDataTable();
		table.addColumn(new WTableColumn("First name", new WTextField()));
		table.addColumn(new WTableColumn("Last name", new WTextField()));
		table.addColumn(new WTableColumn("DOB", new WTextField()));

		add(table);

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

		// Create a button to trigger the display of the data
		WButton displayButton = new WButton("Save data");
		displayButton.setAction(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				table.updateBeanValue();
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
	 * An extension of SimpleTableDataModel which allows editing, and also provides an accessor method to retrieve the
	 * entire data.
	 */
	private static final class MyTableDataModel extends SimpleTableDataModel {

		/**
		 * The table data.
		 */
		private final String[][] data;

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
	}
}
