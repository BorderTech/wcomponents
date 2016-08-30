package com.github.bordertech.wcomponents.util;

import com.github.bordertech.wcomponents.MockTreeItemData;
import com.github.bordertech.wcomponents.TreeItemIdNode;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for the {@link TreeItemUtil} component.
 *
 * @author Jonathan Austin
 * @since 1.2.1
 */
public class TreeItemUtil_Test {

	@Test
	public void testConvertTreeToJsonEmpty() {
		String json = TreeItemUtil.convertTreeToJson(new TreeItemIdNode(null));
		Assert.assertEquals("Empty node - json should be emtpy", "{}", json);
	}

	@Test
	public void testConvertTreeToJsonBasic() {
		String json = TreeItemUtil.convertTreeToJson(MockTreeItemData.TEST_BASIC_TREE);
		Assert.assertEquals("Invalid json returned for basic tree", MockTreeItemData.TEST_BASIC_JSON, json);
	}

	@Test
	public void testConvertTreeToJsonMulti() {
		String json = TreeItemUtil.convertTreeToJson(MockTreeItemData.TEST_MULTI_TREE);
		Assert.assertEquals("Invalid json returned for multiple rows", MockTreeItemData.TEST_MULTI_JSON, json);
	}

	@Test
	public void testConvertJsonToTreeEmptyString() {
		TreeItemIdNode root = TreeItemUtil.convertJsonToTree("");
		Assert.assertNotNull("Empty string - Root node not returned", root);
		Assert.assertTrue("Empty String - Root should have no children", root.getChildren().isEmpty());
	}

	@Test
	public void testConvertJsonToTreeEmptyJson() {
		TreeItemIdNode root = TreeItemUtil.convertJsonToTree("{}");
		Assert.assertNotNull("No json - Root node not returned", root);
		Assert.assertTrue("No json - Root should have no children", root.getChildren().isEmpty());
	}

	@Test
	public void testConvertJsonToTreeBasic() {
		TreeItemIdNode root = TreeItemUtil.convertJsonToTree(MockTreeItemData.TEST_BASIC_JSON);
		Assert.assertNotNull("Root node not returned", root);
		Assert.assertEquals("Root should have one child", 1, root.getChildren().size());
		TreeItemIdNode node = root.getChildren().get(0);
		Assert.assertEquals("Incorrect node id", "A", node.getItemId());
		Assert.assertTrue("Node should have no children", node.getChildren().isEmpty());
	}

	@Test
	public void testConvertJsonToTreeMultipleLevel() {

		TreeItemIdNode root = TreeItemUtil.convertJsonToTree(MockTreeItemData.TEST_MULTI_JSON);
		Assert.assertNotNull("Root node not returned", root);
		Assert.assertEquals("Root should have three children", 3, root.getChildren().size());

		// A
		TreeItemIdNode node = root.getChildren().get(0);
		Assert.assertEquals("Incorrect node id", "A", node.getItemId());
		Assert.assertTrue("Node should have no children", node.getChildren().isEmpty());

		// B
		node = root.getChildren().get(1);
		Assert.assertEquals("Incorrect node id", "B", node.getItemId());
		Assert.assertEquals("Node should have two children", 2, node.getChildren().size());
		// B.1
		node = node.getChildren().get(0);
		Assert.assertEquals("Incorrect node id", "B.1", node.getItemId());
		Assert.assertEquals("Node should have one child", 1, node.getChildren().size());
		// B.1.1
		node = node.getChildren().get(0);
		Assert.assertEquals("Incorrect node id", "B.1.1", node.getItemId());
		Assert.assertTrue("Node should have no children", node.getChildren().isEmpty());

		// B.2
		node = root.getChildren().get(1).getChildren().get(1);
		Assert.assertEquals("Incorrect node id", "B.2", node.getItemId());
		Assert.assertTrue("Node should have no children", node.getChildren().isEmpty());

		// C
		node = root.getChildren().get(2);
		Assert.assertEquals("Incorrect node id", "C", node.getItemId());
		Assert.assertTrue("Node should have no children", node.getChildren().isEmpty());
	}

	@Test
	public void testConvertJsonToTreeToJson() {
		// JSON to Tree
		TreeItemIdNode tree = TreeItemUtil.convertJsonToTree(MockTreeItemData.TEST_MULTI_JSON);
		// Tree back to JSON
		String json = TreeItemUtil.convertTreeToJson(tree);
		Assert.assertEquals("JSON should be the same after conversion", MockTreeItemData.TEST_MULTI_JSON, json);
	}

	@Test
	public void testConvertTreeToJsonToTree() {
		TreeItemIdNode tree = MockTreeItemData.createTreeMulti();
		// Tree to JSON
		String json = TreeItemUtil.convertTreeToJson(tree);
		// JSON to Tree
		TreeItemIdNode tree2 = TreeItemUtil.convertJsonToTree(json);
		// Check tree the same
		Assert.assertTrue("Tree should be the same after conversion", TreeItemUtil.isTreeSame(tree, tree2));
	}

	@Test
	public void testIsSameBasicTree() {
		Assert.assertTrue("Tree is same - Basic tree", TreeItemUtil.isTreeSame(MockTreeItemData.createTreeBasic(), MockTreeItemData.createTreeBasic()));
	}

	@Test
	public void testIsSameMultiTree() {
		Assert.assertTrue("Tree is same - Multi tree", TreeItemUtil.isTreeSame(MockTreeItemData.createTreeMulti(), MockTreeItemData.createTreeMulti()));
	}

	@Test
	public void testIsSameTreesDifferent() {
		Assert.assertFalse("Trees are not the same", TreeItemUtil.isTreeSame(MockTreeItemData.createTreeMulti(), MockTreeItemData.createTreeBasic()));
	}

	@Test
	public void testCopyTree() {
		TreeItemIdNode copy = TreeItemUtil.copyTreeNode(MockTreeItemData.TEST_MULTI_TREE);
		Assert.assertTrue("Copy of tree should be the same", TreeItemUtil.isTreeSame(MockTreeItemData.TEST_MULTI_TREE, copy));
	}

}
