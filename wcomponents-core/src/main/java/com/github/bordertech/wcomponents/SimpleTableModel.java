package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.util.ComparableComparator;
import java.io.Serializable;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple table data model, that takes in tabular data in its constructor. Note that use of this data model is
 * discouraged, as the table data will be stored in the user's session.
 * <p>
 * Used in conjunction with the {@link AdapterBasicTableModel} for {@link WTable}.
 * </p>
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class SimpleTableModel extends AbstractBasicTableModel {

	/**
	 * A simple comparator that compares comparables, for use in sorting e.g. columns containing Strings.
	 */
	public static final ComparableComparator COMPARABLE_COMPARATOR = new ComparableComparator();

	/**
	 * The comparators used for sorting, keyed by column index.
	 */
	private Map<Integer, Comparator<Object>> comparators;

	/**
	 * Indicates whether this model is globally editable.
	 */
	private boolean editable;

	/**
	 * The model's data.
	 */
	private final Serializable[][] data;

	/**
	 * Creates a SimpleTableDataModel containing the given data.
	 *
	 * @param data the table data, the outer list containing rows.
	 */
	public SimpleTableModel(final List<? extends List<? extends Serializable>> data) {
		this.data = new Serializable[data.size()][];

		for (int i = 0; i < this.data.length; i++) {
			List<? extends Serializable> row = data.get(i);
			this.data[i] = row.toArray(new Serializable[row.size()]);
		}
	}

	/**
	 * Creates a SimpleTableDataModel containing the given data.
	 *
	 * @param data the table data, the outer array containing rows.
	 */
	public SimpleTableModel(final Serializable[][] data) {
		this.data = data;
	}

	/**
	 * @return the data for this model
	 */
	public Serializable[][] getData() {
		return data;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isSortable(final int col) {
		return comparators != null && comparators.containsKey(col);
	}

	/**
	 * Sets the comparator for the given column, to enable sorting.
	 *
	 * @param col the column to set the comparator on.
	 * @param comparator the comparator to set.
	 */
	public void setComparator(final int col, final Comparator comparator) {
		synchronized (this) {
			if (comparators == null) {
				comparators = new HashMap<>();
			}
		}

		if (comparator == null) {
			comparators.remove(col);
		} else {
			comparators.put(col, comparator);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getRowCount() {
		return data.length;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object getValueAt(final int row, final int col) {
		return data[row][col];
	}

	/**
	 * Indicates whether the given cell is editable. This model only supports editability at a global level. See
	 * {@link #setEditable(boolean)}.
	 *
	 * @param row ignored.
	 * @param col ignored.
	 * @return true if the given cell is editable, false otherwise.
	 */
	@Override
	public boolean isCellEditable(final int row, final int col) {
		return editable;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setValueAt(final Object value, final int row, final int col) {
		if (!isEditable()) {
			throw new IllegalStateException("Attempted to set a value on an uneditable model");
		}

		data[row][col] = (Serializable) value;
	}

	/**
	 * Indicates whether the data in this model is editable.
	 *
	 * @return true if the data in this model is editable, false otherwise.
	 */
	public boolean isEditable() {
		return editable;
	}

	/**
	 * Sets whether the data in this model is editable. By default, the data is not editable.
	 *
	 * @param editable true if the data is editable, false if it is read-only.
	 */
	public void setEditable(final boolean editable) {
		this.editable = editable;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int[] sort(final int col, final boolean ascending) {
		if (!isSortable(col)) {
			throw new IllegalStateException(
					"Attempted to sort on column " + col + ", which is not sortable");
		}

		return sort(comparators.get(col), col, ascending);
	}
}
