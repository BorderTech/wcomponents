package com.github.bordertech.wcomponents.examples.datatable;

import com.github.bordertech.wcomponents.Action;
import com.github.bordertech.wcomponents.ActionEvent;
import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.SimpleBeanBoundTableDataModel;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WDataTable;
import com.github.bordertech.wcomponents.WDateField;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WTableColumn;
import com.github.bordertech.wcomponents.WText;
import com.github.bordertech.wcomponents.WTextField;
import com.github.bordertech.wcomponents.layout.FlowLayout;
import com.github.bordertech.wcomponents.layout.FlowLayout.Alignment;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.LogFactory;

/**
 * This example shows the use of a {@link WDataTable} with a list of editable beans. All bean binding logic for the
 * table column renderers is performed by the data model.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class DataTableBeanExample extends WPanel {

	/**
	 * The table used in the example.
	 */
	private final WDataTable table = createTable();

	/**
	 * Creates a DataTableBeanExample.
	 */
	public DataTableBeanExample() {
		setLayout(new FlowLayout(Alignment.VERTICAL, 0, 5));

		// Since this data model doesn't store any user state information within it,
		// we can safely use a single shared instance.
		SimpleBeanBoundTableDataModel dataModel = new SimpleBeanBoundTableDataModel(
				new String[]{"firstName", "lastName", "dateOfBirth"});
		dataModel.setEditable(true);
		table.setDataModel(dataModel);

		table.setBeanProperty(".");
		add(table);

		WButton saveButton = new WButton("Update bean");

		saveButton.setAction(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				// Will cause the bean to get updated
				table.updateBeanValue();

				// This will clear out any data held by the table.
				// Any modifications entered by the user should have been copied to the bean.
				List<PersonBean> data = (List<PersonBean>) table.getBean();
				table.reset();
				table.setBean(data);
			}
		});

		WPanel buttonPanel = new WPanel();
		buttonPanel.setLayout(new FlowLayout(Alignment.LEFT, 5, 0));
		buttonPanel.add(saveButton);
		buttonPanel.add(new WButton("Refresh page"));
		add(buttonPanel);

		add(new WText() {
			@Override
			public String getText() {
				List<PersonBean> data = (List<PersonBean>) table.getBean();
				return "Current bean data: " + data.toString();
			}
		});
	}

	/**
	 * Override preparePaintComponent in order to set the data up for the first time the user accesses the example.
	 *
	 * @param request the request being responded to.
	 */
	@Override
	protected void preparePaintComponent(final Request request) {
		super.preparePaintComponent(request);

		if (!isInitialised()) {
			// Create the example data and give it to the table
			table.setBean(createExampleData());
			setInitialised(true);
		}
	}

	/**
	 * Creates and configures the table to be used by the example.
	 *
	 * @return a new configured table.
	 */
	private WDataTable createTable() {
		WDataTable tbl = new WDataTable();
		tbl.addColumn(new WTableColumn("First name", new WTextField()));
		tbl.addColumn(new WTableColumn("Last name", new WTextField()));
		tbl.addColumn(new WTableColumn("DOB", new WDateField()));
		return tbl;
	}

	/**
	 * Creates the example data.
	 *
	 * @return the example data.
	 */
	private List<PersonBean> createExampleData() {
		List<PersonBean> data = new ArrayList<>(3);
		SimpleDateFormat sdf = new SimpleDateFormat("dd/mm/yyyy");

		try {
			data.add(new PersonBean("Joe", "Bloggs", sdf.parse("01/02/1973")));
			data.add(new PersonBean("Jane", "Bloggs", sdf.parse("04/05/1976")));
			data.add(new PersonBean("Kid", "Bloggs", sdf.parse("31/12/1999")));
		} catch (ParseException e) {
			LogFactory.getLog(DataTableBeanExample.class).error("Failed to create test data", e);
		}

		return data;
	}
}
