package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.util.SystemException;
import com.github.bordertech.wcomponents.util.Util;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.Serializable;
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
 * WTree represents a tree view selection control.
 *
 * @author Jonathan Austin
 * @since 1.1.0
 */
public class WTree extends AbstractInput
		implements AjaxTarget, SubordinateTrigger, SubordinateTarget,
		Marginable, Targetable {

	/**
	 * The logger instance for this class.
	 */
	private static final Log LOG = LogFactory.getLog(WTree.class);

	/**
	 * Tree item image request.
	 */
	private static final String ITEM_IMAGE_REQUEST_KEY = "wc_trid";

	/**
	 * Scratch map key for the map between an item id and its row index.
	 */
	private static final String INDEX_MAPPING_SCRATCH_MAP_KEY = "itemIdMap";

	/**
	 * Scratch map key for the map between an item id and its custom tree node.
	 */
	private static final String CUSTOM_IDS_SCRATCH_MAP_KEY = "customIdMap";

	/**
	 * Construct the WTree.
	 */
	public WTree() {
		this(Type.VERTICAL);
	}

	/**
	 * @param type the tree type.
	 */
	public WTree(final Type type) {
		setType(type);
	}

	/**
	 * This is used to indicate the type of tree.
	 */
	public enum Type {
		/**
		 * Horizontal tree.
		 */
		HORIZONTAL,
		/**
		 * Vertical tree.
		 */
		VERTICAL
	};

	/**
	 * This is used to control how row selection should work.
	 */
	public enum SelectMode {
		/**
		 * Indicates that only a single row may be selected.
		 */
		SINGLE,
		/**
		 * Indicates that multiple rows may be selected.
		 */
		MULTIPLE
	};

	/**
	 * This is used to control how row expansion should work.
	 */
	public enum ExpandMode {
		/**
		 * Indicates that row expansion occurs on the client.
		 */
		CLIENT,
		/**
		 * Indicates that row expansion occurs once, via AJAX.
		 */
		LAZY,
		/**
		 * Indicates that row expansion should make an AJAX call every time.
		 */
		DYNAMIC
	};

	/**
	 * Returns a {@link Set} of the selected options. If no options have been selected, then it returns an empty list.
	 * <p>
	 * As getValue calls {@link #getData()} for the currently selected options, it usually expects getData to return
	 * null (for no selection) or a {@link List} of selected options. If the data returned by getData is not null and is
	 * not a List, then setData will either (1) if the data is an array, convert the array to a List or (2) create a
	 * List and add the data as the selected option.
	 * </p>
	 * <p>
	 * getValue will verify the selected option/s are valid. If a selected option does not exist, then it will throw an
	 * {@link IllegalArgumentException}.
	 * </p>
	 *
	 * @return the selected options in the given UI context.
	 */
	@Override
	public Set<String> getValue() {
		// Convert data to a set (if necessary)
		Set<String> data = convertDataToSet(getData());
		if (data == null || data.isEmpty()) {
			return Collections.EMPTY_SET;
		}
		return data;
	}

	/**
	 * Returns a string value of the selected item for this users session. If multiple selections have been made, this
	 * will be a comma separated list of string values. If no value is selected, null is returned.
	 *
	 * @return the selected item value as a rendered String
	 */
	@Override
	public String getValueAsString() {
		Set<String> selected = getValue();
		if (selected == null || selected.isEmpty()) {
			return null;
		}

		StringBuilder stringValues = new StringBuilder();
		boolean first = true;

		for (String item : selected) {
			if (!first) {
				stringValues.append(", ");
			}
			stringValues.append(item);
			first = false;
		}

		return stringValues.toString();
	}

	/**
	 * @return true if nothing selected
	 */
	@Override
	public boolean isEmpty() {
		Set<String> selected = getValue();
		return (selected == null || selected.isEmpty());
	}

	/**
	 *
	 * @return the tree type.
	 */
	public Type getType() {
		return getComponentModel().type;
	}

	/**
	 * @param type the tree type
	 */
	public void setType(final Type type) {
		getOrCreateComponentModel().type = type == null ? Type.VERTICAL : type;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Margin getMargin() {
		return getComponentModel().margin;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setMargin(final Margin margin) {
		getOrCreateComponentModel().margin = margin;
	}

	/**
	 * @return the tree model
	 */
	public TreeModel getTreeModel() {
		return getComponentModel().treeModel;
	}

	/**
	 * Sets the tree model which provides row data.
	 *
	 * @param treeModel the tree model.
	 */
	public void setTreeModel(final TreeModel treeModel) {
		getOrCreateComponentModel().treeModel = treeModel;
		clearItemIdIndexMap();
		setSelectedRows(null);
		setExpandedRows(null);
	}

	/**
	 * @return true if allow the client to shuffle items
	 */
	public boolean isShuffle() {
		return getComponentModel().shuffle;
	}

	/**
	 *
	 * @param shuffle true if allow the client to shuffle items
	 */
	public void setShuffle(final boolean shuffle) {
		getOrCreateComponentModel().shuffle = shuffle;
	}

	/**
	 * The action used when the tree items are shuffled on the client.
	 *
	 * @return the shuffle action
	 */
	public Action getShuffleAction() {
		return getComponentModel().shuffleAction;
	}

	/**
	 * The action used when the tree items are shuffled on the client.
	 *
	 * @param action the shuffle action
	 */
	public void setShuffleAction(final Action action) {
		getOrCreateComponentModel().shuffleAction = action;
	}

	/**
	 * @return the root node of a custom tree structure
	 */
	public ItemIdNode getCustomTree() {
		return getComponentModel().customTree;
	}

	/**
	 * @param json the json representing a custom tree structure
	 */
	public void setCustomTree(final String json) {
		ItemIdNode root = convertJsonToTree(json);
		setCustomTree(root);
	}

	/**
	 * @param customTree the root node of a custom tree structure
	 */
	public void setCustomTree(final ItemIdNode customTree) {
		getOrCreateComponentModel().customTree = customTree;
	}

	/**
	 * @return the row selection mode.
	 */
	public SelectMode getSelectMode() {
		return getComponentModel().selectMode;
	}

	/**
	 * Sets the row selection mode.
	 *
	 * @param selectMode the row selection mode to set.
	 */
	public void setSelectMode(final SelectMode selectMode) {
		getOrCreateComponentModel().selectMode = selectMode == null ? SelectMode.SINGLE : selectMode;
	}

	/**
	 * @return the row expansion mode.
	 */
	public ExpandMode getExpandMode() {
		return getComponentModel().expandMode;
	}

	/**
	 * Sets the row expansion mode.
	 *
	 * @param expandMode the expand mode to set.
	 */
	public void setExpandMode(final ExpandMode expandMode) {
		getOrCreateComponentModel().expandMode = expandMode == null ? ExpandMode.CLIENT : expandMode;
	}

	/**
	 * Retrieve the row keys that are selected.
	 * <p>
	 * A row key uniquely identifies each row and is determined by the {@link TreeModel}. Refer to
	 * {@link TreeModel#getItemId(List)}.
	 * </p>
	 *
	 * @return the selected row keys.
	 */
	public Set<String> getSelectedRows() {
		return getValue();
	}

	/**
	 * Set the row keys that are selected.
	 * <p>
	 * A row key uniquely identifies each row and is determined by the {@link TreeModel}. Refer to
	 * {@link TreeModel#getItemId(List)}.
	 * </p>
	 *
	 * @param itemIds the keys of selected rows.
	 */
	public void setSelectedRows(final Set<String> itemIds) {
		setData(itemIds);
	}

	/**
	 * Retrieve the row keys that are expanded.
	 * <p>
	 * A row key uniquely identifies each row and is determined by the {@link TreeModel}. Refer to
	 * {@link TreeModel#getItemId(List)}.
	 * </p>
	 *
	 * @return the expanded row keys.
	 */
	public Set<String> getExpandedRows() {
		Set<String> keys = getComponentModel().expandedRows;
		if (keys == null) {
			return Collections.emptySet();
		} else {
			return Collections.unmodifiableSet(keys);
		}
	}

	/**
	 * Set the row keys that are expanded.
	 * <p>
	 * A row key uniquely identifies each row and is determined by the {@link TreeModel}. Refer to
	 * {@link TreeModel#getRowKey(List)}.
	 * </p>
	 *
	 * @param itemIds the keys of expanded rows.
	 */
	public void setExpandedRows(final Set<String> itemIds) {
		getOrCreateComponentModel().expandedRows = itemIds;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getTargetId() {
		return getId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<String> getRequestValue(final Request request) {
		if (isPresent(request)) {
			return getNewSelections(request);
		} else {
			return getValue();
		}
	}

	/**
	 * Retrieves a URL for the tree item image.
	 *
	 * @param item the tree item
	 * @param itemId the tree item id
	 * @return the URL to access the tree item image.
	 */
	public String getItemImageUrl(final ItemImage item, final String itemId) {

		if (item == null) {
			return null;
		}

		// Check if has image url
		String url = item.getUrl();
		if (!Util.empty(url)) {
			return url;
		}

		// Check if has image
		Image image = item.getImage();
		if (image == null) {
			return null;
		}

		// Check static resource
		if (image instanceof InternalResource) {
			return ((InternalResource) image).getTargetUrl();
		}

		// Build targetted url
		Environment env = getEnvironment();
		Map<String, String> parameters = env.getHiddenParameters();
		parameters.put(Environment.TARGET_ID, getTargetId());

		String cacheKey = item.getImageCacheKey();

		if (Util.empty(cacheKey)) {
			// Add some randomness to the URL to prevent caching
			String random = WebUtilities.generateRandom();
			parameters.put(Environment.UNIQUE_RANDOM_PARAM, random);
		} else {
			// Remove step counter as not required for cached content
			parameters.remove(Environment.STEP_VARIABLE);
			parameters.remove(Environment.SESSION_TOKEN_VARIABLE);
			// Add the cache key
			parameters.put(Environment.CONTENT_CACHE_KEY, cacheKey);
		}

		// Item id
		parameters.put(ITEM_IMAGE_REQUEST_KEY, itemId);

		// The targetable path needs to be configured for the portal environment.
		url = env.getWServletPath();

		// Note the last parameter. In javascript we don't want to encode "&".
		return WebUtilities.getPath(url, parameters, true);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initialiseComponentModel() {
		super.initialiseComponentModel();
		// Copy the custom tree (if set) to allow the nodes to be updated per user
		ItemIdNode custom = getCustomTree();
		if (custom != null) {
			ItemIdNode copy = copyTreeNode(custom);
			setCustomTree(copy);
		}
	}

	/**
	 * Override preparePaint to register an AJAX operation if necessary.
	 *
	 * @param request the request being responded to.
	 */
	@Override
	protected void preparePaintComponent(final Request request) {
		super.preparePaintComponent(request);
		if (isShuffle()) {
			AjaxHelper.registerComponentTargetItself(getId(), request);
		}
		// Update custom tree nodes (if needed)
		ItemIdNode custom = getCustomTree();
		if (custom != null) {
			updateCustomTreeNodes(custom, getTreeModel(), getExpandMode(), getExpandedRows());
			clearCustomIdMap();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean beforeHandleRequest(final Request request) {
		// Check if is targeted request
		String targetParam = request.getParameter(Environment.TARGET_ID);
		boolean targetted = (targetParam != null && targetParam.equals(getTargetId()));
		if (targetted) {
			handleItemImageRequest(request);
			return false;
		}
		return true;
	}

	/**
	 * Set the inputs based on the incoming request. The text input values are set as an array of strings on the
	 * parameter with this name {@link #getName()}. Any empty strings will be ignored.
	 *
	 * @param request the current request.
	 * @return true if the inputs have changed, otherwise return false
	 */
	@Override
	protected boolean doHandleRequest(final Request request) {
		Set<String> values = getRequestValue(request);
		Set<String> current = getValue();

		boolean changed = selectionsEqual(values, current);

		if (changed) {
			setData(values);
		}

		if (isShuffle()) {
			handleShuffleRequest(request);
		}
		handleExpansionRequest(request);

		return changed;
	}

	/**
	 * Clear the map holding the mapping between custom item ids and their node item.
	 */
	public void clearCustomIdMap() {
		getScratchMap().remove(CUSTOM_IDS_SCRATCH_MAP_KEY);
	}

	/**
	 * @return the map between the custom item ids and their node item.
	 */
	public Map<String, ItemIdNode> getCustomIdMap() {
		Map<String, ItemIdNode> map = (Map<String, ItemIdNode>) getScratchMap().get(CUSTOM_IDS_SCRATCH_MAP_KEY);
		if (map == null) {
			map = createCustomIdMap();
			getScratchMap().put(CUSTOM_IDS_SCRATCH_MAP_KEY, map);
		}
		return map;
	}

	/**
	 * Clear the map holding the mapping between an item id and its row index.
	 */
	public void clearItemIdIndexMap() {
		getScratchMap().remove(INDEX_MAPPING_SCRATCH_MAP_KEY);
	}

	/**
	 * @return the mapping between an item id and its row index.
	 */
	public Map<String, List<Integer>> getItemIdIndexMap() {
		Map<String, List<Integer>> map = (Map<String, List<Integer>>) getScratchMap().get(INDEX_MAPPING_SCRATCH_MAP_KEY);
		if (map == null) {
			map = createItemIdIndexMap();
			getScratchMap().put(INDEX_MAPPING_SCRATCH_MAP_KEY, map);
		}
		return map;
	}

	/**
	 * Handles a request containing row selection data.
	 *
	 * @param request the request containing row selection data.
	 * @return the set of selected item ids.
	 */
	private Set<String> getNewSelections(final Request request) {

		String[] paramValue = request.getParameterValues(getId() + ".selected");
		if (paramValue == null) {
			paramValue = new String[0];
		}

		String[] selectedRowIds = removeEmptyStrings(paramValue);
		Set<String> newSelectionIds = new HashSet<>();

		boolean singleSelect = getSelectMode() == SelectMode.SINGLE;

		if (selectedRowIds != null) {
			int offset = getItemIdPrefix().length();
			for (String selectedRowId : selectedRowIds) {
				String itemId = selectedRowId.substring(offset);
				if (isValidTreeItem(itemId)) {
					newSelectionIds.add(itemId);
					if (singleSelect) {
						break;
					}
				}
			}
		}

		return newSelectionIds;
	}

	/**
	 *
	 * @return the prefix to use on the tree item ids
	 */
	public String getItemIdPrefix() {
		return getId() + "-";
	}

	/**
	 * Selection lists are considered equal if they have the same items (order is not important). An empty list is
	 * considered equal to a null list.
	 *
	 * @param set1 the first list to check.
	 * @param set2 the second list to check.
	 * @return true if the lists are equal, false otherwise.
	 */
	private boolean selectionsEqual(final Set<?> set1, final Set<?> set2) {
		// Empty or null lists
		if ((set1 == null || set1.isEmpty()) && (set2 == null || set2.isEmpty())) {
			return true;
		}

		// Same size and contain same entries
		return set1 != null && set2 != null && set1.size() == set2.size() && set1.
				containsAll(set2);
	}

	/**
	 * Convert the data to a set (if necessary).
	 *
	 * @param data the data to convert to a set
	 * @return the data converted to a set
	 */
	private Set<String> convertDataToSet(final Object data) {
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
	 * Handle a targeted request to retrieve the tree item image.
	 *
	 * @param request the request being processed
	 */
	private void handleItemImageRequest(final Request request) {

		// Check for tree item id
		String itemId = request.getParameter(ITEM_IMAGE_REQUEST_KEY);
		if (itemId == null) {
			throw new SystemException("No tree item id provided for image request.");
		}

		// Check valid item id
		if (!isValidTreeItem(itemId)) {
			throw new SystemException("Tree item id [" + itemId + "] is not valid.");
		}

		List<Integer> index = getItemIdIndexMap().get(itemId);
		ItemImage image = getTreeModel().getItemImage(index);

		ContentEscape escape = new ContentEscape(image.getImage());
		throw escape;
	}

	/**
	 * Handles a request containing row expansion data.
	 *
	 * @param request the request containing row expansion data.
	 */
	private void handleExpansionRequest(final Request request) {

		String[] paramValue = request.getParameterValues(getId() + ".expanded");
		if (paramValue == null) {
			paramValue = new String[0];
		}

		String[] expandedRowIds = removeEmptyStrings(paramValue);
		Set<String> newExpansionIds = new HashSet<>();

		if (expandedRowIds != null) {
			int offset = getItemIdPrefix().length();
			for (String expandedRowId : expandedRowIds) {
				String itemId = expandedRowId.substring(offset);
				if (isValidTreeItem(itemId)) {
					newExpansionIds.add(itemId);
				}
			}
		}
		setExpandedRows(newExpansionIds);
	}

	/**
	 * Handle the tree items that have been shuffled by the client.
	 *
	 * @param request the request being processed
	 */
	private void handleShuffleRequest(final Request request) {

		String json = request.getParameter(getId() + ".shuffle");
		if (Util.empty(json)) {
			return;
		}

		// New
		ItemIdNode newTree = null;
		try {
			newTree = convertJsonToTree(json);
		} catch (Exception e) {
			LOG.warn("Could not parse JSON for shuffle tree items. " + e.getMessage());
			return;
		}

		// Current
		ItemIdNode currentTree = getCustomTree();

		boolean changed = isTreeSame(newTree, currentTree);

		if (changed) {
			setCustomTree(newTree);
			// Run the shuffle action (if set)
			final Action action = getShuffleAction();
			if (action != null) {
				// Set the selected file id as the action object
				final ActionEvent event = new ActionEvent(this, "shuffle");
				Runnable later = new Runnable() {
					@Override
					public void run() {
						action.execute(event);
					}
				};
				invokeLater(later);
			}
		}
	}

	/**
	 * @param jsonString the string of JSON to convert to a custom tree of nodes
	 * @return the custom tree structure of item ids
	 */
	private ItemIdNode convertJsonToTree(final String jsonString) {

		JsonParser parser = new JsonParser();
		JsonObject json = parser.parse(jsonString).getAsJsonObject();

		ItemIdNode root = new ItemIdNode(null);

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
	private void processJsonToTree(final ItemIdNode parentNode, final JsonObject json) {

		String id = json.getAsJsonPrimitive("id").getAsString();

		ItemIdNode node = new ItemIdNode(id);
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
	private boolean isTreeSame(final ItemIdNode tree1, final ItemIdNode tree2) {

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
	 * Helper that removes empty/null string from the <code>original</code> string array.
	 *
	 * @param originals The string array from which the null/empty strings should be removed from.
	 * @return Array of non empty strings from the <code>original</code> string array.
	 */
	private String[] removeEmptyStrings(final String[] originals) {
		if (originals == null) {
			return null;
		} else {
			List<String> parsed = new ArrayList<>();

			for (String original : originals) {
				if (original != null && original.length() > 0) {
					parsed.add(original);
				}
			}

			return parsed.toArray(new String[parsed.size()]);
		}
	}

	/**
	 * @param node the node to copy
	 * @return a copy of the node
	 */
	private ItemIdNode copyTreeNode(final ItemIdNode node) {
		ItemIdNode copy = new ItemIdNode(node.getItemId());
		copy.setHasChildren(node.hasChildren());

		for (ItemIdNode childItem : node.getChildren()) {
			ItemIdNode childCopy = copyTreeNode(childItem);
			copy.addChild(childCopy);
		}
		return copy;
	}

	/**
	 * @return the map containing the map of custom items to their node in the tree
	 */
	private Map<String, ItemIdNode> createCustomIdMap() {
		ItemIdNode custom = getCustomTree();
		Map<String, ItemIdNode> map = new HashMap<>();
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
	private void processCustomIdMapping(final Map<String, ItemIdNode> map, final ItemIdNode node) {
		String itemId = node.getItemId();
		if (!Util.empty(itemId)) {
			map.put(itemId, node);
		}

		for (ItemIdNode childItem : node.getChildren()) {
			processCustomIdMapping(map, childItem);
		}
	}

	/**
	 * Update the custom tree node to make sure any nodes that dont have any children in the custom layout have their
	 * children loaded from the tree model (if they have any).
	 *
	 * @param node the current node.
	 * @param treeModel the tree model
	 * @param mode the expand mode
	 * @param expandedRows the set of expanded rows
	 */
	private void updateCustomTreeNodes(final ItemIdNode node, final TreeModel treeModel, final ExpandMode mode, final Set<String> expandedRows) {

		// Node has no children so check if they need to be loaded from the tree model
		if (node.getChildren().isEmpty()) {
			List<Integer> rowIndex = getItemIdIndexMap().get(node.getItemId());
			loadCustomNodesFromModel(node, rowIndex, treeModel, mode, expandedRows);
		} else {
			// Check children
			for (ItemIdNode child : node.getChildren()) {
				updateCustomTreeNodes(child, treeModel, mode, expandedRows);
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
	private void loadCustomNodesFromModel(final ItemIdNode node, final List<Integer> rowIndex, final TreeModel treeModel, final ExpandMode mode, final Set<String> expandedRows) {

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
		boolean addChildren = (mode == ExpandMode.CLIENT) || (expandedRows != null && expandedRows.contains(node.getItemId()));
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
			ItemIdNode childNode = new ItemIdNode(nextId);
			node.addChild(childNode);
			// Process this child node
			loadCustomNodesFromModel(childNode, nextRow, treeModel, mode, expandedRows);
		}

	}

	/**
	 * @return the map of item ids to their row index.
	 */
	private Map<String, List<Integer>> createItemIdIndexMap() {
		Map<String, List<Integer>> map = new HashMap<>();
		TreeModel treeModel = getTreeModel();
		int rows = treeModel.getRowCount();
		Set<String> expanded = getExpandedRows();
		ExpandMode mode = getExpandMode();

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
	private void processItemIdIndexMapping(final Map<String, List<Integer>> map, final List<Integer> rowIndex, final TreeModel treeModel, final ExpandMode mode, final Set<String> expandedRows) {

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
		boolean addChildren = (mode == ExpandMode.CLIENT) || (expandedRows != null && expandedRows.contains(
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
	 *
	 * @param itemId the item id to check
	 * @return true if this item id is valid.
	 */
	private boolean isValidTreeItem(final String itemId) {

		// Check for custom tree
		ItemIdNode custom = getCustomTree();
		if (custom != null && !getCustomIdMap().containsKey(itemId)) {
			return false;
		}

		// Check is still a valid item
		List<Integer> index = getItemIdIndexMap().get(itemId);
		if (index == null) {
			return false;
		}

		String id = getTreeModel().getItemId(index);
		if (id == null) {
			return false;
		}

		// Check integrity
		if (!Util.equals(id, itemId)) {
			throw new SystemException("Invalid tree item returned from model for index [" + index + "]. Expected id [" + itemId + "] but received id [" + id + "].");
		}

		return true;
	}

	/**
	 * @return a String representation of this component, for debugging purposes.
	 */
	@Override
	public String toString() {
		TreeModel model = getTreeModel();
		return toString(model.getClass().getSimpleName(), -1, -1);
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

	/**
	 * Creates a new component model.
	 *
	 * @return a new WTreeComponentModel.
	 */
	@Override
	protected WTreeComponentModel newComponentModel() {
		return new WTreeComponentModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	// for type safety only
	protected WTreeComponentModel getComponentModel() {
		return (WTreeComponentModel) super.getComponentModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	// for type safety only
	protected WTreeComponentModel getOrCreateComponentModel() {
		return (WTreeComponentModel) super.getOrCreateComponentModel();

	}

	/**
	 * Contains the tree's UI state.
	 *
	 * @author Jonathan Austin
	 * @since 1.0.0
	 */
	public static final class WTreeComponentModel extends InputModel {

		/**
		 * The type of tree.
		 */
		private Type type;

		/**
		 * The margins to be used on the tree.
		 */
		private Margin margin;

		/**
		 * The data model for the tree.
		 */
		private TreeModel treeModel = EmptyTreeModel.INSTANCE;

		// Selection
		/**
		 * Indicates how row selection should function.
		 */
		private SelectMode selectMode = SelectMode.SINGLE;

		/**
		 * Indicates how row expansion should function.
		 */
		private ExpandMode expandMode = ExpandMode.CLIENT;

		/**
		 * Holds the keys of currently expanded rows.
		 */
		private Set<String> expandedRows;

		/**
		 * Allow the rows to be shuffled.
		 */
		private boolean shuffle;

		/**
		 * Shuffle action.
		 */
		private Action shuffleAction;

		/**
		 * This is used to allow a user to have a different tree of nodes.
		 */
		private ItemIdNode customTree;

	}

	/**
	 * A type to indicate how a tree item can be shuffled.
	 */
	public enum ShuffleType {
		/**
		 * Used as a branch only.
		 */
		BRANCH,
		/**
		 * Used as a leaf only.
		 */
		LEAF,
		/**
		 * Used as a leaf or branch.
		 */
		BOTH
	}

	/**
	 * Holds the details of an image for a tree item.
	 */
	public static class ItemImage implements Serializable {

		/**
		 * The URL for an image.
		 */
		private final String url;
		/**
		 * The image content.
		 */
		private final Image image;
		/**
		 * The image cache key.
		 */
		private final String imageCacheKey;

		/**
		 *
		 * @param url the URL for an image.
		 */
		public ItemImage(final String url) {
			this.url = url;
			this.image = null;
			this.imageCacheKey = null;
		}

		/**
		 *
		 * @param image the image for a tree item
		 */
		public ItemImage(final Image image) {
			this(image, null);
		}

		/**
		 *
		 * @param image the image for a tree item
		 * @param imageCacheKey the cache key for the image on a tree item
		 */
		public ItemImage(final Image image, final String imageCacheKey) {
			this.url = null;
			this.image = image;
			this.imageCacheKey = imageCacheKey;
		}

		/**
		 *
		 * @return the URL for an image
		 */
		public String getUrl() {
			return url;
		}

		/**
		 *
		 * @return the image for a tree item
		 */
		public Image getImage() {
			return image;
		}

		/**
		 *
		 * @return the cache key for the image on a tree item
		 */
		public String getImageCacheKey() {
			return imageCacheKey;
		}
	}

	/**
	 * The tree node that holds a tree item id. Used for custom tree structures.
	 */
	public static class ItemIdNode implements Serializable {

		/**
		 * The tree item id.
		 *
		 */
		private final String itemId;

		/**
		 * The list of this nodes children.
		 */
		private List<ItemIdNode> children;

		/**
		 * True if this node has children. Allows it be set true with out actually loading the children.
		 */
		private boolean hasChildren;

		/**
		 * @param itemId the tree item id
		 */
		public ItemIdNode(final String itemId) {
			this.itemId = itemId;
		}

		/**
		 *
		 * @return the tree item id
		 */
		public String getItemId() {
			return itemId;
		}

		/**
		 * @param node the child node to add
		 */
		public void addChild(final ItemIdNode node) {
			if (children == null) {
				children = new ArrayList<>();
			}
			children.add(node);
		}

		/**
		 * @return the list of child nodes
		 */
		public List<ItemIdNode> getChildren() {
			if (children == null) {
				return Collections.EMPTY_LIST;
			} else {
				return Collections.unmodifiableList(children);
			}
		}

		/**
		 * @return true if this node has children
		 */
		public boolean hasChildren() {
			return hasChildren;
		}

		/**
		 *
		 * @param hasChildren true if this node has children
		 */
		public void setHasChildren(final boolean hasChildren) {
			this.hasChildren = hasChildren;
		}

	}

	/**
	 *
	 * @author Jonathan Austin
	 * @since 1.0.0
	 */
	public interface TreeModel extends Serializable {

		/**
		 * Indicates whether the given row is disabled.
		 *
		 * @param row the row index
		 * @return true if the row is disabled, false otherwise.
		 */
		boolean isDisabled(final List<Integer> row);

		/**
		 * Indicates whether the given row is selectable.
		 *
		 * @param row the row index
		 * @return true if the row is selectable, false otherwise.
		 */
		boolean isSelectable(final List<Integer> row);

		/**
		 * Retrieves the value at the given row and column.
		 *
		 * @param row - the row index.
		 * @return the value at the given row and column.
		 */
		String getItemLabel(final List<Integer> row);

		/**
		 * Retrieves the value at the given row and column.
		 *
		 * @param row - the row index.
		 * @return the value at the given row and column.
		 */
		String getItemId(final List<Integer> row);

		/**
		 * Retrieves the value at the given row and column.
		 *
		 * @param row - the row index.
		 * @return the value at the given row and column.
		 */
		ItemImage getItemImage(final List<Integer> row);

		/**
		 * @param row the row index
		 * @return the shuffle type for this item
		 */
		ShuffleType getItemShuffleType(final List<Integer> row);

		/**
		 * Indicates whether the given row is expandable.
		 *
		 * @param row the row index
		 * @return true if the row is expandable, false otherwise.
		 */
		boolean isExpandable(final List<Integer> row);

		/**
		 * Retrieves the number of rows for the root (ie top) level.
		 *
		 * @return the number of rows in the model for the root (ie top) level.
		 */
		int getRowCount();

		/**
		 * Allows the model to report if the row has children without actually having to determine the number of
		 * children (as it might not be known).
		 *
		 * @param row the row index
		 * @return true if the row has children
		 */
		boolean hasChildren(final List<Integer> row);

		/**
		 * Retrieves the number of children a row has.
		 *
		 * @param row the row index
		 * @return the number of rows in the model for this level.
		 */
		int getChildCount(final List<Integer> row);
	}

	/**
	 * A skeleton implementation of a simple data model that does not support sorting, selectability, expandability or
	 * editability.
	 *
	 * @author Jonathan Austin
	 * @since 1.0.0
	 */
	public abstract static class AbstractTreeModel implements TreeModel {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean isDisabled(final List<Integer> row) {
			return false;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean isExpandable(final List<Integer> row) {
			return true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean isSelectable(final List<Integer> row) {
			return false;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ItemImage getItemImage(final List<Integer> row) {
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getItemId(final List<Integer> row) {
			return WTree.rowIndexListToString(row);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ShuffleType getItemShuffleType(final List<Integer> row) {
			return ShuffleType.BOTH;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean hasChildren(final List<Integer> row) {
			return getChildCount(row) > 0;
		}

	}

	/**
	 * An empty data model implementation, the default model used by {@link WTree}.
	 *
	 * @author Jonathan Austin
	 * @since 1.0.0
	 */
	public static final class EmptyTreeModel extends AbstractTreeModel {

		/**
		 * The singleton instance.
		 */
		public static final EmptyTreeModel INSTANCE = new EmptyTreeModel();

		/**
		 * Prevent external instantiation of this class.
		 */
		private EmptyTreeModel() {
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int getRowCount() {
			return 0;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int getChildCount(final List<Integer> row) {
			return 0;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getItemLabel(final List<Integer> row) {
			return null;
		}

	}

}
