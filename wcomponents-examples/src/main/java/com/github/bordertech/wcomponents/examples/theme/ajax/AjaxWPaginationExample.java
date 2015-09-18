package com.github.bordertech.wcomponents.examples.theme.ajax;

import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.SimpleBeanListTableDataModel;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WDataTable;
import com.github.bordertech.wcomponents.WTableColumn;
import com.github.bordertech.wcomponents.WText;
import com.github.bordertech.wcomponents.examples.common.SimpleTableBean;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * An example of server side pagination using AJAX.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class AjaxWPaginationExample extends WContainer {

	/**
	 * The table used in the example.
	 */
	private final WDataTable table;

	/**
	 * Creates a AjaxWPaginationExample.
	 */
	public AjaxWPaginationExample() {
		table = new WDataTable();

		table.addColumn(new WTableColumn("Name", WText.class));
		table.addColumn(new WTableColumn("Type", WText.class));
		table.addColumn(new WTableColumn("Thing", WText.class));

		table.setStripingType(WDataTable.StripingType.ROWS);

		table.setRowsPerPage(3);
		table.setPaginationMode(WDataTable.PaginationMode.DYNAMIC);

		add(table);
	}

	/**
	 * Override preparePaintComponent in order to set up the example data the first time through.
	 *
	 * @param request the request being responded to.
	 */
	@Override
	protected void preparePaintComponent(final Request request) {
		super.preparePaintComponent(request);

		if (!isInitialised()) {
			List<Serializable> items = new ArrayList<>();
			items.add(new SimpleTableBean("A", "none", "thing"));
			items.add(new SimpleTableBean("B", "some", "thing2"));
			items.add(new SimpleTableBean("C", "little", "thing3"));
			items.add(new SimpleTableBean("D", "lots", "thing4"));
			items.add(new SimpleTableBean("E", "none", "thing5"));
			items.add(new SimpleTableBean("F", "some", "thing6"));
			items.add(new SimpleTableBean("G", "little", "thing7"));
			items.add(new SimpleTableBean("H", "lots", "thing8"));
			items.add(new SimpleTableBean("I", "none", "thing9"));
			items.add(new SimpleTableBean("J", "some", "thing10"));
			items.add(new SimpleTableBean("K", "little", "thing11"));
			items.add(new SimpleTableBean("L", "lots", "thing12"));

			table.setDataModel(new SimpleBeanListTableDataModel(
					new String[]{"name", "type", "thing"}, items));
			setInitialised(true);
		}
	}

}
