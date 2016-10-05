package com.github.bordertech.wcomponents.util;

import com.github.bordertech.wcomponents.TreeItemIdNode;
import com.github.bordertech.wcomponents.TreeItemModel;
import com.github.bordertech.wcomponents.WTree;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Utility methods for {@link WTree} and its tree items.
 *
 * @author Jonathan Austin
 * @since 1.0.3
 */
public final class TreeItemUtil {

	/**
	 * The logger instance for this class.
	 */
	private static final Log LOG = LogFactory.getLog(TreeItemUtil.class);

	/**
	 * Row index delimiter.
	 */
	public static final String INDEX_DELIMITER = "-";

	/**
	 * Prevent instantiation of utility class.
	 */
	private TreeItemUtil() {
	}

	/**
	 * @param root the root tree item node
	 * @return the tree item node as a JSON string
	 */
	public static String convertTreeToJson(final TreeItemIdNode root) {

		JsonObject json = new JsonObject();

		if (root.hasChildren()) {
			JsonArray rootArray = new JsonArray();
			json.add("root", rootArray);
			// Process nodes
			for (TreeItemIdNode child : root.getChildren()) {
				processTreeToJson(rootArray, child);
			}
		}

		Gson gson = new Gson();
		return gson.toJson(json);
	}

	/**
	 * @param jsonString the string of JSON to convert to a custom tree of nodes
	 * @return the custom tree structure of item ids
	 */
	public static TreeItemIdNode convertJsonToTree(final String jsonString) {

		TreeItemIdNode root = new TreeItemIdNode(null);
		if (Util.empty(jsonString)) {
			return root;
		}

		JsonParser parser = new JsonParser();
		JsonObject json = parser.parse(jsonString).getAsJsonObject();

		JsonArray children = json.getAsJsonArray("root");
		if (children != null) {
			for (int i = 0; i < children.size(); i++) {
				JsonObject child = children.get(i).getAsJsonObject();
				processJsonToTree(root, child);
			}
		}

		return root;
	}

