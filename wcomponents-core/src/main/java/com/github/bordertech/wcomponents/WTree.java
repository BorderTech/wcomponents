package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.util.SystemException;
import com.github.bordertech.wcomponents.util.TreeItemUtil;
import com.github.bordertech.wcomponents.util.Util;
import java.util.ArrayList;
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
		implements AjaxInternalTrigger, AjaxTarget, AjaxTrigger, SubordinateTrigger, SubordinateTarget,
		Marginable, Targetable {

	/**
	 * The logger instance for this class.
	 */
	private static final Log LOG = LogFactory.getLog(WTree.class);

	/**
	 * Tree item request key.
	 */
	private static final String ITEM_REQUEST_KEY = "wc_tiid";

	/**
	 * Shuffle request key.
	 */
	private static final String SHUFFLE_REQUEST_KEY = "shuffle";

	/**
	 * Open request key.
	 */
	private static final String OPEN_REQUEST_KEY = ".open";

	/**
	 * Scratch map key for the map between an item id and its row index.
	 */
	private static final String EXPANDED_IDS_TO_INDEX_MAPPING_SCRATCH_MAP_KEY = "expandedItemIdMap";

	/**
	 * Scratch map key for the map between an item id and its custom tree node.
	 */
	private static final String CUSTOM_IDS_TO_NODE_SCRATCH_MAP_KEY = "customIdNodeMap";

	/**
	 * Scratch map key for the map between an item id and its row index in custom tree node.
	 */
	private static final String ALL_IDS_TO_INDEX_SCRATCH_MAP_KEY = "customIdIndexMap";

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
		Set<String> data = TreeItemUtil.convertDataToSet(getData());
		if (data == null || data.isEmpty()) {
			return Collections.EMPTY_SET;
		}
		// TODO Similar to select lists, consider validating the selected items
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
	public TreeItemModel getTreeModel() {
		return getComponentModel().treeModel;
	}

	/**
	 * Sets the tree model which provides row data.
	 *
	 * @param treeModel the tree model.
	 */
	public void setTreeModel(final TreeItemModel treeModel) {
		getOrCreateComponentModel().treeModel = treeModel;
		clearItemIdMaps();
		setSelectedRows(null);
		setExpandedRows(null);
		setCustomTree((TreeItemIdNode) null);
	}

	/**
	 * The action when a request to open a tree item is received.
	 *
	 * @return the open action
	 */
	public Action getOpenAction() {
		return getComponentModel().openAction;
	}

	/**
	 * The action when a request to open a tree item is received.
	 *
	 * @param action the open action
	 */
	public void setOpenAction(final Action action) {
		getOrCreateComponentModel().openAction = action;
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
	public TreeItemIdNode getCustomTree() {
		return getComponentModel().customTree;
	}

	/**
	 * @param json the json representing a custom tree structure
	 */
	public void setCustomTree(final String json) {
		TreeItemIdNode root = TreeItemUtil.convertJsonToTree(json);
		setCustomTree(root);
	}

	/**
	 * Set a custom view of the tree nodes.
	 * <p>
	 * When using custom trees it is important to implement the correct logic in getItemRowIndex(itemId) in the model to
	 * match between the row item id ands its row index.
	 * </p>
	 *
	 * @param customTree the root node of a custom tree structure
	 */
	public void setCustomTree(final TreeItemIdNode customTree) {
		getOrCreateComponentModel().customTree = customTree;
		clearItemIdMaps();
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
	 * A row key uniquely identifies each row and is determined by the {@link TreeItemModel}. Refer to
	 * {@link TreeItemModel#getItemId(List)}.
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
	 * A row key uniquely identifies each row and is determined by the {@link TreeItemModel}. Refer to
	 * {@link TreeItemModel#getItemId(List)}.
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
	 * A row key uniquely identifies each row and is determined by the {@link TreeItemModel}. Refer to
	 * {@link TreeItemModel#getItemId(List)}.
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
	 * A row key uniquely identifies each row and is determined by the {@link TreeItemModel}. Refer to
	 * {@link TreeItemModel#getItemId(List)}.
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
	 * <p>
	 * This method is used by the WTree Renderer.
	 * </p>
	 *
	 * @param item the tree item
	 * @param itemId the tree item id
	 * @return the URL to access the tree item image.
	 */
	public String getItemImageUrl(final TreeItemImage item, final String itemId) {

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
		parameters.put(ITEM_REQUEST_KEY, itemId);

		// The targetable path needs to be configured for the portal environment.
		url = env.getWServletPath();

		// Note the last parameter. In javascript we don't want to encode "&".
		return WebUtilities.getPath(url, parameters, true);
	}

	/**
	 *
	 * @return the prefix to use on the tree item ids
	 */
	public String getItemIdPrefix() {
		return getId() + "-";
	}

	/**
	 * Clear the item id maps from, the scratch map.
	 */
	protected void clearItemIdMaps() {
		if (getScratchMap() != null) {
			getScratchMap().remove(CUSTOM_IDS_TO_NODE_SCRATCH_MAP_KEY);
			getScratchMap().remove(ALL_IDS_TO_INDEX_SCRATCH_MAP_KEY);
			getScratchMap().remove(EXPANDED_IDS_TO_INDEX_MAPPING_SCRATCH_MAP_KEY);
		}
	}

	/**
	 * Map an item id to its node in the custom tree. As this can be expensive save the map onto the scratch pad.
	 *
	 * @return the map between the custom item ids and their node item.
	 */
	public Map<String, TreeItemIdNode> getCustomIdNodeMap() {
		// No user context present
		if (getScratchMap() == null) {
			return createCustomIdNodeMapping();
		}
		Map<String, TreeItemIdNode> map = (Map<String, TreeItemIdNode>) getScratchMap().get(CUSTOM_IDS_TO_NODE_SCRATCH_MAP_KEY);
		if (map == null) {
			map = createCustomIdNodeMapping();
			getScratchMap().put(CUSTOM_IDS_TO_NODE_SCRATCH_MAP_KEY, map);
		}
		return map;
	}

	/**
	 * Map all item idsin the model to their row index. As this can be expensive save the map onto the scratch pad.
	 * <p>
	 * This can be very expensive and should be avoided. This will load all the nodes in a tree (including those already
	 * not expanded).
	 * </p>
	 *
	 * @return the map between the all the item ids in the tree model and row indexes
	 */
	public Map<String, List<Integer>> getAllItemIdIndexMap() {
		// No user context present
		if (getScratchMap() == null) {
			return createItemIdIndexMap(false);
		}
		Map<String, List<Integer>> map = (Map<String, List<Integer>>) getScratchMap().get(ALL_IDS_TO_INDEX_SCRATCH_MAP_KEY);
		if (map == null) {
			map = createItemIdIndexMap(false);
			getScratchMap().put(ALL_IDS_TO_INDEX_SCRATCH_MAP_KEY, map);
		}
		return map;
	}

	/**
	 * Map expanded item ids to their row index. As this can be expensive save the map onto the scratch pad.
	 *
	 * @return the mapping between an item id and its row index. Only expanded items.
	 */
	public Map<String, List<Integer>> getExpandedItemIdIndexMap() {
		// CLIENT MODE is include ALL
		boolean expandedOnly = getExpandMode() != ExpandMode.CLIENT;
		if (getScratchMap() == null) {
			if (getCustomTree() == null) {
				return createItemIdIndexMap(expandedOnly);
			} else {
				return createExpandedCustomIdIndexMapping(expandedOnly);
			}
		}
		Map<String, List<Integer>> map = (Map<String, List<Integer>>) getScratchMap().get(EXPANDED_IDS_TO_INDEX_MAPPING_SCRATCH_MAP_KEY);
		if (map == null) {
			if (getCustomTree() == null) {
				map = createItemIdIndexMap(expandedOnly);
			} else {
				map = createExpandedCustomIdIndexMapping(expandedOnly);
			}
			getScratchMap().put(EXPANDED_IDS_TO_INDEX_MAPPING_SCRATCH_MAP_KEY, map);
		}
		return map;
	}

	/**
	 * This method is used with a custom tree to map an item id to its row index.
	 * <p>
	 * The default implementation can be expensive as it looks at all the nodes in the tree to find the item id.
	 * </p>
	 * <p>
	 * An alternative implementation projects could override to use is if the TreeModel used by the project is using the
	 * string version of the row index, override this method to use
	 * {@link TreeItemUtil#rowIndexStringToList(java.lang.String)}.
	 * </p>
	 *
	 * @param itemId the item id
	 * @return the row index for the item id
	 */
	public List<Integer> getRowIndexForCustomItemId(final String itemId) {
		return getAllItemIdIndexMap().get(itemId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initialiseComponentModel() {
		super.initialiseComponentModel();
		// Copy the custom tree (if set) to allow the nodes to be updated per user
		TreeItemIdNode custom = getCustomTree();
		if (custom != null) {
			TreeItemIdNode copy = TreeItemUtil.copyTreeNode(custom);
			setCustomTree(copy);
		}
	}

	/**
	 * Override preparePaint to register an AJAX operation.
	 *
	 * @param request the request being responded to.
	 */
	@Override
	protected void preparePaintComponent(final Request request) {
		super.preparePaintComponent(request);

		// If is an internal AJAX action, set the action type.
		if (isCurrentAjaxTrigger()) {
			AjaxOperation operation = AjaxHelper.getCurrentOperation();
			if (operation.isInternalAjaxRequest()) {
				// Want to replace children in the target (Internal defaults to REPLACE target)
				operation.setAction(AjaxOperation.AjaxAction.IN);
			}
		}
		// Check if a custom tree needs the expanded rows checked
		TreeItemIdNode custom = getCustomTree();
		if (custom != null) {
			checkExpandedCustomNodes();
		}

		// Make sure the ID maps are up to date
		clearItemIdMaps();
		if (getExpandMode() == ExpandMode.LAZY) {
			if (AjaxHelper.getCurrentOperation() == null) {
				clearPrevExpandedRows();
			} else {
				addPrevExpandedCurrent();
			}
		}
	}

	/**
	 * Check if custom nodes that are expanded need their child nodes added from the model.
	 */
	protected void checkExpandedCustomNodes() {

		TreeItemIdNode custom = getCustomTree();
		if (custom == null) {
			return;
		}

		// Get the expanded rows
		Set<String> expanded = getExpandedRows();

		// Process Top Level
		for (TreeItemIdNode node : custom.getChildren()) {
			processCheckExpandedCustomNodes(node, expanded);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void afterPaint(final RenderContext renderContext) {
		super.afterPaint(renderContext);
		// Clear the open id (if set)
		setOpenRequestItemId(null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean beforeHandleRequest(final Request request) {

		// Clear open request (if set)
		setOpenRequestItemId(null);

		// Check if is targeted request (ie item image)
		String targetParam = request.getParameter(Environment.TARGET_ID);
		boolean targetted = (targetParam != null && targetParam.equals(getTargetId()));
		if (targetted) {
			handleItemImageRequest(request);
			return false;
		}

		// Check if open item request
		if (isOpenItemRequest(request)) {
			// Set the expanded rows
			handleExpandedState(request);
			// Handle open request
			handleOpenItemRequest(request);
			return false;
		}

		// Check if shuffle items
		if (isShuffle() && isShuffleRequest(request)) {
			handleShuffleState(request);
			return false;
		}
		return true;
	}

	/**
	 * @param request the request being processed
	 * @return true if its an open item request
	 */
	protected boolean isOpenItemRequest(final Request request) {
		return AjaxHelper.isCurrentAjaxTrigger(this) && request.getParameter(ITEM_REQUEST_KEY) != null;
	}

	/**
	 * @param request the request being processed
	 * @return true if its a shuffle items request
	 */
	protected boolean isShuffleRequest(final Request request) {
		return AjaxHelper.isCurrentAjaxTrigger(this) && request.getParameter(SHUFFLE_REQUEST_KEY) != null;
	}

	/**
	 * @param request the request being processed
	 * @return true if has open request
	 */
	protected boolean hasOpenRequest(final Request request) {
		return request.getParameter(getId() + OPEN_REQUEST_KEY) != null;
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

		handleExpandedState(request);

		Set<String> values = getRequestValue(request);
		Set<String> current = getValue();

		boolean changed = !selectionsEqual(values, current);
		if (changed) {
			setData(values);
		}

		return changed;
	}

	/**
	 * <p>
	 * Indicates whether this tree was present in the request.
	 * </p>
	 *
	 * @param request the request being responded to.
	 * @return true if this tree was present in the request, false if not.
	 */
	@Override
	protected boolean isPresent(final Request request) {
		return request.getParameter(getId() + "-h") != null;
	}

	/**
	 * Return the item ids that have been expanded.
	 * <p>
	 * Note - Only used for when the tree is in LAZY mode
	 * </p>
	 *
	 * @return the previously expanded item ids
	 */
	protected Set<String> getPrevExpandedRows() {
		Set<String> prev = getComponentModel().prevExpandedRows;
		if (prev == null) {
			return Collections.emptySet();
		} else {
			return Collections.unmodifiableSet(prev);
		}
	}

	/**
	 * Save the currently open rows.
	 * <p>
	 * Note - Only used for when the tree is in LAZY mode
	 * </p>
	 */
	protected void addPrevExpandedCurrent() {
		Set<String> rows = getExpandedRows();
		if (!rows.isEmpty()) {
			WTreeComponentModel model = getOrCreateComponentModel();
			if (model.prevExpandedRows == null) {
				model.prevExpandedRows = new HashSet<>();
			}
			model.prevExpandedRows.addAll(rows);
		}
	}

	/**
	 * Clear the previously expanded row keys.
	 * <p>
	 * Note - Only used for when the tree is in LAZY mode
	 * </p>
	 */
	protected void clearPrevExpandedRows() {
		getOrCreateComponentModel().prevExpandedRows = null;
	}

	/**
	 * Handles a request containing row selection data.
	 *
	 * @param request the request containing row selection data.
	 * @return the set of selected item ids.
	 */
	private Set<String> getNewSelections(final Request request) {

		// Check for any selections on the request
		String[] paramValue = request.getParameterValues(getId());
		String[] selectedRowIds = removeEmptyStrings(paramValue);
		if (selectedRowIds == null || selectedRowIds.length == 0) {
			return Collections.EMPTY_SET;
		}

		Set<String> newSelectionIds = new HashSet<>();

		boolean singleSelect = getSelectMode() == SelectMode.SINGLE;

		int offset = getItemIdPrefix().length();
		for (String selectedRowId : selectedRowIds) {
			if (selectedRowId.length() <= offset) {
				LOG.warn("Selected row id [" + selectedRowId + "] does not have a valid prefix and will be ignored.");
				continue;
			}
			String itemId = selectedRowId.substring(offset);
			if (isValidTreeItem(itemId)) {
				newSelectionIds.add(itemId);
				if (singleSelect) {
					break;
				}
			} else {
				LOG.warn("Selected row id [" + itemId + "] is not valid and will be ignored.");
			}
		}

		return newSelectionIds;
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
	 * Handle a targeted request to retrieve the tree item image.
	 *
	 * @param request the request being processed
	 */
	private void handleItemImageRequest(final Request request) {

		// Check for tree item id
		String itemId = request.getParameter(ITEM_REQUEST_KEY);
		if (itemId == null) {
			throw new SystemException("No tree item id provided for image request.");
		}

		// Check valid item id
		if (!isValidTreeItem(itemId)) {
			throw new SystemException("Tree item id for an image request [" + itemId + "] is not valid.");
		}

		List<Integer> index = getExpandedItemIdIndexMap().get(itemId);
		TreeItemImage image = getTreeModel().getItemImage(index);
		if (image == null) {
			throw new SystemException("Tree item id [" + itemId + "] does not have an image.");
		}

		ContentEscape escape = new ContentEscape(image.getImage());
		throw escape;
	}

	/**
	 * Handles a request containing an open request.
	 *
	 * @param request the request containing row open request.
	 */
	private void handleOpenItemRequest(final Request request) {

		// Check for tree item id
		String param = request.getParameter(ITEM_REQUEST_KEY);
		if (param == null) {
			throw new SystemException("No tree item id provided for open request.");
		}

		// Remove the prefix to get the item id
		int offset = getItemIdPrefix().length();
		if (param.length() <= offset) {
			throw new SystemException("Tree item id [" + param + "] does not have the correct prefix value.");
		}
		String itemId = param.substring(offset);

		// Check is a valid item id
		if (!isValidTreeItem(itemId)) {
			throw new SystemException("Tree item id [" + itemId + "] is not valid.");
		}

		// Check expandable
		TreeItemIdNode custom = getCustomTree();
		if (custom == null) {
			List<Integer> rowIndex = getExpandedItemIdIndexMap().get(itemId);
			if (!getTreeModel().isExpandable(rowIndex)) {
				throw new SystemException("Tree item id [" + itemId + "] is not expandable.");
			}
		} else {
			TreeItemIdNode node = getCustomIdNodeMap().get(itemId);
			if (!node.hasChildren()) {
				throw new SystemException("Tree item id [" + itemId + "] is not expandable in custom tree.");
			}
		}

		// Save the open id
		setOpenRequestItemId(itemId);

		// Run the open action (if set)
		final Action action = getOpenAction();
		if (action != null) {
			final ActionEvent event = new ActionEvent(this, "openItem");
			Runnable later = new Runnable() {
				@Override
				public void run() {
					action.execute(event);
				}
			};
			invokeLater(later);
		}

	}

	/**
	 * Handle the tree items that have been shuffled by the client.
	 *
	 * @param request the request being processed
	 */
	private void handleShuffleState(final Request request) {

		String json = request.getParameter(SHUFFLE_REQUEST_KEY);
		if (Util.empty(json)) {
			return;
		}

		// New
		TreeItemIdNode newTree;
		try {
			newTree = TreeItemUtil.convertJsonToTree(json);
		} catch (Exception e) {
			LOG.warn("Could not parse JSON for shuffle tree items. " + e.getMessage());
			return;
		}

		// Current
		TreeItemIdNode currentTree = getCustomTree();

		boolean changed = !TreeItemUtil.isTreeSame(newTree, currentTree);

		if (changed) {
			setCustomTree(newTree);
			// Run the shuffle action (if set)
			final Action action = getShuffleAction();
			if (action != null) {
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
	 * Handle the current expanded state.
	 *
	 * @param request the request containing row expansion data.
	 */
	private void handleExpandedState(final Request request) {

		String[] paramValue = request.getParameterValues(getId() + OPEN_REQUEST_KEY);
		if (paramValue == null) {
			paramValue = new String[0];
		}

		String[] expandedRowIds = removeEmptyStrings(paramValue);
		Set<String> newExpansionIds = new HashSet<>();

		if (expandedRowIds != null) {
			int offset = getItemIdPrefix().length();
			for (String expandedRowId : expandedRowIds) {
				if (expandedRowId.length() <= offset) {
					LOG.warn("Expanded row id [" + expandedRowId + "] does not have a valid prefix and will be ignored.");
					continue;
				}
				// Remove prefix to get item id
				String itemId = expandedRowId.substring(offset);
				// Assume the item id is valid
				newExpansionIds.add(itemId);
			}
		}
		setExpandedRows(newExpansionIds);
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
	 *
	 * @param itemId the item id to check
	 * @return true if this item id is valid.
	 */
	private boolean isValidTreeItem(final String itemId) {

		// Check for custom tree
		TreeItemIdNode custom = getCustomTree();
		if (custom != null) {
			return getCustomIdNodeMap().containsKey(itemId);
		}

		// Check is still a valid item
		return getExpandedItemIdIndexMap().containsKey(itemId);
	}

	/**
	 * @param itemId the item id to open
	 */
	private void setOpenRequestItemId(final String itemId) {
		getOrCreateComponentModel().openRequestItemId = itemId;
	}

	/**
	 * @return the item id to open, or null
	 */
	public String getOpenRequestItemId() {
		return getComponentModel().openRequestItemId;
	}

	/**
	 * Map item ids to the row index.
	 *
	 * @param expandedOnly include expanded only
	 *
	 * @return the map of item ids to their row index.
	 */
	private Map<String, List<Integer>> createItemIdIndexMap(final boolean expandedOnly) {

		Map<String, List<Integer>> map = new HashMap<>();
		TreeItemModel treeModel = getTreeModel();
		int rows = treeModel.getRowCount();
		Set<String> expanded = null;
		WTree.ExpandMode mode = getExpandMode();
		if (expandedOnly) {
			expanded = getExpandedRows();
			if (mode == WTree.ExpandMode.LAZY) {
				expanded = new HashSet<>(expanded);
				expanded.addAll(getPrevExpandedRows());
			}
		}

		for (int i = 0; i < rows; i++) {
			List<Integer> index = new ArrayList<>();
			index.add(i);
			processItemIdIndexMapping(map, index, treeModel, expanded);
		}
		return Collections.unmodifiableMap(map);
	}

	/**
	 * Iterate through the table model to add the item ids and their row index.
	 *
	 * @param map the map of item ids
	 * @param rowIndex the current row index
	 * @param treeModel the tree model
	 * @param expandedRows the set of expanded rows, null if include all
	 */
	private void processItemIdIndexMapping(final Map<String, List<Integer>> map, final List<Integer> rowIndex,
			final TreeItemModel treeModel, final Set<String> expandedRows) {

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

		// Add children if expanded ROWS null or contains ID
		boolean addChildren = expandedRows == null || expandedRows.contains(id);
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
			processItemIdIndexMapping(map, nextRow, treeModel, expandedRows);
		}
	}

	/**
	 * Create the map between the custom item id and its node.
	 *
	 * @return the map between an item id ands its custom node
	 */
	private Map<String, TreeItemIdNode> createCustomIdNodeMapping() {
		TreeItemIdNode custom = getCustomTree();
		if (custom == null) {
			return Collections.EMPTY_MAP;
		}

		Map<String, TreeItemIdNode> map = new HashMap<>();
		processCustomIdNodeMapping(map, custom);
		return Collections.unmodifiableMap(map);
	}

	/**
	 * Iterate over the custom tree structure to add entries to the map.
	 *
	 * @param map the map of custom items and their node
	 * @param node the current node being processed
	 */
	private void processCustomIdNodeMapping(final Map<String, TreeItemIdNode> map, final TreeItemIdNode node) {
		String itemId = node.getItemId();
		if (!Util.empty(itemId)) {
			map.put(itemId, node);
		}

		for (TreeItemIdNode childItem : node.getChildren()) {
			processCustomIdNodeMapping(map, childItem);
		}
	}

	/**
	 * Crate a map of the expanded custom item ids and their row index.
	 *
	 * @param expandedOnly true if expanded only
	 * @return the map between a custom item id ands its row index
	 */
	private Map<String, List<Integer>> createExpandedCustomIdIndexMapping(final boolean expandedOnly) {
		TreeItemIdNode custom = getCustomTree();
		if (custom == null) {
			return Collections.EMPTY_MAP;
		}

		Set<String> expanded = null;
		if (expandedOnly) {
			expanded = getExpandedRows();
		}

		Map<String, List<Integer>> map = new HashMap<>();
		processExpandedCustomIdIndexMapping(custom, expanded, map);
		return Collections.unmodifiableMap(map);
	}

	/**
	 * Iterate through nodes to create a mpa of item ids and their row index
	 * <p>
	 * If a node is flagged as having children and has none, then load them from the tree model.
	 * </p>
	 *
	 * @param node the node to check
	 * @param expandedRows the expanded rows or null if include all
	 * @param map the map of item ids to row index
	 */
	private void processExpandedCustomIdIndexMapping(final TreeItemIdNode node, final Set<String> expandedRows,
			final Map<String, List<Integer>> map) {

		String itemId = node.getItemId();
		if (!Util.empty(itemId)) {
			List<Integer> rowIndex = getRowIndexForCustomItemId(itemId);
			map.put(itemId, rowIndex);
		}

		// Node has no children
		if (!node.hasChildren()) {
			return;
		}

		// Check node is expanded
		boolean expanded = expandedRows == null || expandedRows.contains(node.getItemId());
		if (!expanded) {
			return;
		}

		// Check the expanded child nodes
		for (TreeItemIdNode child : node.getChildren()) {
			processExpandedCustomIdIndexMapping(child, expandedRows, map);
		}
	}

	/**
	 * Iterate through nodes to check expanded nodes have their child nodes.
	 * <p>
	 * If a node is flagged as having children and has none, then load them from the tree model.
	 * </p>
	 *
	 * @param node the node to check
	 * @param expandedRows the expanded rows
	 */
	private void processCheckExpandedCustomNodes(final TreeItemIdNode node, final Set<String> expandedRows) {
		// Node has no children
		if (!node.hasChildren()) {
			return;
		}

		// Check node is expanded
		boolean expanded = getExpandMode() == WTree.ExpandMode.CLIENT || expandedRows.contains(node.getItemId());
		if (!expanded) {
			return;
		}

		if (node.getChildren().isEmpty()) {
			// Add children from the model
			loadCustomNodeChildren(node);
		} else {
			// Check the expanded child nodes
			for (TreeItemIdNode child : node.getChildren()) {
				processCheckExpandedCustomNodes(child, expandedRows);
			}
		}
	}

	/**
	 * Load the children of a custom node that was flagged as having children.
	 *
	 * @param node the node to process
	 */
	private void loadCustomNodeChildren(final TreeItemIdNode node) {

		// Check node was flagged as having children or already has children
		if (!node.hasChildren() || !node.getChildren().isEmpty()) {
			return;
		}

		// Get the row index for the node
		String itemId = node.getItemId();
		List<Integer> rowIndex = getRowIndexForCustomItemId(itemId);

		// Get the tree item model
		TreeItemModel model = getTreeModel();

		// Check tree item is expandable and has children
		if (!model.isExpandable(rowIndex) || !model.hasChildren(rowIndex)) {
			node.setHasChildren(false);
			return;
		}

		// Check actual child count (could have no children even though hasChildren returned true)
		int count = model.getChildCount(rowIndex);
		if (count <= 0) {
			node.setHasChildren(false);
			return;
		}

		// Get the map of item ids already in the custom tree
		Map<String, TreeItemIdNode> mapIds = getCustomIdNodeMap();

		// Add children of item to the node tree
		boolean childAdded = false;
		for (int i = 0; i < count; i++) {
			List<Integer> childIdx = new ArrayList<>(rowIndex);
			childIdx.add(i);
			String childItemId = model.getItemId(childIdx);
			// Check the child item is not already in the custom tree
			if (mapIds.containsKey(childItemId)) {
				continue;
			}
			TreeItemIdNode childNode = new TreeItemIdNode(childItemId);
			childNode.setHasChildren(model.hasChildren(childIdx));
			node.addChild(childNode);
			childAdded = true;
			// For client mode we have to drill down all the children
			if (childNode.hasChildren() && getExpandMode() == WTree.ExpandMode.CLIENT) {
				loadCustomNodeChildren(childNode);
			}
		}
		// This could happen if all the children have been used in the custom map
		if (!childAdded) {
			node.setHasChildren(false);
		}

	}

	/**
	 * @return a String representation of this component, for debugging purposes.
	 */
	@Override
	public String toString() {
		TreeItemModel model = getTreeModel();
		return toString(model.getClass().getSimpleName(), -1, -1);
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
	 * @since 1.1.0
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
		private TreeItemModel treeModel = EmptyTreeItemModel.INSTANCE;

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
		 * Open action.
		 */
		private Action openAction;

		/**
		 * Open item id.
		 */
		private String openRequestItemId;

		/**
		 * This is used to allow a user to have a different tree of nodes.
		 */
		private TreeItemIdNode customTree;

		/**
		 * Track preivously LAZY expanded rows.
		 */
		private Set<String> prevExpandedRows;
	}

}
