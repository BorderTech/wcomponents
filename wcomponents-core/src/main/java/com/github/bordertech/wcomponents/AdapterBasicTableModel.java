package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.WTable.TableModel;
import java.io.Serializable;
import java.util.List;

/**
 * Adapter to allow classes that implement the {@link BasicTableModel} interface to be used as the model for
 * {@link WTable}.
 * <p>
 * {@link BasicTableModel} provides a basic interface for data that does not have a tree like structure (ie not
 * expandable). Therefore, for data that is not expandable, it is recommended to use this adapter and
 * {@link BasicTableModel} interface.
 * </p>
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class AdapterBasicTableModel implements TableModel, Serializable {

	/**
	 * The basic table model to adapt for the WTable.
	 */
	private final BasicTableModel model;

	/**
	 * @param model the basic table model to adapt for the {@link WTable}
	 */
	public AdapterBasicTableModel(final BasicTableModel model) {
		this.model = model;
	}

	/**
	 * @return the basic table model to adapt for the {@link WTable}
	 */
	public BasicTableModel getBacking() {
		return model;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object getValueAt(final List<Integer> row, final int col) {
		return getBacking().getValueAt(getRowIndex(row), col);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isCellEditable(final List<Integer> row, final int col) {
		return getBacking().isCellEditable(getRowIndex(row), col);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setValueAt(final Object value, final List<Integer> row, final int col) {
		getBacking().setValueAt(value, getRowIndex(row), col);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isSortable(final int col) {
		return getBacking().isSortable(col);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int[] sort(final int col, final boolean ascending) {
		return getBacking().sort(col, ascending);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isDisabled(final List<Integer> row) {
		return getBacking().isDisabled(getRowIndex(row));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isSelectable(final List<Integer> row) {
		return getBacking().isSelectable(getRowIndex(row));
	}

	/**
	 * @param row the row index
	 * @return false as data is not expandable
	 */
	@Override
	public boolean isExpandable(final List<Integer> row) {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getRowCount() {
		return getBacking().getRowCount();
	}

	/**
	 * @param row ignored
	 * @return false as data is not expandable
	 */
	@Override
	public boolean hasChildren(final List<Integer> row) {
		return false;
	}

	/**
	 * @param row ignored
	 * @return 0 as data is not expandable
	 */
	@Override
	public int getChildCount(final List<Integer> row) {
		return 0;
	}

	/**
	 * @param row ignored
	 * @return null as data is not expandable
	 */
	@Override
	public Class<? extends WComponent> getRendererClass(final List<Integer> row) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object getRowKey(final List<Integer> row) {
		return getBacking().getRowKey(getRowIndex(row));
	}

	/**
	 * @param row the row index
	 * @return the first level index as the data is only one level.
	 */
	private int getRowIndex(final List<Integer> row) {
		return row.get(0);
	}

	/**
	 * <p>
	 * BasicTableModel provides a basic interface that can be adapted via {@link AdapterBasicTableModel} for
	 * {@link WTable}. This model is used for data that is not in a tree like structure (ie not expandable).
	 * </p>
	 * <p>
	 * As the data is not expandable, the interface only requires a single row index, instead of being like the
	 * {@link TableModel} interface that uses a list of indexes.
	 * </p>
	 * <p>
	 * Row and column indices for all methods are zero-based, and TableModels are not expected to perform
	 * bounds-checking.
	 * </p>
	 *
	 * @author Jonathan Austin
	 * @since 1.0.0
	 */
	public interface BasicTableModel {

		/**
		 * Retrieves the value at the given row and column.
		 *
		 * @param row - the row index.
		 * @param col - the column index. Column of -1 indicates row has a renderer.
		 * @return the value at the given row and column.
		 */
		Object getValueAt(int row, int col);

		/**
		 * Indicates whether the given cell is editable.
		 *
		 * @param row - the row index.
		 * @param col - the column index. Column of -1 indicates row has a renderer.
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
		boolean isDisabled(int row);

		/**
		 * Indicates whether the given row is selectable.
		 *
		 * @param row the row index
		 * @return true if the row is disabled, false otherwise.
		 */
		boolean isSelectable(int row);

		/**
		 * Retrieves the number of rows.
		 *
		 * @return the number of rows in the model for this level.
		 */
		int getRowCount();

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
		 * When rows have been added/removed to the model, the {@link WTable#handleDataChanged()} method on WTable needs
		 * to be called.
		 * </p>
		 *
		 * @param row the row index
		 * @return the key (ie bean) used to uniquely identify this row
		 */
		Object getRowKey(int row);
	}
}
