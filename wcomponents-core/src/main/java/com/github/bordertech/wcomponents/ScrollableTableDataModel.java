package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.WTable.ScrollableTableModel;

/**
 * This extension of {@link TableDataModel} is primarily for models that do not store their data locally. Models
 * implementing this interface can provide more efficient calls to back-end systems, as the data model is notified of
 * which rows are likely to be used in the near future.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 *
 * @deprecated Use {@link WTable} and {@link ScrollableTableModel} instead.
 */
@Deprecated
public interface ScrollableTableDataModel extends TableDataModel {

	/**
	 * This method will be called by the table to notify the TableDataModel of which rows are likely to be used in the
	 * near future.
	 *
	 * @param start the starting row index.
	 * @param end the ending row index.
	 */
	void setCurrentRows(int start, int end);
}
