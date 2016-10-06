package com.github.bordertech.wcomponents.util;

import com.github.bordertech.wcomponents.MockTreeItemData;
import com.github.bordertech.wcomponents.TreeItemIdNode;
import com.github.bordertech.wcomponents.WTree;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for the {@link TreeItemUtil} component.
 *
 * @author Jonathan Austin
 * @since 1.2.1
 */
public class TreeItemUtil_Test {

	private static final Set<String> CONVERT_RESULT = new HashSet<>(Arrays.asList("A", "B"));

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

		// Mock custom structure
		// A
		// C
		// - C.1
		//   - B (hasChildren)
		// C.1.1 (hasChildren)
		TreeItemIdNode root = TreeItemUtil.convertJsonToTree(MockTreeItemData.TEST_MULTI_JSON);
		Assert.assertNotNull("Root node not returned", root);
		Assert.assertEquals("Root should have three children", 3, root.getChildren().size());

		// A
		TreeItemIdNode node = root.getChildren().get(0);
		Assert.assertEquals("Incorrect node id", "A", node.getItemId());
		Assert.assertTrue("Node should have no children", node.getChildren().isEmpty());
		Assert.assertFalse("Node hasChildren flag incorrect", node.hasChildren());

		// C
		node = root.getChildren().get(1);
		Assert.assertEquals("Incorrect node id", "C", node.getItemId());
		Assert.assertEquals("Node should have one child", 1, node.getChildren().size());
		Assert.assertTrue("Node hasChildren flag incorrect", node.hasChildren());
		// C.1
		node = node.getChildren().get(0);
		Assert.assertEquals("Incorrect node id", "C.1", node.getItemId());
		Assert.assertEquals("Node should have one child", 1, node.getChildren().size());
		Assert.assertTrue("Node hasChildren flag incorrect", node.hasChildren());
		// B
		node = node.getChildren().get(0);
		Assert.assertEquals("Incorrect node id", "B", node.getItemId());
		Assert.assertTrue("Node should have no children", node.getChildren().isEmpty());
		Assert.assertTrue("Node hasChildren flag incorrect", node.hasChildren());

