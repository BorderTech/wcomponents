package com.github.bordertech.wcomponents.examples.table;

import com.github.bordertech.wcomponents.Action;
import com.github.bordertech.wcomponents.ActionEvent;
import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.SimpleBeanBoundTableModel;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WDateField;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WTable;
import com.github.bordertech.wcomponents.WTableColumn;
import com.github.bordertech.wcomponents.WText;

/**
 * This example shows the use of an action from within a {@link WTable} table cell.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class TableCellActionExample extends WPanel {

	/**
	 * Used to display the output from the example actions.
	 */
	private final WText actionText = new WText();

	/**
	 * Example table.
	 */
	private final WTable table = new WTable();

	/**
	 * Create example.
	 */
	public TableCellActionExample() {
		add(table);

		// Columns
		table.addColumn(new WTableColumn("First name", new WText()));
		table.addColumn(new WTableColumn("Last name", new WText()));
		table.addColumn(new WTableColumn("DOB", new WDateField()));

		// Button in column with an action
		final WButton button = new WButton("Action");
		button.setBeanProperty("personId");
		button.setAction(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				PersonBean person = (PersonBean) button.getBean();
				actionText.setText("Action for: " + person.getFirstName() + ' ' + person.
						getLastName());
			}
		});
		table.addColumn(new WTableColumn("Action", button));

		// Setup model - The button column gets the "person bean".
		SimpleBeanBoundTableModel model = new SimpleBeanBoundTableModel(
				new String[]{"firstName", "lastName",
					"dateOfBirth", "."});
		table.setTableModel(model);

		WPanel panel = new WPanel();
		add(panel);
		panel.add(actionText);

		// Refresh text via AJAX
		button.setAjaxTarget(panel);
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
