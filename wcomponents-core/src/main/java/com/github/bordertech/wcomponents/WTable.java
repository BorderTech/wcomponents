package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.AdapterBasicTableModel.BasicTableModel;
import com.github.bordertech.wcomponents.util.I18nUtilities;
import com.github.bordertech.wcomponents.util.InternalMessages;
import com.github.bordertech.wcomponents.util.TableUtil;
import com.github.bordertech.wcomponents.util.Util;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>
 * The WTable component is used to display tabular data. It supports common functions such as sorting and pagination of
 * data.
 * </p>
 * <p>
 * The WTable component is only concerned with how the UI functions, not the data behind the table. In a MVC sense, the
 * WTable is the Controller, the view is comprised of the WTable layout and column renderers, and the {@link TableModel}
 * is the model.
 * </p>
 * <p>
 * Columns may only be added statically to the table, but individual columns can be shown/hidden per user by toggling
 * their visibility. See {@link #getColumn(int)} and {@link WComponent#setVisible(boolean)}. Making columns not visible
 * can be problematic with sorting.
 * </p>
 * <p>
 * Another way to make columns not visible is by using {@link #setColumnOrder(int[])}. This can be used to change the
 * column order but also hide columns by not including their index in the array.
 * </p>
 * <p>
 * For data that is not in a tree like structure (ie not expandable), the {@link BasicTableModel} interface can be used
 * via the {@link AdapterBasicTableModel}.
 * </p>
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class WTable extends WBeanComponent implements Container, AjaxTarget, SubordinateTarget,
		Marginable, NamingContextable {

	/**
	 * The logger instance for this class.
	 */
	private static final Log LOG = LogFactory.getLog(WTable.class);

	/**
	 * Default rows per page.
	 */
	private static final int DEFAULT_ROWS = 10;

	/**
	 * For easy access to the columns, including the ability to hide them all at once.
	 */
	private final WContainer columns = new WContainer();

	/**
	 * The repeater that is used to handle the repeated (row) content.
	 */
	private final TableRepeater repeater = new TableRepeater(this);

	/**
	 * Table actions, which are normally visible at the bottom of the table.
	 */
	private final WContainer actions = new WContainer();

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
		 * Indicates that pagination occurs on the client. All data will be sent at once.
		 */
		CLIENT,
		/**
		 * Indicates that pagination occurs via AJAX calls to the server.
		 */
		DYNAMIC
	};

	/**
	 * This is used to control where in the table the pagination controls appear.
	 */
	public enum PaginationLocation {
		/**
		 * Indicates that pagination controls appear in a location determined solely by the theme.
		 */
		AUTO,
		/**
		 * Indicates the pagination controls appear at the top of the table.
		 */
		TOP,
		/**
		 * Indicates the pagination controls appear at the bottom of the table.
		 */
		BOTTOM,
		/**
		 * Indicates that the pagination controls should be placed both at the top and at the bottom of the table.
		 */
		BOTH
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
	 * Prefix used in row ids.
	 */
	public static final String ROW_ID_CONTEXT_PREFIX = "row";

	/**
	 * Selection action command.
	 */
	public static final String SELECTION_ACTION_COMMAND = "selection";

	/**
	 * Creates a WTable.
	 */
	public WTable() {
		add(columns);
		add(repeater);
		add(actions);

		repeater.setRepeatedComponent(new WTableRowRenderer(this));
		repeater.setBeanProvider(new RepeaterRowIdBeanProvider(this));
	}

	/**
	 * Adds a column to the table.
	 *
	 * @param column the column to add.
	 */
	public void addColumn(final WTableColumn column) {
		columns.add(column);
		WTableRowRenderer renderer = (WTableRowRenderer) repeater.getRepeatedComponent();
		renderer.addColumn(column, columns.getChildCount() - 1);
	}

	/**
	 * Retrieves the column at the specified index. Bounds checking is not performed, see {@link #getColumnCount()}.
	 *
	 * @param index the column index. Zero based.
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
	public TableRepeater getRepeater() {
		return repeater;
	}

	/**
	 * @return the table model
	 */
	public TableModel getTableModel() {
		return getComponentModel().tableModel;
	}

	/**
	 * Sets the table model which provides the row/column data.
	 *
	 * @param tableModel the table model.
	 */
	public void setTableModel(final TableModel tableModel) {
		getOrCreateComponentModel().tableModel = tableModel;
		getOrCreateComponentModel().rowIndexMapping = null;
		setSelectedRows(null);
		setExpandedRows(null);
		clearPrevExpandedRows();
		clearPrevRenderedRows();

		if (tableModel instanceof BeanBoundTableModel) {
			((BeanBoundTableModel) tableModel).
					setBeanProvider(new BeanBoundTableModelBeanProvider(this));
			((BeanBoundTableModel) tableModel).setBeanProperty(".");
		}

		if (tableModel instanceof ScrollableTableModel) {
			if (!isPaginated()) {
				throw new IllegalStateException(
						"Set a ScrollableTableModel on a table that is not paginated.");
			}

			int startIndex = getCurrentPage() * getRowsPerPage();
			int endIndex = startIndex + getRowsPerPage() - 1;
			((ScrollableTableModel) tableModel).setCurrentRows(startIndex, endIndex);
		}

		// Flush the repeater's row contexts and scratch maps
		repeater.reset();
	}

	/**
	 * Updates the bean using the table data model's {@link TableModel#setValueAt(Object, List, int)} method.
	 * <p>
	 * The update is only applied if the table has been set as editable via {@link #setEditable(boolean)}. Only rows
	 * that have been rendered are updated.
	 * </p>
	 * <p>
	 * For {@link ScrollableTableModel}, only the rows on the current page are updated.
	 * </p>
	 */
	@Override
	public void updateBeanValue() {
		TableModel model = getTableModel();

		// Only apply updates if table was editable
		if (!isEditable()) {
			LOG.warn("UpdateBeanValue called for table that is not editable");
			return;
		}

		int rows = model.getRowCount();
		if (rows == 0) {
			return;
		}

		int startIndex = 0;
		int endIndex = rows - 1;

		// For scrollable table model, only update the rows on the current page
		if (model instanceof ScrollableTableModel) {
			if (!isPaginated()) {
				throw new IllegalStateException(
						"UpdateBeanValue tried to update a ScrollableTableModel with no pagination.");
			}

			int rowsPerPage = getRowsPerPage();
			int currentPage = getCurrentPage();
			// Only update the rows on the current page
			startIndex = currentPage * rowsPerPage;
			endIndex = Math.min(startIndex + rowsPerPage, rows) - 1;
			LOG.warn("UpdateBeanValue only updating the current page for ScrollableTableModel");
		}

		if (endIndex < startIndex) {
			return;
		}

		// Temporarily widen the pagination on the repeater to hold all rows
		// Calling setBean with a non-null value overrides the DataTableBeanProvider
		repeater.setBean(getRowIds(startIndex, endIndex, true));
		updateBeanValueForRenderedRows();
		repeater.setBean(null);
	}

	/**
	 * Updates the bean using the table data model's {@link TableModel#setValueAt(Object, List, int)} method. This
	 * method only updates the data for the currently set row ids.
	 */
	private void updateBeanValueForRenderedRows() {
		WTableRowRenderer rowRenderer = (WTableRowRenderer) repeater.getRepeatedComponent();
		TableModel model = getTableModel();

		int index = 0;

		List<RowIdWrapper> wrappers = repeater.getBeanList();

		int columnCount = getColumnCount();

		for (RowIdWrapper wrapper : wrappers) {
			UIContext rowContext = repeater.getRowContext(wrapper, index++);
			List<Integer> rowIndex = wrapper.getRowIndex();

			Class<? extends WComponent> expandRenderer = model.getRendererClass(rowIndex);
			if (expandRenderer == null) {
				// Process Columns
				for (int col = 0; col < columnCount; col++) {
					// Check if this cell is editable
					if (model.isCellEditable(rowIndex, col)) {
						updateBeanValueForColumnInRow(rowRenderer, rowContext, rowIndex, col, model);
					}
				}
			} else if (model.isCellEditable(rowIndex, -1)) {
				// Check if this expanded row is editable
				updateBeanValueForRowRenderer(rowRenderer, rowContext, expandRenderer);
			}

		}
	}

	/**
	 * Update the column in the row.
	 *
	 * @param rowRenderer the table row renderer
	 * @param rowContext the row context
	 * @param rowIndex the row id to update
	 * @param col the column to update
	 * @param model the table model
	 */
	private void updateBeanValueForColumnInRow(final WTableRowRenderer rowRenderer,
			final UIContext rowContext,
			final List<Integer> rowIndex, final int col, final TableModel model) {
		// The actual component is wrapped in a renderer wrapper, so we have to fetch it from that
		WComponent renderer = ((Container) rowRenderer.getRenderer(col)).getChildAt(0);

		UIContextHolder.pushContext(rowContext);

		try {
			// If the column is a Container then call updateBeanValue to let the column renderer and its children update
			// the "bean" returned by getValueAt(row, col)
			if (renderer instanceof Container) {
				WebUtilities.updateBeanValue(renderer);
			} else if (renderer instanceof DataBound) { // Update Databound renderer
				Object oldValue = model.getValueAt(rowIndex, col);
				Object newValue = ((DataBound) renderer).getData();
				if (!Util.equals(oldValue, newValue)) {
					model.setValueAt(newValue, rowIndex, col);
				}
			}

		} finally {
			UIContextHolder.popContext();
		}

	}

	/**
	 * Update the expandable row renderer.
	 *
	 * @param rowRenderer the table row renderer
	 * @param rowContext the row context
	 * @param expandRenderer the renderer for the expandable row.
	 */
	private void updateBeanValueForRowRenderer(final WTableRowRenderer rowRenderer,
			final UIContext rowContext,
			final Class<? extends WComponent> expandRenderer) {

		Container expandWrapper = (Container) rowRenderer.
				getExpandedTreeNodeRenderer(expandRenderer);
		if (expandWrapper == null) {
			return;
		}

		// The actual component is wrapped in a renderer wrapper, so we have to fetch it from that
		WComponent expandInstance = expandWrapper.getChildAt(0);

		UIContextHolder.pushContext(rowContext);
		try {
			// Will apply updates to the "bean" returned by the model for this expanded renderer (ie
			// getValueAt(rowIndex, -1))
			WebUtilities.updateBeanValue(expandInstance);
		} finally {
			UIContextHolder.popContext();
		}

	}

	/**
	 * @return the separator type used to visually separate rows or columns.
	 */
	public SeparatorType getSeparatorType() {
		return getComponentModel().separatorType;
	}

	/**
	 * Sets the separator used to visually separate rows or columns.
	 *
	 * @param separatorType the separator type to set.
	 */
	public void setSeparatorType(final SeparatorType separatorType) {
		getOrCreateComponentModel().separatorType = separatorType == null ? SeparatorType.NONE : separatorType;
	}

	/**
	 * @return the striping type used to highlight alternate rows or columns
	 */
	public StripingType getStripingType() {
		return getComponentModel().stripingType;
	}

	/**
	 * Sets the striping type used to highlight alternate rows or columns.
	 *
	 * @param stripingType the striping type to set.
	 */
	public void setStripingType(final StripingType stripingType) {
		getOrCreateComponentModel().stripingType = stripingType == null ? StripingType.NONE : stripingType;
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
	 * @param summary the table summary text to set.
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
	 * @param caption the table caption text to set.
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
	 * Provide an array of column indexes in the order they should be rendered. At least one column must be specifed.
	 * <p>
	 * This can also be used to "hide" columns by not including them in the array.
	 * </p>
	 *
	 * @param columnOrder the column order to set, or null to use default ordering.
	 */
	public void setColumnOrder(final int[] columnOrder) {
		if (columnOrder == null) {
			getOrCreateComponentModel().columnOrder = null;
		} else {
			int count = getColumnCount();
			if (columnOrder.length == 0) {
				throw new IllegalArgumentException("Cannot have an empty column order indices.");
			}
			if (columnOrder.length > count) {
				throw new IllegalArgumentException(
						"Number of column order indices cannot be greater than the number of table columns");
			}
			for (int idx : columnOrder) {
				if (idx < 0 || idx > count - 1) {
					throw new IllegalArgumentException(
							"Illegal index in column order indices [" + idx + "]");
				}
			}
			getOrCreateComponentModel().columnOrder = Arrays.copyOf(columnOrder, columnOrder.length);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setMargin(final Margin margin) {
		getOrCreateComponentModel().margin = margin;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Margin getMargin() {
		return getComponentModel().margin;
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
	 * @param paginationMode the paginationMode to set.
	 */
	public void setPaginationMode(final PaginationMode paginationMode) {
		getOrCreateComponentModel().paginationMode = paginationMode == null ? PaginationMode.NONE : paginationMode;
	}

	/**
	 * The number of rows to display per page. A value of zero, which is only valid when used with
	 * {@link #setRowsPerPageOptions(java.util.List)}, indicates display all rows.
	 *
	 * @return the number of rows to display per page.
	 */
	public int getRowsPerPage() {
		return getComponentModel().rowsPerPage;
	}

	/**
	 * Sets the number of rows to display per page when pagination is enabled.
	 * <p>
	 * If rows per page options have been set, then the value must be a valid option, which can include zero to indicate
	 * show all rows, otherwise the value must be greater than zero.
	 * </p>
	 *
	 * @param rowsPerPage the rowsPerPage to set
	 */
	public void setRowsPerPage(final int rowsPerPage) {
		List<Integer> rowsOptions = getRowsPerPageOptions();
		if (rowsOptions == null) {
			if (rowsPerPage < 1) {
				throw new IllegalArgumentException(
						"Rows per page must be greater than 0, but got: " + rowsPerPage);
			}
		} else if (!rowsOptions.contains(rowsPerPage)) {
			throw new IllegalArgumentException(
					"Rows per page is not a valid rows per page option, got: " + rowsPerPage);
		}
		getOrCreateComponentModel().rowsPerPage = rowsPerPage;
	}

	/**
	 * @return the rows per page options, otherwise null
	 */
	public List<Integer> getRowsPerPageOptions() {
		return getComponentModel().rowsPerPageOptions;
	}

	/**
	 * Set the rows per page options.
	 * <p>
	 * If the current value of {@link #getRowsPerPage()} is not a valid option, it will be set to the first option.
	 * </p>
	 *
	 * @param rowsPerPageOptions the rows per page options
	 */
	public void setRowsPerPageOptions(final List<Integer> rowsPerPageOptions) {
		WTableComponentModel model = getOrCreateComponentModel();
		if (rowsPerPageOptions == null || rowsPerPageOptions.isEmpty()) {
			model.rowsPerPageOptions = null;
			// If the rows per page is currently 0, reset it to the default value
			if (model.rowsPerPage == 0) {
				model.rowsPerPage = DEFAULT_ROWS;
			}
		} else {
			// Validate options
			for (Integer rows : rowsPerPageOptions) {
				if (rows == null || rows < 0) {
					throw new IllegalArgumentException(
							"Rows per page option cannot be less than 0 or null, got: " + rows);
				}
			}
			model.rowsPerPageOptions = new ArrayList<>(rowsPerPageOptions);
			// If the current rows per page is not a valid option, default to the first option
			if (!rowsPerPageOptions.contains(model.rowsPerPage)) {
				model.rowsPerPage = rowsPerPageOptions.get(0);
			}
		}
	}

	/**
	 * @return true if table is currently displaying paginated rows
	 */
	public boolean isPaginated() {
		return getPaginationMode() != PaginationMode.NONE && getRowsPerPage() > 0;
	}

	/**
	 * @return the location for the pagination controls
	 */
	public PaginationLocation getPaginationLocation() {
		return getComponentModel().paginationLocation;
	}

	/**
	 * Sets the location in the table to show the pagination controls.
	 *
	 * @param location the PaginationLocation to set.
	 */
	public void setPaginationLocation(final PaginationLocation location) {
		getOrCreateComponentModel().paginationLocation = location == null ? PaginationLocation.AUTO : location;
	}

	/**
	 * @return the row selection mode.
	 */
	public SelectMode getSelectMode() {
		return getComponentModel().selectMode;
	}

	/**
	 * Sets the row selection mode.
	 *
	 * @param selectMode the row selection mode to set.
	 */
	public void setSelectMode(final SelectMode selectMode) {
		getOrCreateComponentModel().selectMode = selectMode == null ? SelectMode.NONE : selectMode;
	}

	/**
	 * @return the sort mode.
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
		getOrCreateComponentModel().sortMode = sortMode == null ? SortMode.NONE : sortMode;
	}

	/**
	 * <p>
	 * For tables that are editable, extra details about each row must be stored to allow them to be updated. Therefore,
	 * if the table is not editable, the table is able to have improved performance.
	 * </p>
	 *
	 * @return true if table is editable
	 */
	public boolean isEditable() {
		return getComponentModel().editable;
	}

	/**
	 * Sets the table to be editable.
	 * <p>
	 * For tables that are editable, extra details about each row must be stored to allow them to be updated. Therefore,
	 * if the table is not editable, the table is able to have improved performance.
	 * </p>
	 *
	 * @param editable true if editable
	 */
	public void setEditable(final boolean editable) {
		getOrCreateComponentModel().editable = editable;
	}

	/**
	 * @return the table type that controls how the table is displayed.
	 */
	public Type getType() {
		return getComponentModel().type;
	}

	/**
	 * Sets the table type that controls how the table is displayed.
	 *
	 * @param type the table type to set.
	 */
	public void setType(final Type type) {
		getOrCreateComponentModel().type = type == null ? Type.TABLE : type;
	}

	/**
	 * Indicates how the table row "select all" function should be displayed.
	 *
	 * @return the select all mode.
	 */
	public SelectAllType getSelectAllMode() {
		return getComponentModel().selectAllMode;
	}

	/**
	 * Sets how the table row "select all" function should be displayed.
	 *
	 * @param selectAllMode the select all mode to set.
	 */
	public void setSelectAllMode(final SelectAllType selectAllMode) {
		getOrCreateComponentModel().selectAllMode = selectAllMode == null ? SelectAllType.TEXT : selectAllMode;
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
	 * @param expandMode the expand mode to set.
	 */
	public void setExpandMode(final ExpandMode expandMode) {
		getOrCreateComponentModel().expandMode = expandMode == null ? ExpandMode.NONE : expandMode;
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
	 * @return the current page. Zero based.
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
	 * @param currentPage the currentPage to set. Zero based.
	 */
	public void setCurrentPage(final int currentPage) {
		if (currentPage < 0) {
			throw new IllegalArgumentException("Page number must be greater than or equal to zero.");
		}

		WTableComponentModel model = getOrCreateComponentModel();
		model.currentPage = Math.min(model.getMaxPage(), currentPage);

		// Notify the table model that the page has changed
		TableModel dataModel = getTableModel();
		if (dataModel instanceof ScrollableTableModel) {
			if (!isPaginated()) {
				throw new IllegalStateException(
						"Table with no pagination tried to set the current page on a ScrollableTableModel.");
			}
			int startIndex = getCurrentPage() * getRowsPerPage();
			int endIndex = Math.min(startIndex + getRowsPerPage(), dataModel.getRowCount()) - 1;
			((ScrollableTableModel) dataModel).setCurrentRows(startIndex, endIndex);
		}
	}

	/**
	 * Set the row keys that are expanded.
	 * <p>
	 * A row key uniquely identifies each row and is determined by the {@link TableModel}. Refer to
	 * {@link TableModel#getRowKey(List)}.
	 * </p>
	 *
	 * @param rowKeys the keys of expanded rows.
	 */
	public void setExpandedRows(final Set<?> rowKeys) {
		getOrCreateComponentModel().expandedRows = rowKeys;
	}

	/**
	 * Retrieve the row keys that are expanded.
	 * <p>
	 * A row key uniquely identifies each row and is determined by the {@link TableModel}. Refer to
	 * {@link TableModel#getRowKey(List)}.
	 * </p>
	 *
	 * @return the expanded row keys.
	 */
	public Set<?> getExpandedRows() {
		Set<?> keys = getComponentModel().expandedRows;
		if (keys == null) {
			return Collections.emptySet();
		} else {
			return Collections.unmodifiableSet(keys);
		}
	}

	/**
	 * Set the row keys that are selected.
	 * <p>
	 * A row key uniquely identifies each row and is determined by the {@link TableModel}. Refer to
	 * {@link TableModel#getRowKey(List)}.
	 * </p>
	 *
	 * @param rowKeys the keys of selected rows.
	 */
	public void setSelectedRows(final Set<?> rowKeys) {
		getOrCreateComponentModel().selectedRows = rowKeys;
	}

	/**
	 * Retrieve the row keys that are selected.
	 * <p>
	 * A row key uniquely identifies each row and is determined by the {@link TableModel}. Refer to
	 * {@link TableModel#getRowKey(List)}.
	 * </p>
	 *
	 * @return the selected row keys.
	 */
	public Set<?> getSelectedRows() {
		Set<?> keys = getComponentModel().selectedRows;
		if (keys == null) {
			return Collections.emptySet();
		} else {
			return Collections.unmodifiableSet(keys);
		}
	}

	/**
	 * Return the row keys that have been expanded.
	 * <p>
	 * Note - Only used for when the table is editable.
	 * </p>
	 *
	 * @return the previously expanded row keys.
	 */
	protected Set<?> getPrevExpandedRows() {
		Set<?> keys = getComponentModel().prevExpandedRows;
		if (keys == null) {
			return Collections.emptySet();
		} else {
			return Collections.unmodifiableSet(keys);
		}
	}

	/**
	 * Track the row keys that have been expanded.
	 * <p>
	 * Note - Only used for when the table is editable.
	 * </p>
	 *
	 * @param rowKey the row key that has been expanded.
	 */
	protected void addPrevExpandedRow(final Object rowKey) {
		WTableComponentModel model = getOrCreateComponentModel();
		if (model.prevExpandedRows == null) {
			model.prevExpandedRows = new HashSet<>();
		}
		model.prevExpandedRows.add(rowKey);
	}

	/**
	 * Clear the previously expanded row keys.
	 * <p>
	 * Note - Only used for when the table is editable.
	 * </p>
	 */
	protected void clearPrevExpandedRows() {
		getOrCreateComponentModel().prevExpandedRows = null;
	}

	/**
	 * Return the row keys that have been rendered.
	 * <p>
	 * Note - Only used for when the table is editable.
	 * </p>
	 *
	 * @return the previously rendered row keys.
	 */
	protected Set<?> getPrevRenderedRows() {
		Set<?> keys = getComponentModel().prevRenderedRows;
		if (keys == null) {
			return Collections.emptySet();
		} else {
			return Collections.unmodifiableSet(keys);
		}
	}

	/**
	 * Track the row keys that have been rendered.
	 * <p>
	 * Note - Only used for when the table is editable.
	 * </p>
	 *
	 * @param rowKey the row key that has been rendered.
	 */
	protected void addPrevRenderedRow(final Object rowKey) {
		WTableComponentModel model = getOrCreateComponentModel();
		if (model.prevRenderedRows == null) {
			model.prevRenderedRows = new HashSet<>();
		}
		model.prevRenderedRows.add(rowKey);
	}

	/**
	 * Clear the previously rendered row keys.
	 * <p>
	 * Note - Only used for when the table is editable.
	 * </p>
	 */
	protected void clearPrevRenderedRows() {
		getOrCreateComponentModel().prevRenderedRows = null;
	}

	/**
	 * For rendering purposes only - has no effect on model.
	 *
	 * @param index the sort column index, or -1 for no sort.
	 * @param ascending true for ascending order, false for descending
	 */
	protected void setSort(final int index, final boolean ascending) {
		WTableComponentModel model = getOrCreateComponentModel();
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
		TableModel dataModel = getTableModel();
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
					// Allow for column order
					int[] cols = getColumnOrder();
					if (cols != null) {
						sortCol = cols[sortCol];
					}
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
		int[] rowIndexMappings = getTableModel().sort(sortCol, sortAsc);
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
	 * Handles a request containing row selection data.
	 *
	 * @param request the request containing row selection data.
	 */
	private void handleSelectionRequest(final Request request) {
		String[] paramValue = request.getParameterValues(getId() + ".selected");

		if (paramValue == null) {
			paramValue = new String[0];
		}

		Map<List<Integer>, Object> pageRowKeys = getCurrentRowIndexAndKeys();

		String[] selectedRows = removeEmptyStrings(paramValue);
		Set<?> oldSelections = getSelectedRows();
		Set<Object> newSelections;

		boolean singleSelect = getSelectMode() == SelectMode.SINGLE;

		if (getTableModel().getRowCount() == 0) {
			newSelections = new HashSet<>();
			selectedRows = new String[0];
		} else if (getPaginationMode() == PaginationMode.NONE || getPaginationMode() == PaginationMode.CLIENT
				|| oldSelections == null || !isPaginated()) {
			newSelections = new HashSet<>(selectedRows.length);
		} else if (singleSelect && selectedRows.length > 0) {
			// For single-select, we need to remove the old entries
			newSelections = new HashSet<>(1);
		} else {
			// For multi-select, we need the entries for the current page only
			newSelections = new HashSet<>(oldSelections);
			newSelections.removeAll(pageRowKeys.values());
		}

		for (String selectedRow : selectedRows) {
			List<Integer> rowIndex = TableUtil.rowIndexStringToList(selectedRow);
			Object key = pageRowKeys.get(rowIndex);
			if (key == null) {
				continue;
			}
			newSelections.add(key);
			if (singleSelect) {
				break;
			}
		}

		setSelectedRows(newSelections);

		// If there is a selection change action specified, it may need to be fired
		Action selectionChangeAction = getSelectionChangeAction();

		if (selectionChangeAction != null && !newSelections.equals(oldSelections)) {
			selectionChangeAction.execute(new ActionEvent(this, SELECTION_ACTION_COMMAND));
		}
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

		Map<List<Integer>, Object> pageRowKeys = getCurrentRowIndexAndKeys();

		String[] expandedRows = removeEmptyStrings(paramValue);
		Set<?> oldExpansions = getExpandedRows();
		Set<Object> newExpansions;

		TableModel model = getTableModel();

		if (model.getRowCount() == 0) {
			newExpansions = new HashSet<>();
			expandedRows = new String[0];
		} else if (getPaginationMode() == PaginationMode.NONE || getPaginationMode() == PaginationMode.CLIENT
				|| oldExpansions == null || !isPaginated()) {
			newExpansions = new HashSet<>(expandedRows.length);
		} else {
			// row expansions only apply to the current page
			newExpansions = new HashSet<>(oldExpansions);
			newExpansions.removeAll(pageRowKeys.values());
		}

		for (String expandedRow : expandedRows) {
			List<Integer> rowIndex = TableUtil.rowIndexStringToList(expandedRow);
			Object key = pageRowKeys.get(rowIndex);
			if (key != null) {
				newExpansions.add(key);
			}
		}

		setExpandedRows(newExpansions);
	}

	/**
	 * Handles a request containing pagination data.
	 *
	 * @param request the request containing a pagination data.
	 */
	private void handlePaginationRequest(final Request request) {
		String rowsStr = request.getParameter(getId() + ".rows");

		int prevPage = getCurrentPage();
		int prevRows = getRowsPerPage();

		boolean rowsChanged = false;

		// Check for rows per page option and is valid
		if (rowsStr != null && getRowsPerPageOptions() != null) {
			try {
				int newRows = Integer.parseInt(rowsStr);
				// If rows option changed
				if (prevRows != newRows) {
					// Set rows
					setRowsPerPage(newRows);
					if (newRows == 0) {
						setCurrentPage(0);
					} else {
						// Try to calc the page the previous top row was on
						int startIdx = prevPage * prevRows;
						int page = startIdx / newRows;
						setCurrentPage(page);
					}
					rowsChanged = true;
				}
			} catch (NumberFormatException e) {
				LOG.warn("Invalid rows option: " + rowsStr);
			}
		}

		// Check for the current page
		if (!rowsChanged && isPaginated()) {
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

		if (getRowsPerPageOptions() != null || PaginationMode.DYNAMIC.equals(getPaginationMode()) || SortMode.DYNAMIC.
				equals(getSortMode())
				|| ExpandMode.DYNAMIC.equals(getExpandMode()) || ExpandMode.LAZY.equals(
				getExpandMode())) {
			AjaxHelper.registerComponentTargetItself(getId(), request);
		}
	}

	/**
	 * Method to call when the model data has changed. For example, when a row has been added or removed.
	 * <p>
	 * Handles resorting the data (if table sorted) and making sure the table pagination is still correct.
	 * </p>
	 */
	public void handleDataChanged() {
		// Apply sort (if required)
		if (isSorted()) {
			sort(getSortColumnIndex(), isSortAscending());
		}
		setCurrentPage(getCurrentPage());
	}

	/**
	 * Allows a subclass to provide the ID used in the row naming context. It is important this ID is unique for each
	 * row.
	 * <p>
	 * The returned ID must only contain letters, digits or underscores.
	 * </p>
	 *
	 * @param rowIndex the current row index
	 * @param rowKey the current row key.
	 * @return the unique row id or null to use the default context id
	 */
	protected String getRowIdName(final List<Integer> rowIndex, final Object rowKey) {
		return null;
	}

	/**
	 * Describes a constraint on a table action.
	 *
	 * @author Jonathan Austin
	 * @since 1.0.0
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
		 * True if the constraint is an error, false for a warning.
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
		 * @param error true if the constraint is an error, false for a warning.
		 * @param message the message to display when the constraint is not met.
		 */
		public ActionConstraint(final int minSelectedRowCount, final int maxSelectedRowCount,
				final boolean error,
				final String message) {
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
		 * @param minSelectedRowCount the minimum selected row count to set.
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
		 * @param maxSelectedRowCount the maximum selected row count to set.
		 */
		public void setMaxSelectedRowCount(final int maxSelectedRowCount) {
			this.maxSelectedRowCount = maxSelectedRowCount;
		}

		/**
		 * Indicates whether the constraint is an error or warning.
		 *
		 * @return true if the constraint is an error, false for a warning.
		 */
		public boolean isError() {
			return error;
		}

		/**
		 * Sets whether the constraint is an error or warning.
		 *
		 * @param error true if the constraint is an error, false for a warning.
		 */
		public void setError(final boolean error) {
			this.error = error;
		}

		/**
		 * @return the message.
		 */
		public String getMessage() {
			return message;
		}

		/**
		 * @param message the message to set.
		 */
		public void setMessage(final String message) {
			this.message = message;
		}
	}

	/**
	 * A bean provider implementation which uses the bean bound to the table.
	 */
	private static final class BeanBoundTableModelBeanProvider implements BeanProvider, Serializable {

		private final WTable table;

		/**
		 * @param table the parent table
		 */
		private BeanBoundTableModelBeanProvider(final WTable table) {
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
	private static final class RepeaterRowIdBeanProvider implements BeanProvider, Serializable {

		private final WTable table;

		/**
		 * @param table the parent table
		 */
		private RepeaterRowIdBeanProvider(final WTable table) {
			this.table = table;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Object getBean(final BeanProviderBound beanProviderBound) {
			TableModel dataModel = table.getTableModel();
			int rowCount = dataModel.getRowCount();

			if (rowCount == 0) {
				return Collections.emptyList();
			}

			int startIndex = 0;
			int endIndex = rowCount - 1;

			if (PaginationMode.DYNAMIC == table.getPaginationMode() && table.isPaginated()) {
				int rowsPerPage = table.getRowsPerPage();
				int currentPage = table.getCurrentPage();
				// Only render the rows on the current page
				// If total row count has changed, calc the new last page
				startIndex = Math.
						min(currentPage * rowsPerPage, rowCount - (rowCount % rowsPerPage));
				endIndex = Math.min(startIndex + rowsPerPage, rowCount) - 1;
			}

			if (endIndex < startIndex) {
				// No data
				return Collections.EMPTY_LIST;
			}

			return table.getRowIds(startIndex, endIndex, false);
		}
	}

	/**
	 * Determine the row ids for the provided index range.
	 *
	 * @param startIndex the startIndex
	 * @param endIndex the endIndex
	 * @param forUpdate true if building list of rowids for rows that need updating
	 * @return the list of rowIds for the provided index range
	 */
	private List<RowIdWrapper> getRowIds(final int startIndex, final int endIndex,
			final boolean forUpdate) {
		TableModel model = getTableModel();

		// If the table is sorted, we may require a mapping for table row index <--> data model index.
		int[] rowIndexMapping = getComponentModel().rowIndexMapping;

		// Check if sort mapping needs updating
		if (isSorted() && rowIndexMapping != null && rowIndexMapping.length != model.getRowCount()) {
			rowIndexMapping = model.sort(getSortColumnIndex(), isSortAscending());
			getOrCreateComponentModel().rowIndexMapping = rowIndexMapping;
		}

		List<RowIdWrapper> rowIds = new ArrayList<>(endIndex - startIndex + 1);

		ExpandMode mode = getExpandMode();
		boolean expandable = mode != ExpandMode.NONE;

		// Expanded rows - for update, include rows that have been previously expanded
		Set<?> expanded = null;
		if (expandable) {
			expanded = forUpdate ? getPrevExpandedRows() : getExpandedRows();
		}

		// Rendered rows - for update, only process rows that have been rendered
		Set<?> rendered = null;
		int renderedCount = 0;
		if (forUpdate) {
			rendered = getPrevRenderedRows();
			// Check if no rows previously rendered
			if (rendered.isEmpty()) {
				return Collections.EMPTY_LIST;
			}
		}

		boolean editable = isEditable();

		for (int i = startIndex; i <= endIndex; i++) {
			// Create top level
			List<Integer> rowIndex = new ArrayList<>(1);

			// Map ids (if sorted)
			if (rowIndexMapping == null) {
				rowIndex.add(i);
			} else {
				rowIndex.add(rowIndexMapping[i]);
			}

			// Row key
			Object key = model.getRowKey(rowIndex);

			// For update, only process rows that have been rendered
			if (forUpdate && !rendered.contains(key)) {
				continue;
			}

			// Create wrapper
			RowIdWrapper wrapper = new RowIdWrapper(rowIndex, key, null);

			if (expandable) {
				calcChildrenRowIds(rowIds, wrapper, model, null, expanded, mode, forUpdate, editable);
			} else {
				rowIds.add(wrapper);
			}

			// For update, check if all rendered rows have been processed
			if (forUpdate) {
				renderedCount++;
				if (renderedCount == rendered.size()) {
					// No need to keep processing
					break;
				}
			}

			// For render, keep rows that have been rendered for update logic (only if table editable)
			if (editable && !forUpdate) {
				addPrevRenderedRow(key);
			}
		}

		// Set the position of each row in the list of row ids.
		int i = 0;
		for (RowIdWrapper row : rowIds) {
			row.setPosition(i++);
		}

		return rowIds;
	}

	/**
	 * Calculate the row ids of a row's children.
	 *
	 * @param rows the list of row ids
	 * @param row the current row
	 * @param model the table model
	 * @param parent the row's parent
	 * @param expanded the set of expanded rows
	 * @param mode the table expand mode
	 * @param forUpdate true if building list of row ids to update
	 * @param editable true if the table is editable
	 */
	@SuppressWarnings("checkstyle:parameternumber")
	private void calcChildrenRowIds(final List<RowIdWrapper> rows, final RowIdWrapper row,
			final TableModel model,
			final RowIdWrapper parent, final Set<?> expanded, final ExpandMode mode,
			final boolean forUpdate, final boolean editable) {
		// Add row
		rows.add(row);

		// Add to parent
		if (parent != null) {
			parent.addChild(row);
		}

		List<Integer> rowIndex = row.getRowIndex();

		// If row has a renderer, then dont need to process its children (should not have any anyway as it is a "leaf")
		if (model.getRendererClass(rowIndex) != null) {
			return;
		}

		// Check row is expandable
		if (!model.isExpandable(rowIndex)) {
			return;
		}

		// Check has children
		if (!model.hasChildren(rowIndex)) {
			return;
		}
		row.setHasChildren(true);

		// Always add children if CLIENT mode or row is expanded
		boolean addChildren = (mode == ExpandMode.CLIENT) || (expanded != null && expanded.contains(
				row.getRowKey()));
		if (!addChildren) {
			return;
		}

		// Get actual child count
		int children = model.getChildCount(rowIndex);
		if (children == 0) {
			// Could be there are no children even though hasChildren returned true
			row.setHasChildren(false);
			return;
		}

		// Render mode, Keep rows that have been expanded (only if table editable)
		if (!forUpdate && editable) {
			addPrevExpandedRow(row.getRowKey());
		}

		// Add children by processing each child row
		for (int i = 0; i < children; i++) {
			// Add next level
			List<Integer> nextRow = new ArrayList<>(row.getRowIndex());
			nextRow.add(i);
			// Create Wrapper
			Object key = model.getRowKey(nextRow);
			RowIdWrapper wrapper = new RowIdWrapper(nextRow, key, row);
			calcChildrenRowIds(rows, wrapper, model, row, expanded, mode, forUpdate, editable);
		}
	}

	/**
	 * @return the current page row indexes and their keys
	 */
	private Map<List<Integer>, Object> getCurrentRowIndexAndKeys() {
		List<RowIdWrapper> wrappers = repeater.getBeanList();
		if (wrappers == null || wrappers.isEmpty()) {
			return Collections.EMPTY_MAP;
		}

		Map<List<Integer>, Object> rows = new HashMap<>(wrappers.size());
		for (RowIdWrapper wrapper : wrappers) {
			rows.put(wrapper.getRowIndex(), wrapper.getRowKey());
		}
		return rows;
	}

	/**
	 * Contains the table's UI state.
	 *
	 * @author Jonathan Austin
	 * @since 1.0.0
	 */
	public static final class WTableComponentModel extends BeanAndProviderBoundComponentModel {

		/**
		 * The margins to be used on the table.
		 */
		private Margin margin;

		/**
		 * This controls how sorting should function. Sortability is determined by the data model.
		 */
		private SortMode sortMode = SortMode.NONE;

		/**
		 * The data model for the table.
		 */
		private TableModel tableModel = EmptyTableModel.INSTANCE;

		/**
		 * Controls whether background striping is used to distinguish rows/columns from each other.
		 */
		private StripingType stripingType = StripingType.NONE;

		/**
		 * Controls whether a visual separator is used to distinguish rows/columns from each other.
		 */
		private SeparatorType separatorType = SeparatorType.NONE;

		/**
		 * This flag indicates whether column headers should be displayed.
		 */
		private boolean showColumnHeaders = true;

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
		private int rowsPerPage = DEFAULT_ROWS;

		/**
		 * Options for rows per page.
		 */
		private List<Integer> rowsPerPageOptions;

		/**
		 * Stores the current page index.
		 */
		private int currentPage;

		/**
		 * Stores the location to show the pagination controls.
		 */
		private PaginationLocation paginationLocation = PaginationLocation.AUTO;

		// Selection
		/**
		 * Indicates how row selection should function.
		 */
		private SelectMode selectMode = SelectMode.NONE;

		/**
		 * Indicates how the "select all" control should appear.
		 */
		private SelectAllType selectAllMode = SelectAllType.TEXT;

		/**
		 * Holds the keys of the currently selected rows.
		 */
		private Set<?> selectedRows;

		// Row expansion
		/**
		 * Indicates how row expansion should function.
		 */
		private ExpandMode expandMode = ExpandMode.NONE;

		/**
		 * Indicates whether a "expand/collapse all" control should be displayed.
		 */
		private boolean expandAll = false;

		/**
		 * Holds the keys of currently expanded rows.
		 */
		private Set<?> expandedRows;

		/**
		 * Holds the keys of rows that have been expanded (used for bean updating logic).
		 */
		private Set<Object> prevExpandedRows;

		/**
		 * Holds the keys of rows that have been rendered (used for bean updating logic).
		 */
		private Set<Object> prevRenderedRows;

		/**
		 * Flag if table cells/rows are editable. If true, extra details about each row need to be stored.
		 */
		private boolean editable;

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
			int rowCount = tableModel.getRowCount();

			if (rowCount == 0) {
				return 0;
			} else if (paginationMode == PaginationMode.NONE || rowsPerPage == 0) {
				// NONE Pagination or Zero rows per page indicates no pagination when using rows per page options
				return 0;
			} else {
				return rowCount / rowsPerPage - (rowCount % rowsPerPage == 0 ? 1 : 0);
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
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	// to make public
	public int getChildCount() {
		return super.getChildCount();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	// to make public
	public WComponent getChildAt(final int index) {
		return super.getChildAt(index);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	// to make public
	public int getIndexOfChild(final WComponent childComponent) {
		return super.getIndexOfChild(childComponent);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	// to make public
	public List<WComponent> getChildren() {
		return super.getChildren();
	}

	/**
	 * @return a String representation of this component, for debugging purposes.
	 */
	@Override
	public String toString() {
		TableModel model = getTableModel();
		return toString(model.getClass().getSimpleName() + ", " + model.getRowCount() + " rows", -1,
				-1);
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
	 * Creates a new component model.
	 *
	 * @return a new TableModel.
	 */
	@Override
	protected WTableComponentModel newComponentModel() {
		return new WTableComponentModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	// for type safety only
	protected WTableComponentModel getComponentModel() {
		return (WTableComponentModel) super.getComponentModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	// for type safety only
	protected WTableComponentModel getOrCreateComponentModel() {
		return (WTableComponentModel) super.getOrCreateComponentModel();
	}

	/**
	 * <p>
	 * TableModel provides the data for tables. In a MVC sense, the TableModel is the Model, the {@link WTable} is the
	 * controller and the view is comprised of the WTable layout and column renderers.
	 * </p>
	 * <p>
	 * Note that Data may be stored locally or sourced remotely, depending on the particular TableModel implementation.
	 * <p>
	 * <p>
	 * The row indexes used in the interface are a list of row indexes. Each item in the list is the index of the row
	 * for that level. The size of the list passed in matches the depth of the row.
	 * </p>
	 * <p>
	 * Row and column indices for all methods are zero-based, and TableModels are not expected to perform
	 * bounds-checking.
	 * </p>
	 *
	 * @author Jonathan Austin
	 * @since 1.0.0
	 */
	public interface TableModel {

		/**
		 * Retrieves the value at the given row and column.
		 *
		 * @param row - the row index.
		 * @param col - the column index. Column of -1 indicates row has a renderer.
		 * @return the value at the given row and column.
		 */
		Object getValueAt(List<Integer> row, int col);

		/**
		 * Indicates whether the given cell is editable.
		 *
		 * @param row - the row index.
		 * @param col - the column index. Column of -1 indicates row has a renderer.
		 * @return true if the given cell is editable, false otherwise.
		 */
		boolean isCellEditable(List<Integer> row, int col);

		/**
		 * Sets the value at the given row and column.
		 *
		 * @param value the value to set.
		 * @param row - the row index.
		 * @param col - the column index.
		 */
		void setValueAt(Object value, List<Integer> row, int col);

		/**
		 * Indicates whether the model supports sorting by the given column.
		 *
		 * @param col the column index.
		 * @return true if the model is sortable by the given column, false otherwise.
		 */
		boolean isSortable(int col);

		/**
		 * <p>
		 * Sorts the data by the given column. Any previous sorting should be disregarded.
		 * </p>
		 * <p>
		 * Data models must implement sorting in one of two ways.
		 * </p>
		 * <ol>
		 * <li>
		 * <p>
		 * If the data is accessible locally by the data model (ie. a sort won't result in a service call to obtain
		 * sorted data), then this method should not sort the actual data, but return a row-index mapping which the
		 * table will use to access the data. Row selection and expansion will be updated to use the new row indices.
		 * </p>
		 * <p>
		 * For example, if the data for the column is {"a", "b", "d", "c"}, then an ascending sort should return {0, 1,
		 * 3, 2}, and a descending sort {2, 3, 1, 0}.
		 * </p>
		 * </li>
		 * <li>
		 * <p>
		 * If the data is not accessible locally by the data model, or the model is otherwise unable to perform a
		 * mapping between old and new row indices, then the model should sort the actual data, and return null. In this
		 * case, the table will reset any row selection or expansion.
		 * </p>
		 * </li>
		 * </ol>
		 *
		 * @param col the column to sort on
		 * @param ascending true for an ascending sort, false for descending.
		 * @return the row indices in sort order, or null if row mappings can not be determined.
		 */
		int[] sort(int col, boolean ascending);

		/**
		 * Indicates whether the given row is disabled.
		 *
		 * @param row the row index
		 * @return true if the row is disabled, false otherwise.
		 */
		boolean isDisabled(List<Integer> row);

		/**
		 * Indicates whether the given row is selectable.
		 *
		 * @param row the row index
		 * @return true if the row is selectable, false otherwise.
		 */
		boolean isSelectable(List<Integer> row);

		/**
		 * Indicates whether the given row is expandable.
		 *
		 * @param row the row index
		 * @return true if the row is expandable, false otherwise.
		 */
		boolean isExpandable(List<Integer> row);

		/**
		 * Retrieves the number of rows for the root (ie top) level.
		 *
		 * @return the number of rows in the model for the root (ie top) level.
		 */
		int getRowCount();

		/**
		 * Allows the model to report if the row has children without actually having to determine the number of
		 * children (as it might not be known).
		 *
		 * @param row the row index
		 * @return true if the row has children
		 */
		boolean hasChildren(List<Integer> row);

		/**
		 * Retrieves the number of children a row has.
		 *
		 * @param row the row index
		 * @return the number of rows in the model for this level.
		 */
		int getChildCount(List<Integer> row);

		/**
		 * Retrieves the custom renderer for this row.
		 *
		 * @param row the row index
		 * @return the renderer class, or null if the default renderer is to be used.
		 */
		Class<? extends WComponent> getRendererClass(List<Integer> row);

		/**
		 * Retrieves the key (ie bean) used to uniquely identify this row.
		 * <p>
		 * The usual implementation of this method would just return the row id passed in.
		 * </p>
		 * <p>
		 * However, if you are required to dynamically add/remove rows in the model, which would change the row index,
		 * then the implementation of this method needs to return an object that uniquely identifies this row.
		 * </p>
		 * <p>
		 * When rows have been added/removed, the {@link WTable#handleDataChanged} method on WTable needs to be called.
		 * </p>
		 *
		 * @param row the row index
		 * @return the key (ie bean) used to uniquely identify this row
		 */
		Object getRowKey(List<Integer> row);
	}

	/**
	 * This extension of {@link TableModel} is primarily for models that do not store their data locally. Models
	 * implementing this interface can provide more efficient calls to back-end systems, as the data model is notified
	 * of which rows are likely to be used in the near future.
	 * <p>
	 * It is expected this TableModel is always used with Pagination.
	 * </p>
	 *
	 * @author Jonathan Austin
	 * @since 1.0.0
	 */
	public interface ScrollableTableModel extends TableModel {

		/**
		 * This method will be called by the table to notify the TableModel of which rows are likely to be used in the
		 * near future.
		 *
		 * @param start the starting row index.
		 * @param end the ending row index.
		 */
		void setCurrentRows(int start, int end);
	}

	/**
	 * The BeanBoundTableModel provides a link between a bean (bound to a table), and the table model API.
	 *
	 * @author Jonathan Austin
	 * @since 1.0.0
	 */
	public interface BeanBoundTableModel extends TableModel, BeanProviderBound {

	}

	/**
	 * This repeater extension is necessary to ensure that tree-tables are painted correctly.
	 */
	public static class TableRepeater extends WRepeater {

		/**
		 * Parent table.
		 */
		private final WTable table;

		/**
		 * @param table the parent table.
		 */
		public TableRepeater(final WTable table) {
			this.table = table;
			setRowIdProperty("rowKey");
			setIdName(ROW_ID_CONTEXT_PREFIX);
			setNamingContext(true);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getId() {
			return table.getId() + ID_CONTEXT_SEPERATOR + getIdName();
		}

		/**
		 * Override paintComponent, as the table renderer does all the work.
		 *
		 * @param renderContext the RenderContext to send output to.
		 */
		@Override
		protected void paintComponent(final RenderContext renderContext) {
			// Do nothing
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public List<RowIdWrapper> getBeanList() {
			return super.getBeanList();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void cleanupStaleContexts(final Set<?> rowIds) {
			// Tables that are not editable, clean up contexts. Editable tables do not do the clean up so the contexts
			// are available when changing pages
			if (!table.isEditable()) {
				super.cleanupStaleContexts(rowIds);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected String getRowIdName(final Object rowBean, final Object rowId) {
			RowIdWrapper wrapper = (RowIdWrapper) rowBean;
			List<Integer> rowIndex = wrapper.getRowIndex();
			return table.getRowIdName(rowIndex, rowId);
		}
	}

	/**
	 * Used to wrap the row index and the row key (used to uniquely identify the row).
	 * <p>
	 * Intended for internal use only.
	 * </p>
	 */
	public static class RowIdWrapper {

		/**
		 * The row index.
		 */
		private final List<Integer> rowIndex;
		/**
		 * The row key.
		 */
		private final Object rowKey;
		/**
		 * The children of the row.
		 */
		private final List<RowIdWrapper> children = new ArrayList<>();
		/**
		 * Flag if row has children.
		 */
		private boolean hasChildrenFlag = false;
		/**
		 * Parent of the row.
		 */
		private final RowIdWrapper parent;
		/**
		 * Hold its position in the list.
		 */
		private int position;

		/**
		 * @param rowIndex the row index
		 * @param rowKey the row key
		 * @param parent the parent of the row, or null if no parent
		 */
		public RowIdWrapper(final List<Integer> rowIndex, final Object rowKey,
				final RowIdWrapper parent) {
			this.rowIndex = rowIndex;
			this.rowKey = rowKey;
			this.parent = parent;
		}

		/**
		 * @return the row index
		 */
		public List<Integer> getRowIndex() {
			return rowIndex;
		}

		/**
		 * @return the row key
		 */
		public Object getRowKey() {
			return rowKey;
		}

		/**
		 * @return the children of the row, or null if no children
		 */
		public List<RowIdWrapper> getChildren() {
			return children;
		}

		/**
		 * @param hasChildren true if row has children
		 */
		public void setHasChildren(final boolean hasChildren) {
			this.hasChildrenFlag = hasChildren;
		}

		/**
		 * @return true if the row has children
		 */
		public boolean hasChildren() {
			return hasChildrenFlag;
		}

		/**
		 * @param child the child row to add
		 */
		public void addChild(final RowIdWrapper child) {
			children.add(child);
		}

		/**
		 * @return the parent of the row, or null if no parent
		 */
		public RowIdWrapper getParent() {
			return parent;
		}

		/**
		 * @return the position of this row id in the list of row ids.
		 */
		public int getPosition() {
			return position;
		}

		/**
		 * @param position the position of this row id in the list of row ids.
		 */
		public void setPosition(final int position) {
			this.position = position;
		}
	}
}
