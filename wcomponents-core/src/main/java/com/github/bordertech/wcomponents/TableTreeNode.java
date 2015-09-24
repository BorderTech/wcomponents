package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.util.AbstractTreeNode;
import com.github.bordertech.wcomponents.util.TreeNode;
import java.io.Serializable;

/**
 * <p>
 * An extension of AbstractTreeNode that supports a data attribute, the concept of being expanded/collapsed and
 * node-specific renderers.</p>
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 *
 * @deprecated Use {@link WTable} and {@link WTable.TableModel} instead.
 */
@Deprecated
public class TableTreeNode extends AbstractTreeNode {

	/**
	 * Indicates whether this node is in the expanded state.
	 */
	private boolean expanded = false;

	/**
	 * This node's data.
	 */
	private final Serializable data;

	/**
	 * The renderer class for this row.
	 */
	private Class<? extends WComponent> rendererClass = null;

	/**
	 * Indicates whether the {@link #rendererClass custom renderer} spans the entire row.
	 */
	private boolean rendererSpansAllCols = true;

	/**
	 * For quick counting of nodes.
	 */
	private int nodeCount = 0;

	/**
	 * Creates a TableTreeNode with the specified data. The data may be an id or complex data, and should be used in the
	 * {@link AbstractTreeTableDataModel#getValueAt(TableTreeNode, int)} method.
	 *
	 * @param data the data for this node.
	 */
	public TableTreeNode(final Serializable data) {
		this.data = data;
	}

	/**
	 * Creates a TableTreeNode with the specified data. The data may be an id or complex data, and should be used in the
	 * {@link AbstractTreeTableDataModel#getValueAt(TableTreeNode, int)} method.
	 *
	 * @param data the data for this node.
	 * @param rendererClass The renderer class for this row.
	 * @param rendererSpansAllCols true if the renderer should span all columns, false to only span the first.
	 */
	public TableTreeNode(final Serializable data, final Class<? extends WComponent> rendererClass,
			final boolean rendererSpansAllCols) {
		this.data = data;
		this.rendererClass = rendererClass;
		this.rendererSpansAllCols = rendererSpansAllCols;
	}

	/**
	 * @return this node's data.
	 */
	public Serializable getData() {
		return data;
	}

	/**
	 * @return the next visible node, in depth-first order.
	 */
	public TableTreeNode nextVisible() {
		if (!isVisible()) {
			return null;
		}

		if (getChildCount() > 0 && expanded) {
			return (TableTreeNode) getChildAt(0);
		} else if (getParent() != null) {
			for (TreeNode node = this; node != null; node = node.getParent()) {
				TableTreeNode sibling = ((TableTreeNode) node).nextSibling();

				if (sibling != null) {
					return sibling;
				}
			}
		}

		return null;
	}

	/**
	 * @return the next node, in depth-first order.
	 */
	public TableTreeNode next() {
		if (getChildCount() > 0) {
			return (TableTreeNode) getChildAt(0);
		} else if (getParent() != null) {
			for (TreeNode node = this; node != null; node = node.getParent()) {
				TableTreeNode sibling = ((TableTreeNode) node).nextSibling();

				if (sibling != null) {
					return sibling;
				}
			}
		}

		return null;
	}

	/**
	 * Indicates whether this node is currently visible (ie. all ancestors are expanded).
	 *
	 * @return true if this node is currently visible, otherwise false.
	 */
	private boolean isVisible() {
		// Root is always visible
		if (getParent() == null) {
			return true;
		}

		TableTreeNode parent = (TableTreeNode) getParent();
		return parent.isVisible() && parent.isExpanded();
	}

	/**
	 * @return the next sibling of this node, or null if there is none.
	 */
	protected TableTreeNode nextSibling() {
		TreeNode parent = getParent();

		if (parent != null) {
			int index = parent.getIndex(this) + 1;
			int childCount = parent.getChildCount();

			if (index < childCount) {
				return (TableTreeNode) parent.getChildAt(index);
			}
		}

		return null;
	}

	/**
	 * @return the row index of this node.
	 */
	public int getRowIndex() {
		int index = 0;
		TableTreeNode root = (TableTreeNode) getRoot();

		for (TableTreeNode node = root; node != null; node = node.next()) {
			if (node == this) {
				return index;
			}

			index++;
		}

		// impossible
		return -1;
	}

	/**
	 * @return true if this node is expanded, false otherwise.
	 */
	public boolean isExpanded() {
		return expanded;
	}

	/**
	 * Sets whether this node is expanded.
	 *
	 * @param expanded true to set this node expanded this node, false for collapsed.
	 */
	public void setExpanded(final boolean expanded) {
		this.expanded = expanded;
	}

	/**
	 * Retrieves the custom renderer for this node.
	 *
	 * @return the renderer class, or null if the default renderer is to be used.
	 */
	public Class<? extends WComponent> getRendererClass() {
		return rendererClass;
	}

	/**
	 * Sets the renderer to be used to render the row for this node. If null, the default table row renderer will be
	 * used.
	 *
	 * @param rendererClass The renderer class to set.
	 */
	public void setRendererClass(final Class<? extends WComponent> rendererClass) {
		this.rendererClass = rendererClass;
	}

	/**
	 * Indicates whether the custom renderer, if set, should span all table columns.
	 *
	 * @return true if the custom renderer should span all columns, false to only span the first.
	 */
	public boolean isRendererSpansAllCols() {
		return rendererSpansAllCols;
	}

	/**
	 * Sets whether the custom renderer should span all table columns.
	 *
	 * @param rendererSpansAllCols true to span all columns, false to only span the first.
	 */
	public void setRendererSpansAllCols(final boolean rendererSpansAllCols) {
		this.rendererSpansAllCols = rendererSpansAllCols;
	}

	/**
	 * @return the number of nodes contained in this node.
	 */
	public int getNodeCount() {
		return nodeCount;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(final TreeNode node) {
		super.add(node);
		adjustNodeCount(((TableTreeNode) node).nodeCount + 1);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void remove(final TreeNode node) {
		super.remove(node);
		adjustNodeCount(-((TableTreeNode) node).nodeCount - 1);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeAll() {
		super.removeAll();
		adjustNodeCount(-nodeCount);
	}

	/**
	 * Adjusts the node count of this node and all parent nodes. Called when nodes are being added/removed from the
	 * tree.
	 *
	 * @param delta the change in the node count.
	 */
	private void adjustNodeCount(final int delta) {
		for (TableTreeNode parent = this; parent != null; parent = (TableTreeNode) parent.
				getParent()) {
			parent.nodeCount += delta;
		}
	}
}
