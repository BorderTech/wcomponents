package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.WTable.TableModel;

/**
 * <p>
 * TableDataModel provides the data for tables. In a MVC sense, the TableDataModel is the Model, the {@link WDataTable}
 * is the controller and the view is comprised of the WTable layout and column renderers.</p>
 *
 * <p>
 * Note that Data may be stored locally or sourced remotely, depending on the particular TableDataModel
 * implementation.<p>
 *
 * <p>
 * Row and column indices for all methods are zero-based, and TableDataModels are not expected to perform
 * bounds-checking.</p>
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 *
 * @deprecated Use {@link WTable} and {@link TableModel} instead.
 */
@Deprecated
public interface TreeTableDataModel extends TableDataModel {

	/**
	 * Retrieves the value at the given row and column.
	 *
	 * @param node - the tree node for the row.
	 * @param col - the column index.
	 *
	 * @return the value at the given row and column.
	 */
	Object getValueAt(TableTreeNode node, int col);

	/**
	 * Returns the node at the given line.
	 *
	 * @param row the row index.
	 * @return the node at the given line, or null if the index is out of bounds.
	 */
	TableTreeNode getNodeAtLine(int row);
}
