package com.github.bordertech.wcomponents.examples.table;

import com.github.bordertech.wcomponents.AdapterBasicTableModel;
import com.github.bordertech.wcomponents.Image;
import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.SimpleTableModel;
import com.github.bordertech.wcomponents.WImage;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WTable;
import com.github.bordertech.wcomponents.WTable.TableModel;
import com.github.bordertech.wcomponents.WTableColumn;
import com.github.bordertech.wcomponents.WText;
import com.github.bordertech.wcomponents.examples.DynamicImage;

/**
 * This example shows use of a {@link WTable}, with a two-dimensional array of data and rendering of dynamic content.
 * The data will be held in the user's session.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class TableContentExample extends WPanel {

	/**
	 * The table used in the example.
	 */
	private final WTable table;

	/**
	 * Create example.
	 */
	public TableContentExample() {
		table = new WTable();
		table.addColumn(new WTableColumn("Name (text)", new WText()));
		table.addColumn(new WTableColumn("Name (image)", new DynamicWImage()));
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
		super.preparePaintComponent(request);
		if (!isInitialised()) {
			table.setTableModel(createTableModel());
			setInitialised(true);
		}
	}

	/**
	 * Creates a simple table data model containing some dummy data.
	 *
	 * @return a new data model.
	 */
	private TableModel createTableModel() {
		String[][] data = new String[][]{new String[]{"Row 1", "Row 1"}, new String[]{"Row 2", "Row 2"},
		new String[]{"Row 3", "Row 3"}};

		return new AdapterBasicTableModel(new SimpleTableModel(data));
	}

	/**
	 * A WImage implementation which displays a dynamic image depending on its bean value.
	 */
	private static final class DynamicWImage extends WImage {

		/**
		 * @return the image to be displayed.
		 */
		@Override
		public Image getImage() {
			return new DynamicImage(((String) getData()));
		}
	}

}
