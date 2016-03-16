package com.github.bordertech.wcomponents;

import java.io.Serializable;
import java.util.List;

/**
 * Provides the data and details of the tree items used in a {@link WTree}.
 *
 * @author Jonathan Austin
 * @since 1.1.0
 */
public interface TreeItemModel extends Serializable {

	/**
	 * A type to indicate how a tree item can be shuffled.
	 */
	enum ShuffleType {
		/**
		 * Used as a branch only.
		 */
		BRANCH,
		/**
		 * Used as a leaf only.
		 */
		LEAF,
		/**
		 * Used as a leaf or branch.
		 */
		BOTH
	}

	/**
	 * Indicates whether the given row is disabled.
	 *
	 * @param row the row index
	 * @return true if the row is disabled, false otherwise.
	 */
	boolean isDisabled(final List<Integer> row);

	/**
	 * Retrieves the value at the given row and column.
	 *
	 * @param row - the row index.
	 * @return the value at the given row and column.
	 */
	String getItemLabel(final List<Integer> row);

	/**
	 * Retrieves the value at the given row and column.
	 *
	 * @param row - the row index.
	 * @return the value at the given row and column.
	 */
	String getItemId(final List<Integer> row);

	/**
	 * Retrieves the value at the given row and column.
	 *
	 * @param row - the row index.
	 * @return the value at the given row and column.
	 */
	TreeItemImage getItemImage(final List<Integer> row);

	/**
	 * @param row the row index
	 * @return the shuffle type for this item
	 */
	ShuffleType getItemShuffleType(final List<Integer> row);

	/**
	 * Indicates whether the given row is expandable.
	 *
	 * @param row the row index
	 * @return true if the row is expandable, false otherwise.
	 */
	boolean isExpandable(final List<Integer> row);

	/**
	 * Retrieves the number of rows for the root (ie top) level.
	 *
	 * @return the number of rows in the model for the root (ie top) level.
	 */
	int getRowCount();

	/**
	 * Allows the model to report if the row has children without actually having to determine the number of children
	 * (as it might not be known).
	 *
	 * @param row the row index
	 * @return true if the row has children
	 */
	boolean hasChildren(final List<Integer> row);

	/**
	 * Retrieves the number of children a row has.
	 *
	 * @param row the row index
	 * @return the number of rows in the model for this level.
	 */
	int getChildCount(final List<Integer> row);

}
