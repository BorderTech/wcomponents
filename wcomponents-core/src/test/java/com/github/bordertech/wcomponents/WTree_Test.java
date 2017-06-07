package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.util.TreeItemUtil;
import com.github.bordertech.wcomponents.util.mock.MockRequest;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import junit.framework.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link WTree}.
 *
 * @author Jonathan Austin
 * @since 1.2.0
 */
public class WTree_Test extends AbstractWComponentTestCase {

	@Test
	public void testConstructorDefault() {
		WTree tree = new WTree();
		Assert.assertEquals("Default Const - type", WTree.Type.VERTICAL, tree.getType());
	}

	@Test
	public void testConstructor1() {
		WTree tree = new WTree(WTree.Type.HORIZONTAL);
		Assert.assertEquals("Constructor1 - type", WTree.Type.HORIZONTAL, tree.getType());
	}

	@Test
	public void testTypeAccessors() {
		assertAccessorsCorrect(new WTree(), "type", WTree.Type.VERTICAL, WTree.Type.HORIZONTAL, WTree.Type.VERTICAL);
	}

	@Test
	public void testTypeNull() {
		WTree tree = new WTree();
		tree.setType(null);
		Assert.assertEquals("Setting a null type should default to Vertical", WTree.Type.VERTICAL, tree.getType());
	}

	@Test
	public void testMarginAccessors() {
		assertAccessorsCorrect(new WTree(), "margin", null, new Margin(1), new Margin(2));
	}

	@Test
	public void testTreeModelAccessors() {
		assertAccessorsCorrect(new WTree(), "treeModel", EmptyTreeItemModel.INSTANCE, new MockTreeItemData.MyTestModel(Collections.EMPTY_LIST), new MockTreeItemData.MyTestModel(Collections.EMPTY_LIST));
	}

	@Test
	public void testOpenActionAccessors() {
		assertAccessorsCorrect(new WTree(), "openAction", null, new TestAction(), new TestAction());
	}

	@Test
	public void testShuffleAccessors() {
		assertAccessorsCorrect(new WTree(), "shuffle", false, true, false);
	}

	@Test
	public void testShuffleActionAccessors() {
		assertAccessorsCorrect(new WTree(), "shuffleAction", null, new TestAction(), new TestAction());
	}

	@Test
	public void testSelectModeAccessors() {
		assertAccessorsCorrect(new WTree(), "selectMode", WTree.SelectMode.SINGLE, WTree.SelectMode.MULTIPLE, WTree.SelectMode.SINGLE);
	}

	@Test
	public void testSelectModeNull() {
		WTree tree = new WTree();
		tree.setSelectMode(null);
		Assert.assertEquals("Setting a null select mode should default to Single", WTree.SelectMode.SINGLE, tree.getSelectMode());
	}

	@Test
	public void testExpandModeAccessors() {
		assertAccessorsCorrect(new WTree(), "expandMode", WTree.ExpandMode.CLIENT, WTree.ExpandMode.DYNAMIC, WTree.ExpandMode.LAZY);
	}

	@Test
	public void testExpandModeNull() {
		WTree tree = new WTree();
		tree.setExpandMode(null);
		Assert.assertEquals("Setting a null expand mode should default to Client", WTree.ExpandMode.CLIENT, tree.getExpandMode());
	}

	@Test
	public void testSelectedRowsAccessors() {
		assertAccessorsCorrect(new WTree(), "selectedRows", Collections.EMPTY_SET, new HashSet<>(Arrays.asList("A", "B")), new HashSet<>(Arrays.asList("X", "Y")));
	}

	@Test
	public void testExpandedRowsAccessors() {
		assertAccessorsCorrect(new WTree(), "expandedRows", Collections.EMPTY_SET, new HashSet<>(Arrays.asList("A", "B")), new HashSet<>(Arrays.asList("X", "Y")));
	}

	@Test
	public void testGetValueAsString() {

		WTree tree = new WTree();
		Assert.assertNull("Value as String should be null by default", tree.getValueAsString());

		// Null
		tree.setSelectedRows(null);
		Assert.assertNull("Value as String should be null for null selected", tree.getValueAsString());

		// Empty
		tree.setSelectedRows(Collections.EMPTY_SET);
		Assert.assertNull("Value as String should be null for empty", tree.getValueAsString());

		// Selected
		tree.setSelectedRows(MockTreeItemData.SELECTED_B_2);
		Assert.assertEquals("Value as String should be BEAN_B_2", MockTreeItemData.BEAN_B_2.getId(), tree.getValueAsString());
	}

	@Test
	public void testDoHandleRequestNothingSelected() {
		WTree tree = MockTreeItemData.setupWTree();
		MockRequest request = setupRequest(tree);
		boolean changed = tree.doHandleRequest(request);
		Assert.assertEquals("Should have no option selected", Collections.EMPTY_SET, tree.getSelectedRows());
		Assert.assertFalse("doHandleRequest should have returned false", changed);
	}

	@Test
	public void testDoHandleRequestWithValidOption() {
		WTree tree = MockTreeItemData.setupWTree();
		MockRequest request = setupRequest(tree, MockTreeItemData.BEAN_B_2.getId());
		boolean changed = tree.doHandleRequest(request);
		Assert.assertEquals("Should have option BEAN_B_2 selected", MockTreeItemData.SELECTED_B_2, tree.getSelectedRows());
		Assert.assertTrue("doHandleRequest should have returned true", changed);
	}

	@Test
	public void testDoHandleRequestWithInvalidOption() {
		WTree tree = MockTreeItemData.setupWTree();
		tree.setSelectedRows(MockTreeItemData.SELECTED_B_C_1);
		MockRequest request = setupRequest(tree, "XX");
		boolean changed = tree.doHandleRequest(request);
		Assert.assertEquals("Should have no option selected", Collections.EMPTY_SET, tree.getSelectedRows());
		Assert.assertTrue("doHandleRequest should have returned true", changed);
	}

