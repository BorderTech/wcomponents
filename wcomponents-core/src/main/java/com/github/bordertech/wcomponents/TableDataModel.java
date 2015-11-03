package com.github.bordertech.wcomponents;

import java.util.List;

/**
 * <p>
 * TableDataModel provides the data for tables. In a MVC sense, the TableDataModel is the Model, the {@link WDataTable}
 * is the controller and the view is comprised of the WDataTable layout and column renderers.</p>
 *
 * <p>
 * Note that Data may be stored locally or sourced remotely, depending on the particular TableDataModel
 * implementation.</p>
 *
 * <p>
 * Row and column indices for all methods are zero-based, and TableDataModels are not expected to perform
 * bounds-checking.</p>
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 *
 * @deprecated Use {@link WTable} and {@link WTable.TableModel} instead.
 */
@Deprecated
public interface TableDataModel {

	/**
	 * Retrieves the value at the given row and column.
	 *
	 * @param row - the row index.
	 * @param col - the column index.
	 *
	 * @return the value at the given row and column.
	 */
	Object getValueAt(int row, int col);

	/**
	 * Indicates whether the given cell is editable.
	 *
	 * @param row - the row index.
	 * @param col - the column index.
	 *
	 * @return true if the given cell is editable, false otherwise.
	 */
	boolean isCellEditable(int row, int col);

	/**
	 * Sets the value at the given row and column.
	 *
	 * @param value the value to set.
	 * @param row - the row index.
	 * @param col - the column index.
	 */
	void setValueAt(Object value, int row, int col);

	/**
	 * Indicates whether the model supports sorting by the given column.
	 *
	 * @param col the column index.
	 * @return true if the model is sortable by the given column, false otherwise.
	 */
	boolean isSortable(int col);

	/**
	 * <p>
	 * Sorts the data by the given column. Any previous sorting should be disregarded.</p>
	 *
	 * <p>
	 * Data models must implement sorting in one of two ways.</p>
	 * <ol>
	 * <li>
	 * <p>
	 * If the data is accessible locally by the data model (ie. a sort won't result in a service call to obtain sorted
	 * data), then this method should not sort the actual data, but return a row-index mapping which the table will use
	 * to access the data. Row selection and expansion will be updated to use the new row indices.</p>
	 *
	 * <p>
	 * For example, if the data for the column is {"a", "b", "d", "c"}, then an ascending sort should return {0, 1, 3,
	 * 2}, and a descending sort {2, 3, 1, 0}.</p>
	 * </li>
	 * <li>
	 * <p>
	 * If the data is not accessible locally by the data model, or the model is otherwise unable to perform a mapping
	 * between old and new row indices, then the model should sort the actual data, and return null. In this case, the
	 * table will reset any row selection or expansion.</p>
	 * </li>
	 * </ol>
	 *
	 * @param col the column to sort on
	 * @param ascending true for an ascending sort, false for descending.
	 *
	 * @return the row indices in sort order, or null if row mappings can not be determined.
	 */
	int[] sort(int col, boolean ascending);

	/**
	 * Indicates whether the given row is disabled.
	 *
	 * @param row the row index
	 * @return true if the row is disabled, false otherwise.
	 */
	boolean isDisabled(int row);

	/**
	 * Indicates whether the given row is selectable.
	 *
	 * @param row the row index
	 * @return true if the row is disabled, false otherwise.
	 */
	boolean isSelectable(int row);

	/**
	 * Retrieves the filter values for this row.
	 *
	 * @param row the row index
	 * @return the filter values for this row.
	 */
	List<String> getFilterValues(int row);

	/**
	 * Retrieves the number of rows in the model.
	 *
	 * @return the number of rows in the model.
	 */
	int getRowCount();

	/**
	 * Retrieves the row heading text for the given row.
	 *
	 * @param row the row index
	 * @return the row heading text for the given row.
	 */
	String getRowHeader(int row);
}
