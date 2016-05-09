package com.github.bordertech.wcomponents.util;

import java.util.Iterator;
import junit.framework.Assert;
import org.junit.Test;

/**
 * AbstractTreeNode_Test - JUnit tests for {@link AbstractTreeNode}.
 *
 * @author Yiannis Paschalidis.
 * @since 1.0.0
 */
public class AbstractTreeNode_Test {

	@Test
	public void testAdd() {
		TestNode parent = new TestNode();
		TestNode child = new TestNode();

		parent.add(child);
		Assert.assertSame("Incorrect child parent", parent, child.getParent());
		Assert.assertEquals("Incorrect number of children", 1, parent.getChildCount());
		Assert.assertSame("Incorrect child", child, parent.getChildAt(0));
		Assert.assertNull("Parent's parent should be null", parent.getParent());
		Assert.assertEquals("Child should not contain any children", 0, child.getChildCount());

		// Add another node, then move it.
		TestNode grandChild = new TestNode();
		parent.add(grandChild);
		child.add(grandChild);

		Assert.assertSame("Incorrect child parent", parent, child.getParent());
		Assert.assertSame("Incorrect grand child parent", child, grandChild.getParent());
		Assert.assertEquals("Incorrect number of children", 1, parent.getChildCount());
		Assert.assertEquals("Incorrect number of children", 1, child.getChildCount());
		Assert.assertSame("Incorrect child", child, parent.getChildAt(0));
		Assert.assertSame("Incorrect grandChild", grandChild, child.getChildAt(0));
		Assert.assertNull("Parent's parent should be null", parent.getParent());
		Assert.assertEquals("Grandchild should not contain any children", 0, grandChild.
				getChildCount());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddSelf() {
		TestNode parent = new TestNode();
		parent.add(parent);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddAncestor() {
		TestNode parent = new TestNode();
		TestNode child = new TestNode();

		parent.add(child);
		child.add(parent);
	}

	@Test
	public void testChildren() {
		// Test when no children
		TreeNode node = new TestNode();
		Assert.assertFalse("Should not have any children", node.children().hasNext());

		// Test when has children
		node = buildTestTree();
		Iterator<TreeNode> children = node.children();
		Assert.assertEquals("Incorrect child 1", "B", children.next().toString());
		Assert.assertEquals("Incorrect child 2", "C", children.next().toString());
		Assert.assertEquals("Incorrect child 3", "D", children.next().toString());
		Assert.assertFalse("Incorrect number of children", children.hasNext());
	}

	@Test
	public void testBreadthFirst() {
		String expected = "ABCDEFGH";
		String actual = treeToString(buildTestTree());
		Assert.assertEquals("Incorrect breadth-first tree traversal", expected, actual);
	}

	@Test
	public void testDepthFirst() {
		String expected = "EHFBCGDA";

		StringBuffer buf = new StringBuffer();

		for (Iterator<TreeNode> i = buildTestTree().depthFirst(); i.hasNext();) {
			buf.append(i.next());
		}

		Assert.assertEquals("Incorrect depth-first tree traversal", expected, buf.toString());
	}

	@Test
	public void testGetChildAt() {
		TreeNode node = buildTestTree();
		Assert.assertEquals("Incorrect child at 0", "B", node.getChildAt(0).toString());
		Assert.assertEquals("Incorrect child at 1", "C", node.getChildAt(1).toString());
		Assert.assertEquals("Incorrect child at 2", "D", node.getChildAt(2).toString());
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testGetChildAtInvalidIndex() {
		buildTestTree().getChildAt(3);
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testGetChildAtNegativeIndex() {
		buildTestTree().getChildAt(-1);
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testGetChildAtNoChildren() {
		new TestNode().getChildAt(0);
	}

	@Test
	public void testGetIndex() {
		TreeNode node = buildTestTree();
		Assert.assertEquals("Incorrect child index 0", 0, node.getIndex(node.getChildAt(0)));
		Assert.assertEquals("Incorrect child index 1", 1, node.getIndex(node.getChildAt(1)));
		Assert.assertEquals("Incorrect child index 2", 2, node.getIndex(node.getChildAt(2)));
	}

	@Test
	public void testGetIndexNonChild() {
		TreeNode node = buildTestTree();
		Assert.assertEquals("Incorrect child index for non-descendent", -1, node.getIndex(
				new TestNode()));
		Assert.assertEquals("Incorrect child index for grandChild", -1, node.getIndex(getNode(node,
				"E")));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetIndexNullNode() {
		buildTestTree().getIndex(null);
	}

	@Test
	public void testRemove() {
		TreeNode node = buildTestTree();

		// Test removal of nodes which aren't children.
		node.remove(null);
		Assert.assertEquals("Incorrect tree after remove of null", "ABCDEFGH", treeToString(node));

		node.remove(new TestNode());
		Assert.assertEquals("Incorrect tree after remove of non-child", "ABCDEFGH", treeToString(
				node));

		node.remove(getNode(node, "E"));
		Assert.assertEquals("Incorrect tree after remove of non-child", "ABCDEFGH", treeToString(
				node));

		// Test individual node removal
		getNode(node, "D").remove(getNode(node, "G"));
		Assert.assertEquals("Incorrect tree after remove 'D'", "ABCDEFH", treeToString(node));

		// Test branch removal.
		getNode(node, "B").remove(getNode(node, "F"));
		Assert.assertEquals("Incorrect tree after remove 'F'", "ABCDE", treeToString(node));

		// Test removal at root.
		node.remove(getNode(node, "B"));
		Assert.assertEquals("Incorrect tree after remove 'B'", "ACD", treeToString(node));

		// Test removal of all children
		node.remove(getNode(node, "C"));
		node.remove(getNode(node, "D"));
		Assert.assertEquals("Incorrect tree after remove of all children", "A", treeToString(node));
	}

	@Test
	public void testRemoveAll() {
		TreeNode node = buildTestTree();

		// Test remove when there are no children, should not alter tree.
		getNode(node, "C").removeAll();
		Assert.assertEquals("Incorrect tree after remove of all children", "ABCDEFGH", treeToString(node));

		// removeAll with children does not remove the `parent` node.
		getNode(node, "B").removeAll();
		Assert.assertEquals("Incorrect tree after remove of all children", "ABCDG", treeToString(node));
	}

	/**
	 * Handy testing method for returning a String representation of the tree, using a breadth-first iteration.
	 *
	 * @param node the tree to convert.
	 * @return a String representation of the tree.
	 */
	private static String treeToString(final TreeNode node) {
		StringBuffer buf = new StringBuffer();

		for (Iterator<TreeNode> i = node.breadthFirst(); i.hasNext();) {
			buf.append(i.next());
		}

		return buf.toString();
	}

	@Test
	public void testIsDescendant() {
		TreeNode node = buildTestTree();

		Assert.assertFalse("A node is not a descendant of itself", node.isDescendant(node));
		Assert.assertFalse("A parent is not a descendant of a child", node.isDescendant(
				getNode(node, "C")));
		Assert.assertFalse("A node is not a descendant of its sibling", getNode(node, "B").
				isDescendant(getNode(node, "C")));

		Assert.assertTrue("Incorrect isDescendant for child", getNode(node, "B").isDescendant(node));
		Assert.assertTrue("Incorrect isDescendant for grandChild", getNode(node, "F").isDescendant(
				node));
	}

	@Test
	public void testGetLevel() {
		TreeNode node = buildTestTree();
		Assert.assertEquals("Incorrect level for root node", 0, node.getLevel());
		Assert.assertEquals("Incorrect level for child node", 1, getNode(node, "C").getLevel());
		Assert.
				assertEquals("Incorrect level for grand-child node", 2, getNode(node, "G").
						getLevel());
	}

	@Test
	public void testIsLeaf() {
		TreeNode node = buildTestTree();
		Assert.assertFalse("Incorrect isLeaf for non-leaf", node.isLeaf());
		Assert.assertTrue("Incorrect isLeaf for leaf", getNode(node, "C").isLeaf());
	}

	@Test
	public void testIsRelated() {
		TreeNode node = buildTestTree();
		Assert.assertTrue("Incorrect isRelated for related nodes", getNode(node, "H").isRelated(
				getNode(node, "D")));
		Assert.assertFalse("Incorrect isRelated for unrelated node", getNode(node, "H").isRelated(
				new TestNode()));
	}

	@Test
	public void testGetRoot() {
		TreeNode node = buildTestTree();
		Assert.assertSame("Incorrect getRoot for root node", node, node.getRoot());
		Assert.assertSame("Incorrect getRoot for descendant node", node, getNode(node, "H").
				getRoot());
	}

	@Test
	public void testGetPath() {
		TreeNode node = buildTestTree();
		Assert.assertEquals("Incorrect path for root node", "A", pathToString(node.getPath()));
		Assert.assertEquals("Incorrect path for node C", "AC", pathToString(getNode(node, "C").
				getPath()));
		Assert.assertEquals("Incorrect path for node G", "ADG", pathToString(getNode(node, "G").
				getPath()));
		Assert.assertEquals("Incorrect path for node H", "ABFH", pathToString(getNode(node, "H").
				getPath()));
	}

	/**
	 * Retrieves a node with the given data.
	 *
	 * @param root the tree to search.
	 * @param data the data to search for.
	 * @return the node with the given data, or null if not found.
	 */
	private static TreeNode getNode(final TreeNode root, final String data) {
		for (Iterator<TreeNode> i = root.depthFirst(); i.hasNext();) {
			TreeNode node = i.next();

			if (node instanceof TestNode && Util.equals(((TestNode) node).data, data)) {
				return node;
			}
		}

		return null;
	}

	/**
	 * Converts a TreeNode path into a String for testing.
	 *
	 * @param path the path to convert.
	 * @return the converted path.
	 */
	private static String pathToString(final TreeNode[] path) {
		StringBuffer buf = new StringBuffer(path.length);

		for (TreeNode node : path) {
			buf.append(node.toString());
		}

		return buf.toString();
	}

	/**
	 * Builds a tree with the following structure.
	 *
	 * <pre>
	 *          A
	 *         /|\
	 *        B C D
	 *       / \   \
	 *      E   F   G
	 *          |
	 *          H
	 * </pre>
	 *
	 * @return a tree for testing.
	 */
	public TestNode buildTestTree() {
		TestNode nodeA = new TestNode("A");
		TestNode nodeB = new TestNode("B");
		TestNode nodeC = new TestNode("C");
		TestNode nodeD = new TestNode("D");
		TestNode nodeE = new TestNode("E");
		TestNode nodeF = new TestNode("F");
		TestNode nodeG = new TestNode("G");
		TestNode nodeH = new TestNode("H");

		nodeA.add(nodeB);
		nodeA.add(nodeC);
		nodeA.add(nodeD);

		nodeB.add(nodeE);
		nodeB.add(nodeF);

		nodeD.add(nodeG);

		nodeF.add(nodeH);

		return nodeA;
	}

	/**
	 * A trivial implementation of AbstractTreeNode for testing.
	 */
	private static final class TestNode extends AbstractTreeNode {

		/**
		 * Some node data, so we can identify nodes in the tests.
		 */
		private final String data;

		/**
		 * Creates a test node with null data.
		 */
		private TestNode() {
			this(null);
		}

		/**
		 * Creates a test node.
		 *
		 * @param data the test data.
		 */
		private TestNode(final String data) {
			this.data = data;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return data;
		}
	}
}
