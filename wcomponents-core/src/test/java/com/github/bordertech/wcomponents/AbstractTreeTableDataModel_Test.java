package com.github.bordertech.wcomponents;

import java.util.List;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for {@link AbstractTreeTableDataModel}.
 *
 * @author Anthony O'Connor
 * @since 1.0.0
 */
public class AbstractTreeTableDataModel_Test {

	/**
	 * test row index for mock TableTreeNode.
	 */
	private static final int TEST_ROW_INDEX = 42;

	/**
	 * test tableTreeNode.
	 */
	private TableTreeNode node = null;

	/**
	 * test TreeTableDataModel.
	 */
	private TreeTableDataModelTest model = null;

	@Before
	public void setUp() {
		node = new TableTreeNode(null);
		model = new TreeTableDataModelTest(node);
	}

	/**
	 * test constructor.
	 */
	@Test
	public void testConstructor() {
		Assert.assertEquals("should have successfully set the root node when constructed", node,
				model.getRootNode());
	}

	/**
	 * test getNodeAtLine. default case - null.
	 */
	@Test
	public void testGetNodeAtLine() {
		Assert.assertNull("should get null", model.getNodeAtLine(0));
	}

	/**
	 * test getNodeAtLine - for a non empty list of TreeNodes.
	 */
	@Test
	public void testGetNodeAtLineNonEmptyList() {
		TableTreeNode root = new TableTreeNode("root");
		TableTreeNode nodeA = new TableTreeNode("a");
		TableTreeNode nodeB = new TableTreeNode("b");
		TableTreeNode nodeC = new TableTreeNode("c");
		root.add(nodeA);
		nodeA.add(nodeB);
		root.add(nodeC);
		AbstractTreeTableDataModel modelTwo = new TreeTableDataModelTest(root);

		TableTreeNode resultNode = modelTwo.getNodeAtLine(1);

		Assert.assertNotNull("should get non null for non empty list input", resultNode);
		Assert.assertEquals("should return second node added when constructed ", nodeB, resultNode);
	}

	/**
	 * test getRowCount.
	 */
	@Test
	public void testGetRowCount() {
		Assert.assertEquals("row count for node should be 0", 0, model.getRowCount());
	}

	/**
	 * test getValueAt.
	 */
	@Test
	public void testGetValueAt() {
		Assert.assertEquals("should return test set index set in root object method", Integer.
				valueOf(TEST_ROW_INDEX), model
				.getValueAt(0, 0));
	}

	/**
	 * test getRowHeader.
	 */
	@Test
	public void testGetRowHeader() {
		final int testInt = 0;
		Assert.assertNull("should return null for any input", model.getRowHeader(testInt));
	}

	/**
	 * test isSortable.
	 *
	 * @since 1.0.0
	 */
	@Test
	public void testIsSortable() {
		Assert.assertFalse("currently returns false", model.isSortable(0));
	}

	/**
	 * test isDisabled.
	 */
	@Test
	public void testIsDisabled() {
		Assert.assertFalse("currently returns false", model.isDisabled(0));
	}

	/**
	 * test isSelectable.
	 */
	@Test
	public void testIsSelectable() {
		Assert.assertTrue("currently returns true", model.isSelectable(0));
	}

	/**
	 * test isCellEditable.
	 */
	@Test
	public void testIsCellEditable() {
		Assert.assertFalse("currently returns false", model.isCellEditable(0, 0));
	}

	/**
	 * test getFilterValues.
	 */
	@Test
	public void testGetFilterValues() {
		List<String> filterValues = model.getFilterValues(0);
		Assert.assertNull("filter values currently returned as null", filterValues);
	}

	/**
	 * test getRootNode.
	 */
	@Test
	public void testGetRootNode() {
		Assert.assertEquals("should return root node set", node, model.getRootNode());
	}

	/**
	 *
	 * mock TreeTableDataModel class for testing.
	 *
	 * @author Anthony O'Connor
	 * @since 1.0.0
	 */
	private static final class TreeTableDataModelTest extends AbstractTreeTableDataModel {

		/**
		 * @param root the root node
		 */
		private TreeTableDataModelTest(final TableTreeNode root) {
			super(root);
		}

		@Override
		public Object getValueAt(final TableTreeNode treeNode, final int col) {
			return TEST_ROW_INDEX;
		}
	}
}