		// C.1.1
		node = root.getChildren().get(2);
		Assert.assertEquals("Incorrect node id", "C.1.1", node.getItemId());
		Assert.assertTrue("Node should have no children", node.getChildren().isEmpty());
		Assert.assertTrue("Node hasChildren flag incorrect", node.hasChildren());
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
		TreeItemIdNode tree = MockTreeItemData.TEST_MULTI_TREE;
		// Tree to JSON
		String json = TreeItemUtil.convertTreeToJson(tree);
		// JSON to Tree
		TreeItemIdNode tree2 = TreeItemUtil.convertJsonToTree(json);
		// Check tree the same
		Assert.assertTrue("Tree should be the same after conversion", TreeItemUtil.isTreeSame(tree, tree2));
	}

	@Test
	public void testIsSameBasicTree() {
		Assert.assertTrue("Tree is same - Basic tree", TreeItemUtil.isTreeSame(MockTreeItemData.TEST_BASIC_TREE, MockTreeItemData.TEST_BASIC_TREE));
	}

	@Test
	public void testIsSameMultiTree() {
		Assert.assertTrue("Tree is same - Multi tree", TreeItemUtil.isTreeSame(MockTreeItemData.TEST_MULTI_TREE, MockTreeItemData.TEST_MULTI_TREE));
	}

	@Test
	public void testIsSameTreesDifferent() {
		Assert.assertFalse("Trees are not the same", TreeItemUtil.isTreeSame(MockTreeItemData.TEST_BASIC_TREE, MockTreeItemData.TEST_MULTI_TREE));
	}

	@Test
	public void testCopyTree() {
		TreeItemIdNode copy = TreeItemUtil.copyTreeNode(MockTreeItemData.TEST_MULTI_TREE);
		Assert.assertTrue("Copy of tree should be the same", TreeItemUtil.isTreeSame(MockTreeItemData.TEST_MULTI_TREE, copy));
	}

	@Test
	public void testConvertToSetWithNull() {
		Assert.assertNull("Convert null value should be null", TreeItemUtil.convertDataToSet(null));
	}

	@Test
	public void testConvertToSetWithSet() {
		Set<String> data = new HashSet<>();
		data.add("A");
		data.add("B");
		Assert.assertEquals("Convert set value should be equal", CONVERT_RESULT, TreeItemUtil.convertDataToSet(data));
	}

	@Test
	public void testConvertToSetWithCollection() {
		Collection<String> data = new ArrayList<>();
		data.add("A");
		data.add("B");
		Assert.assertEquals("Convert Collection value should be equal", CONVERT_RESULT, TreeItemUtil.convertDataToSet(data));
	}

	@Test
	public void testConvertToSetWithCollectionAndNullItem() {
		Collection<String> data = new ArrayList<>();
		data.add("A");
		data.add("B");
		data.add(null);
		Assert.assertEquals("Convert Collection with null value should exclude null item", CONVERT_RESULT, TreeItemUtil.convertDataToSet(data));
	}

	@Test
	public void testConvertToSetWithArray() {
		String[] data = new String[]{"A", "B"};
		Assert.assertEquals("Convert Array value should be equal", CONVERT_RESULT, TreeItemUtil.convertDataToSet(data));
	}

	@Test
	public void testConvertToSetWithArrayAndNullItem() {
		String[] data = new String[]{"A", "B", null};
		Assert.assertEquals("Convert Array with null value should exclude null item", CONVERT_RESULT, TreeItemUtil.convertDataToSet(data));
	}

	@Test
	public void testConvertToSetWithString() {
		Set<String> result = new HashSet<>();
		result.add("A");
		Assert.assertEquals("Convert String should be put as item in Set", result, TreeItemUtil.convertDataToSet("A"));
	}

	@Test
	public void testLoadCustomNodeChildren() {
		WTree tree = MockTreeItemData.setupWTree();

		TreeItemIdNode customNode = new TreeItemIdNode("B");
		customNode.setHasChildren(true);

		TreeItemUtil.loadCustomNodeChildren(customNode, tree);

		Assert.assertTrue("Node hasChildren flag should be true", customNode.hasChildren());
		Assert.assertFalse("Node child list should not be empty", customNode.getChildren().isEmpty());
	}

	@Test
	public void testLoadCustomNodeChildrenNoChildren() {
		WTree tree = MockTreeItemData.setupWTree();

		TreeItemIdNode customNode = new TreeItemIdNode("A");
		customNode.setHasChildren(true);

		TreeItemUtil.loadCustomNodeChildren(customNode, tree);

		Assert.assertFalse("Node hasChildren flag should be false", customNode.hasChildren());
		Assert.assertTrue("Node child list should be empty", customNode.getChildren().isEmpty());
	}

	@Test
	public void testLoadCustomNodeChildrenButAllChildrenInUse() {
		WTree tree = MockTreeItemData.setupWTree();

		TreeItemIdNode customNode = new TreeItemIdNode(null);

		customNode.addChild(new TreeItemIdNode("B.1"));
		customNode.addChild(new TreeItemIdNode("B.2"));

		// Set has children but all of "B"s children have already been used
		TreeItemIdNode nodeB = new TreeItemIdNode("B");
		nodeB.setHasChildren(true);
		customNode.addChild(nodeB);

		tree.setCustomTree(customNode);

		TreeItemUtil.loadCustomNodeChildren(nodeB, tree);

		Assert.assertFalse("NodeB hasChildren flag should be false", nodeB.hasChildren());
		Assert.assertTrue("NodeB child list should be empty", nodeB.getChildren().isEmpty());
	}

	@Test
	public void testCalcExpandedRowsLevel1() {
		WTree tree = MockTreeItemData.setupWTree();
		Set<String> result = new HashSet<>();
		result.add("C");
		Assert.assertEquals("Wrong expanded rows for item at level 1", result, TreeItemUtil.calcExpandedRowsToReachItemId("C", tree));
	}

	@Test
	public void testCalcExpandedRowsLevel2() {
		WTree tree = MockTreeItemData.setupWTree();

		Set<String> result = new HashSet<>();
		result.add("C");
		result.add("C.1");
		Assert.assertEquals("Wrong expanded rows for item at level 2", result, TreeItemUtil.calcExpandedRowsToReachItemId("C.1", tree));
	}

	@Test
	public void testCalcExpandedRowsLevel3() {
		WTree tree = MockTreeItemData.setupWTree();

		Set<String> result = new HashSet<>();
		result.add("C");
		result.add("C.1");
		result.add("C.1.1");
		Assert.assertEquals("Wrong expanded rows for item at level 3", result, TreeItemUtil.calcExpandedRowsToReachItemId("C.1.1", tree));
	}

	@Test
	public void testCalcCustomExpandedRowsLevel1() {
		WTree tree = MockTreeItemData.setupWTreeWithCustom();
		Set<String> result = new HashSet<>();
		result.add("C");
		Assert.assertEquals("Wrong expanded rows for item at level 1", result, TreeItemUtil.calcExpandedRowsToReachItemId("C", tree));
	}

	@Test
	public void testCalcCustomExpandedRowsLevel2() {
		WTree tree = MockTreeItemData.setupWTreeWithCustom();

		Set<String> expanded = new HashSet<>();
		expanded.add("C");
		tree.setExpandedRows(expanded);

		Set<String> result = new HashSet<>();
		result.add("C");
		result.add("C.1");
		Assert.assertEquals("Wrong expanded rows for item at level 2", result, TreeItemUtil.calcExpandedRowsToReachItemId("C.1", tree));
	}

	@Test
	public void testCalcCustomExpandedRowsLevel3() {
		WTree tree = MockTreeItemData.setupWTreeWithCustom();

		Set<String> expanded = new HashSet<>();
		expanded.add("C");
		expanded.add("C.1");
		tree.setExpandedRows(expanded);

		Set<String> result = new HashSet<>();
		result.add("C");
		result.add("C.1");
		result.add("B");
		Assert.assertEquals("Wrong expanded rows for item at level 3", result, TreeItemUtil.calcExpandedRowsToReachItemId("B", tree));
	}

}
