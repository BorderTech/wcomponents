package com.github.bordertech.wcomponents;

import java.io.Serializable;
import java.util.List;

/**
 * A default implementation of the {@link TreeTableDataModel} interface. This implementation does not support editing,
 * row selection, filtering etc. In this implementation, the root node is never used.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 *
 * @deprecated Use {@link WTable} and {@link AbstractTableModel} instead.
 */
@Deprecated
public abstract class AbstractTreeTableDataModel implements TreeTableDataModel, Serializable {

	/**
	 * The root node for this model.
	 */
	private final TableTreeNode root;

	/**
	 * <p>
	 * Creates a SimpleTableDataModel containing the given data. Note that applications may wish to create their own
	 * TableTreeNode extensions that dynamically load data on e.g. the first call to setExpanded(true)
	 * .<p>
	 *
	 * @param root the root node for the table. Must not be null.
	 */
	public AbstractTreeTableDataModel(final TableTreeNode root) {
		this.root = root;
		root.setExpanded(true);
	}

	/**
	 * Returns the node at the given line.
	 *
	 * @param row the row index.
	 * @return the node at the given line, or null if the index is out of bounds.
	 */
	@Override
	public final TableTreeNode getNodeAtLine(final int row) {
		TableTreeNode node = root.next(); // the root node is never used

		for (int index = 0; node != null && index < row; index++) {
			node = node.next();
		}

		return node;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int getRowCount() {
		return root.getNodeCount();
	}

	/**
	 * Retrieves the value at the given row and column. This implementation delegates to
	 * {@link #getValueAt(TableTreeNode, int)}, which subclasses must implement.
	 *
	 * @param row the row index
	 * @param col the column index.
	 * @return the value for the specified cell.
	 */
	@Override
	public final Object getValueAt(final int row, final int col) {
		TableTreeNode rowNode = getNodeAtLine(row);
		return getValueAt(rowNode, col);
	}

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
	 * @return the table root node.
	 */
	protected TableTreeNode getRootNode() {
		return root;
	}
}
