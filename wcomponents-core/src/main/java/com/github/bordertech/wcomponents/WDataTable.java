package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.util.I18nUtilities;
import com.github.bordertech.wcomponents.util.InternalMessages;
import com.github.bordertech.wcomponents.util.RowIdList;
import com.github.bordertech.wcomponents.util.TreeNode;
import com.github.bordertech.wcomponents.util.Util;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>
 * The WDataTable component is used to display tabular data. It supports common functions such as sorting and pagination
 * of data.</p>
 *
 * <p>
 * The WDataTable component is only concerned with how the UI functions, not the data behind the table. In a MVC sense,
 * the WDataTable is the Controller, the view is comprised of the WDataTable layout and column renderers, and the
 * {@link TableDataModel} is the model.</p>
 *
 * <p>
 * Columns may only be added statically to the table, but individual columns can be shown/hidden per user by toggling
 * their visibility. See {@link #getColumn(int)} and {@link WComponent#setVisible(boolean)}.</p>
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 *
 * @deprecated Use {@link WTable} instead.
 */
@Deprecated
public class WDataTable extends WBeanComponent implements Disableable, Container, AjaxTarget,
		SubordinateTarget, NamingContextable {

	/**
	 * The logger instance for this class.
	 */
	private static final Log LOG = LogFactory.getLog(WDataTable.class);

	/**
	 * For easy access to the columns, including the ability to hide them all at once.
	 */
	private final WContainer columns = new WContainer();

	/**
	 * The repeater that is used to handle the repeated (row) content.
	 */
	private final WRepeater repeater = new WTableRepeater(this);

	/**
	 * Table actions, which are normally visible at the bottom of the table.
	 */
	private final WContainer actions = new WContainer();

	/**
	 * The table column for the row headers.
	 */
	private final WTableColumn rowHeaderColumn = new WTableColumn("", new DefaultWComponent());

	/**
	 * This is used to control how row selection should work.
	 */
	public enum SelectMode {
		/**
		 * Indicates that row selection is not available.
		 */
		NONE,
		/**
		 * Indicates that only a single row may be selected.
		 */
		SINGLE,
		/**
		 * Indicates that multiple rows may be selected.
		 */
		MULTIPLE
	};

	/**
	 * This is used to control how the "select all" function should work.
	 */
	public enum SelectAllType {
		/**
		 * Indicates that the select all/none function should not be available.
		 */
		NONE,
		/**
		 * Indicates that the select all/none function should is displayed as text.
		 */
		TEXT,
		/**
		 * Indicates that the select all/none function should is displayed as a control (checkbox).
		 */
		CONTROL
	};

	/**
	 * This is used to control how row expansion should work.
	 */
	public enum ExpandMode {
		/**
		 * Indicates that row expansion is not supported.
		 */
		NONE,
		/**
		 * Indicates that row expansion occurs on the client.
		 */
		CLIENT,
		/**
		 * Indicates that row expansion occurs on the server (round-trip).
		 */
		SERVER,
		/**
		 * Indicates that row expansion occurs once, via AJAX.
		 */
		LAZY,
		/**
		 * Indicates that row expansion should make an AJAX call every time.
		 */
		DYNAMIC
	};

	/**
	 * This is used to control how pagination should work.
	 */
	public enum PaginationMode {
		/**
		 * Indicates that pagination is not supported, all data will be displayed in the one page.
		 */
		NONE,
		/**
		 * Indicates that pagination occurs using a round-trip to the server (no longer implemented). NOTE: no longer
		 * supported in theme as it causes an a11y failure. Setting this mode will, in effect, set
		 * PaginationMode.DYNAMIC.
		 * @deprecated user PaginationMode.DYNAMIC
		 */
		SERVER,
		/**
		 * Indicates that pagination occurs on the client. All data will be sent at once.
		 */
		CLIENT,
		/**
		 * Indicates that pagination occurs via AJAX calls to the server.
		 */
		DYNAMIC
	};

	/**
	 * This is used to control the type of striping used, if any.
	 */
	public enum StripingType {
		/**
		 * Indicates that no zebra striping should be used.
		 */
		NONE,
		/**
		 * Indicates that zebra striping should be used to highlight rows.
		 */
		ROWS,
		/**
		 * Indicates that zebra striping should be used to highlight columns.
		 */
		COLUMNS
	};

	/**
	 * This is used to control the type of striping used, if any.
	 */
	public enum SeparatorType {
		/**
		 * Indicates that no separators should be displayed.
		 */
		NONE,
		/**
		 * Indicates that horizontal separators should be displayed.
		 */
		HORIZONTAL,
		/**
		 * Indicates that vertical separators should be displayed.
		 */
		VERTICAL,
		/**
		 * Indicates that both horizontal and vertical separators should be displayed.
		 */
		BOTH
	};

	/**
	 * This is used to control how sorting should work.
	 */
	public enum SortMode {
		/**
		 * Indicates that sorting should be disabled.
		 */
		NONE,
		/**
		 * Indicates that sorting occurs using a round-trip to the server.
		 */
		SERVER,
		/**
		 * Indicates that sorting is via AJAX calls.
		 */
		DYNAMIC
	};

	/**
	 * This is used to control how table data should be displayed.
	 */
	public enum Type {
		/**
		 * Indicates that the table should be displayed as a normal table.
		 */
		TABLE,
		/**
		 * Indicates that the table should be displayed as a hierarchical list.
		 */
		HIERARCHIC
	};

	/**
	 * Creates a WDataTable.
	 */
	public WDataTable() {
		add(rowHeaderColumn);
		add(columns);
		add(repeater);
		add(actions);

		repeater.setRepeatedComponent(new WDataTableRowRenderer(this));
		repeater.setBeanProvider(new DataModelBeanProvider(this));
	}

	/**
	 * Adds a column to the table.
	 *
	 * @param column the column to add.
	 */
	public void addColumn(final WTableColumn column) {
		columns.add(column);
		WDataTableRowRenderer renderer = (WDataTableRowRenderer) repeater.getRepeatedComponent();
		renderer.addColumn(column, columns.getChildCount() - 1);
	}

	/**
	 * Retrieves the column at the specified index. Bounds checking is not performed, see {@link #getColumnCount()}.
	 *
	 * @param index the column index.
	 * @return the column at the specified index.
	 */
	public WTableColumn getColumn(final int index) {
		return (WTableColumn) columns.getChildAt(index);
	}

	/**
	 * Returns the number of columns contained in this table. Invisible columns still count towards the total.
	 *
	 * @return the number of columns contained in this table.
	 */
	public int getColumnCount() {
		return columns.getChildCount();
	}

	/**
	 * @return the repeater used to render table rows.
	 */
	public WRepeater getRepeater() {
		return repeater;
	}

	/**
	 * @return the table column used to display row headers.
	 */
	public WTableColumn getRowHeaderColumn() {
		return rowHeaderColumn;
	}

	/**
	 * Indicates whether the WDataTable is disabled in the given context.
	 *
	 * @return true if the table is disabled, otherwise false.
	 */
	@Override
	public boolean isDisabled() {
		return isFlagSet(ComponentModel.DISABLED_FLAG);
	}

	/**
	 * Sets whether the WDataTable is disabled.
	 *
	 * @param disabled true to disable the table, false to enable it.
	 */
	@Override
	public void setDisabled(final boolean disabled) {
		setFlag(ComponentModel.DISABLED_FLAG, disabled);
	}

	/**
	 * @return Returns the dataModel.
	 */
	public TableDataModel getDataModel() {
		return getComponentModel().dataModel;
	}

	/**
	 * Sets the data model.
	 *
	 * @param dataModel The dataModel to set.
	 */
	public void setDataModel(final TableDataModel dataModel) {
		getOrCreateComponentModel().dataModel = dataModel;
		getOrCreateComponentModel().rowIndexMapping = null;

		if (dataModel instanceof BeanTableDataModel) {
			((BeanTableDataModel) dataModel).setBeanProvider(new DataTableBeanProvider(this));
			((BeanTableDataModel) dataModel).setBeanProperty(".");
		}

		if (dataModel instanceof ScrollableTableDataModel) {
			int startIndex = getCurrentPage() * getRowsPerPage();
			int endIndex = startIndex + getRowsPerPage() - 1;
			((ScrollableTableDataModel) dataModel).setCurrentRows(startIndex, endIndex);
		}

		// Flush the repeater's row contexts and scratch maps
		repeater.reset();
	}

	/**
	 * Updates the bean using the table data model's {@link TableDataModel#setValueAt(Object, int, int)} method.
	 */
	@Override
	public void updateBeanValue() {
		TableDataModel model = getDataModel();

		if (model instanceof ScrollableTableDataModel) {
			LOG.warn("UpdateBeanValue only updating the current page for ScrollableTableDataModel");
			updateBeanValueCurrentPageOnly();
		} else if (model.getRowCount() > 0) {
			// Temporarily widen the pagination on the repeater to hold all rows
			// Calling setBean with a non-null value overrides the DataTableBeanProvider
			repeater.setBean(new RowIdList(0, model.getRowCount() - 1));
			updateBeanValueCurrentPageOnly();
			repeater.setBean(null);
		}
	}

	/**
	 * Updates the bean using the table data model's {@link TableDataModel#setValueAt(Object, int, int)} method. This
	 * method only updates the data for the current page.
	 */
	private void updateBeanValueCurrentPageOnly() {
		WDataTableRowRenderer rowRenderer = (WDataTableRowRenderer) repeater.getRepeatedComponent();
		TableDataModel model = getDataModel();

		// The bean list for the repeater is a list of Integer row indices
		for (Integer rowBean : (List<Integer>) repeater.getBeanList()) {
			int row = rowBean;
			UIContext rowContext = repeater.getRowContext(rowBean, row);
			final int columnCount = getColumnCount();

			for (int col = 0; col < columnCount; col++) {
				if (model.isCellEditable(row, col)) {
					// The actual component is wrapped in a renderer wrapper, so we have to fetch it from that
					WComponent renderer = ((Container) rowRenderer.getRenderer(col)).getChildAt(0);

					if (renderer instanceof DataBound) {
						Object oldValue = model.getValueAt(row, col);

						UIContextHolder.pushContext(rowContext);
						Object newValue = null;

						try {
							newValue = ((DataBound) renderer).getData();
						} finally {
							UIContextHolder.popContext();
						}

						if (!Util.equals(oldValue, newValue)) {
							model.setValueAt(newValue, row, col);
						}
					}
				}
			}
		}
	}

	/**
	 * @return the separator type.
	 */
	public SeparatorType getSeparatorType() {
		return getComponentModel().separatorType;
	}

	/**
	 * Sets the separator used to visually separate rows or columns.
	 *
	 * @param separatorType The separator type to set.
	 */
	public void setSeparatorType(final SeparatorType separatorType) {
		getOrCreateComponentModel().separatorType = separatorType;
	}

	/**
	 * @return Returns the striping type.
	 */
	public StripingType getStripingType() {
		return getComponentModel().stripingType;
	}

	/**
	 * Sets the striping type used to highlight alternate rows or columns.
	 *
	 * @param stripingType The striping type to set.
	 */
	public void setStripingType(final StripingType stripingType) {
		getOrCreateComponentModel().stripingType = stripingType;
	}

	/**
	 * Indicates whether table column headers should be displayed.
	 *
	 * @return true if column headers should be displayed, false otherwise.
	 */
	public boolean isShowColumnHeaders() {
		return getComponentModel().showColumnHeaders;
	}

	/**
	 * Sets whether table column headers should be displayed.
	 *
	 * @param showColumnHeaders true to display table column headers, false otherwise.
	 */
	public void setShowColumnHeaders(final boolean showColumnHeaders) {
		getOrCreateComponentModel().showColumnHeaders = showColumnHeaders;
	}

	/**
	 * Indicates whether row headers should be displayed.
	 *
	 * @return true if row headers should be displayed, false otherwise.
	 */
	public boolean isShowRowHeaders() {
		return getComponentModel().showRowHeaders;
	}

	/**
	 * <p>
	 * Sets whether row headers should be displayed.</p>
	 *
	 * <p>
	 * To set the column heading for the row headers, use:
	 * {@link #getRowHeaderColumn()}.<code>getColumnLabel().setText(yourText).</code></p>
	 *
	 * @param showRowHeaders true to show row headers, false otherwise.
	 */
	public void setShowRowHeaders(final boolean showRowHeaders) {
		getOrCreateComponentModel().showRowHeaders = showRowHeaders;
	}

	/**
	 * Indicates whether row indices should be displayed.
	 *
	 * @return true if row indices should be displayed, false otherwise.
	 */
	public boolean isShowRowIndices() {
		return getComponentModel().showRowIndices;
	}

	/**
	 * Sets whether row indices should be displayed.
	 *
	 * @param showRowIndices true if row indices should be displayed, false otherwise.
	 */
	public void setShowRowIndices(final boolean showRowIndices) {
		getOrCreateComponentModel().showRowIndices = showRowIndices;
	}

	/**
	 * Indicates whether the form should submit whenever the row selection changes.
	 *
	 * @return true if form submission should occur on row selection change, false otherwise.
	 */
	public boolean isSubmitOnRowSelect() {
		return getComponentModel().submitOnRowSelect;
	}

	/**
	 * Sets whether the form should submit whenever the row selection changes.
	 *
	 * @param submitOnRowSelect true if form submission should occur on row selection change, false otherwise.
	 */
	public void setSubmitOnRowSelect(final boolean submitOnRowSelect) {
		getOrCreateComponentModel().submitOnRowSelect = submitOnRowSelect;
	}

	/**
	 * Indicates whether filtering is enabled.
	 *
	 * @return true if filtering is enabled, false otherwise.
	 */
	public boolean isFilterable() {
		return getComponentModel().filterable;
	}

	/**
	 * Sets whether filtering is enabled.
	 *
	 * @param filterable true to enable filtering, false otherwise.
	 */
	public void setFilterable(final boolean filterable) {
		getOrCreateComponentModel().filterable = filterable;
	}

	/**
	 * Sets the active filters.
	 *
	 * @param activeFilters The active filters to set.
	 */
	public void setActiveFilters(final List<String> activeFilters) {
		getOrCreateComponentModel().activeFilters = activeFilters;
	}

	/**
	 * @return the active filters.
	 */
	public List<String> getActiveFilters() {
		return getComponentModel().getActiveFilters();
	}

	/**
	 * @return the action to execute when row selection changes.
	 */
	public Action getSelectionChangeAction() {
		return getComponentModel().selectionChangeAction;
	}

	/**
	 * Sets the action to execute when row selection changes.
	 *
	 * @param selectionChangeAction the action to execute on row selection change.
	 */
	public void setSelectionChangeAction(final Action selectionChangeAction) {
		getOrCreateComponentModel().selectionChangeAction = selectionChangeAction;
	}

	/**
	 * @return the message to display when the table contains no rows.
	 */
	public String getNoDataMessage() {
		return I18nUtilities.format(null, getComponentModel().noDataMessage);
	}

	/**
	 * Sets the message to display when the table contains no rows.
	 *
	 * @param noDataMessage the no data message.
	 */
	public void setNoDataMessage(final String noDataMessage) {
		getOrCreateComponentModel().noDataMessage = noDataMessage;
	}

	/**
	 * @return the table summary text.
	 */
	public String getSummary() {
		return I18nUtilities.format(null, getComponentModel().summary);
	}

	/**
	 * Sets the table summary text.
	 *
	 * @param summary The summary to set.
	 */
	public void setSummary(final String summary) {
		getOrCreateComponentModel().summary = summary;
	}

	/**
	 * @return the table caption text.
	 */
	public String getCaption() {
		return I18nUtilities.format(null, getComponentModel().caption);
	}

	/**
	 * Sets the table caption text.
	 *
	 * @param caption The caption to set.
	 */
	public void setCaption(final String caption) {
		getOrCreateComponentModel().caption = caption;
	}

	/**
	 * @return the column order, or null if the default ordering is to be used.
	 */
	public int[] getColumnOrder() {
		return getComponentModel().columnOrder;
	}

	/**
	 * @param columnOrder the column order to set, or null to use default ordering.
	 */
	public void setColumnOrder(final int[] columnOrder) {
		if (columnOrder.length != getColumnCount()) {
			throw new IllegalArgumentException(
					"Number of column order indices must match the number of table columns");
		}

		getOrCreateComponentModel().columnOrder = columnOrder;
	}

	/**
	 * @return the pagination mode.
	 */
	public PaginationMode getPaginationMode() {
		return getComponentModel().paginationMode;
	}

	/**
	 * Sets the pagination mode.
	 *
	 * @param paginationMode The paginationMode to set.
	 */
	public void setPaginationMode(final PaginationMode paginationMode) {
		getOrCreateComponentModel().paginationMode = paginationMode;
	}

	/**
	 * @return the number of rows to display per page.
	 */
	public int getRowsPerPage() {
		return getComponentModel().rowsPerPage;
	}

	/**
	 * Sets the number of rows to display per page when pagination is enabled.
	 *
	 * @param rowsPerPage The rowsPerPage to set, greater than zero.
	 */
	public void setRowsPerPage(final int rowsPerPage) {
		if (rowsPerPage < 1) {
			throw new IllegalArgumentException(
					"Rows per page must be greater than 0, but got: " + rowsPerPage);
		}

		getOrCreateComponentModel().rowsPerPage = rowsPerPage;
	}

	/**
	 * @return the row selection mode..
	 */
	public SelectMode getSelectMode() {
		return getComponentModel().selectMode;
	}

	/**
	 * Sets the row selection mode.
	 *
	 * @param selectMode The select mode to set.
	 */
	public void setSelectMode(final SelectMode selectMode) {
		getOrCreateComponentModel().selectMode = selectMode;
	}

	/**
	 * @return Returns the selectGroup.
	 */
	public String getSelectGroup() {
		return getComponentModel().selectGroup;
	}

	/**
	 * @param selectGroup The selectGroup to set.
	 */
	public void setSelectGroup(final String selectGroup) {
		getOrCreateComponentModel().selectGroup = selectGroup;
	}

	/**
	 * @return Returns the sort mode.
	 */
	public SortMode getSortMode() {
		return getComponentModel().sortMode;
	}

	/**
	 * Sets the table sort mode. The data model controls which columns are sortable.
	 *
	 * @param sortMode The sort mode to set.
	 */
	public void setSortMode(final SortMode sortMode) {
		getOrCreateComponentModel().sortMode = sortMode;
	}

	/**
	 * @return the table type.
	 */
	public Type getType() {
		return getComponentModel().type;
	}

	/**
	 * Sets the table type.
	 *
	 * @param type the table type to set.
	 */
	public void setType(final Type type) {
		getOrCreateComponentModel().type = type;
	}

	/**
	 * Indicates how the table row "select all" function should be displayed.
	 *
	 * @return Returns the select all mode.
	 */
	public SelectAllType getSelectAllMode() {
		return getComponentModel().selectAllMode;
	}

	/**
	 * Sets how the table row "select all" function should be displayed.
	 *
	 * @param selectAllMode The select all mode to set.
	 */
	public void setSelectAllMode(final SelectAllType selectAllMode) {
		getOrCreateComponentModel().selectAllMode = selectAllMode;
	}

	/**
	 * @return the row expansion mode.
	 */
	public ExpandMode getExpandMode() {
		return getComponentModel().expandMode;
	}

	/**
	 * Sets the row expansion mode.
	 *
	 * @param expandMode The expand mode to set.
	 */
	public void setExpandMode(final ExpandMode expandMode) {
		getOrCreateComponentModel().expandMode = expandMode;
	}

	/**
	 * Indicates whether the "expand all" control should be available.
	 *
	 * @return true if the expand all control should be available, false if not.
	 */
	public boolean isExpandAll() {
		return getComponentModel().expandAll;
	}

	/**
	 * Sets whether the "expand all" control should be available.
	 *
	 * @param expandAll true if the expand-all control should be available, false if not.
	 */
	public void setExpandAll(final boolean expandAll) {
		getOrCreateComponentModel().expandAll = expandAll;
	}

	/**
	 * @return the current page.
	 */
	public int getCurrentPage() {
		// Table data may have changed.
		int currentPage = getComponentModel().currentPage;
		int maxPage = getComponentModel().getMaxPage();

		if (currentPage > maxPage) {
			currentPage = maxPage;
			setCurrentPage(maxPage);
		}

		return currentPage;
	}

	/**
	 * @param currentPage The currentPage to set.
	 */
	public void setCurrentPage(final int currentPage) {
		if (currentPage < 0) {
			throw new IllegalArgumentException("Page number must be greater than or equal to zero.");
		}

		TableModel model = getOrCreateComponentModel();
		model.currentPage = Math.min(model.getMaxPage(), currentPage);

		// Notify the table model that the page has changed
		TableDataModel dataModel = getDataModel();

		if (dataModel instanceof ScrollableTableDataModel) {
			int startIndex = getCurrentPage() * getRowsPerPage();
			int endIndex = Math.min(dataModel.getRowCount() - 1, startIndex + getRowsPerPage() - 1);
			((ScrollableTableDataModel) dataModel).setCurrentRows(startIndex, endIndex);
		}
	}

	/**
	 * @param expandedRows The expandedRows to set.
	 */
	public void setExpandedRows(final List<Integer> expandedRows) {
		getOrCreateComponentModel().expandedRows = expandedRows;
	}

	/**
	 * @return Returns the expandedRows.
	 */
	public List<Integer> getExpandedRows() {
		return getComponentModel().getExpandedRows();
	}

	/**
	 * @param selectedRows The selectedRows to set.
	 */
	public void setSelectedRows(final List<Integer> selectedRows) {
		TableModel model = getOrCreateComponentModel();

		if (selectedRows == null) {
			model.selectedRows = null;
		} else {
			model.selectedRows = new ArrayList<>(selectedRows);
			Collections.sort(model.selectedRows);
		}
	}

	/**
	 * @return the list of selected row indices, will not be null.
	 */
	public List<Integer> getSelectedRows() {
		return getComponentModel().getSelectedRows();
	}

	/**
	 * For rendering purposes only - has no effect on model.
	 *
	 * @param index the sort column index, or -1 for no sort.
	 * @param ascending true for ascending order, false for descending
	 */
	protected void setSort(final int index, final boolean ascending) {
		TableModel model = getOrCreateComponentModel();
		model.sortColIndex = index;
		model.sortAscending = ascending;
	}

	/**
	 * @return true if the table is currently sorted
	 */
	public boolean isSorted() {
		return getComponentModel().sortColIndex >= 0;
	}

	/**
	 * @return the index of the column the table is sorted by.
	 */
	public int getSortColumnIndex() {
		return getComponentModel().sortColIndex;
	}

	/**
	 * Indicates whether the sort on this table is ascending. Note that a return value of false does not necessarily
	 * indicate a descending sort - see {@link #isSorted()}.
	 *
	 * @return true if the sort order is ascending, false for descending.
	 */
	public boolean isSortAscending() {
		return getComponentModel().sortAscending;
	}

	/**
	 * Indicates whether the table supports sorting.
	 *
	 * @return true if the table and model both support sorting, false otherwise.
	 */
	public boolean isSortable() {
		// First check global override which turns sorting off
		if (getSortMode() == SortMode.NONE) {
			return false;
		}

		// Otherwise, the table is sortable if at least one column is sortable.
		TableDataModel dataModel = getDataModel();
		final int columnCount = getColumnCount();

		for (int i = 0; i < columnCount; i++) {
			if (dataModel.isSortable(i)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Retrieves the actions for the table.
	 *
	 * @return the list of table actions
	 */
	public List<WButton> getActions() {
		final int numActions = actions.getChildCount();
		List<WButton> buttons = new ArrayList<>(numActions);

		for (int i = 0; i < numActions; i++) {
			WButton button = (WButton) actions.getChildAt(i);
			buttons.add(button);
		}

		return Collections.unmodifiableList(buttons);
	}

	/**
	 * Adds a component to the set of table actions.
	 *
	 * @param button the button to add.
	 */
	public void addAction(final WButton button) {
		actions.add(button);
	}

	/**
	 * Adds a constraint to when the given action can be used.
	 *
	 * @param button the button which the constraint applies to.
	 * @param constraint the constraint to add.
	 */
	public void addActionConstraint(final WButton button, final ActionConstraint constraint) {
		if (button.getParent() != actions) {
			throw new IllegalArgumentException(
					"Can only add a constraint to a button which is in this table's actions");
		}

		getOrCreateComponentModel().addActionConstraint(button, constraint);
	}

	/**
	 * Retrieves the constraints for the given action.
	 *
	 * @param button the button to retrieve the constraints for.
	 * @return the constraints for the given action, or null if there are no constraints.
	 */
	public List<ActionConstraint> getActionConstraints(final WButton button) {
		List<ActionConstraint> constraints = getComponentModel().actionConstraints.get(button);
		return constraints == null ? null : Collections.unmodifiableList(constraints);
	}

	/**
	 * Override handleRequest to add table-specific functionality such as pagination and row selection.
	 *
	 * @param request the request being responded to.
	 */
	@Override
	public void handleRequest(final Request request) {
		super.handleRequest(request);

		if (isPresent(request)) {
			if (getExpandMode() != ExpandMode.NONE) {
				handleExpansionRequest(request);
			}

			if (getSelectMode() != SelectMode.NONE) {
				handleSelectionRequest(request);
			}

			if (getPaginationMode() != PaginationMode.NONE) {
				handlePaginationRequest(request);
			}

			if (isFilterable()) {
				handleFilterRequest(request);
			}

			if (isSortable()) {
				handleSortRequest(request);
			}
		}
	}

	/**
	 * Indicates whether this table was present in the request.
	 *
	 * @param request the request being responded to.
	 * @return true if this table was present in the request, false if not.
	 */
	protected boolean isPresent(final Request request) {
		return request.getParameter(getId() + "-h") != null;
	}

	/**
	 * Handles a request containing sort instruction data.
	 *
	 * @param request the request containing sort instruction data.
	 */
	private void handleSortRequest(final Request request) {
		String sortColStr = request.getParameter(getId() + ".sort");
		String sortDescStr = request.getParameter(getId() + ".sortDesc");

		if (sortColStr != null) {
			if ("".equals(sortColStr)) {
				// Reset sort
				setSort(-1, false);
				getOrCreateComponentModel().rowIndexMapping = null;
			} else {
				try {
					int sortCol = Integer.parseInt(sortColStr);
					boolean sortAsc = !"true".equalsIgnoreCase(sortDescStr);

					// Only process the sort request if it differs from the current sort order
					if (sortCol != getSortColumnIndex() || sortAsc != isSortAscending()) {
						sort(sortCol, sortAsc);
						setFocussed();
					}
				} catch (NumberFormatException e) {
					LOG.warn("Invalid sort column: " + sortColStr);
				}
			}
		}
	}

	/**
	 * Sort the table data by the specified column.
	 *
	 * @param sortCol the column to sort
	 * @param sortAsc true if sort ascending, otherwise sort descending
	 */
	public void sort(final int sortCol, final boolean sortAsc) {
		int[] rowIndexMappings = getDataModel().sort(sortCol, sortAsc);
		getOrCreateComponentModel().rowIndexMapping = rowIndexMappings;

		setSort(sortCol, sortAsc);

		if (rowIndexMappings == null) {
			// There's no way to correlate the previously selected row indices
			// with the new order of rows, so we need to clear out the selection.
			setSelectedRows(null);
			setExpandedRows(null);
		}
	}

	/**
	 * Handles a request containing filtering data.
	 *
	 * @param request the request containing filtering data.
	 */
	private void handleFilterRequest(final Request request) {
		String[] paramValues = request.getParameterValues(getId() + ".filters");

		if (paramValues == null) {
			setActiveFilters(new ArrayList<String>(0));
		} else {
			List<String> filters = Arrays.asList(paramValues);
			setActiveFilters(filters);
		}
	}

	/**
	 * Handles a request containing row selection data.
	 *
	 * @param request the request containing row selection data.
	 */
	private void handleSelectionRequest(final Request request) {
		String[] paramValue = request.getParameterValues(getId() + ".selected");

		if (paramValue == null) {
			paramValue = new String[0];
		}

		String[] selectedRows = removeEmptyStrings(paramValue);
		List<Integer> oldSelections = getSelectedRows();
		List<Integer> newSelections;

		boolean singleSelect = SelectMode.SINGLE.equals(getSelectMode());

		if (getDataModel().getRowCount() == 0) {
			newSelections = new ArrayList<>();
			selectedRows = new String[0];
		} else if (getPaginationMode() == PaginationMode.NONE
				|| getPaginationMode() == PaginationMode.CLIENT
				|| oldSelections == null) {
			newSelections = new ArrayList<>(selectedRows.length);
		} else if (singleSelect && selectedRows.length > 0) {
			// For single-select, we need to remove the old entries
			newSelections = new ArrayList<>(1);
		} else {
			// For multi-select, we need to entries for the current page only
			newSelections = new ArrayList<>(oldSelections);

			int startRow = getCurrentPageStartRow();
			int endRow = getCurrentPageEndRow();
			newSelections.removeAll(getRowIds(startRow, endRow));
		}

		for (String selectedRow : selectedRows) {
			try {
				newSelections.add(Integer.parseInt(selectedRow));

				if (singleSelect) {
					break;
				}
			} catch (NumberFormatException e) {
				LOG.warn("Invalid row id for selection: " + selectedRow);
			}
		}

		setSelectedRows(newSelections);

		// If there is a selection change action specified, it may need to be fired
		Action selectionChangeAction = getSelectionChangeAction();

		if (selectionChangeAction != null && !newSelections.equals(oldSelections)) {
			selectionChangeAction.execute(new ActionEvent(this, "selection"));
		}
	}

	/**
	 * Retrieves the starting row index for the current page. Will always return zero for tables which are not
	 * paginated.
	 *
	 * @return the starting row index for the current page.
	 */
	private int getCurrentPageStartRow() {
		int startRow = 0;

		if (getPaginationMode() != PaginationMode.NONE) {
			int rowsPerPage = getRowsPerPage();
			TableDataModel model = getDataModel();

			if (model instanceof TreeTableDataModel) {
				// For tree tables, pagination only occurs on first-level nodes (ie. those
				// underneath the root node), however they might not be consecutively
				// numbered. Therefore, the start and end row indices need to be adjusted.
				TreeTableDataModel treeModel = (TreeTableDataModel) model;
				TreeNode root = treeModel.getNodeAtLine(0).getRoot();

				int startNode = getCurrentPage() * rowsPerPage;
				startRow = ((TableTreeNode) root.getChildAt(startNode)).getRowIndex() - 1; // -1 as the root is not included in the table
			} else {
				startRow = getCurrentPage() * rowsPerPage;
			}
		}

		return startRow;
	}

	/**
	 * Retrieves the ending row index for the current page. Will always return the row count minus 1 for tables which
	 * are not paginated.
	 *
	 * @return the starting row index for the current page.
	 */
	private int getCurrentPageEndRow() {
		TableDataModel model = getDataModel();
		int rowsPerPage = getRowsPerPage();
		int endRow = model.getRowCount() - 1;

		if (getPaginationMode() != PaginationMode.NONE) {
			if (model instanceof TreeTableDataModel) {
				// For tree tables, pagination only occurs on first-level nodes (ie. those
				// underneath the root node), however they might not be consecutively
				// numbered. Therefore, the start and end row indices need to be adjusted.
				TreeTableDataModel treeModel = (TreeTableDataModel) model;
				TreeNode root = treeModel.getNodeAtLine(0).getRoot();

				int endNode = Math.min(root.getChildCount() - 1,
						(getCurrentPage() + 1) * rowsPerPage - 1);

				endRow = ((TableTreeNode) root.getChildAt(endNode)).getRowIndex() - 1 // -1 as the root is not included in the table
						+ ((TableTreeNode) root.getChildAt(endNode)).getNodeCount();
			} else {
				endRow = Math.min(model.getRowCount() - 1, (getCurrentPage() + 1) * rowsPerPage - 1);
			}
		}

		return endRow;
	}

	/**
	 * Handles a request containing row expansion data.
	 *
	 * @param request the request containing row expansion data.
	 */
	private void handleExpansionRequest(final Request request) {
		String[] paramValue = request.getParameterValues(getId() + ".expanded");

		if (paramValue == null) {
			paramValue = new String[0];
		}

		String[] expandedRows = removeEmptyStrings(paramValue);

		List<Integer> oldExpansions = getExpandedRows();
		List<Integer> expansions;

		TableDataModel model = getDataModel();

		if (model.getRowCount() == 0) {
			setExpandedRows(new ArrayList<Integer>());
			return;
		} else if (getPaginationMode() == PaginationMode.NONE
				|| getPaginationMode() == PaginationMode.CLIENT
				|| oldExpansions == null) {
			expansions = new ArrayList<>(expandedRows.length);
		} else {
			// row expansions only apply to the current page
			expansions = new ArrayList<>(oldExpansions);

			int startRow = getCurrentPageStartRow();
			int endRow = getCurrentPageEndRow();
			expansions.removeAll(getRowIds(startRow, endRow));
		}

		for (String expandedRow : expandedRows) {
			try {
				expansions.add(Integer.parseInt(expandedRow));
			} catch (NumberFormatException e) {
				LOG.warn("Invalid row id for expansion: " + expandedRow);
			}
		}

		// For tree tables, we also have to tell the nodes to expand themselves
		if (model instanceof TreeTableDataModel) {
			TreeTableDataModel treeModel = (TreeTableDataModel) model;

			// We need the expanded indices sorted, as expanding/collapsing sections alters row indices
			Collections.sort(expansions);

			for (int row = 0; row < treeModel.getRowCount(); row++) {
				for (Iterator<TreeNode> i = treeModel.getNodeAtLine(row).depthFirst(); i.hasNext();) {
					TableTreeNode node = (TableTreeNode) i.next();
					node.setExpanded(false);
				}
			}

			for (int i = expansions.size() - 1; i >= 0; i--) {
				treeModel.getNodeAtLine(expansions.get(i)).setExpanded(true);
			}
		}

		setExpandedRows(expansions);
	}

	/**
	 * Handles a request containing pagination data.
	 *
	 * @param request the request containing a pagination data.
	 */
	private void handlePaginationRequest(final Request request) {
		String pageStr = request.getParameter(getId() + ".page");

		if (pageStr == null) {
			setCurrentPage(0);
		} else {
			try {
				int pageNum = Integer.parseInt(pageStr);
				setCurrentPage(pageNum);
			} catch (NumberFormatException e) {
				LOG.warn("Invalid page number: " + pageStr);
			}
		}
	}

	/**
	 * Helper that removes empty/null string from the <code>original</code> string array.
	 *
	 * @param originals The string array from which the null/empty strings should be removed from.
	 * @return Array of non empty strings from the <code>original</code> string array.
	 */
	private String[] removeEmptyStrings(final String[] originals) {
		if (originals == null) {
			return null;
		} else {
			List<String> parsed = new ArrayList<>();
			for (String original : originals) {
				if (original != null && original.length() > 0) {
					parsed.add(original);
				}
			}

			return parsed.toArray(new String[parsed.size()]);
		}
	}

	/**
	 * Override preparePaint to register an AJAX operation if necessary.
	 *
	 * @param request the request being responded to.
	 */
	@Override
	protected void preparePaintComponent(final Request request) {
		super.preparePaintComponent(request);

		if (PaginationMode.DYNAMIC.equals(getPaginationMode())
				|| SortMode.DYNAMIC.equals(getSortMode())
				|| ExpandMode.DYNAMIC.equals(getExpandMode())
				|| ExpandMode.LAZY.equals(getExpandMode())) {
			AjaxHelper.registerComponentTargetItself(getId(), request);
		}
	}

	/**
	 * A naming context is only considered active if it has been set active via {@link #setNamingContext(boolean)} and
	 * also has an id name set via {@link #setIdName(String)}.
	 *
	 * @param context set true if this is a naming context.
	 */
	public void setNamingContext(final boolean context) {
		setFlag(ComponentModel.NAMING_CONTEXT_FLAG, context);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isNamingContext() {
		return isFlagSet(ComponentModel.NAMING_CONTEXT_FLAG);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getNamingContextId() {
		return getId();
	}

	/**
	 * Describes a constraint on a table action.
	 *
	 * @author Yiannis Paschalidis
	 */
	public static final class ActionConstraint implements Serializable {

		/**
		 * The minimum number of rows which must be selected to fulfil the constraint.
		 */
		private int minSelectedRowCount;

		/**
		 * The maximum number of rows which must be selected to fulfil the constraint.
		 */
		private int maxSelectedRowCount;

		/**
		 * True if the constaint is an error, false for a warning.
		 */
		private boolean error;

		/**
		 * The message to display when the constraint is not met.
		 */
		private String message;

		/**
		 * Creates an action constraint.
		 *
		 * @param minSelectedRowCount the minimum number of rows which must be selected to fulfil the constraint, or
		 * zero for any number of rows.
		 * @param maxSelectedRowCount the maximum number of rows which can be selected to fulfil the constraint, or zero
		 * for any number of rows.
		 * @param error true if the constaint is an error, false for a warning.
		 * @param message the message to display when the constraint is not met.
		 */
		public ActionConstraint(final int minSelectedRowCount, final int maxSelectedRowCount,
				final boolean error, final String message) {
			this.minSelectedRowCount = minSelectedRowCount;
			this.maxSelectedRowCount = maxSelectedRowCount;
			this.error = error;
			this.message = message;
		}

		/**
		 * Indicates the minimum number of rows which must be selected for the error/warning not to occur.
		 *
		 * @return the minimum selected row count.
		 */
		public int getMinSelectedRowCount() {
			return minSelectedRowCount;
		}

		/**
		 * @param minSelectedRowCount The minimum selected row count to set.
		 */
		public void setMinSelectedRowCount(final int minSelectedRowCount) {
			this.minSelectedRowCount = minSelectedRowCount;
		}

		/**
		 * Indicates the maximum number of rows which can be selected for the error/warning not to occur.
		 *
		 * @return the maximum selected row count.
		 */
		public int getMaxSelectedRowCount() {
			return maxSelectedRowCount;
		}

		/**
		 * @param maxSelectedRowCount The maximum selected row count to set.
		 */
		public void setMaxSelectedRowCount(final int maxSelectedRowCount) {
			this.maxSelectedRowCount = maxSelectedRowCount;
		}

		/**
		 * Indicates whether the constraint is an error or warning.
		 *
		 * @return true if the constaint is an error, false for a warning.
		 */
		public boolean isError() {
			return error;
		}

		/**
		 * Sets whether the constraint is an error or warning.
		 *
		 * @param error true if the constaint is an error, false for a warning.
		 */
		public void setError(final boolean error) {
			this.error = error;
		}

		/**
		 * @return Returns the message.
		 */
		public String getMessage() {
			return message;
		}

		/**
		 * @param message The message to set.
		 */
		public void setMessage(final String message) {
			this.message = message;
		}
	}

	/**
	 * A bean provider implementation which uses the bean bound to the table.
	 */
	private static final class DataTableBeanProvider implements BeanProvider, Serializable {

		private final WDataTable table;

		/**
		 * @param table the parent table
		 */
		private DataTableBeanProvider(final WDataTable table) {
			this.table = table;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Object getBean(final BeanProviderBound beanProviderBound) {
			return table.getBeanValue();
		}
	}

	/**
	 * A bean provider implementation which provides beans to the table repeater. This provider takes the table's
	 * pagination state into account, so that only visible rows are rendered.
	 */
	private static final class DataModelBeanProvider implements BeanProvider, Serializable {

		private final WDataTable table;

		/**
		 * @param table the parent table
		 */
		private DataModelBeanProvider(final WDataTable table) {
			this.table = table;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Object getBean(final BeanProviderBound beanProviderBound) {
			TableDataModel dataModel = table.getDataModel();

			if (dataModel.getRowCount() == 0) {
				return Collections.emptyList();
			}

			int startIndex = 0;
			int endIndex = dataModel.getRowCount() - 1;

			switch (table.getPaginationMode()) {
				case DYNAMIC:
				case SERVER: {
					int rowsPerPage = table.getRowsPerPage();
					int currentPage = table.getCurrentPage();
					int rowCount = table.getComponentModel().getPaginationRowCount();

					startIndex = Math.min(currentPage * rowsPerPage,
							rowCount - (rowCount % rowsPerPage));
					endIndex = Math.min(startIndex + rowsPerPage, rowCount) - 1;

					if (dataModel instanceof TreeTableDataModel) {
						// Adjust indices (child node index --> table row index).
						TreeNode rootNode = ((TreeTableDataModel) dataModel).getNodeAtLine(0).
								getParent();
						TableTreeNode startNode = (TableTreeNode) rootNode.getChildAt(startIndex);
						TableTreeNode endNode = (TableTreeNode) rootNode.getChildAt(endIndex);
						startIndex = startNode.getRowIndex() - 1;
						endIndex = endNode.getRowIndex() + endNode.getNodeCount() - 1;
					}

					break;
				}
			}

			if (endIndex < startIndex) {
				// No data
				return Collections.EMPTY_LIST;
			}

			return table.getRowIds(startIndex, endIndex);
		}
	}

	/**
	 * Determine the row ids for the provided index range.
	 *
	 * @param startIndex the startIndex
	 * @param endIndex the endIndex
	 * @return the list of rowIds for the provided index range
	 */
	private List<Integer> getRowIds(final int startIndex, final int endIndex) {
		// If the table is sorted, we may require a mapping for table row index <--> data model index.
		int[] rowIndexMapping = getComponentModel().rowIndexMapping;

		// Check if sort mapping needs updating
		if (isSorted() && rowIndexMapping != null && rowIndexMapping.length != getDataModel().
				getRowCount()) {
			rowIndexMapping = getDataModel().sort(getSortColumnIndex(), isSortAscending());
			getOrCreateComponentModel().rowIndexMapping = rowIndexMapping;
		}

		if (rowIndexMapping == null) {
			// No mapping, return from startIndex to endIndex
			return new RowIdList(startIndex, endIndex);
		} else {
			List<Integer> rowIds = new ArrayList<>(endIndex - startIndex + 1);

			for (int i = startIndex; i <= endIndex; i++) {
				rowIds.add(rowIndexMapping[i]);
			}

			return rowIds;
		}

	}

	/**
	 * Contains the table's UI state.
	 *
	 * @author Yiannis Paschalidis
	 */
	public static final class TableModel extends BeanAndProviderBoundComponentModel {

		/**
		 * This controls how sorting should function. Sortability is determined by the data model.
		 */
		private SortMode sortMode = SortMode.SERVER;

		/**
		 * The data model for the table.
		 */
		private TableDataModel dataModel = EmptyTableDataModel.INSTANCE;

		/**
		 * Controls whether backgound striping is used to distinguish rows/columns from each other.
		 */
		private StripingType stripingType = StripingType.NONE;

		/**
		 * Controls whether a visual separator is used to distinguish rows/columns from each other.
		 */
		private SeparatorType separatorType = SeparatorType.NONE;

		/**
		 * This flag indicates whether row headers should be displayed.
		 */
		private boolean showRowHeaders = false;

		/**
		 * This flag indicates whether column headers should be displayed.
		 */
		private boolean showColumnHeaders = true;

		/**
		 * This flag indicates whether row indices should be displayed.
		 */
		private boolean showRowIndices = false;

		/**
		 * The action to execute when the table's row selection changes.
		 */
		private Action selectionChangeAction;

		/**
		 * The text to display when the table contains no data.
		 */
		private String noDataMessage = InternalMessages.DEFAULT_NO_TABLE_DATA;

		/**
		 * The table summary text.
		 */
		private String summary;

		/**
		 * The table caption text.
		 */
		private String caption;

		/**
		 * The column order, only used for re-ordering columns.
		 */
		private int[] columnOrder;

		/**
		 * This is used to control how table data should be displayed.
		 */
		private Type type = Type.TABLE;

		// Pagination
		/**
		 * Indicates how pagination should occur.
		 */
		private PaginationMode paginationMode = PaginationMode.NONE;

		/**
		 * Indicates how many rows to display per page.
		 */
		private int rowsPerPage = 10;

		/**
		 * Stores the current page index.
		 */
		private int currentPage;

		// Selection
		/**
		 * Indicates how row selection should function.
		 */
		private SelectMode selectMode = SelectMode.NONE;
		/**
		 * The select group.
		 */
		private String selectGroup;

		/**
		 * Indicates how the "select all" control should appear.
		 */
		private SelectAllType selectAllMode = SelectAllType.TEXT;

		/**
		 * Holds the currently selected row indices.
		 */
		private List<Integer> selectedRows;

		/**
		 * Indicates whether the client should round-trip every time a row is selected.
		 */
		private boolean submitOnRowSelect = false;

		// Row expansion
		/**
		 * Indicates how row epansion should function.
		 */
		private ExpandMode expandMode = ExpandMode.NONE;

		/**
		 * Indicates whether a "expand/collapse all" control should be displayed.
		 */
		private boolean expandAll = false;

		/**
		 * Holds the currently expanded row indices.
		 */
		private List<Integer> expandedRows;

		// Filtering
		/**
		 * Indicates whether the table should allow client-side filtering of rows.
		 */
		private boolean filterable = false;

		/**
		 * Holds the currently active row filters.
		 */
		private List<String> activeFilters;

		// Sorting
		/**
		 * Holds the currently sorted column index. A value of -1 indicates no active sort.
		 */
		private int sortColIndex = -1;

		/**
		 * Indicates whether the sort is ascending (true) or descending (false).
		 */
		private boolean sortAscending;

		/**
		 * This is used to map rendered table row indices to table model row indices, if the table model supports this
		 * mode of sorting.
		 */
		private int[] rowIndexMapping;

		// Action constraints
		/**
		 * This map holds the action constraints per table action (button).
		 */
		private final Map<WComponent, List<ActionConstraint>> actionConstraints = new HashMap<>();

		/**
		 * @return the maximum page number.
		 */
		private int getMaxPage() {
			int rowCount = getPaginationRowCount();

			if (rowCount == 0) {
				return 0;
			} else {
				return rowCount / rowsPerPage - (rowCount % rowsPerPage == 0 ? 1 : 0);
			}
		}

		/**
		 * The pagination row count takes into account different pagination types. For example, hierarchical tables only
		 * paginate on top-level nodes (to avoid orphan nodes).
		 *
		 * @return the row count for pagination
		 */
		private int getPaginationRowCount() {
			if (dataModel instanceof TreeTableDataModel) {
				// For tree tables, we only include top-level nodes for pagination.
				TreeNode firstNode = ((TreeTableDataModel) dataModel).getNodeAtLine(0);
				return firstNode == null ? 0 : firstNode.getParent().getChildCount();
			} else {
				return dataModel.getRowCount();
			}
		}

		/**
		 * Adds a constraint to the list of constraints for the given button.
		 *
		 * @param button the button to add the constraint for.
		 * @param constraint the constraint to add.
		 */
		public void addActionConstraint(final WButton button, final ActionConstraint constraint) {
			List<ActionConstraint> constraintForComponent = actionConstraints.get(button);

			if (constraintForComponent == null) {
				constraintForComponent = new ArrayList<>();
				actionConstraints.put(button, constraintForComponent);
			}

			constraintForComponent.add(constraint);
		}

		/**
		 * @return a list of expanded row indices, will not be null.
		 */
		private List<Integer> getExpandedRows() {
			if (expandedRows == null) {
				return Collections.emptyList();
			} else {
				return Collections.unmodifiableList(expandedRows);
			}
		}

		/**
		 * @return a list of selected row indices, will not be null.
		 */
		private List<Integer> getSelectedRows() {
			if (selectedRows == null) {
				return Collections.emptyList();
			} else {
				return Collections.unmodifiableList(selectedRows);
			}
		}

		/**
		 * @return a list of active filters, will not be null.
		 */
		private List<String> getActiveFilters() {
			if (activeFilters == null) {
				return Collections.emptyList();
			} else {
				return Collections.unmodifiableList(activeFilters);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override // to make public
	public int getChildCount() {
		return super.getChildCount();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override // to make public
	public WComponent getChildAt(final int index) {
		return super.getChildAt(index);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override // to make public
	public int getIndexOfChild(final WComponent childComponent) {
		return super.getIndexOfChild(childComponent);
	}

	@Override
	public List<WComponent> getChildren() {
		return super.getChildren();
	}

	/**
	 * @return a String representation of this component, for debugging purposes.
	 */
	@Override
	public String toString() {
		TableDataModel model = getDataModel();
		return toString(model.getClass().getSimpleName() + ", " + model.getRowCount() + " rows", -1,
				-1);
	}

	/**
	 * Creates a new component model.
	 *
	 * @return a new TableModel.
	 */
	@Override
	protected TableModel newComponentModel() {
		return new TableModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override // for type safety only
	protected TableModel getComponentModel() {
		return (TableModel) super.getComponentModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override // for type safety only
	protected TableModel getOrCreateComponentModel() {
		return (TableModel) super.getOrCreateComponentModel();
	}
}
