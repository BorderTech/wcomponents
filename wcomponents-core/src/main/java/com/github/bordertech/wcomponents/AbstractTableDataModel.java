package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.util.TableUtil;
import java.io.Serializable;
import java.util.Comparator;
import java.util.List;

/**
 * A skeleton implementation of a simple data model that does not support filtering, sorting, or editability. Subclasses
 * need only implement the {@link TableDataModel#getRowCount()} and {@link TableDataModel#getValueAt(int, int)} methods.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 *
 * @deprecated Use {@link WTable} and {@link AbstractTableModel} instead.
 */
@Deprecated
public abstract class AbstractTableDataModel implements TableDataModel, Serializable {

	/**
	 * Row header text is not used by default.
	 *
	 * @param row ignored.
	 * @return null.
	 */
	@Override
	public String getRowHeader(final int row) {
		return null;
	}

	/**
	 * This model does not support the concept of sorting by default. Subclasses will need to override this method and
	 * {@link #isSortable(int)} to support sorting.
	 *
	 * @param col ignored.
	 * @param ascending ignored.
	 * @return null
	 */
	@Override
	public int[] sort(final int col, final boolean ascending) {
		return null;
	}

	/**
	 * This model does not support the concept of editable cells by default. Subclasses will need to override this
	 * method and {@link #isCellEditable(int, int)} to support cell editing.
	 *
	 * @param value ignored.
	 * @param row ignored.
	 * @param col ignored.
	 */
	@Override
	public void setValueAt(final Object value, final int row, final int col) {
		// NOP
	}

	/**
	 * This model does not support the concept of sorting by default. Subclasses will need to override this method and
	 * {@link #sort(int, boolean)} to support sorting.
	 *
	 * @param col ignored.
	 * @return false.
	 */
	@Override
	public boolean isSortable(final int col) {
		return false;
	}

	/**
	 * This model does not support the concept of row disabling by default. Subclasses will need to override this method
	 * to support row disabling.
	 *
	 * @param row ignored.
	 * @return false.
	 */
	@Override
	public boolean isDisabled(final int row) {
		return false;
	}

	/**
	 * This model does not support the concept of row selectability by default. Subclasses will need to override this
	 * method disable selection of specific rows.
	 *
	 * @param row ignored.
	 * @return true.
	 */
	@Override
	public boolean isSelectable(final int row) {
		return true;
	}

	/**
	 * This model does not support the concept of editable cells by default. Subclasses will need to override this
	 * method and {@link #setValueAt(Object, int, int)} to support cell editing.
	 *
	 * @param row ignored.
	 * @param col ignored.
	 * @return false.
	 */
	@Override
	public boolean isCellEditable(final int row, final int col) {
		return false;
	}

	/**
	 * This model does not support the concept of row filtering by default. Subclasses will need to override this method
	 * to support row filtering.
	 *
	 * @param row ignored.
	 * @return false.
	 */
	@Override
	public List<String> getFilterValues(final int row) {
		return null;
	}

	/**
	 * Calculates the sort order for the data, using the given column and comparator.
	 *
	 * @param comparator the comparator to use for sorting
	 * @param col the column to sort on
	 * @param ascending true for an ascending sort, false for descending.
	 *
	 * @return the row indices in sort order.
	 */
	protected int[] sort(final Comparator<Object> comparator, final int col, final boolean ascending) {
		// We cache the column data to avoid repeated and potentially expensive lookups
		Object[] columnData = new Object[getRowCount()];

		for (int i = getRowCount() - 1; i >= 0; i--) {
			columnData[i] = getValueAt(i, col);
		}

		int[] sortIndices = new int[getRowCount()];

		for (int i = 0; i < sortIndices.length; i++) {
			sortIndices[i] = i;
		}

		TableUtil.sortData(columnData, comparator, ascending, 0, columnData.length - 1, sortIndices);

		return sortIndices;
	}
}