	@Test
	public void testDoHandleRequestWithSameOption() {
		WTree tree = MockTreeItemData.setupWTree();
		tree.setSelectedRows(MockTreeItemData.SELECTED_B_2);
		MockRequest request = setupRequest(tree, MockTreeItemData.BEAN_B_2.getId());
		boolean changed = tree.doHandleRequest(request);
		Assert.assertEquals("Should have option BEAN_B_2 selected", MockTreeItemData.SELECTED_B_2, tree.getSelectedRows());
		Assert.assertFalse("doHandleRequest should have returned false", changed);
	}

	@Test
	public void testGetRequestValue() {

		WTree tree = MockTreeItemData.setupWTree();
		tree.setSelectedRows(MockTreeItemData.SELECTED_B_C_1);

		// Empty Request, should return current selected
		MockRequest request = new MockRequest();
		Assert.assertEquals("Should return the current selected option for an empty request", MockTreeItemData.SELECTED_B_C_1, tree.getRequestValue(request));

		// OptionB_2 on the Request
		request = setupRequest(tree, MockTreeItemData.BEAN_B_2.getId());
		Assert.assertEquals("getRequestValue should return the option on the request", MockTreeItemData.SELECTED_B_2,
				tree.getRequestValue(request));
	}

	@Test
	public void testIsEmpty() {

		WTree tree = MockTreeItemData.setupWTree();
		Assert.assertTrue("Should be empty by default.", tree.isEmpty());

		tree.setSelectedRows(MockTreeItemData.SELECTED_B_C_1);
		Assert.assertFalse("Should not be empty with selected rows.", tree.isEmpty());
	}

	@Test
	public void testCustomTreeAccessors() {
		assertAccessorsCorrect(new WTree(), "customTree", null, new TreeItemIdNode("A"), new TreeItemIdNode("B"));
	}

	@Test
	public void testCustomTreeJson() {
		WTree tree = new WTree();
		tree.setCustomTree(MockTreeItemData.TEST_BASIC_JSON);
		TreeItemIdNode custom = tree.getCustomTree();
		Assert.assertTrue("Invalid tree returned for JSON.", TreeItemUtil.isTreeSame(custom, MockTreeItemData.TEST_BASIC_TREE));
	}

	@Test
	public void testGetItemImageUrlWithNoImage() {
		WTree tree = MockTreeItemData.setupWTree();
		// Idx for Item B
		List<Integer> idx = Arrays.asList(1);
		TreeItemImage image = tree.getTreeModel().getItemImage(idx);
		String itemId = tree.getTreeModel().getItemId(idx);
		String url = tree.getItemImageUrl(image, itemId);
		Assert.assertNull("Url for an item with no image should be null.", url);
	}

	@Test
	public void testGetItemImageUrlWithUrl() {
		WTree tree = MockTreeItemData.setupWTree();
		// Idx for Item A
		List<Integer> idx = Arrays.asList(0);
		TreeItemImage image = tree.getTreeModel().getItemImage(idx);
		String itemId = tree.getTreeModel().getItemId(idx);
		String url = tree.getItemImageUrl(image, itemId);
		Assert.assertTrue("Incorrect Url for an item with an image.", url.contains("URL-A"));
	}

	@Test
	public void testSetModelNoUserContext() {
		WTree tree = new WTree();
		tree.setTreeModel(new MockTreeItemData.MyTestModel(MockTreeItemData.DATA));
	}

	@Test
	public void testLoadCustomNodeChildren() {
		WTree tree = MockTreeItemData.setupWTree();

		TreeItemIdNode customNode = new TreeItemIdNode("B");
		customNode.setHasChildren(true);

		TreeItemIdNode root = new TreeItemIdNode(null);
		root.addChild(customNode);
		tree.setCustomTree(root);
		tree.checkExpandedCustomNodes();

		org.junit.Assert.assertTrue("Node hasChildren flag should be true", customNode.hasChildren());
		org.junit.Assert.assertFalse("Node child list should not be empty", customNode.getChildren().isEmpty());
	}

	@Test
	public void testLoadCustomNodeChildrenNoChildren() {
		WTree tree = MockTreeItemData.setupWTree();

		TreeItemIdNode customNode = new TreeItemIdNode("A");
		customNode.setHasChildren(true);

		TreeItemIdNode root = new TreeItemIdNode(null);
		root.addChild(customNode);
		tree.setCustomTree(root);
		tree.checkExpandedCustomNodes();

		org.junit.Assert.assertFalse("Node hasChildren flag should be false", customNode.hasChildren());
		org.junit.Assert.assertTrue("Node child list should be empty", customNode.getChildren().isEmpty());
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

		TreeItemIdNode root = new TreeItemIdNode(null);
		root.addChild(customNode);
		tree.setCustomTree(root);
		tree.checkExpandedCustomNodes();

		org.junit.Assert.assertFalse("NodeB hasChildren flag should be false", nodeB.hasChildren());
		org.junit.Assert.assertTrue("NodeB child list should be empty", nodeB.getChildren().isEmpty());
	}

	/**
	 * Helper for making mock requests.
	 * @param tree the WTree being tested
	 * @param options the options being tested
	 * @return a mock request
	 */
	private MockRequest setupRequest(final WTree tree, final String... options) {
		MockRequest request = new MockRequest();
		request.setParameter(tree.getId() + "-h", "x");
		if (options != null) {
			int i = 0;
			String prefix = tree.getItemIdPrefix();
			for (String opt : options) {
				options[i++] = prefix + opt;
			}
			request.setParameter(tree.getId(), options);
		}
		return request;
	}

}
