package com.github.bordertech.wcomponents;

import java.util.List;

/**
 * An empty data model implementation, the default model used by {@link WTree}.
 *
 * @author Jonathan Austin
 * @since 1.1.0
 */
public final class EmptyTreeItemModel extends AbstractTreeItemModel {

	/**
	 * The singleton instance.
	 */
	public static final EmptyTreeItemModel INSTANCE = new EmptyTreeItemModel();

	/**
	 * Prevent external instantiation of this class.
	 */
	private EmptyTreeItemModel() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getRowCount() {
		return 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getChildCount(final List<Integer> row) {
		return 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getItemLabel(final List<Integer> row) {
		return null;
	}

}
