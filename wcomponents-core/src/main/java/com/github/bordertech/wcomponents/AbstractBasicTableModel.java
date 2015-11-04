package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.AdapterBasicTableModel.BasicTableModel;
import com.github.bordertech.wcomponents.util.TableUtil;
import java.io.Serializable;
import java.util.Comparator;

/**
 * A skeleton implementation of a basic data model that does not support filtering, sorting, or editability.
 * <p>
 * Used for data that is not in a tree like structure (ie not expandable).
 * </p>
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public abstract class AbstractBasicTableModel implements BasicTableModel, Serializable {

	/**
	 * @param row the row index
	 * @return the row index as the unique key for the row
	 */
	@Override
	public Object getRowKey(final int row) {
		return row;
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
	 * Calculates the sort order for the data, using the given column and comparator.
	 *
	 * @param comparator the comparator to use for sorting
	 * @param col the column to sort on
	 * @param ascending true for an ascending sort, false for descending.
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
