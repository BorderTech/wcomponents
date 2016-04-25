package com.github.bordertech.wcomponents.examples.table;

import com.github.bordertech.wcomponents.Action;
import com.github.bordertech.wcomponents.ActionEvent;
import com.github.bordertech.wcomponents.AjaxTarget;
import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.SimpleBeanBoundTableModel;
import com.github.bordertech.wcomponents.WAjaxControl;
import com.github.bordertech.wcomponents.WBeanContainer;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WConfirmationButton;
import com.github.bordertech.wcomponents.WDateField;
import com.github.bordertech.wcomponents.WDecoratedLabel;
import com.github.bordertech.wcomponents.WMessages;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WStyledText;
import com.github.bordertech.wcomponents.WTable;
import com.github.bordertech.wcomponents.WTable.PaginationMode;
import com.github.bordertech.wcomponents.WTable.SortMode;
import com.github.bordertech.wcomponents.WTableColumn;
import com.github.bordertech.wcomponents.WTextField;
import com.github.bordertech.wcomponents.util.TableUtil;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This example demonstrates an editable table.
 * <p>
 * It shows how inline editing via AJAX actions can be achieved on each row.
 * </p>
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class TableRowEditingAjaxExample extends WPanel {

	/**
	 * Messages for actions.
	 */
	private final WMessages messages = new WMessages();

	/**
	 * The table used in the example.
	 */
	private final WTable table = new WTable() {
		@Override
		protected String getRowIdName(final List<Integer> rowIndex, final Object rowKey) {
			PersonBean bean = (PersonBean) rowKey;
			return bean.getPersonId();
		}
	};
	/**
	 * First name text field.
	 */
	private final WTextField firstNameField = new WTextField();
	/**
	 * Last name text field.
	 */
	private final WTextField lastNameField = new WTextField();
	/**
	 * Date of birth text field.
	 */
	private final WDateField dobField = new WDateField();
	/**
	 * Action button container.
	 */
	private final WBeanContainer actionContainer = new WBeanContainer();

	/**
	 * Create example.
	 */
	public TableRowEditingAjaxExample() {
		table.setIdName("myTable");
		firstNameField.setIdName("fn");
		lastNameField.setIdName("ln");
		dobField.setIdName("dob");

		add(messages);

		// Setup table
		add(table);
		table.setEditable(true);
		table.setSortMode(SortMode.DYNAMIC);
		table.setPaginationMode(PaginationMode.DYNAMIC);
		table.setRowsPerPage(3);

		// Column - First name
		firstNameField.setAccessibleText("First name");
		WDecoratedLabel label = new WDecoratedLabel("First name");
		label.setIdName("col_fn");
		table.addColumn(new WTableColumn(label, firstNameField));

		// Column - Last name
		lastNameField.setAccessibleText("last name");
		label = new WDecoratedLabel("Last name");
		label.setIdName("col_ln");
		table.addColumn(new WTableColumn(label, lastNameField));

		// Column - Date field
		dobField.setAccessibleText("Date of birth");
		label = new WDecoratedLabel("Date of birth");
		label.setIdName("col_dob");
		table.addColumn(new WTableColumn(label, dobField));

		// Column - Action buttons
		label = new WDecoratedLabel("Action");
		label.setIdName("col_action");
		WTableColumn column = new WTableColumn(label, actionContainer);
		column.setWidth(5);
		table.addColumn(column);
		setUpActionButtons();

		// Setup model
		SimpleBeanBoundTableModel model = new SimpleBeanBoundTableModel(
				new String[]{"firstName", "lastName",
					"dateOfBirth", "."}) {
			@Override
			public boolean isCellEditable(final List<Integer> row, final int col) {
				Object key = getRowKey(row);
				return isEditRow(key);
			}
		};
		model.setEditable(true);
		model.setComparator(0, SimpleBeanBoundTableModel.COMPARABLE_COMPARATOR);
		model.setComparator(1, SimpleBeanBoundTableModel.COMPARABLE_COMPARATOR);
		model.setComparator(2, SimpleBeanBoundTableModel.COMPARABLE_COMPARATOR);
		table.setTableModel(model);

		// Create a button to update bean
		WButton saveButton = new WButton("Save data");
		saveButton.setAction(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				table.updateBeanValue();
			}
		});
		table.addAction(saveButton);

		// Create an add button
		WButton addButton = new WButton("Add row");
		addButton.setAction(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				List<PersonBean> beans = (List<PersonBean>) table.getBean();
				beans.add(new PersonBean(String.valueOf(new Date().getTime()), "New First",
						"New Last", new Date()));
				table.handleDataChanged();
			}
		});
		table.addAction(addButton);

		// Refresh button
		WButton refreshButton = new WButton("Refresh");
		table.addAction(refreshButton);

		// Create a component to display the table data in text format
		final WStyledText dataOutput = new WStyledText() {
			@Override
			public String getText() {
				StringBuffer buf = new StringBuffer("Saved data:\n");

				for (PersonBean person : (List<PersonBean>) table.getBean()) {
					buf.append(person.toString());
					buf.append('\n');
				}

				return buf.toString();
			}
		};
		dataOutput.setWhitespaceMode(WStyledText.WhitespaceMode.PRESERVE);
		add(dataOutput);

		saveButton.setIdName("save_btn");
		addButton.setIdName("add_btn");
		refreshButton.setIdName("refresh_btn");

	}

	/**
	 * Setup the action buttons and ajax controls in the action column.
	 */
	private void setUpActionButtons() {
		// Buttons Panel
		WPanel buttonPanel = new WPanel();
		actionContainer.add(buttonPanel);

		// Edit Button
		final WButton editButton = new WButton("Edit") {
			@Override
			public boolean isVisible() {
				Object key = TableUtil.getCurrentRowKey();
				return !isEditRow(key);
			}
		};
		editButton.setImage("/image/pencil.png");
		editButton.setRenderAsLink(true);
		editButton.setToolTip("Edit");
		editButton.setAction(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				Object key = TableUtil.getCurrentRowKey();
				addEditRow(key);
			}
		});

		// Cancel Button
		final WButton cancelButton = new WButton("Cancel") {
			@Override
			public boolean isVisible() {
				Object key = TableUtil.getCurrentRowKey();
				return isEditRow(key);
			}
		};
		cancelButton.setImage("/image/cancel.png");
		cancelButton.setRenderAsLink(true);
		cancelButton.setToolTip("Cancel");
		cancelButton.setAction(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				Object key = TableUtil.getCurrentRowKey();
				removeEditRow(key);
				firstNameField.reset();
				lastNameField.reset();
				dobField.reset();
			}
		});

		// Delete Button
		WConfirmationButton deleteButton = new WConfirmationButton("Delete") {
			@Override
			public boolean isVisible() {
				Object key = TableUtil.getCurrentRowKey();
				return !isEditRow(key);
			}
		};
		deleteButton.setMessage("Do you want to delete row?");
		deleteButton.setImage("/image/remove.png");
		deleteButton.setRenderAsLink(true);
		deleteButton.setToolTip("Delete");
		deleteButton.setAction(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				Object key = TableUtil.getCurrentRowKey();
				removeEditRow(key);
				PersonBean bean = (PersonBean) actionContainer.getBean();
				List<PersonBean> beans = (List<PersonBean>) table.getBean();
				beans.remove(bean);
				table.handleDataChanged();
				messages.success(bean.getFirstName() + " " + bean.getLastName() + " removed.");
			}
		});

		buttonPanel.add(editButton);
		buttonPanel.add(cancelButton);
		buttonPanel.add(deleteButton);

		// Ajax - edit button
		WAjaxControl editAjax = new WAjaxControl(editButton,
				new AjaxTarget[]{firstNameField, lastNameField,
					dobField, buttonPanel}) {
			@Override
			public boolean isVisible() {
				return editButton.isVisible();
			}

		};
		buttonPanel.add(editAjax);

		// Ajax - cancel button
		WAjaxControl cancelAjax = new WAjaxControl(cancelButton,
				new AjaxTarget[]{firstNameField, lastNameField,
					dobField, buttonPanel}) {
			@Override
			public boolean isVisible() {
				return cancelButton.isVisible();
			}
		};
		buttonPanel.add(cancelAjax);

		buttonPanel.setIdName("buttons");
		editAjax.setIdName("ajax_edit");
		cancelAjax.setIdName("ajax_can");
		editButton.setIdName("edit_btn");
		cancelButton.setIdName("cancel_btn");
		deleteButton.setIdName("delete_btn");
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
			table.setBean(ExampleDataUtil.createExampleData());
			setInitialised(true);
		}
	}

	/**
	 * @param rowKey row key to include in edits.
	 */
	public void addEditRow(final Object rowKey) {
		MyModel model = getOrCreateComponentModel();
		if (model.editRows == null) {
			model.editRows = new HashSet<>();
		}
		model.editRows.add(rowKey);
	}

	/**
	 * @param rowKey the row key to remove from the edits.
	 */
	public void removeEditRow(final Object rowKey) {
		MyModel model = getOrCreateComponentModel();
		if (model.editRows != null) {
			model.editRows.remove(rowKey);
		}
	}

	/**
	 * @param key the row key to test
	 * @return true if row key is being edited
	 */
	public boolean isEditRow(final Object key) {
		MyModel model = getComponentModel();
		return model.editRows != null && model.editRows.contains(key);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected MyModel newComponentModel() {
		return new MyModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected MyModel getComponentModel() {
		return (MyModel) super.getComponentModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected MyModel getOrCreateComponentModel() {
		return (MyModel) super.getOrCreateComponentModel();
	}

	/**
	 * Model to hold the edit row keys.
	 */
	public static class MyModel extends PanelModel {

		/**
		 * Edit rows.
		 */
		private Set<Object> editRows;
	}

}
