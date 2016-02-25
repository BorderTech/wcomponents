package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.util.AbstractTreeNode;
import com.github.bordertech.wcomponents.util.TreeNode;
import com.github.bordertech.wcomponents.util.Util;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class WTree extends AbstractInput
		implements AjaxTarget, SubordinateTarget,
		Marginable {

	/**
	 * The logger instance for this class.
	 */
	private static final Log LOG = LogFactory.getLog(WTree.class);

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

		handleExpansionRequest(request);

		return changed;
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
	 * Override preparePaint to register an AJAX operation if necessary.
	 *
	 * @param request the request being responded to.
	 */
	@Override
	protected void preparePaintComponent(final Request request) {
		super.preparePaintComponent(request);

		AjaxHelper.registerComponentTargetItself(getId(), request);
	}

	public TreeItemNode getTreeItemNode(final String itemId) {
		Iterator<TreeNode> nodes = getCurrentRootNode().breadthFirst();
		while (nodes.hasNext()) {
			TreeItemNode treeNode = (TreeItemNode) nodes.next();
			if (treeNode.getTreeItem().getItemId().equals(itemId)) {
				return treeNode;
			}
		}
		return null;
	}

	public TreeItemNode getCurrentRootNode() {
		TreeItemNode root = getShuffledRootNode();
		if (root == null) {
			root = getTreeModel().getRootNode();
		}
		return root == null ? new TreeItemNode(null) : root;
	}

	public TreeItemNode getShuffledRootNode() {
		return getComponentModel().shuffleRootNode;
	}

	public void setShuffledRootNode(final TreeItemNode root) {
		getOrCreateComponentModel().shuffleRootNode = root;

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
		getOrCreateComponentModel().shuffleRootNode = null;
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
	public void setExpandedRows(final Set<?> rowKeys) {
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
	public Set<?> getExpandedRows() {
		Set<?> keys = getComponentModel().expandedRows;
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
	public void setSelectedRows(final Set<?> rowKeys) {
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
	public Set<?> getSelectedRows() {
		return getValue();
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

		String[] selectedRows = removeEmptyStrings(paramValue);
		Set<String> newSelections = new HashSet<>();

		boolean singleSelect = getSelectMode() == SelectMode.SINGLE;

		if (selectedRows != null && getCurrentRootNode().getChildCount() != 0) {
			// Map the rendered IDs back to the row keys
			for (String selectedRow : selectedRows) {
				TreeItem item = getTreeModel().getTreeItem(selectedRow);
				if (item == null) {
					continue;
				}
				newSelections.add(selectedRow);
				if (singleSelect) {
					break;
				}
			}
		}

		return newSelections;
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

		String[] expandedRows = removeEmptyStrings(paramValue);
		Set<Object> newExpansions = new HashSet<>();

		if (expandedRows != null && getCurrentRootNode().getChildCount() != 0) {
			// Map the rendered IDs back to the row keys
			for (String expandedRow : expandedRows) {
				TreeItem item = getTreeModel().getTreeItem(expandedRow);
				if (item == null) {
					continue;
				}
				newExpansions.add(expandedRow);
			}
		}

		setExpandedRows(newExpansions);
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
	 * @return a String representation of this component, for debugging purposes.
	 */
	@Override
	public String toString() {
		TreeModel model = getTreeModel();
		return toString(model.getClass().getSimpleName() + ", " + getCurrentRootNode().getChildCount() + " rows", -1,
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
		 * The data model for the table.
		 */
		private TreeModel treeModel = EmptyTreeModel.INSTANCE;

		// Selection
		/**
		 * Indicates how row selection should function.
		 */
		private SelectMode selectMode = SelectMode.SINGLE;

		// Row expansion
		/**
		 * Indicates how row expansion should function.
		 */
		private ExpandMode expandMode = ExpandMode.LAZY;

		/**
		 * Holds the keys of currently expanded rows.
		 */
		private Set<?> expandedRows;

		private boolean shuffle;

		/**
		 * This is used to map rendered table row indices to table model row indices, if the table model supports this
		 * mode of sorting.
		 */
		private TreeItemNode shuffleRootNode;
	}

	public static class TreeItem implements Serializable {

		private final String itemId;
		private String label;
		private String url;
		private ContentAccess content;

		public TreeItem(final String itemId) {
			this.itemId = itemId;
		}

		public String getItemId() {
			return itemId;
		}

		public String getLabel() {
			return label;
		}

		public void setLabel(String label) {
			this.label = label;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public ContentAccess getContent() {
			return content;
		}

		public void setContent(final ContentAccess content) {
			this.content = content;
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

	public static class TreeItemNode extends AbstractTreeNode {

		private final TreeItem treeItem;

		private boolean childrenToLoad;

		public TreeItemNode(final TreeItem treeItem) {
			this.treeItem = treeItem;
		}

		public TreeItem getTreeItem() {
			return treeItem;
		}

		public void setChildrenToLoad(final boolean childrenToLoad) {
			this.childrenToLoad = childrenToLoad;
		}

		public boolean hasChildrenToLoad() {
			return childrenToLoad;
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

		/**
		 * Indicates whether the given row is expandable.
		 *
		 * @param row the row index
		 * @return true if the row is expandable, false otherwise.
		 */
		boolean isExpandable(final String itemId);

		TreeItemNode getRootNode();

		void loadChildren(final TreeItemNode node);
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
		 * This model does not support the concept of row disabling by default. Subclasses will need to override this
		 * method to support row disabling.
		 *
		 * @param row ignored.
		 * @return false.
		 */
		@Override
		public boolean isDisabled(final String itemId) {
			return false;
		}

		/**
		 * This model does not support the concept of row selectability by default. Subclasses will need to override
		 * this method for selection of specific rows.
		 *
		 * @param row ignored.
		 * @return false
		 */
		@Override
		public boolean isSelectable(final String itemId) {
			return false;
		}

		/**
		 * This model does not support the concept of rows being expandable by default. Subclasses will need to override
		 * this method for expansion of specific rows.
		 *
		 * @param row ignored
		 * @return false
		 */
		@Override
		public boolean isExpandable(final String itemId) {
			return false;
		}

	}

	/**
	 * An empty data model implementation, the default model used by {@link WTable}.
	 *
	 * @author Jonathan Austin
	 * @since 1.0.0
	 */
	public static final class EmptyTreeModel extends AbstractTreeModel {

		private final static TreeItemNode ROOT = new TreeItemNode(null);

		/**
		 * The singleton instance.
		 */
		public static final EmptyTreeModel INSTANCE = new EmptyTreeModel();

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
		public TreeItemNode getRootNode() {
			return ROOT;
		}

		@Override
		public void loadChildren(final TreeItemNode node) {
			// Do nothing
		}

	}

}
