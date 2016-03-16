package com.github.bordertech.wcomponents.util;

import com.github.bordertech.wcomponents.TreeItemIdNode;
import com.github.bordertech.wcomponents.WTree;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.github.bordertech.wcomponents.TreeItemModel;

/**
 * Utility methods for {@link WTree} and its tree items.
 *
 * @author Jonathan Austin
 * @since 1.0.3
 */
public final class TreeItemUtil {

	/**
	 * Prevent instantiation of utility class.
	 */
	private TreeItemUtil() {
	}

	/**
	 * @param jsonString the string of JSON to convert to a custom tree of nodes
	 * @return the custom tree structure of item ids
	 */
	public static TreeItemIdNode convertJsonToTree(final String jsonString) {

		JsonParser parser = new JsonParser();
		JsonObject json = parser.parse(jsonString).getAsJsonObject();

		TreeItemIdNode root = new TreeItemIdNode(null);

		JsonArray children = json.getAsJsonArray("root");
		for (int i = 0; i < children.size(); i++) {
			JsonObject child = children.get(i).getAsJsonObject();
			processJsonToTree(root, child);
		}

		return root;
	}

	/**
	 * Iterate over the JSON objects to create the tree structure.
	 *
	 * @param parentNode the parent node
	 * @param json the current JSON object
	 */
	private static void processJsonToTree(final TreeItemIdNode parentNode, final JsonObject json) {

		String id = json.getAsJsonPrimitive("id").getAsString();

		TreeItemIdNode node = new TreeItemIdNode(id);
		parentNode.addChild(node);

		JsonArray children = json.getAsJsonArray("items");
		for (int i = 0; i < children.size(); i++) {
			JsonObject child = children.get(i).getAsJsonObject();
			processJsonToTree(node, child);
		}
	}