	/**
	 * Load the children of a custom node that was flagged as having children.
	 *
	 * @param node the node to process
	 * @param tree the tree component
	 */
	public static void loadCustomNodeChildren(final TreeItemIdNode node, final WTree tree) {

		// Check node was flagged as having children or already has children
		if (!node.hasChildren() || !node.getChildren().isEmpty()) {
			return;
		}

		// Get the row index for the node
		String itemId = node.getItemId();
		List<Integer> rowIndex = tree.getItemIdIndexMap().get(itemId);

		// Get the tree item model
		TreeItemModel model = tree.getTreeModel();

		// Check tree item is still expandable and has children
		if (!model.isExpandable(rowIndex) || !model.hasChildren(rowIndex)) {
			node.setHasChildren(false);
			return;
		}

		Map<String, TreeItemIdNode> mapIds = tree.getCustomIdMap();

		// Check actual child count (could have no children even though hasChildren returned true)
		int count = model.getChildCount(rowIndex);
		if (count <= 0) {
			node.setHasChildren(false);
			return;
		}

		// Add children of item to the node tree
		boolean childAdded = false;
		for (int i = 0; i < count; i++) {
			List<Integer> childIdx = new ArrayList<>(rowIndex);
			childIdx.add(i);
			String childItemId = model.getItemId(rowIndex);
			// Check the child item is not already in the custom tree
			if (mapIds.containsKey(childItemId)) {
				continue;
			}
			TreeItemIdNode childNode = new TreeItemIdNode(childItemId);
			childNode.setHasChildren(model.hasChildren(rowIndex));
			node.addChild(childNode);
			childAdded = true;
		}
		// This could happen if all the children have been used in the custom map
		if (!childAdded) {
			node.setHasChildren(false);
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
	 * <p>
	 * Removes null items in the conversion.
	 * </p>
	 *
	 * @param data the data to convert to a set
	 * @return the data converted to a set
	 */
	public static Set<String> convertDataToSet(final Object data) {
		if (data == null) {
			return null;
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
	 * Find the item ids of the expanded rows to reach an item id in a custom tree.
	 * <p>
	 * It is assumed the parent nodes of the item id are already expanded.
	 * </p>
	 *
	 * @param itemId the item id of node to search for
	 * @param tree the WTree component
	 * @return the set of expanded rows to reach the item id
	 */
	public static Set<String> calcCustomExpandedRowsToReachItemId(final String itemId, final WTree tree) {
		Set<String> expanded = new HashSet<>();

		TreeItemIdNode root = tree.getCustomTree();
		Set<String> currentExpanded = tree.getExpandedRows();

		// Process top level
		if (root != null) {
			for (TreeItemIdNode node : root.getChildren()) {
				if (processCustomExpandedRowsForItemId(expanded, node, itemId, currentExpanded)) {
					break;
				}
			}

		}

		return Collections.unmodifiableSet(expanded);
	}

	/**
	 * Create the set of expanded rows to reach a particular row index (including the row index).
	 * <p>
	 * It is assumed the parent nodes of the item id are already expanded.
	 * </p>
	 *
	 * @param rowIndex the index of the item id to create the set of parent indexes.
	 * @param model the tree model being processed
	 * @return the set of parent row indexes including the rowIndex.
	 */
	public static Set<String> calcExpandedRowsToReachIndex(final List<Integer> rowIndex, final TreeItemModel model) {

		Set<String> expanded = new HashSet<>();

		// Extract the parent indexes and corresponding item ids
		for (int i = 1; i <= rowIndex.size(); i++) {
			List<Integer> index = rowIndex.subList(0, i);
			String itemId = model.getItemId(index);
			expanded.add(itemId);
		}

		return Collections.unmodifiableSet(expanded);
	}

	/**
	 * Map item ids to the custom tree node.
	 *
	 * @param custom the custom root node.
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
	 * Map item ids to the row index. Only include expanded items.
	 *
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
				index.append(INDEX_DELIMITER);
			}
			index.append(lvl);
			addDelimiter = true;
		}

		return index.toString();
	}

	/**
	 * Convert the string representation of a row index to a list.
	 *
	 * @param row the string representation of the row index
	 * @return the row index
	 */
	public static List<Integer> rowIndexStringToList(final String row) {
		if (row == null) {
			return null;
		}

		List<Integer> rowIndex = new ArrayList<>();

		try {
			// Convert StringId to array
			String[] rowIdString = row.split(INDEX_DELIMITER);
			for (int i = 0; i < rowIdString.length; i++) {
				rowIndex.add(Integer.parseInt(rowIdString[i]));
			}
		} catch (NumberFormatException e) {
			LOG.warn("Invalid row id: " + row);
		}

		return rowIndex;
	}

	/**
	 * @param jsonArray the JSON array holding the node
	 * @param node the node being processed
	 */
	private static void processTreeToJson(final JsonArray jsonArray, final TreeItemIdNode node) {
		// Add node
		JsonObject jsonNode = new JsonObject();
		jsonNode.addProperty("id", node.getItemId());
		jsonArray.add(jsonNode);
		// Process children
		if (node.hasChildren()) {
			if (node.getChildren().isEmpty()) {
				jsonNode.addProperty("expandable", Boolean.TRUE);
			} else {
				JsonArray itemArray = new JsonArray();
				jsonNode.add("items", itemArray);
				for (TreeItemIdNode child : node.getChildren()) {
					processTreeToJson(itemArray, child);
				}
			}
		}
	}

	/**
	 * Iterate over the JSON objects to create the tree structure.
	 *
	 * @param parentNode the parent node
	 * @param json the current JSON object
	 */
	private static void processJsonToTree(final TreeItemIdNode parentNode, final JsonObject json) {

		String id = json.getAsJsonPrimitive("id").getAsString();
		JsonPrimitive expandableJson = json.getAsJsonPrimitive("expandable");

		TreeItemIdNode node = new TreeItemIdNode(id);
		if (expandableJson != null && expandableJson.getAsBoolean()) {
			node.setHasChildren(true);
		}
		parentNode.addChild(node);

		JsonArray children = json.getAsJsonArray("items");
		if (children != null) {
			for (int i = 0; i < children.size(); i++) {
				JsonObject child = children.get(i).getAsJsonObject();
				processJsonToTree(node, child);
			}
		}
	}

	/**
	 * Iterate over the custom tree structure to drill down to the item id.
	 *
	 * @param expanded the expanded rows to get to the open item
	 * @param node the current node being processed
	 * @param itemId the id of the open item
	 * @param currentExpanded the currently expanded rows
	 * @return true if item has been found so include the node item id in the expanded rows
	 */
	private static boolean processCustomExpandedRowsForItemId(final Set<String> expanded, final TreeItemIdNode node, final String itemId, final Set<String> currentExpanded) {

		// Check if node id is a match
		String nodeItemId = node.getItemId();
		if (Util.equals(nodeItemId, itemId)) {
			expanded.add(nodeItemId);
			return true;
		}

		// Check has children
		if (!node.hasChildren()) {
			return false;
		}

		// Assume the parents are "expanded" until reach the "item id" (so only process if expanded)
		boolean alreadyExpanded = currentExpanded != null && currentExpanded.contains(nodeItemId);
		if (!alreadyExpanded) {
			return false;
		}

		// Check the child nodes
		boolean found = false;
		for (TreeItemIdNode childItem : node.getChildren()) {
			if (processCustomExpandedRowsForItemId(expanded, childItem, itemId, currentExpanded)) {
				found = true;
				break;
			}
		}

		// If item has been found, add parent nodes to the expanded rows
		if (found && !Util.empty(nodeItemId)) {
			expanded.add(nodeItemId);
		}

		return found;
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
	private static void processItemIdIndexMapping(final Map<String, List<Integer>> map, final List<Integer> rowIndex, final TreeItemModel treeModel,
			final WTree.ExpandMode mode, final Set<String> expandedRows) {

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

}
