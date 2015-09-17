package com.github.bordertech.wcomponents;

/**
 * An empty data model implementation, the default model used by WDataTable.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 *
 * @deprecated Use {@link WTable} and {@link EmptyTableModel} instead.
 */
@Deprecated
public final class EmptyTableDataModel extends AbstractTableDataModel {

	/**
	 * The singleton instance.
	 */
	public static final EmptyTableDataModel INSTANCE = new EmptyTableDataModel();

	/**
	 * Prevent external instantiation of this class.
	 */
	private EmptyTableDataModel() {
	}

	/**
	 * @return 0, this table model is empty
	 */
	@Override
	public int getRowCount() {
		return 0;
	}

	/**
	 * @param row the row index, ignored.
	 * @param col the column index, ignored.
	 * @return null, this table is empty
	 */
	@Override
	public Object getValueAt(final int row, final int col) {
		return null;
	}

}