	/**
	 *
	 * @param tree1 the first tree to compare
	 * @param tree2 the second tree to compare
	 * @return true if the trees match
	 */
	public static boolean isTreeSame(final TreeItemIdNode tree1, final TreeItemIdNode tree2) {

		// Check IDs match
		if (!Util.equals(tree1.getItemId(), tree2.getItemId())) {
			return false;
		}

		// Check have same number of children
		if (tree1.getChildren().size() != tree2.getChildren().size()) {
			return false;
		}

		// Check child IDs match
		for (int i = 0; i < tree1.getChildren().size(); i++) {
			if (!isTreeSame(tree1.getChildren().get(i), tree2.getChildren().get(i))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * @param node the node to copy
	 * @return a copy of the node
	 */
	public static TreeItemIdNode copyTreeNode(final TreeItemIdNode node) {
		TreeItemIdNode copy = new TreeItemIdNode(node.getItemId());
		copy.setHasChildren(node.hasChildren());

		for (TreeItemIdNode childItem : node.getChildren()) {
			TreeItemIdNode childCopy = copyTreeNode(childItem);
			copy.addChild(childCopy);
		}
		return copy;
	}

	/**
	 * Convert the data to a set (if necessary).
	 *
	 * @param data the data to convert to a set
	 * @return the data converted to a set
	 */
	public static Set<String> convertDataToSet(final Object data) {
		if (data == null) {
			return null;
		} else if (data instanceof Set) {
			return (Set<String>) data;
		} else if (data instanceof Collection) {
			Collection items = (Collection) data;
			Set<String> set = new HashSet<>();
			for (Object item : items) {
				if (item != null) {
					set.add(item.toString());
				}
			}
			return set;
		} else if (data instanceof Object[]) {
			Object[] items = (Object[]) data;
			Set<String> set = new HashSet<>();
			for (Object item : items) {
				if (item != null) {
					set.add(item.toString());
				}
			}
			return set;
		} else {
			Set<String> set = new HashSet<>();
			set.add(data.toString());
			return set;
		}
	}

	/**
	 * @param custom the custom root node.
	 *
	 * @return the map containing the map of custom items to their node in the tree
	 */
	public static Map<String, TreeItemIdNode> createCustomIdMap(final TreeItemIdNode custom) {
		Map<String, TreeItemIdNode> map = new HashMap<>();
		if (custom != null) {
			processCustomIdMapping(map, custom);
		}
		return Collections.unmodifiableMap(map);
	}

	/**
	 * Iterate over the custom tree structure to add entries to the map.
	 *
	 * @param map the map of custom items and their node
	 * @param node the current node being processed
	 */
	private static void processCustomIdMapping(final Map<String, TreeItemIdNode> map, final TreeItemIdNode node) {
		String itemId = node.getItemId();
		if (!Util.empty(itemId)) {
			map.put(itemId, node);
		}

		for (TreeItemIdNode childItem : node.getChildren()) {
			processCustomIdMapping(map, childItem);
		}
	}

	/**
	 * @param tree the tree component to create a map of item ids
	 * @return the map of item ids to their row index.
	 */
	public static Map<String, List<Integer>> createItemIdIndexMap(final WTree tree) {
		Map<String, List<Integer>> map = new HashMap<>();
		TreeItemModel treeModel = tree.getTreeModel();
		int rows = treeModel.getRowCount();
		Set<String> expanded = tree.getExpandedRows();
		WTree.ExpandMode mode = tree.getExpandMode();

		for (int i = 0; i < rows; i++) {
			List<Integer> index = new ArrayList<>();
			index.add(i);
			processItemIdIndexMapping(map, index, treeModel, mode, expanded);
		}
		return Collections.unmodifiableMap(map);
	}

	/**
	 * Iterate through the table model to add the item ids and their row index.
	 *
	 * @param map the map of item ids
	 * @param rowIndex the current row index
	 * @param treeModel the tree model
	 * @param mode the expand mode
	 * @param expandedRows the set of expanded rows
	 */
	private static void processItemIdIndexMapping(final Map<String, List<Integer>> map, final List<Integer> rowIndex, final TreeItemModel treeModel, final WTree.ExpandMode mode, final Set<String> expandedRows) {

		// Add current item
		String id = treeModel.getItemId(rowIndex);
		if (id == null) {
			return;
		}
		map.put(id, rowIndex);

		// Check row is expandable
		if (!treeModel.isExpandable(rowIndex)) {
			return;
		}

		// Check has children
		if (!treeModel.hasChildren(rowIndex)) {
			return;
		}

		// Always add children if CLIENT mode or row is expanded
		boolean addChildren = (mode == WTree.ExpandMode.CLIENT) || (expandedRows != null && expandedRows.contains(
				id));
		if (!addChildren) {
			return;
		}

		// Get actual child count
		int children = treeModel.getChildCount(rowIndex);
		if (children == 0) {
			// Could be there are no children even though hasChildren returned true
			return;
		}

		// Add children by processing each child row
		for (int i = 0; i < children; i++) {
			// Add next level
			List<Integer> nextRow = new ArrayList<>(rowIndex);
			nextRow.add(i);
			processItemIdIndexMapping(map, nextRow, treeModel, mode, expandedRows);
		}
	}

	/**
	 * Update the custom tree node to make sure any nodes that dont have any children in the custom layout have their
	 * children loaded from the tree model (if they have any).
	 *
	 * @param tree the tree component to update its custom tree nodes.
	 */
	public static void updateCustomTreeNodes(final WTree tree) {
		processCustomTreeNodes(tree.getCustomTree(), tree.getTreeModel(), tree.getExpandMode(), tree.getExpandedRows(), tree.getItemIdIndexMap());
	}

	/**
	 * Update the custom tree node to make sure any nodes that dont have any children in the custom layout have their
	 * children loaded from the tree model (if they have any).
	 *
	 * @param node the current node.
	 * @param treeModel the tree model
	 * @param mode the expand mode
	 * @param expandedRows the set of expanded rows
	 * @param mapItemIds the map of item ids to row index
	 */
	private static void processCustomTreeNodes(final TreeItemIdNode node, final TreeItemModel treeModel, final WTree.ExpandMode mode, final Set<String> expandedRows, final Map<String, List<Integer>> mapItemIds) {

		// Node has no children so check if they need to be loaded from the tree model
		if (node.getChildren().isEmpty()) {
			List<Integer> rowIndex = mapItemIds.get(node.getItemId());
			loadCustomNodesFromModel(node, rowIndex, treeModel, mode, expandedRows);
		} else {
			// Check children
			for (TreeItemIdNode child : node.getChildren()) {
				processCustomTreeNodes(child, treeModel, mode, expandedRows, mapItemIds);
			}
		}
	}

	/**
	 * Load the child items from the tree model onto the custom node.
	 *
	 * @param node the node to update
	 * @param rowIndex the current row index
	 * @param treeModel the tree model
	 * @param mode the expand mode
	 * @param expandedRows the set of expanded rows
	 */
	private static void loadCustomNodesFromModel(final TreeItemIdNode node, final List<Integer> rowIndex, final TreeItemModel treeModel, final WTree.ExpandMode mode, final Set<String> expandedRows) {

		// Defualt to no children
		node.setHasChildren(false);

		// Row is not expandable
		if (!treeModel.isExpandable(rowIndex)) {
			return;
		}

		// Row has no children
		if (!treeModel.hasChildren(rowIndex)) {
			return;
		}

		// OK. So has children
		node.setHasChildren(true);

		// Always add children if CLIENT mode or row is expanded
		boolean addChildren = (mode == WTree.ExpandMode.CLIENT) || (expandedRows != null && expandedRows.contains(node.getItemId()));
		if (!addChildren) {
			return;
		}

		// Get actual child count
		int children = treeModel.getChildCount(rowIndex);
		if (children == 0) {
			// Could be there are no children even though hasChildren returned true
			node.setHasChildren(false);
			return;
		}

		// Add children by processing each child row
		for (int i = 0; i < children; i++) {
			// Calc next level index
			List<Integer> nextRow = new ArrayList<>(rowIndex);
			nextRow.add(i);
			// Get child item id
			String nextId = treeModel.getItemId(nextRow);
			// Add child node
			TreeItemIdNode childNode = new TreeItemIdNode(nextId);
			node.addChild(childNode);
			// Process this child node
			loadCustomNodesFromModel(childNode, nextRow, treeModel, mode, expandedRows);
		}

	}

	/**
	 * Convert the row index to its string representation.
	 *
	 * @param row the row index
	 * @return the string representation of the row index
	 */
	public static String rowIndexListToString(final List<Integer> row) {
		if (row == null || row.isEmpty()) {
			return null;
		}

		StringBuilder index = new StringBuilder();
		boolean addDelimiter = false;

		for (Integer lvl : row) {
			if (addDelimiter) {
				index.append('-');
			}
			index.append(lvl);
			addDelimiter = true;
		}

		return index.toString();
	}

}
