package com.github.bordertech.wcomponents;

import java.io.Serializable;
import junit.framework.Assert;
import org.junit.Test;

/**
 * Junit test case for {@link TableTreeNode}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class TableTreeNode_Test {

	@Test
	public void testData() {
		Serializable data = "TableTreeNode_Test.testData.data";
		TableTreeNode node = new TableTreeNode(data);
		Assert.assertSame("Incorrect data", data, node.getData());
	}

	@Test
	public void testExpandedAccessors() {
		TableTreeNode node = new TableTreeNode(null);
		Assert.assertFalse("Should not be expanded by default", node.isExpanded());
		node.setExpanded(true);
		Assert.assertTrue("Should be expanded after setExpanded(true)", node.isExpanded());
		node.setExpanded(false);
		Assert.assertFalse("Should not be expanded after setExpanded(false)", node.isExpanded());
	}

	@Test
	public void testNextVisible() {
		TableTreeNode root = new TableTreeNode("root");
		TableTreeNode nodeA = new TableTreeNode("a");
		TableTreeNode nodeB = new TableTreeNode("b");
		TableTreeNode nodeC = new TableTreeNode("c");

		root.add(nodeA);
		nodeA.add(nodeB);
		root.add(nodeC);

		Assert.assertNull("Should not have next visible if root is collapsed", root.nextVisible());
		root.setExpanded(true);
		Assert.assertSame("nextVisible for root should be A", nodeA, root.nextVisible());
		Assert.assertSame("nextVisible for A should be C", nodeC, nodeA.nextVisible());
		Assert.assertNull("nextVisible for C should be null", nodeC.nextVisible());

		nodeA.setExpanded(true);
		Assert.assertSame("nextVisible for expanded A should be B", nodeB, nodeA.nextVisible());
	}

	@Test
	public void testNext() {
		TableTreeNode root = new TableTreeNode("root");
		TableTreeNode nodeA = new TableTreeNode("a");
		TableTreeNode nodeB = new TableTreeNode("b");
		TableTreeNode nodeC = new TableTreeNode("c");

		root.add(nodeA);
		nodeA.add(nodeB);
		root.add(nodeC);

		Assert.assertSame("next for root should be A", nodeA, root.next());
		Assert.assertSame("next for A should be B", nodeB, nodeA.next());
		Assert.assertSame("next for B should be C", nodeC, nodeB.next());
		Assert.assertNull("next for C should be null", nodeC.next());

		nodeA.setExpanded(true);
	}

	@Test
	public void testGetRowIndex() {
		TableTreeNode root = new TableTreeNode("root");
		TableTreeNode nodeA = new TableTreeNode("a");
		TableTreeNode nodeB = new TableTreeNode("b");
		TableTreeNode nodeC = new TableTreeNode("c");

		root.add(nodeA);
		nodeA.add(nodeB);
		root.add(nodeC);

		Assert.assertEquals("incorrect index for root", 0, root.getRowIndex());
		Assert.assertEquals("incorrect index for A", 1, nodeA.getRowIndex());
		Assert.assertEquals("incorrect index for B", 2, nodeB.getRowIndex());
		Assert.assertEquals("incorrect index for C", 3, nodeC.getRowIndex());

		nodeA.setExpanded(true);
	}

	@Test
	public void testRendererClassAccessors() {
		TableTreeNode node = new TableTreeNode(null, WText.class, true);
		Assert.assertSame("Incorrect renderer class", WText.class, node.getRendererClass());

		node.setRendererClass(WTextField.class);
		Assert.assertSame("Incorrect renderer class after setRenderClass", WTextField.class, node.
				getRendererClass());
	}

	@Test
	public void testRendererSpansAllColsAccessors() {
		TableTreeNode node = new TableTreeNode(null, WText.class, true);
		Assert.assertTrue("RendererSpansAllCols should be true", node.isRendererSpansAllCols());

		node.setRendererSpansAllCols(false);
		Assert.assertFalse("RendererSpansAllCols should be false after set", node.
				isRendererSpansAllCols());
	}

	@Test
	public void testGetNodeCount() {
		TableTreeNode root = new TableTreeNode("root");
		TableTreeNode nodeA = new TableTreeNode("a");
		TableTreeNode nodeB = new TableTreeNode("b");
		TableTreeNode nodeC = new TableTreeNode("c");

		Assert.assertEquals("Incorrect node count", 0, root.getNodeCount());
		root.add(nodeA);
		Assert.assertEquals("Incorrect node count", 1, root.getNodeCount());
		nodeA.add(nodeB);
		Assert.assertEquals("Incorrect node count", 2, root.getNodeCount());
		root.add(nodeC);
		Assert.assertEquals("Incorrect node count", 3, root.getNodeCount());

		root.remove(nodeA);
		Assert.assertEquals("Incorrect node count", 1, root.getNodeCount());
	}
}
