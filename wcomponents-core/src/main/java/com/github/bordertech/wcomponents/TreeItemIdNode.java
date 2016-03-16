package com.github.bordertech.wcomponents;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A node that holds a tree item id. Used for custom tree item structures in {@link WTree}.
 *
 * @author Jonathan Austin
 * @since 1.1.0
 */
public class TreeItemIdNode implements Serializable {

	/**
	 * The tree item id.
	 *
	 */
	private final String itemId;
	/**
	 * The list of this nodes children.
	 */
	private List<TreeItemIdNode> children;
	/**
	 * True if this node has children. Allows it be set true with out actually loading the children.
	 */
	private boolean hasChildren;

	/**
	 * @param itemId the tree item id
	 */
	public TreeItemIdNode(final String itemId) {
		this.itemId = itemId;
	}

	/**
	 *
	 * @return the tree item id
	 */
	public String getItemId() {
		return itemId;
	}

	/**
	 * @param node the child node to add
	 */
	public void addChild(final TreeItemIdNode node) {
		if (children == null) {
			children = new ArrayList<>();
		}
		children.add(node);
	}

	/**
	 * @return the list of child nodes
	 */
	public List<TreeItemIdNode> getChildren() {
		if (children == null) {
			return Collections.EMPTY_LIST;
		} else {
			return Collections.unmodifiableList(children);
		}
	}

	/**
	 * @return true if this node has children
	 */
	public boolean hasChildren() {
		return hasChildren;
	}

	/**
	 *
	 * @param hasChildren true if this node has children
	 */
	public void setHasChildren(final boolean hasChildren) {
		this.hasChildren = hasChildren;
	}

}
