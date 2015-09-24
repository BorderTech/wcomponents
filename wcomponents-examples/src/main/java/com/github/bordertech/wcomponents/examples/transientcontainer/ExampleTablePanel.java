package com.github.bordertech.wcomponents.examples.transientcontainer;

import com.github.bordertech.wcomponents.SimpleBeanListTableDataModel;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WDataTable;
import com.github.bordertech.wcomponents.WTableColumn;
import com.github.bordertech.wcomponents.WText;
import java.util.List;

/**
 * An example table for the {@link TransientDataContainerExample}. The data must be supplied to the table using the
 * setData(List) method.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class ExampleTablePanel extends WContainer {

	/**
	 * The WDataTable used to present the data.
	 */
	private final WDataTable simpleTable = new WDataTable();

	/**
	 * Creates an ExampleTablePanel.
	 */
	public ExampleTablePanel() {
		simpleTable.
				setCaption("Example table usage with no data stored in the UIContext long-term.");
		simpleTable.
				setSummary("Example table usage with no data stored in the UIContext long-term.");
		simpleTable.setNoDataMessage("No Data!");

		simpleTable.addColumn(new WTableColumn("Colour", WText.class));
		simpleTable.addColumn(new WTableColumn("Shape", WText.class));
		simpleTable.addColumn(new WTableColumn("Animal", WText.class));

		add(simpleTable);
	}

	/**
	 * Sets the table data.
	 *
	 * @param data a list of {@link ExampleDataBean}s.
	 */
	public void setData(final List data) {
		// Bean properties to render
		String[] properties = new String[]{"colour", "shape", "animal"};

		simpleTable.setDataModel(new SimpleBeanListTableDataModel(properties, data));
	}
}
