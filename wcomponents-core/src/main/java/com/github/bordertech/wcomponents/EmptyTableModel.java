package com.github.bordertech.wcomponents;

import java.util.List;

/**
 * An empty data model implementation, the default model used by {@link WTable}.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public final class EmptyTableModel extends AbstractTableModel {

	/**
	 * The singleton instance.
	 */
	public static final EmptyTableModel INSTANCE = new EmptyTableModel();

	/**
	 * Prevent external instantiation of this class.
	 */
	private EmptyTableModel() {
	}

	/**
	 * @param row ignored
	 * @param col ignored
	 * @return null
	 */
	@Override
	public Object getValueAt(final List<Integer> row, final int col) {
		return null;
	}

	/**
	 * @return 0
	 */
	@Override
	public int getRowCount() {
		return 0;
	}

	/**
	 * @param row ignored
	 * @return 0
	 */
	@Override
	public int getChildCount(final List<Integer> row) {
		return 0;
	}
}
