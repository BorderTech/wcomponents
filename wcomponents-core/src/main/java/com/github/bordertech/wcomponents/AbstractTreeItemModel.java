package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.util.TreeItemUtil;
import java.util.List;

/**
 * A skeleton implementation of {@link TreeItemModel} used with {@link WTree}.
 *
 * @author Jonathan Austin
 * @since 1.1.0
 */
public abstract class AbstractTreeItemModel implements TreeItemModel {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isDisabled(final List<Integer> row) {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isExpandable(final List<Integer> row) {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TreeItemImage getItemImage(final List<Integer> row) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getItemId(final List<Integer> row) {
		return TreeItemUtil.rowIndexListToString(row);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ShuffleType getItemShuffleType(final List<Integer> row) {
		return ShuffleType.BOTH;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasChildren(final List<Integer> row) {
		return getChildCount(row) > 0;
	}

}
