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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author Jonathan Austin
 * @since 1.0.0
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
	public static final String ITEM_IMAGE_ID_KEY = "wc_treeitemid";

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

		StringBuffer stringValues = new StringBuffer();
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

	public ItemIdNode getCurrentRootNode() {
		ItemIdNode root = getUserRootNode();
		if (root == null) {
			root = getTreeModel().getNodeTree();
		}
		return root == null ? new ItemIdNodeImpl(null) : root;
	}

	public ItemIdNode getUserRootNode() {
		return getComponentModel().userNodeTree;
	}

	public void setUserRootNode(final ItemIdNode root) {
		getOrCreateComponentModel().userNodeTree = root;
	}

	/**
	 * @return true if shuffle items
	 */
	public boolean isShuffle() {
		return getComponentModel().shuffle;
	}

	/**
	 *
	 * @param shuffle true if shuffle items
	 */
	public void setShuffle(final boolean shuffle) {
		getOrCreateComponentModel().shuffle = shuffle;
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
	 * The action used when the tree items are shuffled on the client.
	 *
	 * @return the shuffle action
	 */
	public Action getShuffleAction() {
		return getComponentModel().shuffleAction;
	}

	/**
	 * @return the table model
	 */
	public TreeModel getTreeModel() {
		return getComponentModel().treeModel;
	}

	/**
	 * Sets the table model which provides the row/column data.
	 *
	 * @param treeModel the tree model.
	 */
	public void setTableModel(final TreeModel treeModel) {
		getOrCreateComponentModel().treeModel = treeModel;
		setUserRootNode(null);
		setSelectedRows(null);
		setExpandedRows(null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setMargin(final Margin margin) {
		getOrCreateComponentModel().margin = margin;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Margin getMargin() {
		return getComponentModel().margin;
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
		getOrCreateComponentModel().expandMode = expandMode == null ? ExpandMode.LAZY : expandMode;
	}

	/**
	 * Set the row keys that are expanded.
	 * <p>
	 * A row key uniquely identifies each row and is determined by the {@link TreeModel}. Refer to
	 * {@link TreeModel#getRowKey(List)}.
	 * </p>
	 *
	 * @param rowKeys the keys of expanded rows.
	 */
	public void setExpandedRows(final Set<String> rowKeys) {
		getOrCreateComponentModel().expandedRows = rowKeys;
	}

	/**
	 * Retrieve the row keys that are expanded.
	 * <p>
	 * A row key uniquely identifies each row and is determined by the {@link TreeModel}. Refer to
	 * {@link TreeModel#getRowKey(List)}.
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
	 * Set the row keys that are selected.
	 * <p>
	 * A row key uniquely identifies each row and is determined by the {@link TreeModel}. Refer to
	 * {@link TreeModel#getRowKey(List)}.
	 * </p>
	 *
	 * @param rowKeys the keys of selected rows.
	 */
	public void setSelectedRows(final Set<String> rowKeys) {
		setData(rowKeys);
	}

	/**
	 * Retrieve the row keys that are selected.
	 * <p>
	 * A row key uniquely identifies each row and is determined by the {@link TreeModel}. Refer to
	 * {@link TreeModel#getRowKey(List)}.
	 * </p>
	 *
	 * @return the selected row keys.
	 */
	public Set<String> getSelectedRows() {
		return getValue();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getTargetId() {
		return getId();
	}

	public TreeItem getTreeItem(final String itemId) {
		TreeModel model = getTreeModel();
		if (model == null) {
			return null;
		}
		return model.getTreeItem(itemId);
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
		// Check expanded rows are available
		for (String itemId : getExpandedRows()) {

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
			doHandleItemImageRequest(request);
			return false;
		}
		return true;
	}

	/**
	 * Handle a targeted request to retrieve the tree item image.
	 *
	 * @param request the request being processed
	 */
	protected void doHandleItemImageRequest(final Request request) {

		// Check for tree item id
		String itemId = request.getParameter(ITEM_IMAGE_ID_KEY);
		if (itemId == null) {
			throw new SystemException("No tree item id provided for image request.");
		}

		// Check is valid node
		ItemIdNode node = findItemIdNode(itemId, getCurrentRootNode());
		if (node == null) {
			throw new SystemException("Tree item id [" + itemId + "] is not in the tree.");
		}

		// Check valid item id
		TreeItem item = getTreeItem(itemId);
		if (item == null) {
			throw new SystemException("Tree item id [" + itemId + "] is not in the tree model.");
		}

		ContentEscape escape = new ContentEscape(item.getImage());
		throw escape;
	}

	/**
	 * Retrieves a URL for the tree item image.
	 *
	 * @param item the tree item
	 * @return the URL to access the tree item image.
	 */
	public String getItemImageUrl(final TreeItem item) {

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
		parameters.put(ITEM_IMAGE_ID_KEY, item.getItemId());

		// The targetable path needs to be configured for the portal environment.
		url = env.getWServletPath();

		// Note the last parameter. In javascript we don't want to encode "&".
		return WebUtilities.getPath(url, parameters, true);
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
	 * Handles a request containing row selection data.
	 *
	 * @param request the request containing row selection data.
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
			ItemIdNode root = getCurrentRootNode();
			for (String selectedRowId : selectedRowIds) {
				ItemIdNode item = findItemIdNode(selectedRowId, root);
				if (item == null) {
					continue;
				}
				newSelectionIds.add(selectedRowId);
				if (singleSelect) {
					break;
				}
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
	 * Convert the data to a list (if necessary).
	 *
	 * @param data the data to convert to a list
	 * @return the data converted to a list
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
			ItemIdNode root = getCurrentRootNode();
			for (String expandedRowId : expandedRowIds) {
				ItemIdNode item = findItemIdNode(expandedRowId, root);
				if (item == null) {
					continue;
				}
				newExpansionIds.add(expandedRowId);
			}
		}
		setExpandedRows(newExpansionIds);
	}

	/**
	 * Handle the tree items have been shuffled.
	 *
	 * @param request the request being processed
	 */
	protected void handleShuffleRequest(final Request request) {

		String json = request.getParameter(getId() + ".shuffle");
		if (Util.empty(json)) {
			return;
		}

		// New
		ItemIdNode newTree = null;
		try {
			JsonParser parser = new JsonParser();
			JsonObject jsonRoot = parser.parse(json).getAsJsonObject();
			newTree = handleJsonToTree(jsonRoot);
		} catch (Exception e) {
			LOG.warn("Could not parse JSON for shuffle tree items. " + e.getMessage());
			return;
		}

		// Current
		ItemIdNode currentTree = getCurrentRootNode();

		boolean changed = isNodeSame(newTree, currentTree);

		if (changed) {
			setUserRootNode(newTree);
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

	private ItemIdNode handleJsonToTree(final JsonObject json) {

		ItemIdNodeImpl root = new ItemIdNodeImpl(null);

		JsonArray children = json.getAsJsonArray("root");
		for (int i = 0; i < children.size(); i++) {
			JsonObject child = children.get(i).getAsJsonObject();
			convertJsonToTree(root, child);
		}

		return root;
	}

	private void convertJsonToTree(final ItemIdNodeImpl parentNode, final JsonObject json) {

		String id = json.getAsJsonPrimitive("id").getAsString();

		ItemIdNodeImpl node = new ItemIdNodeImpl(id);
		parentNode.addChild(node);

		JsonArray children = json.getAsJsonArray("items");
		for (int i = 0; i < children.size(); i++) {
			JsonObject child = children.get(i).getAsJsonObject();
			convertJsonToTree(node, child);
		}
	}

	private boolean isNodeSame(final ItemIdNode tree1, final ItemIdNode tree2) {

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
			if (!isNodeSame(tree1.getChildren().get(i), tree2.getChildren().get(i))) {
				return false;
			}
		}
		return true;
	}

	private ItemIdNode copyTreeNode(final ItemIdNode node) {
		ItemIdNodeImpl copy = new ItemIdNodeImpl(node.getItemId());

		for (ItemIdNode childItem : node.getChildren()) {
			ItemIdNode childCopy = copyTreeNode(childItem);
			copy.addChild(childCopy);
		}
		return copy;
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

	private ItemIdNode findItemIdNode(final String itemId, final ItemIdNode node) {
		if (Util.equals(itemId, node.getItemId())) {
			return node;
		}

		for (ItemIdNode childItem : node.getChildren()) {
			ItemIdNode childNode = findItemIdNode(itemId, childItem);
			if (childNode != null) {
				return childNode;
			}
		}

		return null;
	}

	private void checkNodesLoaded(final Set<String> itemIds, final ItemIdNode node) {

		// Check if node is expanded
		if (node.hasChildren()) {
			boolean expand = itemIds.remove(node.getItemId());
			if (expand && node.getChildren().isEmpty()) {
				getTreeModel().loadChildren(node);
			}
			if (itemIds.isEmpty()) {
				return;
			}
		}

		for (ItemIdNode childItem : node.getChildren()) {
			checkNodesLoaded(itemIds, childItem);
			if (itemIds.isEmpty()) {
				return;
			}
		}
	}

	/**
	 * @return a String representation of this component, for debugging purposes.
	 */
	@Override
	public String toString() {
		TreeModel model = getTreeModel();
		return toString(model.getClass().getSimpleName() + ", " + getCurrentRootNode().getChildren().size() + " rows", -1,
				-1);
	}

	/**
	 * Creates a new component model.
	 *
	 * @return a new TableModel.
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
		private ExpandMode expandMode = ExpandMode.LAZY;

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
		private ItemIdNode userNodeTree;
	}

	public enum ShuffleType {
		BRANCH,
		LEAF,
		BOTH
	}

	public static class TreeItem implements Serializable {

		private final String itemId;
		private final String label;
		private final String url;
		private final Image image;
		private final String imageCacheKey;
		private final ShuffleType type;

		public TreeItem(final String itemId, final String label, final String url) {
			this(itemId, label, url, ShuffleType.BOTH);
		}

		public TreeItem(final String itemId, final String label, final Image image) {
			this(itemId, label, image, null, ShuffleType.BOTH);
		}

		public TreeItem(final String itemId, final String label, final Image image, final String imageCacheKey) {
			this(itemId, label, image, imageCacheKey, ShuffleType.BOTH);
		}

		public TreeItem(final String itemId, final String label) {
			this(itemId, label, ShuffleType.BOTH);
		}

		public TreeItem(final String itemId, final String label, final String url, final ShuffleType type) {
			this.itemId = itemId;
			this.label = label;
			this.url = url;
			this.image = null;
			this.imageCacheKey = null;
			this.type = type;
		}

		public TreeItem(final String itemId, final String label, final Image image, final String imageCacheKey, final ShuffleType type) {
			this.itemId = itemId;
			this.label = label;
			this.image = image;
			this.imageCacheKey = imageCacheKey;
			this.url = null;
			this.type = type;
		}

		public TreeItem(final String itemId, final String label, final ShuffleType type) {
			this.itemId = itemId;
			this.label = label;
			this.image = null;
			this.imageCacheKey = null;
			this.url = null;
			this.type = type;
		}

		public String getItemId() {
			return itemId;
		}

		public String getLabel() {
			return label;
		}

		public String getUrl() {
			return url;
		}

		public Image getImage() {
			return image;
		}

		public String getImageCacheKey() {
			return imageCacheKey;
		}

		public ShuffleType getType() {
			return type;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object o) {
			return (o instanceof TreeItem) && Util.equals(itemId, ((TreeItem) o).getItemId());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return itemId.hashCode();
		}
	}

	public static interface ItemIdNode {

		public String getItemId();

		public void addChild(final ItemIdNode child);

		public boolean hasChildren();

		public List<ItemIdNode> getChildren();
	}

	public static class ItemIdNodeImpl implements ItemIdNode {

		private final String itemId;

		private List<ItemIdNode> children;

		public ItemIdNodeImpl(final String itemId) {
			this.itemId = itemId;
		}

		@Override
		public String getItemId() {
			return itemId;
		}

		@Override
		public void addChild(final ItemIdNode node) {
			if (children == null) {
				children = new ArrayList<>();
			}
			children.add(node);
		}

		@Override
		public List<ItemIdNode> getChildren() {
			if (children == null) {
				return Collections.EMPTY_LIST;
			} else {
				return Collections.unmodifiableList(children);
			}
		}

		public boolean hasChildren() {
			return children != null && !children.isEmpty();
		}

	}

	/**
	 * <p>
	 * TableModel provides the data for tables. In a MVC sense, the TableModel is the Model, the {@link WTable} is the
	 * controller and the view is comprised of the WTable layout and column renderers.
	 * </p>
	 * <p>
	 * Note that Data may be stored locally or sourced remotely, depending on the particular TableModel implementation.
	 * <p>
	 * <p>
	 * The row indexes used in the interface are a list of row indexes. Each item in the list is the index of the row
	 * for that level. The size of the list passed in matches the depth of the row.
	 * </p>
	 * <p>
	 * Row and column indices for all methods are zero-based, and TableModels are not expected to perform
	 * bounds-checking.
	 * </p>
	 *
	 * @author Jonathan Austin
	 * @since 1.0.0
	 */
	public interface TreeModel extends Serializable {

		TreeItem getTreeItem(final String itemId);

		/**
		 * Indicates whether the given row is disabled.
		 *
		 * @param row the row index
		 * @return true if the row is disabled, false otherwise.
		 */
		boolean isDisabled(final String itemId);

		/**
		 * Indicates whether the given row is selectable.
		 *
		 * @param row the row index
		 * @return true if the row is selectable, false otherwise.
		 */
		boolean isSelectable(final String itemId);

		ItemIdNode getNodeTree();

		void loadChildren(final ItemIdNode node);
	}

	/**
	 * A skeleton implementation of a simple data model that does not support sorting, selectability, expandability or
	 * editability.
	 *
	 * @author Jonathan Austin
	 * @since 1.0.0
	 */
	public abstract class AbstractTreeModel implements TreeModel {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean isDisabled(final String itemId) {
			return false;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean isSelectable(final String itemId) {
			return false;
		}

		@Override
		public void loadChildren(final ItemIdNode node) {
			// Do nothing
		}

	}

	/**
	 * An empty data model implementation, the default model used by {@link WTable}.
	 *
	 * @author Jonathan Austin
	 * @since 1.0.0
	 */
	public static final class EmptyTreeModel extends AbstractTreeModel {

		/**
		 * The singleton instance.
		 */
		public static final EmptyTreeModel INSTANCE = new EmptyTreeModel();

		private final ItemIdNode root = new ItemIdNodeImpl(null);

		/**
		 * Prevent external instantiation of this class.
		 */
		private EmptyTreeModel() {
		}

		@Override
		public TreeItem getTreeItem(final String itemId) {
			return null;
		}

		@Override
		public ItemIdNode getNodeTree() {
			return root;
		}
	}

}
