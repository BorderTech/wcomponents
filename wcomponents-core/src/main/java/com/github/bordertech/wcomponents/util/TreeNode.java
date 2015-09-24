package com.github.bordertech.wcomponents.util;

import java.util.Iterator;

/**
 * Represents a node in a tree structure. The API is loosely based on Swing's DefaultMutableTreeNode.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public interface TreeNode {

	/**
	 * Adds the given TreeNode to this node.
	 *
	 * @param node the node to add.
	 */
	void add(final TreeNode node);

	/**
	 * @return an iteration of this TreeNode's children.
	 */
	Iterator<TreeNode> children();

	/**
	 * @return a depth-first iteration of this TreeNode's children.
	 */
	Iterator<TreeNode> depthFirst();

	/**
	 * @return a breadth-first iteration of this TreeNode's children.
	 */
	Iterator<TreeNode> breadthFirst();

	/**
	 * Retrieves the TreeNode at the given index.
	 *
	 * @param index the child index.
	 * @return the child TreeNode at the given index.
	 */
	TreeNode getChildAt(final int index);

	/**
	 * Return the index of the child node. Return -1 if the node is not a child of this node.
	 *
	 * @param aChild the node to return index of.
	 * @return the index of the child or -1 if there is no such child node.
	 */
	int getIndex(TreeNode aChild);

	/**
	 * @return the number of child TreeNodes that this node contains.
	 */
	int getChildCount();

	/**
	 * @return the parent TreeNode, or null if the root of the tree.
	 */
	TreeNode getParent();

	/**
	 * Removes the given tree node from this node.
	 *
	 * @param child the TreeNode to remove.
	 */
	void remove(final TreeNode child);

	/**
	 * Removes all children from this node.
	 */
	void removeAll();

	/**
	 * Indicates whether the given node is a descendant of this node.
	 *
	 * @param otherNode the node to check
	 * @return true if the given node is a descendant of this node.
	 */
	boolean isDescendant(final TreeNode otherNode);

	/**
	 * Indicates whether the given node is an ancestor of this node.
	 *
	 * @param otherNode the node to check
	 * @return true if the given node is a descendant of this node.
	 */
	boolean isAncestor(final TreeNode otherNode);

	/**
	 * @return the node's level, ie. the number of parent nodes until the root.
	 */
	int getLevel();

	/**
	 * True if and only if this node has no child nodes.
	 *
	 * @return <code>true</code> if the node has no child nodes.
	 */
	boolean isLeaf();

	/**
	 * Returns true if and only if <code>otherNode</code> is in the same tree as this node. Returns false if
	 * <code>otherNode</code> is null.
	 *
	 * @param otherNode the node to check.
	 * @return true if <code>otherNode</code> is in the same tree as this node; false if <code>otherNode</code> is null
	 */
	boolean isRelated(final TreeNode otherNode);

	/**
	 * @return the root node.
	 */
	TreeNode getRoot();

	/**
	 * Returns the path from the root, to get to this node. The last element in the path is this node.
	 *
	 * @return an array of DefaultTreeNode objects giving the path, where the first element in the path is the root and
	 * the last element is this node.
	 */
	TreeNode[] getPath();
}
