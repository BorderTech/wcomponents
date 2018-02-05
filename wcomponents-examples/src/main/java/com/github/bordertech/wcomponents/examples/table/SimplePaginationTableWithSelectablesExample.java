package com.github.bordertech.wcomponents.examples.table;


import com.github.bordertech.wcomponents.*;

/**
 * This example demonstrates a simple {@link WTable} that has been been bound, has pagination, and has row selection.
 * <p>
 * Uses {@link SimpleBeanBoundTableModel} to handle bean binding.
 * </p>
 * <p>
 * Note: Action constraints on selected rows carry over across pages.
 * </p>
 * @author Jonathan Simmons
 */

public class SimplePaginationTableWithSelectablesExample extends WPanel {

	private final WTable wTable = new WTable();

	private final WStyledText selectionText = new WStyledText();

	public SimplePaginationTableWithSelectablesExample() {
		selectionText.setWhitespaceMode(WStyledText.WhitespaceMode.PRESERVE);
		add(wTable);

		wTable.addColumn(new WTableColumn("First name", new WText()));
		wTable.addColumn(new WTableColumn("Last name", new WText()));
		wTable.addColumn(new WTableColumn("DOB", new WDateField()));

		wTable.setSelectMode(WTable.SelectMode.MULTIPLE);
		wTable.setPaginationMode(WTable.PaginationMode.DYNAMIC);
		wTable.setRowsPerPage(5);

		// Setup model
		SimpleBeanBoundTableModel model = new SimpleBeanBoundTableModel(
			new String[]{"firstName", "lastName",
				"dateOfBirth"});
		model.setSelectable(true);
		wTable.setTableModel(model);

		// Add a button to the table for the user to be able to select rows
		WButton selectButton = new WButton("Select");
		wTable.addAction(selectButton);

		// An action constraint is used so that a row must be selected before using the "Select" button
		wTable.addActionConstraint(selectButton, new WTable.ActionConstraint(1, 3, true, "One to three rows must be selected"));

		WButton deleteWithWarningCondition = new WButton("Delete");
		wTable.addAction(deleteWithWarningCondition);
		// prevent the action if fewer than one row is selected.
		wTable.addActionConstraint(deleteWithWarningCondition, new WTable.ActionConstraint(1, 0, true, "At least one row must be selected"));
		// Warn the user if more than one row is selected but do not prevent the action unless the user bails.
		wTable.addActionConstraint(deleteWithWarningCondition, new WTable.ActionConstraint(0, 1, false, "Are you sure you wish to delete these rows?"));
		WButton editButton = new WButton("Edit");
		wTable.addAction(editButton);
		// Prevent the action unless exactly one row is selected.
		wTable.addActionConstraint(editButton, new WTable.ActionConstraint(1, 1, true, "Exactly one row must be selected"));
		deleteWithWarningCondition.setAction(buttonAction("Dummy delete of:"));
		// The button displays the selected rows in text format.
		selectButton.setAction(buttonAction("Selection:"));
		editButton.setAction(buttonAction("Dummy edit of:"));

		WButton refreshButton = new WButton("Refresh");
		wTable.addAction(refreshButton);

		// Update the text panel on button click
		WPanel textPanel = new WPanel();
		selectButton.setAjaxTarget(textPanel);
		deleteWithWarningCondition.setAjaxTarget(textPanel);
		editButton.setAjaxTarget(textPanel);
		textPanel.add(selectionText);
		add(textPanel);
	}

	/**
	 * Common action for the table buttons.
	 * @param preface the initial content for the text output.
	 * @return an Action instance
	 */
	private Action buttonAction(final String preface) {
		return new Action() {
			@Override
			public void execute(final ActionEvent event) {

				StringBuffer buf = new StringBuffer(preface);
				buf.append("\n");

				for (Object selected : wTable.getSelectedRows()) {
					// The Model uses the "bean" as the key
					PersonBean person = (PersonBean) selected;
					buf.append(person.toString()).append("\n");
				}

				selectionText.setText(buf.toString());
			}
		};
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
			wTable.setBean(ExampleDataUtil.createExampleData());
			setInitialised(true);
		}
	}
}
