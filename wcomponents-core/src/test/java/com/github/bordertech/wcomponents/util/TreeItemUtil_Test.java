package com.github.bordertech.wcomponents.util;

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

	private static final String TEST_MULTI_JSON = "{\"root\":[{\"id\":\"A\"},{\"id\":\"B\",\"items\":[{\"id\":\"B.1\",\"items\":[{\"id\":\"B.1.1\"}]},{\"id\":\"B.2\"}]},{\"id\":\"C\"}]}";

	private static final TreeItemIdNode TEST_MULTI_TREE = createTreeMulti();

	private static final String TEST_BASIC_JSON = "{\"root\":[{\"id\":\"A\"}]}";

	private static final TreeItemIdNode TEST_BASIC_TREE = createTreeBasic();

	@Test
	public void testConvertTreeToJsonEmpty() {
		String json = TreeItemUtil.convertTreeToJson(new TreeItemIdNode(null));
		Assert.assertEquals("Empty node - json should be emtpy", "{}", json);
	}

	@Test
	public void testConvertTreeToJsonBasic() {
		String json = TreeItemUtil.convertTreeToJson(TEST_BASIC_TREE);
		Assert.assertEquals("Invalid json returned for basic tree", TEST_BASIC_JSON, json);
	}

	@Test
	public void testConvertTreeToJsonMulti() {
		String json = TreeItemUtil.convertTreeToJson(TEST_MULTI_TREE);
		Assert.assertEquals("Invalid json returned for multiple rows", TEST_MULTI_JSON, json);
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
		TreeItemIdNode root = TreeItemUtil.convertJsonToTree(TEST_BASIC_JSON);
		Assert.assertNotNull("Root node not returned", root);
		Assert.assertEquals("Root should have one child", 1, root.getChildren().size());
		TreeItemIdNode node = root.getChildren().get(0);
		Assert.assertEquals("Incorrect node id", "A", node.getItemId());
		Assert.assertTrue("Node should have no children", node.getChildren().isEmpty());
	}

	@Test
	public void testConvertJsonToTreeMultipleLevel() {

		TreeItemIdNode root = TreeItemUtil.convertJsonToTree(TEST_MULTI_JSON);
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
		TreeItemIdNode tree = TreeItemUtil.convertJsonToTree(TEST_MULTI_JSON);
		// Tree back to JSON
		String json = TreeItemUtil.convertTreeToJson(tree);
		Assert.assertEquals("JSON should be the same after conversion", TEST_MULTI_JSON, json);
	}

	@Test
	public void testConvertTreeToJsonToTree() {
		TreeItemIdNode tree = createTreeMulti();
		// Tree to JSON
		String json = TreeItemUtil.convertTreeToJson(tree);
		// JSON to Tree
		TreeItemIdNode tree2 = TreeItemUtil.convertJsonToTree(json);
		// Check tree the same
		Assert.assertTrue("Tree should be the same after conversion", TreeItemUtil.isTreeSame(tree, tree2));
	}

	/**
	 * @return a tree with multiple levels
	 */
	private static TreeItemIdNode createTreeMulti() {

		TreeItemIdNode root = new TreeItemIdNode(null);

		// A
		root.addChild(new TreeItemIdNode("A"));

		// B
		TreeItemIdNode nodeB = new TreeItemIdNode("B");
		root.addChild(nodeB);
		// B.1
		TreeItemIdNode nodeB1 = new TreeItemIdNode("B.1");
		nodeB.addChild(nodeB1);
		// B.1.1
		nodeB1.addChild(new TreeItemIdNode("B.1.1"));
		// B.2
		nodeB.addChild(new TreeItemIdNode("B.2"));

		// C
		root.addChild(new TreeItemIdNode("C"));

		return root;
	}

	@Test
	public void testIsSameBasicTree() {
		Assert.assertTrue("Tree is same - Basic tree", TreeItemUtil.isTreeSame(createTreeBasic(), createTreeBasic()));
	}

	@Test
	public void testIsSameMultiTree() {
		Assert.assertTrue("Tree is same - Multi tree", TreeItemUtil.isTreeSame(createTreeMulti(), createTreeMulti()));
	}

	@Test
	public void testIsSameTreesDifferent() {
		Assert.assertFalse("Trees are not the same", TreeItemUtil.isTreeSame(createTreeMulti(), createTreeBasic()));
	}

	@Test
	public void testCopyTree() {
		TreeItemIdNode copy = TreeItemUtil.copyTreeNode(TEST_MULTI_TREE);
		Assert.assertTrue("Copy of tree should be the same", TreeItemUtil.isTreeSame(TEST_MULTI_TREE, copy));
	}

	/**
	 *
	 * @return a basic tree with one level
	 */
	private static TreeItemIdNode createTreeBasic() {
		TreeItemIdNode root = new TreeItemIdNode(null);
		// A
		root.addChild(new TreeItemIdNode("A"));
		return root;
	}

}
