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

	@Override
	public boolean isDisabled(final List<Integer> row) {
		return false;
	}

	@Override
	public boolean isExpandable(final List<Integer> row) {
		return true;
	}

	@Override
	public TreeItemImage getItemImage(final List<Integer> row) {
		return null;
	}

	@Override
	public String getItemId(final List<Integer> row) {
		return TreeItemUtil.rowIndexListToString(row);
	}

	@Override
	public ShuffleType getItemShuffleType(final List<Integer> row) {
		return ShuffleType.BOTH;
	}

	@Override
	public boolean hasChildren(final List<Integer> row) {
		return getChildCount(row) > 0;
	}

}
