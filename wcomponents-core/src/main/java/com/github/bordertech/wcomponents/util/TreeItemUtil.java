package com.github.bordertech.wcomponents.util;

import com.github.bordertech.wcomponents.TreeItemIdNode;
import com.github.bordertech.wcomponents.WTree;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
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

}
