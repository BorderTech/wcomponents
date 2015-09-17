package com.github.bordertech.wcomponents.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * A default implementation of the TreeNode interface. This class is abstract, as it doesn't make sense to instantiate a
 * TreeNode that doesn't contain any data.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public abstract class AbstractTreeNode implements TreeNode, Serializable {

	/**
	 * An iterator for empty lists.
	 */
	private static final Iterator<TreeNode> EMPTY_ITERATOR = new EmptyIterator<>();

	/**
	 * The parent node.
	 */
	private TreeNode parent;

	/**
	 * The child list, lazily initialised.
	 */
	private List<TreeNode> children;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(final TreeNode node) {
		if (node == this) {
			throw new IllegalArgumentException("Can't add a node to itself");
		}

		if (this.isAncestor(node)) {
			throw new IllegalArgumentException("Can't add an ancestor as a child node");
		}

		if (node.getParent() != null) {
			node.getParent().remove(node);
		}

		if (children == null) {
			children = new ArrayList<>();
		}

		children.add(node);

		if (node instanceof AbstractTreeNode) {
			((AbstractTreeNode) node).parent = this;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterator<TreeNode> children() {
		if (children == null) {
			return EMPTY_ITERATOR;
		}

		return children.iterator();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterator<TreeNode> depthFirst() {
		return new PostorderIterator(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterator<TreeNode> breadthFirst() {
		return new BreadthFirstIterator(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TreeNode getChildAt(final int index) {
		if (children == null || index < 0 || index >= children.size()) {
			throw new IndexOutOfBoundsException("Index out of bounds: " + index);
		}

		return children.get(index);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getIndex(final TreeNode aChild) {
		if (aChild == null) {
			throw new IllegalArgumentException("Can't retrieve index for null node");
		}

		if (children != null) {
			return children.indexOf(aChild);
		}

		return -1;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getChildCount() {
		return children == null ? 0 : children.size();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TreeNode getParent() {
		return parent;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void remove(final TreeNode child) {
		if (children != null) {
			children.remove(child);

			if (child instanceof AbstractTreeNode) {
				((AbstractTreeNode) child).parent = null;
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeAll() {
		if (children != null) {
			for (int i = 0; i < children.size(); i++) {
				TreeNode child = children.get(i);

				if (child instanceof AbstractTreeNode) {
					((AbstractTreeNode) child).parent = null;
				}
			}

			children = null;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isDescendant(final TreeNode otherNode) {
		return this.isAncestor(otherNode);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isAncestor(final TreeNode otherNode) {
		if (otherNode != null) {
			for (TreeNode node = getParent(); node != null; node = node.getParent()) {
				if (node == otherNode) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getLevel() {
		int level = 0;

		for (TreeNode node = this; node.getParent() != null; node = node.getParent()) {
			level++;
		}

		return level;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isLeaf() {
		return !children().hasNext();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isRelated(final TreeNode otherNode) {
		return (otherNode != null) && (getRoot() == otherNode.getRoot());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TreeNode getRoot() {
		TreeNode node = this;

		while (node.getParent() != null) {
			node = node.getParent();
		}

		return node;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TreeNode[] getPath() {
		return getPathToRoot(this, 0);
	}

	/**
	 * Builds the parents of node up to and including the root node, where the original node is the last element in the
	 * returned array. The length of the returned array gives the node's depth in the tree.
	 *
	 * @param aNode the DefaultTreeNode to get the path for
	 * @param depth an int giving the number of steps already taken towards the root (on recursive calls), used to size
	 * the returned array
	 * @return an array of TreeNodes giving the path from the root to the specified node
	 */
	private TreeNode[] getPathToRoot(final TreeNode aNode, final int depth) {
		TreeNode[] retNodes;

		// This method recurses, traversing towards the root in order
		// size the array. On the way back, it fills in the nodes,
		// starting from the root and working back to the original node.
		// Check for null, in case someone passed in a null node, or they passed
		// in an element that isn't rooted at root.
		if (aNode == null) {
			if (depth == 0) {
				return null;
			} else {
				retNodes = new TreeNode[depth];
			}
		} else {
			int newDepth = depth + 1;
			retNodes = getPathToRoot(aNode.getParent(), newDepth);
			retNodes[retNodes.length - newDepth] = aNode;
		}

		return retNodes;
	}

	/**
	 * An iteration that traverses the subtree rooted at this node in breadth-first order. The first node returned by
	 * the iteration's <code>nextElement()</code> method is the initial node.
	 * <P>
	 * Adapted from javax.swing.tree.TreeNode.BreadthFirstEnumerator.
	 *
	 * @author Yiannis Paschalidis
	 */
	private static final class BreadthFirstIterator implements Iterator<TreeNode> {

		private final Queue<Iterator<TreeNode>> queue;

		/**
		 * Creates a BreadthFirstIterator.
		 *
		 * @param rootNode the root node for the iterator.
		 */
		private BreadthFirstIterator(final TreeNode rootNode) {
			List<TreeNode> list = new ArrayList<>(1);
			list.add(rootNode);
			queue = new Queue<>();
			queue.enqueue(list.iterator());
		}

		/**
		 * @return true if the iterator has more elements.
		 */
		@Override
		public boolean hasNext() {
			return (!queue.isEmpty() && queue.firstObject().hasNext());
		}

		/**
		 * @return the next element in the iteration.
		 */
		@Override
		public TreeNode next() {
			Iterator<TreeNode> iterator = queue.firstObject();
			TreeNode node = iterator.next();
			Iterator<TreeNode> children = node.children();

			if (!iterator.hasNext()) {
				queue.dequeue();
			}
			if (children.hasNext()) {
				queue.enqueue(children);
			}
			return node;
		}

		/**
		 * Throws an UnsupportedOperationException, as remove is not supported.
		 */
		@Override
		public void remove() {
			throw new UnsupportedOperationException("Remove not supported");
		}

		/**
		 * A simple queue with a linked list data structure.
		 *
		 * @param <T> the queue type
		 * @author Rob Davis
		 */
		private static final class Queue<T> {

			/**
			 * The head of the queue, null if empty.
			 */
			private QNode<T> head;
			/**
			 * The tail of the queue, null if empty.
			 */
			private QNode<T> tail;

			/**
			 * Implementation of linked list queue node.
			 *
			 * @param <T> the queue node type
			 * @author Rob Davis
			 */
			private static final class QNode<T> {

				/**
				 * The value being held by the node.
				 */
				private final T object;
				/**
				 * The next node in the queue, null if end.
				 */
				private QNode<T> next;

				/**
				 * Creates a QNode.
				 *
				 * @param object the node's value.
				 * @param next the next node in the queue.
				 */
				private QNode(final T object, final QNode<T> next) {
					this.object = object;
					this.next = next;
				}
			}

			/**
			 * Adds an object to the queue.
			 *
			 * @param anObject the object to add.
			 */
			public void enqueue(final T anObject) {
				if (head == null) {
					head = new QNode<>(anObject, null);
					tail = head;
				} else {
					tail.next = new QNode<>(anObject, null);
					tail = tail.next;
				}
			}

			/**
			 * Removes the first object from the queue.
			 *
			 * @return the removed object.
			 */
			public Object dequeue() {
				if (head == null) {
					throw new NoSuchElementException("No more elements");
				}

				Object retval = head.object;
				QNode<T> oldHead = head;
				head = head.next;
				if (head == null) {
					tail = null;
				} else {
					oldHead.next = null;
				}
				return retval;
			}

			/**
			 * @return the first object on the queue.
			 */
			public T firstObject() {
				if (head == null) {
					throw new NoSuchElementException("No more elements");
				}

				return head.object;
			}

			/**
			 * Indicates whether this queue is empty.
			 *
			 * @return true if this queue is empty.
			 */
			public boolean isEmpty() {
				return head == null;
			}
		}
	}

	/**
	 * A Post-order (depth first) iteration for a sub-tree. Adapted from
	 * javax.swing.tree.TreeNode.PostorderEnumerationation.
	 *
	 * @author Yiannis Paschalidis
	 */
	private static final class PostorderIterator implements Iterator<TreeNode> {

		private TreeNode root;
		private final Iterator<TreeNode> children;
		private Iterator<TreeNode> subtree;

		/**
		 * Creates a PostorderIterator.
		 *
		 * @param rootNode the root node for this iteration.
		 */
		private PostorderIterator(final TreeNode rootNode) {
			root = rootNode;
			children = root.children();
			subtree = EMPTY_ITERATOR;
		}

		/**
		 * @return true if the iterator has more elements.
		 */
		@Override
		public boolean hasNext() {
			return root != null;
		}

		/**
		 * @return the next element in the iteration.
		 */
		@Override
		public TreeNode next() {
			TreeNode retval;

			if (subtree.hasNext()) {
				retval = subtree.next();
			} else if (children.hasNext()) {
				subtree = new PostorderIterator(children.next());
				retval = subtree.next();
			} else {
				retval = root;
				root = null;
			}

			return retval;
		}

		/**
		 * Throws an UnsupportedOperationException, as remove is not supported.
		 */
		@Override
		public void remove() {
			throw new UnsupportedOperationException("Remove not supported");
		}
	}
}
